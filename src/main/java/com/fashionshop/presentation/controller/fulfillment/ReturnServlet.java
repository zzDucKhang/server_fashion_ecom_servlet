package com.fashionshop.presentation.controller.fulfillment;

import com.fashionshop.business.entity.ReturnRequest;
import com.fashionshop.business.service.fulfillment.ReturnService;
import com.fashionshop.common.response.ApiResponse;
import com.fashionshop.common.response.PagedResponse;
import com.fashionshop.common.util.JsonUtil;
import com.fashionshop.presentation.dto.fulfillment.CreateReturnDTO;
import com.fashionshop.presentation.dto.fulfillment.ReturnQueryDTO;
import com.fashionshop.presentation.dto.fulfillment.UpdateReturnDTO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(urlPatterns = {"/api/v1/fulfillment/returns/*", "/api/returns/*"})
public class ReturnServlet extends HttpServlet {

    private final ReturnService returnService = new ReturnService();

    private Integer getUserId(HttpServletRequest req) {
        Integer id = (Integer) req.getAttribute("currentUserId");
        if (id == null) {
            id = (Integer) req.getAttribute("userId");
        }
        return id;
    }

    private boolean isStaffOrAdmin(HttpServletRequest req) {
        Integer roleId = (Integer) req.getAttribute("currentRoleId");
        return roleId != null && (roleId == 1 || roleId == 2);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer userId = getUserId(req);
        if (userId == null) {
            JsonUtil.sendJson(resp, HttpServletResponse.SC_UNAUTHORIZED, ApiResponse.error(401, "Chưa xác thực"));
            return;
        }

        boolean admin = isStaffOrAdmin(req);
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || "/".equals(pathInfo) || pathInfo.isBlank()) {
            ReturnQueryDTO query = new ReturnQueryDTO();
            String pageStr = req.getParameter("page");
            String limitStr = req.getParameter("limit");
            String status = req.getParameter("status");

            try {
                if (pageStr != null && !pageStr.isBlank()) query.setPage(Integer.parseInt(pageStr.trim()));
                if (limitStr != null && !limitStr.isBlank()) query.setLimit(Integer.parseInt(limitStr.trim()));
            } catch (NumberFormatException e) {
                JsonUtil.sendJson(resp, HttpServletResponse.SC_BAD_REQUEST, ApiResponse.error(400, "Tham số phân trang không hợp lệ"));
                return;
            }

            if (status != null && !status.isBlank()) {
                try {
                    ReturnRequest.ReturnStatus.valueOf(status.trim().toUpperCase());
                    query.setStatus(status.trim().toUpperCase());
                } catch (IllegalArgumentException e) {
                    JsonUtil.sendJson(resp, HttpServletResponse.SC_BAD_REQUEST, ApiResponse.error(400, "Trạng thái đổi trả không hợp lệ: " + status));
                    return;
                }
            }

            PagedResponse<ReturnRequest> result = admin
                    ? returnService.listAllReturns(query)
                    : returnService.getReturnsByUser(userId, query);
            JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, ApiResponse.success(200, "Danh sách yêu cầu đổi trả", result));
        } else {
            // GET /{id}
            try {
                String cleanPath = pathInfo.replaceFirst("^/", "");
                Integer id = Integer.parseInt(cleanPath.trim());
                ReturnRequest rr = returnService.getReturnById(id, userId, admin);
                JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, ApiResponse.success(200, "Chi tiết đổi trả", rr));
            } catch (NumberFormatException e) {
                JsonUtil.sendJson(resp, HttpServletResponse.SC_BAD_REQUEST, ApiResponse.error(400, "Mã đổi trả không hợp lệ"));
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer userId = getUserId(req);
        if (userId == null) {
            JsonUtil.sendJson(resp, HttpServletResponse.SC_UNAUTHORIZED, ApiResponse.error(401, "Chưa xác thực"));
            return;
        }

        CreateReturnDTO dto = JsonUtil.fromJson(req.getReader(), CreateReturnDTO.class);
        ReturnRequest rr = returnService.createReturn(dto, userId);
        JsonUtil.sendJson(resp, HttpServletResponse.SC_CREATED, ApiResponse.success(201, "Tạo yêu cầu đổi trả thành công", rr));
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!isStaffOrAdmin(req)) {
            JsonUtil.sendJson(resp, HttpServletResponse.SC_FORBIDDEN, ApiResponse.error(403, "Bạn không có quyền thực hiện thao tác này"));
            return;
        }

        String pathInfo = req.getPathInfo();
        if (pathInfo != null && pathInfo.contains("/status")) {
            try {
                String idPart = pathInfo.replaceFirst("^/", "").replace("/status", "");
                Integer returnId = Integer.parseInt(idPart);
                UpdateReturnDTO dto = JsonUtil.fromJson(req.getReader(), UpdateReturnDTO.class);
                returnService.updateReturnStatus(returnId, dto);
                JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, ApiResponse.success(200, "Cập nhật yêu cầu đổi trả thành công", null));
            } catch (NumberFormatException e) {
                JsonUtil.sendJson(resp, HttpServletResponse.SC_BAD_REQUEST, ApiResponse.error(400, "Mã đổi trả không hợp lệ"));
            }
        } else {
            JsonUtil.sendJson(resp, HttpServletResponse.SC_NOT_FOUND, ApiResponse.error(404, "Endpoint không tồn tại"));
        }
    }
}

