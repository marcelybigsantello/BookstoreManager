package com.masantello.bookstoremanager.mappers;

import com.masantello.bookstoremanager.dtos.UserDto;
import com.masantello.bookstoremanager.models.User;
import com.masantello.bookstoremanager.models.enums.Gender;
import com.masantello.bookstoremanager.models.enums.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = UserMapper.class)
public class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    @DisplayName("convertToModel - deve converter 'UserDto' e retornar 'User'")
    void convertToModel_shouldConvertAndReturnUser() {
        //Arrange
        var userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("Maria Beatriz");
        userDto.setEmail("mariabeatriz@gmail.com");
        userDto.setGender("Feminino");
        userDto.setUsername("maria.beatriz");
        userDto.setPassword("123456");
        userDto.setRole("Admin");

        //Act
        var result = userMapper.convertToModel(userDto);

        //Assert
        assertEquals(result.getId(), userDto.getId());
        assertEquals(result.getName(), userDto.getName());
        assertEquals(result.getEmail(), userDto.getEmail());
        assertEquals(result.getGender(), Gender.findByDescription(userDto.getGender()));
        assertEquals(result.getUsername(), userDto.getUsername());
        assertEquals(result.getPassword(), userDto.getPassword());
        assertEquals(result.getRole(), Role.findByDescription(userDto.getRole()));
    }

    @Test
    @DisplayName("convertToModel - deve converter 'User' e retornar 'UserDto'")
    void convertToDto_shouldConvertAndReturnUserDto() {
        //Arrange
        var user = new User();
        user.setId(1L);
        user.setName("Maria Beatriz");
        user.setEmail("mariabeatriz@gmail.com");
        user.setGender(Gender.FEMALE);
        user.setUsername("maria.beatriz");
        user.setPassword("123456");
        user.setRole(Role.COMMON);

        //Act
        var result = userMapper.convertToDto(user);

        //Assert
        assertEquals(result.getId(), user.getId());
        assertEquals(result.getName(), user.getName());
        assertEquals(result.getEmail(), user.getEmail());
        assertEquals(result.getGender(), Gender.convertToDescription(user.getGender()));
        assertEquals(result.getUsername(), user.getUsername());
        assertEquals(result.getPassword(), user.getPassword());
        assertEquals(result.getRole(), Role.convertToDescription(user.getRole()));
    }


}
