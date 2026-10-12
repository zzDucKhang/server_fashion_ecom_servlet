package com.fashionshop.presentation.controller.catalog;

import com.fashionshop.business.entity.Brand;
import com.fashionshop.business.service.catalog.BrandService;
import com.fashionshop.common.response.PagedResponse;
import com.fashionshop.common.util.JsonUtil;
import com.fashionshop.common.util.SecurityGuard;
import com.fashionshop.common.util.ValidationUtil;
import com.fashionshop.presentation.dto.catalog.BrandQueryDTO;
import com.fashionshop.presentation.dto.catalog.CreateBrandDTO;
import com.fashionshop.presentation.dto.catalog.UpdateBrandDTO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

@WebServlet("/api/v1/catalog/brands/*")
public class BrandServlet extends HttpServlet {
    private final BrandService brandService = new BrandService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            BrandQueryDTO query = new BrandQueryDTO();
            if (req.getParameter("page") != null) {
                try { query.setPage(Integer.parseInt(req.getParameter("page"))); } catch (NumberFormatException ignored) {}
            }
            if (req.getParameter("limit") != null) {
                try { query.setLimit(Integer.parseInt(req.getParameter("limit"))); } catch (NumberFormatException ignored) {}
            }
            if (req.getParameter("search") != null) {
                query.setSearch(req.getParameter("search"));
            }

            PagedResponse<Brand> result = brandService.list(query);
            JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, result);
        } else {
            Integer brandId = parseId(pathInfo);
            Brand brand = brandService.findById(brandId);
            JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of("statusCode", 200, "data", brand));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer adminId = SecurityGuard.requireAdmin(req);
        CreateBrandDTO dto = JsonUtil.fromJson(req, CreateBrandDTO.class);
        Brand created = brandService.create(adminId, dto);

        JsonUtil.sendJson(resp, HttpServletResponse.SC_CREATED, Map.of(
                "statusCode", 201,
                "message", "Tạo thương hiệu thành công",
                "data", created
        ));
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer adminId = SecurityGuard.requireAdmin(req);
        Integer brandId = parseId(req.getPathInfo());
        UpdateBrandDTO dto = JsonUtil.fromJson(req, UpdateBrandDTO.class);
        ValidationUtil.validate(dto);
        Brand updated = brandService.update(brandId, adminId, dto);

        JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of(
                "statusCode", 200,
                "message", "Cập nhật thương hiệu thành công",
                "data", updated
        ));
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer adminId = SecurityGuard.requireAdmin(req);
        Integer brandId = parseId(req.getPathInfo());
        brandService.delete(brandId, adminId);

        JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of(
                "message", "Xóa thương hiệu thành công"
        ));
    }

    private Integer parseId(String pathInfo) {
        if (pathInfo == null || pathInfo.length() <= 1) {
            throw new IllegalArgumentException("Mã thương hiệu không hợp lệ");
        }
        return Integer.parseInt(pathInfo.substring(1).split("/")[0]);
    }
}

