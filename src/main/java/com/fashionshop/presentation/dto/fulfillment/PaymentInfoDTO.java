package com.fashionshop.presentation.dto.fulfillment;

import java.math.BigDecimal;

public class PaymentInfoDTO {
    private Integer orderId;
    private String orderCode;
    private BigDecimal amount;
    private String paymentMethod;
    private String paymentStatus;
    private String qrUrl;
    private String bankName;
    private String accountNumber;
    private String transferContent;

    public Integer getOrderId() { return orderId; }
    public void setOrderId(Integer orderId) { this.orderId = orderId; }
    public String getOrderCode() { return orderCode; }
    public void setOrderCode(String orderCode) { this.orderCode = orderCode; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
    public String getQrUrl() { return qrUrl; }
    public void setQrUrl(String qrUrl) { this.qrUrl = qrUrl; }
    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }
    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
    public String getTransferContent() { return transferContent; }
    public void setTransferContent(String transferContent) { this.transferContent = transferContent; }
}
