package com.fashionshop.presentation.dto.loyalty;

import com.fashionshop.business.entity.Voucher;
import com.fashionshop.common.util.Validatable;
import com.fashionshop.common.util.ValidationUtil;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;

public class CreateVoucherDTO implements Validatable {
    private String code;
    private Voucher.DiscountType discountType;
    private BigDecimal discountValue;
    private BigDecimal minOrderValue = BigDecimal.ZERO;
    private BigDecimal maxDiscountAmt;
    private Integer totalUsageLimit = 0;
    private Integer userUsageLimit = 1;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Boolean isActive = true;

    @Override
    public Map<String, List<String>> validate() {
        Map<String, List<String>> errors = new LinkedHashMap<>();

        if (code == null || code.isBlank()) {
            ValidationUtil.addError(errors, "code", "Mã voucher không được để trống");
        } else if (code.trim().length() > 100) {
            ValidationUtil.addError(errors, "code", "Mã voucher tối đa 100 ký tự");
        }

        if (discountType == null) {
            ValidationUtil.addError(errors, "discountType", "Loại giảm giá không được để trống");
        }

        if (discountValue == null || discountValue.compareTo(BigDecimal.ZERO) <= 0) {
            ValidationUtil.addError(errors, "discountValue", "Giá trị giảm phải lớn hơn 0");
        } else if (discountType == Voucher.DiscountType.PERCENTAGE && discountValue.compareTo(BigDecimal.valueOf(100)) > 0) {
            ValidationUtil.addError(errors, "discountValue", "Phần trăm giảm phải từ 1 đến 100");
        }

        if (minOrderValue != null && minOrderValue.compareTo(BigDecimal.ZERO) < 0) {
            ValidationUtil.addError(errors, "minOrderValue", "Giá trị đơn hàng tối thiểu không được âm");
        }

        if (maxDiscountAmt != null && maxDiscountAmt.compareTo(BigDecimal.ZERO) <= 0) {
            ValidationUtil.addError(errors, "maxDiscountAmt", "Giảm tối đa phải lớn hơn 0");
        }

        if (totalUsageLimit == null || totalUsageLimit < 0) {
            ValidationUtil.addError(errors, "totalUsageLimit", "Tổng lượt sử dụng không được âm");
        }

        if (userUsageLimit == null || userUsageLimit <= 0) {
            ValidationUtil.addError(errors, "userUsageLimit", "Số lượt mỗi người phải lớn hơn 0");
        }

        if (startDate == null) {
            ValidationUtil.addError(errors, "startDate", "Ngày bắt đầu không được để trống");
        }

        if (endDate == null) {
            ValidationUtil.addError(errors, "endDate", "Ngày kết thúc không được để trống");
        }

        if (startDate != null && endDate != null && !startDate.isBefore(endDate)) {
            ValidationUtil.addError(errors, "endDate", "Ngày kết thúc phải sau ngày bắt đầu");
        }

        return errors;
    }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public Voucher.DiscountType getDiscountType() { return discountType; }
    public void setDiscountType(Voucher.DiscountType discountType) { this.discountType = discountType; }
    public BigDecimal getDiscountValue() { return discountValue; }
    public void setDiscountValue(BigDecimal discountValue) { this.discountValue = discountValue; }
    public BigDecimal getMinOrderValue() { return minOrderValue; }
    public void setMinOrderValue(BigDecimal minOrderValue) { this.minOrderValue = minOrderValue; }
    public BigDecimal getMaxDiscountAmt() { return maxDiscountAmt; }
    public void setMaxDiscountAmt(BigDecimal maxDiscountAmt) { this.maxDiscountAmt = maxDiscountAmt; }
    public Integer getTotalUsageLimit() { return totalUsageLimit; }
    public void setTotalUsageLimit(Integer totalUsageLimit) { this.totalUsageLimit = totalUsageLimit; }
    public Integer getUserUsageLimit() { return userUsageLimit; }
    public void setUserUsageLimit(Integer userUsageLimit) { this.userUsageLimit = userUsageLimit; }
    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }
    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}