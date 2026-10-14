package com.fashionshop.business.service.catalog;

import com.fashionshop.business.entity.Tag;
import com.fashionshop.common.exception.AppException;
import com.fashionshop.common.exception.BadRequestException;
import com.fashionshop.common.exception.NotFoundException;
import com.fashionshop.common.response.PagedResponse;
import com.fashionshop.dataaccess.dao.catalog.TagDAO;
import com.fashionshop.presentation.dto.catalog.CreateTagDTO;
import com.fashionshop.presentation.dto.catalog.TagQueryDTO;
import com.fashionshop.presentation.dto.catalog.UpdateTagDTO;

import java.time.LocalDateTime;
import java.util.List;

public class TagService {
    private final TagDAO tagDAO = new TagDAO();

    public PagedResponse<Tag> list(TagQueryDTO query) {
        List<Tag> list = tagDAO.findPaginatedActive(query.getPage(), query.getLimit(), query.getSearch());
        long total = tagDAO.countActive(query.getSearch());
        return new PagedResponse<>(list, total, query.getPage(), query.getLimit());
    }

    public Tag findById(Integer id) {
        return tagDAO.findById(id)
                .filter(t -> t.getDeletedAt() == null)
                .orElseThrow(() -> new NotFoundException("Thẻ Tag không tồn tại hoặc đã bị xóa"));
    }

    public Tag create(Integer createdById, CreateTagDTO dto) {
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new BadRequestException("Tên tag không được để trống");
        }

        String name = dto.getName().trim();
        if (tagDAO.findByName(name).isPresent()) {
            throw new AppException("Tên thẻ Tag đã tồn tại", 422);
        }

        Tag tag = new Tag();
        tag.setName(name);
        tag.setCreatedById(createdById);

        return tagDAO.save(tag);
    }

    public Tag update(Integer id, Integer updatedById, UpdateTagDTO dto) {
        Tag tag = findById(id);

        if (dto.getName() != null && !dto.getName().trim().isEmpty()) {
            String newName = dto.getName().trim();
            tagDAO.findByName(newName).ifPresent(existing -> {
                if (!existing.getId().equals(id)) {
                    throw new AppException("Tên thẻ Tag đã tồn tại", 422);
                }
            });
            tag.setName(newName);
        }

        tag.setUpdatedById(updatedById);
        return tagDAO.update(tag);
    }

    public void delete(Integer id, Integer deletedById) {
        Tag tag = findById(id);
        tag.setDeletedAt(LocalDateTime.now());
        tag.setDeletedById(deletedById);
        tagDAO.update(tag);
    }
}

