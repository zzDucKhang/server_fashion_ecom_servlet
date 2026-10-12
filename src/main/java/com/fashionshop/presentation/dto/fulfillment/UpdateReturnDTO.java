package com.fashionshop.presentation.dto.fulfillment;

public class UpdateReturnDTO {
    private String status; // APPROVED, REJECTED, COMPLETED

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}

