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

    private static final SecretKey REFRESH_KEY = Keys.hmacShaKeyFor(
            AppConfig.REFRESH_TOKEN_SECRET.getBytes(StandardCharsets.UTF_8)
    );

    private static final SecretKey RESET_KEY = Keys.hmacShaKeyFor(
            AppConfig.get("JWT_RESET_SECRET", AppConfig.ACCESS_TOKEN_SECRET).getBytes(StandardCharsets.UTF_8)
    );

    private static final long RESET_TOKEN_EXPIRATION = 15 * 60 * 1000L; // 15 minutes

    public static String generateAccessToken(Integer userId, Integer roleId, String roleCode, String email) {
        var builder = Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("userId", userId)
                .claim("roleId", roleId)
                .claim("email", email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + AppConfig.ACCESS_TOKEN_EXPIRATION));

        if (roleCode != null) {
            builder.claim("roleCode", roleCode);
        }

        return builder.signWith(ACCESS_KEY).compact();
    }

    public static String generateAccessToken(Integer userId, Integer roleId, String email) {
        return generateAccessToken(userId, roleId, null, email);
    }

    public static Claims verifyToken(String token) {
        return verifyAccessToken(token);
    }

    public static Claims verifyAccessToken(String token) {
        return Jwts.parser()
                .verifyWith(ACCESS_KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public static String generateRefreshToken(Integer userId, String email, String roleCode) {
        var builder = Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("userId", userId)
                .claim("email", email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + AppConfig.REFRESH_TOKEN_EXPIRATION));

        if (roleCode != null) {
            builder.claim("roleCode", roleCode);
        }

        return builder.signWith(REFRESH_KEY).compact();
    }

    public static Claims verifyRefreshToken(String token) {
        return Jwts.parser()
                .verifyWith(REFRESH_KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public static String generateResetToken(String email) {
        return Jwts.builder()
                .claim("email", email)
                .claim("purpose", "RESET_PASSWORD")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + RESET_TOKEN_EXPIRATION))
                .signWith(RESET_KEY)
                .compact();
    }

    public static Claims verifyResetToken(String token) {
        return Jwts.parser()
                .verifyWith(RESET_KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public static long getRemainingMillis(Claims claims) {
        if (claims == null || claims.getExpiration() == null) {
            return 0;
        }
        long diff = claims.getExpiration().getTime() - System.currentTimeMillis();
        return Math.max(diff, 0);
    }

    public static long getRemainingMillis(String token) {
        try {
            Claims claims = verifyAccessToken(token);
            return getRemainingMillis(claims);
        } catch (Exception e) {
            return 0;
        }
    }
}