package com.masantello.bookstoremanager.services;

import com.masantello.bookstoremanager.dtos.AuthorDto;
import com.masantello.bookstoremanager.exceptions.DataIntegrityViolationException;
import com.masantello.bookstoremanager.mappers.AuthorMapper;
import com.masantello.bookstoremanager.models.Author;
import com.masantello.bookstoremanager.models.enums.LiteraryGenre;
import com.masantello.bookstoremanager.repositories.AuthorRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthorService {

    private static final Logger logger = LoggerFactory.getLogger(AuthorService.class);

    private final AuthorMapper authorMapper;
    private final AuthorRepository authorRepository;

    public AuthorService(AuthorMapper authorMapper, AuthorRepository authorRepository) {
        this.authorMapper = authorMapper;
        this.authorRepository = authorRepository;
    }

    public AuthorDto create(AuthorDto authorDto) {
        Author author = authorMapper.convertToModel(authorDto);
        author = authorRepository.save(author);

        logger.info("Author Id={}, Name={} created successfully.", author.getId(), author.getName());
        return authorMapper.convertToDto(author);
    }

    public List<AuthorDto> findAll() {
        var authors = authorRepository.findAll();

        return authors.stream().map(authorMapper::convertToDto).collect(Collectors.toList());
    }

    public AuthorDto findByName(String authorName) {

        var author = authorRepository.findByNameContaining(authorName);
        if (author.isEmpty()) {
            logger.error("Author {} does not exist in database. You should try another one.", authorName);
            throw new EntityNotFoundException("Author " +authorName+ " was not found in database.");
        }

        logger.info("Author {} found in database", authorName);
        return authorMapper.convertToDto(author.get());
    }

    public AuthorDto updateByName(AuthorDto authorDto) {
        Author newAuthorData = authorMapper.convertToModel(findByName(authorDto.getName()));

        newAuthorData.setName(authorDto.getName());
        newAuthorData.setEmail(authorDto.getEmail());
        newAuthorData.setAge(authorDto.getAge());
        newAuthorData.setBirthDate(authorDto.getBirthDate());
        newAuthorData.setLiteraryGenre(LiteraryGenre.findByDescription(authorDto.getLiteraryGenre()));

        authorRepository.save(newAuthorData);
        logger.info("Author {} updated successfully.", authorDto.getName());
        return authorMapper.convertToDto(newAuthorData);
    }

    public void delete(String authorName) {
        var authorDto = findByName(authorName);

        if (authorDto.getBooks() != null && !authorDto.getBooks().isEmpty()) {
            throw new DataIntegrityViolationException("Author " +authorName+ " has some books registered. "
                    + "It is not possible to delete it.");
        }

        authorRepository.delete(authorMapper.convertToModel(authorDto));
    }
}
