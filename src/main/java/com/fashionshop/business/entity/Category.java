package com.fashionshop.business.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "\"Category\"")
public class Category extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "\"defaultName\"", nullable = false, length = 255)
    private String defaultName;

    @Column(name = "\"iconUrl\"", length = 1000)
    private String iconUrl;

    @Column(name = "\"isVisible\"", nullable = false)
    private Boolean isVisible = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"parentId\"")
    private Category parent;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getDefaultName() { return defaultName; }
    public void setDefaultName(String defaultName) { this.defaultName = defaultName; }
    public String getIconUrl() { return iconUrl; }
    public void setIconUrl(String iconUrl) { this.iconUrl = iconUrl; }
    public Boolean getIsVisible() { return isVisible; }
    public void setIsVisible(Boolean isVisible) { this.isVisible = isVisible; }
    public Category getParent() { return parent; }
    public void setParent(Category parent) { this.parent = parent; }

    public void toggleVisibility() {
        this.isVisible = (this.isVisible == null) ? Boolean.FALSE : !this.isVisible;
    }

    public boolean isRootCategory() {
        return this.parent == null;
    }

    public void changeParent(Category newParent) {
        if (newParent != null && newParent.getId().equals(this.getId())) {
            throw new IllegalArgumentException("Một danh mục không thể làm cha của chính nó");
        }
        this.parent = newParent;
    }
}