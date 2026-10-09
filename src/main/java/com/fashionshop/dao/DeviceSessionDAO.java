package com.fashionshop.dao;

import com.fashionshop.business.entity.DeviceSession;
import com.fashionshop.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object quản lý vòng đời của {@link DeviceSession} (lưu token, kiểm tra phiên đăng nhập, thu hồi session).
 */
public class DeviceSessionDAO {

    private static final Logger LOGGER = Logger.getLogger(DeviceSessionDAO.class.getName());

    /**
     * Lưu mới một DeviceSession vào cơ sở dữ liệu có bọc Transaction chuẩn.
     *
     * @param session Thực thể DeviceSession cần lưu
     * @return DeviceSession sau khi được persist/merge
     * @throws PersistenceException nếu xảy ra lỗi DB hoặc Transaction
     */
    public DeviceSession saveSession(DeviceSession session) {
        if (session == null) {
            throw new IllegalArgumentException("DeviceSession không được phép là null.");
        }

        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            if (session.getId() == null) {
                em.persist(session);
            } else {
                session.setUpdatedAt(LocalDateTime.now());
                session = em.merge(session);
            }
            tx.commit();
            return session;
        } catch (PersistenceException ex) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            LOGGER.log(Level.SEVERE, "Lỗi PersistenceException khi lưu DeviceSession: " + ex.getMessage(), ex);
            throw ex;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    /**
     * Cập nhật thông tin DeviceSession (ví dụ khi xoay vòng Refresh Token).
     *
     * @param session Thực thể DeviceSession cần cập nhật
     * @return DeviceSession sau khi merge
     */
    public DeviceSession updateSession(DeviceSession session) {
        if (session == null || session.getId() == null) {
            throw new IllegalArgumentException("DeviceSession và ID không được phép là null khi cập nhật.");
        }

        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            session.setUpdatedAt(LocalDateTime.now());
            DeviceSession updated = em.merge(session);
            tx.commit();
            return updated;
        } catch (PersistenceException ex) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            LOGGER.log(Level.SEVERE, "Lỗi PersistenceException khi cập nhật DeviceSession ID " + session.getId() + ": " + ex.getMessage(), ex);
            throw ex;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    /**
     * Tìm kiếm DeviceSession theo Refresh Token (kèm User và Role).
     *
     * @param token Chuỗi Refresh Token
     * @return Optional chứa DeviceSession
     */
    public Optional<DeviceSession> findByRefreshToken(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }

        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<DeviceSession> query = em.createQuery(
                "SELECT s FROM DeviceSession s JOIN FETCH s.user u LEFT JOIN FETCH u.role " +
                "WHERE s.refreshToken = :token",
                DeviceSession.class
            );
            query.setParameter("token", token);
            DeviceSession session = query.getSingleResult();
            return Optional.ofNullable(session);
        } catch (NoResultException ex) {
            return Optional.empty();
        } catch (PersistenceException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi truy vấn DeviceSession theo refresh token: " + ex.getMessage(), ex);
            throw ex;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    /**
     * Tìm kiếm một DeviceSession còn hiệu lực theo refresh token / session token.
     *
     * @param token Chuỗi token phiên
     * @return Optional chứa DeviceSession nếu còn active và chưa hết hạn
     */
    public Optional<DeviceSession> findActiveByToken(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }

        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<DeviceSession> query = em.createQuery(
                "SELECT s FROM DeviceSession s JOIN FETCH s.user u LEFT JOIN FETCH u.role " +
                "WHERE s.refreshToken = :token AND s.isActive = true AND s.expiresAt > :now",
                DeviceSession.class
            );
            query.setParameter("token", token);
            query.setParameter("now", LocalDateTime.now());
            DeviceSession session = query.getSingleResult();
            return Optional.ofNullable(session);
        } catch (NoResultException ex) {
            return Optional.empty();
        } catch (PersistenceException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi truy vấn DeviceSession theo token: " + ex.getMessage(), ex);
            throw ex;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    /**
     * Kiểm tra nhanh trạng thái hoạt động của phiên đăng nhập (tối ưu hiệu năng bằng COUNT index query).
     *
     * @param token Chuỗi token phiên
     * @return true nếu phiên còn hiệu lực và chưa bị thu hồi
     */
    public boolean isSessionActive(String token) {
        if (token == null || token.isBlank()) {
            return false;
        }

        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(s) FROM DeviceSession s " +
                "WHERE s.refreshToken = :token AND s.isActive = true AND s.expiresAt > :now",
                Long.class
            );
            query.setParameter("token", token);
            query.setParameter("now", LocalDateTime.now());
            Long count = query.getSingleResult();
            return count != null && count > 0;
        } catch (PersistenceException ex) {
            LOGGER.log(Level.WARNING, "Lỗi kiểm tra session active: " + ex.getMessage(), ex);
            return false;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    /**
     * Vô hiệu hóa (revoke) một DeviceSession dựa trên token (đăng xuất).
     *
     * @param token Chuỗi token của phiên cần thu hồi
     */
    public void revokeSession(String token) {
        if (token == null || token.isBlank()) {
            return;
        }

        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            TypedQuery<DeviceSession> query = em.createQuery(
                "SELECT s FROM DeviceSession s WHERE s.refreshToken = :token AND s.isActive = true",
                DeviceSession.class
            );
            query.setParameter("token", token);
            for (DeviceSession s : query.getResultList()) {
                s.revokeSession();
                s.setUpdatedAt(LocalDateTime.now());
                em.merge(s);
            }
            tx.commit();
        } catch (PersistenceException ex) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            LOGGER.log(Level.SEVERE, "Lỗi thu hồi DeviceSession: " + ex.getMessage(), ex);
            throw ex;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
}
