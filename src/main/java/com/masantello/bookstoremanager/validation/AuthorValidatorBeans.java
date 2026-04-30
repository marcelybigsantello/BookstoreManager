package com.masantello.bookstoremanager.validation;

import com.masantello.bookstoremanager.dtos.AuthorDto;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AuthorValidatorBeans {

    //Chain of Responsibilities

    private final AuthorAlreadyExistsValidator authorAlreadyExistsValidator;
    private final NullMandatoryFieldsValidator nullMandatoryFieldsValidator;

    public AuthorValidatorBeans(AuthorAlreadyExistsValidator authorAlreadyExistsValidator,
                                NullMandatoryFieldsValidator nullMandatoryFieldsValidator) {
        this.authorAlreadyExistsValidator = authorAlreadyExistsValidator;
        this.nullMandatoryFieldsValidator = nullMandatoryFieldsValidator;
    }

    @Bean("authorValidator")
    @Scope("prototype")
    public AbstractAuthorValidator<AuthorDto> abstractAuditAuthorValidator() {
        return AbstractAuthorValidator.link(
                authorAlreadyExistsValidator,
                List.of(nullMandatoryFieldsValidator)
        );
    }



}
