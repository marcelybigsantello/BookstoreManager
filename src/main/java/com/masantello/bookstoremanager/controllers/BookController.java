package com.masantello.bookstoremanager.controllers;

import com.masantello.bookstoremanager.dtos.BookDto;
import com.masantello.bookstoremanager.dtos.BookResponseDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Api("Books Management")
public interface BookController {

    @ApiOperation(value = "Book creation operation")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Success book creation"),
            @ApiResponse(code = 400, message = "Missing required fields, wrong field range value or book already registered")
    })
    ResponseEntity<BookResponseDto> create(@RequestBody BookDto bookDto);

    @ApiOperation(value = "List all books operation")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return all registered books")
    })
    ResponseEntity<List<BookResponseDto>> findAll();

    @ApiOperation(value = "Find Book by Title operation")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success book found"),
            @ApiResponse(code = 404, message = "Book not found error code")
    })
    ResponseEntity<List<BookResponseDto>> findByTitle(@PathVariable String title);

    @ApiOperation(value = "List all books of an author")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return all books of that author"),
            @ApiResponse(code = 404, message = "Books of author were not found")
    })
    ResponseEntity<List<BookResponseDto>> findBooksOfAnAuthor(@PathVariable String authorName);

    @ApiOperation(value = "List all books of a publisher")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return all books of that publisher"),
            @ApiResponse(code = 404, message = "Books of publisher were not found")
    })
    ResponseEntity<List<BookResponseDto>> findBooksOfPublisher(@PathVariable String publisherName);

    @ApiOperation(value = "Update book's data")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success book update"),
            @ApiResponse(code = 404, message = "Book not found error code")
    })
    ResponseEntity<BookResponseDto> update(@PathVariable Long bookId, @RequestBody BookDto bookDto);

    @ApiOperation(value = "Delete book operation")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Success book delete"),
            @ApiResponse(code = 404, message = "Book not found error code")
    })
    ResponseEntity<Void> delete(@PathVariable Long bookId);
}
