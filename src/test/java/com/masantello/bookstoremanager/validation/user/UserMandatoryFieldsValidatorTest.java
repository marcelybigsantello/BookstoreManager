package com.masantello.bookstoremanager.validation.user;

import com.masantello.bookstoremanager.dtos.UserDto;
import com.masantello.bookstoremanager.exceptions.MissingMandatoryFieldsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = UserMandatoryFieldsValidator.class)
public class UserMandatoryFieldsValidatorTest {

    @Autowired
    private UserMandatoryFieldsValidator validator;

    private UserDto userDto;

    @BeforeEach
    void setup() {
        userDto = new UserDto();
        userDto.setId(2L);
        userDto.setName("Gabriel João");
        userDto.setEmail("gabriel.joao@gmail.com");
        userDto.setUsername("gabriel.joao");
        userDto.setPassword("45678");
    }

    @Test
    @DisplayName("validate - deve possuir todos os campos obrigatórios e retornar o próprio UserDto")
    void validate_shouldHaveAllMandatoryFields() {

        // Act
        var result = validator.validate(userDto);

        // Assert
        assertNotNull(result);
        assertEquals(result.getId(), userDto.getId());
        assertEquals(result.getName(), userDto.getName());
        assertEquals(result.getEmail(), userDto.getEmail());
        assertEquals(result.getUsername(), userDto.getUsername());
        assertEquals(result.getPassword(), userDto.getPassword());
    }

    @Test
    @DisplayName("validate - deve não possuir nome e retornar MissingMandatoryFieldsException")
    void validate_doesNotHaveName_shouldReturnMissingMandatoryFieldsException() {
        // Arrange
        userDto.setName(null);

        // Act && Assert
        assertThrows(MissingMandatoryFieldsException.class, () -> validator.validate(userDto),
                "Deve lançar MissingMandatoryFieldsException se o nome não foi informado");
    }

    @Test
    @DisplayName("validate - se não possuir email, deve retornar MissingMandatoryFieldsException")
    void validate_doesNotHaveEmail_shouldReturnMissingMandatoryFieldsException() {
        // Arrange
        userDto.setEmail(null);

        // Act && Assert
        assertThrows(MissingMandatoryFieldsException.class, () -> validator.validate(userDto),
                "Deve lançar MissingMandatoryFieldsException se o email não foi informado");
    }

    @Test
    @DisplayName("validate - se não possuir username, deve retornar MissingMandatoryFieldsException")
    void validate_doesNotHaveUsername_shouldReturnMissingMandatoryFieldsException() {
        // Arrange
        userDto.setUsername(null);

        // Act && Assert
        assertThrows(MissingMandatoryFieldsException.class, () -> validator.validate(userDto),
                "Deve lançar MissingMandatoryFieldsException se o username não foi informado");
    }

    @Test
    @DisplayName("validate - deve não possuir senha e retornar MissingMandatoryFieldsException")
    void validate_doesNotHavePassword_shouldReturnMissingMandatoryFieldsException() {
        // Arrange
        userDto.setPassword(null);

        // Act && Assert
        assertThrows(MissingMandatoryFieldsException.class, () -> validator.validate(userDto),
                "Deve lançar MissingMandatoryFieldsException se a senha não foi informada");
    }
}
