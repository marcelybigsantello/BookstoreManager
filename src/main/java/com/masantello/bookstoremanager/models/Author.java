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

    @Column(name = "NAME", nullable = false, unique = true)
    private String name;

    @Column(name = "EMAIL", nullable = false)
    private String email;

    @Column(name = "AGE", columnDefinition = "integer default 0")
    private Integer age;

    @Column(name = "BIRTH_DATE")
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "LITERARY_GENRE", nullable = false)
    private LiteraryGenre literaryGenre;

    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY) //Toda vez que se efetuar uma consulta de author, por padrão, não virá os dados de livro junto
    private List<Book> books;

}
