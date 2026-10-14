package com.fashionshop.presentation.dto.catalog;

import com.fashionshop.common.util.Validatable;
import com.fashionshop.common.util.ValidationUtil;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SyncVariantsDTO implements Validatable {
    private Integer productId;
    private List<UpsertVariantItemDTO> variants;

    @Override
    public Map<String, List<String>> validate() {
        Map<String, List<String>> errors = new LinkedHashMap<>();

        if (productId == null || productId <= 0) {
            ValidationUtil.addError(errors, "productId", "ID sản phẩm phải là số nguyên dương");
        }

        if (variants == null || variants.isEmpty()) {
            ValidationUtil.addError(errors, "variants", "Danh sách biến thể không được để trống");
        } else {
            Set<String> skus = new HashSet<>();
            for (int i = 0; i < variants.size(); i++) {
                UpsertVariantItemDTO item = variants.get(i);
                if (item == null) {
                    ValidationUtil.addError(errors, "variants[" + i + "]", "Biến thể không được để trống");
                    continue;
                }

                if (item.getSku() == null || item.getSku().isBlank()) {
                    ValidationUtil.addError(errors, "variants[" + i + "].sku", "SKU không được để trống");
                } else {
                    String sku = item.getSku().trim().toLowerCase();
                    if (!skus.add(sku)) {
                        ValidationUtil.addError(errors, "variants", "SKU bị trùng trong danh sách biến thể");
                    }
                }

                if (item.getOriginalPrice() == null || item.getOriginalPrice().compareTo(BigDecimal.ZERO) <= 0) {
                    ValidationUtil.addError(errors, "variants[" + i + "].originalPrice", "Giá gốc phải lớn hơn 0");
                }

                if (item.getSellingPrice() == null || item.getSellingPrice().compareTo(BigDecimal.ZERO) <= 0) {
                    ValidationUtil.addError(errors, "variants[" + i + "].sellingPrice", "Giá bán phải lớn hơn 0");
                } else if (item.getOriginalPrice() != null && item.getSellingPrice().compareTo(item.getOriginalPrice()) > 0) {
                    ValidationUtil.addError(errors, "variants[" + i + "].sellingPrice", "Giá bán không được lớn hơn giá gốc");
                }

                if (item.getStockQuantity() != null && item.getStockQuantity() < 0) {
                    ValidationUtil.addError(errors, "variants[" + i + "].stockQuantity", "Tồn kho không được âm");
                }

                if (item.getWeightGram() != null && item.getWeightGram() < 0) {
                    ValidationUtil.addError(errors, "variants[" + i + "].weightGram", "Khối lượng không được âm");
                }

                if (item.getVariant() != null && item.getVariant().trim().length() > 100) {
                    ValidationUtil.addError(errors, "variants[" + i + "].variant", "Tên thuộc tính tối đa 100 ký tự");
                }

                if (item.getVariantValue() != null && item.getVariantValue().trim().length() > 100) {
                    ValidationUtil.addError(errors, "variants[" + i + "].variantValue", "Giá trị thuộc tính tối đa 100 ký tự");
                }

                if (item.getImageUrl() != null && item.getImageUrl().trim().length() > 1000) {
                    ValidationUtil.addError(errors, "variants[" + i + "].imageUrl", "Đường dẫn ảnh tối đa 1000 ký tự");
                }
            }
        }

        return errors;
    }

    public Integer getProductId() { return productId; }
    public void setProductId(Integer productId) { this.productId = productId; }

    public List<UpsertVariantItemDTO> getVariants() { return variants; }
    public void setVariants(List<UpsertVariantItemDTO> variants) { this.variants = variants; }
}
