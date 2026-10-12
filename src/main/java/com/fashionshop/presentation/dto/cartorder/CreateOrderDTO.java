package com.fashionshop.presentation.dto.cartorder;

import com.fashionshop.common.util.Validatable;
import com.fashionshop.common.util.ValidationUtil;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CreateOrderDTO implements Validatable {
    private List<Integer> cartItemIds;
    private DirectBuyDTO directBuy;
    private Integer shippingAddressId;
    private ShippingAddressDTO shippingAddress;
    private String paymentMethod;
    private String shippingMethod = "STANDARD";
    private String customerNote;
    private String voucherCode;
    private Integer pointUsed = 0;

    @Override
    public Map<String, List<String>> validate() {
        Map<String, List<String>> errors = new LinkedHashMap<>();

        if (shippingAddressId == null && shippingAddress == null) {
            ValidationUtil.addError(errors, "shippingAddress", "Vui lòng chọn địa chỉ có sẵn hoặc nhập địa chỉ giao hàng mới");
        }

        if (shippingAddress != null && shippingAddressId == null) {
            if (shippingAddress.getReceiverName() == null || shippingAddress.getReceiverName().trim().isEmpty()) {
                ValidationUtil.addError(errors, "shippingAddress.receiverName", "Vui lòng nhập họ tên người nhận");
            }
            if (shippingAddress.getPhoneNumber() == null || !ValidationUtil.isValidPhone(shippingAddress.getPhoneNumber())) {
                ValidationUtil.addError(errors, "shippingAddress.phoneNumber", "Số điện thoại không đúng định dạng Việt Nam");
            }
            if (shippingAddress.getDetailAddress() == null || shippingAddress.getDetailAddress().trim().isEmpty()) {
                ValidationUtil.addError(errors, "shippingAddress.detailAddress", "Vui lòng nhập địa chỉ giao hàng");
            }
        }

        if (paymentMethod == null || (!paymentMethod.equals("COD") && !paymentMethod.equals("BANK_TRANSFER") && !paymentMethod.equals("MOMO") && !paymentMethod.equals("VNPAY"))) {
            ValidationUtil.addError(errors, "paymentMethod", "Phương thức thanh toán không hợp lệ");
        }

        if (pointUsed != null && pointUsed < 0) {
            ValidationUtil.addError(errors, "pointUsed", "Số điểm sử dụng không được nhỏ hơn 0");
        }

        return errors;
    }

    // Giữ nguyên class DirectBuyDTO và toàn bộ Getter/Setter của bạn...
    public static class DirectBuyDTO {
        private Integer variantId;
        private Integer quantity;
        public Integer getVariantId() { return variantId; }
        public void setVariantId(Integer variantId) { this.variantId = variantId; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }
    
    // (Giữ nguyên phần Getter/Setter gốc)
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