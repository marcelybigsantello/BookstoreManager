package com.masantello.bookstoremanager.validation.user;

import com.masantello.bookstoremanager.dtos.UserDto;
import com.masantello.bookstoremanager.validation.AbstractValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserCreateValidatorBeans {

    private final UserAlreadyExistsValidator userAlreadyExistsValidator;
    private final UserMandatoryFieldsValidator userMandatoryFieldsValidator;
    private final UserUniqueFieldsValidator userPasswordAlreadyRegisteredValidator;

    public UserCreateValidatorBeans(UserAlreadyExistsValidator userAlreadyExistsValidator,
                                    UserMandatoryFieldsValidator userMandatoryFieldsValidator,
                                    UserUniqueFieldsValidator userPasswordAlreadyRegisteredValidator) {
        this.userAlreadyExistsValidator = userAlreadyExistsValidator;
        this.userMandatoryFieldsValidator = userMandatoryFieldsValidator;
        this.userPasswordAlreadyRegisteredValidator = userPasswordAlreadyRegisteredValidator;
    }

    @Bean("createUserValidator")
    public AbstractValidator<UserDto> abstractAuditUserValidator() {
        return AbstractValidator.link(
                userAlreadyExistsValidator,
                List.of(userMandatoryFieldsValidator,
                        userPasswordAlreadyRegisteredValidator)
        );
    }
}
