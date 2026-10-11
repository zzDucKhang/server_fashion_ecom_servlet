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
            Integer userId = claims.get("userId", Integer.class);
            Integer roleId = claims.get("roleId", Integer.class);
            req.setAttribute("currentUserId", userId);
            req.setAttribute("currentRoleId", roleId);
            return userId;
        } catch (Exception e) {
            throw new UnauthorizedException("Access token không hợp lệ hoặc đã hết hạn");
        }
    }

    /**
     * Tương đương requireAdmin trong Node.js (roleId = 1 là ADMIN)
     */
    public static Integer requireAdmin(HttpServletRequest req) {
        Integer userId = requireAuth(req);
        Integer roleId = (Integer) req.getAttribute("currentRoleId");

        // Trong hệ thống roleId = 1 là ADMIN (hoặc query từ RoleDAO)
        if (roleId == null || roleId != 1) {
            throw new ForbiddenException("Bạn không có quyền thực hiện hành động này");
        }
        return userId;
    }
}
