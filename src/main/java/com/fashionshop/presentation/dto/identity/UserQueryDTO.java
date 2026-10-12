package com.fashionshop.presentation.dto.identity;

public class UserQueryDTO {

    private int page = 1;
    private int limit = 10;
    private String search;
    private String status;
    private Integer roleId;

    public UserQueryDTO() {}

    public UserQueryDTO(int page, int limit, String search, String status, Integer roleId) {
        setPage(page);
        setLimit(limit);
        this.search = search;
        this.status = status;
        this.roleId = roleId;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page > 0 ? page : 1;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        if (limit <= 0) {
            this.limit = 10;
        } else {
            this.limit = Math.min(limit, 100);
        }
    }

    public String getSearch() {
        return search != null ? search.trim() : null;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getStatus() {
        return status != null ? status.trim().toUpperCase() : null;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }
}
