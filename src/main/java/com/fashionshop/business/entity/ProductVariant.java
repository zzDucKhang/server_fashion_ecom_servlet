package com.fashionshop.business.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "\"ProductVariant\"")
public class ProductVariant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @JsonIgnoreProperties({"variants", "images", "tags", "hibernateLazyInitializer", "handler"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"productId\"", nullable = false)
    private Product product;

    @Column(nullable = false, unique = true, length = 150)
    private String sku;

    @Column(name = "\"originalPrice\"", nullable = false, precision = 12, scale = 2)
    private BigDecimal originalPrice;

    @Column(name = "\"sellingPrice\"", nullable = false, precision = 12, scale = 2)
    private BigDecimal sellingPrice;

    @Column(name = "\"weightGram\"", nullable = false)
    private Integer weightGram = 0;

    @Column(name = "\"imageUrl\"", length = 1000)
    private String imageUrl;

    @Column(name = "\"variant\"", length = 100)
    private String variant;

    @Column(name = "\"variantValue\"", length = 100)
    private String variantValue;

    @Column(name = "\"stockQuantity\"", nullable = false)
    private Integer stockQuantity = 0;

    @JsonIgnoreProperties({"variant", "hibernateLazyInitializer", "handler"})
    @OneToMany(mappedBy = "variant", fetch = FetchType.EAGER)
    private List<FlashSaleItem> flashSaleItems = new ArrayList<>();

    @Column(name = "\"deletedAt\"")
    private LocalDateTime deletedAt;

    @Column(name = "\"createdAt\"", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Version
    @Column(name = "\"updatedAt\"", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    public Integer getProductId() {
        return product != null ? product.getId() : null;
    }

    public void updateStock(int quantityChange) {
        this.stockQuantity += quantityChange;
    }

    public void updatePrice(BigDecimal newPrice) {
        this.sellingPrice = newPrice;
    }

    public boolean isStockAvailable(int reqQty) {
        return this.stockQuantity >= reqQty;
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    public BigDecimal getOriginalPrice() { return originalPrice; }
    public void setOriginalPrice(BigDecimal originalPrice) { this.originalPrice = originalPrice; }
    public BigDecimal getSellingPrice() { return sellingPrice; }
    public void setSellingPrice(BigDecimal sellingPrice) { this.sellingPrice = sellingPrice; }
    public Integer getWeightGram() { return weightGram; }
    public void setWeightGram(Integer weightGram) { this.weightGram = weightGram; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getVariant() { return variant; }
    public void setVariant(String variant) { this.variant = variant; }
    public String getVariantValue() { return variantValue; }
    public void setVariantValue(String variantValue) { this.variantValue = variantValue; }
    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }
    public List<FlashSaleItem> getFlashSaleItems() { return flashSaleItems; }
    public void setFlashSaleItems(List<FlashSaleItem> flashSaleItems) { this.flashSaleItems = flashSaleItems; }
    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}