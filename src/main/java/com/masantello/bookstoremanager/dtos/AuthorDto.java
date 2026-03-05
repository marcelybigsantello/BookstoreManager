package com.masantello.bookstoremanager.dtos;

import com.masantello.bookstoremanager.models.Book;
import com.masantello.bookstoremanager.models.enums.LiteraryGenre;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthorDto {

    private Long id;

    @NotNull
    @NotEmpty
    @Size(max = 255)
    private String name;

    private String email;

    private Integer age;

    @NotNull
    private LocalDate birthDate;

    @NotNull
    @NotEmpty
    private String literaryGenre;

    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY) //Toda vez que se efetuar uma consulta de author, por padrão, não virá os dados de livro junto
    private List<Book> books;
}
