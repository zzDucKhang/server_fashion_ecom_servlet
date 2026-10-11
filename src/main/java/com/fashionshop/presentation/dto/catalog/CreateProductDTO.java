package com.fashionshop.presentation.dto.catalog;

import java.util.List;

public class CreateProductDTO {
    private String defaultName;
    private String thumbnailUrl;
    private Integer categoryId;
    private Integer brandId;
    private List<Integer> tagIds;

    public String getDefaultName() { return defaultName; }
    public void setDefaultName(String defaultName) { this.defaultName = defaultName; }

    public String getThumbnailUrl() { return thumbnailUrl; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }

    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }

    public Integer getBrandId() { return brandId; }
    public void setBrandId(Integer brandId) { this.brandId = brandId; }

    public List<Integer> getTagIds() { return tagIds; }
    public void setTagIds(List<Integer> tagIds) { this.tagIds = tagIds; }
}