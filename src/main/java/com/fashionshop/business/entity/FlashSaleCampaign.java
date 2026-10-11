package com.fashionshop.business.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "\"FlashSaleCampaign\"")
public class FlashSaleCampaign {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(name = "\"bannerUrl\"", length = 1000)
    private String bannerUrl;

    @Column(name = "\"startTime\"", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "\"endTime\"", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "\"isActive\"", nullable = false)
    private Boolean isActive = true;

    @Column(name = "\"deletedAt\"")
    private LocalDateTime deletedAt;

    @Column(name = "\"createdAt\"", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "\"updatedAt\"", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @OneToMany(mappedBy = "campaign", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FlashSaleItem> items = new ArrayList<>();

    public List<FlashSaleItem> getItems() { return items; }
    public void setItems(List<FlashSaleItem> items) { this.items = items; }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getBannerUrl() { return bannerUrl; }
    public void setBannerUrl(String bannerUrl) { this.bannerUrl = bannerUrl; }
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public boolean isRunningNow() {
        LocalDateTime now = LocalDateTime.now();
        return Boolean.TRUE.equals(this.isActive)
                && now.isAfter(this.startTime)
                && now.isBefore(this.endTime);
    }

    public void endCampaign() {
        this.isActive = false;
        this.endTime = LocalDateTime.now();
    }
}