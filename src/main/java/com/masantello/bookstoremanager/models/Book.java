package com.masantello.bookstoremanager.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "BOOK")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "book_sequence")
    @SequenceGenerator(name = "book_sequence", sequenceName = "S_BOOK", allocationSize = 1)
    @Column(name = "BOOK_ID")
    private Long id;

    @Column(name = "TITLE", nullable = false)
    private String title;

    @Column(name = "ISBN", nullable = false)
    private String isbn;

    @Column(name = "PAGES_NUMBER", columnDefinition = "integer default 0")
    private int pages;

    @Column(name = "RELEASE_DATE", columnDefinition = "TIMESTAMP")
    private LocalDate releaseDate;

    @ManyToOne(cascade = {CascadeType.MERGE})
    @JoinColumn(name = "AUTHOR_ID")
    private Author author;

    @ManyToOne(cascade = {CascadeType.MERGE})
    @JoinColumn(name = "PUBLISHER_ID")
    private Publisher publisher;

    @ManyToOne(cascade = {CascadeType.MERGE})
    @JoinColumn(name = "USER_ID")
    private User user;
}
