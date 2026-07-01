package com.masantello.bookstoremanager.dtos;

import com.masantello.bookstoremanager.models.enums.Role;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private Long id;

    private String name;

    private String email;

    private LocalDate birthDate;

    private String gender;

    private String username;

    private String password;

    private String role;

}
