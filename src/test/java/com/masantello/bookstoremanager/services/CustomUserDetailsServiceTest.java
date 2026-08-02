package com.masantello.bookstoremanager.services;

import com.masantello.bookstoremanager.models.User;
import com.masantello.bookstoremanager.models.enums.Role;
import com.masantello.bookstoremanager.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = CustomUserDetailsService.class)
public class CustomUserDetailsServiceTest {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private UserRepository userRepository;

    @Captor
    private ArgumentCaptor<String> usernameCaptor;

    @BeforeEach
    void setup() {
        usernameCaptor = ArgumentCaptor.forClass(String.class);
    }

    @Test
    void loadUserByUsername_whenUserExists_shouldReturnAuthenticatedUserWithCorrectFieldsAndAuthorities() {
        // Arrange
        String username = "johndoe";
        String password = "hashedPassword";
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setRole(Role.ADMIN); // description -> "Admin"

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // Act
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

        // Assert
        assertNotNull(userDetails);
        assertEquals(username, userDetails.getUsername());
        assertEquals(password, userDetails.getPassword());

        // AuthenticatedUser builds authorities as "ROLE_" + roleDescription (Role.ADMIN.description == "Admin")
        assertTrue(userDetails.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_Admin")),
                "Authorities must contain ROLE_Admin");

        verify(userRepository, times(1)).findByUsername(usernameCaptor.capture());
        assertEquals(username, usernameCaptor.getValue());
    }

    @Test
    void loadUserByUsername_whenUserDoesNotExist_shouldThrowUsernameNotFoundException() {
        // Arrange
        String username = "missing";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act & Assert
        UsernameNotFoundException ex = assertThrows(UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername(username));

        assertTrue(ex.getMessage().contains(username));
        verify(userRepository, times(1)).findByUsername(username);
    }
}
