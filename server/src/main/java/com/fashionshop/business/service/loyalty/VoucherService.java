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
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
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
        if (!body.getStartDate().isBefore(body.getEndDate())) {
            throw new BadRequestException("endDate phải sau startDate");
        }
        if (body.getDiscountType() == Voucher.DiscountType.PERCENTAGE) {
            if (body.getDiscountValue().compareTo(BigDecimal.ZERO) <= 0
                    || body.getDiscountValue().compareTo(BigDecimal.valueOf(100)) > 0) {
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
        String oldCode = voucher.getCode();

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

        // Kiểm tra tính hợp lệ sau khi gộp dữ liệu mới vào bản ghi hiện tại
        if (voucher.getStartDate() != null && voucher.getEndDate() != null
                && !voucher.getStartDate().isBefore(voucher.getEndDate())) {
            throw new BadRequestException("endDate phải sau startDate");
        }
        if (voucher.getDiscountType() == Voucher.DiscountType.PERCENTAGE && voucher.getDiscountValue() != null) {
            if (voucher.getDiscountValue().compareTo(BigDecimal.ZERO) <= 0
                    || voucher.getDiscountValue().compareTo(BigDecimal.valueOf(100)) > 0) {
                throw new BadRequestException("Phần trăm giảm phải từ 1 đến 100");
            }
        }

        Voucher updated = voucherDAO.update(voucher);
        voucherDAO.invalidateVoucherCache(oldCode);
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
        if (now.isBefore(voucher.getStartDate())) {
            throw new BadRequestException("Voucher chưa có hiệu lực");
        }
        if (now.isAfter(voucher.getEndDate())) {
            throw new BadRequestException("Voucher đã hết hạn sử dụng");
        }

        if (voucher.getTotalUsageLimit() > 0 && voucher.getUsedCount() >= voucher.getTotalUsageLimit()) {
            throw new BadRequestException("Voucher đã hết lượt sử dụng");
        }

        if (voucher.getUserUsageLimit() > 0) {
            int usedCount = voucherDAO.countUserVoucherUsage(userId, voucher.getCode());
            if (usedCount >= voucher.getUserUsageLimit()) {
                throw new BadRequestException(
                        "Bạn đã sử dụng mã voucher này " + usedCount + " lần (tối đa " + voucher.getUserUsageLimit() + " lần/người)"
                );
            }
        }

        BigDecimal minOrder = voucher.getMinOrderValue() != null ? voucher.getMinOrderValue() : BigDecimal.ZERO;
        if (body.getOrderTotal().compareTo(minOrder) < 0) {
            throw new BadRequestException("Đơn hàng tối thiểu " + minOrder.longValue() + " VNĐ để áp dụng voucher này");
        }

        BigDecimal discountAmount;
        if (voucher.getDiscountType() == Voucher.DiscountType.PERCENTAGE) {
            discountAmount = body.getOrderTotal().multiply(voucher.getDiscountValue())
                    .divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP);
            if (voucher.getMaxDiscountAmt() != null
                    && voucher.getMaxDiscountAmt().compareTo(BigDecimal.ZERO) > 0
                    && discountAmount.compareTo(voucher.getMaxDiscountAmt()) > 0) {
                discountAmount = voucher.getMaxDiscountAmt();
            }
        } else {
            discountAmount = voucher.getDiscountValue();
        }

        discountAmount = discountAmount.min(body.getOrderTotal()).setScale(0, RoundingMode.HALF_UP);
        BigDecimal finalTotal = body.getOrderTotal().subtract(discountAmount).max(BigDecimal.ZERO).setScale(0, RoundingMode.HALF_UP);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("code", voucher.getCode());
        result.put("discountType", voucher.getDiscountType());
        result.put("discountAmount", discountAmount.longValue());
        result.put("finalTotal", finalTotal.longValue());
        result.put("voucher", voucher);
        return result;
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

    public BigDecimal applyVoucherSafely(String code, Integer userId, BigDecimal orderTotal, EntityManager em) {
        Voucher voucher = voucherDAO.applyVoucherSafely(code, userId, em);
        if (voucher == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal minOrder = voucher.getMinOrderValue() != null ? voucher.getMinOrderValue() : BigDecimal.ZERO;
        if (orderTotal.compareTo(minOrder) < 0) {
            // Hoàn lại bộ đếm Redis trước khi ném lỗi Rollback
            voucherDAO.releaseVoucherSafely(code, userId, em);
            throw new BadRequestException("Đơn hàng tối thiểu " + minOrder.longValue() + "đ");
        }

        BigDecimal discountAmount;
        if (voucher.getDiscountType() == Voucher.DiscountType.PERCENTAGE) {
            discountAmount = orderTotal.multiply(voucher.getDiscountValue())
                    .divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP);
            if (voucher.getMaxDiscountAmt() != null
                    && voucher.getMaxDiscountAmt().compareTo(BigDecimal.ZERO) > 0
                    && discountAmount.compareTo(voucher.getMaxDiscountAmt()) > 0) {
                discountAmount = voucher.getMaxDiscountAmt();
            }
        } else {
            discountAmount = voucher.getDiscountValue() != null ? voucher.getDiscountValue() : BigDecimal.ZERO;
        }

        return discountAmount.min(orderTotal).setScale(0, RoundingMode.HALF_UP);
    }

    public Voucher applyVoucherSafely(String code, Integer userId, EntityManager em) {
        return voucherDAO.applyVoucherSafely(code, userId, em);
    }

    public void releaseVoucherSafely(String code, Integer userId, EntityManager em) {
        voucherDAO.releaseVoucherSafely(code, userId, em);
    }
}