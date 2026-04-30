package com.masantello.bookstoremanager.services;

import com.masantello.bookstoremanager.dtos.AuthorDto;
import com.masantello.bookstoremanager.exceptions.DataIntegrityViolationException;
import com.masantello.bookstoremanager.mappers.AuthorMapper;
import com.masantello.bookstoremanager.models.Author;
import com.masantello.bookstoremanager.models.enums.LiteraryGenre;
import com.masantello.bookstoremanager.repositories.AuthorRepository;
import com.masantello.bookstoremanager.validation.AbstractAuthorValidator;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AuthorService {

    private static final Logger logger = LoggerFactory.getLogger(AuthorService.class);

    private final AuthorMapper authorMapper;
    private final AbstractAuthorValidator<AuthorDto> validatorCreate;
    private final AbstractAuthorValidator<AuthorDto> validatorUpdate;
    private final AbstractAuthorValidator<AuthorDto> validatorDelete;
    private final AuthorRepository authorRepository;

    public AuthorService(AuthorMapper authorMapper,
                         @Qualifier("authorCreateValidator")
                         AbstractAuthorValidator<AuthorDto> validatorCreate,
                         @Qualifier("authorUpdateValidator")
                         AbstractAuthorValidator<AuthorDto> validatorUpdate,
                         @Qualifier("authorDeleteValidator")
                         AbstractAuthorValidator<AuthorDto> validatorDelete,
                         AuthorRepository authorRepository) {
        this.authorMapper = authorMapper;
        this.validatorCreate = validatorCreate;
        this.validatorUpdate = validatorUpdate;
        this.validatorDelete = validatorDelete;
        this.authorRepository = authorRepository;
    }

    public AuthorDto create(AuthorDto authorDto) {
        logger.debug("Validating request's information");
        validatorCreate.validate(authorDto);

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

        var author = authorRepository.findByNameContainingIgnoreCase(authorName);
        if (author.isEmpty()) {
            logger.error("Author {} does not exist in database. You should try another one.", authorName);
            throw new EntityNotFoundException("Author " +authorName+ " was not found in database.");
        }

        logger.info("Author {} found in database", authorName);
        return authorMapper.convertToDto(author.get());
    }


    public AuthorDto updateById(AuthorDto authorDto) {
        validatorUpdate.validate(authorDto);

        Author newAuthorData = authorMapper.convertToModel(authorDto);

        newAuthorData.setName(authorDto.getName());
        newAuthorData.setEmail(authorDto.getEmail());
        newAuthorData.setAge(authorDto.getAge());
        newAuthorData.setBirthDate(authorDto.getBirthDate());
        newAuthorData.setLiteraryGenre(LiteraryGenre.findByDescription(authorDto.getLiteraryGenre()));

        authorRepository.save(newAuthorData);
        logger.info("Author {} updated successfully.", authorDto.getName());
        return authorMapper.convertToDto(newAuthorData);
    }

    public void delete(Long authorId) {
        var author = authorRepository.findById(authorId);

        author.ifPresentOrElse(author1 -> {
            validatorDelete.validate(authorMapper.convertToDto(author1));
            authorRepository.delete(author1);
        }, () -> {
            var errorMessage = String.format("Author Id={%s} not found.", authorId);
            throw new EntityNotFoundException(errorMessage);
        });

    }
}
