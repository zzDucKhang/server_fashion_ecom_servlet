package com.fashionshop.presentation.controller.identity;

import com.fashionshop.business.service.identity.AuthService;
import com.fashionshop.common.exception.NotFoundException;
import com.fashionshop.common.util.ValidationUtil;
import com.fashionshop.presentation.controller.base.BaseHttpServlet;
import com.fashionshop.presentation.dto.identity.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

@WebServlet(urlPatterns = {
    "/api/v1/identity/auth/*",
    "/api/identity/auth/*",
    "/api/auth/*"
})
public class AuthServlet extends BaseHttpServlet {

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
        RegisterDTO dto = parseBody(req, RegisterDTO.class);
        ValidationUtil.validate(dto);
        sendJson(resp, HttpServletResponse.SC_CREATED, Map.of("statusCode", 201, "data", authService.register(dto)));
    }

    private void handleLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        LoginDTO dto = parseBody(req, LoginDTO.class);
        ValidationUtil.validate(dto);
        sendJson(resp, HttpServletResponse.SC_OK, Map.of("statusCode", 200, "data", authService.login(dto, getClientIp(req))));
    }

    private void handleRefresh(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        RefreshTokenDTO dto = parseBody(req, RefreshTokenDTO.class);
        ValidationUtil.validate(dto);
        sendJson(resp, HttpServletResponse.SC_OK, Map.of("statusCode", 200, "data", authService.refreshToken(dto, getClientIp(req))));
    }

    private void handleForgotPassword(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ForgotPasswordDTO dto = parseBody(req, ForgotPasswordDTO.class);
        ValidationUtil.validate(dto);
        sendJson(resp, HttpServletResponse.SC_OK, Map.of("statusCode", 200, "data", authService.forgotPassword(dto)));
    }

    private void handleVerifyCode(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        VerifyOtpDTO dto = parseBody(req, VerifyOtpDTO.class);
        ValidationUtil.validate(dto);
        sendJson(resp, HttpServletResponse.SC_OK, Map.of("statusCode", 200, "data", authService.verifyCode(dto)));
    }

    private void handleResetPassword(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ResetPasswordDTO dto = parseBody(req, ResetPasswordDTO.class);
        ValidationUtil.validate(dto);
        sendJson(resp, HttpServletResponse.SC_OK, Map.of("statusCode", 200, "data", authService.resetPassword(dto)));
    }

    private void handleChangePassword(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ChangePasswordDTO dto = parseBody(req, ChangePasswordDTO.class);
        ValidationUtil.validate(dto);
        sendJson(resp, HttpServletResponse.SC_OK, Map.of("statusCode", 200, "data", authService.changePassword(dto, getUserId(req))));
    }

    private void handleLogout(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String authHeader = req.getHeader("Authorization");
        String accessToken = (authHeader != null && authHeader.startsWith("Bearer "))
                ? authHeader.substring(7).trim() : null;

        String refreshToken = null;
        try {
            RefreshTokenDTO dto = parseBody(req, RefreshTokenDTO.class);
            if (dto != null) {
                refreshToken = dto.getRefreshToken();
            }
        } catch (Exception ignored) {
        }

        sendJson(resp, HttpServletResponse.SC_OK, Map.of("statusCode", 200, "data", authService.logout(accessToken, refreshToken)));
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
