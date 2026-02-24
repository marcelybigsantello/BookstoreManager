package com.masantello.bookstoremanager.models.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Gender {

    FEMALE(1, "Female"),
    MALE(2, "Male");

    private final int code;
    private final String description;
}
