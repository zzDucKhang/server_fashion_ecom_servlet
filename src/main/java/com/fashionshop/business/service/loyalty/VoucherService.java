package com.fashionshop.business.service.loyalty;

import com.fashionshop.business.entity.Voucher;
import com.fashionshop.common.exception.BadRequestException;
import com.fashionshop.common.exception.ConflictException;
import com.fashionshop.common.exception.NotFoundException;
import com.fashionshop.common.response.PagedResponse;
import com.fashionshop.dataaccess.dao.loyalty.VoucherDAO;
import com.fashionshop.presentation.dto.loyalty.*;
import jakarta.persistence.EntityManager;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class VoucherService {
    private final VoucherDAO voucherDAO = new VoucherDAO();

    public PagedResponse<Voucher> listVouchers(VoucherQueryDTO query) {
        List<Voucher> data = voucherDAO.findAllPaginated(query.getPage(), query.getLimit(), query.getIsActive());
        long total = voucherDAO.countAll(query.getIsActive());
        return new PagedResponse<>(data, total, query.getPage(), query.getLimit());
    }

    public Voucher getVoucherById(Integer id) {
        return voucherDAO.findById(id)
                .filter(v -> v.getDeletedAt() == null)
                .orElseThrow(() -> new NotFoundException("Voucher không tồn tại"));
    }

    public Voucher createVoucher(CreateVoucherDTO body) {
        if (body.getCode() == null || body.getCode().trim().length() < 3) {
            throw new BadRequestException("Mã voucher tối thiểu 3 ký tự");
        }
        if (body.getEndDate().isBefore(body.getStartDate())) {
            throw new BadRequestException("Ngày kết thúc phải sau ngày bắt đầu");
        }
        if (body.getDiscountType() == Voucher.DiscountType.PERCENTAGE) {
            if (body.getDiscountValue().compareTo(BigDecimal.ZERO) <= 0 || body.getDiscountValue().compareTo(BigDecimal.valueOf(100)) > 0) {
                throw new BadRequestException("Phần trăm giảm phải từ 1 đến 100");
            }
        }

        String normalizedCode = body.getCode().trim().toUpperCase();
        if (voucherDAO.findByCode(normalizedCode).isPresent()) {
            throw new ConflictException("Mã voucher \"" + normalizedCode + "\" đã tồn tại trong hệ thống");
        }

        Voucher voucher = new Voucher();
        voucher.setCode(normalizedCode);
        voucher.setDiscountType(body.getDiscountType());
        voucher.setDiscountValue(body.getDiscountValue());
        voucher.setMinOrderValue(body.getMinOrderValue() != null ? body.getMinOrderValue() : BigDecimal.ZERO);
        voucher.setMaxDiscountAmt(body.getMaxDiscountAmt());
        voucher.setTotalUsageLimit(body.getTotalUsageLimit() != null ? body.getTotalUsageLimit() : 0);
        voucher.setUserUsageLimit(body.getUserUsageLimit() != null ? body.getUserUsageLimit() : 1);
        voucher.setStartDate(body.getStartDate());
        voucher.setEndDate(body.getEndDate());
        voucher.setIsActive(body.getIsActive() != null ? body.getIsActive() : true);

        return voucherDAO.save(voucher);
    }

    public Voucher updateVoucher(Integer id, UpdateVoucherDTO body) {
        Voucher voucher = getVoucherById(id);

        if (body.getCode() != null) {
            String normalizedCode = body.getCode().trim().toUpperCase();
            voucherDAO.findByCode(normalizedCode).ifPresent(existing -> {
                if (!existing.getId().equals(id)) {
                    throw new ConflictException("Mã voucher \"" + normalizedCode + "\" đã tồn tại trong hệ thống");
                }
            });
            voucher.setCode(normalizedCode);
        }

        if (body.getDiscountType() != null) voucher.setDiscountType(body.getDiscountType());
        if (body.getDiscountValue() != null) voucher.setDiscountValue(body.getDiscountValue());
        if (body.getMinOrderValue() != null) voucher.setMinOrderValue(body.getMinOrderValue());
        if (body.getMaxDiscountAmt() != null) voucher.setMaxDiscountAmt(body.getMaxDiscountAmt());
        if (body.getTotalUsageLimit() != null) voucher.setTotalUsageLimit(body.getTotalUsageLimit());
        if (body.getUserUsageLimit() != null) voucher.setUserUsageLimit(body.getUserUsageLimit());
        if (body.getStartDate() != null) voucher.setStartDate(body.getStartDate());
        if (body.getEndDate() != null) voucher.setEndDate(body.getEndDate());
        if (body.getIsActive() != null) voucher.setIsActive(body.getIsActive());

        Voucher updated = voucherDAO.update(voucher);
        voucherDAO.invalidateVoucherCache(updated.getCode());
        return updated;
    }

    public void deleteVoucher(Integer id) {
        Voucher voucher = getVoucherById(id);
        voucher.setDeletedAt(LocalDateTime.now());
        voucherDAO.update(voucher);
        voucherDAO.invalidateVoucherCache(voucher.getCode());
    }

    public Map<String, Object> validateAndCalculate(ValidateVoucherDTO body, Integer userId) {
        Voucher voucher = voucherDAO.findByCode(body.getCode())
                .orElseThrow(() -> new NotFoundException("Mã voucher không hợp lệ hoặc không tồn tại"));

        if (Boolean.FALSE.equals(voucher.getIsActive())) {
            throw new BadRequestException("Voucher đã bị vô hiệu hóa");
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(voucher.getStartDate())) throw new BadRequestException("Voucher chưa có hiệu lực");
        if (now.isAfter(voucher.getEndDate())) throw new BadRequestException("Voucher đã hết hạn sử dụng");

        if (voucher.getTotalUsageLimit() > 0 && voucher.getUsedCount() >= voucher.getTotalUsageLimit()) {
            throw new BadRequestException("Voucher đã hết lượt sử dụng");
        }

        if (voucher.getUserUsageLimit() > 0) {
            int usedCount = voucherDAO.countUserVoucherUsage(userId, voucher.getCode());
            if (usedCount >= voucher.getUserUsageLimit()) {
                throw new BadRequestException("Bạn đã sử dụng mã voucher này " + usedCount + " lần (tối đa " + voucher.getUserUsageLimit() + " lần/người)");
            }
        }

        BigDecimal minOrder = voucher.getMinOrderValue();
        if (body.getOrderTotal().compareTo(minOrder) < 0) {
            throw new BadRequestException("Đơn hàng tối thiểu " + minOrder.toPlainString() + " VNĐ để áp dụng");
        }

        BigDecimal discountAmount;
        if (voucher.getDiscountType() == Voucher.DiscountType.PERCENTAGE) {
            discountAmount = body.getOrderTotal().multiply(voucher.getDiscountValue()).divide(BigDecimal.valueOf(100));
            if (voucher.getMaxDiscountAmt() != null && discountAmount.compareTo(voucher.getMaxDiscountAmt()) > 0) {
                discountAmount = voucher.getMaxDiscountAmt();
            }
        } else {
            discountAmount = voucher.getDiscountValue();
        }

        discountAmount = discountAmount.min(body.getOrderTotal());

        return Map.of(
                "code", voucher.getCode(),
                "discountType", voucher.getDiscountType(),
                "discountAmount", discountAmount,
                "finalTotal", body.getOrderTotal().subtract(discountAmount),
                "voucher", voucher
        );
    }

    public void applyVoucher(String code, Integer userId) {
        VoucherDAO.AtomicApplyResult res = voucherDAO.atomicApplyVoucher(code, userId);
        if (!res.success()) {
            switch (res.reason()) {
                case VOUCHER_NOT_FOUND -> throw new NotFoundException("Voucher không tồn tại");
                case VOUCHER_INACTIVE -> throw new BadRequestException("Voucher đã bị vô hiệu hóa");
                case VOUCHER_NOT_STARTED -> throw new BadRequestException("Voucher chưa có hiệu lực");
                case VOUCHER_EXPIRED -> throw new BadRequestException("Voucher đã hết hạn sử dụng");
                case VOUCHER_USAGE_LIMIT_REACHED -> throw new BadRequestException("Voucher đã hết lượt sử dụng");
                case USER_USAGE_LIMIT_REACHED -> throw new BadRequestException("Bạn đã dùng hết lượt sử dụng mã voucher này");
            }
        }
    }

    public Voucher applyVoucherSafely(String code, Integer userId, EntityManager em) {
        return voucherDAO.applyVoucherSafely(code, userId, em);
    }

    public void releaseVoucherSafely(String code, Integer userId, EntityManager em) {
        voucherDAO.releaseVoucherSafely(code, userId, em);
    }
}