package com.fashionshop.dataaccess.dao.identity;

import com.fashionshop.business.entity.ShippingAddress;
import com.fashionshop.dataaccess.base.AbstractJpaDAO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class ShippingAddressDAO extends AbstractJpaDAO<ShippingAddress, Integer> {

    public ShippingAddressDAO() {
        super(ShippingAddress.class);
    }

    public List<ShippingAddress> findByUserId(Integer userId) {
        if (userId == null) {
            return List.of();
        }
        return execute(em -> em.createQuery(
                        "SELECT a FROM ShippingAddress a " +
                                "WHERE a.user.id = :userId " +
                                "ORDER BY a.isDefault DESC, a.createdAt DESC", ShippingAddress.class)
                .setParameter("userId", userId)
                .getResultList());
    }

    public Optional<ShippingAddress> findByIdAndUserId(Integer id, Integer userId) {
        if (id == null || userId == null) {
            return Optional.empty();
        }
        return execute(em -> {
            List<ShippingAddress> list = em.createQuery(
                            "SELECT a FROM ShippingAddress a " +
                                    "WHERE a.id = :id AND a.user.id = :userId", ShippingAddress.class)
                    .setParameter("id", id)
                    .setParameter("userId", userId)
                    .setMaxResults(1)
                    .getResultList();
            return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
        });
    }

    public int countByUserId(Integer userId) {
        if (userId == null) {
            return 0;
        }
        return execute(em -> {
            Long count = em.createQuery(
                            "SELECT COUNT(a) FROM ShippingAddress a WHERE a.user.id = :userId", Long.class)
                    .setParameter("userId", userId)
                    .getSingleResult();
            return count != null ? count.intValue() : 0;
        });
    }

    public void resetDefaultByUserId(Integer userId) {
        if (userId == null) {
            return;
        }
        executeTransaction(em -> {
            em.createQuery(
                            "UPDATE ShippingAddress a SET a.isDefault = false, a.updatedAt = :now " +
                                    "WHERE a.user.id = :userId AND a.isDefault = true")
                    .setParameter("now", LocalDateTime.now())
                    .setParameter("userId", userId)
                    .executeUpdate();
            return null;
        });
    }

    public ShippingAddress createWithDefault(ShippingAddress address) {
        return executeTransaction(em -> {
            if (Boolean.TRUE.equals(address.getIsDefault())) {
                em.createQuery(
                                "UPDATE ShippingAddress a SET a.isDefault = false, a.updatedAt = :now " +
                                        "WHERE a.user.id = :userId AND a.isDefault = true")
                        .setParameter("now", LocalDateTime.now())
                        .setParameter("userId", address.getUser().getId())
                        .executeUpdate();
            }
            em.persist(address);
            return address;
        });
    }

    public ShippingAddress updateWithDefault(ShippingAddress address) {
        return executeTransaction(em -> {
            if (Boolean.TRUE.equals(address.getIsDefault())) {
                em.createQuery(
                                "UPDATE ShippingAddress a SET a.isDefault = false, a.updatedAt = :now " +
                                        "WHERE a.user.id = :userId AND a.id != :addrId AND a.isDefault = true")
                        .setParameter("now", LocalDateTime.now())
                        .setParameter("userId", address.getUser().getId())
                        .setParameter("addrId", address.getId())
                        .executeUpdate();
            }
            return em.merge(address);
        });
    }

    public boolean deleteByIdAndUserId(Integer id, Integer userId) {
        if (id == null || userId == null) {
            return false;
        }
        return executeTransaction(em -> {
            List<ShippingAddress> list = em.createQuery(
                            "SELECT a FROM ShippingAddress a WHERE a.id = :id AND a.user.id = :userId", ShippingAddress.class)
                    .setParameter("id", id)
                    .setParameter("userId", userId)
                    .setMaxResults(1)
                    .getResultList();
            if (list.isEmpty()) {
                return false;
            }
            em.remove(list.get(0));
            return true;
        });
    }
}
