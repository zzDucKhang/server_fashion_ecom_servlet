package com.fashionshop.business.service.catalog;

import com.fashionshop.business.entity.*;
import com.fashionshop.common.exception.AppException;
import com.fashionshop.common.exception.BadRequestException;
import com.fashionshop.common.exception.NotFoundException;
import com.fashionshop.common.response.PagedResponse;
import com.fashionshop.dataaccess.dao.catalog.BrandDAO;
import com.fashionshop.dataaccess.dao.catalog.CategoryDAO;
import com.fashionshop.dataaccess.dao.catalog.ProductDAO;
import com.fashionshop.dataaccess.dao.catalog.TagDAO;
import com.fashionshop.presentation.dto.catalog.CreateProductDTO;
import com.fashionshop.presentation.dto.catalog.ProductQueryDTO;
import com.fashionshop.presentation.dto.catalog.UpdateProductDTO;

import java.time.LocalDateTime;
import java.util.List;

public class ProductService {
    private final ProductDAO productDAO = new ProductDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final BrandDAO brandDAO = new BrandDAO();
    private final TagDAO tagDAO = new TagDAO();

    public PagedResponse<Product> list(ProductQueryDTO query) {
        List<Product> list = productDAO.listProducts(query);
        long total = productDAO.countProducts(query);
        return new PagedResponse<>(list, total, query.getPage(), query.getLimit());
    }

    public Product findById(Integer id) {
        return productDAO.findDetailById(id)
                .orElseThrow(() -> new NotFoundException("Sản phẩm không tồn tại hoặc đã bị xóa"));
    }

    public Product create(Integer createdById, CreateProductDTO dto) {
        if (dto.getDefaultName() == null || dto.getDefaultName().trim().isEmpty()) {
            throw new BadRequestException("Tên sản phẩm không được để trống");
        }
        if (dto.getCategoryId() == null) {
            throw new BadRequestException("Danh mục là bắt buộc");
        }

        // Bắt lỗi trùng lặp (Status 422 giống Node.js)
        if (productDAO.findByName(dto.getDefaultName().trim()).isPresent()) {
            throw new AppException("Tên sản phẩm bị trùng lặp", 422);
        }

        Category category = categoryDAO.findById(dto.getCategoryId())
                .orElseThrow(() -> new NotFoundException("Danh mục không tồn tại"));

        Product product = new Product();
        product.setDefaultName(dto.getDefaultName().trim());
        product.setThumbnailUrl(dto.getThumbnailUrl());
        product.setStatus(dto.getStatus() != null ? dto.getStatus() : Product.ProductStatus.DRAFT);
        product.setCategory(category);
        product.setCreatedById(createdById);

        if (dto.getBrandId() != null) {
            Brand brand = brandDAO.findById(dto.getBrandId())
                    .orElseThrow(() -> new NotFoundException("Thương hiệu không tồn tại"));
            product.setBrand(brand);
        }

        // Gán danh sách hình ảnh (DisplayOrder từ 0 -> n)
        if (dto.getImages() != null) {
            for (int i = 0; i < dto.getImages().size(); i++) {
                product.addImage(dto.getImages().get(i), i);
            }
        }

        // Gán Tags
        if (dto.getTagIds() != null && !dto.getTagIds().isEmpty()) {
            product.setTags(tagDAO.findByIds(dto.getTagIds()));
        }

        return productDAO.save(product);
    }

    public Product update(Integer id, Integer updatedById, UpdateProductDTO dto) {
        Product product = findById(id);

        if (dto.getDefaultName() != null && !dto.getDefaultName().trim().isEmpty()) {
            String newName = dto.getDefaultName().trim();
            productDAO.findByName(newName).ifPresent(existing -> {
                if (!existing.getId().equals(id)) {
                    throw new AppException("Tên sản phẩm bị trùng lặp", 422);
                }
            });
            product.setDefaultName(newName);
        }

        if (dto.getThumbnailUrl() != null) product.setThumbnailUrl(dto.getThumbnailUrl());
        if (dto.getStatus() != null) product.setStatus(dto.getStatus());

        if (dto.getCategoryId() != null) {
            Category category = categoryDAO.findById(dto.getCategoryId())
                    .orElseThrow(() -> new NotFoundException("Danh mục không tồn tại"));
            product.setCategory(category);
        }

        if (dto.getBrandId() != null) {
            Brand brand = brandDAO.findById(dto.getBrandId())
                    .orElseThrow(() -> new NotFoundException("Thương hiệu không tồn tại"));
            product.setBrand(brand);
        }

        // Cập nhật lại Tags
        if (dto.getTagIds() != null) {
            product.setTags(tagDAO.findByIds(dto.getTagIds()));
        }

        // Cập nhật lại danh sách hình ảnh
        if (dto.getImages() != null) {
            product.getImages().clear();
            for (int i = 0; i < dto.getImages().size(); i++) {
                product.addImage(dto.getImages().get(i), i);
            }
        }

        product.setUpdatedById(updatedById);
        return productDAO.update(product);
    }

    public void delete(Integer id, Integer deletedById) {
        Product product = findById(id);
        product.setDeletedAt(LocalDateTime.now());
        product.setDeletedById(deletedById);
        productDAO.update(product);
    }
}