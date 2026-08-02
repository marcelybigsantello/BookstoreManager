package com.masantello.bookstoremanager.validation.user;

import com.masantello.bookstoremanager.dtos.UserDto;
import com.masantello.bookstoremanager.exceptions.DataIntegrityViolationException;
import com.masantello.bookstoremanager.models.User;
import com.masantello.bookstoremanager.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = UserUniqueFieldsValidator.class)
public class UserUniqueFieldsValidatorTest {

    @Autowired
    private UserUniqueFieldsValidator userUniqueFieldsValidator;

    @MockitoBean
    private UserRepository userRepository;

    private UserDto userDto;

    @BeforeEach
    void setup() {
        userDto = new UserDto();
        userDto.setEmail("teste@gmail.com");
        userDto.setUsername("testeUnitario");
        userDto.setPassword("123456");
    }

    @Test
    @DisplayName("Validate - email e senha devem ser únicos e deve retornar o próprio DTO de usuário")
    void validate_emailAndPasswordUniques_shouldReturnUserDto() {
        //Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByPassword(anyString())).thenReturn(Optional.empty());

        //Act
        var result = userUniqueFieldsValidator.validate(userDto);

        //Assert
        assertNotNull(result);
        assertEquals(result.getEmail(), userDto.getEmail());
        assertEquals(result.getUsername(), userDto.getUsername());
        assertEquals(result.getPassword(), userDto.getPassword());
        verify(userRepository).findByEmail(userDto.getEmail());
        verify(userRepository).findByPassword(userDto.getPassword());
    }

    @Test
    @DisplayName("validate - email não deve ser único e deve lançar DataIntegrityViolationException")
    void validate_emailNotUnique_shouldThrowDataIntegrityViolationException() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(new User()));
        when(userRepository.findByPassword(anyString())).thenReturn(Optional.empty());

        //Act && Arrange
        assertThrows(DataIntegrityViolationException.class, () -> userUniqueFieldsValidator.validate(userDto),
                "Deve lançar exceção se já existe um e-mail igual cadastrado");

    }

    @Test
    @DisplayName("validate - senha não deve ser única e deve lançar DataIntegrityViolationException")
    void validate_passwordNotUnique_shouldThrowDataIntegrityViolationException() {
        //Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByPassword(anyString())).thenReturn(Optional.of(new User()));

        //Act && Arrange
        assertThrows(DataIntegrityViolationException.class, () ->
                userUniqueFieldsValidator.validate(userDto),
                "Deve lançar exceção se já existe essa senha cadastrada");
    }
}
