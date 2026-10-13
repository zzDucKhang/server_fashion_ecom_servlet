package com.fashionshop.common.util;


import com.fashionshop.common.exception.ForbiddenException;
import com.fashionshop.common.exception.UnauthorizedException;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;

public class SecurityGuard {

    /**
     * Tương đương requireAuth trong Node.js
     */
    public static Integer requireAuth(HttpServletRequest req) {
        String authHeader = req.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new UnauthorizedException("Vui lòng đăng nhập để thực hiện thao tác này");
        }

        try {
            String token = authHeader.substring(7).trim();
            Claims claims = JwtUtil.verifyToken(token);
            Object rawUserId = claims.get("userId");
            Integer userId = null;
            if (rawUserId instanceof Number) {
                userId = ((Number) rawUserId).intValue();
            } else if (rawUserId != null) {
                userId = Integer.parseInt(rawUserId.toString());
            }

            Object rawRoleId = claims.get("roleId");
            Integer roleId = rawRoleId instanceof Number ? ((Number) rawRoleId).intValue() : (rawRoleId != null ? Integer.parseInt(rawRoleId.toString()) : null);

            String roleCode = claims.get("roleCode", String.class);
            if (roleCode == null) {
                roleCode = claims.get("role", String.class);
            }
            String email = claims.get("email", String.class);
            req.setAttribute("currentUserId", userId);
            req.setAttribute("currentRoleId", roleId);
            req.setAttribute("currentRoleCode", roleCode != null ? roleCode : (roleId != null && roleId == 1 ? "ADMIN" : "CUSTOMER"));
            req.setAttribute("currentUserEmail", email);
            return userId;
        } catch (Exception e) {
            throw new UnauthorizedException("Access token không hợp lệ hoặc đã hết hạn");
        }
    }

    /**
     * Parse token nếu có, không quăng exception nếu không có hoặc không hợp lệ (dùng cho guest/optional auth)
     */
    public static Integer getOptionalUserId(HttpServletRequest req) {
        Object existing = req.getAttribute("currentUserId");
        if (existing instanceof Integer) {
            return (Integer) existing;
        }
        String authHeader = req.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        try {
            String token = authHeader.substring(7).trim();
            Claims claims = JwtUtil.verifyToken(token);
            Object rawUserId = claims.get("userId");
            Integer userId = null;
            if (rawUserId instanceof Number) {
                userId = ((Number) rawUserId).intValue();
            } else if (rawUserId != null) {
                userId = Integer.parseInt(rawUserId.toString());
            }

            Object rawRoleId = claims.get("roleId");
            Integer roleId = rawRoleId instanceof Number ? ((Number) rawRoleId).intValue() : (rawRoleId != null ? Integer.parseInt(rawRoleId.toString()) : null);

            String roleCode = claims.get("roleCode", String.class);
            if (roleCode == null) {
                roleCode = claims.get("role", String.class);
            }
            String email = claims.get("email", String.class);
            req.setAttribute("currentUserId", userId);
            req.setAttribute("currentRoleId", roleId);
            req.setAttribute("currentRoleCode", roleCode != null ? roleCode : (roleId != null && roleId == 1 ? "ADMIN" : "CUSTOMER"));
            req.setAttribute("currentUserEmail", email);
            return userId;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Tương đương requireAdmin trong Node.js (roleId = 1 hoặc roleCode = ADMIN)
     */
    public static Integer requireAdmin(HttpServletRequest req) {
        Integer userId = requireAuth(req);
        Integer roleId = (Integer) req.getAttribute("currentRoleId");
        String roleCode = (String) req.getAttribute("currentRoleCode");

        if ((roleId == null || roleId != 1) && !"ADMIN".equalsIgnoreCase(roleCode)) {
            throw new ForbiddenException("Bạn không có quyền thực hiện hành động này");
        }
        return userId;
    }

    public static String getRoleCode(HttpServletRequest req) {
        return (String) req.getAttribute("currentRoleCode");
    }
}
