package com.fashionshop.presentation.filter;

import com.fashionshop.common.exception.UnauthorizedException;
import com.fashionshop.common.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

@WebFilter(urlPatterns = {
        "/api/v1/cart/*",
        "/api/v1/orders/*",
        "/api/v1/identity/users/*"
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

        try {
            String token = authHeader.substring(7).trim();
            Claims claims = JwtUtil.verifyToken(token);
            req.setAttribute("currentUserId", claims.get("userId", Integer.class));
            req.setAttribute("currentRoleId", claims.get("roleId", Integer.class));
            req.setAttribute("currentUserEmail", claims.get("email", String.class));
            chain.doFilter(request, response);
        } catch (Exception e) {
            throw new UnauthorizedException("Access token không hợp lệ hoặc đã hết hạn");
        }
    }
}