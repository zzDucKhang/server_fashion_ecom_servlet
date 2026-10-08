package com.fashionshop.business.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "\"Product\"")
public class Product extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "\"defaultName\"", nullable = false, length = 255)
    private String defaultName;

    @Column(name = "\"thumbnailUrl\"", length = 1000)
    private String thumbnailUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus status = ProductStatus.DRAFT;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"categoryId\"", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"brandId\"")
    private Brand brand;

    @ManyToMany
    @JoinTable(
            name = "\"_ProductTags\"",
            joinColumns = @JoinColumn(name = "\"A\""),
            inverseJoinColumns = @JoinColumn(name = "\"B\"")
    )
    private Set<Tag> tags = new HashSet<>();

    public enum ProductStatus { DRAFT, PUBLISHED, OUT_OF_STOCK, ARCHIVED }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getDefaultName() { return defaultName; }
    public void setDefaultName(String defaultName) { this.defaultName = defaultName; }
    public String getThumbnailUrl() { return thumbnailUrl; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }
    public ProductStatus getStatus() { return status; }
    public void setStatus(ProductStatus status) { this.status = status; }
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
    public Brand getBrand() { return brand; }
    public void setBrand(Brand brand) { this.brand = brand; }
    public Set<Tag> getTags() { return tags; }
    public void setTags(Set<Tag> tags) { this.tags = tags; }

    // Cần thêm danh sách images và variants vào Product.java nếu chưa có
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductVariant> variants = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductImage> images = new ArrayList<>();

    // Business Methods
    public void updateStatus(ProductStatus newStatus) {
        this.status = newStatus;
    }

    public void assignBrand(Brand brand) {
        this.brand = brand;
    }

    public void changeCategory(Category category) {
        this.category = category;
    }

    public void addVariant(ProductVariant variant) {
        this.variants.add(variant);
        variant.setProduct(this);
    }

    public void addImage(String imageUrl, int displayOrder) {
        ProductImage img = new ProductImage();
        img.setImageUrl(imageUrl);
        img.setDisplayOrder(displayOrder);
        img.setProduct(this);
        this.images.add(img);
    }
}