package com.masantello.bookstoremanager.validation.author;

import com.masantello.bookstoremanager.dtos.AuthorDto;
import com.masantello.bookstoremanager.validation.AbstractValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AuthorCreateValidatorBeans {

    private final AuthorAlreadyExistsValidator authorAlreadyExistsValidator;
    private final AuthorMandatoryFieldsValidator nullMandatoryFieldsValidator;

    public AuthorCreateValidatorBeans(AuthorAlreadyExistsValidator authorAlreadyExistsValidator,
                                      AuthorMandatoryFieldsValidator nullMandatoryFieldsValidator) {
        this.authorAlreadyExistsValidator = authorAlreadyExistsValidator;
        this.nullMandatoryFieldsValidator = nullMandatoryFieldsValidator;
    }

    @Bean("authorCreateValidator")
    @Scope("prototype")
    public AbstractValidator<AuthorDto> abstractAuditAuthorValidator() {
        return AbstractValidator.link(
                authorAlreadyExistsValidator,
                List.of(nullMandatoryFieldsValidator)
        );
    }

}
