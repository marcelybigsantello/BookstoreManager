package com.masantello.bookstoremanager.mappers;

import com.masantello.bookstoremanager.dtos.BookDto;
import com.masantello.bookstoremanager.models.Author;
import com.masantello.bookstoremanager.models.Book;
import com.masantello.bookstoremanager.models.Publisher;
import com.masantello.bookstoremanager.models.enums.LiteraryGenre;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = BookMapper.class)
public class BookMapperTest {

    @Autowired
    private BookMapper bookMapper;

    @Test
    @DisplayName("convertToDto - deve converter 'Book' e retornar BookDto")
    void convertToDto_shouldConvertAndReturn() {
        //Arrange
        var book = new Book();
        book.setId(1L);
        book.setTitle("A revolução dos bichos");
        book.setPages(96);
        book.setIsbn("978-85-359-0277-8");
        book.setReleaseDate(LocalDate.of(1945, 8, 17));

        //Act
        var result = bookMapper.convertToDto(book);

        //Assert
        assertEquals(result.getId(), book.getId());
        assertEquals(result.getTitle(), book.getTitle());
        assertEquals(result.getIsbn(), book.getIsbn());
        assertEquals(result.getPages(), book.getPages());
        assertEquals(result.getReleaseDate(), book.getReleaseDate());
    }

    @Test
    @DisplayName("convertToModel - deve converter 'BookDto' e retornar Book")
    void convertToModel_shouldConvertAndReturn() {
        //Arrange
        var bookDto = new BookDto();
        bookDto.setId(1L);
        bookDto.setTitle("A revolução dos bichos");
        bookDto.setPages(96);
        bookDto.setIsbn("978-85-359-0277-8");
        bookDto.setReleaseDate(LocalDate.of(1945, 8, 17));

        //Act
        var result = bookMapper.convertToModel(bookDto);

        //Assert
        assertEquals(result.getId(), bookDto.getId());
        assertEquals(result.getTitle(), bookDto.getTitle());
        assertEquals(result.getIsbn(), bookDto.getIsbn());
        assertEquals(result.getPages(), bookDto.getPages());
        assertEquals(result.getReleaseDate(), bookDto.getReleaseDate());
    }

    @Test
    @DisplayName("convertToResponseDto - deve converter 'Book' e retornar BookResponseDto")
    void convertToResponseDto_shouldConvertAndReturn() {
        var book = new Book();
        book.setId(1L);
        book.setTitle("A revolução dos bichos");
        book.setPages(96);
        book.setIsbn("978-85-359-0277-8");
        book.setReleaseDate(LocalDate.of(1945, 8, 17));
        book.setAuthor(new Author(1L, "George Orwell", "georgeorwellteam@gmail.com", 46,
                LocalDate.of(1903, 6, 25), LiteraryGenre.UTOPIA, null));
        book.setPublisher(new Publisher(1L, "Companhia das Letras", "CL", null, LocalDate.of(1986, 1, 1), null));

        //Act
        var result = bookMapper.convertToResponseDto(book);

        //Assert
        assertEquals(result.getId(), book.getId());
        assertEquals(result.getTitle(), book.getTitle());
        assertEquals(result.getIsbn(), book.getIsbn());
        assertEquals(result.getPages(), book.getPages());
        assertEquals(result.getReleaseDate(), book.getReleaseDate());
        assertEquals(result.getAuthor(), AuthorMapper.convertToResponseDto(book.getAuthor()));
        assertEquals(result.getPublisher(), PublisherMapper.convertToResponseDto(book.getPublisher()));
    }


}
