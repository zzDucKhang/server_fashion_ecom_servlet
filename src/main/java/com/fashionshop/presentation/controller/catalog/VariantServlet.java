package com.fashionshop.presentation.controller.catalog;

import com.fashionshop.business.entity.ProductVariant;
import com.fashionshop.business.service.catalog.VariantService;
import com.fashionshop.common.util.JsonUtil;
import com.fashionshop.common.util.SecurityGuard;
import com.fashionshop.presentation.dto.catalog.SyncVariantsDTO;
import com.fashionshop.presentation.dto.catalog.UpdateStockDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet("/api/v1/catalog/variants/*")
public class VariantServlet extends HttpServlet {
    private final VariantService variantService = new VariantService();

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if ("PATCH".equalsIgnoreCase(req.getMethod())) {
            doPatch(req, resp);
        } else {
            super.service(req, resp);
        }
    }

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

            List<ProductVariant> variants = variantService.getVariantsByProductId(productId);
            JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of(
                    "statusCode", 200,
                    "data", variants
            ));
            return;
        }

        // GET /api/v1/catalog/variants/:variantId
        Integer variantId = parseId(pathInfo);
        ProductVariant variant = variantService.getVariantById(variantId);
        JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of("statusCode", 200, "data", variant));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();

        // POST /api/v1/catalog/variants/sync
        if (pathInfo != null && pathInfo.equals("/sync")) {
            Integer adminId = SecurityGuard.requireAdmin(req);
            SyncVariantsDTO dto = JsonUtil.fromJson(req, SyncVariantsDTO.class);
            List<ProductVariant> variants = variantService.syncProductVariants(dto, adminId);

            JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of(
                    "statusCode", 200,
                    "message", "Đồng bộ biến thể thành công",
                    "data", variants
            ));
            return;
        }

        resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Endpoint không hợp lệ");
    }

    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();

        // PATCH /api/v1/catalog/variants/:variantId/stock
        if (pathInfo != null && pathInfo.endsWith("/stock")) {
            SecurityGuard.requireAdmin(req);
            String[] parts = pathInfo.split("/");
            if (parts.length < 2) throw new IllegalArgumentException("variantId không hợp lệ");
            Integer variantId = Integer.parseInt(parts[1]);

            UpdateStockDTO dto = JsonUtil.fromJson(req, UpdateStockDTO.class);
            ProductVariant updated = variantService.updateStock(variantId, dto.getQuantityChange());

            JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of(
                    "statusCode", 200,
                    "message", "Cập nhật tồn kho thành công",
                    "data", updated
            ));
            return;
        }

        resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Endpoint không hợp lệ");
    }

    private Integer parseId(String pathInfo) {
        if (pathInfo == null || pathInfo.length() <= 1) {
            throw new IllegalArgumentException("Mã biến thể không hợp lệ");
        }
        return Integer.parseInt(pathInfo.substring(1).split("/")[0]);
    }
}

