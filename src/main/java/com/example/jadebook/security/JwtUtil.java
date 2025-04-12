package com.example.jadebook.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);
    private final SecretKey secretKey;
    private final long expiration;

    public JwtUtil(@Value("${jwt.secret:}") String secretFromConfig,
                   @Value("${jwt.expiration:86400000}") long expiration) {
        if (secretFromConfig == null || secretFromConfig.trim().isEmpty()) {
            this.secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);
            logger.warn("JWT secret key is not provided in configuration. A new key has been generated.");
        } else {
            this.secretKey = Keys.hmacShaKeyFor(secretFromConfig.getBytes());
            logger.info("JWT secret key loaded from configuration.");
        }
        this.expiration = expiration;
    }

    public String generateToken(String userId) {
        // 添加日誌，確認 userId
        System.out.println("Generating JWT for userId: " + userId);
        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();
    }

    public String getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        String userId = claims.getSubject();
        // 添加日誌，確認提取的 userId
        System.out.println("Extracted userId from JWT: " + userId);
        return userId;
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }
}