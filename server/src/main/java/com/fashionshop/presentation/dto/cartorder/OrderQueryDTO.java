package com.fashionshop.presentation.dto.cartorder;

public class OrderQueryDTO {
    private int page = 1;
    private int limit = 10;
    private String status;
    private Boolean all;

    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }
    public int getLimit() { return limit; }
    public void setLimit(int limit) { this.limit = limit; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Boolean getAll() { return all; }
    public void setAll(Boolean all) { this.all = all; }
}

