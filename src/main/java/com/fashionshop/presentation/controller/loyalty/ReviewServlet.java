package com.fashionshop.presentation.controller.loyalty;

import com.fashionshop.business.entity.ProductReview;
import com.fashionshop.business.service.loyalty.ReviewService;
import com.fashionshop.common.util.SecurityGuard;
import com.fashionshop.common.util.ValidationUtil;
import com.fashionshop.presentation.controller.base.BaseHttpServlet;
import com.fashionshop.presentation.dto.loyalty.CreateReviewDTO;
import com.fashionshop.presentation.dto.loyalty.ReplyReviewDTO;
import com.fashionshop.presentation.dto.loyalty.ReviewQueryDTO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

@WebServlet(urlPatterns = {
    "/api/v1/loyalty-engage/reviews",
    "/api/v1/loyalty-engage/reviews/*",
    "/api/v1/reviews",
    "/api/v1/reviews/*"
})
public class ReviewServlet extends BaseHttpServlet {
    private final ReviewService reviewService = new ReviewService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            // GET /api/v1/loyalty-engage/reviews?productId=&page=&limit=&rating=
            ReviewQueryDTO query = new ReviewQueryDTO();
            query.setProductId(getIntParameter(req, "productId", null));
            query.setPage(getIntParameter(req, "page", 1));
            query.setLimit(getIntParameter(req, "limit", 10));
            query.setRating(getIntParameter(req, "rating", null));

            var result = reviewService.getReviewsByProduct(query);
            sendJson(resp, HttpServletResponse.SC_OK, Map.of(
                    "statusCode", 200,
                    "data", result.get("data"),
                    "ratingStats", result.get("ratingStats"),
                    "pagination", result.get("pagination"),
                    "totalItems", result.get("totalItems"),
                    "totalPages", result.get("totalPages"),
                    "page", result.get("page"),
                    "limit", result.get("limit")
            ));
        } else {
            // GET /api/v1/loyalty-engage/reviews/{id}
            int id = parseId(pathInfo);
            ProductReview review = reviewService.getReviewById(id);
            sendSuccess(resp, null, review);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            // POST / (Create review - requireAuth)
            Integer userId = getUserId(req);
            CreateReviewDTO dto = parseBody(req, CreateReviewDTO.class);
            ValidationUtil.validate(dto);
            ProductReview created = reviewService.createReview(userId, dto);
            sendJson(resp, HttpServletResponse.SC_CREATED, Map.of(
                    "statusCode", 201,
                    "message", "Đánh giá sản phẩm thành công",
                    "data", created
            ));
            return;
        }

        int reviewId = parseId(pathInfo);

        if (pathInfo.endsWith("/reply")) {
            // POST /{id}/reply (requireAdmin)
            SecurityGuard.requireAdmin(req);
            ReplyReviewDTO dto = parseBody(req, ReplyReviewDTO.class);
            ValidationUtil.validate(dto);
            ProductReview updated = reviewService.replyToReview(reviewId, dto);
            sendSuccess(resp, "Đã gửi phản hồi thành công", updated);
            return;
        }

        sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Endpoint không tồn tại");
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        doPatch(req, resp);
    }

    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo != null && pathInfo.endsWith("/hide")) {
            // PATCH /{id}/hide (requireAdmin)
            SecurityGuard.requireAdmin(req);
            int reviewId = parseId(pathInfo);
            Map<?, ?> body = parseBody(req, Map.class);
            boolean isHidden = Boolean.TRUE.equals(body.get("isHidden"));

            ProductReview updated = reviewService.toggleHideReview(reviewId, isHidden);
            sendSuccess(resp, isHidden ? "Đã ẩn đánh giá" : "Đã hiện đánh giá", updated);
            return;
        }
        sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Endpoint không tồn tại");
    }
}