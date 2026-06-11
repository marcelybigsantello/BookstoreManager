package com.masantello.bookstoremanager.validation.author;

import com.masantello.bookstoremanager.dtos.AuthorDto;
import com.masantello.bookstoremanager.validation.AbstractValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AuthorDeleteValidatorBeans {

    private final AuthorExistsValidator authorExistsValidator;
    private final AuthorHasBooksValidator authorHasBooksValidator;

    public AuthorDeleteValidatorBeans(AuthorExistsValidator authorExistsValidator,
                                      AuthorHasBooksValidator authorHasBooksValidator) {
        this.authorExistsValidator = authorExistsValidator;
        this.authorHasBooksValidator = authorHasBooksValidator;
    }

    @Bean("authorDeleteValidator")
    @Scope("prototype")
    public AbstractValidator<AuthorDto> abstractAuditAuthorValidator() {
        return AbstractValidator.link(
                authorExistsValidator,
                List.of(authorHasBooksValidator)
        );
    }

}
