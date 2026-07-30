package com.masantello.bookstoremanager.mappers;

import com.masantello.bookstoremanager.dtos.AuthorDto;
import com.masantello.bookstoremanager.dtos.AuthorResponseDto;
import com.masantello.bookstoremanager.models.Author;
import com.masantello.bookstoremanager.models.enums.LiteraryGenre;
import org.springframework.stereotype.Component;

@Component
public class AuthorMapper {

    public Author convertToModel(AuthorDto authorDto) {
        var author = new Author();
        author.setId(authorDto.getId());
        author.setName(authorDto.getName());
        author.setEmail(authorDto.getEmail());
        author.setAge(authorDto.getAge());
        author.setBirthDate(authorDto.getBirthDate());
        author.setLiteraryGenre(LiteraryGenre.findByDescription(authorDto.getLiteraryGenre()));
        return author;
    }

    public AuthorDto convertToDto(Author author) {
        var authorDto = new AuthorDto();
        authorDto.setId(author.getId());
        authorDto.setName(author.getName());
        authorDto.setEmail(author.getEmail());
        authorDto.setAge(author.getAge());
        authorDto.setBirthDate(author.getBirthDate());
        authorDto.setLiteraryGenre(LiteraryGenre.convertToDescription(author.getLiteraryGenre()));
        return authorDto;
    }

    public static AuthorResponseDto convertToResponseDto(Author author) {
        var authorResponseDto = new AuthorResponseDto();
        authorResponseDto.setName(author.getName());
        authorResponseDto.setLiteraryGenre(LiteraryGenre.convertToDescription(author.getLiteraryGenre()));
        return authorResponseDto;
    }
}
