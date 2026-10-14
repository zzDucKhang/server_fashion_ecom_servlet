package com.fashionshop.dataaccess.dao.identity;

import com.fashionshop.business.entity.DeviceSession;
import com.fashionshop.dataaccess.base.AbstractJpaDAO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class DeviceSessionDAO extends AbstractJpaDAO<DeviceSession, Integer> {

    public DeviceSessionDAO() {
        super(DeviceSession.class);
    }

    public Optional<DeviceSession> findByRefreshToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            return Optional.empty();
        }
        return execute(em -> {
            List<DeviceSession> list = em.createQuery(
                            "SELECT DISTINCT s FROM DeviceSession s " +
                                    "JOIN FETCH s.user u " +
                                    "JOIN FETCH u.role r " +
                                    "LEFT JOIN FETCH r.permissions " +
                                    "WHERE s.refreshToken = :token", DeviceSession.class)
                    .setParameter("token", refreshToken)
                    .setMaxResults(1)
                    .getResultList();
            return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
        });
    }

    public List<DeviceSession> findActiveByUserId(Integer userId) {
        if (userId == null) {
            return List.of();
        }
        return execute(em -> em.createQuery(
                        "SELECT s FROM DeviceSession s " +
                                "WHERE s.user.id = :userId AND s.isActive = true AND s.expiresAt > :now " +
                                "ORDER BY s.createdAt DESC", DeviceSession.class)
                .setParameter("userId", userId)
                .setParameter("now", LocalDateTime.now())
                .getResultList());
    }

    public void deactivateAllByUserId(Integer userId) {
        if (userId == null) {
            return;
        }
        executeTransaction(em -> {
            em.createQuery(
                            "UPDATE DeviceSession s SET s.isActive = false, s.updatedAt = :now " +
                                    "WHERE s.user.id = :userId AND s.isActive = true")
                    .setParameter("now", LocalDateTime.now())
                    .setParameter("userId", userId)
                    .executeUpdate();
            return null;
        });
    }

    public void deactivateByRefreshToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            return;
        }
        executeTransaction(em -> {
            em.createQuery(
                            "UPDATE DeviceSession s SET s.isActive = false, s.updatedAt = :now " +
                                    "WHERE s.refreshToken = :token")
                    .setParameter("now", LocalDateTime.now())
                    .setParameter("token", refreshToken)
                    .executeUpdate();
            return null;
        });
    }

    public void deleteExpired() {
        executeTransaction(em -> {
            em.createQuery("DELETE FROM DeviceSession s WHERE s.expiresAt < :now OR s.isActive = false")
                    .setParameter("now", LocalDateTime.now())
                    .executeUpdate();
            return null;
        });
    }
}
