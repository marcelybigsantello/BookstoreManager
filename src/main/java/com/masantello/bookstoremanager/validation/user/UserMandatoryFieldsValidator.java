package com.masantello.bookstoremanager.validation.user;

import com.masantello.bookstoremanager.dtos.UserDto;
import com.masantello.bookstoremanager.exceptions.MissingMandatoryFieldsException;
import com.masantello.bookstoremanager.validation.AbstractValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class UserMandatoryFieldsValidator extends AbstractValidator<UserDto> {

    private static final Logger logger = LoggerFactory.getLogger(UserMandatoryFieldsValidator.class);
    private static final String NULL_MANDATORY_FIELDS = "Campos obrigatórios nulos";

    @Override
    public UserDto validate(UserDto userDto) {

        var doesNotHaveName = userDto.getName() == null;
        var doesNotHaveEmail = userDto.getEmail() == null;
        var doesNotHaveUsername = userDto.getUsername() == null;
        var doesNotHavePassword = userDto.getPassword() == null;

        if (doesNotHaveName || doesNotHaveEmail || doesNotHaveUsername || doesNotHavePassword) {
            logger.error("There are missing mandatory fields in publisher creation request. "
                    + "User={}", userDto);
            throw new MissingMandatoryFieldsException(NULL_MANDATORY_FIELDS);
        }

        return validateNext(userDto);
    }
}
