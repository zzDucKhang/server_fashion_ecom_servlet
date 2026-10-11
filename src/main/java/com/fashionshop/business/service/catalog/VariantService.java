package com.fashionshop.business.service.catalog;

import com.fashionshop.business.entity.ProductVariant;
import com.fashionshop.common.exception.AppException;
import com.fashionshop.common.exception.BadRequestException;
import com.fashionshop.common.exception.NotFoundException;
import com.fashionshop.dataaccess.dao.catalog.ProductDAO;
import com.fashionshop.dataaccess.dao.catalog.ProductVariantDAO;
import com.fashionshop.presentation.dto.catalog.SyncVariantsDTO;
import com.fashionshop.presentation.dto.catalog.UpsertVariantItemDTO;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class VariantService {
    private final ProductVariantDAO variantDAO = new ProductVariantDAO();
    private final ProductDAO productDAO = new ProductDAO();

    public List<ProductVariant> getVariantsByProductId(Integer productId) {
        productDAO.findById(productId)
                .filter(p -> p.getDeletedAt() == null)
                .orElseThrow(() -> new NotFoundException("Sản phẩm không tồn tại hoặc đã bị xóa"));
        return variantDAO.findByProductId(productId);
    }

    public ProductVariant getVariantById(Integer variantId) {
        return variantDAO.findActiveById(variantId)
                .orElseThrow(() -> new NotFoundException("Biến thể không tồn tại hoặc đã bị vô hiệu hóa"));
    }

    public List<ProductVariant> syncProductVariants(SyncVariantsDTO data, Integer userId) {
        if (data.getProductId() == null) {
            throw new BadRequestException("productId phải là số nguyên dương");
        }
        if (data.getVariants() == null || data.getVariants().isEmpty()) {
            throw new BadRequestException("Danh sách biến thể không được để trống");
        }

        productDAO.findById(data.getProductId())
                .filter(p -> p.getDeletedAt() == null)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy sản phẩm tương ứng để đồng bộ biến thể"));

        Set<String> skuSet = new HashSet<>();
        for (int i = 0; i < data.getVariants().size(); i++) {
            UpsertVariantItemDTO item = data.getVariants().get(i);
            if (item.getSku() == null || item.getSku().trim().isEmpty()) {
                throw new BadRequestException("Mã SKU không được để trống tại dòng " + (i + 1));
            }
            String skuLower = item.getSku().trim().toLowerCase();
            if (skuSet.contains(skuLower)) {
                throw new AppException("Mã SKU bị trùng lặp trong danh sách: " + item.getSku(), 422);
            }
            skuSet.add(skuLower);

            if (item.getOriginalPrice() == null || item.getOriginalPrice().doubleValue() <= 0) {
                throw new BadRequestException("Giá gốc phải lớn hơn 0 tại SKU " + item.getSku());
            }
            if (item.getSellingPrice() == null || item.getSellingPrice().doubleValue() <= 0) {
                throw new BadRequestException("Giá bán phải lớn hơn 0 tại SKU " + item.getSku());
            }
            if (item.getSellingPrice().compareTo(item.getOriginalPrice()) > 0) {
                throw new BadRequestException("Giá bán (" + item.getSellingPrice() + ") không được lớn hơn giá gốc (" + item.getOriginalPrice() + ") tại SKU " + item.getSku());
            }
        }

        return variantDAO.syncVariants(data, userId);
    }

    public ProductVariant updateStock(Integer variantId, int delta) {
        return variantDAO.updateStockOptimistic(variantId, delta);
    }
}

