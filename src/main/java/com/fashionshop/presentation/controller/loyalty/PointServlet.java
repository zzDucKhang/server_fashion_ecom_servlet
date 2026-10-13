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
import java.util.Map;

@WebServlet(urlPatterns = {"/api/v1/loyalty-engage/points/*", "/api/v1/points/*"})
public class PointServlet extends BaseHttpServlet {
    private final PointService pointService = new PointService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        Integer userId = getUserId(req);

        if ("/balance".equals(pathInfo)) {
            // GET /balance
            int rewardPoints = pointService.getBalance(userId);
            sendSuccess(resp, null, Map.of("rewardPoints", rewardPoints));
            return;
        }

        if ("/history".equals(pathInfo)) {
            // GET /history
            PointQueryDTO query = new PointQueryDTO();
            if (req.getParameter("page") != null) query.setPage(Integer.parseInt(req.getParameter("page")));
            if (req.getParameter("limit") != null) query.setLimit(Integer.parseInt(req.getParameter("limit")));

            var paged = pointService.getHistory(userId, query);
            sendSuccess(resp, null, paged);
            return;
        }

        sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Endpoint không tồn tại");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();

        if ("/earn".equals(pathInfo)) {
            // POST /earn (Admin only)
            Integer adminId = SecurityGuard.requireAdmin(req);
            EarnPointsDTO dto = parseBody(req, EarnPointsDTO.class);
            ValidationUtil.validate(dto);
            var result = pointService.earnPoints(adminId, dto);
            sendSuccess(resp, "Tích điểm thành công", result);
            return;
        }

        if ("/redeem".equals(pathInfo)) {
            // POST /redeem (User auth)
            Integer userId = getUserId(req);
            RedeemPointsDTO dto = parseBody(req, RedeemPointsDTO.class);
            ValidationUtil.validate(dto);
            var result = pointService.redeemPoints(userId, dto);
            sendSuccess(resp, "Đổi điểm thành công", result);
            return;
        }

        sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Endpoint không tồn tại");
    }
}