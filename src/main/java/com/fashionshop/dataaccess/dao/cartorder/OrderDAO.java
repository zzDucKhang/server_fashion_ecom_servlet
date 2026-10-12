package com.fashionshop.dataaccess.dao.cartorder;

import com.fashionshop.business.entity.Order;
import com.fashionshop.common.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.util.Collections;
import java.util.List;

public class OrderDAO {

    public Order findByIdWithItems(Integer orderId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT o FROM Order o LEFT JOIN FETCH o.orderItems WHERE o.id = :id",
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
            String idJpql = "SELECT o.id FROM Order o WHERE o.user.id = :userId";
            if (status != null && !status.isBlank()) {
                idJpql += " AND o.status = :status";
            }
            idJpql += " ORDER BY o.orderDate DESC";

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
                    "SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.orderItems WHERE o.id IN :ids ORDER BY o.orderDate DESC",
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
            String jpql = "SELECT COUNT(o) FROM Order o WHERE o.user.id = :userId";
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
            String idJpql = "SELECT o.id FROM Order o";
            if (status != null && !status.isBlank()) {
                idJpql += " WHERE o.status = :status";
            }
            idJpql += " ORDER BY o.orderDate DESC";

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
                    "SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.orderItems WHERE o.id IN :ids ORDER BY o.orderDate DESC",
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
            String jpql = "SELECT COUNT(o) FROM Order o";
            if (status != null && !status.isBlank()) {
                jpql += " WHERE o.status = :status";
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

