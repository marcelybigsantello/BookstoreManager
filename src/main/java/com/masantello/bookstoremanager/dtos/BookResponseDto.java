package com.masantello.bookstoremanager.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookResponseDto {

    private Long id;

    private String title;

    private String isbn;

    private Integer pages;

    private LocalDate releaseDate;

    private Long author;

    private Long publisher;
}
