package com.fashionshop.presentation.controller.loyalty;

import com.fashionshop.business.service.loyalty.PointService;
import com.fashionshop.common.response.ApiResponse;
import com.fashionshop.presentation.dto.loyalty.EarnPointsDTO;
import com.fashionshop.presentation.dto.loyalty.PointQueryDTO;
import com.fashionshop.presentation.dto.loyalty.RedeemPointsDTO;
import com.fashionshop.common.util.JsonUtil;
import com.fashionshop.common.util.SecurityGuard;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

@WebServlet(urlPatterns = {"/api/v1/loyalty-engage/points/*", "/api/v1/points/*"})
public class PointServlet extends HttpServlet {
    private final PointService pointService = new PointService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        Integer userId = SecurityGuard.requireAuth(req);

        if ("/balance".equals(pathInfo)) {
            // GET /balance
            int rewardPoints = pointService.getBalance(userId);
            JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, ApiResponse.ok(Map.of("rewardPoints", rewardPoints)));
            return;
        }

        if ("/history".equals(pathInfo)) {
            // GET /history
            PointQueryDTO query = new PointQueryDTO();
            if (req.getParameter("page") != null) query.setPage(Integer.parseInt(req.getParameter("page")));
            if (req.getParameter("limit") != null) query.setLimit(Integer.parseInt(req.getParameter("limit")));

            var paged = pointService.getHistory(userId, query);
            JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, ApiResponse.ok(paged));
            return;
        }

        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();

        if ("/earn".equals(pathInfo)) {
            // POST /earn (Admin only)
            Integer adminId = SecurityGuard.requireAdmin(req);
            EarnPointsDTO dto = JsonUtil.fromJson(req, EarnPointsDTO.class);
            var result = pointService.earnPoints(adminId, dto);
            JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, ApiResponse.ok(result));
            return;
        }

        if ("/redeem".equals(pathInfo)) {
            // POST /redeem (User auth)
            Integer userId = SecurityGuard.requireAuth(req);
            RedeemPointsDTO dto = JsonUtil.fromJson(req, RedeemPointsDTO.class);
            var result = pointService.redeemPoints(userId, dto);
            JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, ApiResponse.ok(result));
            return;
        }

        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
    }
}