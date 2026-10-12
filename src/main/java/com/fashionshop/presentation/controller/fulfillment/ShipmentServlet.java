package com.fashionshop.presentation.controller.fulfillment;

import com.fashionshop.business.entity.Shipment;
import com.fashionshop.business.service.fulfillment.ShipmentService;
import com.fashionshop.common.response.ApiResponse;
import com.fashionshop.common.response.PagedResponse;
import com.fashionshop.common.util.JsonUtil;
import com.fashionshop.presentation.dto.fulfillment.CreateShipmentDTO;
import com.fashionshop.presentation.dto.fulfillment.ShipmentQueryDTO;
import com.fashionshop.presentation.dto.fulfillment.UpdateShipmentDTO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(urlPatterns = {"/api/v1/fulfillment/shipments/*", "/api/shipments/*"})
public class ShipmentServlet extends HttpServlet {

    private final ShipmentService shipmentService = new ShipmentService();

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
            String orderIdStr = req.getParameter("orderId");
            if (orderIdStr != null && !orderIdStr.isBlank()) {
                // GET ?orderId=123
                try {
                    Integer orderId = Integer.parseInt(orderIdStr.trim());
                    Shipment shipment = shipmentService.getShipmentByOrderId(orderId, userId, admin);
                    JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, ApiResponse.success(200, "Thông tin vận chuyển", shipment));
                } catch (NumberFormatException e) {
                    JsonUtil.sendJson(resp, HttpServletResponse.SC_BAD_REQUEST, ApiResponse.error(400, "Mã đơn hàng không hợp lệ"));
                }
            } else {
                // GET / - Danh sách phân trang chỉ dành cho Staff/Admin
                if (!admin) {
                    JsonUtil.sendJson(resp, HttpServletResponse.SC_FORBIDDEN, ApiResponse.error(403, "Bạn không có quyền xem danh sách vận đơn"));
                    return;
                }

                ShipmentQueryDTO query = new ShipmentQueryDTO();
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
                        Shipment.ShipmentStatus.valueOf(status.trim().toUpperCase());
                        query.setStatus(status.trim().toUpperCase());
                    } catch (IllegalArgumentException e) {
                        JsonUtil.sendJson(resp, HttpServletResponse.SC_BAD_REQUEST, ApiResponse.error(400, "Trạng thái vận đơn không hợp lệ: " + status));
                        return;
                    }
                }

                PagedResponse<Shipment> result = shipmentService.listShipments(query);
                JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, ApiResponse.success(200, "Danh sách vận đơn", result));
            }
        } else {
            // GET /{id}
            try {
                String cleanPath = pathInfo.replaceFirst("^/", "");
                Integer id = Integer.parseInt(cleanPath.trim());
                Shipment shipment = shipmentService.getShipmentById(id, userId, admin);
                JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, ApiResponse.success(200, "Chi tiết vận đơn", shipment));
            } catch (NumberFormatException e) {
                JsonUtil.sendJson(resp, HttpServletResponse.SC_BAD_REQUEST, ApiResponse.error(400, "Mã vận đơn không hợp lệ"));
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!isStaffOrAdmin(req)) {
            JsonUtil.sendJson(resp, HttpServletResponse.SC_FORBIDDEN, ApiResponse.error(403, "Bạn không có quyền thực hiện thao tác này"));
            return;
        }

        CreateShipmentDTO dto = JsonUtil.fromJson(req.getReader(), CreateShipmentDTO.class);
        Shipment shipment = shipmentService.createShipment(dto);
        JsonUtil.sendJson(resp, HttpServletResponse.SC_CREATED, ApiResponse.success(201, "Tạo vận đơn thành công", shipment));
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
                Integer shipmentId = Integer.parseInt(idPart);
                UpdateShipmentDTO dto = JsonUtil.fromJson(req.getReader(), UpdateShipmentDTO.class);
                shipmentService.updateShipmentStatus(shipmentId, dto);
                JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, ApiResponse.success(200, "Cập nhật trạng thái vận đơn thành công", null));
            } catch (NumberFormatException e) {
                JsonUtil.sendJson(resp, HttpServletResponse.SC_BAD_REQUEST, ApiResponse.error(400, "Mã vận đơn không hợp lệ"));
            }
        } else {
            JsonUtil.sendJson(resp, HttpServletResponse.SC_NOT_FOUND, ApiResponse.error(404, "Endpoint không tồn tại"));
        }
    }
}

