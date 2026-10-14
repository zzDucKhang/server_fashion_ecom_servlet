package com.fashionshop.presentation.dto.catalog;

import com.fashionshop.business.entity.Product;
import com.fashionshop.common.util.Validatable;
import com.fashionshop.common.util.ValidationUtil;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class UpdateProductDTO implements Validatable {
    private String defaultName;
    private String thumbnailUrl;
    private Product.ProductStatus status;
    private Integer categoryId;
    private Integer brandId;
    private List<String> images;
    private List<Integer> tagIds;

    @Override
    public Map<String, List<String>> validate() {
        Map<String, List<String>> errors = new LinkedHashMap<>();

        if (defaultName != null) {
            if (defaultName.isBlank()) {
                ValidationUtil.addError(errors, "defaultName", "Tên sản phẩm không được để trống");
            } else if (defaultName.trim().length() > 255) {
                ValidationUtil.addError(errors, "defaultName", "Tên sản phẩm tối đa 255 ký tự");
            }
        }

        if (thumbnailUrl != null && thumbnailUrl.trim().length() > 1000) {
            ValidationUtil.addError(errors, "thumbnailUrl", "Đường dẫn ảnh đại diện tối đa 1000 ký tự");
        }

        if (categoryId != null && categoryId <= 0) {
            ValidationUtil.addError(errors, "categoryId", "ID danh mục phải là số nguyên dương");
        }

        if (brandId != null && brandId <= 0) {
            ValidationUtil.addError(errors, "brandId", "ID thương hiệu phải là số nguyên dương");
        }

        if (images != null) {
            for (int i = 0; i < images.size(); i++) {
                String image = images.get(i);
                if (image == null || image.isBlank()) {
                    ValidationUtil.addError(errors, "images[" + i + "]", "Đường dẫn hình ảnh không được để trống");
                } else if (image.trim().length() > 1000) {
                    ValidationUtil.addError(errors, "images[" + i + "]", "Đường dẫn hình ảnh tối đa 1000 ký tự");
                }
            }
        }

        if (tagIds != null) {
            for (int i = 0; i < tagIds.size(); i++) {
                Integer tagId = tagIds.get(i);
                if (tagId == null || tagId <= 0) {
                    ValidationUtil.addError(errors, "tagIds[" + i + "]", "ID tag phải là số nguyên dương");
                }
            }
        }

        return errors;
    }

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