package com.fashionshop.common.response;

import java.util.List;

public class PagedResponse<T> {
    private List<T> data;
    private long totalItems;
    private int page;
    private int limit;
    private int totalPages;

    public PagedResponse() {}

    public PagedResponse(List<T> data, long totalItems, int page, int limit) {
        this.data = data;
        this.totalItems = totalItems;
        this.page = page;
        this.limit = limit;
        this.totalPages = (limit > 0) ? (int) Math.ceil((double) totalItems / limit) : 1;
    }

    public List<T> getData() { return data; }
    public void setData(List<T> data) { this.data = data; }

    public long getTotalItems() { return totalItems; }
    public void setTotalItems(long totalItems) { this.totalItems = totalItems; }

    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }

    public int getLimit() { return limit; }
    public void setLimit(int limit) { this.limit = limit; }

    public int getTotalPages() { return totalPages; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }
}