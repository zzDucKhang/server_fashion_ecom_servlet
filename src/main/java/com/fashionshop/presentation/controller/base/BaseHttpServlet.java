package com.fashionshop.presentation.controller.base;

import com.fashionshop.common.exception.BadRequestException;
import com.fashionshop.common.response.ApiResponse;
import com.fashionshop.common.response.PagedResponse;
import com.fashionshop.common.util.JsonUtil;
import com.fashionshop.common.util.SecurityGuard;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

/**
 * BaseHttpServlet providing centralized request/response handling,
 * native HTTP PATCH method routing, authentication context resolution,
 * and standard JSON serialization helpers.
 */
public abstract class BaseHttpServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=UTF-8");

        String method = req.getMethod();
        if ("PATCH".equalsIgnoreCase(method)) {
            doPatch(req, resp);
        } else {
            super.service(req, resp);
        }
    }

    /**
     * Dispatcher for HTTP PATCH requests. Subclasses override to handle PATCH endpoints.
     */
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        sendError(resp, HttpServletResponse.SC_METHOD_NOT_ALLOWED, "HTTP method PATCH is not supported for this endpoint");
    }

    /**
     * Returns the authenticated user's ID, or throws UnauthorizedException (401) if not logged in.
     */
    protected Integer getUserId(HttpServletRequest req) {
        Object attr = req.getAttribute("currentUserId");
        if (attr instanceof Integer) {
            return (Integer) attr;
        }
        return SecurityGuard.requireAuth(req);
    }

    /**
     * Returns the authenticated user's ID if present, or null if guest. Does not throw.
     */
    protected Integer getOptionalUserId(HttpServletRequest req) {
        return SecurityGuard.getOptionalUserId(req);
    }

    /**
     * Checks if current caller possesses ADMIN privileges.
     */
    protected boolean isAdmin(HttpServletRequest req) {
        Object roleCode = req.getAttribute("currentRoleCode");
        Object roleId = req.getAttribute("currentRoleId");
        if ("ADMIN".equalsIgnoreCase(String.valueOf(roleCode)) || Integer.valueOf(1).equals(roleId)) {
            return true;
        }
        Integer optUserId = getOptionalUserId(req);
        if (optUserId == null) {
            return false;
        }
        roleCode = req.getAttribute("currentRoleCode");
        roleId = req.getAttribute("currentRoleId");
        return "ADMIN".equalsIgnoreCase(String.valueOf(roleCode)) || Integer.valueOf(1).equals(roleId);
    }

    /**
     * Checks if current caller possesses STAFF (roleId 2) or ADMIN privileges.
     */
    protected boolean isStaffOrAdmin(HttpServletRequest req) {
        if (isAdmin(req)) {
            return true;
        }
        Object roleCode = req.getAttribute("currentRoleCode");
        Object roleId = req.getAttribute("currentRoleId");
        return "STAFF".equalsIgnoreCase(String.valueOf(roleCode)) || Integer.valueOf(2).equals(roleId);
    }

    /**
     * Serializes an arbitrary object to JSON and writes it to the response stream.
     */
    protected void sendJson(HttpServletResponse resp, int status, Object data) throws IOException {
        JsonUtil.sendJson(resp, status, data);
    }

    /**
     * Sends a successful JSON response matching the client's { statusCode: 200, message: "...", data: ... } format.
     */
    protected void sendSuccess(HttpServletResponse resp, String message, Object data) throws IOException {
        sendJson(resp, HttpServletResponse.SC_OK, Map.of(
                "statusCode", HttpServletResponse.SC_OK,
                "message", message != null ? message : "Thành công",
                "data", data
        ));
    }

    /**
     * Sends a standard ApiResponse wrapper.
     */
    protected void sendApiResponse(HttpServletResponse resp, ApiResponse<?> apiResponse) throws IOException {
        sendJson(resp, apiResponse.getStatusCode(), apiResponse);
    }

    /**
     * Sends a paged JSON response.
     */
    protected void sendPaged(HttpServletResponse resp, PagedResponse<?> pagedResponse) throws IOException {
        sendJson(resp, HttpServletResponse.SC_OK, pagedResponse);
    }

    /**
     * Sends an error JSON response.
     */
    protected void sendError(HttpServletResponse resp, int status, String message) throws IOException {
        sendJson(resp, status, Map.of(
                "statusCode", status,
                "message", message != null ? message : "Đã xảy ra lỗi",
                "error", message != null ? message : "Error"
        ));
    }

    /**
     * Deserializes request body into the specified class type.
     */
    protected <T> T parseBody(HttpServletRequest req, Class<T> clazz) throws IOException {
        return JsonUtil.fromJson(req, clazz);
    }

    /**
     * Safely parses an integer parameter from request query string or body.
     */
    protected Integer getIntParameter(HttpServletRequest req, String name, Integer defaultValue) {
        String val = req.getParameter(name);
        if (val == null || val.isBlank()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(val.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Safely parses a string into an Integer.
     */
    protected Integer parseInteger(String val, Integer defaultValue) {
        if (val == null || val.isBlank()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(val.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Extracts and parses the primary Integer ID parameter from pathInfo (e.g., /123 -> 123, /123/status -> 123).
     * Throws BadRequestException (400) if pathInfo is null, empty, or not a valid number.
     */
    protected Integer parseId(String pathInfo) {
        if (pathInfo == null || pathInfo.isBlank()) {
            throw new BadRequestException("Thiếu ID trong đường dẫn yêu cầu");
        }
        String idStr = pathInfo.startsWith("/") ? pathInfo.substring(1) : pathInfo;
        if (idStr.contains("/")) {
            idStr = idStr.substring(0, idStr.indexOf('/'));
        }
        try {
            return Integer.parseInt(idStr.trim());
        } catch (NumberFormatException e) {
            throw new BadRequestException("ID không hợp lệ: " + pathInfo);
        }
    }
}
