package com.masantello.bookstoremanager.services;

import com.masantello.bookstoremanager.dtos.AuthenticatedUser;
import com.masantello.bookstoremanager.dtos.JwtRequest;
import com.masantello.bookstoremanager.dtos.JwtResponse;
import com.masantello.bookstoremanager.repositories.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenManager jwtTokenManager;

    public AuthenticationService(UserRepository userRepository,
                                 AuthenticationManager authenticationManager,
                                 JwtTokenManager jwtTokenManager) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.jwtTokenManager = jwtTokenManager;
    }

    public JwtResponse createAuthenticationToken(JwtRequest jwtRequest) {
        authenticate(jwtRequest);

        var userDetails = this.loadUserByUsername(jwtRequest.getUsername());
        var token = jwtTokenManager.generateToken(userDetails);

        return JwtResponse.builder().jwtToken(token).build();
    }

    private void authenticate(JwtRequest jwtRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(jwtRequest.getUsername(),
                jwtRequest.getPassword()));
    }

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("User with username='%s' not found",
                        username)));
        return new AuthenticatedUser(
                user.getUsername(),
                user.getPassword(),
                user.getRole().getDescription());
    }
}
