package com.masantello.bookstoremanager.services;

import com.masantello.bookstoremanager.dtos.*;
import com.masantello.bookstoremanager.mappers.AuthorMapper;
import com.masantello.bookstoremanager.mappers.BookMapper;
import com.masantello.bookstoremanager.mappers.PublisherMapper;
import com.masantello.bookstoremanager.models.Author;
import com.masantello.bookstoremanager.models.Book;
import com.masantello.bookstoremanager.models.Publisher;
import com.masantello.bookstoremanager.models.User;
import com.masantello.bookstoremanager.models.enums.LiteraryGenre;
import com.masantello.bookstoremanager.repositories.BookRepository;
import com.masantello.bookstoremanager.validation.AbstractValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = BookService.class)
public class BookServiceTest {

    @Autowired
    private BookService bookService;

    @MockitoBean
    private BookRepository bookRepository;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private AuthorService authorService;

    @MockitoBean
    private PublisherService publisherService;

    @MockitoBean
    private BookMapper bookMapper;

    @MockitoBean(name = "bookCreateValidator")
    private AbstractValidator<BookDto> createBookValidator;

    private BookDto bookDto;
    private BookResponseDto bookResponseDto;

    @BeforeEach
    void setup() {
        bookDto = buildBookDto();
        bookResponseDto = buildBookResponseDto();

        // reset mocks to ensure clean state between tests
        reset(bookRepository, userService, authorService, publisherService, bookMapper, createBookValidator);
    }

    @Test
    void create_shouldSaveBookAndReturnResponseDto() {
        // Arrange
        var authUser = new AuthenticatedUser("jdoe", "pwd", "USER");
        var user = new User(1L, "John Doe", "john@x.com", null, null, "jdoe", "pwd", null, null);
        var modelBook = new Book();
        modelBook.setTitle(bookDto.getTitle());
        modelBook.setIsbn(bookDto.getIsbn());
        modelBook.setPages(bookDto.getPages());
        modelBook.setReleaseDate(bookDto.getReleaseDate());

        var savedBook = new Book(10L, modelBook.getTitle(), modelBook.getIsbn(), modelBook.getPages(), modelBook.getReleaseDate(), new Author(), new Publisher(), user);

        var responseDto = new BookResponseDto();
        responseDto.setId(10L);
        responseDto.setTitle(bookDto.getTitle());

        when(createBookValidator.validate(any(BookDto.class))).thenReturn(bookDto);
        when(userService.findByLoggedUsername(eq(authUser.getUsername()))).thenReturn(user);
        when(authorService.findById(eq(bookDto.getAuthor().getId()))).thenReturn(bookDto.getAuthor());
        when(publisherService.findById(eq(bookDto.getPublisher().getId()))).thenReturn(bookDto.getPublisher());
        when(bookMapper.convertToModel(any(BookDto.class))).thenReturn(modelBook);
        when(bookRepository.save(any(Book.class))).thenReturn(savedBook);
        when(bookMapper.convertToResponseDto(eq(savedBook))).thenReturn(responseDto);

        // Act
        var result = bookService.create(bookDto, authUser);

        // Assert
        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals(bookDto.getTitle(), result.getTitle());

        verify(createBookValidator).validate(bookDto);
        verify(userService).findByLoggedUsername(authUser.getUsername());
        verify(bookMapper).convertToResponseDto(savedBook);
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void findAll_shouldReturnSortedListById() {
        // Arrange
        var b1 = new Book(2L, "B2", null, 100, null, null, null, null);
        var b2 = new Book(1L, "B1", null, 200, null, null, null, null);
        when(bookRepository.findAll()).thenReturn(List.of(b1, b2));
        when(bookMapper.convertToResponseDto(b1)).thenReturn(new BookResponseDto(2L, "B2", null, 100, null, null, null));
        when(bookMapper.convertToResponseDto(b2)).thenReturn(new BookResponseDto(1L, "B1", null, 200, null, null, null));

        // Act
        var result = bookService.findAll();

        // Assert
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId()); // sorted by id ascending
        assertEquals(2L, result.get(1).getId());
    }

    @Test
    void findByTitle_whenNoBooks_shouldThrowEntityNotFoundException() {
        // Arrange
        when(bookRepository.findByTitleContainingIgnoreCase(anyString())).thenReturn(List.of());

        // Act / Assert
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> bookService.findByTitle("nope"));
        assertTrue(ex.getMessage().contains("were not found"));
        verify(bookRepository).findByTitleContainingIgnoreCase("nope");
    }

    @Test
    void findByTitle_shouldFilterOutExactMatches() {
        // Arrange
        var exact = new Book(1L, "Exact Title", null, 0, null, null, null, null);
        var similar = new Book(2L, "Exact Title - Special Edition", null, 0, null, null, null, null);

        when(bookRepository.findByTitleContainingIgnoreCase("Exact Title")).thenReturn(List.of(exact, similar));
        when(bookMapper.convertToResponseDto(similar)).thenReturn(new BookResponseDto(2L, similar.getTitle(), null, 0, null, null, null));

        // Act
        var result = bookService.findByTitle("Exact Title");

        // Assert: exact should be filtered out, similar included
        assertEquals(1, result.size());
        assertEquals(2L, result.get(0).getId());
    }

    @Test
    void findBooksOfAnAuthor_whenFindsBooksOfAnAuthor_shouldReturnListOfBooks() {
        // Arrange
        var authorName = "Tracey Garvis Graves";
        var book1 = new Book(10L, "Sem Lógica para o Amor", null, 320, null,
                new Author(), new Publisher(), new User());
        when(bookRepository.findAllBooksByAuthor(anyString())).thenReturn(List.of(book1));
        when(bookMapper.convertToResponseDto(book1)).thenReturn(bookResponseDto);

        // Act
        var result = bookService.findBooksOfAnAuthor(authorName);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(bookDto.getId(), result.get(0).getId());
        assertEquals(bookDto.getTitle(), result.get(0).getTitle());
        assertEquals(bookDto.getPages(), result.get(0).getPages());

        verify(bookRepository).findAllBooksByAuthor(authorName);
        verify(bookMapper).convertToResponseDto(book1);
    }

    @Test
    void findBooksOfAnAuthor_whenDoesNotFindAnyBookOfAnAuthor_shouldReturnEmptyList() {
        // Arrange
        when(bookRepository.findAllBooksByAuthor(anyString())).thenReturn(List.of());

        //Act && Assert
        var result = bookService.findBooksOfAnAuthor(anyString());

        assertTrue(result.isEmpty());
        verify(bookRepository).findAllBooksByAuthor(anyString());
        verify(bookMapper, never()).convertToResponseDto(any(Book.class));
    }

    @Test
    void findBookOfAPublisher_whenFindsBooksOfThisPublisher_shouldReturnListOfBooks() {
        // Arrange
        var publisherName = "Jangada";
        var book1 = new Book(10L, "Sem Lógica para o Amor", null, 320, null,
                new Author(), new Publisher(), new User());
        when(bookRepository.findAllBooksByPublisher(anyString())).thenReturn(List.of(book1));
        when(bookMapper.convertToResponseDto(book1)).thenReturn(bookResponseDto);

        // Act
        var result = bookService.findBooksOfAPublisher(publisherName);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(bookDto.getId(), result.get(0).getId());
        assertEquals(bookDto.getTitle(), result.get(0).getTitle());
        assertEquals(bookDto.getPages(), result.get(0).getPages());

        verify(bookRepository).findAllBooksByPublisher(publisherName);
        verify(bookMapper).convertToResponseDto(book1);
    }

    @Test
    void findBooksOfAPublisher_whenDoesNotFindAnyBookOfThisPublisher_shouldReturnEmptyList() {
        // Arrange
        when(bookRepository.findAllBooksByPublisher(anyString())).thenReturn(List.of());

        //Act && Assert
        var result = bookService.findBooksOfAPublisher(anyString());

        assertTrue(result.isEmpty());
        verify(bookRepository).findAllBooksByPublisher(anyString());
        verify(bookMapper, never()).convertToResponseDto(any(Book.class));
    }

    @Test
    void update_shouldUpdateBooksDataAndConvertToResponseDto() {
        // Arrange
        bookDto.setIsbn("978-85-123456-7");
        var foundAuthor = bookDto.getAuthor();
        var foundPublisher = bookDto.getPublisher();

        when(authorService.findById(bookDto.getAuthor().getId())).thenReturn(foundAuthor);
        when(publisherService.findById(bookDto.getPublisher().getId())).thenReturn(foundPublisher);
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(bookMapper.convertToResponseDto(any(Book.class))).thenReturn(bookResponseDto);

        // Act
        var result = bookService.update(bookDto);

        // Assert
        var savedBookCaptor = ArgumentCaptor.forClass(Book.class);

        verify(authorService).findById(bookDto.getAuthor().getId());
        verify(publisherService).findById(bookDto.getPublisher().getId());
        verify(bookRepository).save(savedBookCaptor.capture());
        verify(bookMapper).convertToResponseDto(savedBookCaptor.getValue());

        var savedBook = savedBookCaptor.getValue();
        assertNotNull(savedBook);
        assertEquals(bookDto.getTitle(), savedBook.getTitle());
        assertEquals(bookDto.getIsbn(), savedBook.getIsbn());
        assertEquals(bookDto.getPages(), savedBook.getPages());
        assertEquals(bookDto.getReleaseDate(), savedBook.getReleaseDate());
        assertSame(foundAuthor, savedBook.getAuthor());
        assertSame(foundPublisher, savedBook.getPublisher());

        assertSame(bookResponseDto, result);
    }

    @Test
    void delete_whenFound_shouldCallDelete() {
        // Arrange
        var book = new Book(5L, "BookToDelete", null, 416, null, null, null, null);
        when(bookRepository.findById(5L)).thenReturn(Optional.of(book));
        doNothing().when(bookRepository).delete(book);

        // Act
        bookService.delete(5L);

        // Assert
        verify(bookRepository).findById(5L);
        verify(bookRepository).delete(book);
    }

    @Test
    void delete_whenNotFound_shouldThrowEntityNotFoundException() {
        // Arrange
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        // Act / Assert
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> bookService.delete(99L));
        assertTrue(ex.getMessage().equalsIgnoreCase("Book ID='99' not found"));
        verify(bookRepository).findById(99L);
        verify(bookRepository, never()).delete(any(Book.class));
    }

    private BookDto buildBookDto() {
        bookDto = new BookDto();
        bookDto.setId(10L);
        bookDto.setTitle("Sem Lógica para o Amor");
        bookDto.setPages(320);
        bookDto.setReleaseDate(LocalDate.of(2020, 11, 9));
        bookDto.setAuthor(new Author(5L, "Tracey Garvis Graves", null, null, null, LiteraryGenre.ROMANCE, null));
        bookDto.setPublisher(new Publisher(10L, "Jangada", "0010", null, null, null));
        return bookDto;
    }

    private BookResponseDto buildBookResponseDto() {
        var bookResponseDto = new BookResponseDto();
        bookResponseDto.setId(10L);
        bookResponseDto.setTitle("Sem Lógica para o Amor");
        bookResponseDto.setPages(320);
        bookResponseDto.setAuthor(new AuthorResponseDto("Tracey Garvis Graves", "Romance"));
        bookResponseDto.setPublisher(new PublisherResponseDto(10L, "Jangada"));

        return bookResponseDto;
    }

}
