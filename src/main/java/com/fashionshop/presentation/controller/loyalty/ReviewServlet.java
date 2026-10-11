package com.fashionshop.presentation.controller.loyalty;

import com.fashionshop.business.entity.ProductReview;
import com.fashionshop.business.service.loyalty.ReviewService;
import com.fashionshop.common.response.ApiResponse;
import com.fashionshop.common.util.JsonUtil;
import com.fashionshop.common.util.SecurityGuard;
import com.fashionshop.presentation.dto.loyalty.CreateReviewDTO;
import com.fashionshop.presentation.dto.loyalty.ReplyReviewDTO;
import com.fashionshop.presentation.dto.loyalty.ReviewQueryDTO;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

@WebServlet(urlPatterns = {"/api/v1/loyalty-engage/reviews/*", "/api/v1/reviews/*"})
public class ReviewServlet extends HttpServlet {
    private final ReviewService reviewService = new ReviewService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            // GET /api/v1/loyalty-engage/reviews?productId=&page=&limit=&rating=
            ReviewQueryDTO query = new ReviewQueryDTO();
            if (req.getParameter("productId") != null) query.setProductId(Integer.parseInt(req.getParameter("productId")));
            if (req.getParameter("page") != null) query.setPage(Integer.parseInt(req.getParameter("page")));
            if (req.getParameter("limit") != null) query.setLimit(Integer.parseInt(req.getParameter("limit")));
            if (req.getParameter("rating") != null) query.setRating(Integer.parseInt(req.getParameter("rating")));

            var result = reviewService.getReviewsByProduct(query);
            JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of("statusCode", 200, "data", result.get("data"), "ratingStats", result.get("ratingStats"), "pagination", result.get("pagination")));
        } else {
            // GET /api/v1/loyalty-engage/reviews/{id}
            int id = Integer.parseInt(pathInfo.substring(1).split("/")[0]);
            ProductReview review = reviewService.getReviewById(id);
            JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of("statusCode", 200, "data", review));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            // POST / (Create review - requireAuth)
            Integer userId = SecurityGuard.requireAuth(req);
            CreateReviewDTO dto = JsonUtil.fromJson(req, CreateReviewDTO.class);
            ProductReview created = reviewService.createReview(userId, dto);
            JsonUtil.sendJson(resp, HttpServletResponse.SC_CREATED, Map.of(
                    "statusCode", 201,
                    "message", "Đánh giá sản phẩm thành công",
                    "data", created
            ));
            return;
        }

        String[] parts = pathInfo.substring(1).split("/");
        int reviewId = Integer.parseInt(parts[0]);

        if (parts.length > 1 && "reply".equals(parts[1])) {
            // POST /{id}/reply (requireAdmin)
            SecurityGuard.requireAdmin(req);
            ReplyReviewDTO dto = JsonUtil.fromJson(req, ReplyReviewDTO.class);
            ProductReview updated = reviewService.replyToReview(reviewId, dto);
            JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of(
                    "statusCode", 200,
                    "message", "Đã gửi phản hồi thành công",
                    "data", updated
            ));
            return;
        }

        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        doPatch(req, resp);
    }

    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo != null) {
            String[] parts = pathInfo.substring(1).split("/");
            if (parts.length > 1 && "hide".equals(parts[1])) {
                // PATCH /{id}/hide (requireAdmin)
                SecurityGuard.requireAdmin(req);
                int reviewId = Integer.parseInt(parts[0]);
                Map<?, ?> body = JsonUtil.fromJson(req, Map.class);
                boolean isHidden = Boolean.TRUE.equals(body.get("isHidden"));

                ProductReview updated = reviewService.toggleHideReview(reviewId, isHidden);
                JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of(
                        "statusCode", 200,
                        "message", isHidden ? "Đã ẩn đánh giá" : "Đã hiện đánh giá",
                        "data", updated
                ));
                return;
            }
        }
        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
    }
}