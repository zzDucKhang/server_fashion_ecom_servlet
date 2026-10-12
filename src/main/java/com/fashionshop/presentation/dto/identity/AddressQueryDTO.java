package com.fashionshop.presentation.dto.identity;

public class AddressQueryDTO {

    private int page = 1;
    private int limit = 10;

    public AddressQueryDTO() {}

    public AddressQueryDTO(int page, int limit) {
        setPage(page);
        setLimit(limit);
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
}
