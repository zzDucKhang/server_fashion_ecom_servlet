package com.fashionshop.business.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Thực thể DeviceSession quản lý phiên đăng nhập thiết bị và Refresh Token.
 * Ánh xạ tới bảng 'device_sessions' trong cơ sở dữ liệu.
 */
@Entity
@Table(
    name = "device_sessions",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_device_session_refresh_token", columnNames = "refresh_token")
    }
)
public class DeviceSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "refresh_token", unique = true, nullable = false, length = 512)
    private String refreshToken;

    @Column(name = "device_ip", length = 100)
    private String deviceIp;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "is_revoked", nullable = false)
    private boolean isRevoked = false;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    public DeviceSession() {
    }

    public DeviceSession(User user, String refreshToken, String deviceIp, String userAgent, LocalDateTime expiresAt) {
        this.user = user;
        this.refreshToken = refreshToken;
        this.deviceIp = deviceIp;
        this.userAgent = userAgent;
        this.expiresAt = expiresAt;
        this.isRevoked = false;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public DeviceSession(Integer id, User user, String refreshToken, String deviceIp, String userAgent, LocalDateTime expiresAt, boolean isRevoked, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.user = user;
        this.refreshToken = refreshToken;
        this.deviceIp = deviceIp;
        this.userAgent = userAgent;
        this.expiresAt = expiresAt;
        this.isRevoked = isRevoked;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Business Methods
    public void revokeSession() {
        this.isRevoked = true;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isTokenExpired() {
        return this.expiresAt != null && LocalDateTime.now().isAfter(this.expiresAt);
    }

    public void refresh(String newToken, LocalDateTime newExpiresAt) {
        this.refreshToken = newToken;
        this.expiresAt = newExpiresAt;
        this.isRevoked = false;
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getDeviceIp() {
        return deviceIp;
    }

    public void setDeviceIp(String deviceIp) {
        this.deviceIp = deviceIp;
    }

    // Alias tương thích ngược cho ipAddress
    public String getIpAddress() {
        return this.deviceIp;
    }

    public void setIpAddress(String ipAddress) {
        this.deviceIp = ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public boolean isRevoked() {
        return isRevoked;
    }

    public void setRevoked(boolean isRevoked) {
        this.isRevoked = isRevoked;
    }

    // Alias tương thích ngược cho isActive
    public Boolean getIsActive() {
        return !this.isRevoked;
    }

    public void setIsActive(Boolean isActive) {
        this.isRevoked = (isActive == null || !isActive);
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