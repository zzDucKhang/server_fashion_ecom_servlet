package com.fashionshop.presentation.controller.identity;

import com.fashionshop.business.service.identity.UserService;
import com.fashionshop.common.exception.NotFoundException;
import com.fashionshop.common.response.PagedResponse;
import com.fashionshop.common.util.SecurityGuard;
import com.fashionshop.common.util.ValidationUtil;
import com.fashionshop.presentation.controller.base.BaseHttpServlet;
import com.fashionshop.presentation.dto.identity.UpdateUserDTO;
import com.fashionshop.presentation.dto.identity.UpdateUserStatusDTO;
import com.fashionshop.presentation.dto.identity.UserQueryDTO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

@WebServlet(urlPatterns = {
    "/api/v1/identity/users",
    "/api/v1/identity/users/*",
    "/api/identity/users",
    "/api/identity/users/*"
})
public class UserServlet extends BaseHttpServlet {

    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer currentUserId = getUserId(req);
        String currentRoleCode = SecurityGuard.getRoleCode(req);
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            UserQueryDTO query = parseQueryDTO(req);
            PagedResponse<Map<String, Object>> result = userService.getUsers(query, currentRoleCode);
            sendPaged(resp, result);
        } else {
            Integer userId = parseId(pathInfo);
            Map<String, Object> result = userService.getUserById(userId, currentUserId, currentRoleCode);
            sendSuccess(resp, null, result);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer currentUserId = getUserId(req);
        String currentRoleCode = SecurityGuard.getRoleCode(req);
        Integer userId = parseId(req.getPathInfo());

        UpdateUserDTO dto = parseBody(req, UpdateUserDTO.class);
        ValidationUtil.validate(dto);

        Map<String, Object> result = userService.updateUser(userId, dto, currentUserId, currentRoleCode);
        sendSuccess(resp, "Cập nhật thông tin thành công", result);
    }

    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer currentUserId = getUserId(req);
        String currentRoleCode = SecurityGuard.getRoleCode(req);
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || !pathInfo.endsWith("/status")) {
            throw new NotFoundException("Đường dẫn không tồn tại: " + pathInfo);
        }

        Integer userId = parseId(pathInfo);
        UpdateUserStatusDTO dto = parseBody(req, UpdateUserStatusDTO.class);
        ValidationUtil.validate(dto);

        Map<String, Object> result = userService.updateUserStatus(userId, dto, currentUserId, currentRoleCode);
        sendSuccess(resp, "Cập nhật trạng thái người dùng thành công", result);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer currentUserId = getUserId(req);
        String currentRoleCode = SecurityGuard.getRoleCode(req);
        Integer userId = parseId(req.getPathInfo());

        Map<String, Object> result = userService.softDeleteUser(userId, currentUserId, currentRoleCode);
        sendSuccess(resp, null, result);
    }

    private UserQueryDTO parseQueryDTO(HttpServletRequest req) {
        UserQueryDTO query = new UserQueryDTO();
        query.setPage(getIntParameter(req, "page", query.getPage()));
        query.setLimit(getIntParameter(req, "limit", query.getLimit()));
        query.setSearch(req.getParameter("search"));
        query.setStatus(req.getParameter("status"));
        query.setRoleId(getIntParameter(req, "roleId", null));
        return query;
    }
}
