package com.fashionshop.presentation.dto.inventory;

import java.math.BigDecimal;

public class FlashSaleItemDTO {
    private Integer variantId;
    private BigDecimal flashSalePrice;
    private Integer promoQuantity;

    public Integer getVariantId() { return variantId; }
    public void setVariantId(Integer variantId) { this.variantId = variantId; }
    public BigDecimal getFlashSalePrice() { return flashSalePrice; }
    public void setFlashSalePrice(BigDecimal flashSalePrice) { this.flashSalePrice = flashSalePrice; }
    public Integer getPromoQuantity() { return promoQuantity; }
    public void setPromoQuantity(Integer promoQuantity) { this.promoQuantity = promoQuantity; }
}