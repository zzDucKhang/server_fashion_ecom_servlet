package com.fashionshop.presentation.dto.loyalty;

public class RedeemPointsDTO {
    private Integer orderId;
    private Integer pointsToRedeem;

    public Integer getOrderId() { return orderId; }
    public void setOrderId(Integer orderId) { this.orderId = orderId; }

    public Integer getPointsToRedeem() { return pointsToRedeem; }
    public void setPointsToRedeem(Integer pointsToRedeem) { this.pointsToRedeem = pointsToRedeem; }
}