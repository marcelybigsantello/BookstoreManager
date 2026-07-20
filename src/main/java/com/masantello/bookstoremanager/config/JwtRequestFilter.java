package com.masantello.bookstoremanager.config;

import com.masantello.bookstoremanager.services.AuthenticationService;
import com.masantello.bookstoremanager.services.JwtTokenManager;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtRequestFilter.class);
    private static final String BEARER_PREFIX = "Bearer ";

    private final AuthenticationService authenticationService;
    private final JwtTokenManager jwtTokenManager;

    public JwtRequestFilter(AuthenticationService authenticationService,
                            JwtTokenManager jwtTokenManager) {
        this.authenticationService = authenticationService;
        this.jwtTokenManager = jwtTokenManager;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        var username = "";
        var jwtToken = "";

        var requestTokenHeader = request.getHeader("Authorization");
        if (isTokenPresent(requestTokenHeader)) {

            jwtToken = requestTokenHeader.substring(7);
            username = jwtTokenManager.getUsernameFromToken(jwtToken);
        } else {
            logger.warn("JWT Token does not begin with 'Bearer' String");
        }

        if (isUsernameInContext(username)) {
            addUsernameInContext(request, username, jwtToken);
        }

        filterChain.doFilter(request, response);
    }

    private boolean isTokenPresent(String requestTokenHeader) {
        return requestTokenHeader != null && requestTokenHeader.startsWith(BEARER_PREFIX);
    }

    private boolean isUsernameInContext(String username) {
        return username != null && SecurityContextHolder.getContext().getAuthentication() == null;
    }

    private void addUsernameInContext(HttpServletRequest request, String username, String jwtToken) {
        var userDetails = authenticationService.loadUserByUsername(username);
        if (jwtTokenManager.validateToken(jwtToken, userDetails)) {
            var authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
    }
}
