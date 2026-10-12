# Project Dump

**Root:** `C:\Programing\Lap_trinh_Web\fashion_ecom\src\main\java\com\fashionshop\presentation\filter`

**Generated:** 2026-10-12 17:14:50.176491

**Files:** 4 | **Total size:** 7,658 bytes

---

# AuthFilter.java

| Property | Value |
|----------|-------|
| Path | `AuthFilter.java` |
| Size | 2,891 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 15:23:01.412542 |

```java
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
```

# CorsFilter.java

| Property | Value |
|----------|-------|
| Path | `CorsFilter.java` |
| Size | 1,464 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:40.545787 |

```java
package com.fashionshop.presentation.filter;

import com.fashionshop.config.AppConfig;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * CorsFilter
 * Global CORS headers and preflight handling are handled by GlobalExceptionFilter
 * on "/*" to ensure consistent CORS headers even on exception responses.
 * This class provides a dedicated implementation if configured in web.xml.
 */
public class CorsFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        res.setHeader("Access-Control-Allow-Origin", AppConfig.CLIENT_CORS_URL);
        res.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, PATCH, DELETE, OPTIONS");
        res.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, x-session-id");
        res.setHeader("Access-Control-Allow-Credentials", "true");
        res.setHeader("Access-Control-Max-Age", "3600");

        if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
            res.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        chain.doFilter(request, response);
    }
}

```

# GlobalExceptionFilter.java

| Property | Value |
|----------|-------|
| Path | `GlobalExceptionFilter.java` |
| Size | 2,555 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:40.546788 |

```java
package com.fashionshop.presentation.filter;

import com.fashionshop.common.exception.AppException;
import com.fashionshop.common.response.ApiResponse;
import com.fashionshop.common.util.JsonUtil;
import com.fashionshop.config.AppConfig;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebFilter("/*")
public class GlobalExceptionFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        res.setHeader("Access-Control-Allow-Origin", AppConfig.CLIENT_CORS_URL);
        res.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, PATCH, DELETE, OPTIONS");
        res.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, x-session-id");
        res.setHeader("Access-Control-Allow-Credentials", "true");
        res.setHeader("Access-Control-Max-Age", "3600");

        if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
            res.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        try {
            chain.doFilter(request, response);
        } catch (Exception ex) {
            handleException(ex, res);
        }
    }

    private void handleException(Throwable ex, HttpServletResponse res) throws IOException {
        Throwable root = ex;
        while (root.getCause() != null && !(root instanceof AppException)) {
            root = root.getCause();
        }

        if (root instanceof com.fashionshop.common.exception.ValidationException valEx) {
            java.util.Map<String, Object> body = new java.util.LinkedHashMap<>();
            body.put("statusCode", 422);
            body.put("message", valEx.getMessage());
            body.put("errors", valEx.getErrors());
            JsonUtil.sendJson(res, 422, body);
            return;
        }

        int status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        String message = root.getMessage() != null ? root.getMessage() : "Internal Server Error";

        if (root instanceof AppException appEx) {
            status = appEx.getStatusCode();
        }

        JsonUtil.sendJson(res, status, ApiResponse.error(status, message));
    }
}
```

# RoleFilter.java

| Property | Value |
|----------|-------|
| Path | `RoleFilter.java` |
| Size | 748 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:40.547787 |

```java
package com.fashionshop.presentation.filter;

import jakarta.servlet.*;

import java.io.IOException;

/**
 * RoleFilter (RBAC Filter)
 * Note: Fine-grained Role-Based Access Control in this module is enforced directly
 * via SecurityGuard.requireAdmin(req) and SecurityGuard.requireAuth(req) inside
 * each Servlet endpoint to allow flexible per-route permissions (e.g. self vs admin).
 * This class is reserved for declarative URL-pattern RBAC middleware extensions.
 */
public class RoleFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        chain.doFilter(request, response);
    }
}

```

