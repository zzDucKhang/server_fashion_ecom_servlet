package com.fashionshop.business.entity;

import com.fashionshop.business.entity.enums.UserStatus;
import jakarta.persistence.*;

/**
 * Thực thể User đại diện cho người dùng hệ thống trong module Identity & Security.
 * Ánh xạ tới bảng 'users' trong cơ sở dữ liệu.
 */
@Entity
@Table(
    name = "users",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_user_email", columnNames = "email")
    }
)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "full_name", nullable = false, length = 255)
    private String fullName;

    @Column(name = "email", unique = true, nullable = false, length = 255)
    private String email;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "phone", nullable = false, length = 50)
    private String phone;

    @Column(name = "avatar_url", length = 1000)
    private String avatarUrl;

    @Column(name = "reward_points", nullable = false)
    private Integer rewardPoints = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private UserStatus status = UserStatus.ACTIVE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    // Constructors chuẩn JPA
    public User() {
    }

    public User(String fullName, String email, String phone, String password, Role role) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.role = role;
        this.rewardPoints = 0;
        this.status = UserStatus.ACTIVE;
    }

    public User(Integer id, String fullName, String email, String phone, String password, String avatarUrl, Integer rewardPoints, UserStatus status, Role role) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.avatarUrl = avatarUrl;
        this.rewardPoints = rewardPoints;
        this.status = status;
        this.role = role;
    }

    // Business Methods theo Diagram
    public void changePassword(String newHash) {
        this.password = newHash;
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

    public void updateProfile(String fullName, String phone, String avatarUrl) {
        if (fullName != null) this.fullName = fullName;
        if (phone != null) this.phone = phone;
        if (avatarUrl != null) this.avatarUrl = avatarUrl;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Alias tương thích ngược cho passwordHash
    public String getPasswordHash() {
        return this.password;
    }

    public void setPasswordHash(String passwordHash) {
        this.password = passwordHash;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    // Alias tương thích ngược cho phoneNumber
    public String getPhoneNumber() {
        return this.phone;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phone = phoneNumber;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public Integer getRewardPoints() {
        return rewardPoints;
    }

    public void setRewardPoints(Integer rewardPoints) {
        this.rewardPoints = rewardPoints;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}