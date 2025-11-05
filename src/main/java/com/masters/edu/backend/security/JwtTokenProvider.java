package com.masters.edu.backend.security;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenProvider {

    private final JwtProperties properties;

    public JwtTokenProvider(JwtProperties properties) {
        this.properties = properties;
    }

    public String generateAccessToken(UserPrincipal userPrincipal) {
        Instant now = Instant.now();
        Instant expiry = now.plus(properties.getExpirationMinutes(), ChronoUnit.MINUTES);
        return buildToken(userPrincipal, now, expiry, TokenType.ACCESS);
    }

    public String generateRefreshToken(UserPrincipal userPrincipal) {
        Instant now = Instant.now();
        Instant expiry = now.plus(properties.getRefreshExpirationDays(), ChronoUnit.DAYS);
        return buildToken(userPrincipal, now, expiry, TokenType.REFRESH);
    }

    private String buildToken(UserPrincipal userPrincipal, Instant issuedAt, Instant expiry, TokenType tokenType) {
        return Jwts.builder()
                .setSubject(String.valueOf(userPrincipal.getId()))
                .claim("email", userPrincipal.getEmail())
                .claim("role", userPrincipal.getRole())
                .claim("type", tokenType.name())
                .setIssuedAt(Date.from(issuedAt))
                .setExpiration(Date.from(expiry))
                .signWith(Keys.hmacShaKeyFor(properties.getSecret().getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token, TokenType expectedType) {
        try {
            Claims claims = parseClaims(token);
            String type = claims.get("type", String.class);
            return expectedType.name().equals(type) && claims.getExpiration().after(new Date());
        } catch (Exception ex) {
            return false;
        }
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = parseClaims(token);
        return Long.parseLong(claims.getSubject());
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(properties.getSecret().getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}


