package com.fashionshop.business.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "\"User\"")
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "\"fullName\"", nullable = false, length = 255)
    private String fullName;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "\"phoneNumber\"", nullable = false, length = 50)
    private String phoneNumber;

    @Column(name = "\"passwordHash\"", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "\"avatarUrl\"", length = 1000)
    private String avatarUrl;

    @Column(name = "\"rewardPoints\"", nullable = false)
    private Integer rewardPoints = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status = UserStatus.ACTIVE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"roleId\"", nullable = false)
    private Role role;

    public enum UserStatus { ACTIVE, INACTIVE, BLOCKED }

    // Business Methods theo Diagram
    public void changePassword(String newHash) {
        this.passwordHash = newHash;
    }

    public void addRewardPoints(int points) {
        if (points > 0) {
            this.rewardPoints += points;
        }
    }

    public boolean deductRewardPoints(int points) {
        if (points > 0 && this.rewardPoints >= points) {
            this.rewardPoints -= points;
            return true;
        }
        return false;
    }

    public void updateStatus(UserStatus newStatus) {
        this.status = newStatus;
    }

    public void updateProfile(String fullName, String phoneNumber, String avatarUrl) {
        if (fullName != null) this.fullName = fullName;
        if (phoneNumber != null) this.phoneNumber = phoneNumber;
        if (avatarUrl != null) this.avatarUrl = avatarUrl;
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    public Integer getRewardPoints() { return rewardPoints; }
    public void setRewardPoints(Integer rewardPoints) { this.rewardPoints = rewardPoints; }
    public UserStatus getStatus() { return status; }
    public void setStatus(UserStatus status) { this.status = status; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
}