package com.fashionshop.presentation.controller.common;

import com.fashionshop.business.service.common.CloudStorageService;
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

            Map<String, Object> data = storageService.uploadBase64(dto);
            sendSuccess(resp, "Tải lên tập tin lên cloud thành công", data);
        } catch (IllegalArgumentException ex) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Lỗi xử lý tải tệp lên", ex);
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Không thể xử lý tệp tin: " + ex.getMessage());
        }
    }
}
