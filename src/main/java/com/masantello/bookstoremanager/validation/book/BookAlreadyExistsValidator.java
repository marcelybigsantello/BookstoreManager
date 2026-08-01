package com.masantello.bookstoremanager.validation.book;

import com.masantello.bookstoremanager.dtos.BookDto;
import com.masantello.bookstoremanager.repositories.BookRepository;
import com.masantello.bookstoremanager.validation.AbstractValidator;
import jakarta.persistence.EntityExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class BookAlreadyExistsValidator extends AbstractValidator<BookDto> {

    private static final Logger logger = LoggerFactory.getLogger(BookAlreadyExistsValidator.class);

    private final BookRepository bookRepository;

    public BookAlreadyExistsValidator(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public BookDto validate(BookDto bookDto) {

        if (bookRepository.findById(bookDto.getId()).isPresent()) {
            var errorMessage = String.format("This book ID='%s', title='%s', ISBN='%s' already exists.",
                    bookDto.getId(),
                    bookDto.getTitle(),
                    bookDto.getIsbn());
            logger.error(errorMessage);
            throw new EntityExistsException(errorMessage);
        }

        return validateNext(bookDto);
    }
}
