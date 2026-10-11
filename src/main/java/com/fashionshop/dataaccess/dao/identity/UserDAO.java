package com.fashionshop.dataaccess.dao.identity;

import com.fashionshop.business.entity.DeviceSession;
import com.fashionshop.business.entity.User;
import com.fashionshop.common.response.PagedResponse;
import com.fashionshop.dataaccess.base.AbstractJpaDAO;
import jakarta.persistence.TypedQuery;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class UserDAO extends AbstractJpaDAO<User, Integer> {

    public UserDAO() {
        super(User.class);
    }

    public Optional<User> findByEmail(String email) {
        if (email == null) return Optional.empty();
        return execute(em -> {
            var list = em.createQuery(
                    "SELECT u FROM User u " +
                    "LEFT JOIN FETCH u.role " +
                    "WHERE LOWER(u.email) = LOWER(:email) AND u.deletedAt IS NULL", User.class)
                    .setParameter("email", email.trim())
                    .setMaxResults(1)
                    .getResultList();
            return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
        });
    }

    /**
     * Lấy chi tiết User kèm Role và danh sách địa chỉ giao hàng (khớp findById bên Node.js)
     */
    public Optional<User> findDetailById(int id) {
        return execute(em -> {
            var list = em.createQuery(
                    "SELECT DISTINCT u FROM User u " +
                    "LEFT JOIN FETCH u.role " +
                    "LEFT JOIN FETCH u.shippingAddresses " +
                    "WHERE u.id = :id AND u.deletedAt IS NULL", User.class)
                    .setParameter("id", id)
                    .setMaxResults(1)
                    .getResultList();
            return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
        });
    }

    /**
     * Danh sách User phân trang & tìm kiếm đa năng (khớp list() bên Node.js)
     */
    public PagedResponse<User> list(int page, int limit, String search, User.UserStatus status, Integer roleId) {
        return execute(em -> {
            StringBuilder whereClause = new StringBuilder(" WHERE u.deletedAt IS NULL");

            if (status != null) {
                whereClause.append(" AND u.status = :status");
            }
            if (roleId != null) {
                whereClause.append(" AND u.role.id = :roleId");
            }
            if (search != null && !search.isBlank()) {
                whereClause.append(" AND (LOWER(u.email) LIKE LOWER(:search) OR LOWER(u.fullName) LIKE LOWER(:search))");
            }

            // 1. Count
            String countJpql = "SELECT COUNT(u) FROM User u" + whereClause;
            TypedQuery<Long> countQuery = em.createQuery(countJpql, Long.class);
            bindListParams(countQuery, status, roleId, search);
            long totalItems = countQuery.getSingleResult();

            // 2. Select
            String selectJpql = "SELECT u FROM User u LEFT JOIN FETCH u.role" + whereClause + " ORDER BY u.createdAt DESC";
            TypedQuery<User> selectQuery = em.createQuery(selectJpql, User.class);
            bindListParams(selectQuery, status, roleId, search);

            selectQuery.setFirstResult((page - 1) * limit);
            selectQuery.setMaxResults(limit);
            List<User> data = selectQuery.getResultList();

            return new PagedResponse<>(data, totalItems, page, limit);
        });
    }

    private void bindListParams(TypedQuery<?> query, User.UserStatus status, Integer roleId, String search) {
        if (status != null) query.setParameter("status", status);
        if (roleId != null) query.setParameter("roleId", roleId);
        if (search != null && !search.isBlank()) query.setParameter("search", "%" + search.trim() + "%");
    }

    /**
     * Cập nhật hồ sơ người dùng (khớp updateUser bên Node.js)
     */
    public User updateProfile(int id, Integer updatedById, String fullName, String phoneNumber, String avatarUrl) {
        return executeTransaction(em -> {
            User user = em.find(User.class, id);
            if (user != null) {
                if (fullName != null) user.setFullName(fullName.trim());
                if (phoneNumber != null) user.setPhoneNumber(phoneNumber.trim());
                if (avatarUrl != null) user.setAvatarUrl(avatarUrl);
                user.setUpdatedById(updatedById);
                em.merge(user);
            }
            return user;
        });
    }

    /**
     * Cập nhật trạng thái: Nếu khóa BLOCKED hoặc INACTIVE -> Đăng xuất toàn bộ phiên DeviceSession (khớp updateStatus bên Node.js)
     */
    public User updateStatus(int id, Integer updatedById, User.UserStatus status) {
        return executeTransaction(em -> {
            User user = em.find(User.class, id);
            if (user != null) {
                if (status == User.UserStatus.BLOCKED || status == User.UserStatus.INACTIVE) {
                    em.createQuery("UPDATE DeviceSession ds SET ds.isActive = false WHERE ds.user.id = :userId AND ds.isActive = true")
                            .setParameter("userId", id)
                            .executeUpdate();
                }
                user.setStatus(status);
                user.setUpdatedById(updatedById);
                em.merge(user);
            }
            return user;
        });
    }

    /**
     * Xóa mềm người dùng: Hủy toàn bộ DeviceSession + Gán deletedAt (khớp deleteUser bên Node.js)
     */
    public void softDeleteUser(int id, Integer deletedById) {
        executeTransaction(em -> {
            User user = em.find(User.class, id);
            if (user != null) {
                em.createQuery("UPDATE DeviceSession ds SET ds.isActive = false WHERE ds.user.id = :userId AND ds.isActive = true")
                        .setParameter("userId", id)
                        .executeUpdate();

                user.setDeletedAt(LocalDateTime.now());
                user.setDeletedById(deletedById);
                em.merge(user);
            }
            return null;
        });
    }

    public int getRewardPoints(int userId) {
        return execute(em -> {
            var points = em.createQuery(
                    "SELECT u.rewardPoints FROM User u WHERE u.id = :id AND u.deletedAt IS NULL", Integer.class)
                    .setParameter("id", userId)
                    .getResultList();
            return points.isEmpty() ? 0 : points.get(0);
        });
    }
}