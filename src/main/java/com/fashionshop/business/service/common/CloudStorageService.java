package com.fashionshop.business.service.common;

import com.fashionshop.config.AppConfig;
import com.fashionshop.presentation.dto.common.UploadRequestDTO;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * CloudStorageService - Tuong thich 100% voi storage.service.ts cua Node.js server.
 * Ho tro tai len Supabase S3 Cloud Storage va tu dong fallback ve local disk.
 */
public class CloudStorageService {
    private static final Logger logger = Logger.getLogger(CloudStorageService.class.getName());
    private static final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(15))
            .build();

    public Map<String, Object> uploadBase64(UploadRequestDTO input) throws IOException {
        if (input == null) {
            throw new IllegalArgumentException("Dữ liệu upload không được để trống");
        }

        input.validate();
        byte[] fileBytes = input.decodeBytes();
        String mediaType = input.resolveMediaType();
        String ext = "." + input.getFileExtension().toLowerCase();

        String folder = input.getFolder();
        if (folder == null || folder.isBlank()) {
            folder = "general";
        }
        folder = folder.replaceAll("[^a-zA-Z0-9_-]", "");

        String uniqueFileName = System.currentTimeMillis() + "-" + UUID.randomUUID().toString() + ext;
        String fileKey = folder + "/" + uniqueFileName;

        // 1. Kiem tra xem co cau hinh Cloud S3 / Supabase Storage khong
        String s3Endpoint = AppConfig.S3_ENDPOINT;
        String s3AccessKey = AppConfig.S3_ACCESS_KEY;
        String s3SecretKey = AppConfig.S3_SECRET_KEY;
        String bucketName = (AppConfig.S3_BUCKET_NAME != null && !AppConfig.S3_BUCKET_NAME.isBlank())
                ? AppConfig.S3_BUCKET_NAME
                : "fashion-uploads";

        if (s3Endpoint != null && !s3Endpoint.isBlank() && ((s3SecretKey != null && !s3SecretKey.isBlank()) || (s3AccessKey != null && !s3AccessKey.isBlank()))) {
            try {
                String contentType = (mediaType.equals("video")) ? "video/mp4" : "image/jpeg";
                if (input.getFileType() != null && !input.getFileType().isBlank()) {
                    contentType = input.getFileType().trim();
                }

                // Truong hop Supabase Storage
                if (s3Endpoint.contains("supabase.co")) {
                    String baseUrl = s3Endpoint.replace("/s3", "").replaceAll("/+$", "");
                    String restUploadUrl = baseUrl + "/object/" + bucketName + "/" + fileKey;

                    String authKey = (s3SecretKey != null && !s3SecretKey.isBlank()) ? s3SecretKey : s3AccessKey;
                    var requestBuilder = HttpRequest.newBuilder()
                            .uri(URI.create(restUploadUrl))
                            .timeout(Duration.ofSeconds(30))
                            .header("Authorization", "Bearer " + authKey)
                            .header("apiKey", authKey)
                            .header("Content-Type", contentType)
                            .header("x-upsert", "true");

                    if (s3AccessKey != null && !s3AccessKey.isBlank()) {
                        requestBuilder.header("x-access-key", s3AccessKey);
                    }

                    HttpRequest request = requestBuilder
                            .POST(HttpRequest.BodyPublishers.ofByteArray(fileBytes))
                            .build();

                    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                    if (response.statusCode() >= 200 && response.statusCode() < 300) {
                        String publicUrl = baseUrl + "/object/public/" + bucketName + "/" + fileKey;
                        logger.info("[CloudStorageService] Upload S3 Cloud thanh cong len Supabase: " + publicUrl);

                        Map<String, Object> result = new LinkedHashMap<>();
                        result.put("url", publicUrl);
                        result.put("type", mediaType);
                        result.put("key", fileKey);
                        return result;
                    } else {
                        logger.warning("[CloudStorageService] Supabase upload error (" + response.statusCode() + "): " + response.body() + " - fallback to local");
                    }
                }
            } catch (Exception ex) {
                logger.log(Level.WARNING, "[CloudStorageService] Khong the tai len Cloud S3, tu dong chuyen sang local storage", ex);
            }
        }

        // 2. Fallback: Luu cuc bo vao thu muc uploads
        Path uploadRoot = Paths.get(AppConfig.UPLOAD_DIR).toAbsolutePath().normalize();
        Path targetDir = uploadRoot.resolve(folder);
        if (!Files.exists(targetDir)) {
            Files.createDirectories(targetDir);
        }

        Path targetFile = targetDir.resolve(uniqueFileName);
        Files.write(targetFile, fileBytes);

        String localUrl = AppConfig.SERVER_BASE_URL + "/uploads/" + folder + "/" + uniqueFileName;
        logger.info("[CloudStorageService] Luu file cuc bo thanh cong: " + localUrl);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("url", localUrl);
        result.put("type", mediaType);
        result.put("key", fileKey);
        return result;
    }
}
