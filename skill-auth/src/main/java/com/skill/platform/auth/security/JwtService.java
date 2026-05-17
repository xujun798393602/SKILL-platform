package com.skill.platform.auth.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-token-expiry:7200}")
    private long accessTokenExpiry;

    @Value("${jwt.refresh-token-expiry:604800}")
    private long refreshTokenExpiry;

    private final RedisTemplate<String, String> redisTemplate;

    public JwtService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public String generateAccessToken(String userId, String employeeId, List<String> roles) {
        return Jwts.builder()
            .subject(userId)
            .claim("employeeId", employeeId)
            .claim("roles", roles)
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + accessTokenExpiry * 1000))
            .signWith(getSigningKey())
            .compact();
    }

    public String generateRefreshToken(String userId) {
        String refreshToken = UUID.randomUUID().toString();
        String key = "refresh_token:" + refreshToken;
        redisTemplate.opsForValue().set(key, userId, Duration.ofSeconds(refreshTokenExpiry));
        return refreshToken;
    }

    public String validateAccessToken(String token) {
        try {
            Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
            return claims.getSubject();
        } catch (JwtException e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
            return null;
        }
    }

    public String validateRefreshToken(String refreshToken) {
        String key = "refresh_token:" + refreshToken;
        return redisTemplate.opsForValue().get(key);
    }

    public void invalidateRefreshToken(String refreshToken) {
        String key = "refresh_token:" + refreshToken;
        redisTemplate.delete(key);
    }

    public Claims extractClaims(String token) {
        return Jwts.parser()
            .verifyWith(getSigningKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
