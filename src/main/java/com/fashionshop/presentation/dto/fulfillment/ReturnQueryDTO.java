package com.fashionshop.presentation.dto.fulfillment;

import com.fashionshop.business.entity.ReturnRequest;

public class ReturnQueryDTO {
    private int page = 1;
    private int limit = 10;
    private ReturnRequest.ReturnStatus status;
    private Integer orderItemId;

    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }
    public int getLimit() { return limit; }
    public void setLimit(int limit) { this.limit = limit; }
    public ReturnRequest.ReturnStatus getStatus() { return status; }
    public void setStatus(ReturnRequest.ReturnStatus status) { this.status = status; }
    public Integer getOrderItemId() { return orderItemId; }
    public void setOrderItemId(Integer orderItemId) { this.orderItemId = orderItemId; }
}
