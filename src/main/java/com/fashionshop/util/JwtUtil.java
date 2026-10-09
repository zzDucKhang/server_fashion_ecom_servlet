package com.fashionshop.util;

import com.fashionshop.business.entity.User;
import com.fashionshop.config.AppConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class cung cấp các hàm tạo, phân tích và xác thực JSON Web Token (JWT)
 * cho cả Access Token (15 phút) và Refresh Token (7 ngày) sử dụng HMAC-SHA256 (HS256).
 */
public final class JwtUtil {

    private static final Logger LOGGER = Logger.getLogger(JwtUtil.class.getName());

    // Thời hạn Access Token: 15 phút
    public static final long ACCESS_TOKEN_EXPIRATION_MS = 15 * 60 * 1000L;

    // Thời hạn Refresh Token: 7 ngày (7 * 24 * 60 * 60 * 1000)
    public static final long REFRESH_TOKEN_EXPIRATION_MS = 7 * 24 * 60 * 60 * 1000L;

    private static final SecretKey ACCESS_SIGNING_KEY;
    private static final SecretKey REFRESH_SIGNING_KEY;

    static {
        try {
            // 1. Khởi tạo Access Token Signing Key (đảm bảo >= 256 bits)
            String rawAccessSecret = AppConfig.ACCESS_TOKEN_SECRET;
            if (rawAccessSecret == null || rawAccessSecret.isBlank()) {
                rawAccessSecret = "default_enterprise_fashion_ecom_access_secret_key_minimum_256_bits";
            }
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] accessKeyBytes = digest.digest(rawAccessSecret.getBytes(StandardCharsets.UTF_8));
            ACCESS_SIGNING_KEY = Keys.hmacShaKeyFor(accessKeyBytes);

            // 2. Khởi tạo Refresh Token Signing Key (cách ly hoàn toàn với Access Token Key)
            String rawRefreshSecret = AppConfig.REFRESH_TOKEN_SECRET;
            if (rawRefreshSecret == null || rawRefreshSecret.isBlank()) {
                rawRefreshSecret = "default_enterprise_fashion_ecom_refresh_secret_key_minimum_256_bits";
            }
            byte[] refreshKeyBytes = digest.digest(rawRefreshSecret.getBytes(StandardCharsets.UTF_8));
            REFRESH_SIGNING_KEY = Keys.hmacShaKeyFor(refreshKeyBytes);

        } catch (NoSuchAlgorithmException ex) {
            LOGGER.log(Level.SEVERE, "Không tìm thấy thuật toán SHA-256 để khởi tạo JWT Signing Keys", ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    private JwtUtil() {
        throw new UnsupportedOperationException("JwtUtil is a utility class and cannot be instantiated");
    }

    /**
     * Tạo Access Token (JWT) cho User (Hạn 15 phút).
     *
     * @param user Đối tượng User cần cấp Token
     * @return Chuỗi JWT đã ký
     */
    public static String generateAccessToken(User user) {
        if (user == null || user.getId() == null) {
            throw new IllegalArgumentException("User và User ID không được phép là null khi tạo Access Token.");
        }

        long now = System.currentTimeMillis();
        long exp = now + ACCESS_TOKEN_EXPIRATION_MS;

        String roleCode = (user.getRole() != null && user.getRole().getCode() != null)
                ? user.getRole().getCode()
                : "CUSTOMER";

        return Jwts.builder()
                .subject(String.valueOf(user.getId()))
                .claim("tokenType", "ACCESS")
                .claim("role", roleCode)
                .claim("email", user.getEmail() != null ? user.getEmail() : "")
                .claim("fullName", user.getFullName() != null ? user.getFullName() : "")
                .issuedAt(new Date(now))
                .expiration(new Date(exp))
                .signWith(ACCESS_SIGNING_KEY)
                .compact();
    }

    /**
     * Tạo Refresh Token (JWT) cho User với thời hạn sống dài (7 ngày).
     *
     * @param user Đối tượng User cần cấp Refresh Token
     * @return Chuỗi Refresh Token JWT đã ký
     */
    public static String generateRefreshToken(User user) {
        if (user == null || user.getId() == null) {
            throw new IllegalArgumentException("User và User ID không được phép là null khi tạo Refresh Token.");
        }

        long now = System.currentTimeMillis();
        long exp = now + REFRESH_TOKEN_EXPIRATION_MS;

        return Jwts.builder()
                .subject(String.valueOf(user.getId()))
                .claim("tokenType", "REFRESH")
                .id(UUID.randomUUID().toString()) // JTI độc nhất chống Replay Attack
                .issuedAt(new Date(now))
                .expiration(new Date(exp))
                .signWith(REFRESH_SIGNING_KEY)
                .compact();
    }

    /**
     * Tạo Refresh Token ngẫu nhiên (dạng UUID kết hợp Entropy bảo mật cao).
     *
     * @return Chuỗi Refresh Token dạng UUID chuỗi kép an toàn
     */
    public static String generateRefreshToken() {
        return UUID.randomUUID().toString().replace("-", "") + UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * Trích xuất Claims từ Access Token.
     */
    public static Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(ACCESS_SIGNING_KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Trích xuất Claims từ Refresh Token.
     */
    public static Claims extractRefreshTokenClaims(String token) {
        return Jwts.parser()
                .verifyWith(REFRESH_SIGNING_KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Kiểm tra tính hợp lệ của Access Token.
     */
    public static boolean validateToken(String token) {
        if (token == null || token.isBlank()) {
            return false;
        }
        try {
            extractClaims(token);
            return true;
        } catch (ExpiredJwtException ex) {
            LOGGER.fine("Access Token đã hết hạn: " + ex.getMessage());
            return false;
        } catch (JwtException ex) {
            LOGGER.warning("Access Token không hợp lệ: " + ex.getMessage());
            return false;
        }
    }

    /**
     * Kiểm tra tính hợp lệ của Refresh Token.
     */
    public static boolean validateRefreshToken(String token) {
        if (token == null || token.isBlank()) {
            return false;
        }
        try {
            extractRefreshTokenClaims(token);
            return true;
        } catch (ExpiredJwtException ex) {
            LOGGER.fine("Refresh Token đã hết hạn: " + ex.getMessage());
            return false;
        } catch (JwtException ex) {
            LOGGER.warning("Refresh Token không hợp lệ: " + ex.getMessage());
            return false;
        }
    }

    public static Integer getUserIdFromToken(String token) {
        Claims claims = extractClaims(token);
        return Integer.valueOf(claims.getSubject());
    }

    public static Integer getUserIdFromRefreshToken(String token) {
        Claims claims = extractRefreshTokenClaims(token);
        return Integer.valueOf(claims.getSubject());
    }

    public static String getRoleFromToken(String token) {
        Claims claims = extractClaims(token);
        return claims.get("role", String.class);
    }
}
