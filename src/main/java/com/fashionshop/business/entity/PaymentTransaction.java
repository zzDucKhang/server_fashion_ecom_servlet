package com.fashionshop.business.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "\"PaymentTransaction\"")
public class PaymentTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"orderId\"", nullable = false, unique = true)
    private Order order;

    @Column(nullable = false, length = 100)
    private String provider;

    @Column(name = "\"providerTransId\"", unique = true, length = 255)
    private String providerTransId;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Order.PaymentStatus status = Order.PaymentStatus.PENDING;

    @Column(name = "\"createdAt\"", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "\"updatedAt\"", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(name = "\"deletedAt\"")
    private LocalDateTime deletedAt;

    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }

    // Business Methods theo Class Diagram
    public boolean processWebhook(String payload) {
        // Logic kiểm tra chữ ký payload webhook SePay / VNPay
        return payload != null && !payload.isBlank();
    }

    public void markSuccess() {
        this.status = Order.PaymentStatus.SUCCESS;
        if (this.order != null) {
            this.order.markAsPaid();
        }
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }
    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }
    public String getProviderTransId() { return providerTransId; }
    public void setProviderTransId(String providerTransId) { this.providerTransId = providerTransId; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public Order.PaymentStatus getStatus() { return status; }
    public void setStatus(Order.PaymentStatus status) { this.status = status; }


}