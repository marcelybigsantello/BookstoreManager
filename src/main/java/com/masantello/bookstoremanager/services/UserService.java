package com.masantello.bookstoremanager.services;

import com.masantello.bookstoremanager.dtos.UserDto;
import com.masantello.bookstoremanager.mappers.UserMapper;
import com.masantello.bookstoremanager.models.User;
import com.masantello.bookstoremanager.models.enums.Gender;
import com.masantello.bookstoremanager.repositories.UserRepository;
import com.masantello.bookstoremanager.validation.AbstractValidator;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AbstractValidator<UserDto> createUserValidator;
    private final AbstractValidator<UserDto> updateUserValidator;

    public UserService(UserRepository userRepository,
                       UserMapper userMapper,
                       @Qualifier("createUserValidator")
                       AbstractValidator<UserDto> createUserValidator,
                       @Qualifier("updateUserValidator")
                       AbstractValidator<UserDto> updateUserValidator) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.createUserValidator = createUserValidator;
        this.updateUserValidator = updateUserValidator;
    }

    public UserDto create(UserDto userDto) {
        //Chain of Responsibilities
        createUserValidator.validate(userDto);

        var user = userMapper.convertToModel(userDto);
        user.setPassword(user.getPassword());
        user = userRepository.save(user);

        logger.info("User '{}' created successfully in Book Store Manager.", user.getUsername());
        return userMapper.convertToDto(user);
    }

    public List<UserDto> findAll() {
        var users = userRepository.findAll();
        return users.stream().map(userMapper::convertToDto).toList();
    }

    public List<UserDto> findByUsername(String userName) {
        var usersByUsername = userRepository.findByUsernameContainingIgnoreCase(userName);

        if (usersByUsername.isEmpty()) {
            logger.info("The given username {} was not found in database. You should try another one.", userName);
        }

        logger.info("{} user(s) containing the {} username were found in database.", usersByUsername.size(), userName);

        return usersByUsername.stream().map(userMapper::convertToDto).toList();
    }

    public void update(UserDto userDto) {
        //Chain of Responsibilities
        updateUserValidator.validate(userDto);

        var user = userMapper.convertToModel(userDto);
        user.setId(userDto.getId());
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        user.setGender(Gender.findByDescription(userDto.getGender()));
        user.setBirthDate(userDto.getBirthDate());
        user.setUsername(userDto.getUsername());
        user.setPassword(user.getPassword());

        userRepository.save(user);
        logger.info("User {}, Name={} updated successfully!", userDto.getUsername(), user.getName());
    }

    public void delete(Long userId) {
        var user = userRepository.findById(userId);

        user.ifPresentOrElse(user1 -> {
            logger.info("Deleting User {}, Name={} from the system.", user1.getUsername(), user1.getName());
            userRepository.delete(user1);
        }, () -> {
            var errorMessage = String.format("User %s was not found in database.", userId);
            throw new EntityNotFoundException(errorMessage);
        });

    }

    public User verifyAndGetUserIfExists(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(username));
    }
}
