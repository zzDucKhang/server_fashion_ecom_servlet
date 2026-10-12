package com.fashionshop.presentation.dto.loyalty;

import com.fashionshop.common.util.Validatable;
import com.fashionshop.common.util.ValidationUtil;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RedeemPointsDTO implements Validatable {
    private Integer orderId;
    private Integer pointsToRedeem;

    @Override
    public Map<String, List<String>> validate() {
        Map<String, List<String>> errors = new LinkedHashMap<>();

        if (orderId == null || orderId <= 0) {
            ValidationUtil.addError(errors, "orderId", "Mã đơn hàng phải là số nguyên dương");
        }

        if (pointsToRedeem == null || pointsToRedeem <= 0) {
            ValidationUtil.addError(errors, "pointsToRedeem", "Số điểm quy đổi phải lớn hơn 0");
        }

        return errors;
    }

    public Integer getOrderId() { return orderId; }
    public void setOrderId(Integer orderId) { this.orderId = orderId; }

    public Integer getPointsToRedeem() { return pointsToRedeem; }
    public void setPointsToRedeem(Integer pointsToRedeem) { this.pointsToRedeem = pointsToRedeem; }
}