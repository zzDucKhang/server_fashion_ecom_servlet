package com.fashionshop.dataaccess.dao.loyalty;

import com.fashionshop.business.entity.Order;
import com.fashionshop.business.entity.PointTransaction;
import com.fashionshop.business.entity.User;
import com.fashionshop.common.exception.NotFoundException;
import com.fashionshop.dataaccess.base.AbstractJpaDAO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class PointTransactionDAO extends AbstractJpaDAO<PointTransaction, Integer> {

    public enum RedeemFailureReason {
        USER_NOT_FOUND,
        INSUFFICIENT_POINTS,
        EXCEEDS_ORDER_TOTAL
    }

    public record AtomicEarnResult(boolean isIdempotent, PointTransaction transaction, int currentPoint, int pointChange) {}

    public record AtomicRedeemResult(boolean isIdempotent, PointTransaction transaction, int currentPoint, int discountAmount, RedeemFailureReason error) {
        public static AtomicRedeemResult ok(boolean idempotent, PointTransaction tx, int points, int discount) {
            return new AtomicRedeemResult(idempotent, tx, points, discount, null);
        }
        public static AtomicRedeemResult fail(RedeemFailureReason reason, int currentPoints) {
            return new AtomicRedeemResult(false, null, currentPoints, 0, reason);
        }
    }

    public PointTransactionDAO() {
        super(PointTransaction.class);
    }

    public List<PointTransaction> findByUserId(int userId, int page, int limit) {
        return execute(em -> em.createQuery(
                "SELECT pt FROM PointTransaction pt " +
                "LEFT JOIN FETCH pt.user " +
                "LEFT JOIN FETCH pt.order " +
                "WHERE pt.user.id = :userId ORDER BY pt.createdAt DESC", PointTransaction.class)
                .setParameter("userId", userId)
                .setFirstResult((page - 1) * limit)
                .setMaxResults(limit)
                .getResultList());
    }

    public long countByUserId(int userId) {
        return execute(em -> em.createQuery(
                "SELECT COUNT(pt) FROM PointTransaction pt WHERE pt.user.id = :userId", Long.class)
                .setParameter("userId", userId)
                .getSingleResult());
    }

    public Optional<PointTransaction> findExistingByOrderAndType(EntityManager em, int orderId, PointTransaction.PointTransType type) {
        List<PointTransaction> list = em.createQuery(
                "SELECT pt FROM PointTransaction pt " +
                "LEFT JOIN FETCH pt.user " +
                "LEFT JOIN FETCH pt.order " +
                "WHERE pt.order.id = :orderId AND pt.transactionType = :type", PointTransaction.class)
                .setParameter("orderId", orderId)
                .setParameter("type", type)
                .setMaxResults(1)
                .getResultList();
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    /**
     * Tích điểm độc lập (dùng cho API POST /points/earn)
     */
    public AtomicEarnResult atomicEarnPoints(int userId, int orderId, int pointChange, String description) {
        return executeTransaction(em -> atomicEarnPoints(userId, orderId, pointChange, description, em));
    }

    /**
     * Tích điểm bên trong Transaction có sẵn (dùng khi Admin duyệt đơn sang DELIVERED)
     */
    public AtomicEarnResult atomicEarnPoints(int userId, int orderId, int pointChange, String description, EntityManager em) {
        User lockedUser = em.find(User.class, userId, LockModeType.PESSIMISTIC_WRITE);
        if (lockedUser == null || lockedUser.getDeletedAt() != null) {
            return null;
        }

        int currentBalance = lockedUser.getRewardPoints() != null ? lockedUser.getRewardPoints() : 0;

        // Kiểm tra tính Idempotent (chống cộng trùng 2 lần cho cùng 1 đơn hàng)
        Optional<PointTransaction> existing = findExistingByOrderAndType(em, orderId, PointTransaction.PointTransType.EARN);
        if (existing.isPresent()) {
            return new AtomicEarnResult(true, existing.get(), currentBalance, 0);
        }

        int newPoints = currentBalance + pointChange;
        lockedUser.setRewardPoints(newPoints);
        em.merge(lockedUser);

        Order order = em.find(Order.class, orderId);

        PointTransaction transaction = new PointTransaction();
        transaction.setUser(lockedUser);
        transaction.setOrder(order);
        transaction.setPointChange(pointChange);
        transaction.setCurrentPoint(newPoints);
        transaction.setTransactionType(PointTransaction.PointTransType.EARN);
        transaction.setDescription(description);

        em.persist(transaction);

        return new AtomicEarnResult(false, transaction, newPoints, pointChange);
    }

    /**
     * Tiêu điểm thưởng an toàn (Atomic Redeem độc lập)
     */
    public AtomicRedeemResult atomicRedeemPoints(int userId, int orderId, int pointsToRedeem, BigDecimal orderTotal, String description) {
        return executeTransaction(em -> atomicRedeemPoints(userId, orderId, pointsToRedeem, orderTotal, description, em));
    }

    /**
     * Tiêu điểm thưởng bên trong Transaction có sẵn (dùng trong Checkout OrderService)
     */
    public AtomicRedeemResult atomicRedeemPoints(int userId, int orderId, int pointsToRedeem, BigDecimal orderTotal, String description, EntityManager em) {
        User lockedUser = em.find(User.class, userId, LockModeType.PESSIMISTIC_WRITE);
        if (lockedUser == null || lockedUser.getDeletedAt() != null) {
            return AtomicRedeemResult.fail(RedeemFailureReason.USER_NOT_FOUND, 0);
        }

        int currentBalance = lockedUser.getRewardPoints() != null ? lockedUser.getRewardPoints() : 0;

        Optional<PointTransaction> existing = findExistingByOrderAndType(em, orderId, PointTransaction.PointTransType.REDEEM);
        if (existing.isPresent()) {
            return AtomicRedeemResult.ok(true, existing.get(), currentBalance, Math.abs(existing.get().getPointChange()));
        }

        if (currentBalance < pointsToRedeem) {
            return AtomicRedeemResult.fail(RedeemFailureReason.INSUFFICIENT_POINTS, currentBalance);
        }

        if (BigDecimal.valueOf(pointsToRedeem).compareTo(orderTotal) > 0) {
            return AtomicRedeemResult.fail(RedeemFailureReason.EXCEEDS_ORDER_TOTAL, currentBalance);
        }

        int newPoints = currentBalance - pointsToRedeem;
        lockedUser.setRewardPoints(newPoints);
        em.merge(lockedUser);

        Order order = em.find(Order.class, orderId);

        PointTransaction transaction = new PointTransaction();
        transaction.setUser(lockedUser);
        transaction.setOrder(order);
        transaction.setPointChange(-pointsToRedeem);
        transaction.setCurrentPoint(newPoints);
        transaction.setTransactionType(PointTransaction.PointTransType.REDEEM);
        transaction.setDescription(description);

        em.persist(transaction);

        return AtomicRedeemResult.ok(false, transaction, newPoints, pointsToRedeem);
    }

    /**
     * Bồi hoàn điểm khi hủy đơn hoặc trả hàng (giống hệt compensatePointsForCancelledOrder bên Node.js)
     */
    public void compensatePointsForCancelledOrder(int orderId, EntityManager em) {
        Order order = em.find(Order.class, orderId);
        if (order == null) throw new NotFoundException("Đơn hàng #" + orderId + " không tồn tại");

        int userId = order.getUser().getId();
        User lockedUser = em.find(User.class, userId, LockModeType.PESSIMISTIC_WRITE);
        if (lockedUser == null) throw new NotFoundException("Người dùng #" + userId + " không tồn tại");

        int currentPoints = lockedUser.getRewardPoints() != null ? lockedUser.getRewardPoints() : 0;

        // 1. Hoàn lại số điểm khách đã dùng khi đặt đơn (nếu có)
        if (order.getPointUsed() != null && order.getPointUsed() > 0) {
            Optional<PointTransaction> existingRefund = findExistingByOrderAndType(em, order.getId(), PointTransaction.PointTransType.REFUND);
            if (existingRefund.isEmpty()) {
                currentPoints += order.getPointUsed();
                lockedUser.setRewardPoints(currentPoints);
                em.merge(lockedUser);

                PointTransaction refundTx = new PointTransaction();
                refundTx.setUser(lockedUser);
                refundTx.setOrder(order);
                refundTx.setPointChange(order.getPointUsed());
                refundTx.setCurrentPoint(currentPoints);
                refundTx.setTransactionType(PointTransaction.PointTransType.REFUND);
                refundTx.setDescription("Hoàn " + order.getPointUsed() + " điểm do hủy/hoàn đơn hàng #" + order.getOrderCode());
                em.persist(refundTx);
            }
        }

        // 2. Thu hồi số điểm đã tích từ đơn hàng này (nếu trước đó đơn đã từng được cộng điểm EARN)
        if (order.getPointEarned() != null && order.getPointEarned() > 0) {
            Optional<PointTransaction> existingEarn = findExistingByOrderAndType(em, order.getId(), PointTransaction.PointTransType.EARN);
            if (existingEarn.isPresent()) {
                List<PointTransaction> existingRevokes = em.createQuery(
                        "SELECT pt FROM PointTransaction pt WHERE pt.order.id = :orderId AND pt.transactionType = :type AND pt.description LIKE :desc", PointTransaction.class)
                        .setParameter("orderId", order.getId())
                        .setParameter("type", PointTransaction.PointTransType.REDEEM)
                        .setParameter("desc", "%Thu hồi%")
                        .setMaxResults(1)
                        .getResultList();

                if (existingRevokes.isEmpty()) {
                    int pointsToDeduct = Math.min(currentPoints, order.getPointEarned());
                    if (pointsToDeduct > 0) {
                        currentPoints -= pointsToDeduct;
                        lockedUser.setRewardPoints(currentPoints);
                        em.merge(lockedUser);

                        PointTransaction revokeTx = new PointTransaction();
                        revokeTx.setUser(lockedUser);
                        revokeTx.setOrder(order);
                        revokeTx.setPointChange(-pointsToDeduct);
                        revokeTx.setCurrentPoint(currentPoints);
                        revokeTx.setTransactionType(PointTransaction.PointTransType.REDEEM);
                        revokeTx.setDescription("Thu hồi " + pointsToDeduct + " điểm thưởng do hủy/hoàn đơn hàng #" + order.getOrderCode());
                        em.persist(revokeTx);
                    }
                }
            }
        }
    }
}