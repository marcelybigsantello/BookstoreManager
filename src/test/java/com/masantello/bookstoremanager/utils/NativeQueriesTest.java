package com.masantello.bookstoremanager.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = NativeQueries.class)
public class NativeQueriesTest {

    @Test
    @DisplayName("deve chamar a NativeQueries e suas constantes")
    void shouldReferenceNativeQueriesClassAndConstants() {
        Class<?> clazz = NativeQueries.class;
        assertNotNull(clazz);
        assertEquals("com.masantello.bookstoremanager.utils.NativeQueries", clazz.getName());

        String authorQuery = NativeQueries.FIND_ALL_BOOKS_OF_AN_AUTHOR;
        String publisherQuery = NativeQueries.FIND_ALL_BOOKS_OF_A_PUBLISHER;

        assertNotNull(authorQuery);
        assertTrue(authorQuery.toUpperCase().contains("SELECT"));
        assertNotNull(publisherQuery);
        assertTrue(publisherQuery.toUpperCase().contains("LEFT JOIN PUBLISHER P"));
    }
}
