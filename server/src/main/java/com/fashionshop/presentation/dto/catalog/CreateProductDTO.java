package com.fashionshop.presentation.dto.catalog;

import com.fashionshop.business.entity.Product;
import com.fashionshop.common.util.Validatable;
import com.fashionshop.common.util.ValidationUtil;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CreateProductDTO implements Validatable {
    private String defaultName;
    private String thumbnailUrl;
    private Product.ProductStatus status = Product.ProductStatus.DRAFT;
    private Integer categoryId;
    private Integer brandId;
    private List<String> images;
    private List<Integer> tagIds;

    @Override
    public Map<String, List<String>> validate() {
        Map<String, List<String>> errors = new LinkedHashMap<>();

        if (defaultName == null || defaultName.trim().isEmpty()) {
            ValidationUtil.addError(errors, "defaultName", "Tên sản phẩm không được để trống");
        } else if (defaultName.trim().length() > 255) {
            ValidationUtil.addError(errors, "defaultName", "Tên sản phẩm tối đa 255 ký tự");
        }

        if (categoryId == null || categoryId <= 0) {
            ValidationUtil.addError(errors, "categoryId", "Danh mục là bắt buộc và phải hợp lệ");
        }

        if (brandId != null && brandId <= 0) {
            ValidationUtil.addError(errors, "brandId", "Thương hiệu không hợp lệ");
        }

        return errors;
    }

    // Giữ nguyên các Getter/Setter của bạn...
    public String getDefaultName() { return defaultName; }
    public void setDefaultName(String defaultName) { this.defaultName = defaultName; }
    public String getThumbnailUrl() { return thumbnailUrl; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }
    public Product.ProductStatus getStatus() { return status; }
    public void setStatus(Product.ProductStatus status) { this.status = status; }
    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }
    public Integer getBrandId() { return brandId; }
    public void setBrandId(Integer brandId) { this.brandId = brandId; }
    public List<String> getImages() { return images; }
    public void setImages(List<String> images) { this.images = images; }
    public List<Integer> getTagIds() { return tagIds; }
    public void setTagIds(List<Integer> tagIds) { this.tagIds = tagIds; }
}