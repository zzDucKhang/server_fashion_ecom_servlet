package com.fashionshop.business.service.catalog;

import com.fashionshop.business.entity.Brand;
import com.fashionshop.business.entity.Category;
import com.fashionshop.business.entity.Product;
import com.fashionshop.common.exception.BadRequestException;
import com.fashionshop.common.exception.ConflictException;
import com.fashionshop.common.exception.NotFoundException;
import com.fashionshop.common.response.PagedResponse;
import com.fashionshop.dataaccess.dao.catalog.BrandDAO;
import com.fashionshop.dataaccess.dao.catalog.CategoryDAO;
import com.fashionshop.dataaccess.dao.catalog.ProductDAO;
import com.fashionshop.presentation.dto.catalog.CreateProductDTO;
import com.fashionshop.presentation.dto.catalog.ProductQueryDTO;

import java.time.LocalDateTime;
import java.util.List;

public class ProductService {
    private final ProductDAO productDAO = new ProductDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final BrandDAO brandDAO = new BrandDAO();

    public PagedResponse<Product> listProducts(ProductQueryDTO query) {
        List<Product> products = productDAO.findWithFilter(query);
        long total = productDAO.countWithFilter(query);
        return new PagedResponse<>(products, total, query.getPage(), query.getLimit());
    }

    public Product getProductById(Integer id) {
        return productDAO.findById(id)
                .filter(p -> p.getDeletedAt() == null)
                .orElseThrow(() -> new NotFoundException("Sản phẩm không tồn tại hoặc đã bị xóa"));
    }

    public Product createProduct(CreateProductDTO dto) {
        if (dto.getDefaultName() == null || dto.getDefaultName().isBlank()) {
            throw new BadRequestException("Tên sản phẩm không được để trống");
        }
        if (dto.getCategoryId() == null) {
            throw new BadRequestException("Vui lòng chọn danh mục cho sản phẩm");
        }

        if (productDAO.findByName(dto.getDefaultName().trim()).isPresent()) {
            throw new ConflictException("Tên sản phẩm đã tồn tại trong hệ thống");
        }

        Category category = categoryDAO.findById(dto.getCategoryId())
                .orElseThrow(() -> new NotFoundException("Danh mục không tồn tại"));

        Product product = new Product();
        product.setDefaultName(dto.getDefaultName().trim());
        product.setThumbnailUrl(dto.getThumbnailUrl());
        product.setCategory(category);

        if (dto.getBrandId() != null) {
            Brand brand = brandDAO.findById(dto.getBrandId())
                    .orElseThrow(() -> new NotFoundException("Thương hiệu không tồn tại"));
            product.setBrand(brand);
        }

        return productDAO.save(product);
    }

    public void deleteProduct(Integer id) {
        Product product = getProductById(id);
        product.setDeletedAt(LocalDateTime.now());
        productDAO.update(product);
    }
}