package com.fashionshop.dto.auth;

import com.fashionshop.business.entity.User;
import com.fashionshop.business.entity.enums.UserStatus;

import java.time.LocalDateTime;

/**
 * Data Transfer Object trả về thông tin User an toàn (tuyệt đối không lộ passwordHash).
 */
public class UserResponse {

    private Integer id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String avatarUrl;
    private UserStatus status;
    private String role;
    private Integer rewardPoints;
    private LocalDateTime createdAt;

    public UserResponse() {
    }

    public static UserResponse fromEntity(User user) {
        if (user == null) {
            return null;
        }
        UserResponse dto = new UserResponse();
        dto.setId(user.getId());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setAvatarUrl(user.getAvatarUrl());
        dto.setStatus(user.getStatus());
        if (user.getRole() != null) {
            dto.setRole(user.getRole().getCode());
        }
        dto.setRewardPoints(user.getRewardPoints());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Integer getRewardPoints() {
        return rewardPoints;
    }

    public void setRewardPoints(Integer rewardPoints) {
        this.rewardPoints = rewardPoints;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
