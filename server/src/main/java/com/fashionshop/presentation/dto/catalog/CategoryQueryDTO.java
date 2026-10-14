package com.fashionshop.presentation.dto.catalog;

public class CategoryQueryDTO {
    private Integer parentCategoryId;
    private String search;

    public Integer getParentCategoryId() { return parentCategoryId; }
    public void setParentCategoryId(Integer parentCategoryId) { this.parentCategoryId = parentCategoryId; }

    public String getSearch() { return search; }
    public void setSearch(String search) { this.search = search; }
}

