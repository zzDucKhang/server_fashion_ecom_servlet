package com.fashionshop.presentation.controller.catalog;

import com.fashionshop.business.entity.Tag;
import com.fashionshop.business.service.catalog.TagService;
import com.fashionshop.common.response.PagedResponse;
import com.fashionshop.common.util.SecurityGuard;
import com.fashionshop.common.util.ValidationUtil;
import com.fashionshop.presentation.controller.base.BaseHttpServlet;
import com.fashionshop.presentation.dto.catalog.CreateTagDTO;
import com.fashionshop.presentation.dto.catalog.TagQueryDTO;
import com.fashionshop.presentation.dto.catalog.UpdateTagDTO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

@WebServlet(urlPatterns = {
    "/api/v1/catalog/tags",
    "/api/v1/catalog/tags/*",
    "/api/v1/tags",
    "/api/v1/tags/*"
})
public class TagServlet extends BaseHttpServlet {
    private final TagService tagService = new TagService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            TagQueryDTO query = new TagQueryDTO();
            query.setPage(getIntParameter(req, "page", 1));
            query.setLimit(getIntParameter(req, "limit", 10));
            query.setSearch(req.getParameter("search"));

            PagedResponse<Tag> result = tagService.list(query);
            sendPaged(resp, result);
        } else {
            Integer tagId = parseId(pathInfo);
            Tag tag = tagService.findById(tagId);
            sendSuccess(resp, null, tag);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer adminId = SecurityGuard.requireAdmin(req);
        CreateTagDTO dto = parseBody(req, CreateTagDTO.class);
        ValidationUtil.validate(dto);
        Tag created = tagService.create(adminId, dto);

        sendJson(resp, HttpServletResponse.SC_CREATED, Map.of(
                "statusCode", 201,
                "message", "Tạo thẻ tag thành công",
                "data", created
        ));
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer adminId = SecurityGuard.requireAdmin(req);
        Integer tagId = parseId(req.getPathInfo());
        UpdateTagDTO dto = parseBody(req, UpdateTagDTO.class);
        ValidationUtil.validate(dto);
        Tag updated = tagService.update(tagId, adminId, dto);

        sendSuccess(resp, "Cập nhật thẻ tag thành công", updated);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer adminId = SecurityGuard.requireAdmin(req);
        Integer tagId = parseId(req.getPathInfo());
        tagService.delete(tagId, adminId);

        sendSuccess(resp, "Xóa thẻ tag thành công", null);
    }
}
