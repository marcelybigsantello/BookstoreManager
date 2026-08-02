package com.masantello.bookstoremanager.validation.user;

import com.masantello.bookstoremanager.dtos.UserDto;
import com.masantello.bookstoremanager.models.User;
import com.masantello.bookstoremanager.repositories.UserRepository;
import jakarta.persistence.EntityExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = UserAlreadyExistsValidator.class)
public class UserAlreadyExistsValidatorTest {

    @Autowired
    private UserAlreadyExistsValidator validator;

    @MockitoBean
    private UserRepository userRepository;

    private UserDto userDto;

    @BeforeEach
    void setup() {
        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("Maria Clara");
        userDto.setEmail("mariaclara.oliveira@gmail.com");
        userDto.setBirthDate(LocalDate.of(2007, 6, 20));
        userDto.setUsername("maria.clara.oliveira");
        userDto.setPassword("12345");
        userDto.setRole("Common");
    }

    @Test
    @DisplayName("validate - usuário não existe, pode ser cadastrado e deve retornar o próprio UserDto")
    void validate_userDoesNotExist_shouldReturnTheUserDto() {
        // Arrange
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act
        var result = validator.validate(userDto);

        // Assert
        assertNotNull(result);
        assertEquals(result.getId(), userDto.getId());
        assertEquals(result.getName(), userDto.getName());
        assertThat(userDto.getEmail()).isEqualTo(result.getEmail());
        assertThat(userDto.getBirthDate()).isEqualTo(result.getBirthDate());
        assertThat(userDto.getUsername()).isEqualTo(result.getUsername());
        assertThat(userDto.getPassword()).isEqualTo(result.getPassword());
        assertThat(userDto.getRole()).isEqualTo(result.getRole());
        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    @DisplayName("validate - usuário existe, não pode ser cadastrado e deve retornar EntityExistsException")
    void validate_userExists_shouldReturnEntityExistsException() {
        // Arrange
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));

        // Act && Arrange
        assertThrows(EntityExistsException.class, () -> validator.validate(userDto),
                "Deve lançar exceção se o usuário já está cadastrado no sistema");
    }

}
