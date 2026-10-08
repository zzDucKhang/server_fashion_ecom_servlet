package com.fashionshop.business.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "\"CartItem\"", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"\"cartId\"", "\"variantId\""})
})
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"cartId\"", nullable = false)
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"variantId\"", nullable = false)
    private ProductVariant variant;

    @Column(nullable = false)
    private Integer quantity = 1;

    @Column(name = "\"createdAt\"", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "\"updatedAt\"", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Cart getCart() { return cart; }
    public void setCart(Cart cart) { this.cart = cart; }
    public ProductVariant getVariant() { return variant; }
    public void setVariant(ProductVariant variant) { this.variant = variant; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public void updateQuantity(int newQty) {
        if (newQty <= 0) {
            throw new IllegalArgumentException("Số lượng phải lớn hơn 0");
        }
        this.quantity = newQty;
    }

    public BigDecimal getSubtotal() {
        if (this.variant != null && this.variant.getSellingPrice() != null) {
            return this.variant.getSellingPrice().multiply(BigDecimal.valueOf(this.quantity));
        }
        return BigDecimal.ZERO;
    }
}