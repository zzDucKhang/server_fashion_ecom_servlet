package com.fashionshop.dto.address;

import com.fashionshop.business.entity.ShippingAddress;
import java.time.LocalDateTime;

/**
 * Data Transfer Object trả về thông tin địa chỉ giao hàng an toàn, chuẩn REST API.
 */
public class ShippingAddressResponse {

    private Integer id;
    private String receiverName;
    private String phone;
    private String street;
    private String ward;
    private String district;
    private String city;
    private String fullAddress;
    private Boolean isDefault;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ShippingAddressResponse() {
    }

    public static ShippingAddressResponse fromEntity(ShippingAddress addr) {
        if (addr == null) {
            return null;
        }

        ShippingAddressResponse res = new ShippingAddressResponse();
        res.setId(addr.getId());
        res.setReceiverName(addr.getReceiverName());
        res.setPhone(addr.getPhoneNumber());
        res.setStreet(addr.getDetailAddress());
        res.setWard(addr.getWard());
        res.setDistrict(addr.getDistrict());
        res.setCity(addr.getProvince());
        res.setFullAddress(addr.getFullAddress());
        res.setIsDefault(addr.getIsDefault());
        res.setCreatedAt(addr.getCreatedAt());
        res.setUpdatedAt(addr.getUpdatedAt());
        return res;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
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
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
