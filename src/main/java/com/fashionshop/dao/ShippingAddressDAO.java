package com.fashionshop.dao;

import com.fashionshop.business.entity.ShippingAddress;
import com.fashionshop.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object quản lý dữ liệu Sổ địa chỉ giao hàng ({@link ShippingAddress}).
 */
public class ShippingAddressDAO {

    private static final Logger LOGGER = Logger.getLogger(ShippingAddressDAO.class.getName());

    /**
     * Lấy danh sách địa chỉ giao hàng của 1 user (sắp xếp địa chỉ mặc định lên đầu).
     *
     * @param userId ID của người dùng
     * @return Danh sách địa chỉ giao hàng
     */
    public List<ShippingAddress> findByUserId(int userId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<ShippingAddress> query = em.createQuery(
                "SELECT a FROM ShippingAddress a " +
                "WHERE a.user.id = :userId " +
                "ORDER BY a.isDefault DESC, a.createdAt DESC",
                ShippingAddress.class
            );
            query.setParameter("userId", userId);
            return query.getResultList();
        } catch (PersistenceException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi truy vấn danh sách địa chỉ của User ID " + userId + ": " + ex.getMessage(), ex);
            throw ex;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    /**
     * Tìm địa chỉ theo ID nhưng bắt buộc phải thuộc về đúng user đang request (chống lỗi bảo mật IDOR).
     *
     * @param addressId ID của địa chỉ
     * @param userId ID của người dùng đang request
     * @return Optional chứa ShippingAddress nếu tìm thấy và thuộc về user, ngược lại Optional.empty()
     */
    public Optional<ShippingAddress> findByIdAndUserId(int addressId, int userId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<ShippingAddress> query = em.createQuery(
                "SELECT a FROM ShippingAddress a " +
                "WHERE a.id = :addressId AND a.user.id = :userId",
                ShippingAddress.class
            );
            query.setParameter("addressId", addressId);
            query.setParameter("userId", userId);
            ShippingAddress address = query.getSingleResult();
            return Optional.ofNullable(address);
        } catch (NoResultException ex) {
            return Optional.empty();
        } catch (PersistenceException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi truy vấn địa chỉ ID " + addressId + " của User ID " + userId + ": " + ex.getMessage(), ex);
            throw ex;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    /**
     * Thêm mới địa chỉ giao hàng vào Database có bọc EntityTransaction.
     *
     * @param address Đối tượng ShippingAddress cần lưu
     * @return ShippingAddress sau khi persist thành công
     */
    public ShippingAddress save(ShippingAddress address) {
        if (address == null) {
            throw new IllegalArgumentException("ShippingAddress không được phép là null.");
        }

        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            if (address.getCreatedAt() == null) {
                address.setCreatedAt(LocalDateTime.now());
            }
            address.setUpdatedAt(LocalDateTime.now());
            if (address.getId() == null) {
                em.persist(address);
            } else {
                address = em.merge(address);
            }
            tx.commit();
            return address;
        } catch (PersistenceException ex) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            LOGGER.log(Level.SEVERE, "Lỗi PersistenceException khi lưu ShippingAddress: " + ex.getMessage(), ex);
            throw ex;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    /**
     * Cập nhật thông tin địa chỉ giao hàng vào Database có bọc EntityTransaction.
     *
     * @param address Đối tượng ShippingAddress cần cập nhật
     * @return ShippingAddress sau khi merge
     */
    public ShippingAddress update(ShippingAddress address) {
        if (address == null || address.getId() == null) {
            throw new IllegalArgumentException("ShippingAddress và ID không được phép là null khi cập nhật.");
        }

        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            address.setUpdatedAt(LocalDateTime.now());
            ShippingAddress updated = em.merge(address);
            tx.commit();
            return updated;
        } catch (PersistenceException ex) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            LOGGER.log(Level.SEVERE, "Lỗi PersistenceException khi cập nhật ShippingAddress ID " + address.getId() + ": " + ex.getMessage(), ex);
            throw ex;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    /**
     * Xóa một địa chỉ giao hàng khỏi Database có bọc EntityTransaction.
     *
     * @param address Đối tượng ShippingAddress cần xóa
     */
    public void delete(ShippingAddress address) {
        if (address == null || address.getId() == null) {
            return;
        }

        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            ShippingAddress managed = em.find(ShippingAddress.class, address.getId());
            if (managed != null) {
                em.remove(managed);
            }
            tx.commit();
        } catch (PersistenceException ex) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            LOGGER.log(Level.SEVERE, "Lỗi PersistenceException khi xóa ShippingAddress ID " + address.getId() + ": " + ex.getMessage(), ex);
            throw ex;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    /**
     * Xóa một địa chỉ giao hàng theo ID và User ID.
     *
     * @param addressId ID của địa chỉ cần xóa
     * @param userId ID của người dùng sở hữu địa chỉ
     * @return true nếu xóa thành công ít nhất 1 bản ghi
     */
    public boolean deleteByIdAndUserId(int addressId, int userId) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            int deletedCount = em.createQuery(
                "DELETE FROM ShippingAddress a WHERE a.id = :addressId AND a.user.id = :userId"
            )
            .setParameter("addressId", addressId)
            .setParameter("userId", userId)
            .executeUpdate();
            tx.commit();
            return deletedCount > 0;
        } catch (PersistenceException ex) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            LOGGER.log(Level.SEVERE, "Lỗi khi xóa địa chỉ ID " + addressId + " của User ID " + userId + ": " + ex.getMessage(), ex);
            throw ex;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    /**
     * Set isDefault = false cho toàn bộ địa chỉ của một user.
     * Thường dùng trước khi đặt một địa chỉ mới làm mặc định.
     *
     * @param userId ID của người dùng
     */
    public void resetDefaultAddress(int userId) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.createQuery(
                "UPDATE ShippingAddress a SET a.isDefault = false, a.updatedAt = :now " +
                "WHERE a.user.id = :userId AND a.isDefault = true"
            )
            .setParameter("now", LocalDateTime.now())
            .setParameter("userId", userId)
            .executeUpdate();
            tx.commit();
        } catch (PersistenceException ex) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            LOGGER.log(Level.SEVERE, "Lỗi resetDefaultAddress cho User ID " + userId + ": " + ex.getMessage(), ex);
            throw ex;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
}
