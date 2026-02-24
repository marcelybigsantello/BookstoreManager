package com.masantello.bookstoremanager.models.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LiteraryGenre {

    ACTION(1, "Action"),
    ADVENTURE(2,"Adventure"),
    COMEDY(3,"Comedy"),
    DRAMA(4,"Drama"),
    FANTASY(5,"Fantasy"),
    HORROR(6,"Horror"),
    MYSTERY(7,"Mystery"),
    ROMANCE(8,"Romance"),
    SCIENCE_FICTION(9,"Science Fiction"),
    THRILLER(10,"Thriller");

    private final int code;
    private final String description;

    public static LiteraryGenre findByCode(int code) {
        for (LiteraryGenre gender : LiteraryGenre.values()) {
            if (gender.getCode() == code) {
                return gender;
            }
        }
        return null;
    }

    public static LiteraryGenre findByDescription(String description) {
        for (LiteraryGenre gender : LiteraryGenre.values()) {
            if (gender.getDescription().equalsIgnoreCase(description)) {
                return gender;
            }
        }
        return null;
    }

}
