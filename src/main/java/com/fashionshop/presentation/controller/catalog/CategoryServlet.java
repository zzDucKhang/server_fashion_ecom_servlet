package com.fashionshop.presentation.controller.catalog;

import com.fashionshop.business.entity.Category;
import com.fashionshop.business.service.catalog.CategoryService;
import com.fashionshop.common.util.SecurityGuard;
import com.fashionshop.common.util.ValidationUtil;
import com.fashionshop.presentation.controller.base.BaseHttpServlet;
import com.fashionshop.presentation.dto.catalog.CreateCategoryDTO;
import com.fashionshop.presentation.dto.catalog.UpdateCategoryDTO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = {
    "/api/v1/catalog/categories",
    "/api/v1/catalog/categories/*",
    "/api/v1/categories",
    "/api/v1/categories/*",
    "/api/categories",
    "/api/categories/*"
})
public class CategoryServlet extends BaseHttpServlet {
    private final CategoryService categoryService = new CategoryService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            Integer parentCategoryId = getIntParameter(req, "parentCategoryId", null);

            List<Category> list = categoryService.findAll(parentCategoryId);
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("statusCode", 200);
            response.put("data", list);
            response.put("totalItems", list.size());

            sendJson(resp, HttpServletResponse.SC_OK, response);
        } else {
            Integer categoryId = parseId(pathInfo);
            Category category = categoryService.findById(categoryId);
            sendSuccess(resp, null, category);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer adminId = SecurityGuard.requireAdmin(req);
        CreateCategoryDTO dto = parseBody(req, CreateCategoryDTO.class);
        ValidationUtil.validate(dto);
        Category created = categoryService.create(adminId, dto);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("statusCode", 201);
        response.put("message", "Tạo danh mục thành công");
        response.put("data", created);
        sendJson(resp, HttpServletResponse.SC_CREATED, response);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer adminId = SecurityGuard.requireAdmin(req);
        Integer categoryId = parseId(req.getPathInfo());
        UpdateCategoryDTO dto = parseBody(req, UpdateCategoryDTO.class);
        ValidationUtil.validate(dto);
        Category updated = categoryService.update(categoryId, adminId, dto);

        sendSuccess(resp, "Cập nhật danh mục thành công", updated);
    }

    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        doPut(req, resp);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer adminId = SecurityGuard.requireAdmin(req);
        Integer categoryId = parseId(req.getPathInfo());
        categoryService.delete(categoryId, adminId);

        sendJson(resp, HttpServletResponse.SC_OK, Map.of(
                "statusCode", 200,
                "message", "Xóa danh mục thành công"
        ));
    }
}