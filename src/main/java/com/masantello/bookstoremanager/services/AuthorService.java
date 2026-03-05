package com.masantello.bookstoremanager.services;

import com.masantello.bookstoremanager.dtos.AuthorDto;
import com.masantello.bookstoremanager.mappers.AuthorMapper;
import com.masantello.bookstoremanager.models.Author;
import com.masantello.bookstoremanager.repositories.AuthorRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthorService {

    private final AuthorMapper authorMapper;
    private final AuthorRepository authorRepository;

    public AuthorService(AuthorMapper authorMapper, AuthorRepository authorRepository) {
        this.authorMapper = authorMapper;
        this.authorRepository = authorRepository;
    }

    public AuthorDto create(AuthorDto authorDto) {
        Author author = authorMapper.convertToModel(authorDto);
        author = authorRepository.save(author);
        return authorMapper.convertToDto(author);
    }




}
