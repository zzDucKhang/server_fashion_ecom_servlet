package com.fashionshop.dataaccess.dao.fulfillment;

import com.fashionshop.business.entity.Order;
import com.fashionshop.business.entity.PaymentTransaction;
import com.fashionshop.common.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

public class PaymentTransactionDAO {

    public PaymentTransaction findPendingByOrderId(Integer orderId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT p FROM PaymentTransaction p WHERE p.order.id = :orderId AND p.status = 'PENDING' AND p.provider = 'SEPAY'",
                            PaymentTransaction.class)
                    .setParameter("orderId", orderId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    public PaymentTransaction findByOrderId(Integer orderId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT p FROM PaymentTransaction p WHERE p.order.id = :orderId",
                            PaymentTransaction.class)
                    .setParameter("orderId", orderId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    public PaymentTransaction create(PaymentTransaction payment) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(payment);
            em.getTransaction().commit();
            return payment;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public PaymentTransaction update(PaymentTransaction payment) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            PaymentTransaction merged = em.merge(payment);
            em.getTransaction().commit();
            return merged;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public void markSuccess(Integer paymentId, String providerTransId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            PaymentTransaction payment = em.find(PaymentTransaction.class, paymentId);
            if (payment != null) {
                payment.setStatus(Order.PaymentStatus.SUCCESS);
                payment.setProviderTransId(providerTransId);

                Order order = payment.getOrder();
                if (order != null) {
                    order.setPaymentStatus(Order.PaymentStatus.SUCCESS);
                    order.setStatus(Order.OrderStatus.CONFIRMED);
                }
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}