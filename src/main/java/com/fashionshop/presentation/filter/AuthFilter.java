package com.fashionshop.presentation.filter;

import com.fashionshop.common.exception.UnauthorizedException;
import com.fashionshop.common.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

@WebFilter(urlPatterns = {
        "/api/v1/orders/*",
        "/api/orders/*",
        "/api/v1/payments/*",
        "/api/payments/*",
        "/api/v1/fulfillment/shipments/*",
        "/api/shipments/*",
        "/api/v1/fulfillment/returns/*",
        "/api/returns/*",
        "/api/v1/identity/users/*",
        "/api/v1/identity/addresses/*",
        "/api/v1/identity/roles/*"
})
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;

        if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
            chain.doFilter(request, response);
            return;
        }

        String authHeader = req.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new UnauthorizedException("Vui lòng đăng nhập để thực hiện thao tác này");
        }

        String token = authHeader.substring(7).trim();

        // Check Redis blacklist
        try {
            if (com.fashionshop.common.util.RedisUtil.getClient() != null
                    && com.fashionshop.common.util.RedisUtil.getClient().getBucket("blacklist:token:" + token).isExists()) {
                throw new UnauthorizedException("Access token không hợp lệ hoặc đã hết hạn");
            }
        } catch (UnauthorizedException ue) {
            throw ue;
        } catch (Exception ignored) {
            // Redis error gracefully ignored in case cache is offline
        }

        try {
            Claims claims = JwtUtil.verifyToken(token);
            Integer userId = claims.get("userId", Integer.class);
            Integer roleId = claims.get("roleId", Integer.class);
            String roleCode = claims.get("roleCode", String.class);
            String email = claims.get("email", String.class);

            req.setAttribute("currentUserId", userId);
            req.setAttribute("currentRoleId", roleId);
            req.setAttribute("currentRoleCode", roleCode != null ? roleCode : (roleId != null && roleId == 1 ? "ADMIN" : "CUSTOMER"));
            req.setAttribute("currentUserEmail", email);
            chain.doFilter(request, response);
        } catch (Exception e) {
            throw new UnauthorizedException("Access token không hợp lệ hoặc đã hết hạn");
        }
    }
}