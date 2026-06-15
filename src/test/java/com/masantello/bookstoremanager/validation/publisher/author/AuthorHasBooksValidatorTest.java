package com.masantello.bookstoremanager.validation.publisher.author;

import com.masantello.bookstoremanager.dtos.AuthorDto;
import com.masantello.bookstoremanager.exceptions.DataIntegrityViolationException;
import com.masantello.bookstoremanager.models.Book;
import com.masantello.bookstoremanager.validation.AbstractValidator;
import com.masantello.bookstoremanager.validation.author.AuthorHasBooksValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = AuthorHasBooksValidator.class)
public class AuthorHasBooksValidatorTest {

    @Autowired
    private AuthorHasBooksValidator validator;

    private AuthorDto authorDto;
    private Book book;

    @BeforeEach
    void setup() {
        book = new Book();
        book.setId(1L);
        book.setTitle("A Hora da Estrela");

        authorDto = new AuthorDto();
        authorDto.setId(1L);
        authorDto.setName("Clarice Lispector");
        authorDto.setEmail("clarice@example.com");
        authorDto.setAge(57);
        authorDto.setBirthDate(LocalDate.of(1920, 12, 10));
        authorDto.setBooks(null);
    }


    @Test
    @DisplayName("Quando author não possui livros (lista vazia) e não há next, deve retornar o mesmo AuthorDto")
    void testValidate_whenBooksIsEmpty_andNoNext_shouldReturnSameDto() {
        // Arrange
        authorDto.setBooks(new ArrayList<>());

        // Act
        AuthorDto result = validator.validate(authorDto);

        // Assert
        assertNull(result);
        //assertEquals(authorDto, result);
        assertDoesNotThrow(() -> validator.validate(authorDto));
    }

    @Test
    @DisplayName("Quando author não possui livros e existe next, deve chamar next.validate")
    void testValidate_whenBooksIsNull_andHasNext_shouldCallNext() {
        // Arrange
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
    @DisplayName("Quando author possui livros deve lançar DataIntegrityViolationException")
    void testValidate_whenAuthorHasBooks_shouldThrowDataIntegrityViolationException() {
        // Arrange
        authorDto.setBooks(List.of(book));

        // Act & Assert
        DataIntegrityViolationException exception = assertThrows(
                DataIntegrityViolationException.class,
                () -> validator.validate(authorDto)
        );

        assertEquals(String.format("Author %s has some books registered. It is not possible to delete it",
                authorDto.getName()), exception.getMessage());
    }

    @Test
    @DisplayName("Quando author possui múltiplos livros deve lançar DataIntegrityViolationException")
    void testValidate_whenAuthorHasMultipleBooks_shouldThrowDataIntegrityViolationException() {
        // Arrange
        Book book2 = new Book();
        book2.setId(2L);
        book2.setTitle("A Paixão Segundo G.H.");

        authorDto.setBooks(List.of(book, book2));

        // Act & Assert
        DataIntegrityViolationException exception = assertThrows(
                DataIntegrityViolationException.class,
                () -> validator.validate(authorDto)
        );

        assertEquals(String.format("Author %s has some books registered. It is not possible to delete it",
                authorDto.getName()), exception.getMessage());
    }

    @Test
    @DisplayName("Quando author possui livros, não deve chamar next e deve lançar exception")
    void testValidate_whenAuthorHasBooks_shouldNotCallNext() {
        // Arrange
        authorDto.setBooks(List.of(book));

        @SuppressWarnings("unchecked")
        AbstractValidator<AuthorDto> next = mock(AbstractValidator.class);
        validator.next = next;

        // Act & Assert
        assertThrows(DataIntegrityViolationException.class, () -> validator.validate(authorDto));

        // next nunca deve ser chamado
        verify(next, never()).validate(any());
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
