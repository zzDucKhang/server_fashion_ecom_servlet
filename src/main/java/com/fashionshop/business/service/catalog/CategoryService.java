package com.fashionshop.business.service.catalog;

import com.fashionshop.business.entity.Category;
import com.fashionshop.common.exception.AppException;
import com.fashionshop.common.exception.BadRequestException;
import com.fashionshop.common.exception.NotFoundException;
import com.fashionshop.dataaccess.dao.catalog.CategoryDAO;
import com.fashionshop.presentation.dto.catalog.CreateCategoryDTO;
import com.fashionshop.presentation.dto.catalog.UpdateCategoryDTO;

import java.time.LocalDateTime;
import java.util.List;

public class CategoryService {
    private final CategoryDAO categoryDAO = new CategoryDAO();

    public List<Category> findAll(Integer parentCategoryId) {
        if (parentCategoryId != null) {
            return categoryDAO.findByParentId(parentCategoryId);
        }
        return categoryDAO.findAllActive();
    }

    public Category findById(Integer id) {
        return categoryDAO.findById(id)
                .filter(c -> c.getDeletedAt() == null)
                .orElseThrow(() -> new NotFoundException("Danh mục không tồn tại hoặc đã bị xóa"));
    }

    public Category create(Integer createdById, CreateCategoryDTO dto) {
        if (dto.getDefaultName() == null || dto.getDefaultName().trim().isEmpty()) {
            throw new BadRequestException("Tên danh mục không được để trống");
        }

        String name = dto.getDefaultName().trim();
        if (categoryDAO.findByName(name).isPresent()) {
            throw new AppException("Danh mục đã tồn tại", 422);
        }

        Category category = new Category();
        category.setDefaultName(name);
        category.setIconUrl(dto.getIconUrl());
        category.setCreatedById(createdById);

        if (dto.getParentCategoryId() != null) {
            Category parent = findById(dto.getParentCategoryId());
            category.setParent(parent);
        }

        return categoryDAO.save(category);
    }

    public Category update(Integer id, Integer updatedById, UpdateCategoryDTO dto) {
        Category category = findById(id);

        if (dto.getDefaultName() != null && !dto.getDefaultName().trim().isEmpty()) {
            String newName = dto.getDefaultName().trim();
            categoryDAO.findByName(newName).ifPresent(existing -> {
                if (!existing.getId().equals(id)) {
                    throw new AppException("Danh mục đã tồn tại", 422);
                }
            });
            category.setDefaultName(newName);
        }

        if (dto.getIconUrl() != null) {
            category.setIconUrl(dto.getIconUrl());
        }

        if (dto.getIsVisible() != null) {
            category.setIsVisible(dto.getIsVisible());
        }

        if (dto.getParentCategoryId() != null) {
            if (dto.getParentCategoryId().equals(id)) {
                throw new BadRequestException("Danh mục cha không thể là chính nó");
            }
            Category newParent = findById(dto.getParentCategoryId());
            category.setParent(newParent);
        }

        category.setUpdatedById(updatedById);
        return categoryDAO.update(category);
    }

    public void delete(Integer id, Integer deletedById) {
        Category category = findById(id);
        category.setDeletedAt(LocalDateTime.now());
        category.setDeletedById(deletedById);
        categoryDAO.update(category);
    }
}

