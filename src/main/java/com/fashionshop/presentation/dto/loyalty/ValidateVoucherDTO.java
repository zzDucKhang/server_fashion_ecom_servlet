package com.fashionshop.presentation.dto.loyalty;

import java.math.BigDecimal;

public class ValidateVoucherDTO {
    private String code;
    private BigDecimal orderTotal;

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public BigDecimal getOrderTotal() { return orderTotal; }
    public void setOrderTotal(BigDecimal orderTotal) { this.orderTotal = orderTotal; }
}