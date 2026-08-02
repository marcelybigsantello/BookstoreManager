package com.masantello.bookstoremanager.validation.user;

import com.masantello.bookstoremanager.dtos.UserDto;
import com.masantello.bookstoremanager.models.User;
import com.masantello.bookstoremanager.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = UserExistsValidator.class)
public class UserExistsValidatorTest {

    @Autowired
    private UserExistsValidator userExistsValidator;

    @MockitoBean
    private UserRepository userRepository;

    private UserDto userDto;

    @BeforeEach
    void setup() {
        userDto = new UserDto();
        userDto.setId(10L);
        userDto.setName("Heloísa Carvalho");
        userDto.setBirthDate(LocalDate.of(2003, 7, 22));
        userDto.setGender("Female");
        userDto.setUsername("heloisa.carvalho");
        userDto.setPassword("54321");
        userDto.setRole("Common");
    }

    @Test
    @DisplayName("validate - deve encontrar o usuário na base e retornar o próprio UserDto")
    void validate_shouldFindUserAndReturnUserDto() {
        // Arrange
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));

        // Act
        var result = userExistsValidator.validate(userDto);

        //Assert
        assertNotNull(result);
        assertThat(userDto.getId()).isEqualTo(result.getId());
        assertThat(userDto.getName()).isEqualTo(result.getName());
        assertThat(userDto.getBirthDate()).isEqualTo(result.getBirthDate());
        assertThat(userDto.getUsername()).isEqualTo(result.getUsername());
        assertThat(userDto.getPassword()).isEqualTo(result.getPassword());
        assertThat(userDto.getRole()).isEqualTo(result.getRole());
        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    @DisplayName("validate - deve não encontrar o usuário a ser alterado e retornar EntityNotFoundException")
    void validate_shouldNotFindUserAndThrowsEntityNotFoundException() {
        // Arrange
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & assert
        assertThrows(EntityNotFoundException.class, () -> userExistsValidator.validate(userDto),
                "Deve lançar EntityNotFoundException se usuário a ser alterado não foi encontrado.");
    }
}
