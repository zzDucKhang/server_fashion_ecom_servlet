package com.fashionshop.presentation.controller.identity;

import com.fashionshop.business.service.identity.UserService;
import com.fashionshop.common.exception.BadRequestException;
import com.fashionshop.common.exception.NotFoundException;
import com.fashionshop.common.response.PagedResponse;
import com.fashionshop.common.util.JsonUtil;
import com.fashionshop.common.util.SecurityGuard;
import com.fashionshop.common.util.ValidationUtil;
import com.fashionshop.presentation.dto.identity.UpdateUserDTO;
import com.fashionshop.presentation.dto.identity.UpdateUserStatusDTO;
import com.fashionshop.presentation.dto.identity.UserQueryDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

@WebServlet("/api/v1/identity/users/*")
public class UserServlet extends HttpServlet {

    private final UserService userService = new UserService();

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if ("PATCH".equalsIgnoreCase(req.getMethod())) {
            doPatch(req, resp);
        } else {
            super.service(req, resp);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer currentUserId = SecurityGuard.requireAuth(req);
        String currentRoleCode = SecurityGuard.getRoleCode(req);
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            // GET /api/v1/identity/users -> ADMIN ONLY
            UserQueryDTO query = parseQueryDTO(req);
            PagedResponse<Map<String, Object>> result = userService.getUsers(query, currentRoleCode);
            JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, result);
        } else {
            // GET /api/v1/identity/users/:userId -> Owner or ADMIN
            Integer userId = parseUserId(pathInfo);
            Map<String, Object> result = userService.getUserById(userId, currentUserId, currentRoleCode);
            JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of(
                    "statusCode", 200,
                    "data", result
            ));
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // PUT /api/v1/identity/users/:userId -> Owner or ADMIN
        Integer currentUserId = SecurityGuard.requireAuth(req);
        String currentRoleCode = SecurityGuard.getRoleCode(req);
        Integer userId = parseUserId(req.getPathInfo());

        UpdateUserDTO dto = JsonUtil.fromJson(req, UpdateUserDTO.class);
        ValidationUtil.validate(dto);

        Map<String, Object> result = userService.updateUser(userId, dto, currentUserId, currentRoleCode);
        JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of(
                "statusCode", 200,
                "message", "Cập nhật thông tin thành công",
                "data", result
        ));
    }

    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // PATCH /api/v1/identity/users/:userId/status -> ADMIN ONLY
        Integer currentUserId = SecurityGuard.requireAuth(req);
        String currentRoleCode = SecurityGuard.getRoleCode(req);

        String pathInfo = req.getPathInfo();
        if (pathInfo == null || !pathInfo.endsWith("/status")) {
            throw new NotFoundException("Đường dẫn không tồn tại: " + pathInfo);
        }

        String idPart = pathInfo.substring(1, pathInfo.lastIndexOf('/'));
        Integer userId;
        try {
            userId = Integer.parseInt(idPart);
        } catch (NumberFormatException e) {
            throw new BadRequestException("ID người dùng không hợp lệ");
        }

        UpdateUserStatusDTO dto = JsonUtil.fromJson(req, UpdateUserStatusDTO.class);
        ValidationUtil.validate(dto);

        Map<String, Object> result = userService.updateUserStatus(userId, dto, currentUserId, currentRoleCode);
        JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of(
                "statusCode", 200,
                "message", "Cập nhật trạng thái người dùng thành công",
                "data", result
        ));
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // DELETE /api/v1/identity/users/:userId -> ADMIN ONLY
        Integer currentUserId = SecurityGuard.requireAuth(req);
        String currentRoleCode = SecurityGuard.getRoleCode(req);
        Integer userId = parseUserId(req.getPathInfo());

        Map<String, Object> result = userService.softDeleteUser(userId, currentUserId, currentRoleCode);
        JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of(
                "statusCode", 200,
                "data", result
        ));
    }

    private Integer parseUserId(String pathInfo) {
        if (pathInfo == null || pathInfo.length() <= 1) {
            throw new BadRequestException("Thiếu ID người dùng");
        }
        try {
            String idStr = pathInfo.substring(1);
            if (idStr.contains("/")) {
                idStr = idStr.substring(0, idStr.indexOf('/'));
            }
            return Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            throw new BadRequestException("ID người dùng không hợp lệ: " + pathInfo);
        }
    }

    private UserQueryDTO parseQueryDTO(HttpServletRequest req) {
        UserQueryDTO query = new UserQueryDTO();
        String pageParam = req.getParameter("page");
        if (pageParam != null) {
            try { query.setPage(Integer.parseInt(pageParam)); } catch (NumberFormatException ignored) {}
        }
        String limitParam = req.getParameter("limit");
        if (limitParam != null) {
            try { query.setLimit(Integer.parseInt(limitParam)); } catch (NumberFormatException ignored) {}
        }
        String searchParam = req.getParameter("search");
        if (searchParam != null) {
            query.setSearch(searchParam);
        }
        String statusParam = req.getParameter("status");
        if (statusParam != null) {
            query.setStatus(statusParam);
        }
        String roleIdParam = req.getParameter("roleId");
        if (roleIdParam != null) {
            try { query.setRoleId(Integer.parseInt(roleIdParam)); } catch (NumberFormatException ignored) {}
        }
        return query;
    }
}
