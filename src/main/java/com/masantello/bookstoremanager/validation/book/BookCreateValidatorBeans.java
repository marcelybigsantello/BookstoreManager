package com.masantello.bookstoremanager.validation.book;

import com.masantello.bookstoremanager.dtos.BookDto;
import com.masantello.bookstoremanager.validation.AbstractValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BookCreateValidatorBeans {

    private final BookAlreadyExistsValidator bookAlreadyExistsValidator;
    private final ReleaseDateAndPagesNumberValidator releaseDateAndPagesNumberValidator;

    public BookCreateValidatorBeans(BookAlreadyExistsValidator bookAlreadyExistsValidator,
                                    ReleaseDateAndPagesNumberValidator releaseDateAndPagesNumberValidator) {
        this.bookAlreadyExistsValidator = bookAlreadyExistsValidator;
        this.releaseDateAndPagesNumberValidator = releaseDateAndPagesNumberValidator;
    }

    @Bean("bookCreateValidator")
    @Scope("prototype")
    public AbstractValidator<BookDto> abstractAuditBookValidator() {
        return AbstractValidator.link(
                bookAlreadyExistsValidator,
                List.of(releaseDateAndPagesNumberValidator));
    }
}
