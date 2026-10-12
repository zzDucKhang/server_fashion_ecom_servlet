package com.fashionshop.presentation.dto.cartorder;

public class OrderQueryDTO {
    private int page = 1;
    private int limit = 10;
    private String status;

    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }
    public int getLimit() { return limit; }
    public void setLimit(int limit) { this.limit = limit; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}

