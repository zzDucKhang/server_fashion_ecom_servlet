package com.fashionshop.presentation.dto.cartorder;

import com.fashionshop.common.util.Validatable;
import com.fashionshop.common.util.ValidationUtil;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class UpdateCartItemDTO implements Validatable {
    private Integer quantity;

    @Override
    public Map<String, List<String>> validate() {
        Map<String, List<String>> errors = new LinkedHashMap<>();

        if (quantity == null || quantity < 0 || quantity > 100) {
            ValidationUtil.addError(errors, "quantity", "Số lượng phải từ 0 đến 100");
        }

        return errors;
    }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}