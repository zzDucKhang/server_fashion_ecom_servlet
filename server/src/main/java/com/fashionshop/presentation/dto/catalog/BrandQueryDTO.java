package com.fashionshop.presentation.dto.catalog;

public class BrandQueryDTO {
    private int page = 1;
    private int limit = 10;
    private String search;

    public int getPage() { return page > 0 ? page : 1; }
    public void setPage(int page) { this.page = page > 0 ? page : 1; }

    public int getLimit() { return limit > 0 ? limit : 10; }
    public void setLimit(int limit) { this.limit = limit > 0 ? limit : 10; }

    public String getSearch() { return search; }
    public void setSearch(String search) { this.search = search; }
}

