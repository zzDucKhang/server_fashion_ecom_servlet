package com.fashionshop.dataaccess.dao.cartorder;

import com.fashionshop.business.entity.Order;
import com.fashionshop.common.util.JPAUtil;
import com.fashionshop.dataaccess.base.AbstractJpaDAO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class OrderDAO extends AbstractJpaDAO<Order, Integer> {

    public OrderDAO() {
        super(Order.class);
    }

    @Override
    public Optional<Order> findById(Integer id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(findByIdWithItems(id));
    }

    public Order findByIdWithItems(Integer orderId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT DISTINCT o FROM Order o " +
                    "LEFT JOIN FETCH o.user " +
                    "LEFT JOIN FETCH o.shipment " +
                    "LEFT JOIN FETCH o.paymentTransaction " +
                    "LEFT JOIN FETCH o.orderItems oi " +
                    "LEFT JOIN FETCH oi.variant v " +
                    "LEFT JOIN FETCH v.product " +
                    "WHERE o.id = :id AND o.deletedAt IS NULL",
                    Order.class)
                    .setParameter("id", orderId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    public List<Order> findByUserId(Integer userId, int offset, int limit, String status) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String idJpql = "SELECT o.id FROM Order o WHERE o.user.id = :userId AND o.deletedAt IS NULL";
            if (status != null && !status.isBlank()) {
                idJpql += " AND o.status = :status";
            }
            idJpql += " ORDER BY o.createdAt DESC";

            TypedQuery<Integer> idQuery = em.createQuery(idJpql, Integer.class)
                    .setParameter("userId", userId)
                    .setFirstResult(offset)
                    .setMaxResults(limit);

            if (status != null && !status.isBlank()) {
                idQuery.setParameter("status", Order.OrderStatus.valueOf(status.trim().toUpperCase()));
            }

            List<Integer> ids = idQuery.getResultList();
            if (ids.isEmpty()) {
                return Collections.emptyList();
            }

            return em.createQuery(
                    "SELECT DISTINCT o FROM Order o " +
                    "LEFT JOIN FETCH o.user " +
                    "LEFT JOIN FETCH o.shipment " +
                    "LEFT JOIN FETCH o.paymentTransaction " +
                    "LEFT JOIN FETCH o.orderItems oi " +
                    "LEFT JOIN FETCH oi.variant v " +
                    "LEFT JOIN FETCH v.product " +
                    "WHERE o.id IN :ids ORDER BY o.createdAt DESC",
                    Order.class)
                    .setParameter("ids", ids)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public long countByUserId(Integer userId, String status) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT COUNT(o) FROM Order o WHERE o.user.id = :userId AND o.deletedAt IS NULL";
            if (status != null && !status.isBlank()) {
                jpql += " AND o.status = :status";
            }

            TypedQuery<Long> query = em.createQuery(jpql, Long.class)
                    .setParameter("userId", userId);

            if (status != null && !status.isBlank()) {
                query.setParameter("status", Order.OrderStatus.valueOf(status.trim().toUpperCase()));
            }

            return query.getSingleResult();
        } finally {
            em.close();
        }
    }

    public List<Order> findAll(int offset, int limit, String status) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String idJpql = "SELECT o.id FROM Order o WHERE o.deletedAt IS NULL";
            if (status != null && !status.isBlank()) {
                idJpql += " AND o.status = :status";
            }
            idJpql += " ORDER BY o.createdAt DESC";

            TypedQuery<Integer> idQuery = em.createQuery(idJpql, Integer.class)
                    .setFirstResult(offset)
                    .setMaxResults(limit);

            if (status != null && !status.isBlank()) {
                idQuery.setParameter("status", Order.OrderStatus.valueOf(status.trim().toUpperCase()));
            }

            List<Integer> ids = idQuery.getResultList();
            if (ids.isEmpty()) {
                return Collections.emptyList();
            }

            return em.createQuery(
                    "SELECT DISTINCT o FROM Order o " +
                    "LEFT JOIN FETCH o.user " +
                    "LEFT JOIN FETCH o.shipment " +
                    "LEFT JOIN FETCH o.paymentTransaction " +
                    "LEFT JOIN FETCH o.orderItems oi " +
                    "LEFT JOIN FETCH oi.variant v " +
                    "LEFT JOIN FETCH v.product " +
                    "WHERE o.id IN :ids ORDER BY o.createdAt DESC",
                    Order.class)
                    .setParameter("ids", ids)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public long countAll(String status) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT COUNT(o) FROM Order o WHERE o.deletedAt IS NULL";
            if (status != null && !status.isBlank()) {
                jpql += " AND o.status = :status";
            }

            TypedQuery<Long> query = em.createQuery(jpql, Long.class);
            if (status != null && !status.isBlank()) {
                query.setParameter("status", Order.OrderStatus.valueOf(status.trim().toUpperCase()));
            }

            return query.getSingleResult();
        } finally {
            em.close();
        }
    }
}