package com.masantello.bookstoremanager.validation.author;

import com.masantello.bookstoremanager.dtos.AuthorDto;
import com.masantello.bookstoremanager.exceptions.MissingMandatoryFieldsException;
import com.masantello.bookstoremanager.validation.AbstractValidator;
import com.masantello.bookstoremanager.validation.author.AuthorMandatoryFieldsValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = AuthorMandatoryFieldsValidator.class)
public class AuthorMandatoryFieldsValidatorTest {

    @Autowired
    private AuthorMandatoryFieldsValidator validator;

    private AuthorDto authorDto;

    @BeforeEach
    void setup() {
        authorDto = new AuthorDto();
        authorDto.setId(1L);
        authorDto.setName("Clarice Lispector");
        authorDto.setEmail("clarice@example.com");
        authorDto.setAge(57);
        authorDto.setBirthDate(LocalDate.of(1920, 12, 10));
        authorDto.setLiteraryGenre("Romance");
    }

    @Test
    @DisplayName("Quando name e literaryGenre são válidos e não há next, deve retornar o mesmo AuthorDto")
    void testValidate_whenNameAndLiteraryGenreAreValid_andNoNext_shouldReturnSameDto() {
        // Act
        AuthorDto result = validator.validate(authorDto);

        // Assert
        assertSame(authorDto, result);
        assertEquals("Clarice Lispector", result.getName());
        assertEquals("Romance", result.getLiteraryGenre());
    }

    @Test
    @DisplayName("Quando name e literaryGenre são válidos e existe next, deve chamar next.validate")
    void testValidate_whenNameAndLiteraryGenreAreValid_andHasNext_shouldCallNext() {
        // Arrange
        @SuppressWarnings("unchecked")
        AbstractValidator<AuthorDto> next = mock(AbstractValidator.class);
        AuthorDto nextResult = new AuthorDto();
        nextResult.setName("Modificado");
        when(next.validate(authorDto)).thenReturn(nextResult);

        validator.next = next;

        // Act
        AuthorDto result = validator.validate(authorDto);

        // Assert
        assertNotNull(result);
        assertEquals("Modificado", result.getName());
        verify(next, times(1)).validate(authorDto);
    }

    @Test
    @DisplayName("Quando name é null deve lançar MissingMandatoryFieldsException")
    void testValidate_whenNameIsNull_shouldThrowMissingMandatoryFieldsException() {
        // Arrange
        authorDto.setName(null);

        // Act & Assert
        MissingMandatoryFieldsException exception = assertThrows(
                MissingMandatoryFieldsException.class,
                () -> validator.validate(authorDto)
        );

        assertEquals("Campos obrigatorios ausentes", exception.getMessage());
    }

    @Test
    @DisplayName("Quando literaryGenre é null deve lançar MissingMandatoryFieldsException")
    void testValidate_whenLiteraryGenreIsNull_shouldThrowMissingMandatoryFieldsException() {
        // Arrange
        authorDto.setLiteraryGenre(null);

        // Act & Assert
        MissingMandatoryFieldsException exception = assertThrows(
                MissingMandatoryFieldsException.class,
                () -> validator.validate(authorDto)
        );

        assertEquals("Campos obrigatorios ausentes", exception.getMessage());
    }

    @Test
    @DisplayName("Quando name e literaryGenre são null deve lançar MissingMandatoryFieldsException")
    void testValidate_whenNameAndLiteraryGenreAreNull_shouldThrowMissingMandatoryFieldsException() {
        // Arrange
        authorDto.setName(null);
        authorDto.setLiteraryGenre(null);

        // Act & Assert
        MissingMandatoryFieldsException exception = assertThrows(
                MissingMandatoryFieldsException.class,
                () -> validator.validate(authorDto)
        );

        assertEquals("Campos obrigatorios ausentes", exception.getMessage());
    }

    @Test
    @DisplayName("Quando authorDto é null deve lançar NullPointerException")
    void testValidate_whenDtoIsNull_shouldThrowNullPointerException() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> validator.validate(null));
    }

    @Test
    @DisplayName("Quando next.validate retorna null, o resultado deve ser null")
    void testValidate_whenNextReturnsNull_shouldReturnNull() {
        // Arrange
        @SuppressWarnings("unchecked")
        AbstractValidator<AuthorDto> next = mock(AbstractValidator.class);
        when(next.validate(authorDto)).thenReturn(null);

        validator.next = next;

        // Act
        AuthorDto result = validator.validate(authorDto);

        // Assert
        assertNull(result);
        verify(next, times(1)).validate(authorDto);
    }

}
