package com.masantello.bookstoremanager.validation.author;

import com.masantello.bookstoremanager.dtos.AuthorDto;
import com.masantello.bookstoremanager.repositories.AuthorRepository;
import com.masantello.bookstoremanager.validation.AbstractValidator;
import jakarta.persistence.EntityExistsException;
import org.springframework.stereotype.Component;

@Component
public class AuthorAlreadyExistsValidator extends AbstractValidator<AuthorDto> {

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
