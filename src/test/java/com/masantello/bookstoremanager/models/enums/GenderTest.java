package com.masantello.bookstoremanager.models.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class GenderTest {

    @Test
    @DisplayName("findByDescription - should return gender when description exists ignoring case")
    void findByDescription_whenDescriptionExists_shouldReturnMatchingGender() {
        assertSame(Gender.MALE, Gender.findByDescription("male"));
        assertSame(Gender.FEMALE, Gender.findByDescription("FEMALE"));
    }

    @Test
    @DisplayName("findByDescription - should return null when description does not exist")
    void findByDescription_whenDescriptionDoesNotExist_shouldReturnNull() {
        assertNull(Gender.findByDescription("Other"));
    }

    @Test
    @DisplayName("convertToDescription - should return description for a known gender")
    void convertToDescription_whenGenderExists_shouldReturnItsDescription() {
        assertEquals("Male", Gender.convertToDescription(Gender.MALE));
        assertEquals("Female", Gender.convertToDescription(Gender.FEMALE));
    }

    @Test
    @DisplayName("convertToDescription - should return null when gender is null")
    void convertToDescription_whenGenderIsNull_shouldReturnNull() {
        assertNull(Gender.convertToDescription(null));
    }
}
