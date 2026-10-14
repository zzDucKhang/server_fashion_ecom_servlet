package com.fashionshop.presentation.dto.loyalty;

import com.fashionshop.common.util.Validatable;
import com.fashionshop.common.util.ValidationUtil;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class EarnPointsDTO implements Validatable {
    private Integer orderId;
    private String description = "Cộng điểm từ đơn hàng";

    @Override
    public Map<String, List<String>> validate() {
        Map<String, List<String>> errors = new LinkedHashMap<>();

        if (orderId == null || orderId <= 0) {
            ValidationUtil.addError(errors, "orderId", "Mã đơn hàng phải là số nguyên dương");
        }

        if (description != null && description.trim().length() > 255) {
            ValidationUtil.addError(errors, "description", "Mô tả tối đa 255 ký tự");
        }

        return errors;
    }

    public Integer getOrderId() { return orderId; }
    public void setOrderId(Integer orderId) { this.orderId = orderId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}