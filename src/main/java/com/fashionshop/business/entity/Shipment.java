package com.fashionshop.business.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "\"Shipment\"")
public class Shipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"orderId\"", nullable = false, unique = true)
    private Order order;

    @Column(nullable = false, length = 100)
    private String carrier;

    @Column(name = "\"trackingCode\"", nullable = false, unique = true, length = 150)
    private String trackingCode;

    @Column(name = "\"actualShippingFee\"", nullable = false, precision = 12, scale = 2)
    private BigDecimal actualShippingFee;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShipmentStatus status = ShipmentStatus.PENDING;

    @Column(name = "\"estimatedDelivery\"")
    private LocalDateTime estimatedDelivery;

    @Column(name = "\"createdAt\"", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "\"updatedAt\"", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    public enum ShipmentStatus { PENDING, PICKED_UP, IN_TRANSIT, DELIVERED, FAILED }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }
    public String getCarrier() { return carrier; }
    public void setCarrier(String carrier) { this.carrier = carrier; }
    public String getTrackingCode() { return trackingCode; }
    public void setTrackingCode(String trackingCode) { this.trackingCode = trackingCode; }
    public BigDecimal getActualShippingFee() { return actualShippingFee; }
    public void setActualShippingFee(BigDecimal actualShippingFee) { this.actualShippingFee = actualShippingFee; }
    public ShipmentStatus getStatus() { return status; }
    public void setStatus(ShipmentStatus status) { this.status = status; }
    public LocalDateTime getEstimatedDelivery() { return estimatedDelivery; }
    public void setEstimatedDelivery(LocalDateTime estimatedDelivery) { this.estimatedDelivery = estimatedDelivery; }

    public void updateTracking(String code) {
        this.trackingCode = code;
    }

    public void changeStatus(ShipmentStatus newStatus) {
        this.status = newStatus;
    }
}