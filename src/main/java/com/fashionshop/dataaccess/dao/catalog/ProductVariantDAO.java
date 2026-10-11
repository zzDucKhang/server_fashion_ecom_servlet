package com.fashionshop.dataaccess.dao.catalog;

import com.fashionshop.business.entity.Product;
import com.fashionshop.business.entity.ProductVariant;
import com.fashionshop.common.exception.AppException;
import com.fashionshop.common.exception.BadRequestException;
import com.fashionshop.common.exception.NotFoundException;
import com.fashionshop.dataaccess.base.AbstractJpaDAO;
import com.fashionshop.presentation.dto.catalog.SyncVariantsDTO;
import com.fashionshop.presentation.dto.catalog.UpsertVariantItemDTO;
import jakarta.persistence.LockModeType;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.TypedQuery;

import java.time.LocalDateTime;
import java.util.*;

public class ProductVariantDAO extends AbstractJpaDAO<ProductVariant, Integer> {

    public ProductVariantDAO() {
        super(ProductVariant.class);
    }

    public List<ProductVariant> findByProductId(Integer productId) {
        return execute(em -> {
            TypedQuery<ProductVariant> query = em.createQuery(
                    "SELECT v FROM ProductVariant v WHERE v.product.id = :productId AND v.deletedAt IS NULL ORDER BY v.id ASC",
                    ProductVariant.class
            );
            query.setParameter("productId", productId);
            return query.getResultList();
        });
    }

    public Optional<ProductVariant> findActiveById(Integer id) {
        return execute(em -> {
            TypedQuery<ProductVariant> query = em.createQuery(
                    "SELECT v FROM ProductVariant v LEFT JOIN FETCH v.product WHERE v.id = :id AND v.deletedAt IS NULL",
                    ProductVariant.class
            );
            query.setParameter("id", id);
            List<ProductVariant> list = query.getResultList();
            return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
        });
    }

    public Optional<ProductVariant> findBySku(String sku) {
        return execute(em -> {
            List<ProductVariant> list = em.createQuery(
                            "SELECT v FROM ProductVariant v WHERE LOWER(v.sku) = LOWER(:sku) AND v.deletedAt IS NULL",
                            ProductVariant.class)
                    .setParameter("sku", sku.trim())
                    .setMaxResults(1)
                    .getResultList();
            return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
        });
    }

    /**
     * Đồng bộ biến thể theo lô sử dụng Transaction và Pessimistic Locking chống Race Condition
     */
    public List<ProductVariant> syncVariants(SyncVariantsDTO dto, Integer userId) {
        return executeTransaction(em -> {
            Integer productId = dto.getProductId();
            Product product = em.find(Product.class, productId, LockModeType.PESSIMISTIC_WRITE);
            if (product == null || product.getDeletedAt() != null) {
                throw new NotFoundException("Không tìm thấy sản phẩm tương ứng để đồng bộ biến thể");
            }

            TypedQuery<ProductVariant> existingQuery = em.createQuery(
                    "SELECT v FROM ProductVariant v WHERE v.product.id = :productId AND v.deletedAt IS NULL",
                    ProductVariant.class
            );
            existingQuery.setParameter("productId", productId);
            existingQuery.setLockMode(LockModeType.PESSIMISTIC_WRITE);
            List<ProductVariant> existingVariants = existingQuery.getResultList();

            Set<String> payloadSkus = new HashSet<>();
            for (UpsertVariantItemDTO item : dto.getVariants()) {
                payloadSkus.add(item.getSku().trim().toLowerCase());
            }

            // Xóa mềm các SKU cũ không còn trong payload
            for (ProductVariant existing : existingVariants) {
                if (!payloadSkus.contains(existing.getSku().trim().toLowerCase())) {
                    existing.setDeletedAt(LocalDateTime.now());
                    em.merge(existing);
                }
            }

            List<ProductVariant> result = new ArrayList<>();
            for (UpsertVariantItemDTO item : dto.getVariants()) {
                String cleanSku = item.getSku().trim();
                Optional<ProductVariant> matched = existingVariants.stream()
                        .filter(v -> v.getSku().equalsIgnoreCase(cleanSku) && v.getDeletedAt() == null)
                        .findFirst();

                if (matched.isPresent()) {
                    ProductVariant v = matched.get();
                    v.setOriginalPrice(item.getOriginalPrice());
                    v.setSellingPrice(item.getSellingPrice());
                    v.setStockQuantity(item.getStockQuantity());
                    v.setWeightGram(item.getWeightGram());
                    v.setImageUrl(item.getImageUrl());
                    v.setVariant(item.getVariant());
                    v.setVariantValue(item.getVariantValue());
                    result.add(em.merge(v));
                } else {
                    ProductVariant newVariant = new ProductVariant();
                    newVariant.setProduct(product);
                    newVariant.setSku(cleanSku);
                    newVariant.setOriginalPrice(item.getOriginalPrice());
                    newVariant.setSellingPrice(item.getSellingPrice());
                    newVariant.setStockQuantity(item.getStockQuantity());
                    newVariant.setWeightGram(item.getWeightGram());
                    newVariant.setImageUrl(item.getImageUrl());
                    newVariant.setVariant(item.getVariant());
                    newVariant.setVariantValue(item.getVariantValue());
                    em.persist(newVariant);
                    result.add(newVariant);
                }
            }
            return result;
        });
    }

    /**
     * Cập nhật tồn kho với Optimistic Concurrency Control
     */
    public ProductVariant updateStockOptimistic(Integer variantId, int delta) {
        try {
            return executeTransaction(em -> {
                ProductVariant variant = em.find(ProductVariant.class, variantId);
                if (variant == null || variant.getDeletedAt() != null) {
                    throw new NotFoundException("Biến thể không tồn tại");
                }

                if (delta < 0 && variant.getStockQuantity() < Math.abs(delta)) {
                    throw new BadRequestException("Số lượng tồn kho không đủ để thực hiện khấu trừ");
                }

                variant.updateStock(delta);
                return em.merge(variant);
            });
        } catch (OptimisticLockException e) {
            throw new AppException("Xung đột phiên bản dữ liệu (Version Conflict). Vui lòng thử lại!", 409);
        }
    }
}

