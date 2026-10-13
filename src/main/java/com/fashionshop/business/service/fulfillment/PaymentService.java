package com.fashionshop.business.service.fulfillment;

import com.fashionshop.business.entity.Order;
import com.fashionshop.business.entity.PaymentTransaction;
import com.fashionshop.common.exception.BadRequestException;
import com.fashionshop.common.exception.ForbiddenException;
import com.fashionshop.common.exception.NotFoundException;
import com.fashionshop.common.util.JPAUtil;
import com.fashionshop.config.AppConfig;
import com.fashionshop.dataaccess.dao.fulfillment.PaymentTransactionDAO;
import com.fashionshop.presentation.dto.fulfillment.PaymentInfoDTO;
import com.fashionshop.presentation.dto.fulfillment.PaymentStatusDTO;
import com.fashionshop.presentation.dto.fulfillment.SepayWebhookDTO;
import jakarta.persistence.EntityManager;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PaymentService {

    private final PaymentTransactionDAO paymentDAO = new PaymentTransactionDAO();
    
    public boolean verifySePaySignature(String signatureHeader, String rawPayload) {
        if (signatureHeader == null || signatureHeader.trim().isEmpty() || rawPayload == null) {
            return false;
        }
        
        String secret = AppConfig.SEPAY_WEBHOOK_SECRET;
        if (secret == null || secret.trim().isEmpty()) {
            return false; // Bắt buộc cấu hình SEPAY_WEBHOOK_SECRET
        }

        try {
            Mac hmacSha256 = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            hmacSha256.init(secretKey);
            byte[] hash = hmacSha256.doFinal(rawPayload.getBytes(StandardCharsets.UTF_8));

            String calculatedBase64 = Base64.getEncoder().encodeToString(hash);
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            String calculatedHex = sb.toString();

            String cleanSig = signatureHeader.trim();
            if (cleanSig.startsWith("sha256=")) {
                cleanSig = cleanSig.substring(7);
            }

            return MessageDigest.isEqual(cleanSig.getBytes(StandardCharsets.UTF_8), calculatedBase64.getBytes(StandardCharsets.UTF_8))
                    || MessageDigest.isEqual(cleanSig.toLowerCase().getBytes(StandardCharsets.UTF_8), calculatedHex.toLowerCase().getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            return false;
        }
    }

    public boolean verifySePaySignature(byte[] rawBody, String signatureHeader, String timestamp) {
        if (rawBody == null || signatureHeader == null || signatureHeader.isBlank()) {
            return false;
        }

        // Bắt buộc xác thực timestamp webhook nếu có gửi (chống replay attack trong 5 phút)
        if (timestamp != null && !timestamp.isBlank()) {
            try {
                long reqTime = Long.parseLong(timestamp.trim());
                if (reqTime < 10000000000L) {
                    reqTime *= 1000L;
                }
                long currentTime = System.currentTimeMillis();
                if (Math.abs(currentTime - reqTime) > 5 * 60 * 1000L) {
                    return false;
                }
            } catch (NumberFormatException e) {
                return false;
            }
        }

        String rawPayload = new String(rawBody, StandardCharsets.UTF_8);
        if (timestamp != null && !timestamp.isBlank()) {
            String timestampedPayload = timestamp.trim() + "." + rawPayload;
            if (verifySePaySignature(signatureHeader, timestampedPayload)) {
                return true;
            }
        }
        return verifySePaySignature(signatureHeader, rawPayload);
    }

    public void handleSePayWebhook(SepayWebhookDTO payload) {
        if (!"in".equalsIgnoreCase(payload.getTransferType())) {
            throw new BadRequestException("Không phải giao dịch tiền vào");
        }

        // Kiểm tra số tài khoản nhận tiền (hỗ trợ cả accountNumber và subAccount)
        String configuredAccount = AppConfig.SEPAY_ACCOUNT_NUMBER;
        if (configuredAccount != null && !configuredAccount.isBlank()) {
            boolean matchAcc = payload.getAccountNumber() != null && configuredAccount.trim().equalsIgnoreCase(payload.getAccountNumber().trim());
            boolean matchSub = payload.getSubAccount() != null && configuredAccount.trim().equalsIgnoreCase(payload.getSubAccount().trim());
            if (!matchAcc && !matchSub) {
                throw new BadRequestException("Tài khoản nhận tiền không hợp lệ");
            }
        }

        String content = payload.getCode() != null && !payload.getCode().isBlank() ? payload.getCode() : payload.getContent();
        if (content == null || content.isBlank()) {
            throw new BadRequestException("Không tìm thấy nội dung chuyển khoản");
        }

        Pattern dhPattern = Pattern.compile("DH[\\s_-]*(\\d+)", Pattern.CASE_INSENSITIVE);
        Matcher dhMatcher = dhPattern.matcher(content);
        Integer orderId = null;
        String orderCode = null;

        if (dhMatcher.find()) {
            orderId = Integer.parseInt(dhMatcher.group(1));
        } else {
            Pattern ordPattern = Pattern.compile("(ORD-\\d{8}-[A-Za-z0-9]+)", Pattern.CASE_INSENSITIVE);
            Matcher ordMatcher = ordPattern.matcher(content);
            if (ordMatcher.find()) {
                orderCode = ordMatcher.group(1).toUpperCase();
            } else {
                throw new NotFoundException("Mã đơn hàng không hợp lệ trong nội dung chuyển khoản");
            }
        }

        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            // Kiểm tra Idempotency theo ID giao dịch SePay (tránh xử lý webhook trùng)
            if (payload.getId() != null) {
                String providerTransId = String.valueOf(payload.getId());
                Long existingCount = em.createQuery(
                        "SELECT COUNT(p) FROM PaymentTransaction p WHERE p.providerTransId = :providerTransId", Long.class)
                        .setParameter("providerTransId", providerTransId)
                        .getSingleResult();
                if (existingCount != null && existingCount > 0) {
                    em.getTransaction().rollback();
                    return; // Giao dịch này đã được ghi nhận trước đó (Idempotent)
                }
            }

            Order order = null;
            if (orderId != null) {
                order = em.find(Order.class, orderId);
            } else if (orderCode != null) {
                try {
                    order = em.createQuery("SELECT o FROM Order o WHERE UPPER(o.orderCode) = :orderCode", Order.class)
                            .setParameter("orderCode", orderCode)
                            .getSingleResult();
                    orderId = order.getId();
                } catch (jakarta.persistence.NoResultException e) {
                    order = null;
                }
            }

            if (order == null) {
                throw new NotFoundException("Không tìm thấy đơn hàng cho nội dung: " + content);
            }

            if (order.getPaymentStatus() == Order.PaymentStatus.SUCCESS) {
                em.getTransaction().rollback();
                return; // Đã xử lý thanh toán
            }

            if (payload.getTransferAmount() == null || order.getTotalAmount().compareTo(payload.getTransferAmount()) > 0) {
                throw new BadRequestException("Số tiền thanh toán không đủ");
            }

            PaymentTransaction payment = paymentDAO.findPendingByOrderId(orderId);
            if (payment != null) {
                // Đã có pending: cập nhật trạng thái SUCCESS
                payment.setStatus(Order.PaymentStatus.SUCCESS);
                payment.setProviderTransId(String.valueOf(payload.getId()));
                em.merge(payment);
            } else {
                // Khách quét QR thanh toán trực tiếp không qua /initiate: tự tạo PaymentTransaction
                PaymentTransaction newTx = new PaymentTransaction();
                newTx.setOrder(order);
                newTx.setProvider("SEPAY");
                newTx.setAmount(payload.getTransferAmount());
                newTx.setStatus(Order.PaymentStatus.SUCCESS);
                newTx.setProviderTransId(String.valueOf(payload.getId()));
                em.persist(newTx);
            }

            // Cập nhật trạng thái Order
            order.setPaymentStatus(Order.PaymentStatus.SUCCESS);
            if (order.getStatus() == Order.OrderStatus.PENDING_PAYMENT) {
                order.setStatus(Order.OrderStatus.PROCESSING);
            }
            em.merge(order);

            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public PaymentTransaction initiatePayment(Integer orderId, Integer userId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Order order = em.find(Order.class, orderId);
            if (order == null) throw new NotFoundException("Đơn hàng không tồn tại");

            if (!order.getUser().getId().equals(userId)) {
                throw new ForbiddenException("Không có quyền truy cập đơn hàng này");
            }

            if (order.getPaymentStatus() == Order.PaymentStatus.SUCCESS) {
                throw new BadRequestException("Đơn hàng đã được thanh toán");
            }

            PaymentTransaction existingTx = paymentDAO.findByOrderId(orderId);
            if (existingTx != null) {
                if (existingTx.getStatus() == Order.PaymentStatus.PENDING) {
                    return existingTx;
                }
                existingTx.setStatus(Order.PaymentStatus.PENDING);
                existingTx.setAmount(order.getTotalAmount());
                existingTx.setProvider("SEPAY");
                return paymentDAO.update(existingTx);
            }

            PaymentTransaction tx = new PaymentTransaction();
            tx.setOrder(order);
            tx.setProvider("SEPAY");
            tx.setAmount(order.getTotalAmount());
            tx.setStatus(Order.PaymentStatus.PENDING);

            return paymentDAO.create(tx);
        } finally {
            em.close();
        }
    }

    public PaymentInfoDTO getPaymentInfo(Integer orderId, Integer userId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Order order = em.find(Order.class, orderId);
            if (order == null) throw new NotFoundException("Đơn hàng không tồn tại");

            if (!order.getUser().getId().equals(userId)) {
                throw new ForbiddenException("Không có quyền truy cập đơn hàng này");
            }

            String acc = AppConfig.get("SEPAY_ACCOUNT_NUMBER", "");
            String bank = AppConfig.get("SEPAY_BANK_CODE", "MBBank");
            String qrBaseUrl = AppConfig.get("SEPAY_QR_BASE_URL", "https://qr.sepay.vn/img");
            String content = "DH" + order.getId();

            String qrUrl = String.format("%s?acc=%s&bank=%s&amount=%s&des=%s",
                    qrBaseUrl, acc, bank, order.getTotalAmount().toPlainString(),
                    URLEncoder.encode(content, StandardCharsets.UTF_8));

            PaymentInfoDTO dto = new PaymentInfoDTO();
            dto.setOrderId(order.getId());
            dto.setOrderCode(order.getOrderCode());
            dto.setAmount(order.getTotalAmount());
            dto.setPaymentMethod(order.getPaymentMethod().name());
            dto.setPaymentStatus(order.getPaymentStatus().name());
            dto.setQrUrl(qrUrl);
            dto.setBankName(bank);
            dto.setAccountNumber(acc);
            dto.setTransferContent(content);

            return dto;
        } finally {
            em.close();
        }
    }

    public PaymentStatusDTO getPaymentStatus(Integer orderId, Integer userId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Order order = em.find(Order.class, orderId);
            if (order == null) throw new NotFoundException("Đơn hàng không tồn tại");

            if (!order.getUser().getId().equals(userId)) {
                throw new ForbiddenException("Không có quyền truy cập đơn hàng này");
            }

            PaymentStatusDTO dto = new PaymentStatusDTO();
            dto.setOrderId(order.getId());
            dto.setPaymentStatus(order.getPaymentStatus().name());
            dto.setOrderStatus(order.getStatus().name());

            return dto;
        } finally {
            em.close();
        }
    }

    public List<PaymentTransaction> listTransactions(int page, int limit) {
        return paymentDAO.findAll(page, limit);
    }

    public long countTransactions() {
        return paymentDAO.countAll();
    }

    public PaymentTransaction findById(Integer id) {
        PaymentTransaction tx = paymentDAO.findById(id);
        if (tx == null) {
            throw new NotFoundException("Giao dịch thanh toán không tồn tại: " + id);
        }
        return tx;
    }
}