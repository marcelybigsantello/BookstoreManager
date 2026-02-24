package com.masantello.bookstoremanager.models;

import com.masantello.bookstoremanager.models.enums.LiteraryGenre;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "Author")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(columnDefinition = "integer default 0")
    private int age;

    @Column(name = "birthDate")
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "literaryGenre", nullable = false)
    private LiteraryGenre literaryGenre;

    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY) //Toda vez que se efetuar uma consulta de author, por padrão, não virá os dados de livro junto
    private List<Book> books;

}
