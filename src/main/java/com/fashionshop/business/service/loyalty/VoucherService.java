package com.fashionshop.business.service.loyalty;

import com.fashionshop.business.entity.Voucher;
import com.fashionshop.common.exception.BadRequestException;
import com.fashionshop.common.exception.NotFoundException;
import jakarta.persistence.EntityManager;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class VoucherService {

    public BigDecimal applyVoucherSafely(String voucherCode, Integer userId, BigDecimal orderSubTotal, EntityManager em) {
        if (voucherCode == null || voucherCode.isBlank()) {
            return BigDecimal.ZERO;
        }

        List<Voucher> vouchers = em.createQuery(
                "SELECT v FROM Voucher v WHERE LOWER(v.code) = LOWER(:code) AND v.deletedAt IS NULL", Voucher.class)
                .setParameter("code", voucherCode.trim())
                .getResultList();

        if (vouchers.isEmpty()) {
            throw new NotFoundException("Mã giảm giá không tồn tại");
        }

        Voucher voucher = vouchers.get(0);

        if (Boolean.FALSE.equals(voucher.getIsActive())) {
            throw new BadRequestException("Mã giảm giá hiện đang bị vô hiệu hoá");
        }

        LocalDateTime now = LocalDateTime.now();
        if (voucher.getStartDate() != null && now.isBefore(voucher.getStartDate())) {
            throw new BadRequestException("Mã giảm giá chưa đến đợt áp dụng");
        }

        if (voucher.getEndDate() != null && now.isAfter(voucher.getEndDate())) {
            throw new BadRequestException("Mã giảm giá đã hết hạn sử dụng");
        }

        if (voucher.getTotalUsageLimit() != null && voucher.getTotalUsageLimit() > 0 
                && voucher.getUsedCount() >= voucher.getTotalUsageLimit()) {
            throw new BadRequestException("Mã giảm giá đã hết lượt sử dụng");
        }

        if (voucher.getMinOrderValue() != null && orderSubTotal.compareTo(voucher.getMinOrderValue()) < 0) {
            throw new BadRequestException("Đơn hàng chưa đạt giá trị tối thiểu " + voucher.getMinOrderValue() + "đ để áp dụng voucher");
        }

        // Kiểm tra giới hạn lượt dùng của user này
        if (voucher.getUserUsageLimit() != null && voucher.getUserUsageLimit() > 0) {
            Long userUsed = em.createQuery(
                    "SELECT COUNT(o) FROM Order o WHERE o.user.id = :userId AND LOWER(o.voucherCode) = LOWER(:code) AND o.status != 'CANCELLED'", Long.class)
                    .setParameter("userId", userId)
                    .setParameter("code", voucherCode.trim())
                    .getSingleResult();

            if (userUsed >= voucher.getUserUsageLimit()) {
                throw new BadRequestException("Bạn đã dùng hết số lượt cho phép của mã giảm giá này");
            }
        }

        // Tăng lượt sử dụng
        voucher.incrementUsage();
        em.merge(voucher);

        // Tính số tiền giảm
        return voucher.calculateDiscount(orderSubTotal);
    }

    public void releaseVoucherSafely(String voucherCode, Integer userId, EntityManager em) {
        if (voucherCode == null || voucherCode.isBlank()) return;

        List<Voucher> vouchers = em.createQuery(
                "SELECT v FROM Voucher v WHERE LOWER(v.code) = LOWER(:code)", Voucher.class)
                .setParameter("code", voucherCode.trim())
                .getResultList();

        if (!vouchers.isEmpty()) {
            Voucher voucher = vouchers.get(0);
            if (voucher.getUsedCount() != null && voucher.getUsedCount() > 0) {
                voucher.setUsedCount(voucher.getUsedCount() - 1);
                em.merge(voucher);
            }
        }
    }
}

