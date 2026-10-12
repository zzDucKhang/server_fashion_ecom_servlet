package com.fashionshop.presentation.controller.identity;

import com.fashionshop.business.service.identity.RolePermissionService;
import com.fashionshop.common.exception.BadRequestException;
import com.fashionshop.common.util.JsonUtil;
import com.fashionshop.common.util.SecurityGuard;
import com.fashionshop.common.util.ValidationUtil;
import com.fashionshop.presentation.dto.identity.CreateRoleDTO;
import com.fashionshop.presentation.dto.identity.UpdateRoleDTO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet("/api/v1/identity/roles/*")
public class RoleServlet extends HttpServlet {

    private final RolePermissionService rolePermissionService = new RolePermissionService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        SecurityGuard.requireAdmin(req);
        String currentRoleCode = SecurityGuard.getRoleCode(req);
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            // GET /api/v1/identity/roles
            List<Map<String, Object>> roles = rolePermissionService.getAllRoles(currentRoleCode);
            JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of(
                    "statusCode", 200,
                    "data", roles
            ));
        } else {
            // GET /api/v1/identity/roles/:roleId
            Integer roleId = parseRoleId(pathInfo);
            Map<String, Object> role = rolePermissionService.getRoleById(roleId, currentRoleCode);
            JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of(
                    "statusCode", 200,
                    "data", role
            ));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer adminId = SecurityGuard.requireAdmin(req);
        String currentRoleCode = SecurityGuard.getRoleCode(req);

        CreateRoleDTO dto = JsonUtil.fromJson(req, CreateRoleDTO.class);
        ValidationUtil.validate(dto);

        Map<String, Object> created = rolePermissionService.createRole(dto, adminId, currentRoleCode);
        JsonUtil.sendJson(resp, HttpServletResponse.SC_CREATED, Map.of(
                "statusCode", 201,
                "message", "Tạo vai trò thành công",
                "data", created
        ));
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer adminId = SecurityGuard.requireAdmin(req);
        String currentRoleCode = SecurityGuard.getRoleCode(req);
        Integer roleId = parseRoleId(req.getPathInfo());

        UpdateRoleDTO dto = JsonUtil.fromJson(req, UpdateRoleDTO.class);
        ValidationUtil.validate(dto);

        Map<String, Object> updated = rolePermissionService.updateRole(roleId, dto, adminId, currentRoleCode);
        JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of(
                "statusCode", 200,
                "message", "Cập nhật vai trò thành công",
                "data", updated
        ));
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer adminId = SecurityGuard.requireAdmin(req);
        String currentRoleCode = SecurityGuard.getRoleCode(req);
        Integer roleId = parseRoleId(req.getPathInfo());

        Map<String, Object> result = rolePermissionService.softDeleteRole(roleId, adminId, currentRoleCode);
        JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of(
                "statusCode", 200,
                "data", result
        ));
    }

    private Integer parseRoleId(String pathInfo) {
        if (pathInfo == null || pathInfo.length() <= 1) {
            throw new BadRequestException("Thiếu ID vai trò");
        }
        try {
            String idStr = pathInfo.substring(1);
            if (idStr.contains("/")) {
                idStr = idStr.substring(0, idStr.indexOf('/'));
            }
            return Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            throw new BadRequestException("ID vai trò không hợp lệ: " + pathInfo);
        }
    }
}
