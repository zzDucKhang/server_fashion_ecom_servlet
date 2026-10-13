package com.fashionshop.presentation.controller.common;

import com.fashionshop.config.AppConfig;
import com.fashionshop.presentation.controller.base.BaseHttpServlet;
import com.fashionshop.presentation.dto.common.UploadRequestDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(urlPatterns = {"/api/v1/common/upload", "/common/upload"})
public class UploadServlet extends BaseHttpServlet {
    private static final Logger logger = Logger.getLogger(UploadServlet.class.getName());

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            UploadRequestDTO dto = parseBody(req, UploadRequestDTO.class);
            if (dto == null) {
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Payload không hợp lệ");
                return;
            }

            dto.validate();
            byte[] fileBytes = dto.decodeBytes();

            String dateFolder = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String uniqueName = UUID.randomUUID().toString().substring(0, 8) + "_" + dto.getSanitizedFileName();

            Path uploadRoot = Paths.get(AppConfig.UPLOAD_DIR).toAbsolutePath().normalize();
            Path targetDir = uploadRoot.resolve(dateFolder);
            if (!Files.exists(targetDir)) {
                Files.createDirectories(targetDir);
            }

            Path targetFile = targetDir.resolve(uniqueName);
            Files.write(targetFile, fileBytes);

            String relativeUrlPath = dateFolder + "/" + uniqueName;
            String fullUrl = AppConfig.SERVER_BASE_URL + "/uploads/" + relativeUrlPath;
            String mediaType = dto.resolveMediaType();

            Map<String, Object> data = Map.of(
                    "url", fullUrl,
                    "type", mediaType
            );

            sendSuccess(resp, "Tải lên tập tin thành công", data);
        } catch (IllegalArgumentException ex) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Lỗi xử lý tải tệp lên", ex);
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Không thể xử lý tệp tin: " + ex.getMessage());
        }
    }
}
