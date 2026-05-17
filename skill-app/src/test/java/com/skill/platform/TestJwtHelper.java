package com.skill.platform;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Arrays;
import java.util.Date;

@Component
public class TestJwtHelper {

    @Value("${jwt.secret:test-secret-key-for-testing-only-256bits!!}")
    private String secret;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(String userId, String employeeId, String... roles) {
        return Jwts.builder()
                .subject(userId)
                .claim("employeeId", employeeId)
                .claim("roles", Arrays.asList(roles))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 7200000))
                .signWith(getSigningKey())
                .compact();
    }

    public String generateExpiredToken(String userId) {
        return Jwts.builder()
                .subject(userId)
                .issuedAt(new Date(System.currentTimeMillis() - 10000000))
                .expiration(new Date(System.currentTimeMillis() - 5000000))
                .signWith(getSigningKey())
                .compact();
    }
}
