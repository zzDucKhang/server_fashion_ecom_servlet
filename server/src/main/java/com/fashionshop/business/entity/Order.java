package com.fashionshop.business.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "\"Order\"")
public class Order extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "\"orderCode\"", nullable = false, unique = true, length = 100)
    private String orderCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"userId\"", nullable = false)
    private User user;

    @Column(name = "\"shippingAddress\"", nullable = false, length = 500)
    private String shippingAddress;

    @Column(name = "\"subTotal\"", nullable = false, precision = 12, scale = 2)
    private BigDecimal subTotal = BigDecimal.ZERO;

    @Column(name = "\"shippingFee\"", nullable = false, precision = 12, scale = 2)
    private BigDecimal shippingFee = BigDecimal.ZERO;

    @Column(name = "\"shippingMethod\"", nullable = false, length = 50)
    private String shippingMethod = "STANDARD";

    @Column(name = "\"shippingDiscount\"", nullable = false, precision = 12, scale = 2)
    private BigDecimal shippingDiscount = BigDecimal.ZERO;

    @Column(name = "\"voucherCode\"", length = 100)
    private String voucherCode;

    @Column(name = "\"voucherDiscount\"", nullable = false, precision = 12, scale = 2)
    private BigDecimal voucherDiscount = BigDecimal.ZERO;

    @Column(name = "\"pointUsed\"", nullable = false)
    private Integer pointUsed = 0;

    @Column(name = "\"pointDiscount\"", nullable = false, precision = 12, scale = 2)
    private BigDecimal pointDiscount = BigDecimal.ZERO;

    @Column(name = "\"pointEarned\"", nullable = false)
    private Integer pointEarned = 0;

    @Column(name = "\"totalAmount\"", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "\"paymentMethod\"", nullable = false)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "\"paymentStatus\"", nullable = false)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.PENDING_PAYMENT;

    @Column(name = "\"customerNote\"", columnDefinition = "TEXT")
    private String customerNote;

    @Column(name = "\"orderDate\"", nullable = false)
    private LocalDateTime orderDate = LocalDateTime.now();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private Shipment shipment;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private PaymentTransaction paymentTransaction;

    public enum PaymentMethod { COD, VNPAY, MOMO, BANK_TRANSFER }
    public enum PaymentStatus { PENDING, SUCCESS, FAILED, REFUNDED }
    public enum OrderStatus { PENDING_PAYMENT, CONFIRMED, PROCESSING, SHIPPED, DELIVERED, CANCELLED, RETURNED }

    public void markAsPaid() {
        this.paymentStatus = PaymentStatus.SUCCESS;
        this.status = OrderStatus.CONFIRMED;
    }

    public void updateStatus(OrderStatus newStatus) {
        this.status = newStatus;
    }

    public void cancelOrder() {
        if (this.status == OrderStatus.PENDING_PAYMENT || this.status == OrderStatus.CONFIRMED || this.status == OrderStatus.PROCESSING) {
            this.status = OrderStatus.CANCELLED;
        }
    }

    
    public Integer getUserId() {
        return user != null ? user.getId() : null;
    }

    public String getReceiverName() {
        if (shippingAddress != null && !shippingAddress.isBlank()) {
            String firstPart = shippingAddress.split("\\|")[0].trim();
            if (firstPart.contains(" - ")) {
                return firstPart.split(" - ", 2)[0].trim();
            }
            return firstPart;
        }
        return user != null ? user.getFullName() : "Khách hàng";
    }

    public String getReceiverPhone() {
        if (shippingAddress != null && !shippingAddress.isBlank()) {
            String[] pipeParts = shippingAddress.split("\\|");
            String firstPart = pipeParts[0].trim();
            if (firstPart.contains(" - ")) {
                return firstPart.split(" - ", 2)[1].trim();
            }
            if (pipeParts.length > 1) {
                return pipeParts[1].trim();
            }
        }
        return user != null ? user.getPhoneNumber() : "";
    }

    public List<PaymentTransaction> getPaymentTransactions() {
        return paymentTransaction != null ? List.of(paymentTransaction) : Collections.emptyList();
    }

    public List<Object> getPointTransactions() {
        return Collections.emptyList();
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getOrderCode() { return orderCode; }
    public void setOrderCode(String orderCode) { this.orderCode = orderCode; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }
    public BigDecimal getSubTotal() { return subTotal; }
    public void setSubTotal(BigDecimal subTotal) { this.subTotal = subTotal; }
    public BigDecimal getShippingFee() { return shippingFee; }
    public void setShippingFee(BigDecimal shippingFee) { this.shippingFee = shippingFee; }
    public String getShippingMethod() { return shippingMethod; }
    public void setShippingMethod(String shippingMethod) { this.shippingMethod = shippingMethod; }
    public BigDecimal getShippingDiscount() { return shippingDiscount; }
    public void setShippingDiscount(BigDecimal shippingDiscount) { this.shippingDiscount = shippingDiscount; }
    public String getVoucherCode() { return voucherCode; }
    public void setVoucherCode(String voucherCode) { this.voucherCode = voucherCode; }
    public BigDecimal getVoucherDiscount() { return voucherDiscount; }
    public void setVoucherDiscount(BigDecimal voucherDiscount) { this.voucherDiscount = voucherDiscount; }
    public Integer getPointUsed() { return pointUsed; }
    public void setPointUsed(Integer pointUsed) { this.pointUsed = pointUsed; }
    public BigDecimal getPointDiscount() { return pointDiscount; }
    public void setPointDiscount(BigDecimal pointDiscount) { this.pointDiscount = pointDiscount; }
    public Integer getPointEarned() { return pointEarned; }
    public void setPointEarned(Integer pointEarned) { this.pointEarned = pointEarned; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }
    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; }
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
    public String getCustomerNote() { return customerNote; }
    public void setCustomerNote(String customerNote) { this.customerNote = customerNote; }
    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }
    public List<OrderItem> getOrderItems() { return orderItems; }
    public void setOrderItems(List<OrderItem> orderItems) { this.orderItems = orderItems; }
    public Shipment getShipment() { return shipment; }
    public void setShipment(Shipment shipment) { this.shipment = shipment; }
    public PaymentTransaction getPaymentTransaction() { return paymentTransaction; }
    public void setPaymentTransaction(PaymentTransaction paymentTransaction) { this.paymentTransaction = paymentTransaction; }
}