package com.fashionshop.presentation.controller.fulfillment;

import com.fashionshop.business.service.fulfillment.PaymentService;
import com.fashionshop.common.response.ApiResponse;
import com.fashionshop.common.util.JsonUtil;
import com.fashionshop.presentation.dto.fulfillment.SepayWebhookDTO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;

@WebServlet(urlPatterns = {"/api/v1/fulfillment/payments/webhook", "/api/sepay/webhook"})
public class SepayWebhookServlet extends HttpServlet {

    private final PaymentService paymentService = new PaymentService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");

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

        if (!paymentService.verifySePaySignature(rawBody, signature, timestamp)) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write(JsonUtil.toJson(ApiResponse.error(401, "Chữ ký SePay không hợp lệ")));
            return;
        }

        SepayWebhookDTO payload = JsonUtil.fromJson(new String(rawBody, "UTF-8"), SepayWebhookDTO.class);
        paymentService.handleSePayWebhook(payload);

        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().write(JsonUtil.toJson(ApiResponse.success(200, "OK", null)));
    }
}