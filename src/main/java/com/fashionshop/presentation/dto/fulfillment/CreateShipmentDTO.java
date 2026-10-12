package com.fashionshop.presentation.dto.fulfillment;

import java.math.BigDecimal;

public class CreateShipmentDTO {
    private Integer orderId;
    private String carrier;
    private String trackingCode;
    private BigDecimal actualShippingFee;
    private String estimatedDelivery;

    public Integer getOrderId() { return orderId; }
    public void setOrderId(Integer orderId) { this.orderId = orderId; }
    public String getCarrier() { return carrier; }
    public void setCarrier(String carrier) { this.carrier = carrier; }
    public String getTrackingCode() { return trackingCode; }
    public void setTrackingCode(String trackingCode) { this.trackingCode = trackingCode; }
    public BigDecimal getActualShippingFee() { return actualShippingFee; }
    public void setActualShippingFee(BigDecimal actualShippingFee) { this.actualShippingFee = actualShippingFee; }
    public String getEstimatedDelivery() { return estimatedDelivery; }
    public void setEstimatedDelivery(String estimatedDelivery) { this.estimatedDelivery = estimatedDelivery; }
}

