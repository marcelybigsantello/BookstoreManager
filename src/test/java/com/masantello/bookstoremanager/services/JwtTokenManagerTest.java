package com.masantello.bookstoremanager.services;

import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = JwtTokenManager.class)
public class JwtTokenManagerTest {

    @Autowired
    private JwtTokenManager jwtTokenManager;

    @MockitoBean
    private UserDetails userDetails;

    @BeforeEach
    void setup() {
        when(userDetails.getUsername()).thenReturn("johndoe");
    }

    @Test
    void generateToken_shouldCreateTokenAndExtractUsername() {
        String token = jwtTokenManager.generateToken(userDetails);

        assertNotNull(token);
        assertFalse(token.isBlank());
        assertEquals("johndoe", jwtTokenManager.getUsernameFromToken(token));
    }

    @Test
    void getExpirationDateFromToken_shouldReturnValidFutureDate() {
        String token = jwtTokenManager.generateToken(userDetails);

        Date expirationDate = jwtTokenManager.getExpirationDateFromToken(token);

        assertNotNull(expirationDate);
        assertTrue(expirationDate.after(new Date()));
    }

    @Test
    void validateToken_shouldReturnTrueForMatchingAndNotExpiredToken() {
        String token = jwtTokenManager.generateToken(userDetails);

        assertTrue(jwtTokenManager.validateToken(token, userDetails));
    }

    @Test
    void validateToken_shouldReturnFalseWhenUsernameDoesNotMatch() {
        String token = jwtTokenManager.generateToken(userDetails);
        UserDetails anotherUser = mock(UserDetails.class);
        when(anotherUser.getUsername()).thenReturn("janedoe");

        assertFalse(jwtTokenManager.validateToken(token, anotherUser));
    }

    @Test
    void validateToken_shouldReturnFalseWhenTokenIsExpired() {
        JwtTokenManager expiredTokenManager = new JwtTokenManager(0L);
        String expiredToken = expiredTokenManager.generateToken(userDetails);

        assertThrows(ExpiredJwtException.class, () -> expiredTokenManager.validateToken(expiredToken, userDetails));
    }

}
