package com.fashionshop.business.service.fulfillment;

import com.fashionshop.business.entity.Order;
import com.fashionshop.business.entity.Shipment;
import com.fashionshop.business.service.cartorder.OrderService;
import com.fashionshop.common.exception.BadRequestException;
import com.fashionshop.common.exception.ForbiddenException;
import com.fashionshop.common.exception.NotFoundException;
import com.fashionshop.common.response.PagedResponse;
import com.fashionshop.common.util.JPAUtil;
import com.fashionshop.dataaccess.dao.fulfillment.ShipmentDAO;
import com.fashionshop.presentation.dto.fulfillment.CreateShipmentDTO;
import com.fashionshop.presentation.dto.fulfillment.ShipmentQueryDTO;
import com.fashionshop.presentation.dto.fulfillment.UpdateShipmentDTO;
import jakarta.persistence.EntityManager;

import java.time.LocalDateTime;
import java.util.List;

public class ShipmentService {

    private final ShipmentDAO shipmentDAO = new ShipmentDAO();
    private final OrderService orderService = new OrderService();

    public Shipment createShipment(CreateShipmentDTO dto) {
        if (dto.getOrderId() == null) {
            throw new BadRequestException("Mã đơn hàng không được để trống");
        }
        if (dto.getCarrier() == null || dto.getCarrier().isBlank()) {
            throw new BadRequestException("Đơn vị vận chuyển không được để trống");
        }
        if (dto.getTrackingCode() == null || dto.getTrackingCode().isBlank()) {
            throw new BadRequestException("Mã vận đơn không được để trống");
        }

        EntityManager em = JPAUtil.getEntityManager();
        try {
            Order order = em.find(Order.class, dto.getOrderId());
            if (order == null) throw new NotFoundException("Đơn hàng không tồn tại");

            Shipment existing = shipmentDAO.findByOrderId(dto.getOrderId());
            if (existing != null) {
                throw new BadRequestException("Đơn hàng đã có thông tin vận chuyển");
            }

            Shipment shipment = new Shipment();
            shipment.setOrder(order);
            shipment.setCarrier(dto.getCarrier());
            shipment.setTrackingCode(dto.getTrackingCode());
            shipment.setActualShippingFee(dto.getActualShippingFee() != null ? dto.getActualShippingFee() : order.getShippingFee());
            shipment.setStatus(Shipment.ShipmentStatus.PENDING);

            return shipmentDAO.create(shipment);
        } finally {
            em.close();
        }
    }

    public Shipment getShipmentByOrderId(Integer orderId, Integer userId, boolean isAdmin) {
        Shipment shipment = shipmentDAO.findByOrderId(orderId);
        if (shipment == null) throw new NotFoundException("Thông tin vận chuyển không tồn tại");
        if (!isAdmin && (userId == null || !shipment.getOrder().getUser().getId().equals(userId))) {
            throw new ForbiddenException("Không có quyền xem thông tin vận chuyển này");
        }
        return shipment;
    }

    public Shipment getShipmentByOrderId(Integer orderId) {
        return getShipmentByOrderId(orderId, null, true);
    }

    public Shipment getShipmentById(Integer id, Integer userId, boolean isAdmin) {
        Shipment shipment = shipmentDAO.findById(id);
        if (shipment == null) throw new NotFoundException("Thông tin vận chuyển không tồn tại");
        if (!isAdmin && (userId == null || !shipment.getOrder().getUser().getId().equals(userId))) {
            throw new ForbiddenException("Không có quyền xem thông tin vận đơn này");
        }
        return shipment;
    }

    public Shipment getShipmentById(Integer id) {
        return getShipmentById(id, null, true);
    }

    public void updateShipmentStatus(Integer shipmentId, UpdateShipmentDTO dto) {
        Shipment shipment = shipmentDAO.findById(shipmentId);
        if (shipment == null) throw new NotFoundException("Vận đơn không tồn tại");

        if (dto.getStatus() == null || dto.getStatus().isBlank()) {
            throw new BadRequestException("Trạng thái vận đơn không được để trống");
        }

        try {
            Shipment.ShipmentStatus newStatus = Shipment.ShipmentStatus.valueOf(dto.getStatus().toUpperCase());
            shipmentDAO.updateStatus(shipmentId, newStatus, dto.getTrackingCode());
            if (shipment.getOrder() != null) {
                if (newStatus == Shipment.ShipmentStatus.DELIVERED) {
                    orderService.updateOrderStatus(shipment.getOrder().getId(), Order.OrderStatus.DELIVERED.name());
                } else if (newStatus == Shipment.ShipmentStatus.IN_TRANSIT || newStatus == Shipment.ShipmentStatus.PICKED_UP) {
                    orderService.updateOrderStatus(shipment.getOrder().getId(), Order.OrderStatus.SHIPPED.name());
                }
            }
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Trạng thái vận đơn không hợp lệ: " + dto.getStatus());
        }
    }

    public PagedResponse<Shipment> listShipments(ShipmentQueryDTO query) {
        int page = Math.max(1, query.getPage());
        int limit = Math.max(1, Math.min(100, query.getLimit()));
        int offset = (page - 1) * limit;

        List<Shipment> shipments = shipmentDAO.findAll(offset, limit, query.getStatus());
        long total = shipmentDAO.countAll(query.getStatus());

        return new PagedResponse<>(shipments, total, page, limit);
    }
}

