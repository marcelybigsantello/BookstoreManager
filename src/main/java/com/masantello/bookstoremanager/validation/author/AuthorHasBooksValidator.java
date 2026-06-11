package com.masantello.bookstoremanager.validation.author;

import com.masantello.bookstoremanager.dtos.AuthorDto;
import com.masantello.bookstoremanager.exceptions.DataIntegrityViolationException;
import com.masantello.bookstoremanager.validation.AbstractValidator;
import org.springframework.stereotype.Component;

@Component
public class AuthorHasBooksValidator extends AbstractValidator<AuthorDto> {


    @Override
    public AuthorDto validate(AuthorDto authorDto) {

        if (authorDto.getBooks() != null && !authorDto.getBooks().isEmpty()) {
            var errorMessage = String.format("Author %s has some books registered. It is not possible to delete it",
                    authorDto.getName());
            throw new DataIntegrityViolationException(errorMessage);
        }

        return validateNext(authorDto);
    }
}
