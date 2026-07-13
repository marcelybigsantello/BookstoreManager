package com.masantello.bookstoremanager.validation.author;

import com.masantello.bookstoremanager.dtos.AuthorDto;
import com.masantello.bookstoremanager.repositories.AuthorRepository;
import com.masantello.bookstoremanager.validation.AbstractValidator;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class AuthorExistsValidator extends AbstractValidator<AuthorDto> {

    private static final Logger logger = LoggerFactory.getLogger(AuthorExistsValidator.class);

    private static final String AUTHOR_NOT_FOUND = "Author '%s' not found in database";
    private final AuthorRepository authorRepository;

    public AuthorExistsValidator(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @Override
    public AuthorDto validate(AuthorDto authorDto) {

        if (authorRepository.findById(authorDto.getId()).isEmpty()) {
            var errorMessage = String.format(AUTHOR_NOT_FOUND, authorDto.getName());
            throw new EntityNotFoundException(errorMessage);
        }
        logger.info("Author ID={}, Name='{}' found in database", authorDto.getId(), authorDto.getName());
        return validateNext(authorDto);
    }
}
