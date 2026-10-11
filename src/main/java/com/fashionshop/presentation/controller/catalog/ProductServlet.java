package com.fashionshop.presentation.controller.catalog;

import com.fashionshop.business.entity.Product;
import com.fashionshop.business.service.catalog.ProductService;
import com.fashionshop.common.response.ApiResponse;
import com.fashionshop.common.response.PagedResponse;
import com.fashionshop.common.util.JsonUtil;
import com.fashionshop.presentation.dto.catalog.CreateProductDTO;
import com.fashionshop.presentation.dto.catalog.ProductQueryDTO;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/api/v1/catalog/products/*")
public class ProductServlet extends HttpServlet {
    private final ProductService productService = new ProductService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            // GET /api/v1/catalog/products
            ProductQueryDTO queryDTO = new ProductQueryDTO();
            if (req.getParameter("page") != null) queryDTO.setPage(Integer.parseInt(req.getParameter("page")));
            if (req.getParameter("limit") != null) queryDTO.setLimit(Integer.parseInt(req.getParameter("limit")));
            if (req.getParameter("search") != null) queryDTO.setSearch(req.getParameter("search"));
            if (req.getParameter("categoryId") != null) queryDTO.setCategoryId(Integer.parseInt(req.getParameter("categoryId")));
            if (req.getParameter("brandId") != null) queryDTO.setBrandId(Integer.parseInt(req.getParameter("brandId")));

            PagedResponse<Product> result = productService.listProducts(queryDTO);
            JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, ApiResponse.ok(result));
        } else {
            // GET /api/v1/catalog/products/{id}
            String[] splits = pathInfo.split("/");
            Integer productId = Integer.parseInt(splits[1]);
            Product product = productService.getProductById(productId);
            JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, ApiResponse.ok(product));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // POST /api/v1/catalog/products
        CreateProductDTO dto = JsonUtil.fromJson(req, CreateProductDTO.class);
        Product created = productService.createProduct(dto);
        JsonUtil.sendJson(resp, HttpServletResponse.SC_CREATED, ApiResponse.created(created, "Tạo sản phẩm thành công"));
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // DELETE /api/v1/catalog/products/{id}
        String pathInfo = req.getPathInfo();
        String[] splits = pathInfo.split("/");
        Integer productId = Integer.parseInt(splits[1]);
        productService.deleteProduct(productId);
        JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, ApiResponse.ok("Xóa sản phẩm thành công"));
    }
}