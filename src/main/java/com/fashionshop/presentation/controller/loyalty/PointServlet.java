package com.fashionshop.presentation.controller.loyalty;

import com.fashionshop.business.service.loyalty.PointService;
import com.fashionshop.common.util.SecurityGuard;
import com.fashionshop.common.util.ValidationUtil;
import com.fashionshop.presentation.controller.base.BaseHttpServlet;
import com.fashionshop.presentation.dto.loyalty.EarnPointsDTO;
import com.fashionshop.presentation.dto.loyalty.PointQueryDTO;
import com.fashionshop.presentation.dto.loyalty.RedeemPointsDTO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

@WebServlet(urlPatterns = {
    "/api/v1/loyalty-engage/points",
    "/api/v1/loyalty-engage/points/*",
    "/api/v1/points",
    "/api/v1/points/*"
})
public class PointServlet extends BaseHttpServlet {
    private final PointService pointService = new PointService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        Integer userId = getUserId(req);

        if ("/balance".equals(pathInfo)) {
            // GET /balance -> { statusCode: 200, data: { rewardPoints: 1500 } }
            int rewardPoints = pointService.getBalance(userId);
            sendSuccess(resp, null, Map.of("rewardPoints", rewardPoints));
            return;
        }

        if ("/history".equals(pathInfo)) {
            // GET /history -> { statusCode: 200, data: [...], pagination: { page, limit, total, totalPages } }
            PointQueryDTO query = new PointQueryDTO();
            query.setPage(getIntParameter(req, "page", 1));
            query.setLimit(getIntParameter(req, "limit", 10));

            var paged = pointService.getHistory(userId, query);
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("statusCode", 200);
            response.put("data", paged.getData());
            response.put("pagination", Map.of(
                    "page", paged.getPage(),
                    "limit", paged.getLimit(),
                    "total", paged.getTotalItems(),
                    "totalPages", paged.getTotalPages()
            ));
            sendJson(resp, HttpServletResponse.SC_OK, response);
            return;
        }

        sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Endpoint không tồn tại");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();

        if ("/earn".equals(pathInfo)) {
            // POST /earn (Admin only) -> { statusCode: 200, message: "...", currentPoint: ..., transaction: ... }
            Integer adminId = SecurityGuard.requireAdmin(req);
            EarnPointsDTO dto = parseBody(req, EarnPointsDTO.class);
            ValidationUtil.validate(dto);
            var result = pointService.earnPoints(adminId, dto);

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("statusCode", 200);
            response.putAll(result);
            response.put("data", result);
            sendJson(resp, HttpServletResponse.SC_OK, response);
            return;
        }

        if ("/redeem".equals(pathInfo)) {
            // POST /redeem (User auth) -> { statusCode: 200, message: "...", discountAmount: ..., currentPoint: ..., transaction: ... }
            Integer userId = getUserId(req);
            RedeemPointsDTO dto = parseBody(req, RedeemPointsDTO.class);
            ValidationUtil.validate(dto);
            var result = pointService.redeemPoints(userId, dto);

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("statusCode", 200);
            response.putAll(result);
            response.put("data", result);
            sendJson(resp, HttpServletResponse.SC_OK, response);
            return;
        }

        sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Endpoint không tồn tại");
    }
}