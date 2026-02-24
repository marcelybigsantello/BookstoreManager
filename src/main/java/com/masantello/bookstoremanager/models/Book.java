package com.masantello.bookstoremanager.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "Book")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "ISBN", nullable = false)
    private String isbn;

    @Column(name = "pages", columnDefinition = "integer default 0")
    private int pages;

    @Column(name = "chapters", columnDefinition = "integer default 0")
    private int chapters;

    @Column(name = "releaseDate", columnDefinition = "TIMESTAMP")
    private LocalDate releaseDate;

    @ManyToOne(cascade = {CascadeType.MERGE})
    private Author author;

    @ManyToOne(cascade = {CascadeType.MERGE})
    private Publisher publisher;

    @ManyToOne(cascade = {CascadeType.MERGE})
    private Users user;
}
