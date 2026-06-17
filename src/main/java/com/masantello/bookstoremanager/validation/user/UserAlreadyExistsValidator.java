package com.masantello.bookstoremanager.validation.user;

import com.masantello.bookstoremanager.dtos.UserDto;
import com.masantello.bookstoremanager.repositories.UserRepository;
import com.masantello.bookstoremanager.validation.AbstractValidator;
import jakarta.persistence.EntityExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class UserAlreadyExistsValidator extends AbstractValidator<UserDto> {

    private static final Logger logger = LoggerFactory.getLogger(UserAlreadyExistsValidator.class);

    private final UserRepository userRepository;

    public UserAlreadyExistsValidator(UserRepository userRepository) { this.userRepository = userRepository; }

    @Override
    public UserDto validate(UserDto userDto) {

        if (!userRepository.findByUsernameContainingIgnoreCase(userDto.getUsername()).isEmpty()) {
            logger.error("This username '{}' already exists. You should try another one.", userDto.getUsername());
            var errorMessage = String.format("This username %s already exists. ", userDto.getUsername());
            throw new EntityExistsException(errorMessage);
        }

        return validateNext(userDto);
    }
}
