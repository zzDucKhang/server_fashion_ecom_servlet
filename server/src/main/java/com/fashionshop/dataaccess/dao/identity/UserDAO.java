package com.fashionshop.dataaccess.dao.identity;

import com.fashionshop.business.entity.User;
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
        if (email == null || email.isBlank()) {
            return Optional.empty();
        }
        return execute(em -> {
            List<User> list = em.createQuery(
                            "SELECT u FROM User u WHERE LOWER(u.email) = LOWER(:email) AND u.deletedAt IS NULL", User.class)
                    .setParameter("email", email.trim())
                    .setMaxResults(1)
                    .getResultList();
            return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
        });
    }

    public Optional<User> findByEmailWithRole(String email) {
        if (email == null || email.isBlank()) {
            return Optional.empty();
        }
        return execute(em -> {
            List<User> list = em.createQuery(
                            "SELECT DISTINCT u FROM User u " +
                                    "JOIN FETCH u.role r " +
                                    "LEFT JOIN FETCH r.permissions " +
                                    "WHERE LOWER(u.email) = LOWER(:email) AND u.deletedAt IS NULL", User.class)
                    .setParameter("email", email.trim())
                    .setMaxResults(1)
                    .getResultList();
            return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
        });
    }

    public Optional<User> findByIdWithRole(Integer id) {
        if (id == null) {
            return Optional.empty();
        }
        return execute(em -> {
            List<User> list = em.createQuery(
                            "SELECT DISTINCT u FROM User u " +
                                    "JOIN FETCH u.role r " +
                                    "LEFT JOIN FETCH r.permissions " +
                                    "WHERE u.id = :id AND u.deletedAt IS NULL", User.class)
                    .setParameter("id", id)
                    .setMaxResults(1)
                    .getResultList();
            return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
        });
    }

    public boolean existsByEmail(String email) {
        if (email == null || email.isBlank()) {
            return false;
        }
        return execute(em -> {
            Long count = em.createQuery(
                            "SELECT COUNT(u) FROM User u WHERE LOWER(u.email) = LOWER(:email) AND u.deletedAt IS NULL", Long.class)
                    .setParameter("email", email.trim())
                    .getSingleResult();
            return count != null && count > 0;
        });
    }

    public List<User> findFiltered(String search, String status, Integer roleId, int page, int limit) {
        return execute(em -> {
            StringBuilder jpql = new StringBuilder(
                    "SELECT DISTINCT u FROM User u JOIN FETCH u.role r WHERE u.deletedAt IS NULL"
            );
            appendFilters(jpql, search, status, roleId);
            jpql.append(" ORDER BY u.id DESC");

            TypedQuery<User> query = em.createQuery(jpql.toString(), User.class);
            bindParams(query, search, status, roleId);

            int pageNumber = page > 0 ? page : 1;
            int pageSize = limit > 0 ? limit : 10;
            query.setFirstResult((pageNumber - 1) * pageSize);
            query.setMaxResults(pageSize);

            return query.getResultList();
        });
    }

    public long countFiltered(String search, String status, Integer roleId) {
        return execute(em -> {
            StringBuilder jpql = new StringBuilder(
                    "SELECT COUNT(u) FROM User u WHERE u.deletedAt IS NULL"
            );
            appendFilters(jpql, search, status, roleId);

            TypedQuery<Long> query = em.createQuery(jpql.toString(), Long.class);
            bindParams(query, search, status, roleId);

            return query.getSingleResult();
        });
    }

    public long countByRoleId(Integer roleId) {
        if (roleId == null) {
            return 0;
        }
        return execute(em -> em.createQuery(
                        "SELECT COUNT(u) FROM User u WHERE u.role.id = :roleId AND u.deletedAt IS NULL", Long.class)
                .setParameter("roleId", roleId)
                .getSingleResult());
    }

    public void softDelete(Integer userId, Integer deletedById) {
        executeTransaction(em -> {
            em.createQuery("UPDATE User u SET u.deletedAt = :now, u.deletedById = :deletedById, u.updatedAt = :now WHERE u.id = :userId")
                    .setParameter("now", LocalDateTime.now())
                    .setParameter("deletedById", deletedById)
                    .setParameter("userId", userId)
                    .executeUpdate();
            return null;
        });
    }

    private void appendFilters(StringBuilder jpql, String search, String status, Integer roleId) {
        if (search != null && !search.trim().isEmpty()) {
            jpql.append(" AND (LOWER(u.fullName) LIKE :search OR LOWER(u.email) LIKE :search OR u.phoneNumber LIKE :search)");
        }
        if (status != null && !status.trim().isEmpty()) {
            jpql.append(" AND u.status = :status");
        }
        if (roleId != null) {
            jpql.append(" AND u.role.id = :roleId");
        }
    }

    private void bindParams(TypedQuery<?> query, String search, String status, Integer roleId) {
        if (search != null && !search.trim().isEmpty()) {
            query.setParameter("search", "%" + search.trim().toLowerCase() + "%");
        }
        if (status != null && !status.trim().isEmpty()) {
            try {
                User.UserStatus userStatus = User.UserStatus.valueOf(status.trim().toUpperCase());
                query.setParameter("status", userStatus);
            } catch (IllegalArgumentException e) {
                query.setParameter("status", null);
            }
        }
        if (roleId != null) {
            query.setParameter("roleId", roleId);
        }
    }
}
