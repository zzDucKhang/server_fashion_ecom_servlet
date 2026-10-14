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
import java.util.LinkedHashMap;
import java.util.Map;

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

    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        sendError(resp, HttpServletResponse.SC_METHOD_NOT_ALLOWED, "HTTP method PATCH is not supported for this endpoint");
    }

    protected Integer getUserId(HttpServletRequest req) {
        Object attr = req.getAttribute("currentUserId");
        if (attr instanceof Integer) {
            return (Integer) attr;
        }
        return SecurityGuard.requireAuth(req);
    }

    protected Integer getOptionalUserId(HttpServletRequest req) {
        return SecurityGuard.getOptionalUserId(req);
    }

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

    protected boolean isStaffOrAdmin(HttpServletRequest req) {
        if (isAdmin(req)) {
            return true;
        }
        Object roleCode = req.getAttribute("currentRoleCode");
        Object roleId = req.getAttribute("currentRoleId");
        return "STAFF".equalsIgnoreCase(String.valueOf(roleCode)) || Integer.valueOf(2).equals(roleId);
    }

    protected void sendJson(HttpServletResponse resp, int status, Object data) throws IOException {
        JsonUtil.sendJson(resp, status, data);
    }

    protected void sendSuccess(HttpServletResponse resp, String message, Object data) throws IOException {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("statusCode", HttpServletResponse.SC_OK);
        if (message != null) {
            body.put("message", message);
        }
        body.put("data", data);
        sendJson(resp, HttpServletResponse.SC_OK, body);
    }

    protected void sendApiResponse(HttpServletResponse resp, ApiResponse<?> apiResponse) throws IOException {
        sendJson(resp, apiResponse.getStatusCode(), apiResponse);
    }

    protected void sendPaged(HttpServletResponse resp, PagedResponse<?> pagedResponse) throws IOException {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("statusCode", HttpServletResponse.SC_OK);
        body.put("data", pagedResponse.getData());
        body.put("totalItems", pagedResponse.getTotalItems());
        body.put("page", pagedResponse.getPage());
        body.put("limit", pagedResponse.getLimit());
        body.put("totalPages", pagedResponse.getTotalPages());
        sendJson(resp, HttpServletResponse.SC_OK, body);
    }

    protected void sendError(HttpServletResponse resp, int status, String message) throws IOException {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("success", false);
        body.put("statusCode", status);
        body.put("message", message != null ? message : "Đã xảy ra lỗi");
        sendJson(resp, status, body);
    }

    protected <T> T parseBody(HttpServletRequest req, Class<T> clazz) throws IOException {
        return JsonUtil.fromJson(req, clazz);
    }

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