package com.fashionshop.presentation.controller.identity;

import com.fashionshop.business.service.identity.RolePermissionService;
import com.fashionshop.common.exception.BadRequestException;
import com.fashionshop.common.util.JsonUtil;
import com.fashionshop.common.util.SecurityGuard;
import com.fashionshop.common.util.ValidationUtil;
import com.fashionshop.presentation.dto.identity.CreatePermissionDTO;
import com.fashionshop.presentation.dto.identity.UpdatePermissionDTO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = {
        "/api/v1/identity/roles/permissions",
        "/api/v1/identity/roles/permissions/*"
})
public class PermissionServlet extends HttpServlet {

    private final RolePermissionService rolePermissionService = new RolePermissionService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        SecurityGuard.requireAdmin(req);
        String currentRoleCode = SecurityGuard.getRoleCode(req);
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            // GET /api/v1/identity/roles/permissions?module=...
            String module = req.getParameter("module");
            List<Map<String, Object>> permissions = rolePermissionService.getAllPermissions(module, currentRoleCode);
            JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of(
                    "statusCode", 200,
                    "data", permissions
            ));
        } else {
            // GET /api/v1/identity/roles/permissions/:permissionId
            Integer permissionId = parsePermissionId(pathInfo);
            Map<String, Object> permission = rolePermissionService.getPermissionById(permissionId, currentRoleCode);
            JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of(
                    "statusCode", 200,
                    "data", permission
            ));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer adminId = SecurityGuard.requireAdmin(req);
        String currentRoleCode = SecurityGuard.getRoleCode(req);

        CreatePermissionDTO dto = JsonUtil.fromJson(req, CreatePermissionDTO.class);
        ValidationUtil.validate(dto);

        Map<String, Object> created = rolePermissionService.createPermission(dto, adminId, currentRoleCode);
        JsonUtil.sendJson(resp, HttpServletResponse.SC_CREATED, Map.of(
                "statusCode", 201,
                "message", "Tạo quyền hạn thành công",
                "data", created
        ));
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer adminId = SecurityGuard.requireAdmin(req);
        String currentRoleCode = SecurityGuard.getRoleCode(req);
        Integer permissionId = parsePermissionId(req.getPathInfo());

        UpdatePermissionDTO dto = JsonUtil.fromJson(req, UpdatePermissionDTO.class);
        ValidationUtil.validate(dto);

        Map<String, Object> updated = rolePermissionService.updatePermission(permissionId, dto, adminId, currentRoleCode);
        JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of(
                "statusCode", 200,
                "message", "Cập nhật quyền hạn thành công",
                "data", updated
        ));
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer adminId = SecurityGuard.requireAdmin(req);
        String currentRoleCode = SecurityGuard.getRoleCode(req);
        Integer permissionId = parsePermissionId(req.getPathInfo());

        Map<String, Object> result = rolePermissionService.softDeletePermission(permissionId, adminId, currentRoleCode);
        JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of(
                "statusCode", 200,
                "data", result
        ));
    }

    private Integer parsePermissionId(String pathInfo) {
        if (pathInfo == null || pathInfo.length() <= 1) {
            throw new BadRequestException("Thiếu ID quyền hạn");
        }
        try {
            String idStr = pathInfo.substring(1);
            if (idStr.contains("/")) {
                idStr = idStr.substring(0, idStr.indexOf('/'));
            }
            return Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            throw new BadRequestException("ID quyền hạn không hợp lệ: " + pathInfo);
        }
    }
}
