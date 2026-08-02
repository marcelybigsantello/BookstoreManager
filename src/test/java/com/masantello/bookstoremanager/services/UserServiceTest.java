package com.masantello.bookstoremanager.services;

import com.masantello.bookstoremanager.dtos.UserDto;
import com.masantello.bookstoremanager.mappers.UserMapper;
import com.masantello.bookstoremanager.models.User;
import com.masantello.bookstoremanager.models.enums.Gender;
import com.masantello.bookstoremanager.models.enums.Role;
import com.masantello.bookstoremanager.repositories.UserRepository;
import com.masantello.bookstoremanager.validation.AbstractValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = UserService.class)
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private UserMapper userMapper;

    @MockitoBean
    @Qualifier("createUserValidator")
    AbstractValidator<UserDto> createUserValidator;

    @MockitoBean
    @Qualifier("updateUserValidator")
    AbstractValidator<UserDto> updateUserValidator;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    private UserDto userDto;
    private User user;
    private User userDto2;

    private final String plainPassword = "plainPassword";
    private final String encodedPassword = "encodedPassword";

    @BeforeEach
    void setup() {
        userDto = buildUserDto();
        user = buildUser();
        userDto2 = buildUser2();
    }

    @Test
    @DisplayName("create - if valid user, should validate encode and persist user")
    void create_whenValidUser_shouldValidateEncoderAndPersistUser() {
        // Arrange
        var expectedResponse = buildUserDto();
        expectedResponse.setPassword(encodedPassword);

        when(createUserValidator.validate(userDto)).thenReturn(userDto);
        when(userMapper.convertToModel(userDto)).thenReturn(user);
        when(passwordEncoder.encode(plainPassword)).thenReturn(encodedPassword);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.convertToDto(any(User.class))).thenReturn(expectedResponse);

        // Act
        var result = userService.create(userDto);

        // Assert
        assertNotNull(result);
        assertEquals(encodedPassword, result.getPassword());
        verify(createUserValidator).validate(userDto);
        verify(userMapper).convertToDto(any(User.class));
        verify(passwordEncoder).encode(plainPassword);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertEquals(encodedPassword, userCaptor.getValue().getPassword());

        verify(userMapper).convertToDto(any(User.class));
    }

    @Test
    @DisplayName("findAll - should return all users from database")
    void findAll_shouldReturnAllUsersFromDatabase() {
        // Arrange
        when(userRepository.findAll()).thenReturn(List.of(user, userDto2));
        when(userMapper.convertToDto(user)).thenReturn(userDto);
        when(userMapper.convertToDto(userDto2)).thenReturn(new UserDto(2L, "Tom Cruise", "tomcruiseofficial@gmail.com",
                null, null, "tom.cruise", "654321", "Admin"));

        // Act
        var result = userService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(userDto.getId(), result.get(0).getId());
        assertEquals(userDto.getName(), result.get(0).getName());
        assertEquals(userDto.getUsername(), result.get(0).getUsername());
        assertEquals(userDto.getPassword(), result.get(0).getPassword());

        verify(userRepository).findAll();
        verify(userMapper).convertToDto(user);
    }

    @Test
    @DisplayName("findByUsername - if part of username exists, should return all username which contains it")
    void findByUsername_whenUsernameExists_shouldReturnAllUsernamesWhichContainsIt() {
        // Arrange
        var partOfUserName = "user";
        when(userRepository.findByUsernameContainingIgnoreCase(partOfUserName)).thenReturn(List.of(user));
        when(userMapper.convertToDto(user)).thenReturn(userDto);

        // Act
        var result = userService.findByUsername(partOfUserName);

        // Assert
        assertNotNull(result);
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getUsername()).isEqualTo(userDto.getUsername());
        assertThat(result.get(0).getEmail()).isEqualTo(userDto.getEmail());
        assertThat(result.get(0).getPassword()).isEqualTo(userDto.getPassword());

        verify(userRepository, times(1)).findByUsernameContainingIgnoreCase(partOfUserName);
        verify(userMapper, times(1)).convertToDto(any(User.class));
    }

    @Test
    @DisplayName("findByUsername - if part of username does not exist, should log it and return empty list")
    void findByUsername_whenUsernameDoesNotExist_shouldReturnEmptyList() {
        // Arrange
        var partOfUserName = "user";
        when(userRepository.findByUsernameContainingIgnoreCase(partOfUserName)).thenReturn(List.of());

        // Act
        var result = userService.findByUsername(partOfUserName);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(userRepository, times(1)).findByUsernameContainingIgnoreCase(partOfUserName);
        verify(userMapper, never()).convertToDto(any(User.class));
    }

    @Test
    @DisplayName("update - if valid user, should validate encode and save user")
    void update_whenValidUser_shouldValidateEncodeAndSaveUser() {
        // Arrange
        when(updateUserValidator.validate(userDto)).thenReturn(userDto);
        when(userMapper.convertToModel(userDto)).thenReturn(user);
        when(passwordEncoder.encode(plainPassword)).thenReturn(encodedPassword);
        when(userRepository.save(user)).thenReturn(any(User.class));

        // Act
        userService.update(userDto);

        // Assert
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(userCaptor.capture());

        var updatedUser = userCaptor.getValue();
        assertEquals(encodedPassword, updatedUser.getPassword());
        assertEquals(updatedUser.getUsername(), userDto.getUsername());
        assertEquals(updatedUser.getEmail(), userDto.getEmail());
        assertEquals(updatedUser.getName(), userDto.getName());
        assertNotEquals(updatedUser.getPassword(), userDto.getPassword());
        assertEquals(Role.convertToDescription(updatedUser.getRole()), userDto.getRole());

        verify(updateUserValidator).validate(userDto);
        verify(userMapper).convertToModel(userDto);
        verify(passwordEncoder).encode(plainPassword);
    }

    @Test
    @DisplayName("delete - if user exists, should remove user from database successfully")
    void delete_whenUserExists_shouldRemoveUserFromDatabase() {
        // Arrange
        var userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        userService.delete(userId);

        // Assert
        verify(userRepository).findById(userId);
        verify(userRepository).delete(user);
    }

    @Test
    @DisplayName("delete - if user does not exist, should throw EntityNotFoundException")
    void delete_whenUserDoesNotExist_shouldThrowEntityNotFoundException() {
        // Arrange
        var userIdNotExistent = 99L;
        when(userRepository.findById(userIdNotExistent)).thenReturn(Optional.empty());

        // Act && Assert
        var exception = assertThrows(EntityNotFoundException.class, () -> userService.delete(userIdNotExistent));

        assertTrue(exception.getMessage().contains("99"));
        verify(userRepository).findById(userIdNotExistent);
        verify(userRepository, never()).delete(any(User.class));
    }

    @Test
    @DisplayName("findByLoggedUsername - find successfully that specific user and return it")
    void findByLoggedUsername_ifUserExists_returnSpecificUser() {
        // Arrange
        var username = "tom.cruise";
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(userDto2));

        // Act
        var result = userService.findByLoggedUsername(username);

        // Assert
        assertNotNull(result);
        assertThat(result.getUsername()).isEqualTo(username);
        assertThat(result.getId()).isEqualTo(userDto2.getId());
        assertThat(result.getName()).isEqualTo(userDto2.getName());
        assertThat(result.getUsername()).isEqualTo(userDto2.getUsername());
        assertThat(result.getPassword()).isEqualTo(userDto2.getPassword());

        verify(userRepository).findByUsername(username);
    }

    @Test
    @DisplayName("findByLoggedUsername_ifUserDoesNotExist_shouldThrowEntityNotFoundException")
    void findByLoggedUsername_ifUserDoesNotExist_shouldThrowEntityNotFoundException() {
        // Arrange
        var unknownUsername = "unknown username";
        when(userRepository.findByUsername(unknownUsername)).thenReturn(Optional.empty());

        // Act && Assert
        var exception = assertThrows(EntityNotFoundException.class,
                () -> userService.findByLoggedUsername(unknownUsername));

        assertTrue(exception.getMessage().equalsIgnoreCase("User with username='unknown username' not found in database."));
        verify(userRepository, times(1)).findByUsername(unknownUsername);
    }

    private UserDto buildUserDto() {
        var userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("Nickolas Sparks");
        userDto.setEmail("nicholas.sparks@gmail.com");
        userDto.setBirthDate(LocalDate.of(1970, 12, 31));
        userDto.setGender("Male");
        userDto.setUsername("nickolas.sparks");
        userDto.setPassword("plainPassword");
        userDto.setRole("Admin");
        return userDto;
    }

    private User buildUser() {
        var user = new User();
        user.setId(1L);
        user.setName("Nickolas Sparks");
        user.setEmail("nicholas.sparks@gmail.com");
        user.setBirthDate(LocalDate.of(1970, 12, 31));
        user.setGender(Gender.MALE);
        user.setUsername("nickolas.sparks");
        user.setPassword("plainPassword");
        user.setRole(Role.ADMIN);

        return user;
    }

    private User buildUser2() {
        var user2 = new User();
        user2.setId(2L);
        user2.setName("Tom Cruise");
        user2.setEmail("tomcruiseofficial@gmail.com");
        user2.setUsername("tom.cruise");
        user2.setPassword("654321");
        user2.setRole(Role.ADMIN);

        return user2;
    }

    private UserDto buildUserDto2() {
        var userDto2 = new UserDto();
        userDto2.setId(2L);
        userDto2.setName("Tom Cruise");
        userDto2.setEmail("tomcruiseofficial@gmail.com");
        userDto2.setUsername("tom.cruise");
        userDto2.setPassword("654321");
        userDto2.setRole(Role.convertToDescription(Role.ADMIN));

        return userDto2;
    }

}
