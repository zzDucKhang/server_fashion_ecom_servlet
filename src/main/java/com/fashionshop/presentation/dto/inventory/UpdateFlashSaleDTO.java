package com.fashionshop.presentation.dto.inventory;

import com.fashionshop.common.util.Validatable;
import com.fashionshop.common.util.ValidationUtil;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class UpdateFlashSaleDTO implements Validatable {
    private String name;
    private String bannerUrl;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Boolean isActive;
    private List<FlashSaleItemDTO> items;

    @Override
    public Map<String, List<String>> validate() {
        Map<String, List<String>> errors = new LinkedHashMap<>();

        if (name != null) {
            if (name.isBlank()) {
                ValidationUtil.addError(errors, "name", "Tên chiến dịch không được để trống");
            } else if (name.trim().length() > 255) {
                ValidationUtil.addError(errors, "name", "Tên chiến dịch tối đa 255 ký tự");
            }
        }

        if (bannerUrl != null && bannerUrl.trim().length() > 1000) {
            ValidationUtil.addError(errors, "bannerUrl", "Đường dẫn banner tối đa 1000 ký tự");
        }

        if (startTime != null && endTime != null && !startTime.isBefore(endTime)) {
            ValidationUtil.addError(errors, "endTime", "Thời gian kết thúc phải sau thời gian bắt đầu");
        }

        if (items != null) {
            if (items.isEmpty()) {
                ValidationUtil.addError(errors, "items", "Danh sách sản phẩm Flash Sale không được để trống");
            } else {
                Set<Integer> variantIds = new HashSet<>();
                for (int i = 0; i < items.size(); i++) {
                    FlashSaleItemDTO item = items.get(i);
                    if (item == null) {
                        ValidationUtil.addError(errors, "items[" + i + "]", "Sản phẩm Flash Sale không được để trống");
                        continue;
                    }

                    if (item.getVariantId() == null || item.getVariantId() <= 0) {
                        ValidationUtil.addError(errors, "items[" + i + "].variantId", "variantId phải là số nguyên dương");
                    } else if (!variantIds.add(item.getVariantId())) {
                        ValidationUtil.addError(errors, "items", "Không được có sản phẩm trùng trong Flash Sale");
                    }

                    if (item.getFlashSalePrice() == null || item.getFlashSalePrice().doubleValue() <= 0) {
                        ValidationUtil.addError(errors, "items[" + i + "].flashSalePrice", "Giá flash sale phải lớn hơn 0");
                    }

                    if (item.getPromoQuantity() == null || item.getPromoQuantity() <= 0) {
                        ValidationUtil.addError(errors, "items[" + i + "].promoQuantity", "Số lượng khuyến mãi phải lớn hơn 0");
                    }
                }
            }
        }

        return errors;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getBannerUrl() { return bannerUrl; }
    public void setBannerUrl(String bannerUrl) { this.bannerUrl = bannerUrl; }
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    public List<FlashSaleItemDTO> getItems() { return items; }
    public void setItems(List<FlashSaleItemDTO> items) { this.items = items; }
}