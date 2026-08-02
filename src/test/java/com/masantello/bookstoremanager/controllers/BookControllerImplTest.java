package com.masantello.bookstoremanager.controllers;

import com.masantello.bookstoremanager.dtos.*;
import com.masantello.bookstoremanager.models.Author;
import com.masantello.bookstoremanager.models.Publisher;
import com.masantello.bookstoremanager.services.BookService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = BookControllerImpl.class)
public class BookControllerImplTest {

    @Autowired
    private BookControllerImpl bookController;

    @MockitoBean
    private BookService bookService;

    private BookDto bookDto;
    private BookResponseDto bookResponseDto;
    private AuthenticatedUser authenticatedUser;

    @BeforeEach
    void setup() {
        bookDto = new BookDto();
        bookDto.setTitle("Effective Java");
        bookDto.setIsbn("978-0134685991");
        bookDto.setPages(416);
        bookDto.setReleaseDate(LocalDate.of(2018, 1, 6));

        var author = new Author();
        author.setId(1L);
        bookDto.setAuthor(author);

        var publisher = new Publisher();
        publisher.setId(1L);
        bookDto.setPublisher(publisher);

        bookResponseDto = new BookResponseDto();
        bookResponseDto.setId(10L);
        bookResponseDto.setTitle(bookDto.getTitle());
        bookResponseDto.setIsbn(bookDto.getIsbn());
        bookResponseDto.setPages(bookDto.getPages());
        bookResponseDto.setReleaseDate(bookDto.getReleaseDate());

        authenticatedUser = new AuthenticatedUser("user1", "password", "ADMIN");
    }

    @Test
    @DisplayName("Create - deve criar livro e retornar 201")
    void create_shouldReturnBookCreated() {
        when(bookService.create(any(BookDto.class), any(AuthenticatedUser.class))).thenReturn(bookResponseDto);

        ResponseEntity<BookResponseDto> response = bookController.create(bookDto, authenticatedUser);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getHeaders().getLocation(), "Location header should be present");
        verify(bookService, times(1)).create(bookDto, authenticatedUser);
    }

    @Test
    @DisplayName("findAll - deve retornar lista de livros 200 OK")
    void findAll_shouldReturnBooksList() {
        when(bookService.findAll()).thenReturn(List.of(bookResponseDto));

        ResponseEntity<List<BookResponseDto>> response = bookController.findAll();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(bookResponseDto.getTitle(), response.getBody().get(0).getTitle());
        verify(bookService, times(1)).findAll();
    }

    @Test
    @DisplayName("findAll - deve retornar lista vazia quando não houver livros")
    void findAll_shouldReturnEmptyList() {
        when(bookService.findAll()).thenReturn(List.of());

        var response = bookController.findAll();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
        verify(bookService, times(1)).findAll();
    }

    @Test
    @DisplayName("findByTitle - deve retornar lista de livros contendo o título")
    void findByTitle_shouldReturnBooksList() {
        var title = "Effective Java";
        when(bookService.findByTitle(title)).thenReturn(List.of(bookResponseDto));

        var response = bookController.findByTitle(title);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(bookResponseDto.getTitle(), response.getBody().get(0).getTitle());
        verify(bookService, times(1)).findByTitle(title);
    }

    @Test
    @DisplayName("findByTitle - deve retornar lista vazia e service lançar EntityNotFoundException")
    void findByTitle_shouldPropagateEntityNotFoundException() {
        var title = "A Empregada";
        when(bookService.findByTitle(title)).thenThrow(new EntityNotFoundException("Books with title '" + title
                + "' were not found."));
        assertThrows(EntityNotFoundException.class, () -> bookController.findByTitle(title));
        verify(bookService, times(1)).findByTitle(title);
    }

    @Test
    @DisplayName("findBooksOfAnAuthor - deve retornar lista vazia quando não houver livros do autor")
    void findBooksOfAnAuthor_shouldReturnEmptyBookList() {
        var authorName = "Unknown author";
        when(bookService.findBooksOfAnAuthor(authorName)).thenReturn(List.of());

        var response = bookController.findBooksOfAnAuthor(authorName);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
        verify(bookService, times(1)).findBooksOfAnAuthor(authorName);
    }

    @Test
    @DisplayName("findBooksOfAPublisher - deve retornar lista contendo 02 elementos")
    void findBooksOfAPublisher_shouldReturnBooksList() {
        var publisherName = "Editora Companhia das Letras";

        bookResponseDto.setTitle("A Seleção");
        bookResponseDto.setPages(384);
        bookResponseDto.setAuthor(new AuthorResponseDto("Kiera Cass", "Romance"));
        bookResponseDto.setPublisher(new PublisherResponseDto(1L, "Editora Companhia das Letras"));

        var bookResponseDto2 = new BookResponseDto();
        bookResponseDto2.setTitle("A Elite");
        bookResponseDto2.setPublisher(new PublisherResponseDto(1L, "Editora Companhia das Letras"));
        bookResponseDto2.setAuthor(new AuthorResponseDto("Kiera Cass", "Romance"));
        bookResponseDto2.setPages(354);
        when(bookService.findBooksOfAPublisher(publisherName)).thenReturn(List.of(bookResponseDto, bookResponseDto2));

        var response = bookController.findBooksOfPublisher(publisherName);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals("A Seleção", response.getBody().get(0).getTitle());
        assertEquals("A Elite", response.getBody().get(1).getTitle());
        verify(bookService, times(1)).findBooksOfAPublisher(publisherName);
    }

    @Test
    @DisplayName("Update - deve setar id do path no DTO e retornar 200 com body atualizado")
    void update_shouldReturnUpdatedBook() {
        BookDto newBookDto = new BookDto();
        newBookDto.setTitle("Effective Java - 3rd Edition");
        newBookDto.setPages(500);
        newBookDto.setReleaseDate(LocalDate.of(2019, 1, 6));

        BookResponseDto updatedResponse = new BookResponseDto();
        updatedResponse.setTitle(newBookDto.getTitle());
        updatedResponse.setPages(newBookDto.getPages());

        when(bookService.update(any(BookDto.class))).thenReturn(updatedResponse);

        var response = bookController.update(20L, newBookDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(updatedResponse.getTitle(), response.getBody().getTitle());
        assertEquals(updatedResponse.getPages(), response.getBody().getPages());
        verify(bookService, times(1)).update(newBookDto);
    }

    @Test
    @DisplayName("Delete - deve excluir livro e returnar 204 NO_CONTENT")
    void delete_shouldDeleteBookSuccessfully() {
        var bookId = 10L;

        doNothing().when(bookService).delete(bookId);

        var response = bookController.delete(bookId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(bookService, times(1)).delete(bookId);
    }

}
