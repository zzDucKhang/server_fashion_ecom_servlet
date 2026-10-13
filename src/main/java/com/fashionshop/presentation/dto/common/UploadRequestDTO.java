package com.fashionshop.presentation.dto.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.nio.file.Paths;
import java.util.Base64;
import java.util.Set;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UploadRequestDTO {
    @com.fasterxml.jackson.annotation.JsonAlias({"name", "filename"})
    private String fileName;

    @com.fasterxml.jackson.annotation.JsonAlias({"type", "mimeType"})
    private String fileType;

    @com.fasterxml.jackson.annotation.JsonAlias({"file", "data", "base64Data"})
    private String base64;

    @com.fasterxml.jackson.annotation.JsonAlias({"directory", "path"})
    private String folder = "general";

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            "jpg", "jpeg", "png", "gif", "webp", "bmp", "svg",
            "mp4", "webm", "quicktime", "mov", "avi", "mkv"
    );

    public UploadRequestDTO() {
    }

    public UploadRequestDTO(String fileName, String fileType, String base64) {
        this(fileName, fileType, base64, "general");
    }

    public UploadRequestDTO(String fileName, String fileType, String base64, String folder) {
        this.fileName = fileName;
        this.fileType = fileType;
        this.base64 = base64;
        this.folder = (folder != null && !folder.isBlank()) ? folder.trim() : "general";
    }

    public String getFolder() {
        return (folder != null && !folder.isBlank()) ? folder.trim() : "general";
    }

    public void setFolder(String folder) {
        this.folder = (folder != null && !folder.isBlank()) ? folder.trim() : "general";
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getBase64() {
        return base64;
    }

    public void setBase64(String base64) {
        this.base64 = base64;
    }

    public String getFile() {
        return base64;
    }

    public void setFile(String file) {
        this.base64 = file;
    }

    public String getResolvedMediaType() {
        return resolveMediaType();
    }

    public byte[] getDecodedBytes() {
        return decodeBytes();
    }

    public String resolveMediaType() {
        if (fileType != null && fileType.toLowerCase().startsWith("video")) {
            return "video";
        }
        String ext = getFileExtension().toLowerCase();
        if (Set.of("mp4", "webm", "mov", "avi", "mkv").contains(ext)) {
            return "video";
        }
        return "image";
    }

    public String getFileExtension() {
        if (fileName == null || !fileName.contains(".")) {
            if (fileType != null && fileType.contains("/")) {
                String sub = fileType.substring(fileType.indexOf('/') + 1);
                return "jpeg".equalsIgnoreCase(sub) ? "jpg" : sub;
            }
            return "png";
        }
        return fileName.substring(fileName.lastIndexOf('.') + 1);
    }

    public String getSanitizedFileName() {
        if (fileName == null || fileName.isBlank()) {
            return UUID.randomUUID().toString() + "." + getFileExtension();
        }
        String nameOnly = Paths.get(fileName).getFileName().toString();
        nameOnly = nameOnly.replaceAll("[^a-zA-Z0-9._-]", "_");
        if (nameOnly.isBlank()) {
            nameOnly = UUID.randomUUID().toString() + "." + getFileExtension();
        }
        return nameOnly;
    }

    public byte[] decodeBytes() {
        if (base64 == null || base64.isBlank()) {
            throw new IllegalArgumentException("Dữ liệu base64 không được để trống");
        }
        String cleanBase64 = base64;
        int commaIndex = cleanBase64.indexOf(',');
        if (commaIndex != -1 && cleanBase64.substring(0, commaIndex).contains("base64")) {
            cleanBase64 = cleanBase64.substring(commaIndex + 1);
        }
        cleanBase64 = cleanBase64.replaceAll("\\s+", "");
        byte[] decoded = Base64.getDecoder().decode(cleanBase64);
        if (decoded.length > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("Kích thước tệp vượt quá giới hạn cho phép (tối đa 10MB)");
        }
        return decoded;
    }

    public void validate() {
        if (fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException("Tên tệp không được để trống");
        }
        String ext = getFileExtension().toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(ext)) {
            throw new IllegalArgumentException("Định dạng tệp không được hỗ trợ: ." + ext);
        }
        if (fileType != null) {
            String lowerType = fileType.toLowerCase();
            if (!lowerType.startsWith("image/") && !lowerType.startsWith("video/")) {
                throw new IllegalArgumentException("Loại MIME không được hỗ trợ: " + fileType);
            }
        }
        decodeBytes();
    }
}
