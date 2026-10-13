package com.fashionshop.presentation.controller.cartorder;

import com.fashionshop.business.entity.Order;
import com.fashionshop.business.service.cartorder.OrderService;
import com.fashionshop.business.service.fulfillment.PaymentService;
import com.fashionshop.common.response.ApiResponse;
import com.fashionshop.common.response.PagedResponse;
import com.fashionshop.common.util.ValidationUtil;
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
import java.util.Map;

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

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();

        // 1. GET /dashboard-stats (Admin / Staff dashboard statistics)
        if (pathInfo != null && (pathInfo.equals("/dashboard-stats") || pathInfo.equals("dashboard-stats"))) {
            if (!isStaffOrAdmin(req)) {
                sendError(resp, HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền xem thống kê");
                return;
            }
            Map<String, Object> stats = orderService.getDashboardStats();
            sendSuccess(resp, "Thống kê dashboard", stats);
            return;
        }

        Integer userId = getUserId(req);
        if (userId == null) {
            sendError(resp, HttpServletResponse.SC_UNAUTHORIZED, "Chưa xác thực");
            return;
        }

        // 2. GET /{orderId}/payment-details
        if (pathInfo != null && pathInfo.endsWith("/payment-details")) {
            Integer orderId = parseId(pathInfo);
            PaymentInfoDTO info = paymentService.getPaymentInfo(orderId, userId);
            Map<String, Object> details = Map.of(
                    "qrUrl", info.getQrUrl(),
                    "amount", info.getAmount(),
                    "transferContent", info.getTransferContent(),
                    "orderCode", info.getOrderCode(),
                    "totalAmount", info.getAmount(),
                    "paymentMethod", info.getPaymentMethod()
            );
            sendSuccess(resp, "Lấy thông tin thanh toán thành công", details);
            return;
        }

        // 3. GET / - Danh sách đơn hàng
        if (pathInfo == null || "/".equals(pathInfo) || pathInfo.isBlank()) {
            OrderQueryDTO query = new OrderQueryDTO();
            query.setPage(getIntParameter(req, "page", 1));
            query.setLimit(getIntParameter(req, "limit", 10));
            query.setStatus(req.getParameter("status"));

            PagedResponse<Order> result = orderService.getOrders(userId, query);
            sendSuccess(resp, "Lấy danh sách đơn hàng thành công", result);
        } else {
            // 4. GET /{id} - Chi tiết đơn hàng
            Integer orderId = parseId(pathInfo);
            Order order = orderService.getOrderById(orderId, userId);
            sendSuccess(resp, "Chi tiết đơn hàng", order);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Integer userId = getUserId(req);
        if (userId == null) {
            sendError(resp, HttpServletResponse.SC_UNAUTHORIZED, "Chưa xác thực");
            return;
        }

        String pathInfo = req.getPathInfo();
        if (pathInfo == null || "/".equals(pathInfo) || pathInfo.isBlank()) {
            // POST / - Tạo đơn hàng mới
            CreateOrderDTO dto = parseBody(req, CreateOrderDTO.class);
            ValidationUtil.validate(dto);

            Order order = orderService.createOrder(userId, dto);
            sendJson(resp, HttpServletResponse.SC_CREATED, ApiResponse.success(201, "Tạo đơn hàng thành công", order));
        } else if (pathInfo.endsWith("/cancel")) {
            // POST /{id}/cancel - Hủy đơn hàng
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
        if (!isStaffOrAdmin(req)) {
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