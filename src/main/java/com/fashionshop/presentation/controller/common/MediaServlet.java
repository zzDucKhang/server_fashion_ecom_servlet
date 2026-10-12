package com.fashionshop.presentation.controller.common;

import com.fashionshop.config.AppConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * MediaServlet handles serving of static media assets uploaded to ./uploads/
 * with caching, proper MIME detection, and HTTP 206 Range partial content streaming for videos.
 */
@WebServlet(urlPatterns = {"/uploads/*"})
public class MediaServlet extends HttpServlet {

    private static final Pattern RANGE_PATTERN = Pattern.compile("bytes=(\\d*)-(\\d*)");

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.trim().isEmpty() || pathInfo.contains("..")) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid file path");
            return;
        }

        Path uploadRoot = Paths.get(AppConfig.UPLOAD_DIR).toAbsolutePath().normalize();
        Path filePath = uploadRoot.resolve(pathInfo.startsWith("/") ? pathInfo.substring(1) : pathInfo).normalize();

        // Ensure file does not escape upload directory
        if (!filePath.startsWith(uploadRoot) || !Files.exists(filePath) || Files.isDirectory(filePath)) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Media file not found");
            return;
        }

        long fileLength = Files.size(filePath);
        String contentType = Files.probeContentType(filePath);
        if (contentType == null) {
            String fileName = filePath.getFileName().toString().toLowerCase();
            if (fileName.endsWith(".mp4")) contentType = "video/mp4";
            else if (fileName.endsWith(".webm")) contentType = "video/webm";
            else if (fileName.endsWith(".mov")) contentType = "video/quicktime";
            else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) contentType = "image/jpeg";
            else if (fileName.endsWith(".png")) contentType = "image/png";
            else if (fileName.endsWith(".gif")) contentType = "image/gif";
            else if (fileName.endsWith(".webp")) contentType = "image/webp";
            else contentType = "application/octet-stream";
        }

        resp.setContentType(contentType);
        resp.setHeader("Accept-Ranges", "bytes");
        resp.setHeader("Cache-Control", "public, max-age=86400");

        String rangeHeader = req.getHeader("Range");
        if (rangeHeader != null && rangeHeader.startsWith("bytes=")) {
            handleRangeRequest(filePath, fileLength, rangeHeader, resp);
        } else {
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setHeader("Content-Length", String.valueOf(fileLength));
            Files.copy(filePath, resp.getOutputStream());
        }
    }

    private void handleRangeRequest(Path filePath, long fileLength, String rangeHeader, HttpServletResponse resp)
            throws IOException {
        Matcher matcher = RANGE_PATTERN.matcher(rangeHeader);
        if (!matcher.matches()) {
            resp.setStatus(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
            resp.setHeader("Content-Range", "bytes */" + fileLength);
            return;
        }

        String startGroup = matcher.group(1);
        String endGroup = matcher.group(2);

        long start = 0;
        long end = fileLength - 1;

        if (!startGroup.isEmpty()) {
            start = Long.parseLong(startGroup);
        }
        if (!endGroup.isEmpty()) {
            end = Long.parseLong(endGroup);
        }

        if (start > end || start >= fileLength) {
            resp.setStatus(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
            resp.setHeader("Content-Range", "bytes */" + fileLength);
            return;
        }

        if (end >= fileLength) {
            end = fileLength - 1;
        }

        long contentLength = end - start + 1;
        resp.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
        resp.setHeader("Content-Range", "bytes " + start + "-" + end + "/" + fileLength);
        resp.setHeader("Content-Length", String.valueOf(contentLength));

        try (InputStream in = Files.newInputStream(filePath);
             OutputStream out = resp.getOutputStream()) {
            long skipped = in.skip(start);
            if (skipped < start) {
                return;
            }
            byte[] buffer = new byte[8192];
            long bytesRemaining = contentLength;
            while (bytesRemaining > 0) {
                int read = in.read(buffer, 0, (int) Math.min(buffer.length, bytesRemaining));
                if (read == -1) {
                    break;
                }
                out.write(buffer, 0, read);
                bytesRemaining -= read;
            }
        }
    }
}
