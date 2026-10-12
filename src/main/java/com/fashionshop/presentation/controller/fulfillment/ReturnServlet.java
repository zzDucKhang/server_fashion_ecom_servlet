package com.fashionshop.presentation.controller.fulfillment;

import com.fashionshop.business.entity.ReturnRequest;
import com.fashionshop.business.service.fulfillment.ReturnService;
import com.fashionshop.common.response.ApiResponse;
import com.fashionshop.presentation.dto.fulfillment.CreateReturnDTO;
import com.fashionshop.presentation.dto.fulfillment.ReturnQueryDTO;
import com.fashionshop.presentation.dto.fulfillment.UpdateReturnDTO;
import com.fashionshop.common.util.JsonUtil;
import com.fashionshop.common.util.SecurityGuard;
import com.fashionshop.common.util.ValidationUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = {"/api/v1/fulfillment/returns/*", "/api/v1/returns/*"})
public class ReturnServlet extends HttpServlet {
    private final ReturnService returnService = new ReturnService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            // GET / (Admin only)
            SecurityGuard.requireAdmin(req);
            ReturnQueryDTO query = new ReturnQueryDTO();
            if (req.getParameter("status") != null) {
                query.setStatus(ReturnRequest.ReturnStatus.valueOf(req.getParameter("status")));
            }
            if (req.getParameter("orderItemId") != null) {
                query.setOrderItemId(Integer.parseInt(req.getParameter("orderItemId")));
            }

            List<ReturnRequest> list = returnService.findAll(query);
            JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of("data", list, "totalItems", list.size()));
        } else {
            // GET /:returnId (Require Auth)
            SecurityGuard.requireAuth(req);
            int returnId = Integer.parseInt(pathInfo.substring(1).split("/")[0]);
            ReturnRequest returnRequest = returnService.findById(returnId);
            JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, ApiResponse.ok(returnRequest));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // POST / (Khách hàng tạo yêu cầu trả hàng)
        SecurityGuard.requireAuth(req);
        CreateReturnDTO dto = JsonUtil.fromJson(req, CreateReturnDTO.class);
        ValidationUtil.validate(dto);
        ReturnRequest created = returnService.create(dto);

        JsonUtil.sendJson(resp, HttpServletResponse.SC_CREATED, Map.of(
                "statusCode", 201,
                "message", "Tạo yêu cầu trả hàng thành công",
                "data", created
        ));
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // PUT /:returnId (Admin duyệt/cập nhật)
        SecurityGuard.requireAdmin(req);
        int returnId = Integer.parseInt(req.getPathInfo().substring(1).split("/")[0]);
        UpdateReturnDTO dto = JsonUtil.fromJson(req, UpdateReturnDTO.class);
        ValidationUtil.validate(dto);
        ReturnRequest updated = returnService.update(returnId, dto);

        JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of(
                "statusCode", 200,
                "message", "Cập nhật yêu cầu trả hàng thành công",
                "data", updated
        ));
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // DELETE /:returnId (Admin xóa yêu cầu PENDING)
        SecurityGuard.requireAdmin(req);
        int returnId = Integer.parseInt(req.getPathInfo().substring(1).split("/")[0]);
        returnService.delete(returnId);

        JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of(
                "statusCode", 200,
                "message", "Xóa yêu cầu trả hàng thành công"
        ));
    }
}