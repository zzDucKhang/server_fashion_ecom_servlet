package com.fashionshop.dataaccess.dao.loyalty;

import com.fashionshop.business.entity.*;
import com.fashionshop.common.util.JsonUtil;
import com.fashionshop.common.util.RedisUtil;
import com.fashionshop.dataaccess.base.AbstractJpaDAO;
import com.fashionshop.presentation.dto.loyalty.CreateReviewDTO;
import com.fashionshop.presentation.dto.loyalty.ReviewQueryDTO;

import jakarta.persistence.TypedQuery;
import org.redisson.api.RBucket;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

public class ProductReviewDAO extends AbstractJpaDAO<ProductReview, Integer> {
    private static final Logger logger = Logger.getLogger(ProductReviewDAO.class.getName());

    public record RatingStats(double averageRating, long totalReviews) {}

    public ProductReviewDAO() {
        super(ProductReview.class);
    }

    /**
     * Xác thực người dùng đã mua sản phẩm trong đơn hàng DELIVERED
     */
    public boolean verifyDeliveredOrderProduct(int userId, int orderId, int productId) {
        return execute(em -> {
            String jpql = "SELECT COUNT(o) FROM Order o " +
                    "JOIN o.orderItems oi " +
                    "WHERE o.id = :orderId " +
                    "  AND o.user.id = :userId " +
                    "  AND o.status = :status " +
                    "  AND o.deletedAt IS NULL " +
                    "  AND oi.deletedAt IS NULL " +
                    "  AND (oi.variant.product.id = :productId OR oi.variant.id = :productId)";

            Long count = em.createQuery(jpql, Long.class)
                    .setParameter("orderId", orderId)
                    .setParameter("userId", userId)
                    .setParameter("status", Order.OrderStatus.DELIVERED)
                    .setParameter("productId", productId)
                    .getSingleResult();

            return count != null && count > 0;
        });
    }

    /**
     * Tạo đánh giá kèm Media trong Transaction an toàn
     */
    public ProductReview createReview(int userId, CreateReviewDTO dto) {
        return executeTransaction(em -> {
            User user = em.find(User.class, userId);
            Product product = em.find(Product.class, dto.getProductId());
            Order order = em.find(Order.class, dto.getOrderId());

            ProductReview review = new ProductReview();
            review.setUser(user);
            review.setProduct(product);
            review.setOrder(order);
            review.setRating(dto.getRating());
            review.setContent(dto.getContent().trim());
            review.setIsHidden(false);
            review.setCreatedAt(LocalDateTime.now());
            review.setUpdatedAt(LocalDateTime.now());

            // 1. QUAN TRỌNG: Phải persist(review) TRƯỚC để review có ID và trở thành Managed Entity
            em.persist(review);

            // 2. Sau khi review đã được lưu, mới gán vào media và persist(media)
            if (dto.getMediaUrls() != null && !dto.getMediaUrls().isEmpty()) {
                for (CreateReviewDTO.MediaItemDTO item : dto.getMediaUrls()) {
                    if (item != null && item.getUrl() != null && !item.getUrl().isBlank()) {
                        ReviewMedia media = new ReviewMedia();
                        media.setReview(review);
                        media.setMediaUrl(item.getUrl());
                        media.setMediaType(item.getType() != null ? item.getType() : "image");
                        review.getMedias().add(media);
                        em.persist(media);
                    }
                }
            }

            return review;
        });
    }

    /**
     * Kiểm tra xem đơn hàng đã từng đánh giá sản phẩm này chưa (chống P2002 Unique Constraint)
     */
    public boolean existsByOrderIdAndProductId(int orderId, int productId) {
        return execute(em -> {
            Long count = em.createQuery(
                    "SELECT COUNT(pr) FROM ProductReview pr " +
                    "WHERE pr.order.id = :orderId AND pr.product.id = :productId", Long.class)
                    .setParameter("orderId", orderId)
                    .setParameter("productId", productId)
                    .getSingleResult();
            return count > 0;
        });
    }

    

    public List<ProductReview> findReviewsByProduct(ReviewQueryDTO query) {
        return execute(em -> {
            StringBuilder jpql = new StringBuilder(
                    "SELECT DISTINCT pr FROM ProductReview pr " +
                    "LEFT JOIN FETCH pr.user " +
                    "LEFT JOIN FETCH pr.medias " +
                    "WHERE pr.deletedAt IS NULL");

            if (query.getProductId() != null) {
                jpql.append(" AND pr.product.id = :productId AND pr.isHidden = false");
            }
            if (query.getRating() != null) {
                jpql.append(" AND pr.rating = :rating");
            }
            jpql.append(" ORDER BY pr.createdAt DESC");

            TypedQuery<ProductReview> tq = em.createQuery(jpql.toString(), ProductReview.class);
            if (query.getProductId() != null) {
                tq.setParameter("productId", query.getProductId());
            }
            if (query.getRating() != null) {
                tq.setParameter("rating", query.getRating().shortValue());
            }

            int page = query.getPage() > 0 ? query.getPage() : 1;
            int limit = query.getLimit() > 0 ? query.getLimit() : 10;
            tq.setFirstResult((page - 1) * limit);
            tq.setMaxResults(limit);
            return tq.getResultList();
        });
    }

    public long countReviewsByProduct(ReviewQueryDTO query) {
        return execute(em -> {
            StringBuilder jpql = new StringBuilder(
                    "SELECT COUNT(pr) FROM ProductReview pr " +
                    "WHERE pr.deletedAt IS NULL");

            if (query.getProductId() != null) {
                jpql.append(" AND pr.product.id = :productId AND pr.isHidden = false");
            }
            if (query.getRating() != null) {
                jpql.append(" AND pr.rating = :rating");
            }

            TypedQuery<Long> tq = em.createQuery(jpql.toString(), Long.class);
            if (query.getProductId() != null) {
                tq.setParameter("productId", query.getProductId());
            }
            if (query.getRating() != null) {
                tq.setParameter("rating", query.getRating().shortValue());
            }

            return tq.getSingleResult();
        });
    }

    public RatingStats getAverageRating(int productId) {
        return execute(em -> {
            String jpql = "SELECT AVG(pr.rating), COUNT(pr.id) FROM ProductReview pr " +
                    "WHERE pr.product.id = :productId AND pr.isHidden = false AND pr.deletedAt IS NULL";

            Object[] result = (Object[]) em.createQuery(jpql).setParameter("productId", productId).getSingleResult();

            Double avg = (Double) result[0];
            Long count = (Long) result[1];

            double roundedAvg = (avg != null && count > 0)
                    ? BigDecimal.valueOf(avg).setScale(1, RoundingMode.HALF_UP).doubleValue()
                    : 0.0;

            return new RatingStats(roundedAvg, count != null ? count : 0L);
        });
    }

    public Optional<ProductReview> findDetailById(int id) {
        return execute(em -> {
            List<ProductReview> list = em.createQuery(
                    "SELECT pr FROM ProductReview pr " +
                    "LEFT JOIN FETCH pr.user " +
                    "LEFT JOIN FETCH pr.medias " +
                    "WHERE pr.id = :id AND pr.deletedAt IS NULL", ProductReview.class)
                    .setParameter("id", id)
                    .setMaxResults(1)
                    .getResultList();
            return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
        });
    }

    /**
     * Đồng bộ Rating Snapshot vào Redis key `catalog:product:{productId}:rating` (TTL 24h)
     */
    public RatingStats syncProductRatingHook(int productId) {
        RatingStats stats = getAverageRating(productId);
        try {
            String cacheKey = "catalog:product:" + productId + ":rating";
            RBucket<String> bucket = RedisUtil.getClient().getBucket(cacheKey);

            Map<String, Object> payload = Map.of(
                    "productId", productId,
                    "averageRating", stats.averageRating(),
                    "totalReviews", stats.totalReviews(),
                    "updatedAt", LocalDateTime.now().toString()
            );

            bucket.set(JsonUtil.getMapper().writeValueAsString(payload), Duration.ofSeconds(86400));
        } catch (Exception e) {
            logger.warning("[ProductReviewDAO] Redis cache sync warning: " + e.getMessage());
        }
        return stats;
    }
}