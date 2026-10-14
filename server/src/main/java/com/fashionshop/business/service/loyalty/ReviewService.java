package com.fashionshop.business.service.loyalty;

import com.fashionshop.business.entity.ProductReview;
import com.fashionshop.common.exception.BadRequestException;
import com.fashionshop.common.exception.ConflictException;
import com.fashionshop.common.exception.NotFoundException;
import com.fashionshop.dataaccess.dao.catalog.ProductDAO;
import com.fashionshop.dataaccess.dao.loyalty.ProductReviewDAO;
import com.fashionshop.presentation.dto.loyalty.CreateReviewDTO;
import com.fashionshop.presentation.dto.loyalty.ReplyReviewDTO;
import com.fashionshop.presentation.dto.loyalty.ReviewQueryDTO;

import java.util.List;
import java.util.Map;

public class ReviewService {
    private final ProductReviewDAO reviewDAO = new ProductReviewDAO();
    private final ProductDAO productDAO = new ProductDAO();

    public ProductReview createReview(int userId, CreateReviewDTO body) {
        if (body.getOrderId() == null) {
            throw new BadRequestException("Bạn chỉ có thể đánh giá sản phẩm từ đơn hàng đã giao thành công");
        }
        if (body.getRating() == null || body.getRating() < 1 || body.getRating() > 5) {
            throw new BadRequestException("Số sao đánh giá phải từ 1 đến 5");
        }
        if (body.getContent() == null || body.getContent().trim().isEmpty()) {
            throw new BadRequestException("Nội dung đánh giá không được để trống");
        }

        productDAO.findById(body.getProductId())
                .filter(p -> p.getDeletedAt() == null)
                .orElseThrow(() -> new NotFoundException("Sản phẩm không tồn tại"));

        boolean validOrder = reviewDAO.verifyDeliveredOrderProduct(userId, body.getOrderId(), body.getProductId());
        if (!validOrder) {
            throw new BadRequestException("Bạn chỉ có thể đánh giá sản phẩm đã mua từ đơn hàng đã giao thành công (DELIVERED)");
        }

        // Chặn trùng lặp (P2002)
        if (reviewDAO.existsByOrderIdAndProductId(body.getOrderId(), body.getProductId())) {
            throw new ConflictException("Bạn đã đánh giá sản phẩm này trong đơn hàng này rồi");
        }

        ProductReview created = reviewDAO.createReview(userId, body);

        // Kích hoạt hook đồng bộ Redis cache rating
        reviewDAO.syncProductRatingHook(body.getProductId());

        return created;
    }

    public Map<String, Object> getReviewsByProduct(ReviewQueryDTO query) {
        if (query.getProductId() != null) {
            productDAO.findById(query.getProductId())
                    .filter(p -> p.getDeletedAt() == null)
                    .orElseThrow(() -> new NotFoundException("Sản phẩm không tồn tại"));
        }

        List<ProductReview> data = reviewDAO.findReviewsByProduct(query);
        long total = reviewDAO.countReviewsByProduct(query);
        ProductReviewDAO.RatingStats ratingStats = (query.getProductId() != null)
                ? reviewDAO.getAverageRating(query.getProductId())
                : new ProductReviewDAO.RatingStats(0.0, total);

        int limit = query.getLimit() > 0 ? query.getLimit() : 10;
        int page = query.getPage() > 0 ? query.getPage() : 1;
        int totalPages = (int) Math.ceil((double) total / limit);

        return Map.of(
                "data", data,
                "ratingStats", Map.of(
                        "averageRating", ratingStats.averageRating(),
                        "totalReviews", ratingStats.totalReviews()
                ),
                "pagination", Map.of(
                        "page", page,
                        "limit", limit,
                        "total", total,
                        "totalPages", totalPages
                ),
                "totalItems", total,
                "totalPages", totalPages,
                "page", page,
                "limit", limit
        );
    }

    public ProductReview getReviewById(int id) {
        return reviewDAO.findDetailById(id)
                .orElseThrow(() -> new NotFoundException("Đánh giá không tồn tại"));
    }

    public ProductReview replyToReview(int id, ReplyReviewDTO body) {
        if (body.getShopReply() == null || body.getShopReply().trim().isEmpty()) {
            throw new BadRequestException("Nội dung phản hồi không được để trống");
        }

        ProductReview review = getReviewById(id);
        review.setShopReply(body.getShopReply().trim());

        return reviewDAO.update(review);
    }

    public ProductReview toggleHideReview(int id, boolean isHidden) {
        ProductReview review = getReviewById(id);
        review.setIsHidden(isHidden);

        ProductReview updated = reviewDAO.update(review);

        // Cập nhật lại rating snapshot trên Redis khi ẩn/hiện review
        reviewDAO.syncProductRatingHook(review.getProduct().getId());

        return updated;
    }
}