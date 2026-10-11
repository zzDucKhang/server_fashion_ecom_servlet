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

        int status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        String message = root.getMessage() != null ? root.getMessage() : "Internal Server Error";

        if (root instanceof AppException appEx) {
            status = appEx.getStatusCode();
        }

        JsonUtil.sendJson(res, status, ApiResponse.error(status, message));
    }
}