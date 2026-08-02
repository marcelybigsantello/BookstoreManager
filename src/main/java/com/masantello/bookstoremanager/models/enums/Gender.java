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

    public static Gender findByDescription(String description) {
        for (Gender gender : Gender.values()) {
            if (gender.getDescription().equalsIgnoreCase(description)) {
                return gender;
            }
        }
        return null;
    }

    public static String convertToDescription(Gender gender) {
        for (Gender gender1 : Gender.values()) {
            if (gender != null && gender1.getDescription().equalsIgnoreCase(gender.getDescription())) {
                return gender1.getDescription();
            }
        }
        return null;
    }
}
