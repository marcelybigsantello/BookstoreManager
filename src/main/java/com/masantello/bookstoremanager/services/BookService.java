package com.masantello.bookstoremanager.services;

import com.masantello.bookstoremanager.dtos.AuthenticatedUser;
import com.masantello.bookstoremanager.dtos.BookDto;
import com.masantello.bookstoremanager.dtos.BookResponseDto;
import com.masantello.bookstoremanager.mappers.BookMapper;
import com.masantello.bookstoremanager.models.Book;
import com.masantello.bookstoremanager.repositories.BookRepository;
import com.masantello.bookstoremanager.validation.AbstractValidator;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.text.Collator;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class BookService {

    private static final Logger logger = LoggerFactory.getLogger(BookService.class);

    private final BookRepository bookRepository;
    private final UserService userService;
    private final AuthorService authorService;
    private final PublisherService publisherService;
    private final BookMapper bookMapper;
    private final AbstractValidator<BookDto> createBookValidator;

    public BookService(BookRepository bookRepository, UserService userService, AuthorService authorService,
                       PublisherService publisherService, BookMapper bookMapper,
                       @Qualifier("bookCreateValidator")
                       AbstractValidator<BookDto> createBookValidator) {
        this.bookRepository = bookRepository;
        this.userService = userService;
        this.authorService = authorService;
        this.publisherService = publisherService;
        this.bookMapper = bookMapper;
        this.createBookValidator = createBookValidator;
    }

    public BookResponseDto create(@Valid BookDto bookDto, AuthenticatedUser authenticatedUser) {
        //Chain of Responsibilities
        createBookValidator.validate(bookDto);

        var foundUser = userService.findByLoggedUsername(authenticatedUser.getUsername());
        var foundAuthor = authorService.findById(bookDto.getAuthor().getId());
        var foundPublisher = publisherService.findById(bookDto.getPublisher().getId());

        var book = bookMapper.convertToModel(bookDto);
        book.setUser(foundUser);
        book.setAuthor(foundAuthor);
        book.setPublisher(foundPublisher);

        book = bookRepository.save(book);

        return bookMapper.convertToResponseDto(book);
    }

    public List<BookResponseDto> findAll() {
        var books = bookRepository.findAll();
        return books.stream()
                .map(bookMapper::convertToResponseDto)
                .sorted(Comparator.comparing(BookResponseDto::getId))
                .toList();
    }

    public List<BookResponseDto> findByTitle(String title) {

        Collator collator = Collator.getInstance(Locale.US);
        collator.setStrength(Collator.PRIMARY);

        var books = bookRepository.findByTitleContainingIgnoreCase(title);

        if (books.isEmpty()) {
            var errorMessage = String.format("Book with title '%s' were not found. ", title);
            logger.warn(errorMessage);
            throw new EntityNotFoundException(errorMessage);
        }

        return books
                .stream()
                .filter(books1 -> collator.compare(books1.getTitle(), title) != 0)
                .map(bookMapper::convertToResponseDto)
                .collect(Collectors.toList());
    }

    public List<BookResponseDto> findBooksOfAnAuthor(String authorName) {
        var books = bookRepository.findAllBooksByAuthor(authorName);

        if (books.isEmpty()) {
            var errorMessage = String.format("It was not possible to find books of author '%s'", authorName);
            logger.warn(errorMessage);
            return List.of();
        }

        return books.stream()
                .map(bookMapper::convertToResponseDto)
                .sorted(Comparator.comparing(BookResponseDto::getId))
                .toList();
    }

    public List<BookResponseDto> findBooksOfAPublisher(String publisherName) {
        var books = bookRepository.findAllBooksByPublisher(publisherName);

        if (books.isEmpty()) {
            var errorMessage = String.format("It was not possible to find books of publisher '%s'", publisherName);
            logger.error(errorMessage);
            return List.of();
        }

        return books.stream()
                .map(bookMapper::convertToResponseDto)
                .sorted(Comparator.comparing(BookResponseDto::getId))
                .toList();
    }

    public BookResponseDto update(@Valid BookDto bookDto) {

        var bookNewData = new Book();
        bookNewData.setTitle(bookDto.getTitle());
        bookNewData.setIsbn(bookDto.getIsbn());
        bookNewData.setPages(bookDto.getPages());
        bookNewData.setReleaseDate(bookDto.getReleaseDate());

        var foundAuthor = authorService.findById(bookDto.getAuthor().getId());
        bookNewData.setAuthor(foundAuthor);
        var foundPublisher = publisherService.findById(bookDto.getPublisher().getId());
        bookNewData.setPublisher(foundPublisher);

        bookRepository.save(bookNewData);

        logger.info("Book '{}' updated successfully.", bookNewData.getTitle());

        return bookMapper.convertToResponseDto(bookNewData);
    }

    public void delete(Long bookId) {

        var book = bookRepository.findById(bookId);

        book.ifPresentOrElse(book1 -> {
            logger.info("Book ID='{}', Title='{}' is going to be deleted.", book1.getId(), book1.getTitle());
            bookRepository.delete(book1);
        }, () -> {
            var errorMessage = String.format("Book ID='%s' not found", bookId);
            logger.error(errorMessage);
            throw new EntityNotFoundException(errorMessage);
        });
    }

}
