package com.netzero.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final SecretKey key;
    private final long accessTokenValidity;
    private final long refreshTokenValidity;

    public JwtTokenProvider(
            @Value("${jwt.secret:netzero-secret-key-must-be-at-least-32-characters-long}") String secret,
            @Value("${jwt.access-token-validity:3600000}") long accessTokenValidity,
            @Value("${jwt.refresh-token-validity:604800000}") long refreshTokenValidity) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenValidity = accessTokenValidity;
        this.refreshTokenValidity = refreshTokenValidity;
    }

    public String createAccessToken(Long userId) {
        return createToken(userId, accessTokenValidity);
    }

    public String createRefreshToken(Long userId) {
        return createToken(userId, refreshTokenValidity);
    }

    private String createToken(Long userId, long validity) {
        Date now = new Date();
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .issuedAt(now)
                .expiration(new Date(now.getTime() + validity))
                .signWith(key)
                .compact();
    }

    public Long getUserId(String token) {
        return Long.parseLong(
                Jwts.parser().verifyWith(key).build()
                        .parseSignedClaims(token)
                        .getPayload()
                        .getSubject()
        );
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
