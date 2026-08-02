package com.masantello.bookstoremanager.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@SpringBootTest(classes = JwtAuthenticationEntryPoint.class)
public class JwtAuthenticationEntryPointTest {

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    private HttpServletRequest request;
    private HttpServletResponse response;

    @BeforeEach
    void setup() {
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
    }

    @Test
    @DisplayName("Should send unauthorized error when commence is called")
    void testCommence_ShouldSendUnauthorizedError() throws IOException, ServletException {
        // Arrange
        AuthenticationException authException = new BadCredentialsException("Bad credentials");

        // Act
        jwtAuthenticationEntryPoint.commence(request, response, authException);

        // Assert
        verify(response, times(1)).sendError(
                HttpServletResponse.SC_UNAUTHORIZED,
                "Unauthorized"
        );
    }

    @Test
    @DisplayName("Should send 401 status code specifically")
    void testCommence_ShouldSendCorrectStatusCode() throws IOException, ServletException {
        // Arrange
        AuthenticationException authException = new BadCredentialsException("Invalid token");
        int expectedStatusCode = HttpServletResponse.SC_UNAUTHORIZED;

        // Act
        jwtAuthenticationEntryPoint.commence(request, response, authException);

        // Assert
        verify(response, times(1)).sendError(expectedStatusCode, "Unauthorized");
    }

    @Test
    @DisplayName("Should send correct error message")
    void testCommence_ShouldSendCorrectErrorMessage() throws IOException, ServletException {
        // Arrange
        AuthenticationException authException = new BadCredentialsException("Invalid credentials");
        String expectedMessage = "Unauthorized";

        // Act
        jwtAuthenticationEntryPoint.commence(request, response, authException);

        // Assert
        verify(response, times(1)).sendError(
                HttpServletResponse.SC_UNAUTHORIZED,
                expectedMessage
        );
    }

    @Test
    @DisplayName("Should handle BadCredentialsException")
    void testCommence_WithBadCredentialsException() throws IOException, ServletException {
        // Arrange
        AuthenticationException authException = new BadCredentialsException("Credentials do not match");

        // Act
        jwtAuthenticationEntryPoint.commence(request, response, authException);

        // Assert
        verify(response, times(1)).sendError(
                HttpServletResponse.SC_UNAUTHORIZED,
                "Unauthorized"
        );
    }

    @Test
    @DisplayName("Should handle DisabledException")
    void testCommence_WithDisabledException() throws IOException, ServletException {
        // Arrange
        AuthenticationException authException = new DisabledException("Account is disabled");

        // Act
        jwtAuthenticationEntryPoint.commence(request, response, authException);

        // Assert
        verify(response, times(1)).sendError(
                HttpServletResponse.SC_UNAUTHORIZED,
                "Unauthorized"
        );
    }

    @Test
    @DisplayName("Should handle generic AuthenticationException")
    void testCommence_WithGenericAuthenticationException() throws IOException, ServletException {
        // Arrange
        AuthenticationException authException = new AuthenticationException("Generic auth error") {};

        // Act
        jwtAuthenticationEntryPoint.commence(request, response, authException);

        // Assert
        verify(response, times(1)).sendError(
                HttpServletResponse.SC_UNAUTHORIZED,
                "Unauthorized"
        );
    }

    @Test
    @DisplayName("Should propagate IOException when response.sendError throws IOException")
    void testCommence_WhenResponseThrowsIOException() throws IOException, ServletException {
        // Arrange
        AuthenticationException authException = new BadCredentialsException("Bad credentials");
        doThrow(new IOException("Response error")).when(response).sendError(
                HttpServletResponse.SC_UNAUTHORIZED,
                "Unauthorized"
        );

        // Act & Assert
        assertThrows(IOException.class, () ->
                jwtAuthenticationEntryPoint.commence(request, response, authException)
        );
    }


    @Test
    @DisplayName("Should handle null request gracefully")
    void testCommence_WithNullRequest() throws IOException, ServletException {
        // Arrange
        AuthenticationException authException = new BadCredentialsException("Bad credentials");

        // Act
        jwtAuthenticationEntryPoint.commence(null, response, authException);

        // Assert
        verify(response, times(1)).sendError(
                HttpServletResponse.SC_UNAUTHORIZED,
                "Unauthorized"
        );
    }

    @Test
    @DisplayName("Should handle null exception gracefully")
    void testCommence_WithNullException() throws IOException, ServletException {
        // Act & Assert
        // Note: @NonNull annotation on exception parameter means null should not be allowed
        // This test validates that the method can still be called even with null exception
        // In production, the container should prevent this through validation
        assertDoesNotThrow(() ->
                jwtAuthenticationEntryPoint.commence(request, response, null)
        );
    }

    @Test
    @DisplayName("Should call sendError only once")
    void testCommence_ShouldCallSendErrorOnlyOnce() throws IOException, ServletException {
        // Arrange
        AuthenticationException authException = new BadCredentialsException("Bad credentials");

        // Act
        jwtAuthenticationEntryPoint.commence(request, response, authException);

        // Assert
        verify(response, times(1)).sendError(anyInt(), anyString());
    }

    @Test
    @DisplayName("Should call sendError with exact parameters")
    void testCommence_ShouldCallSendErrorWithExactParameters() throws IOException, ServletException {
        // Arrange
        AuthenticationException authException = new BadCredentialsException("Bad credentials");

        // Act
        jwtAuthenticationEntryPoint.commence(request, response, authException);

        // Assert
        verify(response).sendError(
                eq(HttpServletResponse.SC_UNAUTHORIZED),
                eq("Unauthorized")
        );
    }

    @Test
    @DisplayName("Should be stateless and reusable")
    void testCommence_ShouldBeStatelessAndReusable() throws IOException, ServletException {
        // Arrange
        AuthenticationException authException1 = new BadCredentialsException("Error 1");
        AuthenticationException authException2 = new DisabledException("Error 2");

        // Act
        jwtAuthenticationEntryPoint.commence(request, response, authException1);
        jwtAuthenticationEntryPoint.commence(request, response, authException2);

        // Assert
        verify(response, times(2)).sendError(
                HttpServletResponse.SC_UNAUTHORIZED,
                "Unauthorized"
        );
    }

    @Test
    @DisplayName("Should work correctly with different response objects")
    void testCommence_WithDifferentResponseObjects() throws IOException, ServletException {
        // Arrange
        HttpServletResponse response1 = mock(HttpServletResponse.class);
        HttpServletResponse response2 = mock(HttpServletResponse.class);
        AuthenticationException authException = new BadCredentialsException("Bad credentials");

        // Act
        jwtAuthenticationEntryPoint.commence(request, response1, authException);
        jwtAuthenticationEntryPoint.commence(request, response2, authException);

        // Assert
        verify(response1, times(1)).sendError(
                HttpServletResponse.SC_UNAUTHORIZED,
                "Unauthorized"
        );
        verify(response2, times(1)).sendError(
                HttpServletResponse.SC_UNAUTHORIZED,
                "Unauthorized"
        );
    }

    @Test
    @DisplayName("Should not modify request or exception parameters")
    void testCommence_ShouldNotModifyInputParameters() throws IOException, ServletException {
        // Arrange
        AuthenticationException authException = new BadCredentialsException("Original message");
        String originalExceptionMessage = authException.getMessage();

        // Act
        jwtAuthenticationEntryPoint.commence(request, response, authException);

        // Assert
        assertEquals(originalExceptionMessage, authException.getMessage());
        verify(request, never()).setAttribute(anyString(), any());
        verify(request, never()).setAttribute(anyString(), anyString());
    }

}
