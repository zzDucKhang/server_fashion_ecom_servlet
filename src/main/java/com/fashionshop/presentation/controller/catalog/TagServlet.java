package com.fashionshop.presentation.controller.catalog;

import com.fashionshop.business.entity.Tag;
import com.fashionshop.business.service.catalog.TagService;
import com.fashionshop.common.response.PagedResponse;
import com.fashionshop.common.util.JsonUtil;
import com.fashionshop.common.util.SecurityGuard;
import com.fashionshop.presentation.dto.catalog.CreateTagDTO;
import com.fashionshop.presentation.dto.catalog.TagQueryDTO;
import com.fashionshop.presentation.dto.catalog.UpdateTagDTO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

@WebServlet("/api/v1/catalog/tags/*")
public class TagServlet extends HttpServlet {
    private final TagService tagService = new TagService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            TagQueryDTO query = new TagQueryDTO();
            if (req.getParameter("page") != null) {
                try { query.setPage(Integer.parseInt(req.getParameter("page"))); } catch (NumberFormatException ignored) {}
            }
            if (req.getParameter("limit") != null) {
                try { query.setLimit(Integer.parseInt(req.getParameter("limit"))); } catch (NumberFormatException ignored) {}
            }
            if (req.getParameter("search") != null) {
                query.setSearch(req.getParameter("search"));
            }

            PagedResponse<Tag> result = tagService.list(query);
            JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, result);
        } else {
            Integer tagId = parseId(pathInfo);
            Tag tag = tagService.findById(tagId);
            JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of("statusCode", 200, "data", tag));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer adminId = SecurityGuard.requireAdmin(req);
        CreateTagDTO dto = JsonUtil.fromJson(req, CreateTagDTO.class);
        Tag created = tagService.create(adminId, dto);

        JsonUtil.sendJson(resp, HttpServletResponse.SC_CREATED, Map.of(
                "statusCode", 201,
                "message", "Tạo thẻ tag thành công",
                "data", created
        ));
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer adminId = SecurityGuard.requireAdmin(req);
        Integer tagId = parseId(req.getPathInfo());
        UpdateTagDTO dto = JsonUtil.fromJson(req, UpdateTagDTO.class);
        Tag updated = tagService.update(tagId, adminId, dto);

        JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of(
                "statusCode", 200,
                "message", "Cập nhật thẻ tag thành công",
                "data", updated
        ));
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer adminId = SecurityGuard.requireAdmin(req);
        Integer tagId = parseId(req.getPathInfo());
        tagService.delete(tagId, adminId);

        JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of(
                "message", "Xóa thẻ tag thành công"
        ));
    }

    private Integer parseId(String pathInfo) {
        if (pathInfo == null || pathInfo.length() <= 1) {
            throw new IllegalArgumentException("Mã thẻ tag không hợp lệ");
        }
        return Integer.parseInt(pathInfo.substring(1).split("/")[0]);
    }
}

