package com.masantello.bookstoremanager.services;

import com.masantello.bookstoremanager.dtos.JwtRequest;
import com.masantello.bookstoremanager.models.User;
import com.masantello.bookstoremanager.models.enums.Role;
import com.masantello.bookstoremanager.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = AuthenticationService.class)
public class AuthenticationServiceTest {

    @Autowired
    private AuthenticationService authenticationService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private JwtTokenManager jwtTokenManager;

    private User user;
    private final String username = "test.user";
    private final String password = "secret";

    @BeforeEach
    void setup() {
        user = new User();
        user.setId(1L);
        user.setUsername(username);
        user.setPassword("encoded");
        user.setRole(Role.ADMIN);
    }

    @Test
    @DisplayName("createAuthenticationToken - when authentication and user exist should return jwt")
    void createAuthenticationToken_whenAuthSucceeds_shouldReturnJwtResponse() {
        // arrange
        var jwtRequest = new JwtRequest(username, password);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(jwtTokenManager.generateToken(any(UserDetails.class))).thenReturn("generated-jwt");

        // act
        var response = authenticationService.createAuthenticationToken(jwtRequest);

        // assert
        assertNotNull(response);
        assertEquals("generated-jwt", response.jwtToken());
        // verify authentication was invoked with correct credentials
        var captor = org.mockito.ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
        verify(authenticationManager).authenticate(captor.capture());
        var token = captor.getValue();
        assertEquals(username, token.getPrincipal());
        assertEquals(password, token.getCredentials());
    }

    @Test
    @DisplayName("createAuthenticationToken - when authentication fails should throw AuthenticationException")
    void createAuthenticationToken_whenAuthFails_shouldThrow() {
        // arrange
        var jwtRequest = new JwtRequest(username, password);
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Bad credentials"));

        // act & assert
        assertThrows(BadCredentialsException.class, () -> authenticationService.createAuthenticationToken(jwtRequest));
        verify(authenticationManager).authenticate(any());
        verify(userRepository, never()).findByUsername(anyString());
        verify(jwtTokenManager, never()).generateToken(any());
    }

    @Test
    @DisplayName("loadUserByUsername - when user exists should return UserDetails")
    void loadUserByUsername_whenUserExists_shouldReturnUserDetails() {
        // arrange
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // act
        var userDetails = authenticationService.loadUserByUsername(username);

        // assert
        assertNotNull(userDetails);
        assertEquals(username, userDetails.getUsername());
        assertEquals(user.getPassword(), userDetails.getPassword());
        assertThat(userDetails.getAuthorities()).isNotEmpty();
    }

    @Test
    @DisplayName("loadUserByUsername - when user not found should throw UsernameNotFoundException")
    void loadUserByUsername_whenUserNotFound_shouldThrow() {
        // arrange
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // act & assert
        var ex = assertThrows(UsernameNotFoundException.class, () -> authenticationService.loadUserByUsername(username));
        assertTrue(ex.getMessage().contains(username));
        verify(userRepository).findByUsername(username);
    }


}
