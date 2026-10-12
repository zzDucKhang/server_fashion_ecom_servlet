package com.fashionshop.dataaccess.dao.cartorder;

import com.fashionshop.business.entity.CartItem;
import com.fashionshop.common.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

public class CartItemDAO {

    public CartItem findByIdWithVariant(Integer cartItemId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT ci FROM CartItem ci JOIN FETCH ci.variant v JOIN FETCH v.product WHERE ci.id = :id",
                    CartItem.class)
                    .setParameter("id", cartItemId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }
}

