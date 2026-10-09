package com.fashionshop.dao;

import com.fashionshop.business.entity.Role;
import com.fashionshop.business.entity.User;
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
 * Data Access Object phụ trách các thao tác dữ liệu liên quan đến {@link User} và {@link Role}.
 */
public class UserDAO {

    private static final Logger LOGGER = Logger.getLogger(UserDAO.class.getName());

    /**
     * Tìm kiếm User theo địa chỉ email (không phân biệt hoa thường).
     *
     * @param email Email của User cần tìm
     * @return Optional chứa User nếu tìm thấy, ngược lại Optional.empty()
     */
    public Optional<User> findByEmail(String email) {
        if (email == null || email.isBlank()) {
            return Optional.empty();
        }

        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<User> query = em.createQuery(
                "SELECT u FROM User u LEFT JOIN FETCH u.role WHERE LOWER(TRIM(u.email)) = LOWER(TRIM(:email))",
                User.class
            );
            query.setParameter("email", email);
            User user = query.getSingleResult();
            return Optional.ofNullable(user);
        } catch (NoResultException ex) {
            return Optional.empty();
        } catch (PersistenceException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi truy vấn User theo email: " + email, ex);
            throw ex;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    /**
     * Tìm kiếm User theo ID (sử dụng FETCH JOIN để eager load Role).
     *
     * @param id ID của User cần tìm
     * @return Optional chứa User nếu tìm thấy, ngược lại Optional.empty()
     */
    public Optional<User> findById(int id) {
        return findById(Integer.valueOf(id));
    }

    /**
     * Tìm kiếm User theo ID dạng đối tượng Integer (sử dụng FETCH JOIN để eager load Role).
     *
     * @param id ID của User cần tìm
     * @return Optional chứa User nếu tìm thấy, ngược lại Optional.empty()
     */
    public Optional<User> findById(Integer id) {
        if (id == null) {
            return Optional.empty();
        }

        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<User> query = em.createQuery(
                "SELECT u FROM User u LEFT JOIN FETCH u.role WHERE u.id = :id",
                User.class
            );
            query.setParameter("id", id);
            User user = query.getSingleResult();
            return Optional.ofNullable(user);
        } catch (NoResultException ex) {
            return Optional.empty();
        } catch (PersistenceException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi truy vấn User theo ID: " + id, ex);
            throw ex;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    /**
     * Cập nhật thông tin User vào Database có bọc EntityTransaction.
     *
     * @param user Thực thể User cần cập nhật
     * @return User sau khi merge thành công
     * @throws PersistenceException khi xảy ra lỗi Transaction hoặc vi phạm ràng buộc DB
     */
    public User update(User user) {
        if (user == null || user.getId() == null) {
            throw new IllegalArgumentException("User và User ID không được phép là null khi cập nhật.");
        }

        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            user.setUpdatedAt(LocalDateTime.now());
            User updated = em.merge(user);
            tx.commit();
            return updated;
        } catch (PersistenceException ex) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            LOGGER.log(Level.SEVERE, "Lỗi PersistenceException khi cập nhật User ID " + user.getId() + ": " + ex.getMessage(), ex);
            throw ex;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    /**
     * Lưu mới hoặc cập nhật thực thể User vào Database có bọc Transaction (begin, commit, rollback).
     *
     * @param user Đối tượng User cần lưu
     * @return User sau khi lưu thành công
     * @throws PersistenceException khi xảy ra lỗi Transaction hoặc vi phạm ràng buộc DB
     */
    public User saveUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User không được phép là null.");
        }

        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            if (user.getId() == null) {
                em.persist(user);
            } else {
                user = em.merge(user);
            }
            tx.commit();
            return user;
        } catch (PersistenceException ex) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            LOGGER.log(Level.SEVERE, "Lỗi PersistenceException khi lưu User [email=" + user.getEmail() + "]: " + ex.getMessage(), ex);
            throw ex;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    /**
     * Tìm kiếm Role theo mã code (vd: CUSTOMER, ADMIN).
     *
     * @param code Mã Role
     * @return Optional<Role>
     */
    public Optional<Role> findRoleByCode(String code) {
        if (code == null || code.isBlank()) {
            return Optional.empty();
        }

        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Role> query = em.createQuery(
                "SELECT r FROM Role r WHERE UPPER(TRIM(r.code)) = UPPER(TRIM(:code))",
                Role.class
            );
            query.setParameter("code", code);
            Role role = query.getSingleResult();
            return Optional.ofNullable(role);
        } catch (NoResultException ex) {
            return Optional.empty();
        } catch (PersistenceException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi truy vấn Role theo code: " + code, ex);
            throw ex;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    /**
     * Lấy Role theo code hoặc tự động tạo mới Role mặc định nếu Database chưa được seed dữ liệu.
     *
     * @param code Mã định danh Role (vd: CUSTOMER)
     * @param defaultName Tên hiển thị Role (vd: Khách hàng)
     * @return Role hợp lệ
     */
    public Role getOrCreateRole(String code, String defaultName) {
        return findRoleByCode(code).orElseGet(() -> {
            EntityManager em = JPAUtil.getEntityManager();
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                Role newRole = new Role();
                newRole.setCode(code.toUpperCase().trim());
                newRole.setName(defaultName);
                newRole.setIsActive(true);
                em.persist(newRole);
                tx.commit();
                return newRole;
            } catch (PersistenceException ex) {
                if (tx != null && tx.isActive()) {
                    tx.rollback();
                }
                LOGGER.log(Level.SEVERE, "Lỗi PersistenceException khi tạo Role mặc định: " + code, ex);
                throw ex;
            } finally {
                if (em != null && em.isOpen()) {
                    em.close();
                }
            }
        });
    }
}
