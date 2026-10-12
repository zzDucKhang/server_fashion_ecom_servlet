package com.fashionshop.presentation.controller.inventory;

import com.fashionshop.business.entity.FlashSaleCampaign;
import com.fashionshop.business.service.inventory.FlashSaleService;
import com.fashionshop.common.response.PagedResponse;
import com.fashionshop.common.util.SecurityGuard;
import com.fashionshop.common.util.ValidationUtil;
import com.fashionshop.presentation.controller.base.BaseHttpServlet;
import com.fashionshop.presentation.dto.inventory.CreateFlashSaleDTO;
import com.fashionshop.presentation.dto.inventory.FlashSaleQueryDTO;
import com.fashionshop.presentation.dto.inventory.UpdateFlashSaleDTO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = {"/api/v1/inventory-sale/flash-sales/*", "/api/v1/flash-sales/*"})
public class FlashSaleServlet extends BaseHttpServlet {
    private final FlashSaleService flashSaleService = new FlashSaleService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();

        if ("/active".equals(pathInfo)) {
            // GET /active -> Public
            List<FlashSaleCampaign> activeList = flashSaleService.findActiveCampaigns();
            sendSuccess(resp, null, activeList);
            return;
        }

        if (pathInfo == null || pathInfo.equals("/")) {
            // GET / -> Public list
            FlashSaleQueryDTO query = new FlashSaleQueryDTO();
            query.setPage(getIntParameter(req, "page", 1));
            query.setLimit(getIntParameter(req, "limit", 10));
            if (req.getParameter("isActive") != null) query.setIsActive(Boolean.parseBoolean(req.getParameter("isActive")));

            PagedResponse<FlashSaleCampaign> result = flashSaleService.list(query);
            sendPaged(resp, result);
        } else {
            // GET /:campaignId -> Public detail
            int campaignId = parseId(pathInfo);
            FlashSaleCampaign campaign = flashSaleService.findById(campaignId);
            sendSuccess(resp, null, campaign);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        SecurityGuard.requireAdmin(req);
        CreateFlashSaleDTO dto = parseBody(req, CreateFlashSaleDTO.class);
        ValidationUtil.validate(dto);
        FlashSaleCampaign created = flashSaleService.create(dto);

        sendJson(resp, HttpServletResponse.SC_CREATED, Map.of(
                "statusCode", 201,
                "message", "Tạo chiến dịch Flash Sale thành công",
                "data", created
        ));
    }

    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        doPut(req, resp);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        SecurityGuard.requireAdmin(req);
        int campaignId = parseId(req.getPathInfo());
        UpdateFlashSaleDTO dto = parseBody(req, UpdateFlashSaleDTO.class);
        ValidationUtil.validate(dto);
        FlashSaleCampaign updated = flashSaleService.update(campaignId, dto);

        sendSuccess(resp, "Cập nhật chiến dịch Flash Sale thành công", updated);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        SecurityGuard.requireAdmin(req);
        int campaignId = parseId(req.getPathInfo());
        flashSaleService.delete(campaignId);

        sendSuccess(resp, "Xóa chiến dịch Flash Sale thành công", null);
    }
}