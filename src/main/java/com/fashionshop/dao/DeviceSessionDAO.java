package com.fashionshop.dao;

import com.fashionshop.business.entity.DeviceSession;
import com.fashionshop.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;

import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object quản lý vòng đời của {@link DeviceSession} trong cơ sở dữ liệu.
 */
public class DeviceSessionDAO {

    private static final Logger LOGGER = Logger.getLogger(DeviceSessionDAO.class.getName());

    /**
     * Ghi session mới hoặc cập nhật session vào DB có bọc EntityTransaction.
     *
     * @param session Thực thể DeviceSession cần lưu
     * @throws PersistenceException khi xảy ra lỗi Transaction hoặc DB constraint
     */
    public void saveSession(DeviceSession session) {
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
                em.merge(session);
            }
            tx.commit();
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
     * Tìm session đang active dựa trên refresh token (kiểm tra isRevoked == false và expiresAt > NOW()).
     *
     * @param token Chuỗi Refresh Token
     * @return DeviceSession nếu tìm thấy và còn hiệu lực, ngược lại trả về null
     */
    public DeviceSession findByRefreshToken(String token) {
        if (token == null || token.isBlank()) {
            return null;
        }

        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<DeviceSession> query = em.createQuery(
                "SELECT s FROM DeviceSession s JOIN FETCH s.user u LEFT JOIN FETCH u.role " +
                "WHERE s.refreshToken = :token AND s.isRevoked = false AND s.expiresAt > :now",
                DeviceSession.class
            );
            query.setParameter("token", token.trim());
            query.setParameter("now", LocalDateTime.now());
            List<DeviceSession> results = query.getResultList();
            return results.isEmpty() ? null : results.get(0);
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
     * Đánh dấu isRevoked = true cho một session cụ thể dựa trên token.
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
                "SELECT s FROM DeviceSession s WHERE s.refreshToken = :token AND s.isRevoked = false",
                DeviceSession.class
            );
            query.setParameter("token", token.trim());
            for (DeviceSession s : query.getResultList()) {
                s.revokeSession();
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

    /**
     * Đánh dấu isRevoked = true cho TOÀN BỘ session của user (dùng khi User đổi mật khẩu).
     *
     * @param userId ID của người dùng
     */
    public void revokeAllSessions(int userId) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.createQuery(
                "UPDATE DeviceSession s SET s.isRevoked = true, s.updatedAt = :now " +
                "WHERE s.user.id = :userId AND s.isRevoked = false"
            )
            .setParameter("now", LocalDateTime.now())
            .setParameter("userId", userId)
            .executeUpdate();
            tx.commit();
            LOGGER.info("Đã thu hồi toàn bộ session của User ID: " + userId);
        } catch (PersistenceException ex) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            LOGGER.log(Level.SEVERE, "Lỗi thu hồi toàn bộ session cho User ID: " + userId, ex);
            throw ex;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    /**
     * Cập nhật thông tin phiên đăng nhập (phục vụ xoay vòng Refresh Token).
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
     * Kiểm tra nhanh trạng thái hoạt động của token (sử dụng query index COUNT tối ưu).
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
                "WHERE s.refreshToken = :token AND s.isRevoked = false AND s.expiresAt > :now",
                Long.class
            );
            query.setParameter("token", token.trim());
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
}
