package com.fashionshop.presentation.dto.fulfillment;

import com.fashionshop.business.entity.Shipment;
import com.fashionshop.common.util.Validatable;
import com.fashionshop.common.util.ValidationUtil;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class UpdateShipmentDTO implements Validatable {
    private String status;
    private String trackingCode;

    @Override
    public Map<String, List<String>> validate() {
        Map<String, List<String>> errors = new LinkedHashMap<>();

        if (status == null || status.isBlank()) {
            ValidationUtil.addError(errors, "status", "Trạng thái vận đơn không được để trống");
        } else {
            try {
                Shipment.ShipmentStatus.valueOf(status.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                ValidationUtil.addError(errors, "status", "Trạng thái vận đơn không hợp lệ");
            }
        }

        if (trackingCode != null && trackingCode.trim().length() > 150) {
            ValidationUtil.addError(errors, "trackingCode", "Mã vận đơn tối đa 150 ký tự");
        }

        return errors;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getTrackingCode() { return trackingCode; }
    public void setTrackingCode(String trackingCode) { this.trackingCode = trackingCode; }
}

