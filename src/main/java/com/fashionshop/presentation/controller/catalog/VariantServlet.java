package com.fashionshop.presentation.controller.catalog;

import com.fashionshop.business.service.catalog.VariantService;
import com.fashionshop.common.util.SecurityGuard;
import com.fashionshop.common.util.ValidationUtil;
import com.fashionshop.presentation.controller.base.BaseHttpServlet;
import com.fashionshop.presentation.dto.catalog.SyncVariantsDTO;
import com.fashionshop.presentation.dto.catalog.UpdateStockDTO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(urlPatterns = {
    "/api/v1/catalog/variants",
    "/api/v1/catalog/variants/*",
    "/api/v1/variants",
    "/api/v1/variants/*"
})
public class VariantServlet extends BaseHttpServlet {
    private final VariantService variantService = new VariantService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            throw new IllegalArgumentException("Vui lòng cung cấp productId hoặc variantId");
        }

        // GET /api/v1/catalog/variants/products/:productId
        if (pathInfo.startsWith("/products/")) {
            String[] parts = pathInfo.split("/");
            if (parts.length < 3) throw new IllegalArgumentException("productId không hợp lệ");
            Integer productId = Integer.parseInt(parts[2]);

            var variants = variantService.getVariantsByProductId(productId);
            sendSuccess(resp, null, variants);
            return;
        }

        // GET /api/v1/catalog/variants/:variantId
        Integer variantId = parseId(pathInfo);
        var variant = variantService.getVariantById(variantId);
        sendSuccess(resp, null, variant);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();

        // POST /api/v1/catalog/variants/sync
        if (pathInfo != null && pathInfo.equals("/sync")) {
            Integer adminId = SecurityGuard.requireAdmin(req);
            SyncVariantsDTO dto = parseBody(req, SyncVariantsDTO.class);
            ValidationUtil.validate(dto);
            var syncedVariants = variantService.syncProductVariants(dto, adminId);

            sendSuccess(resp, "Đồng bộ biến thể thành công", syncedVariants);
            return;
        }

        sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Endpoint không hợp lệ");
    }

    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();

        // PATCH /api/v1/catalog/variants/:variantId/stock
        if (pathInfo != null && pathInfo.endsWith("/stock")) {
            SecurityGuard.requireAdmin(req);
            String[] parts = pathInfo.split("/");
            if (parts.length < 2) throw new IllegalArgumentException("variantId không hợp lệ");
            Integer variantId = Integer.parseInt(parts[1]);

            UpdateStockDTO dto = parseBody(req, UpdateStockDTO.class);
            ValidationUtil.validate(dto);
            var updated = variantService.updateStock(variantId, dto.getQuantityChange());

            sendSuccess(resp, "Cập nhật tồn kho thành công", updated);
            return;
        }

        sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Endpoint không hợp lệ");
    }
}
