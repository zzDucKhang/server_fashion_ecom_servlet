package com.fashionshop.presentation.controller.common;

import com.fashionshop.business.service.common.CloudStorageService;
import com.fashionshop.config.AppConfig;
import com.fashionshop.presentation.controller.base.BaseHttpServlet;
import com.fashionshop.presentation.dto.common.UploadRequestDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(urlPatterns = {"/api/v1/common/upload", "/common/upload"})
public class UploadServlet extends BaseHttpServlet {
    private static final Logger logger = Logger.getLogger(UploadServlet.class.getName());
    private final CloudStorageService storageService = new CloudStorageService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            UploadRequestDTO dto = parseBody(req, UploadRequestDTO.class);
            if (dto == null) {
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Payload không hợp lệ");
                return;
            }

            // 1. Phân tích tên miền thật (Hỗ trợ tốt khi deploy lên Nginx/Cloudflare)
            String scheme = req.getHeader("X-Forwarded-Proto");
            if (scheme == null) scheme = req.getScheme();

            String host = req.getHeader("X-Forwarded-Host");
            if (host == null) host = req.getServerName();

            int port = req.getServerPort();
            String portHeader = req.getHeader("X-Forwarded-Port");
            if (portHeader != null) {
                try {
                    port = Integer.parseInt(portHeader);
                } catch (NumberFormatException ignored) {}
            }

            // 2. Lấy Base URL linh hoạt
            String serverBaseUrl;
            String envBaseUrl = System.getenv("SERVER_BASE_URL") != null ? System.getenv("SERVER_BASE_URL") : AppConfig.get("SERVER_BASE_URL");
            
            // Ưu tiên dùng cứng biến SERVER_BASE_URL trên Render nếu bạn có điền
            if (envBaseUrl != null && !envBaseUrl.isBlank() && !envBaseUrl.contains("localhost")) {
                serverBaseUrl = envBaseUrl;
            } else {
                // Đã fix lỗi dính cổng :80 khi Render proxy HTTPS về HTTP
                if (port == 80 || port == 443 || (scheme.equals("https") && port == 80)) {
                    serverBaseUrl = scheme + "://" + host;
                } else {
                    serverBaseUrl = scheme + "://" + host + ":" + port;
                }
            }

            // 3. Tiến hành upload
            Map<String, Object> data = storageService.uploadBase64(dto, serverBaseUrl);
            sendSuccess(resp, "Tải lên tập tin thành công", data);

        } catch (IllegalArgumentException ex) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Lỗi xử lý tải tệp lên", ex);
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Không thể xử lý tệp tin: " + ex.getMessage());
        }
    }
}