package com.fashionshop.presentation.dto.loyalty;

import com.fashionshop.common.util.Validatable;
import com.fashionshop.common.util.ValidationUtil;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ValidateVoucherDTO implements Validatable {
    private String code;
    private BigDecimal orderTotal;

    @Override
    public Map<String, List<String>> validate() {
        Map<String, List<String>> errors = new LinkedHashMap<>();

        if (code == null || code.isBlank()) {
            ValidationUtil.addError(errors, "code", "Mã voucher không được để trống");
        } else if (code.trim().length() > 100) {
            ValidationUtil.addError(errors, "code", "Mã voucher tối đa 100 ký tự");
        }

        if (orderTotal == null) {
            ValidationUtil.addError(errors, "orderTotal", "Tổng tiền đơn hàng không được để trống");
        } else if (orderTotal.compareTo(BigDecimal.ZERO) < 0) {
            ValidationUtil.addError(errors, "orderTotal", "Tổng tiền đơn hàng không được âm");
        }

        return errors;
    }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public BigDecimal getOrderTotal() { return orderTotal; }
    public void setOrderTotal(BigDecimal orderTotal) { this.orderTotal = orderTotal; }
}