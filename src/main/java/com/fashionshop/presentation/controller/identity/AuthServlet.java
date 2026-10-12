package com.fashionshop.presentation.controller.identity;

import com.fashionshop.business.service.identity.AuthService;
import com.fashionshop.common.exception.NotFoundException;
import com.fashionshop.common.util.JsonUtil;
import com.fashionshop.common.util.SecurityGuard;
import com.fashionshop.common.util.ValidationUtil;
import com.fashionshop.presentation.dto.identity.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

@WebServlet("/api/v1/identity/auth/*")
public class AuthServlet extends HttpServlet {

    private final AuthService authService = new AuthService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null) {
            throw new NotFoundException("Đường dẫn không tồn tại");
        }

        switch (pathInfo) {
            case "/register" -> handleRegister(req, resp);
            case "/login" -> handleLogin(req, resp);
            case "/refresh" -> handleRefresh(req, resp);
            case "/forgot-password" -> handleForgotPassword(req, resp);
            case "/verify-code" -> handleVerifyCode(req, resp);
            case "/reset-password" -> handleResetPassword(req, resp);
            case "/change-password" -> handleChangePassword(req, resp);
            case "/logout" -> handleLogout(req, resp);
            default -> throw new NotFoundException("Đường dẫn không tồn tại: " + pathInfo);
        }
    }

    private void handleRegister(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        RegisterDTO dto = JsonUtil.fromJson(req, RegisterDTO.class);
        ValidationUtil.validate(dto);
        Map<String, Object> result = authService.register(dto);
        JsonUtil.sendJson(resp, HttpServletResponse.SC_CREATED, Map.of(
                "statusCode", 201,
                "data", result
        ));
    }

    private void handleLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        LoginDTO dto = JsonUtil.fromJson(req, LoginDTO.class);
        ValidationUtil.validate(dto);
        Map<String, Object> result = authService.login(dto, getClientIp(req));
        JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of(
                "statusCode", 200,
                "data", result
        ));
    }

    private void handleRefresh(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        RefreshTokenDTO dto = JsonUtil.fromJson(req, RefreshTokenDTO.class);
        ValidationUtil.validate(dto);
        Map<String, Object> result = authService.refreshToken(dto, getClientIp(req));
        JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of(
                "statusCode", 200,
                "data", result
        ));
    }

    private void handleForgotPassword(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ForgotPasswordDTO dto = JsonUtil.fromJson(req, ForgotPasswordDTO.class);
        ValidationUtil.validate(dto);
        Map<String, Object> result = authService.forgotPassword(dto);
        JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of(
                "statusCode", 200,
                "data", result
        ));
    }

    private void handleVerifyCode(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        VerifyOtpDTO dto = JsonUtil.fromJson(req, VerifyOtpDTO.class);
        ValidationUtil.validate(dto);
        Map<String, Object> result = authService.verifyCode(dto);
        JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of(
                "statusCode", 200,
                "data", result
        ));
    }

    private void handleResetPassword(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ResetPasswordDTO dto = JsonUtil.fromJson(req, ResetPasswordDTO.class);
        ValidationUtil.validate(dto);
        Map<String, Object> result = authService.resetPassword(dto);
        JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of(
                "statusCode", 200,
                "data", result
        ));
    }

    private void handleChangePassword(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer userId = SecurityGuard.requireAuth(req);
        ChangePasswordDTO dto = JsonUtil.fromJson(req, ChangePasswordDTO.class);
        ValidationUtil.validate(dto);
        Map<String, Object> result = authService.changePassword(dto, userId);
        JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of(
                "statusCode", 200,
                "data", result
        ));
    }

    private void handleLogout(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String authHeader = req.getHeader("Authorization");
        String accessToken = (authHeader != null && authHeader.startsWith("Bearer "))
                ? authHeader.substring(7).trim() : null;

        String refreshToken = null;
        try {
            RefreshTokenDTO dto = JsonUtil.fromJson(req, RefreshTokenDTO.class);
            if (dto != null) {
                refreshToken = dto.getRefreshToken();
            }
        } catch (Exception ignored) {
            // Body may be empty on logout
        }

        Map<String, Object> result = authService.logout(accessToken, refreshToken);
        JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of(
                "statusCode", 200,
                "data", result
        ));
    }

    private String getClientIp(HttpServletRequest req) {
        String xForwardedFor = req.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[0].trim();
        }
        String xRealIp = req.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isBlank()) {
            return xRealIp.trim();
        }
        return req.getRemoteAddr();
    }
}
