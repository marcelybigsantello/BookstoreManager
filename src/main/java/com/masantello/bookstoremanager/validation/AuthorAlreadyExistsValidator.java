package com.masantello.bookstoremanager.validation;

import com.masantello.bookstoremanager.dtos.AuthorDto;
import com.masantello.bookstoremanager.repositories.AuthorRepository;
import jakarta.persistence.EntityExistsException;
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
            var errorMessage = String.format("This author %s is already registered.", authorDto.getName());
            throw new EntityExistsException(errorMessage);
        }

        return validateNext(authorDto);
    }
}
