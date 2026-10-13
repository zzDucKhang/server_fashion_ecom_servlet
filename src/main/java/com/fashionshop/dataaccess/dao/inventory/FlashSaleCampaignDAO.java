package com.fashionshop.dataaccess.dao.inventory;

import com.fashionshop.business.entity.FlashSaleCampaign;
import com.fashionshop.business.entity.FlashSaleItem;
import com.fashionshop.business.entity.ProductVariant;
import com.fashionshop.dataaccess.base.AbstractJpaDAO;
import com.fashionshop.presentation.dto.inventory.CreateFlashSaleDTO;
import com.fashionshop.presentation.dto.inventory.FlashSaleItemDTO;
import com.fashionshop.presentation.dto.inventory.FlashSaleQueryDTO;
import com.fashionshop.presentation.dto.inventory.UpdateFlashSaleDTO;
import jakarta.persistence.TypedQuery;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class FlashSaleCampaignDAO extends AbstractJpaDAO<FlashSaleCampaign, Integer> {

    public FlashSaleCampaignDAO() {
        super(FlashSaleCampaign.class);
    }

    public List<FlashSaleCampaign> findActiveCampaigns() {
        return execute(em -> {
            LocalDateTime now = LocalDateTime.now();
            String jpql = "SELECT DISTINCT c FROM FlashSaleCampaign c " +
                    "LEFT JOIN FETCH c.items i " +
                    "LEFT JOIN FETCH i.variant v " +
                    "LEFT JOIN FETCH v.product p " +
                    "WHERE c.deletedAt IS NULL " +
                    "  AND c.isActive = true " +
                    "  AND :now >= c.startTime AND :now <= c.endTime " +
                    "ORDER BY c.endTime ASC";

            return em.createQuery(jpql, FlashSaleCampaign.class)
                    .setParameter("now", now)
                    .getResultList();
        });
    }

    public Optional<FlashSaleCampaign> findDetailById(int id) {
        return execute(em -> {
            String jpql = "SELECT DISTINCT c FROM FlashSaleCampaign c " +
                    "LEFT JOIN FETCH c.items i " +
                    "LEFT JOIN FETCH i.variant v " +
                    "LEFT JOIN FETCH v.product p " +
                    "WHERE c.id = :id AND c.deletedAt IS NULL";

            List<FlashSaleCampaign> list = em.createQuery(jpql, FlashSaleCampaign.class)
                    .setParameter("id", id)
                    .setMaxResults(1)
                    .getResultList();

            return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
        });
    }

    public List<FlashSaleCampaign> list(FlashSaleQueryDTO query) {
        return execute(em -> {
            StringBuilder jpql = new StringBuilder(
                    "SELECT DISTINCT c FROM FlashSaleCampaign c " +
                    "LEFT JOIN FETCH c.items i " +
                    "LEFT JOIN FETCH i.variant v " +
                    "WHERE c.deletedAt IS NULL");

            if (query.getIsActive() != null) {
                jpql.append(" AND c.isActive = :isActive");
            }
            jpql.append(" ORDER BY c.createdAt DESC");

            TypedQuery<FlashSaleCampaign> tq = em.createQuery(jpql.toString(), FlashSaleCampaign.class);
            if (query.getIsActive() != null) {
                tq.setParameter("isActive", query.getIsActive());
            }

            tq.setFirstResult((query.getPage() - 1) * query.getLimit());
            tq.setMaxResults(query.getLimit());
            return tq.getResultList();
        });
    }

    public long count(FlashSaleQueryDTO query) {
        return execute(em -> {
            StringBuilder jpql = new StringBuilder("SELECT COUNT(c) FROM FlashSaleCampaign c WHERE c.deletedAt IS NULL");
            if (query.getIsActive() != null) {
                jpql.append(" AND c.isActive = :isActive");
            }

            TypedQuery<Long> tq = em.createQuery(jpql.toString(), Long.class);
            if (query.getIsActive() != null) {
                tq.setParameter("isActive", query.getIsActive());
            }
            return tq.getSingleResult();
        });
    }

    public List<ProductVariant> findVariantsForFlashSale(List<Integer> variantIds) {
        if (variantIds == null || variantIds.isEmpty()) return Collections.emptyList();
        return execute(em -> em.createQuery(
                "SELECT v FROM ProductVariant v WHERE v.id IN :ids AND v.deletedAt IS NULL", ProductVariant.class)
                .setParameter("ids", variantIds)
                .getResultList());
    }

    public FlashSaleCampaign create(CreateFlashSaleDTO dto) {
        return executeTransaction(em -> {
            FlashSaleCampaign campaign = new FlashSaleCampaign();
            campaign.setName(dto.getName().trim());
            campaign.setBannerUrl(dto.getBannerUrl());
            campaign.setStartTime(dto.getStartTime());
            campaign.setEndTime(dto.getEndTime());
            campaign.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);

            em.persist(campaign);

            for (FlashSaleItemDTO itemDto : dto.getItems()) {
                ProductVariant variant = em.find(ProductVariant.class, itemDto.getVariantId());
                FlashSaleItem item = new FlashSaleItem();
                item.setCampaign(campaign);
                item.setVariant(variant);
                item.setFlashSalePrice(itemDto.getFlashSalePrice());
                item.setPromoQuantity(itemDto.getPromoQuantity());
                item.setSoldQuantity(0);

                campaign.getItems().add(item);
                em.persist(item);
            }

            return campaign;
        });
    }

    public FlashSaleCampaign update(int campaignId, UpdateFlashSaleDTO dto) {
        return executeTransaction(em -> {
            FlashSaleCampaign campaign = em.find(FlashSaleCampaign.class, campaignId);

            if (dto.getName() != null) campaign.setName(dto.getName().trim());
            if (dto.getBannerUrl() != null) campaign.setBannerUrl(dto.getBannerUrl());
            if (dto.getStartTime() != null) campaign.setStartTime(dto.getStartTime());
            if (dto.getEndTime() != null) campaign.setEndTime(dto.getEndTime());
            if (dto.getIsActive() != null) campaign.setIsActive(dto.getIsActive());

            if (dto.getItems() != null) {
                // Map lưu lại soldQuantity hiện tại của từng variant
                Map<Integer, Integer> soldQuantityMap = campaign.getItems().stream()
                        .collect(Collectors.toMap(
                                item -> item.getVariant().getId(),
                                FlashSaleItem::getSoldQuantity,
                                (a, b) -> a
                        ));

                Set<Integer> incomingVariantIds = dto.getItems().stream()
                        .map(FlashSaleItemDTO::getVariantId)
                        .collect(Collectors.toSet());

                // Xóa các items không còn nằm trong payload cập nhật
                Iterator<FlashSaleItem> iterator = campaign.getItems().iterator();
                while (iterator.hasNext()) {
                    FlashSaleItem item = iterator.next();
                    if (!incomingVariantIds.contains(item.getVariant().getId())) {
                        em.remove(item);
                        iterator.remove();
                    }
                }

                // Cập nhật hoặc thêm mới các items
                for (FlashSaleItemDTO itemDto : dto.getItems()) {
                    Optional<FlashSaleItem> existingOpt = campaign.getItems().stream()
                            .filter(i -> i.getVariant().getId().equals(itemDto.getVariantId()))
                            .findFirst();

                    if (existingOpt.isPresent()) {
                        FlashSaleItem existing = existingOpt.get();
                        existing.setFlashSalePrice(itemDto.getFlashSalePrice());
                        existing.setPromoQuantity(itemDto.getPromoQuantity());
                    } else {
                        ProductVariant variant = em.find(ProductVariant.class, itemDto.getVariantId());
                        FlashSaleItem newItem = new FlashSaleItem();
                        newItem.setCampaign(campaign);
                        newItem.setVariant(variant);
                        newItem.setFlashSalePrice(itemDto.getFlashSalePrice());
                        newItem.setPromoQuantity(itemDto.getPromoQuantity());
                        newItem.setSoldQuantity(soldQuantityMap.getOrDefault(itemDto.getVariantId(), 0));

                        campaign.getItems().add(newItem);
                        em.persist(newItem);
                    }
                }
            }

            return campaign;
        });
    }

    public Optional<FlashSaleItem> findActiveFlashSaleItemWithLock(int variantId, jakarta.persistence.EntityManager em) {
        LocalDateTime now = LocalDateTime.now();
        String jpql = "SELECT fsi FROM FlashSaleItem fsi " +
                "JOIN fsi.campaign c " +
                "WHERE fsi.variant.id = :variantId " +
                "  AND c.deletedAt IS NULL " +
                "  AND c.isActive = true " +
                "  AND :now >= c.startTime AND :now <= c.endTime";

        List<FlashSaleItem> items = em.createQuery(jpql, FlashSaleItem.class)
                .setParameter("variantId", variantId)
                .setParameter("now", now)
                .setLockMode(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
                .setMaxResults(1)
                .getResultList();

        return items.isEmpty() ? Optional.empty() : Optional.of(items.get(0));
    }
}