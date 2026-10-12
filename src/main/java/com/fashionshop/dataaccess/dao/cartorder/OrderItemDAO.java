package com.fashionshop.dataaccess.dao.cartorder;

import com.fashionshop.business.entity.OrderItem;
import com.fashionshop.dataaccess.base.AbstractJpaDAO;

import java.util.List;
import java.util.Optional;

public class OrderItemDAO extends AbstractJpaDAO<OrderItem, Integer> {

    public OrderItemDAO() {
        super(OrderItem.class);
    }

    public List<OrderItem> findByOrderId(int orderId) {
        return execute(em -> em.createQuery(
                "SELECT DISTINCT oi FROM OrderItem oi " +
                "LEFT JOIN FETCH oi.variant v " +
                "LEFT JOIN FETCH v.product p " +
                "WHERE oi.order.id = :orderId AND oi.deletedAt IS NULL", OrderItem.class)
                .setParameter("orderId", orderId)
                .getResultList());
    }

    public Optional<OrderItem> findDetailById(int id) {
        return execute(em -> {
            var list = em.createQuery(
                    "SELECT oi FROM OrderItem oi " +
                    "LEFT JOIN FETCH oi.order o " +
                    "LEFT JOIN FETCH oi.variant v " +
                    "WHERE oi.id = :id AND oi.deletedAt IS NULL", OrderItem.class)
                    .setParameter("id", id)
                    .setMaxResults(1)
                    .getResultList();
            return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
        });
    }
}