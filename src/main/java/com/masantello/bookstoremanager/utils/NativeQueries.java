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

    public static final String FIND_ALL_BOOKS_OF_A_PUBLISHER = """
            SELECT
                P.PUBLISHER_NAME,
                B.*
            FROM
                BOOK      B
                LEFT JOIN PUBLISHER P ON B.PUBLISHER_ID = P.PUBLISHER_ID
            WHERE
                UPPER(P.PUBLISHER_NAME) LIKE UPPER('%' || :publisherName || '%')
            """;
}
