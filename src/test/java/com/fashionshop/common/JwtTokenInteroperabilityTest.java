package com.fashionshop.common;

import com.fashionshop.common.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JwtTokenInteroperabilityTest {

    @Test
    @DisplayName("Verify Access Token contains both role and roleCode claims matching Express server")
    void testAccessTokenClaimsParity() {
        Integer userId = 42;
        Integer roleId = 1;
        String roleCode = "ADMIN";
        String email = "admin@fashion.test";

        String token = JwtUtil.generateAccessToken(userId, roleId, roleCode, email);
        assertNotNull(token);

        Claims claims = JwtUtil.verifyAccessToken(token);
        assertEquals(String.valueOf(userId), claims.getSubject());
        assertEquals(userId, ((Number) claims.get("userId")).intValue());
        assertEquals(roleId, ((Number) claims.get("roleId")).intValue());
        assertEquals(email, claims.get("email"));
        assertEquals("ADMIN", claims.get("roleCode"));
        assertEquals("ADMIN", claims.get("role"));
    }

    @Test
    @DisplayName("Verify Refresh Token contains role, roleCode, and email claims")
    void testRefreshTokenClaimsParity() {
        Integer userId = 99;
        String email = "customer@fashion.test";
        String roleCode = "CUSTOMER";

        String refreshToken = JwtUtil.generateRefreshToken(userId, email, roleCode);
        assertNotNull(refreshToken);

        Claims claims = JwtUtil.verifyRefreshToken(refreshToken);
        assertEquals(String.valueOf(userId), claims.getSubject());
        assertEquals(userId, ((Number) claims.get("userId")).intValue());
        assertEquals(email, claims.get("email"));
        assertEquals("CUSTOMER", claims.get("roleCode"));
        assertEquals("CUSTOMER", claims.get("role"));
    }
}
