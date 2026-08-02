package com.masantello.bookstoremanager.config;

import com.masantello.bookstoremanager.dtos.AuthenticatedUser;
import com.masantello.bookstoremanager.services.AuthenticationService;
import com.masantello.bookstoremanager.services.JwtTokenManager;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@SpringBootTest(classes = JwtRequestFilter.class)
public class JwtRequestFilterTest {

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @MockitoBean
    private AuthenticationService authenticationService;

    @MockitoBean
    private JwtTokenManager jwtTokenManager;

    private HttpServletRequest request;
    private HttpServletResponse response;
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        filterChain = mock(FilterChain.class);
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Should continue filter chain when Authorization header is null")
    void testDoFilterInternal_WhenAuthorizationHeaderIsNull() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn(null);

        // Act
        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @DisplayName("Should continue filter chain when Authorization header does not start with Bearer prefix")
    void testDoFilterInternal_WhenTokenDoesNotStartWithBearer() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("InvalidToken123");

        // Act
        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(jwtTokenManager, never()).getUsernameFromToken(anyString());
    }

    @Test
    @DisplayName("Should extract username from valid JWT token and set authentication when token is valid")
    void testDoFilterInternal_WhenValidTokenIsProvided() throws ServletException, IOException {
        // Arrange
        String validToken = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.validtoken";
        String username = "testuser";

        AuthenticatedUser userDetails = new AuthenticatedUser(
                username,
                "hashedPassword",
                "Admin"
        );

        when(request.getHeader("Authorization")).thenReturn(validToken);
        when(jwtTokenManager.getUsernameFromToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.validtoken"))
                .thenReturn(username);
        when(authenticationService.loadUserByUsername(username)).thenReturn(userDetails);
        when(jwtTokenManager.validateToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.validtoken", userDetails))
                .thenReturn(true);

        // Act
        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertEquals(userDetails, authentication.getPrincipal());
        verify(jwtTokenManager, times(1)).validateToken(anyString(), eq(userDetails));
    }

    @Test
    @DisplayName("Should not set authentication when token validation fails")
    void testDoFilterInternal_WhenTokenValidationFails() throws ServletException, IOException {
        // Arrange
        String validToken = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.invalidtoken";
        String username = "testuser";

        AuthenticatedUser userDetails = new AuthenticatedUser(
                username,
                "hashedPassword",
                "Admin"
        );

        when(request.getHeader("Authorization")).thenReturn(validToken);
        when(jwtTokenManager.getUsernameFromToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.invalidtoken"))
                .thenReturn(username);
        when(authenticationService.loadUserByUsername(username)).thenReturn(userDetails);
        when(jwtTokenManager.validateToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.invalidtoken", userDetails))
                .thenReturn(false);

        // Act
        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @DisplayName("Should not set authentication when username is empty")
    void testDoFilterInternal_WhenUsernameIsEmpty() throws ServletException, IOException {
        // Arrange
        String validToken = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.validtoken";

        when(request.getHeader("Authorization")).thenReturn(validToken);
        when(jwtTokenManager.getUsernameFromToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.validtoken"))
                .thenReturn("");

        // Act
        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(authenticationService, never()).loadUserByUsername(anyString());
    }

    @Test
    @DisplayName("Should not set authentication when username is blank")
    void testDoFilterInternal_WhenUsernameIsBlank() throws ServletException, IOException {
        // Arrange
        String validToken = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.validtoken";

        when(request.getHeader("Authorization")).thenReturn(validToken);
        when(jwtTokenManager.getUsernameFromToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.validtoken"))
                .thenReturn("   ");

        // Act
        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(authenticationService, never()).loadUserByUsername(anyString());
    }

    @Test
    @DisplayName("Should not set authentication when SecurityContext already has authentication")
    void testDoFilterInternal_WhenSecurityContextAlreadyHasAuthentication() throws ServletException, IOException {
        // Arrange
        String validToken = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.validtoken";
        String username = "testuser";

        AuthenticatedUser existingUser = new AuthenticatedUser(
                "existinguser",
                "hashedPassword",
                "Admin"
        );

        // Set existing authentication in context
        SecurityContextHolder.getContext().setAuthentication(
                new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                        existingUser, null, new ArrayList<>()
                )
        );

        when(request.getHeader("Authorization")).thenReturn(validToken);
        when(jwtTokenManager.getUsernameFromToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.validtoken"))
                .thenReturn(username);

        // Act
        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        verify(authenticationService, never()).loadUserByUsername(anyString());
        assertEquals("existinguser",
                ((AuthenticatedUser) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication())
                        .getPrincipal()).getUsername());
    }

    @Test
    @DisplayName("Should extract correct token by removing Bearer prefix")
    void testDoFilterInternal_WhenExtractingTokenFromBearerHeader() throws ServletException, IOException {
        // Arrange
        String authorizationHeader = "Bearer myValidToken123";
        String expectedToken = "myValidToken123";
        String username = "testuser";

        AuthenticatedUser userDetails = new AuthenticatedUser(
                username,
                "hashedPassword",
                "Admin"
        );

        when(request.getHeader("Authorization")).thenReturn(authorizationHeader);
        when(jwtTokenManager.getUsernameFromToken(expectedToken)).thenReturn(username);
        when(authenticationService.loadUserByUsername(username)).thenReturn(userDetails);
        when(jwtTokenManager.validateToken(expectedToken, userDetails)).thenReturn(true);

        // Act
        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(jwtTokenManager, times(1)).getUsernameFromToken(expectedToken);
        verify(jwtTokenManager, times(1)).validateToken(expectedToken, userDetails);
    }

    @Test
    @DisplayName("Should set WebAuthenticationDetails on authentication token")
    void testDoFilterInternal_WhenSettingWebAuthenticationDetails() throws ServletException, IOException {
        // Arrange
        String validToken = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.validtoken";
        String username = "testuser";

        AuthenticatedUser userDetails = new AuthenticatedUser(
                username,
                "hashedPassword",
                "Admin"
        );

        when(request.getHeader("Authorization")).thenReturn(validToken);
        when(jwtTokenManager.getUsernameFromToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.validtoken"))
                .thenReturn(username);
        when(authenticationService.loadUserByUsername(username)).thenReturn(userDetails);
        when(jwtTokenManager.validateToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.validtoken", userDetails))
                .thenReturn(true);

        // Act
        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        // Assert
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertNotNull(authentication.getDetails());
    }

    @Test
    @DisplayName("Should always call filterChain.doFilter regardless of authentication success")
    void testDoFilterInternal_ShouldAlwaysCallFilterChain() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn(null);

        // Act
        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("Should not set authentication when username is null")
    void testDoFilterInternal_WhenUsernameIsNull() throws ServletException, IOException {
        // Arrange
        String authorizationHeader = "Bearer some.token";
        when(request.getHeader("Authorization")).thenReturn(authorizationHeader);
        when(jwtTokenManager.getUsernameFromToken("some.token")).thenReturn(null);

        // Act
        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication()); // Como username == null, não deve haver autenticação no SecurityContext
        verify(authenticationService, never()).loadUserByUsername(anyString()); // Não deve tentar carregar o usuário
        verify(jwtTokenManager, never()).validateToken(anyString(), any()); // Nem validar o token
    }

}
