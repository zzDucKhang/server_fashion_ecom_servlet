package com.fashionshop.presentation.controller.fulfillment;

import com.fashionshop.business.entity.PaymentTransaction;
import com.fashionshop.business.service.fulfillment.PaymentService;
import com.fashionshop.common.util.SecurityGuard;
import com.fashionshop.presentation.controller.base.BaseHttpServlet;
import com.fashionshop.presentation.dto.fulfillment.PaymentInfoDTO;
import com.fashionshop.presentation.dto.fulfillment.PaymentStatusDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

@WebServlet(urlPatterns = {
    "/api/v1/fulfillment/payments",
    "/api/v1/fulfillment/payments/*",
    "/api/v1/payments",
    "/api/v1/payments/*",
    "/api/payments",
    "/api/payments/*"
})
public class PaymentServlet extends BaseHttpServlet {

    private final PaymentService paymentService = new PaymentService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Integer userId = getUserId(req);
        if (userId == null) {
            sendError(resp, HttpServletResponse.SC_UNAUTHORIZED, "Chưa xác thực người dùng");
            return;
        }

        String pathInfo = req.getPathInfo();

        if (pathInfo == null || "/".equals(pathInfo) || pathInfo.isBlank()) {
            // Admin query: GET /api/v1/fulfillment/payments
            SecurityGuard.requireAdmin(req);
            int page = getIntParameter(req, "page", 1);
            int limit = getIntParameter(req, "limit", 10);
            java.util.List<PaymentTransaction> list = paymentService.listTransactions(page, limit);
            long total = paymentService.countTransactions();
            int totalPages = (int) Math.ceil((double) total / limit);
            sendJson(resp, HttpServletResponse.SC_OK, Map.of(
                    "statusCode", 200,
                    "message", "Danh sách giao dịch thanh toán",
                    "data", list,
                    "totalItems", total,
                    "totalPages", Math.max(1, totalPages),
                    "page", page,
                    "limit", limit
            ));
            return;
        }

        if ("/info".equals(pathInfo)) {
            // GET /api/payments/info?orderId=123
            Integer orderId = getIntParameter(req, "orderId", null);
            if (orderId == null) {
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Thiếu hoặc sai định dạng mã đơn hàng orderId");
                return;
            }

            PaymentInfoDTO paymentInfo = paymentService.getPaymentInfo(orderId, userId);
            sendSuccess(resp, "Lấy thông tin thanh toán thành công", paymentInfo);

        } else if ("/status".equals(pathInfo)) {
            // GET /api/payments/status?orderId=123 (Dùng cho Frontend Polling)
            Integer orderId = getIntParameter(req, "orderId", null);
            if (orderId == null) {
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Thiếu hoặc sai định dạng mã đơn hàng orderId");
                return;
            }

            PaymentStatusDTO status = paymentService.getPaymentStatus(orderId, userId);
            sendSuccess(resp, "Trạng thái thanh toán", status);

        } else {
            // GET /:paymentId
            Integer paymentId = parseId(pathInfo);
            PaymentTransaction tx = paymentService.findById(paymentId);
            if (!isAdmin(req) && (tx.getOrder() == null || tx.getOrder().getUser() == null || !userId.equals(tx.getOrder().getUser().getId()))) {
                sendError(resp, HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền xem giao dịch này");
                return;
            }
            sendSuccess(resp, "Chi tiết giao dịch thanh toán", tx);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Integer userId = getUserId(req);
        if (userId == null) {
            sendError(resp, HttpServletResponse.SC_UNAUTHORIZED, "Chưa xác thực người dùng");
            return;
        }

        String pathInfo = req.getPathInfo();

        if (pathInfo == null || "/".equals(pathInfo) || pathInfo.isBlank() || "/initiate".equals(pathInfo)) {
            // POST / (createPayment from client orderApi) or POST /initiate
            Integer orderId = null;

            // 1. Try reading JSON body
            Map<?, ?> body = parseBody(req, Map.class);
            if (body != null && body.get("orderId") != null) {
                orderId = parseInteger(String.valueOf(body.get("orderId")), null);
            }

            // 2. Try fallback query param
            if (orderId == null) {
                orderId = getIntParameter(req, "orderId", null);
            }

            if (orderId == null) {
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Thiếu tham số orderId");
                return;
            }

            PaymentTransaction tx = paymentService.initiatePayment(orderId, userId);
            PaymentInfoDTO info = paymentService.getPaymentInfo(orderId, userId);

            Map<String, Object> responseData = Map.of(
                    "qrUrl", info.getQrUrl(),
                    "amount", info.getAmount(),
                    "transferContent", info.getTransferContent(),
                    "paymentId", String.valueOf(tx.getId()),
                    "orderCode", info.getOrderCode(),
                    "bankName", info.getBankName(),
                    "accountNumber", info.getAccountNumber()
            );

            sendSuccess(resp, "Khởi tạo giao dịch thành công", responseData);
        } else {
            sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Endpoint không tồn tại");
        }
    }
}