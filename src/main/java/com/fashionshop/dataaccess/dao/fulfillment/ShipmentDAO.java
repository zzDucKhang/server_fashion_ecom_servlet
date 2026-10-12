package com.fashionshop.dataaccess.dao.fulfillment;

import com.fashionshop.business.entity.Order;
import com.fashionshop.business.entity.Shipment;
import com.fashionshop.common.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class ShipmentDAO {

    public Shipment findByOrderId(Integer orderId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT s FROM Shipment s LEFT JOIN FETCH s.order o LEFT JOIN FETCH o.user WHERE s.order.id = :orderId",
                    Shipment.class)
                    .setParameter("orderId", orderId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    public Shipment findById(Integer id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT s FROM Shipment s LEFT JOIN FETCH s.order o LEFT JOIN FETCH o.user WHERE s.id = :id",
                    Shipment.class)
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    public Shipment create(Shipment shipment) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(shipment);
            em.getTransaction().commit();
            return shipment;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public void updateStatus(Integer shipmentId, Shipment.ShipmentStatus status, String trackingCode) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Shipment shipment = em.find(Shipment.class, shipmentId);
            if (shipment != null) {
                shipment.setStatus(status);
                if (trackingCode != null && !trackingCode.isBlank()) {
                    shipment.setTrackingCode(trackingCode);
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

    public List<Shipment> findAll(int offset, int limit, String status) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT s FROM Shipment s";
            if (status != null && !status.isBlank()) {
                jpql += " WHERE s.status = :status";
            }
            jpql += " ORDER BY s.createdAt DESC";

            TypedQuery<Shipment> query = em.createQuery(jpql, Shipment.class)
                    .setFirstResult(offset)
                    .setMaxResults(limit);

            if (status != null && !status.isBlank()) {
                query.setParameter("status", Shipment.ShipmentStatus.valueOf(status.trim().toUpperCase()));
            }

            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public long countAll(String status) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT COUNT(s) FROM Shipment s";
            if (status != null && !status.isBlank()) {
                jpql += " WHERE s.status = :status";
            }

            TypedQuery<Long> query = em.createQuery(jpql, Long.class);
            if (status != null && !status.isBlank()) {
                query.setParameter("status", Shipment.ShipmentStatus.valueOf(status.trim().toUpperCase()));
            }

            return query.getSingleResult();
        } finally {
            em.close();
        }
    }

    public void delete(Integer id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Shipment s = em.find(Shipment.class, id);
            if (s != null) {
                em.remove(s);
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

