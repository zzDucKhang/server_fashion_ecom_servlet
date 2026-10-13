package com.fashionshop.presentation.controller.catalog;

import com.fashionshop.business.entity.Brand;
import com.fashionshop.business.service.catalog.BrandService;
import com.fashionshop.common.response.PagedResponse;
import com.fashionshop.common.util.SecurityGuard;
import com.fashionshop.common.util.ValidationUtil;
import com.fashionshop.presentation.controller.base.BaseHttpServlet;
import com.fashionshop.presentation.dto.catalog.BrandQueryDTO;
import com.fashionshop.presentation.dto.catalog.CreateBrandDTO;
import com.fashionshop.presentation.dto.catalog.UpdateBrandDTO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

@WebServlet("/api/v1/catalog/brands/*")
public class BrandServlet extends BaseHttpServlet {
    private final BrandService brandService = new BrandService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            BrandQueryDTO query = new BrandQueryDTO();
            query.setPage(getIntParameter(req, "page", 1));
            query.setLimit(getIntParameter(req, "limit", 10));
            query.setSearch(req.getParameter("search"));

            PagedResponse<Brand> result = brandService.list(query);
            sendPaged(resp, result);
        } else {
            Integer brandId = parseId(pathInfo);
            Brand brand = brandService.findById(brandId);
            sendSuccess(resp, null, brand);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer adminId = SecurityGuard.requireAdmin(req);
        CreateBrandDTO dto = parseBody(req, CreateBrandDTO.class);
        Brand created = brandService.create(adminId, dto);

        sendJson(resp, HttpServletResponse.SC_CREATED, Map.of(
                "statusCode", 201,
                "message", "Tạo thương hiệu thành công",
                "data", created
        ));
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer adminId = SecurityGuard.requireAdmin(req);
        Integer brandId = parseId(req.getPathInfo());
        UpdateBrandDTO dto = parseBody(req, UpdateBrandDTO.class);
        ValidationUtil.validate(dto);
        Brand updated = brandService.update(brandId, adminId, dto);

        sendSuccess(resp, "Cập nhật thương hiệu thành công", updated);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer adminId = SecurityGuard.requireAdmin(req);
        Integer brandId = parseId(req.getPathInfo());
        brandService.delete(brandId, adminId);

        sendSuccess(resp, "Xóa thương hiệu thành công", null);
    }
}
