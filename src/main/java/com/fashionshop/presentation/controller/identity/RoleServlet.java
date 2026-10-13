package com.fashionshop.presentation.controller.identity;

import com.fashionshop.business.service.identity.RolePermissionService;
import com.fashionshop.common.util.SecurityGuard;
import com.fashionshop.common.util.ValidationUtil;
import com.fashionshop.presentation.controller.base.BaseHttpServlet;
import com.fashionshop.presentation.dto.identity.CreateRoleDTO;
import com.fashionshop.presentation.dto.identity.UpdateRoleDTO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet("/api/v1/identity/roles/*")
public class RoleServlet extends BaseHttpServlet {

    private final RolePermissionService rolePermissionService = new RolePermissionService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        SecurityGuard.requireAdmin(req);
        String currentRoleCode = SecurityGuard.getRoleCode(req);
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            // GET /api/v1/identity/roles
            List<Map<String, Object>> roles = rolePermissionService.getAllRoles(currentRoleCode);
            sendSuccess(resp, null, roles);
        } else {
            // GET /api/v1/identity/roles/:roleId
            Integer roleId = parseId(pathInfo);
            Map<String, Object> role = rolePermissionService.getRoleById(roleId, currentRoleCode);
            sendSuccess(resp, null, role);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer adminId = SecurityGuard.requireAdmin(req);
        String currentRoleCode = SecurityGuard.getRoleCode(req);

        CreateRoleDTO dto = parseBody(req, CreateRoleDTO.class);
        ValidationUtil.validate(dto);

        Map<String, Object> created = rolePermissionService.createRole(dto, adminId, currentRoleCode);
        sendJson(resp, HttpServletResponse.SC_CREATED, Map.of(
                "statusCode", 201,
                "message", "Tạo vai trò thành công",
                "data", created
        ));
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer adminId = SecurityGuard.requireAdmin(req);
        String currentRoleCode = SecurityGuard.getRoleCode(req);
        Integer roleId = parseId(req.getPathInfo());

        UpdateRoleDTO dto = parseBody(req, UpdateRoleDTO.class);
        ValidationUtil.validate(dto);

        Map<String, Object> updated = rolePermissionService.updateRole(roleId, dto, adminId, currentRoleCode);
        sendSuccess(resp, "Cập nhật vai trò thành công", updated);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer adminId = SecurityGuard.requireAdmin(req);
        String currentRoleCode = SecurityGuard.getRoleCode(req);
        Integer roleId = parseId(req.getPathInfo());

        Map<String, Object> result = rolePermissionService.softDeleteRole(roleId, adminId, currentRoleCode);
        sendSuccess(resp, null, result);
    }
}
