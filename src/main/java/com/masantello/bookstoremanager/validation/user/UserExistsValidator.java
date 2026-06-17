package com.masantello.bookstoremanager.validation.user;

import com.masantello.bookstoremanager.dtos.UserDto;
import com.masantello.bookstoremanager.repositories.UserRepository;
import com.masantello.bookstoremanager.validation.AbstractValidator;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class UserExistsValidator extends AbstractValidator<UserDto> {

    private static final Logger logger = LoggerFactory.getLogger(UserExistsValidator.class);

    private static final String USER_NOT_FOUND = "User{id=%s, name=%s, username=%s} not found in database";
    private final UserRepository userRepository;

    public UserExistsValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDto validate(UserDto userDto) {

        if (userRepository.findById(userDto.getId()).isEmpty()) {
            var errorMessage = String.format(USER_NOT_FOUND, userDto.getId(), userDto.getName(), userDto.getUsername());
            throw new EntityNotFoundException(errorMessage);
        }
        logger.info("User ID={}, Username='{}' found in database", userDto.getId(), userDto.getUsername());

        return validateNext(userDto);
    }
}
