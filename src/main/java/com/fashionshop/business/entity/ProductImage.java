package com.fashionshop.business.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "\"ProductImage\"")
public class ProductImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"productId\"", nullable = false)
    private Product product;

    @Column(name = "\"imageUrl\"", nullable = false, length = 1000)
    private String imageUrl;

    @Column(name = "\"displayOrder\"", nullable = false)
    private Integer displayOrder = 0;

    @Column(name = "\"createdAt\"", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }

    public void reOrder(int newOrder) {
        this.displayOrder = newOrder;
    }

    public void updateUrl(String newUrl) {
        if (newUrl != null && !newUrl.isBlank()) {
            this.imageUrl = newUrl;
        }
    }
}