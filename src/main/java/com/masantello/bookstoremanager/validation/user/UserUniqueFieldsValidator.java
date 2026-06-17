package com.masantello.bookstoremanager.validation.user;

import com.masantello.bookstoremanager.dtos.UserDto;
import com.masantello.bookstoremanager.exceptions.DataIntegrityViolationException;
import com.masantello.bookstoremanager.repositories.UserRepository;
import com.masantello.bookstoremanager.validation.AbstractValidator;
import jakarta.persistence.NoResultException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class UserUniqueFieldsValidator extends AbstractValidator<UserDto> {

    private static final Logger logger = LoggerFactory.getLogger(UserUniqueFieldsValidator.class);
    private final UserRepository userRepository;

    public UserUniqueFieldsValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDto validate(UserDto userDto) {

        logger.trace("Validating unique fields in user's creating request");

        var emailIsNotUnique = userRepository.findByEmail(userDto.getEmail()).isPresent();
        var passwordIsNotUnique = userRepository.findByPassword(userDto.getPassword()).isPresent();

        String errorMessage = "";

        if (emailIsNotUnique) {
            logger.error("This email '{}' is already associated with an account.", userDto.getEmail());
            errorMessage = String.format("This email '%s' is already associated with an account.",
                    userDto.getEmail());
            throw new DataIntegrityViolationException(errorMessage);
        }

        if (passwordIsNotUnique) {
            logger.info("This password '{}' is already being used. You should try another one.", userDto.getPassword());
            errorMessage = String.format("This password '%s' is already being used.", userDto.getPassword());
            throw new DataIntegrityViolationException(errorMessage);
        }

        return validateNext(userDto);
    }
}
