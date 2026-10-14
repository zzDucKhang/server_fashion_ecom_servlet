package com.fashionshop.presentation.controller.fulfillment;

import com.fashionshop.business.entity.ReturnRequest;
import com.fashionshop.business.service.fulfillment.ReturnService;
import com.fashionshop.common.util.SecurityGuard;
import com.fashionshop.common.util.ValidationUtil;
import com.fashionshop.presentation.controller.base.BaseHttpServlet;
import com.fashionshop.presentation.dto.fulfillment.CreateReturnDTO;
import com.fashionshop.presentation.dto.fulfillment.ReturnQueryDTO;
import com.fashionshop.presentation.dto.fulfillment.UpdateReturnDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = {
    "/api/v1/fulfillment/returns",
    "/api/v1/fulfillment/returns/*",
    "/api/v1/returns",
    "/api/v1/returns/*"
})
public class ReturnServlet extends BaseHttpServlet {
    private final ReturnService returnService = new ReturnService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/") || pathInfo.isBlank()) {
            // GET / (Admin list returns)
            SecurityGuard.requireAdmin(req);
            ReturnQueryDTO query = new ReturnQueryDTO();
            if (req.getParameter("status") != null) {
                try {
                    query.setStatus(ReturnRequest.ReturnStatus.valueOf(req.getParameter("status")));
                } catch (IllegalArgumentException e) {
                    // Ignore invalid status enum filter
                }
            }
            query.setOrderItemId(getIntParameter(req, "orderItemId", null));

            List<ReturnRequest> list = returnService.findAll(query);
            sendSuccess(resp, "Danh sách yêu cầu đổi trả", list);
        } else {
            // GET /:returnId (Require Auth)
            SecurityGuard.requireAuth(req);
            int returnId = parseId(pathInfo);
            ReturnRequest returnRequest = returnService.findById(returnId);
            sendSuccess(resp, "Chi tiết yêu cầu đổi trả", returnRequest);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // POST / (Khách hàng tạo yêu cầu trả hàng)
        SecurityGuard.requireAuth(req);
        CreateReturnDTO dto = parseBody(req, CreateReturnDTO.class);
        ValidationUtil.validate(dto);
        ReturnRequest created = returnService.create(dto);

        sendJson(resp, HttpServletResponse.SC_CREATED, Map.of(
                "statusCode", 201,
                "message", "Tạo yêu cầu trả hàng thành công",
                "data", created
        ));
    }

    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handleReturnStatusUpdate(req, resp);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handleReturnStatusUpdate(req, resp);
    }

    private void handleReturnStatusUpdate(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // PATCH hoặc PUT /:returnId hoặc /:returnId/status (Admin duyệt/cập nhật)
        SecurityGuard.requireAdmin(req);
        int returnId = parseId(req.getPathInfo());
        UpdateReturnDTO dto = parseBody(req, UpdateReturnDTO.class);
        ValidationUtil.validate(dto);
        ReturnRequest updated = returnService.update(returnId, dto);

        sendSuccess(resp, "Cập nhật yêu cầu trả hàng thành công", updated);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // DELETE /:returnId (Admin xóa yêu cầu PENDING)
        SecurityGuard.requireAdmin(req);
        int returnId = parseId(req.getPathInfo());
        returnService.delete(returnId);

        sendSuccess(resp, "Xóa yêu cầu trả hàng thành công", null);
    }
}