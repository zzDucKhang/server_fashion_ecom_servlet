package com.fashionshop.dto.address;

/**
 * Data Transfer Object tiếp nhận yêu cầu thêm mới hoặc cập nhật địa chỉ giao hàng.
 * Hỗ trợ linh hoạt cả hai chuẩn đặt tên: (phone / phoneNumber), (street / detailAddress), (city / province).
 */
public class ShippingAddressRequest {

    private String receiverName;
    private String phone;
    private String phoneNumber;
    private String street;
    private String detailAddress;
    private String ward;
    private String district;
    private String city;
    private String province;
    private Boolean isDefault;

    public ShippingAddressRequest() {
    }

    public ShippingAddressRequest(String receiverName, String phone, String street, String ward, String district, String city, Boolean isDefault) {
        this.receiverName = receiverName;
        this.phone = phone;
        this.street = street;
        this.ward = ward;
        this.district = district;
        this.city = city;
        this.isDefault = isDefault;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getPhone() {
        if (phone != null && !phone.isBlank()) {
            return phone;
        }
        return phoneNumber;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhoneNumber() {
        return getPhone();
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getStreet() {
        if (street != null && !street.isBlank()) {
            return street;
        }
        return detailAddress;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getDetailAddress() {
        return getStreet();
    }

    public void setDetailAddress(String detailAddress) {
        this.detailAddress = detailAddress;
    }

    public String getWard() {
        return ward;
    }

    public void setWard(String ward) {
        this.ward = ward;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getCity() {
        if (city != null && !city.isBlank()) {
            return city;
        }
        return province;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return getCity();
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }
}
