package com.fashionshop.presentation.dto.fulfillment;

import com.fashionshop.business.entity.ReturnRequest;

public class UpdateReturnDTO {
    private Integer quantity;
    private String reason;
    private String description;
    private ReturnRequest.ReturnStatus status;

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public ReturnRequest.ReturnStatus getStatus() { return status; }
    public void setStatus(ReturnRequest.ReturnStatus status) { this.status = status; }
}