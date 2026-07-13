package com.masantello.bookstoremanager.utils;

public class NativeQueries {

    public static final String FIND_ALL_BOOKS_OF_AN_AUTHOR = """
        SELECT
            A.AUTHOR_NAME,
            B.*
        FROM
            BOOK   B
            LEFT JOIN AUTHOR A ON A.AUTHOR_ID = B.AUTHOR_ID
        WHERE
            UPPER(A.AUTHOR_NAME) LIKE UPPER('%' || :authorName || '%')
        """;

}
