package com.fashionshop.presentation.controller.fulfillment;

import com.fashionshop.business.entity.PaymentTransaction;
import com.fashionshop.business.service.fulfillment.PaymentService;
import com.fashionshop.common.exception.BadRequestException;
import com.fashionshop.common.response.ApiResponse;
import com.fashionshop.common.util.JsonUtil;
import com.fashionshop.presentation.dto.fulfillment.PaymentInfoDTO;
import com.fashionshop.presentation.dto.fulfillment.PaymentStatusDTO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(urlPatterns = {"/api/v1/payments/*", "/api/payments/*"})
public class PaymentServlet extends HttpServlet {

    private final PaymentService paymentService = new PaymentService();

    private Integer getUserId(HttpServletRequest req) {
        Integer id = (Integer) req.getAttribute("currentUserId");
        if (id == null) {
            id = (Integer) req.getAttribute("userId");
        }
        return id;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        Integer userId = getUserId(req);
        if (userId == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write(JsonUtil.toJson(ApiResponse.error(401, "Chưa xác thực người dùng")));
            return;
        }

        String pathInfo = req.getPathInfo();

        if ("/info".equals(pathInfo)) {
            // GET /api/payments/info?orderId=123
            String orderIdStr = req.getParameter("orderId");
            if (orderIdStr == null || orderIdStr.isBlank()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(JsonUtil.toJson(ApiResponse.error(400, "Thiếu tham số orderId")));
                return;
            }

            Integer orderId;
            try {
                orderId = Integer.parseInt(orderIdStr.trim());
            } catch (NumberFormatException e) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(JsonUtil.toJson(ApiResponse.error(400, "Mã đơn hàng không hợp lệ")));
                return;
            }

            PaymentInfoDTO paymentInfo = paymentService.getPaymentInfo(orderId, userId);

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(JsonUtil.toJson(ApiResponse.success(200, "Lấy thông tin thanh toán thành công", paymentInfo)));

        } else if ("/status".equals(pathInfo)) {
            // GET /api/payments/status?orderId=123 (Dùng cho Frontend Polling)
            String orderIdStr = req.getParameter("orderId");
            if (orderIdStr == null || orderIdStr.isBlank()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(JsonUtil.toJson(ApiResponse.error(400, "Thiếu tham số orderId")));
                return;
            }

            Integer orderId;
            try {
                orderId = Integer.parseInt(orderIdStr.trim());
            } catch (NumberFormatException e) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(JsonUtil.toJson(ApiResponse.error(400, "Mã đơn hàng không hợp lệ")));
                return;
            }

            PaymentStatusDTO status = paymentService.getPaymentStatus(orderId, userId);

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(JsonUtil.toJson(ApiResponse.success(200, "Trạng thái thanh toán", status)));

        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write(JsonUtil.toJson(ApiResponse.error(404, "Endpoint không tồn tại")));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        Integer userId = getUserId(req);
        if (userId == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write(JsonUtil.toJson(ApiResponse.error(401, "Chưa xác thực người dùng")));
            return;
        }

        String pathInfo = req.getPathInfo();

        if ("/initiate".equals(pathInfo)) {
            // POST /api/payments/initiate?orderId=123
            String orderIdStr = req.getParameter("orderId");
            if (orderIdStr == null || orderIdStr.isBlank()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(JsonUtil.toJson(ApiResponse.error(400, "Thiếu tham số orderId")));
                return;
            }

            Integer orderId;
            try {
                orderId = Integer.parseInt(orderIdStr.trim());
            } catch (NumberFormatException e) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(JsonUtil.toJson(ApiResponse.error(400, "Mã đơn hàng không hợp lệ")));
                return;
            }

            PaymentTransaction tx = paymentService.initiatePayment(orderId, userId);

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(JsonUtil.toJson(ApiResponse.success(200, "Khởi tạo giao dịch thành công", tx)));

        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write(JsonUtil.toJson(ApiResponse.error(404, "Endpoint không tồn tại")));
        }
    }
}