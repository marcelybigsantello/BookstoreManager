package com.masantello.bookstoremanager.dtos;

import com.masantello.bookstoremanager.models.Book;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthorDto {

    private Long id;

    @Size(max = 255)
    private String name;

    private String email;

    private Integer age;

    private LocalDate birthDate;

    private String literaryGenre;

    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY) //Toda vez que se efetuar uma consulta de author, por padrão, não virá os dados de livro junto
    private List<Book> books;
}
