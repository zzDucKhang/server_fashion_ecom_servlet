package com.fashionshop.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fashionshop.business.entity.User;
import com.fashionshop.common.exception.AppException;
import com.fashionshop.common.exception.BadRequestException;
import com.fashionshop.common.exception.NotFoundException;
import com.fashionshop.common.exception.UnauthorizedException;
import com.fashionshop.common.response.ApiResponse;
import com.fashionshop.dao.DeviceSessionDAO;
import com.fashionshop.dao.UserDAO;
import com.fashionshop.dto.auth.ChangePasswordRequest;
import com.fashionshop.dto.auth.UserResponse;
import com.fashionshop.util.PasswordUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller phụ trách các nghiệp vụ quản lý tài khoản người dùng (User Profile & Account Security).
 * Chạy phía sau {@link com.fashionshop.filter.JwtAuthenticationFilter}.
 *
 * URL Mappings (/api/user/*):
 * - GET  /api/user/profile          : Lấy thông tin hồ sơ người dùng (loại trừ tuyệt đối mật khẩu)
 * - POST /api/user/change-password  : Đổi mật khẩu, cập nhật DB và thu hồi toàn bộ phiên đăng nhập thiết bị
 */
@WebServlet(
    name = "UserServlet",
    urlPatterns = {
        "/api/user/*"
    }
)
public class UserServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(UserServlet.class.getName());

    private final ObjectMapper objectMapper;
    private final UserDAO userDAO;
    private final DeviceSessionDAO deviceSessionDAO;

    public UserServlet() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.userDAO = new UserDAO();
        this.deviceSessionDAO = new DeviceSessionDAO();
    }

    // Constructor phục vụ Dependency Injection khi viết Unit Test
    public UserServlet(UserDAO userDAO, DeviceSessionDAO deviceSessionDAO, ObjectMapper objectMapper) {
        this.userDAO = userDAO;
        this.deviceSessionDAO = deviceSessionDAO;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String path = normalizePath(req.getPathInfo());

        try {
            Integer userId = extractUserId(req);

            if ("profile".equals(path) || "".equals(path)) {
                handleGetProfile(resp, userId);
            } else {
                writeErrorResponse(resp, HttpServletResponse.SC_NOT_FOUND,
                        "Không tìm thấy endpoint yêu cầu: " + req.getRequestURI());
            }
        } catch (AppException ex) {
            writeErrorResponse(resp, ex.getStatusCode(), ex.getMessage());
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Lỗi không xác định khi xử lý GET /api/user: " + ex.getMessage(), ex);
            writeErrorResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Đã xảy ra lỗi hệ thống. Vui lòng thử lại sau.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String path = normalizePath(req.getPathInfo());

        try {
            Integer userId = extractUserId(req);

            if ("change-password".equals(path)) {
                handleChangePassword(req, resp, userId);
            } else {
                writeErrorResponse(resp, HttpServletResponse.SC_NOT_FOUND,
                        "Không tìm thấy endpoint yêu cầu: " + req.getRequestURI());
            }
        } catch (AppException ex) {
            writeErrorResponse(resp, ex.getStatusCode(), ex.getMessage());
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Lỗi không xác định khi xử lý POST /api/user: " + ex.getMessage(), ex);
            writeErrorResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Đã xảy ra lỗi hệ thống. Vui lòng thử lại sau.");
        }
    }

    /**
     * Xử lý truy vấn thông tin Profile của User hiện tại (GET /api/user/profile).
     */
    private void handleGetProfile(HttpServletResponse resp, Integer userId) throws IOException {
        User user = userDAO.findById(userId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy thông tin người dùng với ID: " + userId));

        UserResponse userResponse = UserResponse.fromEntity(user);

        resp.setStatus(HttpServletResponse.SC_OK);
        objectMapper.writeValue(resp.getOutputStream(), ApiResponse.ok(userResponse, "Lấy thông tin người dùng thành công."));
    }

    /**
     * Xử lý đổi mật khẩu người dùng (POST /api/user/change-password).
     * Bắt buộc băm mật khẩu mới bằng BCrypt và thu hồi TOÀN BỘ phiên thiết bị qua DeviceSessionDAO.
     */
    private void handleChangePassword(HttpServletRequest req, HttpServletResponse resp, Integer userId) throws IOException {
        ChangePasswordRequest changeReq;
        try {
            changeReq = objectMapper.readValue(req.getInputStream(), ChangePasswordRequest.class);
        } catch (JsonProcessingException ex) {
            throw new BadRequestException("Định dạng dữ liệu JSON không hợp lệ.");
        }

        if (changeReq == null) {
            throw new BadRequestException("Dữ liệu yêu cầu đổi mật khẩu không được để trống.");
        }

        String oldPassword = changeReq.getOldPassword();
        String newPassword = changeReq.getNewPassword();

        if (oldPassword == null || oldPassword.isBlank()) {
            throw new BadRequestException("Mật khẩu hiện tại (oldPassword) không được để trống.");
        }

        if (newPassword == null || newPassword.isBlank()) {
            throw new BadRequestException("Mật khẩu mới (newPassword) không được để trống.");
        }

        if (newPassword.trim().length() < 6) {
            throw new BadRequestException("Mật khẩu mới phải có độ dài tối thiểu từ 6 ký tự trở lên.");
        }

        if (oldPassword.trim().equals(newPassword.trim())) {
            throw new BadRequestException("Mật khẩu mới không được trùng với mật khẩu cũ.");
        }

        // 1. Lấy thông tin User từ cơ sở dữ liệu
        User user = userDAO.findById(userId)
                .orElseThrow(() -> new NotFoundException("Người dùng không tồn tại trong hệ thống."));

        // 2. Xác thực mật khẩu cũ bằng PasswordUtil.checkPassword
        if (!PasswordUtil.checkPassword(oldPassword, user.getPassword())) {
            throw new BadRequestException("Sai mật khẩu cũ.");
        }

        // 3. Băm mật khẩu mới bằng PasswordUtil.hashPassword (BCrypt cost factor = 12)
        String hashedNewPassword = PasswordUtil.hashPassword(newPassword.trim());
        user.setPassword(hashedNewPassword);
        userDAO.update(user);

        // 4. BẮT BUỘC: Thu hồi TOÀN BỘ session thiết bị của user để kick tất cả thiết bị
        deviceSessionDAO.revokeAllSessions(userId);

        LOGGER.info("User ID " + userId + " đã đổi mật khẩu thành công. Đã thu hồi toàn bộ session.");

        // 5. Trả về phản hồi JSON 200 OK
        resp.setStatus(HttpServletResponse.SC_OK);
        objectMapper.writeValue(
                resp.getOutputStream(),
                ApiResponse.ok(null, "Đổi mật khẩu thành công. Vui lòng đăng nhập lại.")
        );
    }

    /**
     * Trích xuất an toàn userId từ HttpServletRequest do JwtAuthenticationFilter gắn vào.
     */
    private Integer extractUserId(HttpServletRequest req) {
        Object userIdAttr = req.getAttribute("userId");
        if (userIdAttr == null) {
            throw new UnauthorizedException("Unauthorized: Không tìm thấy thông tin định danh người dùng trong phiên làm việc.");
        }

        if (userIdAttr instanceof Number number) {
            return number.intValue();
        }

        try {
            return Integer.parseInt(userIdAttr.toString().trim());
        } catch (NumberFormatException ex) {
            throw new UnauthorizedException("Unauthorized: Mã định danh người dùng không hợp lệ.");
        }
    }

    /**
     * Chuẩn hóa đường dẫn tương đối từ pathInfo (vd: "/profile/" -> "profile").
     */
    private String normalizePath(String pathInfo) {
        if (pathInfo == null) {
            return "";
        }
        String trimmed = pathInfo.trim();
        while (trimmed.startsWith("/")) {
            trimmed = trimmed.substring(1);
        }
        while (trimmed.endsWith("/")) {
            trimmed = trimmed.substring(0, trimmed.length() - 1);
        }
        return trimmed.toLowerCase();
    }

    /**
     * Ghi phản hồi lỗi có cấu trúc chuẩn ApiResponse vào response stream.
     */
    private void writeErrorResponse(HttpServletResponse resp, int statusCode, String message) throws IOException {
        resp.setStatus(statusCode);
        resp.setContentType("application/json;charset=UTF-8");
        objectMapper.writeValue(resp.getOutputStream(), ApiResponse.error(statusCode, message));
    }
}
