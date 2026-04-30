package com.masantello.bookstoremanager.validation;

import com.masantello.bookstoremanager.dtos.AuthorDto;
import com.masantello.bookstoremanager.exceptions.ObjectExistsException;
import com.masantello.bookstoremanager.repositories.AuthorRepository;
import org.springframework.stereotype.Component;

@Component
public class AuthorAlreadyExistsValidator extends AbstractAuthorValidator<AuthorDto> {

    private final AuthorRepository authorRepository;

    public AuthorAlreadyExistsValidator(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @Override
    public AuthorDto validate(AuthorDto authorDto) {
        if (authorRepository.findByNameContainingIgnoreCase(authorDto.getName()).isPresent()) {
            throw new ObjectExistsException("This author is already registered.");
        }

        return validateNext(authorDto);
    }
}
