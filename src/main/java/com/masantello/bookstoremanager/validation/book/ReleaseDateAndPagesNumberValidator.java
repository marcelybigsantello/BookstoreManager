package com.masantello.bookstoremanager.validation.book;

import com.masantello.bookstoremanager.dtos.BookDto;
import com.masantello.bookstoremanager.exceptions.DataIntegrityViolationException;
import com.masantello.bookstoremanager.validation.AbstractValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class ReleaseDateAndPagesNumberValidator extends AbstractValidator<BookDto> {

    private static final Logger logger = LoggerFactory.getLogger(ReleaseDateAndPagesNumberValidator.class);

    @Override
    public BookDto validate(BookDto bookDto) {

        if (bookDto.getReleaseDate().isAfter(LocalDate.now())
                || bookDto.getReleaseDate().isBefore(LocalDate.of(1100, 1, 1))) {
            var errorMessage = String.format("Release date of book '%s' is invalid: '%s'", bookDto.getTitle(),
                    bookDto.getReleaseDate());
            logger.error(errorMessage);
            throw new DataIntegrityViolationException(errorMessage);
        }

        if (bookDto.getPages() <= 0) {
            var errorMessage = String.format("Book '%s' with invalid number of pages: %s", bookDto.getTitle(), bookDto.getPages());
            logger.error(errorMessage);
            throw new DataIntegrityViolationException(errorMessage);
        }

        return validateNext(bookDto);
    }
}
