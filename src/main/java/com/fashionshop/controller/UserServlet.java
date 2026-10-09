package com.fashionshop.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fashionshop.business.entity.ShippingAddress;
import com.fashionshop.business.entity.User;
import com.fashionshop.common.exception.AppException;
import com.fashionshop.common.exception.BadRequestException;
import com.fashionshop.common.exception.NotFoundException;
import com.fashionshop.common.exception.UnauthorizedException;
import com.fashionshop.common.response.ApiResponse;
import com.fashionshop.dao.DeviceSessionDAO;
import com.fashionshop.dao.ShippingAddressDAO;
import com.fashionshop.dao.UserDAO;
import com.fashionshop.dto.address.ShippingAddressRequest;
import com.fashionshop.dto.address.ShippingAddressResponse;
import com.fashionshop.dto.auth.ChangePasswordRequest;
import com.fashionshop.dto.auth.UserResponse;
import com.fashionshop.util.PasswordUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller phụ trách các nghiệp vụ quản lý tài khoản người dùng và sổ địa chỉ giao hàng (Identity & Security Module).
 * Chạy an toàn phía sau {@link com.fashionshop.filter.JwtAuthenticationFilter}.
 *
 * URL Mappings (/api/user/*):
 * - GET    /api/user/profile                  : Lấy thông tin hồ sơ cá nhân
 * - POST   /api/user/change-password          : Đổi mật khẩu & thu hồi toàn bộ session thiết bị
 * - GET    /api/user/addresses                : Lấy danh sách sổ địa chỉ của user
 * - POST   /api/user/addresses                : Thêm mới một địa chỉ giao hàng
 * - PUT    /api/user/addresses/{id}           : Cập nhật thông tin địa chỉ (chống IDOR)
 * - PATCH  /api/user/addresses/{id}/default   : Đặt địa chỉ làm mặc định
 * - DELETE /api/user/addresses/{id}           : Xóa địa chỉ giao hàng (chống IDOR)
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
    private final ShippingAddressDAO shippingAddressDAO;

    public UserServlet() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.userDAO = new UserDAO();
        this.deviceSessionDAO = new DeviceSessionDAO();
        this.shippingAddressDAO = new ShippingAddressDAO();
    }

    public UserServlet(UserDAO userDAO, DeviceSessionDAO deviceSessionDAO, ObjectMapper objectMapper) {
        this(userDAO, deviceSessionDAO, new ShippingAddressDAO(), objectMapper);
    }

    public UserServlet(UserDAO userDAO, DeviceSessionDAO deviceSessionDAO, ShippingAddressDAO shippingAddressDAO, ObjectMapper objectMapper) {
        this.userDAO = userDAO;
        this.deviceSessionDAO = deviceSessionDAO;
        this.shippingAddressDAO = shippingAddressDAO;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if ("PATCH".equalsIgnoreCase(req.getMethod())) {
            doPatch(req, resp);
        } else {
            super.service(req, resp);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String path = normalizePath(req.getPathInfo());
        String[] segments = path.isEmpty() ? new String[0] : path.split("/");

        try {
            Integer userId = extractUserId(req);

            if (segments.length == 0 || (segments.length == 1 && "profile".equals(segments[0]))) {
                handleGetProfile(resp, userId);
            } else if (segments.length == 1 && "addresses".equals(segments[0])) {
                handleGetAddresses(resp, userId);
            } else if (segments.length == 2 && "addresses".equals(segments[0])) {
                int addressId = parseId(segments[1], "địa chỉ");
                handleGetAddressById(resp, userId, addressId);
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
        String[] segments = path.isEmpty() ? new String[0] : path.split("/");

        try {
            Integer userId = extractUserId(req);

            if (segments.length == 1 && "change-password".equals(segments[0])) {
                handleChangePassword(req, resp, userId);
            } else if (segments.length == 1 && "addresses".equals(segments[0])) {
                handleCreateAddress(req, resp, userId);
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

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String path = normalizePath(req.getPathInfo());
        String[] segments = path.isEmpty() ? new String[0] : path.split("/");

        try {
            Integer userId = extractUserId(req);

            if (segments.length == 2 && "addresses".equals(segments[0])) {
                int addressId = parseId(segments[1], "địa chỉ");
                handleUpdateAddress(req, resp, userId, addressId);
            } else {
                writeErrorResponse(resp, HttpServletResponse.SC_NOT_FOUND,
                        "Không tìm thấy endpoint yêu cầu: " + req.getRequestURI());
            }
        } catch (AppException ex) {
            writeErrorResponse(resp, ex.getStatusCode(), ex.getMessage());
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Lỗi không xác định khi xử lý PUT /api/user: " + ex.getMessage(), ex);
            writeErrorResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Đã xảy ra lỗi hệ thống. Vui lòng thử lại sau.");
        }
    }

    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String path = normalizePath(req.getPathInfo());
        String[] segments = path.isEmpty() ? new String[0] : path.split("/");

        try {
            Integer userId = extractUserId(req);

            if (segments.length >= 2 && "addresses".equals(segments[0])) {
                int addressId = parseId(segments[1], "địa chỉ");
                handleSetDefaultAddress(resp, userId, addressId);
            } else {
                writeErrorResponse(resp, HttpServletResponse.SC_NOT_FOUND,
                        "Không tìm thấy endpoint yêu cầu: " + req.getRequestURI());
            }
        } catch (AppException ex) {
            writeErrorResponse(resp, ex.getStatusCode(), ex.getMessage());
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Lỗi không xác định khi xử lý PATCH /api/user: " + ex.getMessage(), ex);
            writeErrorResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Đã xảy ra lỗi hệ thống. Vui lòng thử lại sau.");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String path = normalizePath(req.getPathInfo());
        String[] segments = path.isEmpty() ? new String[0] : path.split("/");

        try {
            Integer userId = extractUserId(req);

            if (segments.length == 2 && "addresses".equals(segments[0])) {
                int addressId = parseId(segments[1], "địa chỉ");
                handleDeleteAddress(resp, userId, addressId);
            } else {
                writeErrorResponse(resp, HttpServletResponse.SC_NOT_FOUND,
                        "Không tìm thấy endpoint yêu cầu: " + req.getRequestURI());
            }
        } catch (AppException ex) {
            writeErrorResponse(resp, ex.getStatusCode(), ex.getMessage());
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Lỗi không xác định khi xử lý DELETE /api/user: " + ex.getMessage(), ex);
            writeErrorResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Đã xảy ra lỗi hệ thống. Vui lòng thử lại sau.");
        }
    }

    // ==========================================
    // CÁC HÀM XỬ LÝ NGHIỆP VỤ (HANDLERS)
    // ==========================================

    /**
     * Lấy thông tin cá nhân của User hiện tại (GET /api/user/profile).
     */
    private void handleGetProfile(HttpServletResponse resp, Integer userId) throws IOException {
        User user = userDAO.findById(userId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy thông tin người dùng với ID: " + userId));

        UserResponse userResponse = UserResponse.fromEntity(user);
        resp.setStatus(HttpServletResponse.SC_OK);
        objectMapper.writeValue(resp.getOutputStream(), ApiResponse.ok(userResponse, "Lấy thông tin người dùng thành công."));
    }

    /**
     * Đổi mật khẩu người dùng (POST /api/user/change-password).
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

        User user = userDAO.findById(userId)
                .orElseThrow(() -> new NotFoundException("Người dùng không tồn tại trong hệ thống."));

        if (!PasswordUtil.checkPassword(oldPassword, user.getPassword())) {
            throw new BadRequestException("Sai mật khẩu cũ.");
        }

        String hashedNewPassword = PasswordUtil.hashPassword(newPassword.trim());
        user.setPassword(hashedNewPassword);
        userDAO.update(user);

        // Bắt buộc kick toàn bộ session
        deviceSessionDAO.revokeAllSessions(userId);

        LOGGER.info("User ID " + userId + " đã đổi mật khẩu thành công. Đã thu hồi toàn bộ session.");

        resp.setStatus(HttpServletResponse.SC_OK);
        objectMapper.writeValue(
                resp.getOutputStream(),
                ApiResponse.ok(null, "Đổi mật khẩu thành công. Vui lòng đăng nhập lại.")
        );
    }

    /**
     * Lấy danh sách địa chỉ giao hàng của User hiện tại (GET /api/user/addresses).
     */
    private void handleGetAddresses(HttpServletResponse resp, Integer userId) throws IOException {
        List<ShippingAddress> addresses = shippingAddressDAO.findByUserId(userId);
        List<ShippingAddressResponse> responseList = addresses.stream()
                .map(ShippingAddressResponse::fromEntity)
                .toList();

        resp.setStatus(HttpServletResponse.SC_OK);
        objectMapper.writeValue(
                resp.getOutputStream(),
                ApiResponse.ok(responseList, "Lấy danh sách địa chỉ giao hàng thành công.")
        );
    }

    /**
     * Lấy chi tiết một địa chỉ giao hàng cụ thể (GET /api/user/addresses/{id}).
     */
    private void handleGetAddressById(HttpServletResponse resp, Integer userId, int addressId) throws IOException {
        ShippingAddress address = shippingAddressDAO.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new NotFoundException("Địa chỉ giao hàng không tồn tại hoặc bạn không có quyền truy cập."));

        resp.setStatus(HttpServletResponse.SC_OK);
        objectMapper.writeValue(
                resp.getOutputStream(),
                ApiResponse.ok(ShippingAddressResponse.fromEntity(address), "Lấy chi tiết địa chỉ giao hàng thành công.")
        );
    }

    /**
     * Thêm mới địa chỉ giao hàng (POST /api/user/addresses).
     */
    private void handleCreateAddress(HttpServletRequest req, HttpServletResponse resp, Integer userId) throws IOException {
        ShippingAddressRequest addressReq;
        try {
            addressReq = objectMapper.readValue(req.getInputStream(), ShippingAddressRequest.class);
        } catch (JsonProcessingException ex) {
            throw new BadRequestException("Định dạng dữ liệu JSON không hợp lệ.");
        }

        validateAddressRequest(addressReq);

        User user = userDAO.findById(userId)
                .orElseThrow(() -> new NotFoundException("Người dùng không tồn tại trong hệ thống."));

        boolean isDefault = Boolean.TRUE.equals(addressReq.getIsDefault());

        // Nếu user chọn isDefault = true, gọi DAO reset các địa chỉ cũ về false trước khi save
        if (isDefault) {
            shippingAddressDAO.resetDefaultAddress(userId);
        }

        ShippingAddress address = new ShippingAddress();
        address.setUser(user);
        address.setReceiverName(addressReq.getReceiverName().trim());
        address.setPhoneNumber(addressReq.getPhone().trim());
        address.setProvince(addressReq.getCity().trim());
        address.setDistrict(addressReq.getDistrict().trim());
        address.setWard(addressReq.getWard().trim());
        address.setDetailAddress(addressReq.getStreet().trim());
        address.setIsDefault(isDefault);

        ShippingAddress saved = shippingAddressDAO.save(address);

        resp.setStatus(HttpServletResponse.SC_CREATED);
        objectMapper.writeValue(
                resp.getOutputStream(),
                ApiResponse.created(ShippingAddressResponse.fromEntity(saved), "Thêm mới địa chỉ giao hàng thành công.")
        );
    }

    /**
     * Cập nhật địa chỉ giao hàng (PUT /api/user/addresses/{id}).
     * Bắt buộc dùng findByIdAndUserId để chống lỗ hổng bảo mật IDOR.
     */
    private void handleUpdateAddress(HttpServletRequest req, HttpServletResponse resp, Integer userId, int addressId) throws IOException {
        ShippingAddress address = shippingAddressDAO.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new NotFoundException("Địa chỉ giao hàng không tồn tại hoặc bạn không có quyền truy cập."));

        ShippingAddressRequest addressReq;
        try {
            addressReq = objectMapper.readValue(req.getInputStream(), ShippingAddressRequest.class);
        } catch (JsonProcessingException ex) {
            throw new BadRequestException("Định dạng dữ liệu JSON không hợp lệ.");
        }

        validateAddressRequest(addressReq);

        boolean isNewDefault = Boolean.TRUE.equals(addressReq.getIsDefault());
        if (isNewDefault && !Boolean.TRUE.equals(address.getIsDefault())) {
            shippingAddressDAO.resetDefaultAddress(userId);
            address.setIsDefault(true);
        } else if (addressReq.getIsDefault() != null) {
            address.setIsDefault(addressReq.getIsDefault());
        }

        address.updateAddress(
                addressReq.getReceiverName().trim(),
                addressReq.getPhone().trim(),
                addressReq.getCity().trim(),
                addressReq.getDistrict().trim(),
                addressReq.getWard().trim(),
                addressReq.getStreet().trim()
        );

        ShippingAddress updated = shippingAddressDAO.update(address);

        resp.setStatus(HttpServletResponse.SC_OK);
        objectMapper.writeValue(
                resp.getOutputStream(),
                ApiResponse.ok(ShippingAddressResponse.fromEntity(updated), "Cập nhật địa chỉ giao hàng thành công.")
        );
    }

    /**
     * Đặt một địa chỉ làm mặc định (PATCH /api/user/addresses/{id}/default).
     * Bắt buộc dùng findByIdAndUserId kiểm tra quyền sở hữu và reset các địa chỉ cũ về false.
     */
    private void handleSetDefaultAddress(HttpServletResponse resp, Integer userId, int addressId) throws IOException {
        ShippingAddress address = shippingAddressDAO.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new NotFoundException("Địa chỉ giao hàng không tồn tại hoặc bạn không có quyền truy cập."));

        // Reset các địa chỉ cũ về false
        shippingAddressDAO.resetDefaultAddress(userId);

        address.setIsDefault(true);
        ShippingAddress updated = shippingAddressDAO.update(address);

        resp.setStatus(HttpServletResponse.SC_OK);
        objectMapper.writeValue(
                resp.getOutputStream(),
                ApiResponse.ok(ShippingAddressResponse.fromEntity(updated), "Đặt địa chỉ mặc định thành công.")
        );
    }

    /**
     * Xóa một địa chỉ giao hàng (DELETE /api/user/addresses/{id}).
     * Bắt buộc kiểm tra quyền sở hữu bằng findByIdAndUserId trước khi xóa.
     */
    private void handleDeleteAddress(HttpServletResponse resp, Integer userId, int addressId) throws IOException {
        ShippingAddress address = shippingAddressDAO.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new NotFoundException("Địa chỉ giao hàng không tồn tại hoặc bạn không có quyền truy cập."));

        shippingAddressDAO.delete(address);

        resp.setStatus(HttpServletResponse.SC_OK);
        objectMapper.writeValue(
                resp.getOutputStream(),
                ApiResponse.ok(null, "Xóa địa chỉ giao hàng thành công.")
        );
    }

    // ==========================================
    // CÁC HÀM TIỆN ÍCH HỖ TRỢ (HELPERS)
    // ==========================================

    private void validateAddressRequest(ShippingAddressRequest req) {
        if (req == null) {
            throw new BadRequestException("Dữ liệu địa chỉ giao hàng không được để trống.");
        }
        if (req.getReceiverName() == null || req.getReceiverName().isBlank()) {
            throw new BadRequestException("Tên người nhận (receiverName) không được để trống.");
        }
        if (req.getPhone() == null || req.getPhone().isBlank()) {
            throw new BadRequestException("Số điện thoại người nhận (phone) không được để trống.");
        }
        if (req.getCity() == null || req.getCity().isBlank()) {
            throw new BadRequestException("Tỉnh/Thành phố (city) không được để trống.");
        }
        if (req.getDistrict() == null || req.getDistrict().isBlank()) {
            throw new BadRequestException("Quận/Huyện (district) không được để trống.");
        }
        if (req.getWard() == null || req.getWard().isBlank()) {
            throw new BadRequestException("Phường/Xã (ward) không được để trống.");
        }
        if (req.getStreet() == null || req.getStreet().isBlank()) {
            throw new BadRequestException("Địa chỉ chi tiết (street) không được để trống.");
        }
    }

    private int parseId(String idStr, String paramName) {
        if (idStr == null || idStr.isBlank()) {
            throw new BadRequestException("Mã " + paramName + " không được để trống.");
        }
        try {
            int id = Integer.parseInt(idStr.trim());
            if (id <= 0) {
                throw new BadRequestException("Mã " + paramName + " phải là số nguyên dương hợp lệ.");
            }
            return id;
        } catch (NumberFormatException ex) {
            throw new BadRequestException("Mã " + paramName + " không hợp lệ: " + idStr);
        }
    }

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

    private void writeErrorResponse(HttpServletResponse resp, int statusCode, String message) throws IOException {
        resp.setStatus(statusCode);
        resp.setContentType("application/json;charset=UTF-8");
        objectMapper.writeValue(resp.getOutputStream(), ApiResponse.error(statusCode, message));
    }
}
