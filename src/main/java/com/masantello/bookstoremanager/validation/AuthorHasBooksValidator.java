package com.masantello.bookstoremanager.validation;

import com.masantello.bookstoremanager.dtos.AuthorDto;
import com.masantello.bookstoremanager.exceptions.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

@Component
public class AuthorHasBooksValidator extends AbstractAuthorValidator<AuthorDto> {


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
