package com.masantello.bookstoremanager.controllers;

import com.masantello.bookstoremanager.dtos.BookDto;
import com.masantello.bookstoremanager.dtos.BookResponseDto;
import com.masantello.bookstoremanager.services.BookService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/books")
public class BookControllerImpl implements BookController {

    private final BookService bookService;

    public BookControllerImpl(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping
    public ResponseEntity<BookResponseDto> create(@RequestBody @Valid BookDto bookDto) {
        var book = bookService.create(bookDto);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(bookDto.getId()).toUri();
        return ResponseEntity.ok().body(book);
    }

    @GetMapping
    public ResponseEntity<List<BookResponseDto>> findAll() {
        var books = bookService.findAll();

        return ResponseEntity.ok().body(books);
    }

    @GetMapping(value = "/{title}")
    public ResponseEntity<List<BookResponseDto>> findByTitle(@PathVariable String title) {
        var books = bookService.findByTitle(title);

        return ResponseEntity.ok().body(books);
    }

    @GetMapping(value = "author/{authorName}")
    public ResponseEntity<List<BookResponseDto>> findBooksOfAnAuthor(@PathVariable String authorName) {
        var books = bookService.findBooksOfAnAuthor(authorName);

        return ResponseEntity.ok().body(books);
    }

    @GetMapping(value = "publisher/{publisherName}")
    public ResponseEntity<List<BookResponseDto>> findBooksOfPublisher(@PathVariable String publisherName) {
        var books = bookService.findBookOfAPublisher(publisherName);

        return ResponseEntity.ok().body(books);
    }

    @PutMapping(value = "/{bookId}")
    public ResponseEntity<BookResponseDto> update(@PathVariable Long bookId, @RequestBody @Valid BookDto bookDto) {
        bookDto.setId(bookId);
        BookResponseDto book = bookService.update(bookDto);

        return ResponseEntity.ok().body(book);
    }

    @DeleteMapping(value = "/{bookId}")
    public ResponseEntity<Void> delete(@PathVariable Long bookId) {
        bookService.delete(bookId);

        return ResponseEntity.noContent().build();
    }
}
