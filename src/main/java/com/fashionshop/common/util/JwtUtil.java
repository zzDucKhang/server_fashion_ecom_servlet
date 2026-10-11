package com.fashionshop.common.util;


import com.fashionshop.config.AppConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class JwtUtil {
    private static final SecretKey ACCESS_KEY = Keys.hmacShaKeyFor(
            AppConfig.ACCESS_TOKEN_SECRET.getBytes(StandardCharsets.UTF_8)
    );

    public static String generateAccessToken(Integer userId, Integer roleId, String email) {
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("userId", userId)
                .claim("roleId", roleId)
                .claim("email", email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + AppConfig.ACCESS_TOKEN_EXPIRATION))
                .signWith(ACCESS_KEY)
                .compact();
    }

    public static Claims verifyToken(String token) {
        return Jwts.parser()
                .verifyWith(ACCESS_KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}