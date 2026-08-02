package com.masantello.bookstoremanager.controllers;

import com.masantello.bookstoremanager.dtos.JwtRequest;
import com.masantello.bookstoremanager.dtos.JwtResponse;
import com.masantello.bookstoremanager.dtos.UserDto;
import com.masantello.bookstoremanager.services.AuthenticationService;
import com.masantello.bookstoremanager.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = UserControllerImpl.class)
public class UserControllerImplTest {

    @Autowired
    private UserControllerImpl userController;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private AuthenticationService authenticationService;

    private UserDto userDto;
    private JwtRequest jwtRequest;

    @BeforeEach
    void setUp() {
        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("João Silva");
        userDto.setEmail("joao.silva@email.com");
        userDto.setBirthDate(LocalDate.of(1990, 5, 15));
        userDto.setGender("Masculino");
        userDto.setUsername("joaosilva");
        userDto.setPassword("senha123");
        userDto.setRole("ADMIN");

        jwtRequest = new JwtRequest();
        jwtRequest.setUsername("joaosilva");
        jwtRequest.setPassword("senha123");
    }

    // ======================== CREATE TESTS ========================
    @Test
    @DisplayName("Should create a new user successfully and return 201 CREATED")
    void testCreateUserSuccess() {
        // Arrange
        UserDto newUserDto = new UserDto();
        newUserDto.setName("João Silva");
        newUserDto.setEmail("joao.silva@email.com");
        newUserDto.setBirthDate(LocalDate.of(1990, 5, 15));
        newUserDto.setGender("Masculino");
        newUserDto.setUsername("joaosilva");
        newUserDto.setPassword("senha123");
        newUserDto.setRole("USER");

        when(userService.create(any(UserDto.class))).thenReturn(userDto);

        // Act
        ResponseEntity<UserDto> response = userController.create(newUserDto);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getHeaders().getLocation());
        verify(userService, times(1)).create(any(UserDto.class));
    }

    @Test
    @DisplayName("Should invoke UserService.create exactly once during user creation")
    void testCreateUserCallsServiceOnce() {
        // Arrange
        when(userService.create(any(UserDto.class))).thenReturn(userDto);

        // Act
        userController.create(userDto);

        // Assert
        verify(userService, times(1)).create(any(UserDto.class));
    }

    @Test
    @DisplayName("Should build correct URI location with created user ID")
    void testCreateUserReturnsCorrectLocationUri() {
        // Arrange
        UserDto userToCreate = new UserDto();
        userToCreate.setUsername("newuser");
        userToCreate.setEmail("newuser@email.com");

        UserDto createdUser = new UserDto();
        createdUser.setId(5L);
        createdUser.setUsername("newuser");
        createdUser.setEmail("newuser@email.com");

        when(userService.create(any(UserDto.class))).thenReturn(createdUser);

        // Act
        ResponseEntity<UserDto> response = userController.create(userToCreate);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getHeaders().getLocation());
        assertTrue(response.getHeaders().getLocation().toString().contains("5"));
    }

    // ======================== FIND ALL TESTS ========================
    @Test
    @DisplayName("Should return all users with 200 OK status code")
    void testFindAllUsersSuccess() {
        // Arrange
        UserDto user2 = new UserDto();
        user2.setId(2L);
        user2.setName("Maria Santos");
        user2.setUsername("mariasantos");
        user2.setEmail("maria@email.com");

        List<UserDto> userList = Arrays.asList(userDto, user2);
        when(userService.findAll()).thenReturn(userList);

        // Act
        ResponseEntity<List<UserDto>> response = userController.findAll();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        verify(userService, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no users exist")
    void testFindAllUsersEmpty() {
        // Arrange
        when(userService.findAll()).thenReturn(Collections.emptyList());

        // Act
        ResponseEntity<List<UserDto>> response = userController.findAll();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
        verify(userService, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return correct user data in findAll response")
    void testFindAllUsersReturnsCorrectData() {
        // Arrange
        List<UserDto> userList = Collections.singletonList(userDto);
        when(userService.findAll()).thenReturn(userList);

        // Act
        ResponseEntity<List<UserDto>> response = userController.findAll();

        // Assert
        assertNotNull(response.getBody());
        UserDto returnedUser = response.getBody().get(0);
        assertEquals(1L, returnedUser.getId());
        assertEquals("João Silva", returnedUser.getName());
        assertEquals("joao.silva@email.com", returnedUser.getEmail());
        assertEquals("joaosilva", returnedUser.getUsername());
    }

    // ======================== FIND BY USERNAME TESTS ========================
    @Test
    @DisplayName("Should find user by username and return 200 OK")
    void testFindByUsernameSuccess() {
        // Arrange
        String username = "joaosilva";
        List<UserDto> userList = Collections.singletonList(userDto);
        when(userService.findByUsername(username)).thenReturn(userList);

        // Act
        ResponseEntity<List<UserDto>> response = userController.findByUserName(username);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(username, response.getBody().get(0).getUsername());
        verify(userService, times(1)).findByUsername(username);
    }

    @Test
    @DisplayName("Should return empty list when user not found by username")
    void testFindByUsernameNotFound() {
        // Arrange
        String username = "nonexistentuser";
        when(userService.findByUsername(username)).thenReturn(Collections.emptyList());

        // Act
        ResponseEntity<List<UserDto>> response = userController.findByUserName(username);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
        verify(userService, times(1)).findByUsername(username);
    }

    @Test
    @DisplayName("Should return multiple users with same username pattern")
    void testFindByUsernameReturnsMultipleUsers() {
        // Arrange
        String usernamePattern = "joao";
        UserDto user2 = new UserDto();
        user2.setId(2L);
        user2.setUsername("joao.silva.2");
        user2.setEmail("joao2@email.com");

        List<UserDto> userList = Arrays.asList(userDto, user2);
        when(userService.findByUsername(usernamePattern)).thenReturn(userList);

        // Act
        ResponseEntity<List<UserDto>> response = userController.findByUserName(usernamePattern);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        verify(userService, times(1)).findByUsername(usernamePattern);
    }

    // ======================== UPDATE TESTS ========================
    @Test
    @DisplayName("Should update user and return 204 NO_CONTENT")
    void testUpdateUserSuccess() {
        // Arrange
        Long userId = 1L;
        UserDto updatedUserDto = new UserDto();
        updatedUserDto.setName("João Silva Atualizado");
        updatedUserDto.setEmail("joao.silva.updated@email.com");
        updatedUserDto.setUsername("joaosilva");

        doNothing().when(userService).update(any(UserDto.class));

        // Act
        ResponseEntity<Void> response = userController.update(userId, updatedUserDto);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(userService, times(1)).update(any(UserDto.class));
    }

    @Test
    @DisplayName("Should set user ID from path variable before updating")
    void testUpdateUserSetsIdFromPathVariable() {
        // Arrange
        Long userId = 10L;
        UserDto updateDto = new UserDto();
        updateDto.setName("Updated User");
        updateDto.setUsername("updateduser");

        doNothing().when(userService).update(any(UserDto.class));

        // Act
        userController.update(userId, updateDto);

        // Assert
        verify(userService, times(1)).update(argThat(dto -> dto.getId().equals(userId)));
    }

    @Test
    @DisplayName("Should update user with valid data")
    void testUpdateUserWithValidData() {
        // Arrange
        Long userId = 1L;
        UserDto updateDto = new UserDto();
        updateDto.setName("New Name");
        updateDto.setEmail("newemail@email.com");
        updateDto.setGender("Feminino");
        updateDto.setBirthDate(LocalDate.of(1995, 3, 20));

        doNothing().when(userService).update(any(UserDto.class));

        // Act
        ResponseEntity<Void> response = userController.update(userId, updateDto);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(userService, times(1)).update(any(UserDto.class));
    }

    // ======================== DELETE TESTS ========================
    @Test
    @DisplayName("Should delete user and return 204 NO_CONTENT")
    void testDeleteUserSuccess() {
        // Arrange
        Long userId = 1L;
        doNothing().when(userService).delete(userId);

        // Act
        ResponseEntity<Void> response = userController.delete(userId);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(userService, times(1)).delete(userId);
    }

    @Test
    @DisplayName("Should delete different users by ID")
    void testDeleteMultipleUsers() {
        // Arrange
        doNothing().when(userService).delete(anyLong());

        // Act
        userController.delete(1L);
        userController.delete(2L);
        userController.delete(5L);

        // Assert
        verify(userService).delete(1L);
        verify(userService).delete(2L);
        verify(userService).delete(5L);
        verify(userService, times(3)).delete(anyLong());
    }

    @Test
    @DisplayName("Should pass correct user ID to service during deletion")
    void testDeleteUserPassesCorrectId() {
        // Arrange
        Long userId = 15L;
        doNothing().when(userService).delete(userId);

        // Act
        userController.delete(userId);

        // Assert
        verify(userService, times(1)).delete(15L);
    }

    // ======================== AUTHENTICATE TESTS ========================
    @Test
    @DisplayName("Should authenticate user and return JWT token")
    void testCreateAuthenticationTokenSuccess() {
        // Arrange
        JwtResponse jwtResponse = JwtResponse.builder()
                .jwtToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJqb2FvcyJ9.signature")
                .build();

        when(authenticationService.createAuthenticationToken(any(JwtRequest.class)))
                .thenReturn(jwtResponse);

        // Act
        JwtResponse response = userController.createAuthenticationToken(jwtRequest);

        // Assert
        assertNotNull(response);
        assertNotNull(response.jwtToken());
        assertTrue(response.jwtToken().contains("eyJ"));
        verify(authenticationService, times(1)).createAuthenticationToken(any(JwtRequest.class));
    }

    @Test
    @DisplayName("Should pass correct credentials to authentication service")
    void testCreateAuthenticationTokenPassesCorrectCredentials() {
        // Arrange
        JwtRequest request = new JwtRequest();
        request.setUsername("testuser");
        request.setPassword("testpass123");

        JwtResponse jwtResponse = JwtResponse.builder()
                .jwtToken("token123")
                .build();

        when(authenticationService.createAuthenticationToken(any(JwtRequest.class)))
                .thenReturn(jwtResponse);

        // Act
        userController.createAuthenticationToken(request);

        // Assert
        verify(authenticationService, times(1))
                .createAuthenticationToken(argThat(req ->
                        req.getUsername().equals("testuser") &&
                                req.getPassword().equals("testpass123")
                ));
    }

    @Test
    @DisplayName("Should invoke AuthenticationService exactly once during authentication")
    void testCreateAuthenticationTokenCallsServiceOnce() {
        // Arrange
        JwtResponse jwtResponse = JwtResponse.builder()
                .jwtToken("token")
                .build();

        when(authenticationService.createAuthenticationToken(any(JwtRequest.class)))
                .thenReturn(jwtResponse);

        // Act
        userController.createAuthenticationToken(jwtRequest);

        // Assert
        verify(authenticationService, times(1)).createAuthenticationToken(any(JwtRequest.class));
    }

    @Test
    @DisplayName("Should return different JWT tokens for different requests")
    void testCreateAuthenticationTokenReturnsDifferentTokens() {
        // Arrange
        JwtResponse response1 = JwtResponse.builder().jwtToken("token1").build();
        JwtResponse response2 = JwtResponse.builder().jwtToken("token2").build();

        JwtRequest request1 = new JwtRequest("user1", "pass1");
        JwtRequest request2 = new JwtRequest("user2", "pass2");

        when(authenticationService.createAuthenticationToken(request1)).thenReturn(response1);
        when(authenticationService.createAuthenticationToken(request2)).thenReturn(response2);

        // Act
        JwtResponse result1 = userController.createAuthenticationToken(request1);
        JwtResponse result2 = userController.createAuthenticationToken(request2);

        // Assert
        assertNotEquals(result1.jwtToken(), result2.jwtToken());
        assertEquals("token1", result1.jwtToken());
        assertEquals("token2", result2.jwtToken());
    }


}
