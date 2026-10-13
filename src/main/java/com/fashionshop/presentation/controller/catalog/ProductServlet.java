package com.fashionshop.presentation.controller.catalog;

import com.fashionshop.business.entity.Product;
import com.fashionshop.business.service.catalog.ProductService;
import com.fashionshop.common.response.PagedResponse;
import com.fashionshop.common.util.SecurityGuard;
import com.fashionshop.common.util.ValidationUtil;
import com.fashionshop.presentation.controller.base.BaseHttpServlet;
import com.fashionshop.presentation.dto.catalog.CreateProductDTO;
import com.fashionshop.presentation.dto.catalog.ProductQueryDTO;
import com.fashionshop.presentation.dto.catalog.UpdateProductDTO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

@WebServlet("/api/v1/catalog/products/*")
public class ProductServlet extends BaseHttpServlet {
    private final ProductService productService = new ProductService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            ProductQueryDTO queryDTO = new ProductQueryDTO();
            queryDTO.setPage(getIntParameter(req, "page", queryDTO.getPage()));
            queryDTO.setLimit(getIntParameter(req, "limit", queryDTO.getLimit()));
            queryDTO.setSearch(req.getParameter("search"));
            queryDTO.setCategoryId(getIntParameter(req, "categoryId", null));
            queryDTO.setBrandId(getIntParameter(req, "brandId", null));
            String statusParam = req.getParameter("status");
            if (statusParam != null && !statusParam.isBlank()) {
                try {
                    queryDTO.setStatus(Product.ProductStatus.valueOf(statusParam.trim().toUpperCase()));
                } catch (IllegalArgumentException e) {
                    // Ignore invalid status enum filter
                }
            }

            PagedResponse<Product> result = productService.list(queryDTO);
            sendPaged(resp, result);
        } else {
            Integer productId = parseId(pathInfo);
            Product product = productService.findById(productId);
            sendSuccess(resp, null, product);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer adminId = SecurityGuard.requireAdmin(req);
        CreateProductDTO dto = parseBody(req, CreateProductDTO.class);
        Product created = productService.create(adminId, dto);

        sendJson(resp, HttpServletResponse.SC_CREATED, Map.of(
                "statusCode", 201,
                "message", "Tạo sản phẩm thành công",
                "data", created
        ));
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer adminId = SecurityGuard.requireAdmin(req);
        Integer productId = parseId(req.getPathInfo());
        UpdateProductDTO dto = parseBody(req, UpdateProductDTO.class);
        ValidationUtil.validate(dto);
        Product updated = productService.update(productId, adminId, dto);

        sendSuccess(resp, "Cập nhật sản phẩm thành công", updated);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer adminId = SecurityGuard.requireAdmin(req);
        Integer productId = parseId(req.getPathInfo());
        productService.delete(productId, adminId);

        sendSuccess(resp, "Xóa sản phẩm thành công", null);
    }
}