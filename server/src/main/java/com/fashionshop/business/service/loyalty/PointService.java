package com.fashionshop.business.service.loyalty;

import com.fashionshop.business.entity.Order;
import com.fashionshop.business.entity.PointTransaction;
import com.fashionshop.business.entity.User;
import com.fashionshop.common.exception.BadRequestException;
import com.fashionshop.common.exception.NotFoundException;
import com.fashionshop.common.response.PagedResponse;
import com.fashionshop.dataaccess.dao.cartorder.OrderDAO;
import com.fashionshop.dataaccess.dao.identity.UserDAO;
import com.fashionshop.dataaccess.dao.loyalty.PointTransactionDAO;
import com.fashionshop.presentation.dto.loyalty.EarnPointsDTO;
import com.fashionshop.presentation.dto.loyalty.PointQueryDTO;
import com.fashionshop.presentation.dto.loyalty.RedeemPointsDTO;
import jakarta.persistence.EntityManager;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PointService {
    private final PointTransactionDAO pointTransactionDAO = new PointTransactionDAO();
    private final OrderDAO orderDAO = new OrderDAO();
    private final UserDAO userDAO = new UserDAO();

    public Map<String, Object> earnPoints(Integer adminId, EarnPointsDTO body) {
        Order order = orderDAO.findById(body.getOrderId())
                .filter(o -> o.getDeletedAt() == null)
                .orElseThrow(() -> new NotFoundException("Đơn hàng không tồn tại"));

        if (order.getStatus() != Order.OrderStatus.DELIVERED) {
            throw new BadRequestException("Chỉ cộng điểm khi đơn hàng đã giao thành công (DELIVERED)");
        }

        int targetUserId = order.getUser().getId();
        int pointChange = order.getTotalAmount().multiply(new BigDecimal("0.01")).setScale(0, RoundingMode.FLOOR).intValue();
        if (pointChange <= 0) {
            throw new BadRequestException("Giá trị đơn hàng quá nhỏ, không đủ điều kiện cộng điểm");
        }

        String desc = (body.getDescription() != null && !body.getDescription().isBlank())
                ? body.getDescription().trim()
                : "Cộng điểm từ đơn hàng #" + order.getOrderCode();

        PointTransactionDAO.AtomicEarnResult result = pointTransactionDAO.atomicEarnPoints(targetUserId, body.getOrderId(), pointChange, desc);
        if (result == null) {
            throw new NotFoundException("Người dùng không tồn tại");
        }

        Map<String, Object> res = new LinkedHashMap<>();
        if (result.isIdempotent()) {
            res.put("message", "Đơn hàng #" + body.getOrderId() + " đã được cộng điểm trước đó (idempotent)");
        } else {
            res.put("message", "Cộng " + pointChange + " điểm thành công cho user #" + targetUserId);
        }
        res.put("currentPoint", result.currentPoint());
        res.put("transaction", result.transaction());
        return res;
    }

    /**
     * Tự động cộng 1% điểm thưởng khi đơn hàng chuyển sang trạng thái DELIVERED (chạy chung Transaction với OrderService)
     */
    public void awardPointsForDeliveredOrder(Order order, EntityManager em) {
        if (order == null || order.getUser() == null) return;

        int pointEarned = (order.getPointEarned() != null && order.getPointEarned() > 0)
                ? order.getPointEarned()
                : (order.getTotalAmount() != null
                    ? order.getTotalAmount().multiply(new BigDecimal("0.01")).setScale(0, RoundingMode.FLOOR).intValue()
                    : 0);

        if (pointEarned <= 0) return;

        order.setPointEarned(pointEarned);
        String desc = "Tích lũy điểm thưởng từ đơn hàng " + order.getOrderCode();
        pointTransactionDAO.atomicEarnPoints(order.getUser().getId(), order.getId(), pointEarned, desc, em);
    }

    public Map<String, Object> redeemPoints(Integer userId, RedeemPointsDTO body) {
        Order order = orderDAO.findById(body.getOrderId())
                .filter(o -> o.getDeletedAt() == null)
                .orElseThrow(() -> new NotFoundException("Đơn hàng không tồn tại"));

        if (!order.getUser().getId().equals(userId)) {
            throw new BadRequestException("Đơn hàng không thuộc về người dùng này");
        }

        String desc = "Dùng " + body.getPointsToRedeem() + " điểm (tương đương " + body.getPointsToRedeem() + " VNĐ) thanh toán đơn hàng #" + order.getId();

        PointTransactionDAO.AtomicRedeemResult result = pointTransactionDAO.atomicRedeemPoints(
                userId, body.getOrderId(), body.getPointsToRedeem(), order.getTotalAmount(), desc);

        if (result.error() != null) {
            switch (result.error()) {
                case USER_NOT_FOUND -> throw new NotFoundException("Người dùng không tồn tại");
                case INSUFFICIENT_POINTS -> throw new BadRequestException(
                        "Số điểm không đủ. Bạn đang có " + result.currentPoint() + " điểm, cần " + body.getPointsToRedeem() + " điểm");
                case EXCEEDS_ORDER_TOTAL -> throw new BadRequestException(
                        "Số điểm sử dụng (" + body.getPointsToRedeem() + " điểm) vượt quá giá trị đơn hàng (" + order.getTotalAmount().toPlainString() + " VNĐ)");
            }
        }

        Map<String, Object> res = new LinkedHashMap<>();
        if (result.isIdempotent()) {
            res.put("message", "Điểm đã được áp dụng cho đơn hàng #" + body.getOrderId() + " trước đó (idempotent)");
        } else {
            res.put("message", "Đã dùng " + body.getPointsToRedeem() + " điểm (tương đương " + body.getPointsToRedeem() + " VNĐ)");
        }
        res.put("discountAmount", result.discountAmount());
        res.put("currentPoint", result.currentPoint());
        res.put("transaction", result.transaction());
        return res;
    }

    public PagedResponse<PointTransaction> getHistory(Integer userId, PointQueryDTO query) {
        List<PointTransaction> data = pointTransactionDAO.findByUserId(userId, query.getPage(), query.getLimit());
        long total = pointTransactionDAO.countByUserId(userId);
        return new PagedResponse<>(data, total, query.getPage(), query.getLimit());
    }

    public int getBalance(Integer userId) {
        User user = userDAO.findById(userId)
                .filter(u -> u.getDeletedAt() == null)
                .orElseThrow(() -> new NotFoundException("Người dùng không tồn tại"));
        return user.getRewardPoints() != null ? user.getRewardPoints() : 0;
    }

    public void compensatePointsForCancelledOrder(int orderId, EntityManager em) {
        pointTransactionDAO.compensatePointsForCancelledOrder(orderId, em);
    }
}