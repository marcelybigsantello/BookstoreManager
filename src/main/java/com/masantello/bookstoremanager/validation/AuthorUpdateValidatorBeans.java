package com.masantello.bookstoremanager.validation;

import com.masantello.bookstoremanager.dtos.AuthorDto;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AuthorUpdateValidatorBeans {

    private final AuthorExistsValidator authorExistsValidator;
    private final NullMandatoryFieldsValidator nullMandatoryFieldsValidator;

    public AuthorUpdateValidatorBeans(AuthorExistsValidator authorExistsValidator,
                                      NullMandatoryFieldsValidator nullMandatoryFieldsValidator) {
        this.authorExistsValidator = authorExistsValidator;
        this.nullMandatoryFieldsValidator = nullMandatoryFieldsValidator;
    }

    @Bean("authorUpdateValidator")
    @Scope("prototype")
    public AbstractAuthorValidator<AuthorDto> abstractAuditAuthorValidator() {
        return AbstractAuthorValidator.link(
                authorExistsValidator,
                List.of(nullMandatoryFieldsValidator)
        );
    }



}
