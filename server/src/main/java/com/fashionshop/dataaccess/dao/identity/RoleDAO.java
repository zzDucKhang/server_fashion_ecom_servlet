package com.fashionshop.dataaccess.dao.identity;

import com.fashionshop.business.entity.Role;
import com.fashionshop.dataaccess.base.AbstractJpaDAO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class RoleDAO extends AbstractJpaDAO<Role, Integer> {

    public RoleDAO() {
        super(Role.class);
    }

    public Optional<Role> findByCode(String code) {
        if (code == null || code.isBlank()) {
            return Optional.empty();
        }
        return execute(em -> {
            List<Role> list = em.createQuery(
                            "SELECT DISTINCT r FROM Role r LEFT JOIN FETCH r.permissions " +
                                    "WHERE LOWER(r.code) = LOWER(:code) AND r.deletedAt IS NULL", Role.class)
                    .setParameter("code", code.trim())
                    .setMaxResults(1)
                    .getResultList();
            return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
        });
    }

    public Optional<Role> findByIdWithPermissions(Integer id) {
        if (id == null) {
            return Optional.empty();
        }
        return execute(em -> {
            List<Role> list = em.createQuery(
                            "SELECT DISTINCT r FROM Role r LEFT JOIN FETCH r.permissions " +
                                    "WHERE r.id = :id AND r.deletedAt IS NULL", Role.class)
                    .setParameter("id", id)
                    .setMaxResults(1)
                    .getResultList();
            return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
        });
    }

    public List<Role> findAllActive() {
        return execute(em -> em.createQuery(
                        "SELECT DISTINCT r FROM Role r LEFT JOIN FETCH r.permissions " +
                                "WHERE r.isActive = true AND r.deletedAt IS NULL ORDER BY r.id ASC", Role.class)
                .getResultList());
    }

    public List<Role> findAllWithPermissions() {
        return execute(em -> em.createQuery(
                        "SELECT DISTINCT r FROM Role r LEFT JOIN FETCH r.permissions " +
                                "WHERE r.deletedAt IS NULL ORDER BY r.id ASC", Role.class)
                .getResultList());
    }

    public boolean existsByCode(String code) {
        if (code == null || code.isBlank()) {
            return false;
        }
        return execute(em -> {
            Long count = em.createQuery(
                            "SELECT COUNT(r) FROM Role r WHERE LOWER(r.code) = LOWER(:code) AND r.deletedAt IS NULL", Long.class)
                    .setParameter("code", code.trim())
                    .getSingleResult();
            return count != null && count > 0;
        });
    }

    public void softDelete(Integer roleId, Integer deletedById) {
        executeTransaction(em -> {
            em.createQuery("UPDATE Role r SET r.deletedAt = :now, r.deletedById = :deletedById, r.isActive = false, r.updatedAt = :now WHERE r.id = :roleId")
                    .setParameter("now", LocalDateTime.now())
                    .setParameter("deletedById", deletedById)
                    .setParameter("roleId", roleId)
                    .executeUpdate();
            return null;
        });
    }
}
