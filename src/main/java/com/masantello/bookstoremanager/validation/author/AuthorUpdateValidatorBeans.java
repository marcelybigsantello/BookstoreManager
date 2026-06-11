package com.masantello.bookstoremanager.validation.author;

import com.masantello.bookstoremanager.dtos.AuthorDto;
import com.masantello.bookstoremanager.validation.AbstractValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AuthorUpdateValidatorBeans {

    private final AuthorExistsValidator authorExistsValidator;
    private final AuthorMandatoryFieldsValidator nullMandatoryFieldsValidator;

    public AuthorUpdateValidatorBeans(AuthorExistsValidator authorExistsValidator,
                                      AuthorMandatoryFieldsValidator nullMandatoryFieldsValidator) {
        this.authorExistsValidator = authorExistsValidator;
        this.nullMandatoryFieldsValidator = nullMandatoryFieldsValidator;
    }

    @Bean("authorUpdateValidator")
    @Scope("prototype")
    public AbstractValidator<AuthorDto> abstractAuditAuthorValidator() {
        return AbstractValidator.link(
                authorExistsValidator,
                List.of(nullMandatoryFieldsValidator)
        );
    }



}
