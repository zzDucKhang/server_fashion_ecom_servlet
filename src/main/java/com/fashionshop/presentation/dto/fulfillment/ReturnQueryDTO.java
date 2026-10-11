package com.fashionshop.presentation.dto.fulfillment;

import com.fashionshop.business.entity.ReturnRequest;

public class ReturnQueryDTO {
    private ReturnRequest.ReturnStatus status;
    private Integer orderItemId;

    public ReturnRequest.ReturnStatus getStatus() { return status; }
    public void setStatus(ReturnRequest.ReturnStatus status) { this.status = status; }
    public Integer getOrderItemId() { return orderItemId; }
    public void setOrderItemId(Integer orderItemId) { this.orderItemId = orderItemId; }
}