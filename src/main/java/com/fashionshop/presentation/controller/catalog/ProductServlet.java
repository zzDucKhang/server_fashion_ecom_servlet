package com.fashionshop.presentation.controller.catalog;

import com.fashionshop.business.entity.Product;
import com.fashionshop.business.service.catalog.ProductService;
import com.fashionshop.common.response.PagedResponse;
import com.fashionshop.common.util.JsonUtil;
import com.fashionshop.common.util.SecurityGuard;
import com.fashionshop.presentation.dto.catalog.CreateProductDTO;
import com.fashionshop.presentation.dto.catalog.ProductQueryDTO;
import com.fashionshop.presentation.dto.catalog.UpdateProductDTO;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

@WebServlet("/api/v1/catalog/products/*")
public class ProductServlet extends HttpServlet {
    private final ProductService productService = new ProductService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            // GET /api/v1/catalog/products -> Trả về trực tiếp PagedResponse
            ProductQueryDTO queryDTO = new ProductQueryDTO();
            if (req.getParameter("page") != null) queryDTO.setPage(Integer.parseInt(req.getParameter("page")));
            if (req.getParameter("limit") != null) queryDTO.setLimit(Integer.parseInt(req.getParameter("limit")));
            if (req.getParameter("search") != null) queryDTO.setSearch(req.getParameter("search"));
            if (req.getParameter("categoryId") != null) queryDTO.setCategoryId(Integer.parseInt(req.getParameter("categoryId")));
            if (req.getParameter("brandId") != null) queryDTO.setBrandId(Integer.parseInt(req.getParameter("brandId")));
            if (req.getParameter("status") != null) queryDTO.setStatus(Product.ProductStatus.valueOf(req.getParameter("status")));

            PagedResponse<Product> result = productService.list(queryDTO);
            JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, result);
        } else {
            // GET /api/v1/catalog/products/:productId -> Trả về { statusCode: 200, data }
            Integer productId = parseProductId(pathInfo);
            Product product = productService.findById(productId);
            JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of("statusCode", 200, "data", product));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // POST /api/v1/catalog/products -> ADMIN ONLY
        Integer adminId = SecurityGuard.requireAdmin(req);
        CreateProductDTO dto = JsonUtil.fromJson(req, CreateProductDTO.class);
        Product created = productService.create(adminId, dto);

        JsonUtil.sendJson(resp, HttpServletResponse.SC_CREATED, Map.of(
                "statusCode", 201,
                "message", "Tạo sản phẩm thành công",
                "data", created
        ));
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // PUT /api/v1/catalog/products/:productId -> ADMIN ONLY
        Integer adminId = SecurityGuard.requireAdmin(req);
        Integer productId = parseProductId(req.getPathInfo());
        UpdateProductDTO dto = JsonUtil.fromJson(req, UpdateProductDTO.class);
        Product updated = productService.update(productId, adminId, dto);

        JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of(
                "statusCode", 200,
                "message", "Cập nhật sản phẩm thành công",
                "data", updated
        ));
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // DELETE /api/v1/catalog/products/:productId -> ADMIN ONLY
        Integer adminId = SecurityGuard.requireAdmin(req);
        Integer productId = parseProductId(req.getPathInfo());
        productService.delete(productId, adminId);

        JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of(
                "message", "Xóa sản phẩm thành công"
        ));
    }

    private Integer parseProductId(String pathInfo) {
        if (pathInfo == null || pathInfo.length() <= 1) {
            throw new IllegalArgumentException("Mã sản phẩm không hợp lệ");
        }
        return Integer.parseInt(pathInfo.substring(1).split("/")[0]);
    }
}