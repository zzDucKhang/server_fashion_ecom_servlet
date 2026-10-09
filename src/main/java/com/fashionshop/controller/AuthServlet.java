package com.fashionshop.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fashionshop.business.entity.DeviceSession;
import com.fashionshop.business.entity.Role;
import com.fashionshop.business.entity.User;
import com.fashionshop.business.entity.enums.UserStatus;
import com.fashionshop.common.exception.ForbiddenException;
import com.fashionshop.common.exception.UnauthorizedException;
import com.fashionshop.common.response.ApiResponse;
import com.fashionshop.dao.DeviceSessionDAO;
import com.fashionshop.dao.UserDAO;
import com.fashionshop.dto.auth.*;
import com.fashionshop.util.JwtUtil;
import com.fashionshop.util.PasswordUtil;
import jakarta.persistence.PersistenceException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * Controller tiếp nhận và xử lý toàn bộ vòng đời xác thực người dùng (Auth Lifecycle).
 * URL Mapping:
 * - POST /api/auth/register : Đăng ký tài khoản mới
 * - POST /api/auth/login    : Đăng nhập cấp Access Token (15p) & Refresh Token (7 ngày)
 * - POST /api/auth/refresh  : Cấp lại Access Token mới và xoay vòng Refresh Token
 * - POST /api/auth/logout   : Đăng xuất và thu hồi phiên đăng nhập DeviceSession
 */
@WebServlet(
    name = "AuthServlet",
    urlPatterns = {
        "/api/auth/register",
        "/api/auth/login",
        "/api/auth/refresh",
        "/api/auth/logout"
    }
)
public class AuthServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(AuthServlet.class.getName());
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    private final ObjectMapper objectMapper;
    private final UserDAO userDAO;
    private final DeviceSessionDAO deviceSessionDAO;

    public AuthServlet() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.userDAO = new UserDAO();
        this.deviceSessionDAO = new DeviceSessionDAO();
    }

    // Constructor phục vụ Dependency Injection khi viết Unit Test
    public AuthServlet(UserDAO userDAO, DeviceSessionDAO deviceSessionDAO, ObjectMapper objectMapper) {
        this.userDAO = userDAO;
        this.deviceSessionDAO = deviceSessionDAO;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String path = req.getServletPath();

        try {
            if ("/api/auth/login".equalsIgnoreCase(path)) {
                handleLogin(req, resp);
            } else if ("/api/auth/register".equalsIgnoreCase(path)) {
                handleRegister(req, resp);
            } else if ("/api/auth/refresh".equalsIgnoreCase(path)) {
                handleRefreshToken(req, resp);
            } else if ("/api/auth/logout".equalsIgnoreCase(path)) {
                handleLogout(req, resp);
            } else {
                writeErrorResponse(resp, HttpServletResponse.SC_NOT_FOUND, "Không tìm thấy endpoint yêu cầu.");
            }
        } catch (UnauthorizedException ex) {
            writeErrorResponse(resp, ex.getStatusCode(), ex.getMessage());
        } catch (ForbiddenException ex) {
            writeErrorResponse(resp, ex.getStatusCode(), ex.getMessage());
        }
    }

    /**
     * Xử lý luồng Đăng nhập (Login)
     */
    private void handleLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        LoginRequest loginReq;
        try {
            loginReq = objectMapper.readValue(req.getInputStream(), LoginRequest.class);
        } catch (JsonProcessingException ex) {
            writeErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "Định dạng JSON gửi lên không hợp lệ.");
            return;
        }

        if (loginReq == null || loginReq.getEmail() == null || loginReq.getEmail().isBlank()
                || loginReq.getPassword() == null || loginReq.getPassword().isBlank()) {
            throw new UnauthorizedException("Email và mật khẩu không được để trống.");
        }

        String email = loginReq.getEmail().trim().toLowerCase();

        // 1. Tìm User qua UserDAO. Nếu không tồn tại -> ném UnauthorizedException
        Optional<User> userOpt = userDAO.findByEmail(email);
        if (userOpt.isEmpty()) {
            throw new UnauthorizedException("Email hoặc mật khẩu không chính xác.");
        }

        User user = userOpt.get();

        // 2. Gọi PasswordUtil.checkPassword(). Nếu sai -> ném UnauthorizedException
        boolean passwordValid = PasswordUtil.checkPassword(loginReq.getPassword(), user.getPasswordHash());
        if (!passwordValid) {
            throw new UnauthorizedException("Email hoặc mật khẩu không chính xác.");
        }

        // 3. Kiểm tra trạng thái tài khoản
        if (user.getStatus() == UserStatus.BLOCKED) {
            throw new ForbiddenException("Tài khoản đã bị khóa. Vui lòng liên hệ quản trị viên.");
        }
        if (user.getStatus() == UserStatus.INACTIVE) {
            throw new ForbiddenException("Tài khoản chưa được kích hoạt.");
        }

        // 4. Tạo Access Token (15 phút) & Refresh Token (7 ngày)
        String accessToken = JwtUtil.generateAccessToken(user);
        String refreshToken = JwtUtil.generateRefreshToken(user);

        // 5. Tạo đối tượng DeviceSession và lưu thông tin phiên đăng nhập vào DB
        try {
            DeviceSession session = new DeviceSession();
            session.setUser(user);
            session.setRefreshToken(refreshToken);
            session.setIpAddress(getClientIp(req));
            session.setIsActive(true);
            session.setExpiresAt(LocalDateTime.now().plusDays(7));
            session.setCreatedAt(LocalDateTime.now());
            session.setUpdatedAt(LocalDateTime.now());

            deviceSessionDAO.saveSession(session);
        } catch (PersistenceException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lưu DeviceSession cho User ID: " + user.getId(), ex);
            writeErrorResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Lỗi cơ sở dữ liệu khi khởi tạo phiên đăng nhập.");
            return;
        }

        // 6. Trả về JSON Response chuẩn với status 200 OK
        UserResponse userResponse = UserResponse.fromEntity(user);
        LoginResponse loginResponse = new LoginResponse(accessToken, refreshToken, userResponse);

        resp.setStatus(HttpServletResponse.SC_OK);
        objectMapper.writeValue(resp.getOutputStream(), ApiResponse.ok(loginResponse, "Login successful"));
    }

    /**
     * Xử lý luồng Cấp lại Token (Refresh Token Rotation)
     */
    private void handleRefreshToken(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        RefreshTokenRequest refreshReq = null;
        try {
            refreshReq = objectMapper.readValue(req.getInputStream(), RefreshTokenRequest.class);
        } catch (Exception ignored) {
            // Cho phép fallback sang header hoặc cookie
        }

        String refreshToken = (refreshReq != null) ? refreshReq.getRefreshToken() : null;
        if (refreshToken == null || refreshToken.isBlank()) {
            refreshToken = req.getHeader("x-refresh-token");
        }
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new UnauthorizedException("Vui lòng cung cấp Refresh Token hợp lệ.");
        }

        refreshToken = refreshToken.trim();

        // 1. Tìm trong DeviceSessionDAO
        Optional<DeviceSession> sessionOpt = deviceSessionDAO.findByRefreshToken(refreshToken);
        if (sessionOpt.isEmpty()) {
            throw new UnauthorizedException("Phiên đăng nhập không tồn tại hoặc đã hết hạn. Vui lòng đăng nhập lại.");
        }

        DeviceSession session = sessionOpt.get();

        // 2. Kiểm tra tính hợp lệ và hạn dùng
        if (!Boolean.TRUE.equals(session.getIsActive())) {
            throw new UnauthorizedException("Phiên đăng nhập đã bị thu hồi. Vui lòng đăng nhập lại.");
        }
        if (session.isTokenExpired()) {
            deviceSessionDAO.revokeSession(refreshToken);
            throw new UnauthorizedException("Refresh Token đã hết hạn. Vui lòng đăng nhập lại.");
        }
        if (session.getUser().getStatus() == UserStatus.BLOCKED) {
            throw new ForbiddenException("Tài khoản của bạn đã bị khóa.");
        }

        // 3. Dùng JwtUtil tạo Access Token mới và xoay vòng Refresh Token mới
        String newAccessToken = JwtUtil.generateAccessToken(session.getUser());
        String newRefreshToken = JwtUtil.generateRefreshToken(session.getUser());
        LocalDateTime newExpiresAt = LocalDateTime.now().plusDays(7);

        session.refresh(newRefreshToken, newExpiresAt);

        try {
            deviceSessionDAO.updateSession(session);
        } catch (PersistenceException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi khi cập nhật DeviceSession lúc làm mới token", ex);
            writeErrorResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Lỗi cơ sở dữ liệu khi làm mới token.");
            return;
        }

        // 4. Trả về JSON chứa Access Token mới
        TokenRefreshResponse tokenResponse = new TokenRefreshResponse(
            newAccessToken,
            newRefreshToken,
            JwtUtil.ACCESS_TOKEN_EXPIRATION_MS / 1000
        );

        resp.setStatus(HttpServletResponse.SC_OK);
        objectMapper.writeValue(resp.getOutputStream(), ApiResponse.ok(tokenResponse, "Làm mới token thành công."));
    }

    /**
     * Xử lý luồng Đăng xuất (Logout)
     */
    private void handleLogout(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String tokenToRevoke = null;

        try {
            LogoutRequest logoutReq = objectMapper.readValue(req.getInputStream(), LogoutRequest.class);
            if (logoutReq != null && logoutReq.getRefreshToken() != null && !logoutReq.getRefreshToken().isBlank()) {
                tokenToRevoke = logoutReq.getRefreshToken().trim();
            }
        } catch (Exception ignored) {
        }

        if (tokenToRevoke == null || tokenToRevoke.isBlank()) {
            tokenToRevoke = req.getHeader("x-refresh-token");
        }
        if (tokenToRevoke == null || tokenToRevoke.isBlank()) {
            String authHeader = req.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                tokenToRevoke = authHeader.substring(7).trim();
            }
        }

        // Thu hồi session trong DB
        if (tokenToRevoke != null && !tokenToRevoke.isBlank()) {
            try {
                deviceSessionDAO.revokeSession(tokenToRevoke);
            } catch (PersistenceException ex) {
                LOGGER.log(Level.SEVERE, "Lỗi thu hồi session khi đăng xuất: " + ex.getMessage(), ex);
            }
        }

        resp.setStatus(HttpServletResponse.SC_OK);
        objectMapper.writeValue(resp.getOutputStream(), ApiResponse.ok(null, "Đăng xuất thành công."));
    }

    /**
     * Xử lý luồng Đăng ký (Register)
     */
    private void handleRegister(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        RegisterRequest registerReq;
        try {
            registerReq = objectMapper.readValue(req.getInputStream(), RegisterRequest.class);
        } catch (JsonProcessingException ex) {
            writeErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "Định dạng JSON gửi lên không hợp lệ.");
            return;
        }

        String validationError = validateRegisterInput(registerReq);
        if (validationError != null) {
            writeErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, validationError);
            return;
        }

        String email = registerReq.getEmail().trim().toLowerCase();

        try {
            Optional<User> existingUser = userDAO.findByEmail(email);
            if (existingUser.isPresent()) {
                writeErrorResponse(resp, HttpServletResponse.SC_CONFLICT, "Email đã được đăng ký trong hệ thống.");
                return;
            }
        } catch (PersistenceException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi kiểm tra email trùng lặp: " + email, ex);
            writeErrorResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Lỗi hệ thống khi kiểm tra thông tin tài khoản.");
            return;
        }

        String hashedPassword = PasswordUtil.hashPassword(registerReq.getPassword());

        Role defaultRole;
        try {
            defaultRole = userDAO.getOrCreateRole("CUSTOMER", "Khách hàng");
        } catch (PersistenceException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi truy xuất Role mặc định", ex);
            writeErrorResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Không thể gán quyền mặc định cho tài khoản.");
            return;
        }

        User newUser = new User();
        newUser.setFullName(registerReq.getFullName().trim());
        newUser.setEmail(email);
        newUser.setPhoneNumber(registerReq.getPhone().trim());
        newUser.setPasswordHash(hashedPassword);
        newUser.setStatus(UserStatus.ACTIVE);
        newUser.setRole(defaultRole);
        newUser.setRewardPoints(0);

        try {
            User savedUser = userDAO.saveUser(newUser);
            UserResponse userResponse = UserResponse.fromEntity(savedUser);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            objectMapper.writeValue(resp.getOutputStream(), ApiResponse.created(userResponse, "Đăng ký tài khoản thành công."));
        } catch (PersistenceException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi PersistenceException khi lưu người dùng mới: " + email, ex);
            writeErrorResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Lỗi cơ sở dữ liệu khi đăng ký tài khoản.");
        }
    }

    private String validateRegisterInput(RegisterRequest req) {
        if (req == null) {
            return "Payload yêu cầu không được để trống.";
        }
        if (req.getEmail() == null || req.getEmail().isBlank() || !EMAIL_PATTERN.matcher(req.getEmail().trim()).matches()) {
            return "Email không hợp lệ.";
        }
        if (req.getPassword() == null || req.getPassword().length() < 6) {
            return "Mật khẩu phải chứa ít nhất 6 ký tự.";
        }
        if (req.getFullName() == null || req.getFullName().isBlank()) {
            return "Họ và tên không được để trống.";
        }
        if (req.getPhone() == null || req.getPhone().isBlank()) {
            return "Số điện thoại không được để trống.";
        }
        return null;
    }

    private String getClientIp(HttpServletRequest req) {
        String ip = req.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank() || "unknown".equalsIgnoreCase(ip)) {
            ip = req.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isBlank() || "unknown".equalsIgnoreCase(ip)) {
            ip = req.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return (ip != null && !ip.isBlank()) ? ip : "127.0.0.1";
    }

    private void writeErrorResponse(HttpServletResponse resp, int statusCode, String message) throws IOException {
        resp.setStatus(statusCode);
        objectMapper.writeValue(resp.getOutputStream(), ApiResponse.error(statusCode, message));
    }
}
