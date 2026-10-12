package com.fashionshop.presentation.controller.fulfillment;

import com.fashionshop.business.service.fulfillment.PaymentService;
import com.fashionshop.common.util.JsonUtil;
import com.fashionshop.config.AppConfig;
import com.fashionshop.presentation.controller.base.BaseHttpServlet;
import com.fashionshop.presentation.dto.fulfillment.SepayWebhookDTO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@WebServlet(urlPatterns = {
        "/api/v1/fulfillment/payments/webhook",
        "/api/v1/fulfillment/payments/webhook/*",
        "/api/sepay/webhook",
        "/api/sepay/webhook/*"
})
public class SepayWebhookServlet extends BaseHttpServlet {

    private final PaymentService paymentService = new PaymentService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String signature = req.getHeader("x-sepay-signature");
        String timestamp = req.getHeader("x-sepay-timestamp");

        InputStream is = req.getInputStream();
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int nRead;
        while ((nRead = is.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        byte[] rawBody = buffer.toByteArray();

        // Kiểm tra cấu hình và xác thực chữ ký bảo mật webhook SePay
        String secret = AppConfig.SEPAY_WEBHOOK_SECRET;
        if (secret != null && !secret.isBlank()) {
            if (signature == null || signature.isBlank()) {
                sendError(resp, HttpServletResponse.SC_UNAUTHORIZED, "Missing SePay signature");
                return;
            }
            if (timestamp == null || timestamp.isBlank()) {
                sendError(resp, HttpServletResponse.SC_UNAUTHORIZED, "Missing SePay timestamp");
                return;
            }
            if (!paymentService.verifySePaySignature(rawBody, signature, timestamp)) {
                sendError(resp, HttpServletResponse.SC_UNAUTHORIZED, "Invalid SePay signature");
                return;
            }
        }

        SepayWebhookDTO payload;
        try {
            payload = JsonUtil.fromJson(new String(rawBody, StandardCharsets.UTF_8), SepayWebhookDTO.class);
        } catch (Exception e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Payload is not a valid JSON");
            return;
        }

        paymentService.handleSePayWebhook(payload);

        // Phản hồi định dạng tiêu chuẩn { success: true }
        sendJson(resp, HttpServletResponse.SC_OK, Map.of(
                "success", true,
                "statusCode", HttpServletResponse.SC_OK,
                "message", "Xử lý webhook SePay thành công"
        ));
    }
}