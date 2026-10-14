package com.fashionshop.business.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "\"Brand\"")
public class Brand extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "\"defaultName\"", nullable = false, length = 255)
    private String defaultName;

    @Column(name = "\"logoUrl\"", length = 1000)
    private String logoUrl;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getDefaultName() { return defaultName; }
    public void setDefaultName(String defaultName) { this.defaultName = defaultName; }
    public String getLogoUrl() { return logoUrl; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }

    public void updateBrandInfo(String name, String logo) {
        if (name != null && !name.isBlank()) {
            this.defaultName = name;
        }
        if (logo != null && !logo.isBlank()) {
            this.logoUrl = logo;
        }
    }

    public void softDelete() {
        setDeletedAt(LocalDateTime.now());
    }
}