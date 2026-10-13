package com.fashionshop.presentation.filter;

import com.fashionshop.common.util.JwtUtil;
import com.fashionshop.common.util.RedisUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

/**
 * Filter that parses authentication tokens if present, but allows guest access to proceed
 * without throwing UnauthorizedException.
 */
@WebFilter(urlPatterns = {
        "/api/v1/cart-order/cart",
        "/api/v1/cart-order/cart/*",
        "/api/v1/cart",
        "/api/v1/cart/*",
        "/api/cart",
        "/api/cart/*",
        "/api/v1/catalog/*",
        "/api/v1/loyalty-engage/reviews",
        "/api/v1/loyalty-engage/reviews/*",
        "/api/v1/reviews",
        "/api/v1/reviews/*",
        "/api/v1/loyalty-engage/chat",
        "/api/v1/loyalty-engage/chat/*",
        "/api/v1/chat",
        "/api/v1/chat/*",
        "/api/v1/common/*"
})
public class OptionalAuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;

        String authHeader = req.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7).trim();
            try {
                // If Redis is online, verify token is not blacklisted
                boolean blacklisted = false;
                if (RedisUtil.isAvailable()) {
                    try {
                        blacklisted = RedisUtil.getClient().getBucket("blacklist:token:" + token).isExists();
                    } catch (Exception ignored) {
                    }
                }

                if (!blacklisted) {
                    Claims claims = JwtUtil.verifyToken(token);
                    Integer userId = claims.get("userId", Integer.class);
                    Integer roleId = claims.get("roleId", Integer.class);
                    String roleCode = claims.get("roleCode", String.class);
                    String email = claims.get("email", String.class);

                    req.setAttribute("currentUserId", userId);
                    req.setAttribute("currentRoleId", roleId);
                    req.setAttribute("currentRoleCode", roleCode != null ? roleCode : (roleId != null && roleId == 1 ? "ADMIN" : "CUSTOMER"));
                    req.setAttribute("currentUserEmail", email);
                }
            } catch (Exception ignored) {
                // Invalid token for optional auth routes is gracefully ignored
            }
        }

        chain.doFilter(request, response);
    }
}
