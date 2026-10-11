package com.fashionshop.presentation.dto.catalog;

public class ProductQueryDTO {
    private int page = 1;
    private int limit = 10;
    private String search;
    private Integer categoryId;
    private Integer brandId;

    public int getPage() { return page; }
    public void setPage(int page) { this.page = page > 0 ? page : 1; }

    public int getLimit() { return limit; }
    public void setLimit(int limit) { this.limit = limit > 0 ? limit : 10; }

    public String getSearch() { return search; }
    public void setSearch(String search) { this.search = search; }

    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }

    public Integer getBrandId() { return brandId; }
    public void setBrandId(Integer brandId) { this.brandId = brandId; }
}