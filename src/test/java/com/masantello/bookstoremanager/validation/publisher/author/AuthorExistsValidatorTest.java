package com.masantello.bookstoremanager.validation.publisher.author;

import com.masantello.bookstoremanager.dtos.AuthorDto;
import com.masantello.bookstoremanager.models.Author;
import com.masantello.bookstoremanager.repositories.AuthorRepository;
import com.masantello.bookstoremanager.validation.AbstractValidator;
import com.masantello.bookstoremanager.validation.author.AuthorExistsValidator;
import jakarta.persistence.EntityNotFoundException;
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

@SpringBootTest(classes = AuthorExistsValidator.class)
public class AuthorExistsValidatorTest {

    @Autowired
    private AuthorExistsValidator validator;

    @MockitoBean
    private AuthorRepository authorRepository;

    private Author author;
    private AuthorDto authorDto;

    @BeforeEach
    void setup() {
        author = new Author();
        author.setId(1L);
        author.setName("Jorge Amado");
        author.setEmail("jorge@example.com");
        author.setAge(80);
        author.setBirthDate(LocalDate.of(1912, 4, 10));

        authorDto = new AuthorDto();
        authorDto.setId(1L);
        authorDto.setName("Jorge Amado");
        authorDto.setEmail("jorge@example.com");
        authorDto.setAge(80);
        authorDto.setBirthDate(LocalDate.of(1912, 4, 10));
    }

    @Test
    @DisplayName("Quando author existe no banco e não há next, deve retornar o mesmo AuthorDto")
    void testValidate_whenAuthorExists_andNoNext_shouldReturnSameDto() {
        // Arrange
        when(authorRepository.findByNameContainingIgnoreCase(authorDto.getName()))
                .thenReturn(Optional.of(author));

        // Act
        AuthorDto result = validator.validate(authorDto);

        // Assert
        assertSame(authorDto, result);
        assertEquals("Jorge Amado", result.getName());
        verify(authorRepository, times(1)).findByNameContainingIgnoreCase(authorDto.getName());
    }

    @Test
    @DisplayName("Quando author existe no banco e existe next, deve chamar next.validate e retornar seu resultado")
    void testValidate_whenAuthorExists_andHasNext_shouldCallNextAndReturnItsResult() {
        // Arrange
        when(authorRepository.findByNameContainingIgnoreCase(authorDto.getName()))
                .thenReturn(Optional.of(author));

        @SuppressWarnings("unchecked")
        AbstractValidator<AuthorDto> next = mock(AbstractValidator.class);
        AuthorDto nextResult = new AuthorDto();
        nextResult.setName("Resultado Modified");
        when(next.validate(authorDto)).thenReturn(nextResult);

        validator.next = next;

        // Act
        AuthorDto result = validator.validate(authorDto);

        // Assert
        assertNotNull(result);
        assertEquals("Resultado Modified", result.getName());
        verify(authorRepository, times(1)).findByNameContainingIgnoreCase(authorDto.getName());
        verify(next, times(1)).validate(authorDto);
    }

    @Test
    @DisplayName("Quando author não existe no banco deve lançar EntityNotFoundException")
    void testValidate_whenAuthorDoesNotExist_shouldThrowEntityNotFoundException() {
        // Arrange
        when(authorRepository.findByNameContainingIgnoreCase(authorDto.getName()))
                .thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> validator.validate(authorDto)
        );

        assertEquals(String.format("Author %s not found in database", authorDto.getName()),
                exception.getMessage());
        verify(authorRepository, times(1)).findByNameContainingIgnoreCase(authorDto.getName());
    }

    @Test
    @DisplayName("Quando authorDto é null deve lançar NullPointerException")
    void testValidate_whenAuthorDtoIsNull_shouldThrowNullPointerException() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> validator.validate(null));

        // nunca deve chamar o repositório
        verify(authorRepository, never()).findByNameContainingIgnoreCase(any());
    }

    @Test
    @DisplayName("Quando name do author é vazio deve lançar EntityNotFoundException")
    void testValidate_whenNameIsEmpty_shouldThrowEntityNotFoundException() {
        // Arrange
        authorDto.setName("");
        when(authorRepository.findByNameContainingIgnoreCase(""))
                .thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> validator.validate(authorDto)
        );

        assertEquals("Author  not found in database", exception.getMessage());
        verify(authorRepository, times(1)).findByNameContainingIgnoreCase("");
    }

    @Test
    @DisplayName("Quando repositório retorna null deve lançar NullPointerException")
    void testValidate_whenRepositoryReturnsNull_shouldThrowNullPointerException() {
        // Arrange
        when(authorRepository.findByNameContainingIgnoreCase(authorDto.getName()))
                .thenThrow(new NullPointerException("Repository returned null"));

        // Act & Assert
        assertThrows(NullPointerException.class, () -> validator.validate(authorDto));

        verify(authorRepository, times(1)).findByNameContainingIgnoreCase(authorDto.getName());
    }

    @Test
    @DisplayName("Quando next.validate retorna null, o resultado deve ser null")
    void testValidate_whenNextReturnsNull_shouldReturnNull() {
        // Arrange
        when(authorRepository.findByNameContainingIgnoreCase(authorDto.getName()))
                .thenReturn(Optional.of(author));

        @SuppressWarnings("unchecked")
        AbstractValidator<AuthorDto> next = mock(AbstractValidator.class);
        when(next.validate(authorDto)).thenReturn(null);

        validator.next = next;

        // Act
        AuthorDto result = validator.validate(authorDto);

        // Assert
        assertNull(result);
        verify(authorRepository, times(1)).findByNameContainingIgnoreCase(authorDto.getName());
        verify(next, times(1)).validate(authorDto);
    }

    @Test
    @DisplayName("Quando author existe com nome em caso diferente (lowercase) deve ser encontrado")
    void testValidate_whenAuthorExistsWithDifferentCase_shouldFindAuthor() {
        // Arrange
        when(authorRepository.findByNameContainingIgnoreCase("jorge amado"))
                .thenReturn(Optional.of(author));
        authorDto.setName("jorge amado");

        // Act
        AuthorDto result = validator.validate(authorDto);

        // Assert
        assertSame(authorDto, result);
        verify(authorRepository, times(1)).findByNameContainingIgnoreCase("jorge amado");
    }

    @Test
    @DisplayName("Quando author existe com nome em caso diferente (uppercase) deve ser encontrado")
    void testValidate_whenAuthorExistsWithUpperCase_shouldFindAuthor() {
        // Arrange
        when(authorRepository.findByNameContainingIgnoreCase("JORGE AMADO"))
                .thenReturn(Optional.of(author));
        authorDto.setName("JORGE AMADO");

        // Act
        AuthorDto result = validator.validate(authorDto);

        // Assert
        assertSame(authorDto, result);
        verify(authorRepository, times(1)).findByNameContainingIgnoreCase("JORGE AMADO");
    }

    @Test
    @DisplayName("Quando author existe e possui email null deve retornar o DTO")
    void testValidate_whenAuthorExistsWithNullEmail_shouldReturnDto() {
        // Arrange
        authorDto.setEmail(null);
        when(authorRepository.findByNameContainingIgnoreCase(authorDto.getName()))
                .thenReturn(Optional.of(author));

        // Act
        AuthorDto result = validator.validate(authorDto);

        // Assert
        assertSame(authorDto, result);
        assertNull(result.getEmail());
        verify(authorRepository, times(1)).findByNameContainingIgnoreCase(authorDto.getName());
    }

    @Test
    @DisplayName("Quando author existe e possui age null deve retornar o DTO")
    void testValidate_whenAuthorExistsWithNullAge_shouldReturnDto() {
        // Arrange
        authorDto.setAge(null);
        when(authorRepository.findByNameContainingIgnoreCase(authorDto.getName()))
                .thenReturn(Optional.of(author));

        // Act
        AuthorDto result = validator.validate(authorDto);

        // Assert
        assertNull(result);
        verify(authorRepository, times(1)).findByNameContainingIgnoreCase(authorDto.getName());
    }

    @Test
    @DisplayName("Quando author existe com nome contendo espaços no início e fim deve ser encontrado")
    void testValidate_whenAuthorNameHasLeadingAndTrailingSpaces_shouldFindAuthor() {
        // Arrange
        authorDto.setName("   Jorge Amado   ");
        when(authorRepository.findByNameContainingIgnoreCase("   Jorge Amado   "))
                .thenReturn(Optional.of(author));

        // Act
        AuthorDto result = validator.validate(authorDto);

        // Assert
        assertNull(result);
        verify(authorRepository, times(1)).findByNameContainingIgnoreCase("   Jorge Amado   ");
    }

    @Test
    @DisplayName("Quando author não existe torna-se uma tentativa de busca com caracteres especiais deve lançar EntityNotFoundException")
    void testValidate_whenAuthorNameWithSpecialCharacters_shouldThrowEntityNotFound() {
        // Arrange
        authorDto.setName("Jorge @Amado!");
        when(authorRepository.findByNameContainingIgnoreCase("Jorge @Amado!"))
                .thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> validator.validate(authorDto)
        );

        assertEquals("Author Jorge @Amado! not found in database", exception.getMessage());
        verify(authorRepository, times(1)).findByNameContainingIgnoreCase("Jorge @Amado!");
    }

}
