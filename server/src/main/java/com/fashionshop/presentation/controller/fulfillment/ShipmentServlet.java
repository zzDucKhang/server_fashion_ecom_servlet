package com.fashionshop.presentation.controller.fulfillment;

import com.fashionshop.business.entity.Shipment;
import com.fashionshop.business.service.fulfillment.ShipmentService;
import com.fashionshop.common.response.PagedResponse;
import com.fashionshop.common.util.ValidationUtil;
import com.fashionshop.presentation.controller.base.BaseHttpServlet;
import com.fashionshop.presentation.dto.fulfillment.CreateShipmentDTO;
import com.fashionshop.presentation.dto.fulfillment.ShipmentQueryDTO;
import com.fashionshop.presentation.dto.fulfillment.UpdateShipmentDTO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(urlPatterns = {
    "/api/v1/fulfillment/shipments",
    "/api/v1/fulfillment/shipments/*",
    "/api/shipments",
    "/api/shipments/*"
})
public class ShipmentServlet extends BaseHttpServlet {

    private final ShipmentService shipmentService = new ShipmentService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer userId = getUserId(req);
        boolean admin = isStaffOrAdmin(req);
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || "/".equals(pathInfo) || pathInfo.isBlank()) {
            Integer orderId = getIntParameter(req, "orderId", null);
            if (orderId != null) {
                Shipment shipment = shipmentService.getShipmentByOrderId(orderId, userId, admin);
                sendSuccess(resp, "Thông tin vận chuyển", shipment);
            } else {
                if (!admin) {
                    sendError(resp, HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền xem danh sách vận đơn");
                    return;
                }

                ShipmentQueryDTO query = new ShipmentQueryDTO();
                query.setPage(getIntParameter(req, "page", 1));
                query.setLimit(getIntParameter(req, "limit", 10));

                String status = req.getParameter("status");
                if (status != null && !status.isBlank()) {
                    try {
                        Shipment.ShipmentStatus.valueOf(status.trim().toUpperCase());
                        query.setStatus(status.trim().toUpperCase());
                    } catch (IllegalArgumentException e) {
                        sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Trạng thái vận đơn không hợp lệ: " + status);
                        return;
                    }
                }

                PagedResponse<Shipment> result = shipmentService.listShipments(query);
                // Dùng sendPaged để trả về { statusCode: 200, data: [...], totalItems, page, limit, totalPages } khớp với admin/shipments/page.tsx
                sendPaged(resp, result);
            }
        } else if (pathInfo.startsWith("/by-order/")) {
            Integer orderId = parseId(pathInfo.substring(9));
            Shipment shipment = shipmentService.getShipmentByOrderId(orderId, userId, admin);
            sendSuccess(resp, "Thông tin vận chuyển", shipment);
        } else {
            Integer id = parseId(pathInfo);
            Shipment shipment = shipmentService.getShipmentById(id, userId, admin);
            sendSuccess(resp, "Chi tiết vận đơn", shipment);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!isStaffOrAdmin(req)) {
            sendError(resp, HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền thực hiện thao tác này");
            return;
        }

        CreateShipmentDTO dto = parseBody(req, CreateShipmentDTO.class);
        ValidationUtil.validate(dto);
        Shipment shipment = shipmentService.createShipment(dto);
        sendJson(resp, HttpServletResponse.SC_CREATED, java.util.Map.of(
                "statusCode", 201,
                "message", "Tạo lô hàng thành công",
                "data", shipment
        ));
    }

    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        doPut(req, resp);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!isStaffOrAdmin(req)) {
            sendError(resp, HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền thực hiện thao tác này");
            return;
        }

        String pathInfo = req.getPathInfo();
        if (pathInfo != null && !pathInfo.equals("/") && !pathInfo.isBlank()) {
            Integer shipmentId = parseId(pathInfo);
            UpdateShipmentDTO dto = parseBody(req, UpdateShipmentDTO.class);
            ValidationUtil.validate(dto);
            shipmentService.updateShipmentStatus(shipmentId, dto);
            Shipment updated = shipmentService.getShipmentById(shipmentId);
            sendSuccess(resp, "Cập nhật lô hàng thành công", updated);
        } else {
            sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Endpoint không tồn tại");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!isStaffOrAdmin(req)) {
            sendError(resp, HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền thực hiện thao tác này");
            return;
        }
        Integer id = parseId(req.getPathInfo());
        shipmentService.deleteShipment(id);
        sendSuccess(resp, "Xóa lô hàng thành công", null);
    }
}