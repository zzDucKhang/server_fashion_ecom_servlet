package com.fashionshop.presentation.dto.fulfillment;

import com.fashionshop.business.entity.ReturnRequest;
import com.fashionshop.common.util.Validatable;
import com.fashionshop.common.util.ValidationUtil;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class UpdateReturnDTO implements Validatable {
    private Integer quantity;
    private String reason;
    private String description;
    private ReturnRequest.ReturnStatus status;

    @Override
    public Map<String, List<String>> validate() {
        Map<String, List<String>> errors = new LinkedHashMap<>();

        if (quantity != null && quantity <= 0) {
            ValidationUtil.addError(errors, "quantity", "Số lượng trả phải lớn hơn 0");
        }

        if (reason != null) {
            if (reason.isBlank()) {
                ValidationUtil.addError(errors, "reason", "Lý do trả hàng không được để trống");
            } else if (reason.trim().length() > 255) {
                ValidationUtil.addError(errors, "reason", "Lý do trả hàng tối đa 255 ký tự");
            }
        }

        if (description != null && description.trim().length() > 1000) {
            ValidationUtil.addError(errors, "description", "Mô tả tối đa 1000 ký tự");
        }

        return errors;
    }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public ReturnRequest.ReturnStatus getStatus() { return status; }
    public void setStatus(ReturnRequest.ReturnStatus status) { this.status = status; }
    public void setStatus(String status) {
        if (status != null && !status.isBlank()) {
            this.status = ReturnRequest.ReturnStatus.valueOf(status.trim().toUpperCase());
        }
    }
}
