package com.fashionshop.presentation.dto.loyalty;

public class PointQueryDTO {
    private int page = 1;
    private int limit = 10;

    public int getPage() { return page; }
    public void setPage(int page) { this.page = page > 0 ? page : 1; }

    public int getLimit() { return limit; }
    public void setLimit(int limit) { this.limit = limit > 0 ? limit : 10; }
}