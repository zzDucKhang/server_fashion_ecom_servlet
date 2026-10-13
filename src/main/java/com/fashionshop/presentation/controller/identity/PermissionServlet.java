package com.fashionshop.presentation.controller.identity;

import com.fashionshop.business.service.identity.RolePermissionService;
import com.fashionshop.common.util.SecurityGuard;
import com.fashionshop.common.util.ValidationUtil;
import com.fashionshop.presentation.controller.base.BaseHttpServlet;
import com.fashionshop.presentation.dto.identity.CreatePermissionDTO;
import com.fashionshop.presentation.dto.identity.UpdatePermissionDTO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = {
        "/api/v1/identity/roles/permissions",
        "/api/v1/identity/roles/permissions/*"
})
public class PermissionServlet extends BaseHttpServlet {

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
            sendSuccess(resp, null, permissions);
        } else {
            // GET /api/v1/identity/roles/permissions/:permissionId
            Integer permissionId = parseId(pathInfo);
            Map<String, Object> permission = rolePermissionService.getPermissionById(permissionId, currentRoleCode);
            sendSuccess(resp, null, permission);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer adminId = SecurityGuard.requireAdmin(req);
        String currentRoleCode = SecurityGuard.getRoleCode(req);

        CreatePermissionDTO dto = parseBody(req, CreatePermissionDTO.class);
        ValidationUtil.validate(dto);

        Map<String, Object> created = rolePermissionService.createPermission(dto, adminId, currentRoleCode);
        sendJson(resp, HttpServletResponse.SC_CREATED, Map.of(
                "statusCode", 201,
                "message", "Tạo quyền hạn thành công",
                "data", created
        ));
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer adminId = SecurityGuard.requireAdmin(req);
        String currentRoleCode = SecurityGuard.getRoleCode(req);
        Integer permissionId = parseId(req.getPathInfo());

        UpdatePermissionDTO dto = parseBody(req, UpdatePermissionDTO.class);
        ValidationUtil.validate(dto);

        Map<String, Object> updated = rolePermissionService.updatePermission(permissionId, dto, adminId, currentRoleCode);
        sendSuccess(resp, "Cập nhật quyền hạn thành công", updated);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer adminId = SecurityGuard.requireAdmin(req);
        String currentRoleCode = SecurityGuard.getRoleCode(req);
        Integer permissionId = parseId(req.getPathInfo());

        Map<String, Object> result = rolePermissionService.softDeletePermission(permissionId, adminId, currentRoleCode);
        sendSuccess(resp, null, result);
    }
}
