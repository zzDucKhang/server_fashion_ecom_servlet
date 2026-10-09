package com.fashionshop.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fashionshop.business.entity.Role;
import com.fashionshop.business.entity.User;
import com.fashionshop.business.entity.enums.UserStatus;
import com.fashionshop.common.response.ApiResponse;
import com.fashionshop.dao.UserDAO;
import com.fashionshop.dto.auth.RegisterRequest;
import com.fashionshop.dto.auth.UserResponse;
import com.fashionshop.util.PasswordUtil;
import jakarta.persistence.PersistenceException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * Controller tiếp nhận và xử lý yêu cầu Đăng ký (Register) tài khoản người dùng mới.
 * URL Mapping: /api/auth/register
 */
@WebServlet(name = "AuthServlet", urlPatterns = {"/api/auth/register"})
public class AuthServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(AuthServlet.class.getName());
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    private final ObjectMapper objectMapper;
    private final UserDAO userDAO;

    public AuthServlet() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.userDAO = new UserDAO();
    }

    // Constructor phục vụ Dependency Injection khi viết Unit Test
    public AuthServlet(UserDAO userDAO, ObjectMapper objectMapper) {
        this.userDAO = userDAO;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");

        RegisterRequest registerReq;
        try {
            registerReq = objectMapper.readValue(req.getInputStream(), RegisterRequest.class);
        } catch (JsonProcessingException ex) {
            writeErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "Định dạng JSON gửi lên không hợp lệ.");
            return;
        }

        // 1. Kiểm tra tính hợp lệ của dữ liệu đầu vào (Input Validation)
        String validationError = validateRegisterInput(registerReq);
        if (validationError != null) {
            writeErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, validationError);
            return;
        }

        String email = registerReq.getEmail().trim().toLowerCase();

        // 2. Kiểm tra xem Email đã tồn tại trong hệ thống chưa
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

        // 3. Băm mật khẩu sử dụng BCrypt (Work Factor = 12)
        String hashedPassword = PasswordUtil.hashPassword(registerReq.getPassword());

        // 4. Khởi tạo User và gán Role mặc định CUSTOMER, trạng thái ACTIVE
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

        // 5. Lưu User vào Database có bọc Transaction
        try {
            User savedUser = userDAO.saveUser(newUser);

            // 6. Trả về kết quả thành công HTTP 201 Created cùng dữ liệu an toàn (không kèm passwordHash)
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

    private void writeErrorResponse(HttpServletResponse resp, int statusCode, String message) throws IOException {
        resp.setStatus(statusCode);
        objectMapper.writeValue(resp.getOutputStream(), ApiResponse.error(statusCode, message));
    }
}
