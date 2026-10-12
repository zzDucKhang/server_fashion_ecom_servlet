package com.fashionshop.presentation.dto.cartorder;

import java.util.List;

public class CreateOrderDTO {
    private List<Integer> cartItemIds;
    private DirectBuyDTO directBuy;
    private Integer shippingAddressId;
    private ShippingAddressDTO shippingAddress;
    private String paymentMethod; // COD, VNPAY, MOMO, BANK_TRANSFER
    private String shippingMethod = "STANDARD";
    private String customerNote;
    private String voucherCode;
    private Integer pointUsed = 0;

    public static class DirectBuyDTO {
        private Integer variantId;
        private Integer quantity;

        public Integer getVariantId() { return variantId; }
        public void setVariantId(Integer variantId) { this.variantId = variantId; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }

    // Getters and Setters
    public List<Integer> getCartItemIds() { return cartItemIds; }
    public void setCartItemIds(List<Integer> cartItemIds) { this.cartItemIds = cartItemIds; }
    public DirectBuyDTO getDirectBuy() { return directBuy; }
    public void setDirectBuy(DirectBuyDTO directBuy) { this.directBuy = directBuy; }
    public Integer getShippingAddressId() { return shippingAddressId; }
    public void setShippingAddressId(Integer shippingAddressId) { this.shippingAddressId = shippingAddressId; }
    public ShippingAddressDTO getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(ShippingAddressDTO shippingAddress) { this.shippingAddress = shippingAddress; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public String getShippingMethod() { return shippingMethod; }
    public void setShippingMethod(String shippingMethod) { this.shippingMethod = shippingMethod; }
    public String getCustomerNote() { return customerNote; }
    public void setCustomerNote(String customerNote) { this.customerNote = customerNote; }
    public String getVoucherCode() { return voucherCode; }
    public void setVoucherCode(String voucherCode) { this.voucherCode = voucherCode; }
    public Integer getPointUsed() { return pointUsed; }
    public void setPointUsed(Integer pointUsed) { this.pointUsed = pointUsed; }
}