package com.fashionshop.presentation.dto.loyalty;

public class EarnPointsDTO {
    private Integer orderId;
    private String description = "Cộng điểm từ đơn hàng";

    public Integer getOrderId() { return orderId; }
    public void setOrderId(Integer orderId) { this.orderId = orderId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}