package com.fashionshop.business.service.catalog;

import com.fashionshop.business.entity.Brand;
import com.fashionshop.common.exception.AppException;
import com.fashionshop.common.exception.BadRequestException;
import com.fashionshop.common.exception.NotFoundException;
import com.fashionshop.common.response.PagedResponse;
import com.fashionshop.dataaccess.dao.catalog.BrandDAO;
import com.fashionshop.presentation.dto.catalog.BrandQueryDTO;
import com.fashionshop.presentation.dto.catalog.CreateBrandDTO;
import com.fashionshop.presentation.dto.catalog.UpdateBrandDTO;

import java.time.LocalDateTime;
import java.util.List;

public class BrandService {
    private final BrandDAO brandDAO = new BrandDAO();

    public PagedResponse<Brand> list(BrandQueryDTO query) {
        List<Brand> list = brandDAO.findPaginatedActive(query.getPage(), query.getLimit(), query.getSearch());
        long total = brandDAO.countActive(query.getSearch());
        return new PagedResponse<>(list, total, query.getPage(), query.getLimit());
    }

    public List<Brand> findAllActive() {
        return brandDAO.findAllActive();
    }

    public Brand findById(Integer id) {
        return brandDAO.findById(id)
                .filter(b -> b.getDeletedAt() == null)
                .orElseThrow(() -> new NotFoundException("Thương hiệu không tồn tại hoặc đã bị xóa"));
    }

    public Brand create(Integer createdById, CreateBrandDTO dto) {
        if (dto.getDefaultName() == null || dto.getDefaultName().trim().isEmpty()) {
            throw new BadRequestException("Tên thương hiệu không được để trống");
        }

        String name = dto.getDefaultName().trim();
        if (brandDAO.findByName(name).isPresent()) {
            throw new AppException("Tên thương hiệu đã tồn tại", 422);
        }

        Brand brand = new Brand();
        brand.setDefaultName(name);
        brand.setLogoUrl(dto.getLogo());
        brand.setCreatedById(createdById);

        return brandDAO.save(brand);
    }

    public Brand update(Integer id, Integer updatedById, UpdateBrandDTO dto) {
        Brand brand = findById(id);

        if (dto.getDefaultName() != null && !dto.getDefaultName().trim().isEmpty()) {
            String newName = dto.getDefaultName().trim();
            brandDAO.findByName(newName).ifPresent(existing -> {
                if (!existing.getId().equals(id)) {
                    throw new AppException("Tên thương hiệu đã tồn tại", 422);
                }
            });
            brand.setDefaultName(newName);
        }

        if (dto.getLogo() != null) {
            brand.setLogoUrl(dto.getLogo());
        }

        brand.setUpdatedById(updatedById);
        return brandDAO.update(brand);
    }

    public void delete(Integer id, Integer deletedById) {
        Brand brand = findById(id);
        brand.setDeletedAt(LocalDateTime.now());
        brand.setDeletedById(deletedById);
        brandDAO.update(brand);
    }
}

