package com.fashionshop.business.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "\"PointTransaction\"")
public class PointTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"userId\"", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"orderId\"")
    private Order order;

    @Column(name = "\"pointChange\"", nullable = false)
    private Integer pointChange;

    @Column(name = "\"currentPoint\"", nullable = false)
    private Integer currentPoint;

    @Enumerated(EnumType.STRING)
    @Column(name = "\"transactionType\"", nullable = false)
    private PointTransType transactionType;

    @Column(nullable = false, length = 255)
    private String description;

    @Column(name = "\"createdAt\"", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum PointTransType { EARN, REDEEM, REFUND, EXPIRE }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }
    public Integer getPointChange() { return pointChange; }
    public void setPointChange(Integer pointChange) { this.pointChange = pointChange; }
    public Integer getCurrentPoint() { return currentPoint; }
    public void setCurrentPoint(Integer currentPoint) { this.currentPoint = currentPoint; }
    public PointTransType getTransactionType() { return transactionType; }
    public void setTransactionType(PointTransType transactionType) { this.transactionType = transactionType; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public void recordChange(int delta, String desc) {
        this.pointChange = delta;
        this.description = desc;
    }

    public int getBalanceSnapshot() {
        return this.currentPoint != null ? this.currentPoint : 0;
    }
}