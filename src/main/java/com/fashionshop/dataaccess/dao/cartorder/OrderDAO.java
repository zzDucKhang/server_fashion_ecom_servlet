package com.fashionshop.dataaccess.dao.cartorder;

import com.fashionshop.business.entity.Order;
import com.fashionshop.business.entity.OrderItem;
import com.fashionshop.common.response.PagedResponse;
import com.fashionshop.dataaccess.base.AbstractJpaDAO;
import jakarta.persistence.TypedQuery;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;

public class OrderDAO extends AbstractJpaDAO<Order, Integer> {

    public OrderDAO() {
        super(Order.class);
    }

    /**
     * Tìm chi tiết đơn hàng (khớp findById bên Node.js OrderRepo)
     */
    public Optional<Order> findByIdAndUser(int id, Integer userId) {
        return execute(em -> {
            StringBuilder jpql = new StringBuilder(
                    "SELECT DISTINCT o FROM Order o " +
                    "LEFT JOIN FETCH o.orderItems oi " +
                    "LEFT JOIN FETCH oi.variant v " +
                    "LEFT JOIN FETCH v.product p " +
                    "LEFT JOIN FETCH o.shipment s " +
                    "LEFT JOIN FETCH o.paymentTransaction pt " +
                    "WHERE o.id = :id AND o.deletedAt IS NULL");

            if (userId != null) {
                jpql.append(" AND o.user.id = :userId");
            }

            TypedQuery<Order> query = em.createQuery(jpql.toString(), Order.class)
                    .setParameter("id", id);

            if (userId != null) {
                query.setParameter("userId", userId);
            }

            List<Order> list = query.setMaxResults(1).getResultList();
            return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
        });
    }

    public Optional<Order> findByOrderCode(String orderCode) {
        return execute(em -> {
            var list = em.createQuery(
                    "SELECT o FROM Order o WHERE o.orderCode = :code AND o.deletedAt IS NULL", Order.class)
                    .setParameter("code", orderCode.trim())
                    .setMaxResults(1)
                    .getResultList();
            return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
        });
    }

    /**
     * Danh sách đơn hàng phân trang (khớp list() bên Node.js OrderRepo)
     */
    public PagedResponse<Order> list(Integer userId, int page, int limit, Order.OrderStatus status, boolean isAdmin, boolean all) {
        return execute(em -> {
            StringBuilder whereClause = new StringBuilder(" WHERE o.deletedAt IS NULL");

            if (!(isAdmin && all)) {
                whereClause.append(" AND o.user.id = :userId");
            }
            if (status != null) {
                whereClause.append(" AND o.status = :status");
            }

            // 1. Đếm tổng số bản ghi
            String countJpql = "SELECT COUNT(o) FROM Order o" + whereClause;
            TypedQuery<Long> countQuery = em.createQuery(countJpql, Long.class);
            if (!(isAdmin && all)) countQuery.setParameter("userId", userId);
            if (status != null) countQuery.setParameter("status", status);
            long totalItems = countQuery.getSingleResult();

            // 2. Lấy dữ liệu phân trang
            String selectJpql = "SELECT DISTINCT o FROM Order o " +
                    "LEFT JOIN FETCH o.orderItems oi " +
                    "LEFT JOIN FETCH oi.variant v " +
                    "LEFT JOIN FETCH v.product p" +
                    whereClause + " ORDER BY o.createdAt DESC";

            TypedQuery<Order> selectQuery = em.createQuery(selectJpql, Order.class);
            if (!(isAdmin && all)) selectQuery.setParameter("userId", userId);
            if (status != null) selectQuery.setParameter("status", status);

            selectQuery.setFirstResult((page - 1) * limit);
            selectQuery.setMaxResults(limit);
            List<Order> data = selectQuery.getResultList();

            return new PagedResponse<>(data, totalItems, page, limit);
        });
    }

    /**
     * Cập nhật trạng thái đơn (khớp updateStatus bên Node.js)
     */
    public Order updateStatus(int id, Order.OrderStatus status, Integer updatedById, Order.PaymentStatus paymentStatus) {
        return executeTransaction(em -> {
            Order order = em.find(Order.class, id);
            if (order != null) {
                order.setStatus(status);
                order.setUpdatedById(updatedById);
                if (paymentStatus != null) {
                    order.setPaymentStatus(paymentStatus);
                }
                em.merge(order);
            }
            return order;
        });
    }

    /**
     * Hủy đơn hàng (khớp cancelOrder bên Node.js)
     */
    public Order cancelOrder(int id, Order.PaymentStatus paymentStatus, Integer updatedById) {
        return executeTransaction(em -> {
            Order order = em.find(Order.class, id);
            if (order != null) {
                order.setStatus(Order.OrderStatus.CANCELLED);
                order.setPaymentStatus(paymentStatus);
                order.setUpdatedById(updatedById);
                em.merge(order);
            }
            return order;
        });
    }

    /**
     * Thống kê Dashboard cho Admin (khớp getDashboardStats bên Node.js)
     */
    public Map<String, Object> getDashboardStats() {
        return execute(em -> {
            LocalDateTime startOfMonth = YearMonth.now().atDay(1).atStartOfDay();

            Long totalUsers = em.createQuery(
                    "SELECT COUNT(u) FROM User u WHERE u.deletedAt IS NULL", Long.class)
                    .getSingleResult();

            Long totalOrders = em.createQuery(
                    "SELECT COUNT(o) FROM Order o WHERE o.deletedAt IS NULL", Long.class)
                    .getSingleResult();

            Long totalProducts = em.createQuery(
                    "SELECT COUNT(p) FROM Product p WHERE p.deletedAt IS NULL", Long.class)
                    .getSingleResult();

            BigDecimal totalRevenue = em.createQuery(
                    "SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o " +
                    "WHERE o.deletedAt IS NULL AND o.status NOT IN (:cancelled, :returned)", BigDecimal.class)
                    .setParameter("cancelled", Order.OrderStatus.CANCELLED)
                    .setParameter("returned", Order.OrderStatus.RETURNED)
                    .getSingleResult();

            Long newUsersThisMonth = em.createQuery(
                    "SELECT COUNT(u) FROM User u WHERE u.deletedAt IS NULL AND u.createdAt >= :startOfMonth", Long.class)
                    .setParameter("startOfMonth", startOfMonth)
                    .getSingleResult();

            Long ordersPending = em.createQuery(
                    "SELECT COUNT(o) FROM Order o WHERE o.deletedAt IS NULL AND o.status IN (:s1, :s2, :s3)", Long.class)
                    .setParameter("s1", Order.OrderStatus.PENDING_PAYMENT)
                    .setParameter("s2", Order.OrderStatus.PROCESSING)
                    .setParameter("s3", Order.OrderStatus.CONFIRMED)
                    .getSingleResult();

            Map<String, Object> stats = new HashMap<>();
            stats.put("totalUsers", totalUsers);
            stats.put("totalOrders", totalOrders);
            stats.put("totalProducts", totalProducts);
            stats.put("totalRevenue", totalRevenue);
            stats.put("newUsersThisMonth", newUsersThisMonth);
            stats.put("ordersPending", ordersPending);

            return stats;
        });
    }
}