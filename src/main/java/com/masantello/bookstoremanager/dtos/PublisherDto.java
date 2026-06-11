package com.masantello.bookstoremanager.dtos;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PublisherDto {

    private Long id;

    @Size(max = 50)
    private String name;

    @Size(max = 50)
    private String code;

    private String description;

    private LocalDate foundationDate;

}
