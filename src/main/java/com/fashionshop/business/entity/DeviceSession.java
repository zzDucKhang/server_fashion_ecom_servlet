package com.fashionshop.business.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "\"DeviceSession\"")
public class DeviceSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"userId\"", nullable = false)
    private User user;

    @Column(name = "\"refreshToken\"", nullable = false, unique = true, length = 1000)
    private String refreshToken;

    @Column(name = "\"ipAddress\"", nullable = false, length = 100)
    private String ipAddress;

    @Column(name = "\"isActive\"", nullable = false)
    private Boolean isActive = true;

    @Column(name = "\"expiresAt\"", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "\"createdAt\"", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "\"updatedAt\"", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public void revokeSession() {
        this.isActive = false;
    }

    public boolean isTokenExpired() {
        return this.expiresAt != null && LocalDateTime.now().isAfter(this.expiresAt);
    }

    public void refresh(String newToken, LocalDateTime newExpiresAt) {
        this.refreshToken = newToken;
        this.expiresAt = newExpiresAt;
        this.isActive = true;
        this.updatedAt = LocalDateTime.now();
    }
}