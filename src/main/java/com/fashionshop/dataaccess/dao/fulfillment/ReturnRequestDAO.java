package com.fashionshop.dataaccess.dao.fulfillment;

import com.fashionshop.business.entity.ReturnRequest;
import com.fashionshop.common.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.util.Collections;
import java.util.List;

public class ReturnRequestDAO {

    public ReturnRequest findByIdWithMedias(Integer id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT r FROM ReturnRequest r LEFT JOIN FETCH r.medias WHERE r.id = :id",
                    ReturnRequest.class)
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    public ReturnRequest create(ReturnRequest returnRequest) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(returnRequest);
            em.getTransaction().commit();
            return returnRequest;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public void updateStatus(Integer returnId, ReturnRequest.ReturnStatus status) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            ReturnRequest rr = em.find(ReturnRequest.class, returnId);
            if (rr != null) {
                rr.setStatus(status);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public List<ReturnRequest> findByUserId(Integer userId, int offset, int limit, String status) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String idJpql = "SELECT r.id FROM ReturnRequest r WHERE r.orderItem.order.user.id = :userId";
            if (status != null && !status.isBlank()) {
                idJpql += " AND r.status = :status";
            }
            idJpql += " ORDER BY r.createdAt DESC";

            TypedQuery<Integer> idQuery = em.createQuery(idJpql, Integer.class)
                    .setParameter("userId", userId)
                    .setFirstResult(offset)
                    .setMaxResults(limit);

            if (status != null && !status.isBlank()) {
                idQuery.setParameter("status", ReturnRequest.ReturnStatus.valueOf(status.trim().toUpperCase()));
            }

            List<Integer> ids = idQuery.getResultList();
            if (ids.isEmpty()) {
                return Collections.emptyList();
            }

            return em.createQuery(
                    "SELECT DISTINCT r FROM ReturnRequest r LEFT JOIN FETCH r.medias WHERE r.id IN :ids ORDER BY r.createdAt DESC",
                    ReturnRequest.class)
                    .setParameter("ids", ids)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public long countByUserId(Integer userId, String status) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT COUNT(r) FROM ReturnRequest r WHERE r.orderItem.order.user.id = :userId";
            if (status != null && !status.isBlank()) {
                jpql += " AND r.status = :status";
            }

            TypedQuery<Long> query = em.createQuery(jpql, Long.class)
                    .setParameter("userId", userId);

            if (status != null && !status.isBlank()) {
                query.setParameter("status", ReturnRequest.ReturnStatus.valueOf(status.trim().toUpperCase()));
            }

            return query.getSingleResult();
        } finally {
            em.close();
        }
    }

    public List<ReturnRequest> findAll(int offset, int limit, String status) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String idJpql = "SELECT r.id FROM ReturnRequest r";
            if (status != null && !status.isBlank()) {
                idJpql += " AND r.status = :status";
            }
            idJpql += " ORDER BY r.createdAt DESC";

            TypedQuery<Integer> idQuery = em.createQuery(idJpql, Integer.class)
                    .setFirstResult(offset)
                    .setMaxResults(limit);

            if (status != null && !status.isBlank()) {
                idQuery.setParameter("status", ReturnRequest.ReturnStatus.valueOf(status.trim().toUpperCase()));
            }

            List<Integer> ids = idQuery.getResultList();
            if (ids.isEmpty()) {
                return Collections.emptyList();
            }

            return em.createQuery(
                    "SELECT DISTINCT r FROM ReturnRequest r LEFT JOIN FETCH r.medias WHERE r.id IN :ids ORDER BY r.createdAt DESC",
                    ReturnRequest.class)
                    .setParameter("ids", ids)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public long countAll(String status) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT COUNT(r) FROM ReturnRequest r";
            if (status != null && !status.isBlank()) {
                jpql += " WHERE r.status = :status";
            }

            TypedQuery<Long> query = em.createQuery(jpql, Long.class);
            if (status != null && !status.isBlank()) {
                query.setParameter("status", ReturnRequest.ReturnStatus.valueOf(status.trim().toUpperCase()));
            }

            return query.getSingleResult();
        } finally {
            em.close();
        }
    }
}

