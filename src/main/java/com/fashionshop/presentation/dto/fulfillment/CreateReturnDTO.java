package com.fashionshop.presentation.dto.fulfillment;

import com.fashionshop.common.util.Validatable;
import com.fashionshop.common.util.ValidationUtil;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CreateReturnDTO implements Validatable {
    private Integer orderItemId;
    private Integer quantity;
    private String reason;
    private String description;
    private List<ReturnMediaDTO> medias = new ArrayList<>();

    @Override
    public Map<String, List<String>> validate() {
        Map<String, List<String>> errors = new LinkedHashMap<>();

        if (orderItemId == null || orderItemId <= 0) {
            ValidationUtil.addError(errors, "orderItemId", "Mã dòng đơn hàng phải là số nguyên dương");
        }

        if (quantity == null || quantity <= 0) {
            ValidationUtil.addError(errors, "quantity", "Số lượng trả phải lớn hơn 0");
        }

        if (reason == null || reason.isBlank()) {
            ValidationUtil.addError(errors, "reason", "Lý do trả hàng không được để trống");
        } else if (reason.trim().length() > 255) {
            ValidationUtil.addError(errors, "reason", "Lý do trả hàng tối đa 255 ký tự");
        }

        if (description != null && description.trim().length() > 1000) {
            ValidationUtil.addError(errors, "description", "Mô tả tối đa 1000 ký tự");
        }

        if (medias != null) {
            for (int i = 0; i < medias.size(); i++) {
                ReturnMediaDTO media = medias.get(i);
                if (media == null) {
                    ValidationUtil.addError(errors, "medias[" + i + "]", "Media không được để trống");
                    continue;
                }
                if (media.getMediaUrl() == null || media.getMediaUrl().isBlank()) {
                    ValidationUtil.addError(errors, "medias[" + i + "].mediaUrl", "Đường dẫn media không được để trống");
                } else if (media.getMediaUrl().trim().length() > 1000) {
                    ValidationUtil.addError(errors, "medias[" + i + "].mediaUrl", "Đường dẫn media tối đa 1000 ký tự");
                }
                if (media.getMediaType() != null && media.getMediaType().trim().length() > 50) {
                    ValidationUtil.addError(errors, "medias[" + i + "].mediaType", "Loại media tối đa 50 ký tự");
                }
            }
        }

        return errors;
    }

    public Integer getOrderItemId() { return orderItemId; }
    public void setOrderItemId(Integer orderItemId) { this.orderItemId = orderItemId; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public List<ReturnMediaDTO> getMedias() { return medias; }
    public void setMedias(List<ReturnMediaDTO> medias) { this.medias = medias; }
}
