package com.fashionshop.presentation.controller.inventory;

import com.fashionshop.business.entity.FlashSaleCampaign;
import com.fashionshop.business.service.inventory.FlashSaleService;
import com.fashionshop.common.response.ApiResponse;
import com.fashionshop.common.response.PagedResponse;
import com.fashionshop.presentation.dto.inventory.CreateFlashSaleDTO;
import com.fashionshop.presentation.dto.inventory.FlashSaleQueryDTO;
import com.fashionshop.presentation.dto.inventory.UpdateFlashSaleDTO;
import com.fashionshop.common.util.JsonUtil;
import com.fashionshop.common.util.SecurityGuard;
import com.fashionshop.common.util.ValidationUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = {"/api/v1/inventory-sale/flash-sales/*", "/api/v1/flash-sales/*"})
public class FlashSaleServlet extends HttpServlet {
    private final FlashSaleService flashSaleService = new FlashSaleService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();

        if ("/active".equals(pathInfo)) {
            // GET /active -> Public
            List<FlashSaleCampaign> activeList = flashSaleService.findActiveCampaigns();
            JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of("statusCode", 200, "data", activeList));
            return;
        }

        if (pathInfo == null || pathInfo.equals("/")) {
            // GET / -> Public list
            FlashSaleQueryDTO query = new FlashSaleQueryDTO();
            if (req.getParameter("page") != null) query.setPage(Integer.parseInt(req.getParameter("page")));
            if (req.getParameter("limit") != null) query.setLimit(Integer.parseInt(req.getParameter("limit")));
            if (req.getParameter("isActive") != null) query.setIsActive(Boolean.parseBoolean(req.getParameter("isActive")));

            PagedResponse<FlashSaleCampaign> result = flashSaleService.list(query);
            JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, result);
        } else {
            // GET /:campaignId -> Public detail
            int campaignId = Integer.parseInt(pathInfo.substring(1).split("/")[0]);
            FlashSaleCampaign campaign = flashSaleService.findById(campaignId);
            JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of("statusCode", 200, "data", campaign));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // POST / -> requireAdmin
        SecurityGuard.requireAdmin(req);
        CreateFlashSaleDTO dto = JsonUtil.fromJson(req, CreateFlashSaleDTO.class);
        ValidationUtil.validate(dto);
        FlashSaleCampaign created = flashSaleService.create(dto);

        JsonUtil.sendJson(resp, HttpServletResponse.SC_CREATED, Map.of(
                "statusCode", 201,
                "message", "Tạo chiến dịch Flash Sale thành công",
                "data", created
        ));
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // PUT /:campaignId -> requireAdmin
        SecurityGuard.requireAdmin(req);
        int campaignId = Integer.parseInt(req.getPathInfo().substring(1).split("/")[0]);
        UpdateFlashSaleDTO dto = JsonUtil.fromJson(req, UpdateFlashSaleDTO.class);
        ValidationUtil.validate(dto);
        FlashSaleCampaign updated = flashSaleService.update(campaignId, dto);

        JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of(
                "statusCode", 200,
                "message", "Cập nhật chiến dịch Flash Sale thành công",
                "data", updated
        ));
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // DELETE /:campaignId -> requireAdmin
        SecurityGuard.requireAdmin(req);
        int campaignId = Integer.parseInt(req.getPathInfo().substring(1).split("/")[0]);
        flashSaleService.delete(campaignId);

        JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of(
                "message", "Xóa chiến dịch Flash Sale thành công"
        ));
    }
}