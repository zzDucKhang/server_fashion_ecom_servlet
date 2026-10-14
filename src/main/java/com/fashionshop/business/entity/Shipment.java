package com.fashionshop.business.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Entity
@Table(name = "\"Shipment\"")
public class Shipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @JsonIgnore
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

    @Column(name = "\"deletedAt\"")
    private LocalDateTime deletedAt;

    public enum ShipmentStatus { PENDING, PICKED_UP, IN_TRANSIT, DELIVERED, FAILED }

    public Integer getOrderId() {
        return order != null ? order.getId() : null;
    }

    /**
     * Xuất thông tin tóm tắt của Order cho trang Admin Shipments (tránh vòng lặp vô tận với Order.shipment)
     */
    @JsonProperty("order")
    public Map<String, Object> getOrderSummary() {
        if (order == null) return null;
        try {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", order.getId());
            map.put("orderCode", order.getOrderCode());
            map.put("status", order.getStatus() != null ? order.getStatus().name() : null);

            String receiverName = "Khách hàng";
            String receiverPhone = "";
            String addr = order.getShippingAddress();
            if (addr != null && !addr.isBlank()) {
                String[] pipeParts = addr.split("\\|");
                String firstPart = pipeParts[0].trim();
                if (firstPart.contains(" - ")) {
                    String[] dashParts = firstPart.split(" - ", 2);
                    receiverName = dashParts[0].trim();
                    receiverPhone = dashParts[1].trim();
                } else {
                    receiverName = firstPart;
                    if (pipeParts.length > 1) {
                        receiverPhone = pipeParts[1].trim();
                    }
                }
            }
            map.put("receiverName", receiverName);
            map.put("receiverPhone", receiverPhone);
            return map;
        } catch (Exception e) {
            return null;
        }
    }

    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }

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
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public void updateTracking(String code) {
        this.trackingCode = code;
    }

    public void changeStatus(ShipmentStatus newStatus) {
        this.status = newStatus;
    }
}