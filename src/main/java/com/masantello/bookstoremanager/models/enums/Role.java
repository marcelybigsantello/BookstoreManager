package com.masantello.bookstoremanager.models.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Role {

    ADMIN(1, "Admin"),
    COMMON(2, "Common");

    private final int code;
    private final String description;

    public static Role findByDescription(String description) {
        for (Role role : Role.values()) {
            if (role.getDescription().equalsIgnoreCase(description)) {
                return role;
            }
        }
        return null;
    }

    public static String convertToDescription(Role role) {
        for (Role selectedRole : Role.values()) {
            if (role != null && selectedRole.getDescription().equalsIgnoreCase(role.getDescription())) {
                return selectedRole.getDescription();
            }
        }
        return null;
    }
}
