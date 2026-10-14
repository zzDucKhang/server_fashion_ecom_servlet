package com.fashionshop.presentation.controller.cartorder;

import com.fashionshop.business.entity.Order;
import com.fashionshop.business.entity.User;
import com.fashionshop.business.service.cartorder.OrderService;
import com.fashionshop.business.service.fulfillment.PaymentService;
import com.fashionshop.common.response.ApiResponse;
import com.fashionshop.common.response.PagedResponse;
import com.fashionshop.common.util.ValidationUtil;
import com.fashionshop.dataaccess.dao.identity.UserDAO;
import com.fashionshop.presentation.controller.base.BaseHttpServlet;
import com.fashionshop.presentation.dto.cartorder.CreateOrderDTO;
import com.fashionshop.presentation.dto.cartorder.OrderQueryDTO;
import com.fashionshop.presentation.dto.cartorder.UpdateOrderStatusDTO;
import com.fashionshop.presentation.dto.fulfillment.PaymentInfoDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@WebServlet(urlPatterns = {
    "/api/v1/cart-order/orders",
    "/api/v1/cart-order/orders/*",
    "/api/v1/orders",
    "/api/v1/orders/*",
    "/api/orders",
    "/api/orders/*"
})
public class OrderServlet extends BaseHttpServlet {

    private final OrderService orderService = new OrderService();
    private final PaymentService paymentService = new PaymentService();
    private final UserDAO userDAO = new UserDAO();

    /**
     * Kiểm tra quyền Admin/Staff chính xác từ cả JWT Token và Database (giống OrderController của Node.js)
     */
    private boolean checkAdminOrStaff(HttpServletRequest req, Integer userId) {
        if (isStaffOrAdmin(req)) {
            return true;
        }
        if (userId != null) {
            try {
                Optional<User> uOpt = userDAO.findByIdWithRole(userId);
                if (uOpt.isPresent() && uOpt.get().getRole() != null) {
                    String code = uOpt.get().getRole().getCode();
                    Integer roleId = uOpt.get().getRole().getId();
                    if ("ADMIN".equalsIgnoreCase(code) || "STAFF".equalsIgnoreCase(code)
                            || Integer.valueOf(1).equals(roleId) || Integer.valueOf(2).equals(roleId)) {
                        return true;
                    }
                }
            } catch (Exception ignored) {
            }
        }
        return false;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        Integer userId = getUserId(req);
        boolean isAdmin = checkAdminOrStaff(req, userId);

        // 1. GET /dashboard-stats (Admin / Staff dashboard statistics)
        if (pathInfo != null && (pathInfo.equals("/dashboard-stats") || pathInfo.equals("dashboard-stats"))) {
            if (!isAdmin) {
                sendError(resp, HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền xem thống kê");
                return;
            }
            Map<String, Object> stats = orderService.getDashboardStats();
            sendSuccess(resp, "Thống kê dashboard", stats);
            return;
        }

        // 2. GET /{orderId}/payment-details
        if (pathInfo != null && pathInfo.endsWith("/payment-details")) {
            Integer orderId = parseId(pathInfo);
            PaymentInfoDTO info = paymentService.getPaymentInfo(orderId, userId);
            Map<String, Object> details = new LinkedHashMap<>();
            details.put("qrUrl", info.getQrUrl());
            details.put("amount", info.getAmount());
            details.put("transferContent", info.getTransferContent());
            details.put("orderCode", info.getOrderCode());
            details.put("totalAmount", info.getAmount());
            details.put("paymentMethod", info.getPaymentMethod());
            sendSuccess(resp, "Lấy thông tin thanh toán thành công", details);
            return;
        }

        // 3. GET / - Danh sách đơn hàng
        if (pathInfo == null || "/".equals(pathInfo) || pathInfo.isBlank()) {
            OrderQueryDTO query = new OrderQueryDTO();
            query.setPage(getIntParameter(req, "page", 1));
            query.setLimit(getIntParameter(req, "limit", 10));
            query.setStatus(req.getParameter("status"));

            String allParam = req.getParameter("all");
            if (allParam != null) {
                query.setAll("true".equalsIgnoreCase(allParam.trim()) || "1".equals(allParam.trim()));
            }

            PagedResponse<Order> result = orderService.getOrders(userId, query, isAdmin);

            // Trả về cấu trúc chuẩn khớp 100% với Node.js OrderController:
            // { statusCode: 200, data: { data: [...], totalItems, page, limit, totalPages }, totalItems, page, limit, totalPages }
            Map<String, Object> innerData = new LinkedHashMap<>();
            innerData.put("data", result.getData());
            innerData.put("totalItems", result.getTotalItems());
            innerData.put("page", result.getPage());
            innerData.put("limit", result.getLimit());
            innerData.put("totalPages", result.getTotalPages());

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("statusCode", 200);
            response.put("message", "Lấy danh sách đơn hàng thành công");
            response.put("data", innerData);
            response.put("totalItems", result.getTotalItems());
            response.put("page", result.getPage());
            response.put("limit", result.getLimit());
            response.put("totalPages", result.getTotalPages());

            sendJson(resp, HttpServletResponse.SC_OK, response);
        } else {
            // 4. GET /{id} - Chi tiết đơn hàng
            Integer orderId = parseId(pathInfo);
            Order order = orderService.getOrderById(orderId, userId, isAdmin);
            sendSuccess(resp, "Chi tiết đơn hàng", order);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Integer userId = getUserId(req);

        String pathInfo = req.getPathInfo();
        if (pathInfo == null || "/".equals(pathInfo) || pathInfo.isBlank()) {
            CreateOrderDTO dto = parseBody(req, CreateOrderDTO.class);
            ValidationUtil.validate(dto);

            Order order = orderService.createOrder(userId, dto);
            sendJson(resp, HttpServletResponse.SC_CREATED, ApiResponse.success(201, "Tạo đơn hàng thành công", order));
        } else if (pathInfo.endsWith("/cancel")) {
            Integer orderId = parseId(pathInfo);
            Order cancelled = orderService.cancelOrder(orderId, userId);
            sendSuccess(resp, "Hủy đơn hàng thành công", cancelled);
        } else {
            sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Endpoint không tồn tại");
        }
    }

    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handleStatusUpdate(req, resp);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handleStatusUpdate(req, resp);
    }

    private void handleStatusUpdate(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer userId = getUserId(req);
        if (!checkAdminOrStaff(req, userId)) {
            sendError(resp, HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền thực hiện thao tác này");
            return;
        }

        String pathInfo = req.getPathInfo();
        if (pathInfo != null && pathInfo.endsWith("/status")) {
            Integer orderId = parseId(pathInfo);
            UpdateOrderStatusDTO dto = parseBody(req, UpdateOrderStatusDTO.class);
            ValidationUtil.validate(dto);
            Order updated = orderService.updateOrderStatus(orderId, dto.getStatus());
            sendSuccess(resp, "Cập nhật trạng thái thành công", updated);
        } else {
            sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Endpoint không tồn tại");
        }
    }
}