package com.fashionshop.presentation.dto.cartorder;

public class AddCartItemDTO {
    private Integer variantId;
    private Integer quantity;

    public Integer getVariantId() { return variantId; }
    public void setVariantId(Integer variantId) { this.variantId = variantId; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}