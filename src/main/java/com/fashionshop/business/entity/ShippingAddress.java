package com.fashionshop.business.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "\"ShippingAddress\"")
public class ShippingAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"userId\"", nullable = false)
    private User user;

    @Column(name = "\"receiverName\"", nullable = false, length = 255)
    private String receiverName;

    @Column(name = "\"phoneNumber\"", nullable = false, length = 50)
    private String phoneNumber;

    @Column(nullable = false, length = 150)
    private String province;

    @Column(nullable = false, length = 150)
    private String district;

    @Column(nullable = false, length = 150)
    private String ward;

    @Column(name = "\"detailAddress\"", nullable = false, length = 500)
    private String detailAddress;

    @Column(name = "\"isDefault\"", nullable = false)
    private Boolean isDefault = false;

    @Column(name = "\"createdAt\"", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "\"updatedAt\"", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    // Business Methods theo Diagram
    public String getFullAddress() {
        return String.format("%s, %s, %s, %s",
                detailAddress != null ? detailAddress : "",
                ward != null ? ward : "",
                district != null ? district : "",
                province != null ? province : "");
    }

    public void updateAddress(String receiverName, String phone, String province, String district, String ward, String detail) {
        this.receiverName = receiverName;
        this.phoneNumber = phone;
        this.province = province;
        this.district = district;
        this.ward = ward;
        this.detailAddress = detail;
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getReceiverName() { return receiverName; }
    public void setReceiverName(String receiverName) { this.receiverName = receiverName; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getProvince() { return province; }
    public void setProvince(String province) { this.province = province; }
    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }
    public String getWard() { return ward; }
    public void setWard(String ward) { this.ward = ward; }
    public String getDetailAddress() { return detailAddress; }
    public void setDetailAddress(String detailAddress) { this.detailAddress = detailAddress; }
    public Boolean getIsDefault() { return isDefault; }
    public void setIsDefault(Boolean isDefault) { this.isDefault = isDefault; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}