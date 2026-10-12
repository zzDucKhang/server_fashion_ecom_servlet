package com.fashionshop.presentation.controller.catalog;

import com.fashionshop.business.entity.Category;
import com.fashionshop.business.service.catalog.CategoryService;
import com.fashionshop.common.util.JsonUtil;
import com.fashionshop.common.util.SecurityGuard;
import com.fashionshop.common.util.ValidationUtil;
import com.fashionshop.presentation.dto.catalog.CreateCategoryDTO;
import com.fashionshop.presentation.dto.catalog.UpdateCategoryDTO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet("/api/v1/catalog/categories/*")
public class CategoryServlet extends HttpServlet {
    private final CategoryService categoryService = new CategoryService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            Integer parentCategoryId = null;
            if (req.getParameter("parentCategoryId") != null) {
                try {
                    parentCategoryId = Integer.parseInt(req.getParameter("parentCategoryId"));
                } catch (NumberFormatException ignored) {}
            }

            List<Category> list = categoryService.findAll(parentCategoryId);
            JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of(
                    "data", list,
                    "totalItems", list.size()
            ));
        } else {
            Integer categoryId = parseId(pathInfo);
            Category category = categoryService.findById(categoryId);
            JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of("statusCode", 200, "data", category));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer adminId = SecurityGuard.requireAdmin(req);
        CreateCategoryDTO dto = JsonUtil.fromJson(req, CreateCategoryDTO.class);
        Category created = categoryService.create(adminId, dto);

        JsonUtil.sendJson(resp, HttpServletResponse.SC_CREATED, Map.of(
                "statusCode", 201,
                "message", "Tạo danh mục thành công",
                "data", created
        ));
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer adminId = SecurityGuard.requireAdmin(req);
        Integer categoryId = parseId(req.getPathInfo());
        UpdateCategoryDTO dto = JsonUtil.fromJson(req, UpdateCategoryDTO.class);
        ValidationUtil.validate(dto);
        Category updated = categoryService.update(categoryId, adminId, dto);

        JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of(
                "statusCode", 200,
                "message", "Cập nhật danh mục thành công",
                "data", updated
        ));
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer adminId = SecurityGuard.requireAdmin(req);
        Integer categoryId = parseId(req.getPathInfo());
        categoryService.delete(categoryId, adminId);

        JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of(
                "message", "Xóa danh mục thành công"
        ));
    }

    private Integer parseId(String pathInfo) {
        if (pathInfo == null || pathInfo.length() <= 1) {
            throw new IllegalArgumentException("Mã danh mục không hợp lệ");
        }
        return Integer.parseInt(pathInfo.substring(1).split("/")[0]);
    }
}

