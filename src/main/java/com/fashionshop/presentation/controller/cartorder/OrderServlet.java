package com.fashionshop.presentation.controller.cartorder;

import com.fashionshop.business.entity.Order;
import com.fashionshop.business.service.cartorder.OrderService;
import com.fashionshop.common.exception.BadRequestException;
import com.fashionshop.common.response.ApiResponse;
import com.fashionshop.common.response.PagedResponse;
import com.fashionshop.common.util.JsonUtil;
import com.fashionshop.presentation.dto.cartorder.CreateOrderDTO;
import com.fashionshop.presentation.dto.cartorder.OrderQueryDTO;
import com.fashionshop.presentation.dto.cartorder.UpdateOrderStatusDTO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.fashionshop.common.util.ValidationUtil;

import java.io.IOException;

@WebServlet(urlPatterns = {"/api/v1/orders/*", "/api/orders/*"})
public class OrderServlet extends HttpServlet {

    private final OrderService orderService = new OrderService();

    private Integer getUserId(HttpServletRequest req) {
        Integer id = (Integer) req.getAttribute("currentUserId");
        if (id == null) {
            id = (Integer) req.getAttribute("userId");
        }
        return id;
    }

    private boolean isStaffOrAdmin(HttpServletRequest req) {
        Integer roleId = (Integer) req.getAttribute("currentRoleId");
        return roleId != null && (roleId == 1 || roleId == 2);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer userId = getUserId(req);
        if (userId == null) {
            JsonUtil.sendJson(resp, HttpServletResponse.SC_UNAUTHORIZED, ApiResponse.error(401, "Chưa xác thực"));
            return;
        }

        String pathInfo = req.getPathInfo();
        if (pathInfo == null || "/".equals(pathInfo) || pathInfo.isBlank()) {
            // GET / - danh sách đơn hàng
            OrderQueryDTO query = new OrderQueryDTO();
            String pageStr = req.getParameter("page");
            String limitStr = req.getParameter("limit");
            String status = req.getParameter("status");

            if (pageStr != null && !pageStr.isBlank()) query.setPage(Integer.parseInt(pageStr));
            if (limitStr != null && !limitStr.isBlank()) query.setLimit(Integer.parseInt(limitStr));
            query.setStatus(status);

            PagedResponse<Order> result = orderService.getOrders(userId, query);
            JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, ApiResponse.success(200, "Lấy danh sách đơn hàng thành công", result));
        } else {
            // GET /{id} - chi tiết đơn hàng
            try {
                String cleanPath = pathInfo.replaceFirst("^/", "");
                Integer orderId = Integer.parseInt(cleanPath);
                Order order = orderService.getOrderById(orderId, userId);
                JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, ApiResponse.success(200, "Chi tiết đơn hàng", order));
            } catch (NumberFormatException e) {
                JsonUtil.sendJson(resp, HttpServletResponse.SC_BAD_REQUEST, ApiResponse.error(400, "Mã đơn hàng không hợp lệ"));
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer userId = getUserId(req);
        if (userId == null) {
            JsonUtil.sendJson(resp, HttpServletResponse.SC_UNAUTHORIZED, ApiResponse.error(401, "Chưa xác thực"));
            return;
        }

        String pathInfo = req.getPathInfo();
        if (pathInfo == null || "/".equals(pathInfo) || pathInfo.isBlank()) {
            // POST / - Tạo đơn hàng mới
            CreateOrderDTO dto = JsonUtil.fromJson(req.getReader(), CreateOrderDTO.class);
            ValidationUtil.validate(dto);

            Order order = orderService.createOrder(userId, dto);
            JsonUtil.sendJson(resp, HttpServletResponse.SC_CREATED, ApiResponse.success(201, "Tạo đơn hàng thành công", order));
        } else if (pathInfo.endsWith("/cancel")) {
            // POST /{id}/cancel - Hủy đơn hàng
            try {
                String idPart = pathInfo.replaceFirst("^/", "").replace("/cancel", "");
                Integer orderId = Integer.parseInt(idPart);
                Order cancelled = orderService.cancelOrder(orderId, userId);
                JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, ApiResponse.success(200, "Hủy đơn hàng thành công", cancelled));
            } catch (NumberFormatException e) {
                JsonUtil.sendJson(resp, HttpServletResponse.SC_BAD_REQUEST, ApiResponse.error(400, "Mã đơn hàng không hợp lệ"));
            }
        } else {
            JsonUtil.sendJson(resp, HttpServletResponse.SC_NOT_FOUND, ApiResponse.error(404, "Endpoint không tồn tại"));
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!isStaffOrAdmin(req)) {
            JsonUtil.sendJson(resp, HttpServletResponse.SC_FORBIDDEN, ApiResponse.error(403, "Bạn không có quyền thực hiện thao tác này"));
            return;
        }

        // PUT /{id}/status - Cập nhật trạng thái (Admin/Staff)
        String pathInfo = req.getPathInfo();
        if (pathInfo != null && pathInfo.endsWith("/status")) {
            try {
                String idPart = pathInfo.replaceFirst("^/", "").replace("/status", "");
                Integer orderId = Integer.parseInt(idPart);
                UpdateOrderStatusDTO dto = JsonUtil.fromJson(req.getReader(), UpdateOrderStatusDTO.class);
                ValidationUtil.validate(dto);
                Order updated = orderService.updateOrderStatus(orderId, dto.getStatus());
                JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, ApiResponse.success(200, "Cập nhật trạng thái thành công", updated));
            } catch (NumberFormatException e) {
                JsonUtil.sendJson(resp, HttpServletResponse.SC_BAD_REQUEST, ApiResponse.error(400, "Mã đơn hàng không hợp lệ"));
            }
        } else {
            JsonUtil.sendJson(resp, HttpServletResponse.SC_NOT_FOUND, ApiResponse.error(404, "Endpoint không tồn tại"));
        }
    }
}