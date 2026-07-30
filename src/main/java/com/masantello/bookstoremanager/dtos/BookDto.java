package com.masantello.bookstoremanager.dtos;

import com.masantello.bookstoremanager.models.Author;
import com.masantello.bookstoremanager.models.Publisher;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.ISBN;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookDto {

    private Long id;

    @Size(max = 255)
    private String title;

    @ISBN
    private String isbn;

    private Integer pages;

    private LocalDate releaseDate;

    @NotNull
    private Author author;

    @NotNull
    private Publisher publisher;

}
