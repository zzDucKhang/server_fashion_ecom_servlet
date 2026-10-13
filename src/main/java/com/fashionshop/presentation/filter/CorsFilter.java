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

        String origin = req.getHeader("Origin");
        if (origin != null && !origin.isBlank()) {
            res.setHeader("Access-Control-Allow-Origin", origin);
            res.setHeader("Vary", "Origin");
        } else {
            res.setHeader("Access-Control-Allow-Origin", AppConfig.CLIENT_CORS_URL != null ? AppConfig.CLIENT_CORS_URL : "*");
        }
        res.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, PATCH, DELETE, OPTIONS, HEAD");
        res.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, x-session-id, x-sepay-signature, x-sepay-timestamp, Accept, Origin, X-Requested-With");
        res.setHeader("Access-Control-Allow-Credentials", "true");
        res.setHeader("Access-Control-Max-Age", "3600");

        if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
            res.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        chain.doFilter(request, response);
    }
}
