package com.fashionshop.common;

import com.fashionshop.business.service.common.CloudStorageService;
import com.fashionshop.presentation.dto.common.UploadRequestDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Base64;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class CloudStorageServiceTest {

    private final CloudStorageService storageService = new CloudStorageService();

    @Test
    @DisplayName("Verify UploadRequestDTO supports folder, filename, and base64 parsing")
    void testUploadRequestDTOFields() {
        String rawText = "Hello World Fashion";
        String base64 = "data:image/png;base64," + Base64.getEncoder().encodeToString(rawText.getBytes());

        UploadRequestDTO dto = new UploadRequestDTO("test_avatar.png", "image/png", base64, "avatars");
        assertEquals("avatars", dto.getFolder());
        assertEquals("test_avatar.png", dto.getFileName());
        assertEquals("image", dto.resolveMediaType());
        assertNotNull(dto.decodeBytes());
    }

    @Test
    @DisplayName("Verify CloudStorageService returns url, type, and key format matching Express contract")
    void testStorageServiceUpload() throws IOException {
        String rawText = "Test Image Content";
        String base64 = Base64.getEncoder().encodeToString(rawText.getBytes());

        UploadRequestDTO dto = new UploadRequestDTO("sample.jpg", "image/jpeg", base64, "products");
        Map<String, Object> result = storageService.uploadBase64(dto);

        assertNotNull(result);
        assertNotNull(result.get("url"));
        assertEquals("image", result.get("type"));
        assertNotNull(result.get("key"));
        assertTrue(result.get("key").toString().startsWith("products/"));
    }
}
