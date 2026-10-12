package com.fashionshop.presentation.dto.fulfillment;

public class UpdateShipmentDTO {
    private String status;
    private String trackingCode;

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getTrackingCode() { return trackingCode; }
    public void setTrackingCode(String trackingCode) { this.trackingCode = trackingCode; }
}

