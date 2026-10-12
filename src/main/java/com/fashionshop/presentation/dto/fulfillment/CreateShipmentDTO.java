package com.fashionshop.presentation.dto.fulfillment;

import com.fashionshop.common.util.Validatable;
import com.fashionshop.common.util.ValidationUtil;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CreateShipmentDTO implements Validatable {
    private Integer orderId;
    private String carrier;
    private String trackingCode;
    private BigDecimal actualShippingFee;
    private String estimatedDelivery;

    @Override
    public Map<String, List<String>> validate() {
        Map<String, List<String>> errors = new LinkedHashMap<>();

        if (orderId == null || orderId <= 0) {
            ValidationUtil.addError(errors, "orderId", "Mã đơn hàng phải là số nguyên dương");
        }

        if (carrier == null || carrier.isBlank()) {
            ValidationUtil.addError(errors, "carrier", "Đơn vị vận chuyển không được để trống");
        } else if (carrier.trim().length() > 100) {
            ValidationUtil.addError(errors, "carrier", "Đơn vị vận chuyển tối đa 100 ký tự");
        }

        if (trackingCode == null || trackingCode.isBlank()) {
            ValidationUtil.addError(errors, "trackingCode", "Mã vận đơn không được để trống");
        } else if (trackingCode.trim().length() > 150) {
            ValidationUtil.addError(errors, "trackingCode", "Mã vận đơn tối đa 150 ký tự");
        }

        if (actualShippingFee != null && actualShippingFee.compareTo(BigDecimal.ZERO) < 0) {
            ValidationUtil.addError(errors, "actualShippingFee", "Phí vận chuyển thực tế không được âm");
        }

        if (estimatedDelivery != null && estimatedDelivery.trim().length() > 255) {
            ValidationUtil.addError(errors, "estimatedDelivery", "Ngày giao dự kiến tối đa 255 ký tự");
        }

        return errors;
    }

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

