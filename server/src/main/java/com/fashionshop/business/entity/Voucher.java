package com.fashionshop.business.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "\"Voucher\"")
public class Voucher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 100)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(name = "\"discountType\"", nullable = false)
    private DiscountType discountType;

    @Column(name = "\"discountValue\"", nullable = false, precision = 12, scale = 2)
    private BigDecimal discountValue;

    @Column(name = "\"minOrderValue\"", nullable = false, precision = 12, scale = 2)
    private BigDecimal minOrderValue = BigDecimal.ZERO;

    @Column(name = "\"maxDiscountAmt\"", precision = 12, scale = 2)
    private BigDecimal maxDiscountAmt;

    @Column(name = "\"totalUsageLimit\"", nullable = false)
    private Integer totalUsageLimit = 0;

    @Column(name = "\"usedCount\"", nullable = false)
    private Integer usedCount = 0;

    @Column(name = "\"userUsageLimit\"", nullable = false)
    private Integer userUsageLimit = 1;

    @Column(name = "\"startDate\"", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "\"endDate\"", nullable = false)
    private LocalDateTime endDate;

    @Column(name = "\"isActive\"", nullable = false)
    private Boolean isActive = true;

    @Column(name = "\"deletedAt\"")
    private LocalDateTime deletedAt;

    @Column(name = "\"createdAt\"", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "\"updatedAt\"", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    public enum DiscountType { PERCENTAGE, FIXED_AMOUNT }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public DiscountType getDiscountType() { return discountType; }
    public void setDiscountType(DiscountType discountType) { this.discountType = discountType; }
    public BigDecimal getDiscountValue() { return discountValue; }
    public void setDiscountValue(BigDecimal discountValue) { this.discountValue = discountValue; }
    public BigDecimal getMinOrderValue() { return minOrderValue; }
    public void setMinOrderValue(BigDecimal minOrderValue) { this.minOrderValue = minOrderValue; }
    public BigDecimal getMaxDiscountAmt() { return maxDiscountAmt; }
    public void setMaxDiscountAmt(BigDecimal maxDiscountAmt) { this.maxDiscountAmt = maxDiscountAmt; }
    public Integer getTotalUsageLimit() { return totalUsageLimit; }
    public void setTotalUsageLimit(Integer totalUsageLimit) { this.totalUsageLimit = totalUsageLimit; }
    public Integer getUsedCount() { return usedCount; }
    public void setUsedCount(Integer usedCount) { this.usedCount = usedCount; }
    public Integer getUserUsageLimit() { return userUsageLimit; }
    public void setUserUsageLimit(Integer userUsageLimit) { this.userUsageLimit = userUsageLimit; }
    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }
    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public boolean isExpired() {
        return this.endDate != null && LocalDateTime.now().isAfter(this.endDate);
    }

    public boolean isValid(BigDecimal orderAmt) {
        if (Boolean.FALSE.equals(this.isActive) || isExpired()) {
            return false;
        }
        if (this.totalUsageLimit != null && this.totalUsageLimit > 0 && this.usedCount >= this.totalUsageLimit) {
            return false;
        }
        return orderAmt != null && orderAmt.compareTo(this.minOrderValue) >= 0;
    }

    public BigDecimal calculateDiscount(BigDecimal amt) {
        if (!isValid(amt)) {
            return BigDecimal.ZERO;
        }
        BigDecimal discount = BigDecimal.ZERO;
        if (this.discountType == DiscountType.PERCENTAGE) {
            discount = amt.multiply(this.discountValue).divide(BigDecimal.valueOf(100));
            if (this.maxDiscountAmt != null && discount.compareTo(this.maxDiscountAmt) > 0) {
                discount = this.maxDiscountAmt;
            }
        } else if (this.discountType == DiscountType.FIXED_AMOUNT) {
            discount = this.discountValue.min(amt);
        }
        return discount;
    }

    public void incrementUsage() {
        this.usedCount = (this.usedCount == null ? 0 : this.usedCount) + 1;
    }
}