package com.fashionshop.dataaccess.dao.identity;

import com.fashionshop.business.entity.Permission;
import com.fashionshop.dataaccess.base.AbstractJpaDAO;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class PermissionDAO extends AbstractJpaDAO<Permission, Integer> {

    public PermissionDAO() {
        super(Permission.class);
    }

    public Optional<Permission> findByCode(String code) {
        if (code == null || code.isBlank()) {
            return Optional.empty();
        }
        return execute(em -> {
            List<Permission> list = em.createQuery(
                            "SELECT p FROM Permission p WHERE LOWER(p.code) = LOWER(:code) AND p.deletedAt IS NULL", Permission.class)
                    .setParameter("code", code.trim())
                    .setMaxResults(1)
                    .getResultList();
            return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
        });
    }

    public List<Permission> findByModule(String module) {
        return execute(em -> {
            String jpql = (module == null || module.isBlank())
                    ? "SELECT p FROM Permission p WHERE p.deletedAt IS NULL ORDER BY p.module ASC, p.code ASC"
                    : "SELECT p FROM Permission p WHERE LOWER(p.module) = LOWER(:module) AND p.deletedAt IS NULL ORDER BY p.code ASC";
            var query = em.createQuery(jpql, Permission.class);
            if (module != null && !module.isBlank()) {
                query.setParameter("module", module.trim());
            }
            return query.getResultList();
        });
    }

    public List<Permission> findAllActive() {
        return execute(em -> em.createQuery(
                        "SELECT p FROM Permission p WHERE p.deletedAt IS NULL ORDER BY p.module ASC, p.code ASC", Permission.class)
                .getResultList());
    }

    public List<Permission> findByIds(Collection<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        return execute(em -> em.createQuery(
                        "SELECT p FROM Permission p WHERE p.id IN :ids AND p.deletedAt IS NULL", Permission.class)
                .setParameter("ids", ids)
                .getResultList());
    }

    public boolean existsByCode(String code) {
        if (code == null || code.isBlank()) {
            return false;
        }
        return execute(em -> {
            Long count = em.createQuery(
                            "SELECT COUNT(p) FROM Permission p WHERE LOWER(p.code) = LOWER(:code) AND p.deletedAt IS NULL", Long.class)
                    .setParameter("code", code.trim())
                    .getSingleResult();
            return count != null && count > 0;
        });
    }

    public void softDelete(Integer permissionId, Integer deletedById) {
        executeTransaction(em -> {
            em.createQuery("UPDATE Permission p SET p.deletedAt = :now, p.deletedById = :deletedById, p.updatedAt = :now WHERE p.id = :permissionId")
                    .setParameter("now", LocalDateTime.now())
                    .setParameter("deletedById", deletedById)
                    .setParameter("permissionId", permissionId)
                    .executeUpdate();
            return null;
        });
    }
}
