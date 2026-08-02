package com.masantello.bookstoremanager.validation.book;

import com.masantello.bookstoremanager.dtos.BookDto;
import com.masantello.bookstoremanager.models.Book;
import com.masantello.bookstoremanager.repositories.BookRepository;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = BookAlreadyExistsValidator.class)
public class BookAlreadyExistsValidatorTest {

    @Autowired
    private BookAlreadyExistsValidator validator;

    @MockitoBean
    private BookRepository bookRepository;

    private BookDto bookDto;

    @BeforeEach
    void setup() {
        bookDto = new BookDto();
        bookDto.setId(1L);
        bookDto.setTitle("1984");
        bookDto.setPages(416);
        bookDto.setReleaseDate(LocalDate.of(1949, 6, 8));
    }

    @Test
    @DisplayName("validate - o livro não está cadastrado previamente e deve retornar o próprio objeto DTO")
    void validate_shouldReturnTheBookDtoSuccessfully() {
        //Arrange
        when(bookRepository.findById(anyLong())).thenReturn(Optional.empty());

        //Act
        var result = validator.validate(bookDto);

        //Assert
        assertNotNull(result);
        assertEquals(result.getId(), bookDto.getId());
        assertEquals(result.getTitle(), bookDto.getTitle());
        assertEquals(result.getPages(), bookDto.getPages());
        assertEquals(result.getReleaseDate(), bookDto.getReleaseDate());
        verify(bookRepository).findById(anyLong());
    }

    @Test
    @DisplayName("validate - o livro já está cadastrado e deve retornar lançar EntityExistsException")
    void validate_shouldThrowEntityExistsException() {
        //Arrange
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(new Book()));

        //Act && Assert
        assertThrows(EntityExistsException.class, () -> validator.validate(bookDto),
                "Deve lançar EntityExistsException quando um livro buscado pelo ID já existe cadastrado");
    }

}
