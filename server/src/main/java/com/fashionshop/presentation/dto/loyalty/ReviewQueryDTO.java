package com.fashionshop.presentation.dto.loyalty;

public class ReviewQueryDTO {
    private Integer productId;
    private int page = 1;
    private int limit = 10;
    private Integer rating;

    public Integer getProductId() { return productId; }
    public void setProductId(Integer productId) { this.productId = productId; }
    public int getPage() { return page; }
    public void setPage(int page) { this.page = page > 0 ? page : 1; }
    public int getLimit() { return limit; }
    public void setLimit(int limit) { this.limit = limit > 0 ? limit : 10; }
    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }
}