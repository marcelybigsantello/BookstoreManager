package com.masantello.bookstoremanager.models;

import com.masantello.bookstoremanager.models.enums.LiteraryGenre;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "AUTHOR")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "author_sequence")
    @SequenceGenerator(name = "author_sequence", sequenceName = "S_AUTHOR", allocationSize = 1)
    @Column(name = "AUTHOR_ID")
    private Long id;

    @Column(name = "AUTHOR_NAME", nullable = false, unique = true)
    private String name;

    @Column(name = "AUTHOR_EMAIL", nullable = false)
    private String email;

    @Column(name = "AUTHOR_AGE", columnDefinition = "integer default 0")
    private Integer age;

    @Column(name = "BIRTH_DATE")
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "LITERARY_GENRE", nullable = false)
    private LiteraryGenre literaryGenre;

    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY) //Toda vez que se efetuar uma consulta de author, por padrão, não virá os dados de livros junto
    private List<Book> books;

}
