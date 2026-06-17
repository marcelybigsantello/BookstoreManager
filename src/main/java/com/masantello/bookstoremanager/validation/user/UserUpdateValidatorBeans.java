package com.masantello.bookstoremanager.validation.user;

import com.masantello.bookstoremanager.dtos.UserDto;
import com.masantello.bookstoremanager.validation.AbstractValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserUpdateValidatorBeans {

    private final UserExistsValidator userExistsValidator;
    private final UserMandatoryFieldsValidator userMandatoryFieldsValidator;

    public UserUpdateValidatorBeans(UserExistsValidator userExistsValidator,
                                    UserMandatoryFieldsValidator userMandatoryFieldsValidator) {
        this.userExistsValidator = userExistsValidator;
        this.userMandatoryFieldsValidator = userMandatoryFieldsValidator;
    }

    @Bean("updateUserValidator")
    @Scope("prototype")
    public AbstractValidator<UserDto> abstractUserValidator() {
        return AbstractValidator.link(
                userExistsValidator,
                List.of(userMandatoryFieldsValidator)
        );
    }
}
