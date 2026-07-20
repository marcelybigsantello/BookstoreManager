package com.masantello.bookstoremanager.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtTokenManager {

    private final long jwtTokenValidity;
    private final String secret;

    public JwtTokenManager(
            @Value("${jwt.validity}") Long jwtTokenValidity,
            @Value("${jwt.secret}") String secret) {
        this.jwtTokenValidity = jwtTokenValidity;
        this.secret = secret;
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();

        var key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .claims(claims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(Date.from(Instant.now().plusSeconds(jwtTokenValidity * 1000)))
                .signWith(key)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return getClaimsForToken(token, Claims::getSubject);
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimsForToken(token, Claims::getExpiration);
    }

    private <T> T getClaimsForToken(String token, Function<Claims, T> claimsResolver) {
        var key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        var claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
        return claimsResolver.apply(claims);
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        var username = getUsernameFromToken(token);
        return (username.equalsIgnoreCase(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        var expirationDate = getExpirationDateFromToken(token);
        return expirationDate.before(new Date());
    }
}
