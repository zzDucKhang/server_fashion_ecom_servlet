package com.fashionshop.business.service.fulfillment;

import com.fashionshop.business.entity.Order;
import com.fashionshop.business.entity.OrderItem;
import com.fashionshop.business.entity.ProductVariant;
import com.fashionshop.business.entity.ReturnMedia;
import com.fashionshop.business.entity.ReturnRequest;
import com.fashionshop.common.exception.BadRequestException;
import com.fashionshop.common.exception.ForbiddenException;
import com.fashionshop.common.exception.NotFoundException;
import com.fashionshop.common.response.PagedResponse;
import com.fashionshop.common.util.JPAUtil;
import com.fashionshop.dataaccess.dao.fulfillment.ReturnRequestDAO;
import com.fashionshop.presentation.dto.fulfillment.CreateReturnDTO;
import com.fashionshop.presentation.dto.fulfillment.ReturnMediaDTO;
import com.fashionshop.presentation.dto.fulfillment.ReturnQueryDTO;
import com.fashionshop.presentation.dto.fulfillment.UpdateReturnDTO;
import jakarta.persistence.EntityManager;

import java.util.ArrayList;
import java.util.List;

public class ReturnService {

    private final ReturnRequestDAO returnDAO = new ReturnRequestDAO();

    public ReturnRequest createReturn(CreateReturnDTO dto, Integer userId) {
        if (dto.getOrderItemId() == null) {
            throw new BadRequestException("Mã sản phẩm trong đơn hàng không được để trống");
        }
        if (dto.getQuantity() == null || dto.getQuantity() <= 0) {
            throw new BadRequestException("Số lượng đổi trả phải lớn hơn 0");
        }
        if (dto.getReason() == null || dto.getReason().isBlank()) {
            throw new BadRequestException("Lý do đổi trả không được để trống");
        }

        EntityManager em = JPAUtil.getEntityManager();
        try {
            OrderItem orderItem = em.find(OrderItem.class, dto.getOrderItemId());
            if (orderItem == null) throw new NotFoundException("Sản phẩm đơn hàng không tồn tại");

            if (!orderItem.getOrder().getUser().getId().equals(userId)) {
                throw new ForbiddenException("Không có quyền tạo yêu cầu cho sản phẩm này");
            }

            if (orderItem.getOrder().getStatus() != Order.OrderStatus.DELIVERED) {
                throw new BadRequestException("Chỉ có thể yêu cầu đổi trả khi đơn hàng đã giao thành công");
            }

            if (dto.getQuantity() > orderItem.getQuantity()) {
                throw new BadRequestException("Số lượng đổi trả vượt quá số lượng đã mua");
            }

            // Chống gửi trùng lặp yêu cầu đổi trả cho cùng một OrderItem
            Long existingActiveReturns = em.createQuery(
                    "SELECT COUNT(r) FROM ReturnRequest r WHERE r.orderItem.id = :orderItemId AND r.status != :rejectedStatus", Long.class)
                    .setParameter("orderItemId", dto.getOrderItemId())
                    .setParameter("rejectedStatus", ReturnRequest.ReturnStatus.REJECTED)
                    .getSingleResult();
            if (existingActiveReturns != null && existingActiveReturns > 0) {
                throw new BadRequestException("Sản phẩm này đã có yêu cầu đổi trả đang được xử lý");
            }

            ReturnRequest rr = new ReturnRequest();
            rr.setOrderItem(orderItem);
            rr.setQuantity(dto.getQuantity());
            rr.setReason(dto.getReason());
            rr.setDescription(dto.getDescription());
            rr.setStatus(ReturnRequest.ReturnStatus.PENDING);

            if (dto.getMedias() != null && !dto.getMedias().isEmpty()) {
                List<ReturnMedia> medias = new ArrayList<>();
                for (ReturnMediaDTO mDto : dto.getMedias()) {
                    ReturnMedia media = new ReturnMedia();
                    media.setReturnRequest(rr);
                    media.setMediaUrl(mDto.getMediaUrl());
                    media.setMediaType(mDto.getMediaType() != null ? mDto.getMediaType() : "IMAGE");
                    medias.add(media);
                }
                rr.setMedias(medias);
            }

            return returnDAO.create(rr);
        } finally {
            em.close();
        }
    }

    public PagedResponse<ReturnRequest> getReturnsByUser(Integer userId, ReturnQueryDTO query) {
        int page = Math.max(1, query.getPage());
        int limit = Math.max(1, Math.min(100, query.getLimit()));
        int offset = (page - 1) * limit;

        List<ReturnRequest> list = returnDAO.findByUserId(userId, offset, limit, query.getStatus());
        long total = returnDAO.countByUserId(userId, query.getStatus());

        return new PagedResponse<>(list, total, page, limit);
    }

    public ReturnRequest getReturnById(Integer id, Integer userId, boolean isAdmin) {
        ReturnRequest rr = returnDAO.findByIdWithMedias(id);
        if (rr == null) throw new NotFoundException("Yêu cầu đổi trả không tồn tại");

        if (!isAdmin && !rr.getOrderItem().getOrder().getUser().getId().equals(userId)) {
            throw new ForbiddenException("Không có quyền xem yêu cầu đổi trả này");
        }

        return rr;
    }

    public void updateReturnStatus(Integer returnId, UpdateReturnDTO dto) {
        if (dto == null || dto.getStatus() == null || dto.getStatus().isBlank()) {
            throw new BadRequestException("Trạng thái đổi trả không được để trống");
        }

        ReturnRequest.ReturnStatus newStatus;
        try {
            newStatus = ReturnRequest.ReturnStatus.valueOf(dto.getStatus().trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Trạng thái đổi trả không hợp lệ: " + dto.getStatus());
        }

        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            ReturnRequest rr = em.find(ReturnRequest.class, returnId);
            if (rr == null) throw new NotFoundException("Yêu cầu đổi trả không tồn tại");

            ReturnRequest.ReturnStatus oldStatus = rr.getStatus();
            rr.setStatus(newStatus);

            if (newStatus == ReturnRequest.ReturnStatus.COMPLETED && oldStatus != ReturnRequest.ReturnStatus.COMPLETED) {
                OrderItem orderItem = rr.getOrderItem();
                if (orderItem != null) {
                    if (orderItem.getVariant() != null) {
                        ProductVariant variant = em.find(ProductVariant.class, orderItem.getVariant().getId(), jakarta.persistence.LockModeType.PESSIMISTIC_WRITE);
                        if (variant != null) {
                            variant.updateStock(rr.getQuantity());
                        }
                    }
                    Order order = orderItem.getOrder();
                    if (order != null) {
                        order.setStatus(Order.OrderStatus.RETURNED);
                    }
                }
            }

            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public PagedResponse<ReturnRequest> listAllReturns(ReturnQueryDTO query) {
        int page = Math.max(1, query.getPage());
        int limit = Math.max(1, Math.min(100, query.getLimit()));
        int offset = (page - 1) * limit;

        List<ReturnRequest> list = returnDAO.findAll(offset, limit, query.getStatus());
        long total = returnDAO.countAll(query.getStatus());

        return new PagedResponse<>(list, total, page, limit);
    }
}

