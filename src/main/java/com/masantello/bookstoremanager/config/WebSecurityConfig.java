package com.masantello.bookstoremanager.config;

import com.masantello.bookstoremanager.models.enums.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {

    private static final String USERS_API_URL = "/api/v1/users/**";
    private static final String AUTHORS_API_URL = "/api/v1/authors/**";
    private static final String PUBLISHERS_API_URL = "/api/v1/publishers/**";
    private static final String BOOKS_API_URL = "/api/v1/books/**";
    private static final String ROLE_ADMIN = Role.ADMIN.getDescription();
    private static final String ROLE_COMMON = Role.COMMON.getDescription();

    private static final String[] SWAGGER_RESOURCES = {
            "/v2/api-docs",
            "/configuration/ui",
            "/configuration/security",
            "/webjars/**"
    };

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;


    public WebSecurityConfig(JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
                             UserDetailsService userDetailsService,
                             PasswordEncoder passwordEncoder) {
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public SecurityFilterChain configure(HttpSecurity httpSecurity) {
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable).
                authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(USERS_API_URL).permitAll()
                        .requestMatchers(AUTHORS_API_URL).hasAnyRole(ROLE_ADMIN)
                        .requestMatchers(PUBLISHERS_API_URL).hasAnyRole(ROLE_ADMIN)
                        .requestMatchers(BOOKS_API_URL).hasAnyRole(ROLE_ADMIN, ROLE_COMMON)
                        .requestMatchers(SWAGGER_RESOURCES).permitAll()
                        .anyRequest().authenticated())
                .exceptionHandling(exception -> exception.authenticationEntryPoint(jwtAuthenticationEntryPoint))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return httpSecurity.build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) {
        AuthenticationManagerBuilder authBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authBuilder.authenticationProvider(authenticationProvider());
        return authBuilder.build();
    }


}
