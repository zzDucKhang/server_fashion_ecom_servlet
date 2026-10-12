package com.fashionshop.presentation.dto.loyalty;

import com.fashionshop.common.util.Validatable;
import com.fashionshop.common.util.ValidationUtil;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ReplyReviewDTO implements Validatable {
    private String shopReply;

    @Override
    public Map<String, List<String>> validate() {
        Map<String, List<String>> errors = new LinkedHashMap<>();

        if (shopReply == null || shopReply.isBlank()) {
            ValidationUtil.addError(errors, "shopReply", "Nội dung phản hồi không được để trống");
        } else if (shopReply.trim().length() > 2000) {
            ValidationUtil.addError(errors, "shopReply", "Nội dung phản hồi tối đa 2000 ký tự");
        }

        return errors;
    }

    public String getShopReply() { return shopReply; }
    public void setShopReply(String shopReply) { this.shopReply = shopReply; }
}