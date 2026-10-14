package com.fashionshop.presentation.dto.cartorder;

import com.fashionshop.business.entity.Order;
import com.fashionshop.common.util.Validatable;
import com.fashionshop.common.util.ValidationUtil;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class UpdateOrderStatusDTO implements Validatable {
    private String status;

    @Override
    public Map<String, List<String>> validate() {
        Map<String, List<String>> errors = new LinkedHashMap<>();

        if (status == null || status.isBlank()) {
            ValidationUtil.addError(errors, "status", "Trạng thái đơn hàng không được để trống");
        } else {
            try {
                Order.OrderStatus.valueOf(status.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                ValidationUtil.addError(errors, "status", "Trạng thái đơn hàng không hợp lệ");
            }
        }

        return errors;
    }

    public String getStatus() { return status != null ? status.trim().toUpperCase() : null; }
    public void setStatus(String status) { this.status = status; }
}

