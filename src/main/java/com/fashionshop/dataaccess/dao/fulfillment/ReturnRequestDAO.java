package com.fashionshop.dataaccess.dao.fulfillment;

import com.fashionshop.business.entity.OrderItem;
import com.fashionshop.business.entity.ReturnMedia;
import com.fashionshop.business.entity.ReturnRequest;
import com.fashionshop.dataaccess.base.AbstractJpaDAO;
import com.fashionshop.presentation.dto.fulfillment.CreateReturnDTO;
import com.fashionshop.presentation.dto.fulfillment.ReturnMediaDTO;
import com.fashionshop.presentation.dto.fulfillment.ReturnQueryDTO;
import com.fashionshop.presentation.dto.fulfillment.UpdateReturnDTO;
import jakarta.persistence.TypedQuery;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class ReturnRequestDAO extends AbstractJpaDAO<ReturnRequest, Integer> {

    public ReturnRequestDAO() {
        super(ReturnRequest.class);
    }

    public List<ReturnRequest> findAllWithFilter(ReturnQueryDTO query) {
        return execute(em -> {
            StringBuilder jpql = new StringBuilder(
                    "SELECT DISTINCT rr FROM ReturnRequest rr " +
                    "LEFT JOIN FETCH rr.orderItem " +
                    "LEFT JOIN FETCH rr.medias " +
                    "WHERE rr.deletedAt IS NULL");

            if (query.getStatus() != null) {
                jpql.append(" AND rr.status = :status");
            }
            if (query.getOrderItemId() != null) {
                jpql.append(" AND rr.orderItem.id = :orderItemId");
            }
            jpql.append(" ORDER BY rr.createdAt DESC");

            TypedQuery<ReturnRequest> tq = em.createQuery(jpql.toString(), ReturnRequest.class);
            if (query.getStatus() != null) {
                tq.setParameter("status", query.getStatus());
            }
            if (query.getOrderItemId() != null) {
                tq.setParameter("orderItemId", query.getOrderItemId());
            }

            return tq.getResultList();
        });
    }

    public Optional<ReturnRequest> findDetailById(int id) {
        return execute(em -> {
            String jpql = "SELECT DISTINCT rr FROM ReturnRequest rr " +
                    "LEFT JOIN FETCH rr.orderItem " +
                    "LEFT JOIN FETCH rr.medias " +
                    "WHERE rr.id = :id AND rr.deletedAt IS NULL";

            List<ReturnRequest> list = em.createQuery(jpql, ReturnRequest.class)
                    .setParameter("id", id)
                    .setMaxResults(1)
                    .getResultList();

            return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
        });
    }

    public ReturnRequest create(CreateReturnDTO dto) {
        return executeTransaction(em -> {
            OrderItem orderItem = em.find(OrderItem.class, dto.getOrderItemId());

            ReturnRequest rr = new ReturnRequest();
            rr.setOrderItem(orderItem);
            rr.setQuantity(dto.getQuantity());
            rr.setReason(dto.getReason().trim());
            rr.setDescription(dto.getDescription());
            rr.setStatus(ReturnRequest.ReturnStatus.PENDING);

            em.persist(rr);

            if (dto.getMedias() != null && !dto.getMedias().isEmpty()) {
                for (ReturnMediaDTO mDto : dto.getMedias()) {
                    ReturnMedia media = new ReturnMedia();
                    media.setReturnRequest(rr);
                    media.setMediaUrl(mDto.getMediaUrl());
                    media.setMediaType(mDto.getMediaType());
                    rr.getMedias().add(media);
                    em.persist(media);
                }
            }

            return rr;
        });
    }

    public ReturnRequest update(int id, UpdateReturnDTO dto) {
        return executeTransaction(em -> {
            ReturnRequest rr = em.find(ReturnRequest.class, id);
            if (rr == null) {
                throw new com.fashionshop.common.exception.NotFoundException("Yêu cầu trả hàng không tồn tại: " + id);
            }

            if (dto.getQuantity() != null) rr.setQuantity(dto.getQuantity());
            if (dto.getReason() != null) rr.setReason(dto.getReason().trim());
            if (dto.getDescription() != null) rr.setDescription(dto.getDescription());
            if (dto.getStatus() != null) rr.setStatus(dto.getStatus());

            return rr;
        });
    }

    public void softDelete(int id) {
        executeTransaction(em -> {
            ReturnRequest rr = em.find(ReturnRequest.class, id);
            if (rr != null) {
                rr.setDeletedAt(LocalDateTime.now());
            }
            return null;
        });
    }
}