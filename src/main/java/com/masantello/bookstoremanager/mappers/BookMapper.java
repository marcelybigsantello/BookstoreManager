package com.masantello.bookstoremanager.mappers;

import com.masantello.bookstoremanager.dtos.BookDto;
import com.masantello.bookstoremanager.dtos.BookResponseDto;
import com.masantello.bookstoremanager.models.Book;
import org.springframework.stereotype.Component;

@Component
public class BookMapper {

    public BookDto convertToDto(Book book) {
        var bookDto = new BookDto();
        bookDto.setId(book.getId());
        bookDto.setTitle(book.getTitle());
        bookDto.setIsbn(book.getIsbn());
        bookDto.setPages(book.getPages());
        bookDto.setReleaseDate(book.getReleaseDate());
        return bookDto;
    }

    public Book convertToModel(BookDto bookDto) {
        var book = new Book();
        book.setId(bookDto.getId());
        book.setTitle(bookDto.getTitle());
        book.setIsbn(bookDto.getIsbn());
        book.setPages(bookDto.getPages());
        book.setReleaseDate(bookDto.getReleaseDate());
        return book;
    }

    public BookResponseDto convertToResponseDto(Book book) {
        var bookResponseDto = new BookResponseDto();
        bookResponseDto.setId(book.getId());
        bookResponseDto.setTitle(book.getTitle());
        bookResponseDto.setIsbn(book.getIsbn());
        bookResponseDto.setPages(book.getPages());
        bookResponseDto.setReleaseDate(book.getReleaseDate());
        bookResponseDto.setAuthor(book.getAuthor().getId());
        bookResponseDto.setPublisher(book.getPublisher().getId());

        return bookResponseDto;
    }
}
