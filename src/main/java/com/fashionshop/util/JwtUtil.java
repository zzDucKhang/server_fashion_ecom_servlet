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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class cung cấp các hàm tạo, phân tích và xác thực JSON Web Token (JWT)
 * sử dụng thuật toán HMAC-SHA256 (HS256).
 */
public final class JwtUtil {

    private static final Logger LOGGER = Logger.getLogger(JwtUtil.class.getName());

    // Thời hạn Access Token mặc định: 15 phút (theo yêu cầu hệ thống)
    public static final long ACCESS_TOKEN_EXPIRATION_MS = 15 * 60 * 1000L;

    private static final SecretKey SIGNING_KEY;

    static {
        try {
            // Lấy secret từ AppConfig; băm qua SHA-256 để đảm bảo độ dài tối thiểu 256-bit (32 bytes) cho HS256
            String rawSecret = AppConfig.ACCESS_TOKEN_SECRET;
            if (rawSecret == null || rawSecret.isBlank()) {
                rawSecret = "default_enterprise_fashion_ecom_jwt_secret_key_minimum_256_bits";
            }
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] keyBytes = digest.digest(rawSecret.getBytes(StandardCharsets.UTF_8));
            SIGNING_KEY = Keys.hmacShaKeyFor(keyBytes);
        } catch (NoSuchAlgorithmException ex) {
            LOGGER.log(Level.SEVERE, "Không tìm thấy thuật toán SHA-256 để khởi tạo JWT Signing Key", ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    private JwtUtil() {
        throw new UnsupportedOperationException("JwtUtil is a utility class and cannot be instantiated");
    }

    /**
     * Tạo Access Token (JWT) cho User.
     * - Subject: User ID
     * - Claims: role, email, fullName
     * - Expiration: 15 phút
     * - Algorithm: HMAC-SHA256
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
                .claim("role", roleCode)
                .claim("email", user.getEmail() != null ? user.getEmail() : "")
                .claim("fullName", user.getFullName() != null ? user.getFullName() : "")
                .issuedAt(new Date(now))
                .expiration(new Date(exp))
                .signWith(SIGNING_KEY)
                .compact();
    }

    /**
     * Trích xuất toàn bộ Claims từ Token sau khi đã xác thực chữ ký số.
     *
     * @param token Chuỗi JWT
     * @return Claims payload
     * @throws JwtException nếu chữ ký sai hoặc token bị giả mạo / hết hạn
     */
    public static Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(SIGNING_KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Kiểm tra tính hợp lệ của Token (chữ ký hợp lệ và chưa hết hạn).
     *
     * @param token Chuỗi JWT cần kiểm tra
     * @return true nếu token hợp lệ, ngược lại false
     */
    public static boolean validateToken(String token) {
        if (token == null || token.isBlank()) {
            return false;
        }
        try {
            extractClaims(token);
            return true;
        } catch (ExpiredJwtException ex) {
            LOGGER.fine("JWT Token đã hết hạn: " + ex.getMessage());
            return false;
        } catch (JwtException ex) {
            LOGGER.warning("JWT Token không hợp lệ hoặc sai chữ ký: " + ex.getMessage());
            return false;
        }
    }

    /**
     * Lấy User ID từ Token.
     *
     * @param token Chuỗi JWT
     * @return User ID kiểu Integer
     */
    public static Integer getUserIdFromToken(String token) {
        Claims claims = extractClaims(token);
        return Integer.valueOf(claims.getSubject());
    }

    /**
     * Lấy Role Code từ Token.
     *
     * @param token Chuỗi JWT
     * @return Role code
     */
    public static String getRoleFromToken(String token) {
        Claims claims = extractClaims(token);
        return claims.get("role", String.class);
    }
}
