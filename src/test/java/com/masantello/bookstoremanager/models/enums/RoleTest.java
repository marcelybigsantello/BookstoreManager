package com.masantello.bookstoremanager.models.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RoleTest {

    @Test
    @DisplayName("findByDescription - should return role when description exists ignoring case")
    void findByDescription_whenDescriptionExists_shouldReturnMatchingRole() {
        assertSame(Role.ADMIN, Role.findByDescription("admin"));
        assertSame(Role.COMMON, Role.findByDescription("COMMON"));
    }

    @Test
    @DisplayName("findByDescription - should return null when description does not exist")
    void findByDescription_whenDescriptionDoesNotExist_shouldReturnNull() {
        assertNull(Role.findByDescription("Manager"));
    }

    @Test
    @DisplayName("convertToDescription - should return description for a known role")
    void convertToDescription_whenRoleExists_shouldReturnItsDescription() {
        assertEquals("Admin", Role.convertToDescription(Role.ADMIN));
        assertEquals("Common", Role.convertToDescription(Role.COMMON));
    }

    @Test
    @DisplayName("convertToDescription - should return null when role is null")
    void convertToDescription_whenRoleIsNull_shouldReturnNull() {
        assertNull(Role.convertToDescription(null));
    }
}
