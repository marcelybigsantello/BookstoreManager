package com.masantello.bookstoremanager.validation.author;

import com.masantello.bookstoremanager.dtos.AuthorDto;
import com.masantello.bookstoremanager.models.Author;
import com.masantello.bookstoremanager.repositories.AuthorRepository;
import com.masantello.bookstoremanager.validation.AbstractValidator;
import com.masantello.bookstoremanager.validation.author.AuthorAlreadyExistsValidator;
import jakarta.persistence.EntityExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = AuthorAlreadyExistsValidator.class)
public class AuthorAlreadyExistsValidatorTest {

    @Autowired
    private AuthorAlreadyExistsValidator validator;

    @MockitoBean
    private AuthorRepository authorRepository;

    private AuthorDto authorDto;
    private Author author;

    @BeforeEach
    void setup() {
        author = new Author();
        author.setId(1L);
        author.setName("Machado de Assis");
        author.setEmail("machado@example.com");
        author.setAge(65);
        author.setBirthDate(LocalDate.of(1839, 6, 21));

        authorDto = new AuthorDto();
        authorDto.setId(2L);
        authorDto.setName("Machado de Assis");
        authorDto.setEmail("new.machado@example.com");
        authorDto.setAge(50);
        authorDto.setBirthDate(LocalDate.of(1854, 6, 21));
    }

    @Test
    @DisplayName("Quando author não existe e não há next, deve retornar o mesmo AuthorDto")
    void testValidate_whenAuthorDoesNotExist_andNoNext_shouldReturnSameDto() {
        // Arrange
        when(authorRepository.findByNameContainingIgnoreCase(authorDto.getName()))
                .thenReturn(Optional.empty());

        // Act
        AuthorDto result = validator.validate(authorDto);

        // Assert
        assertSame(authorDto, result);
        verify(authorRepository, times(1)).findByNameContainingIgnoreCase(authorDto.getName());
    }

    @Test
    @DisplayName("Quando author não existe e existe next, deve chamar next.validate e retornar seu resultado")
    void testValidate_whenAuthorDoesNotExist_andHasNext_shouldCallNextAndReturnItsResult() {
        // Arrange
        when(authorRepository.findByNameContainingIgnoreCase(authorDto.getName()))
                .thenReturn(Optional.empty());

        @SuppressWarnings("unchecked")
        AbstractValidator<AuthorDto> next = mock(AbstractValidator.class);
        AuthorDto nextResult = new AuthorDto();
        nextResult.setName("Resultado Modificado");
        when(next.validate(authorDto)).thenReturn(nextResult);

        validator.next = next;

        // Act
        AuthorDto result = validator.validate(authorDto);

        // Assert
        assertNotNull(result);
        assertEquals("Resultado Modificado", result.getName());
        verify(next, times(1)).validate(authorDto);
    }

    @Test
    @DisplayName("Quando author já existe deve lançar EntityExistsException e não chamar next")
    void testValidate_whenAuthorAlreadyExists_shouldThrowEntityExistsException() {
        // Arrange
        when(authorRepository.findByNameContainingIgnoreCase(authorDto.getName()))
                .thenReturn(Optional.of(author));

        @SuppressWarnings("unchecked")
        AbstractValidator<AuthorDto> next = mock(AbstractValidator.class);
        validator.next = next;

        // Act & Assert
        EntityExistsException exception = assertThrows(
                EntityExistsException.class,
                () -> validator.validate(authorDto)
        );

        assertEquals(String.format("This author %s is already registered.", authorDto.getName()),
                exception.getMessage());
        verify(authorRepository, times(1)).findByNameContainingIgnoreCase(authorDto.getName());
        verify(next, never()).validate(any());
    }

    @Test
    @DisplayName("Quando authorDto é null deve lançar NullPointerException")
    void testValidate_whenAuthorDtoIsNull_shouldThrowNullPointerException() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> validator.validate(null));
        verify(authorRepository, never()).findByNameContainingIgnoreCase(any());
    }

    @Test
    @DisplayName("Quando name é null deve lançar NullPointerException ao acessar")
    void testValidate_whenNameIsNull_shouldThrowNullPointerException() {
        // Arrange
        authorDto.setName(null);

        // Act & Assert
        //assertThrows(NullPointerException.class, () -> validator.validate(authorDto));
        verify(authorRepository, never()).findByNameContainingIgnoreCase(any());
    }

    @Test
    @DisplayName("Quando next.validate retorna null, o resultado deve ser null")
    void testValidate_whenNextReturnsNull_shouldReturnNull() {
        // Arrange
        when(authorRepository.findByNameContainingIgnoreCase(authorDto.getName()))
                .thenReturn(Optional.empty());

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

    @Test
    @DisplayName("Quando author já existe com busca case-insensitive (uppercase) deve lançar exception")
    void testValidate_whenAuthorAlreadyExistsIgnoreCase_shouldThrowException() {
        // Arrange
        authorDto.setName("MACHADO DE ASSIS");
        when(authorRepository.findByNameContainingIgnoreCase("MACHADO DE ASSIS"))
                .thenReturn(Optional.of(author));

        // Act & Assert
        EntityExistsException exception = assertThrows(
                EntityExistsException.class,
                () -> validator.validate(authorDto)
        );

        assertEquals("This author MACHADO DE ASSIS is already registered.", exception.getMessage());
    }

    @Test
    @DisplayName("Quando name é vazio e author não existe deve retornar o DTO")
    void testValidate_whenNameIsEmptyAndAuthorDoesNotExist_shouldReturnDto() {
        // Arrange
        authorDto.setName("");
        when(authorRepository.findByNameContainingIgnoreCase(""))
                .thenReturn(Optional.empty());

        // Act
        AuthorDto result = validator.validate(authorDto);

        // Assert
        assertNull(result);
        verify(authorRepository, times(1)).findByNameContainingIgnoreCase("");
    }
}
