package com.masantello.bookstoremanager.validation.user;

import com.masantello.bookstoremanager.dtos.UserDto;
import com.masantello.bookstoremanager.validation.AbstractValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class UserCreateValidatorBeans {

    private final UserAlreadyExistsValidator userAlreadyExistsValidator;
    private final UserMandatoryFieldsValidator userMandatoryFieldsValidator;
    private final UserUniqueFieldsValidator userUniqueFieldsValidator;

    public UserCreateValidatorBeans(UserAlreadyExistsValidator userAlreadyExistsValidator,
                                    UserMandatoryFieldsValidator userMandatoryFieldsValidator,
                                    UserUniqueFieldsValidator userPasswordAlreadyRegisteredValidator) {
        this.userAlreadyExistsValidator = userAlreadyExistsValidator;
        this.userMandatoryFieldsValidator = userMandatoryFieldsValidator;
        this.userUniqueFieldsValidator = userPasswordAlreadyRegisteredValidator;
    }

    @Bean("createUserValidator")
    @Scope("prototype")
    public AbstractValidator<UserDto> abstractAuditUserValidator() {
        return AbstractValidator.link(
                userAlreadyExistsValidator,
                Arrays.asList(userMandatoryFieldsValidator, userUniqueFieldsValidator)
        );
    }
}
