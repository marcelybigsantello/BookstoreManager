package com.masantello.bookstoremanager.mappers;

import com.masantello.bookstoremanager.dtos.UserDto;
import com.masantello.bookstoremanager.models.User;
import com.masantello.bookstoremanager.models.enums.Gender;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User convertToModel(UserDto userDto) {
        User user = new User();
        user.setId(userDto.getId());
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        user.setGender(Gender.findByDescription(userDto.getGender()));
        user.setBirthDate(userDto.getBirthDate());
        user.setUsername(userDto.getUsername());
        user.setPassword(userDto.getPassword());

        return user;
    }

    public UserDto convertToDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
        userDto.setGender(Gender.convertToDescription(user.getGender()));
        userDto.setBirthDate(user.getBirthDate());
        userDto.setUsername(user.getUsername());
        userDto.setPassword(user.getPassword());

        return userDto;
    }
}
