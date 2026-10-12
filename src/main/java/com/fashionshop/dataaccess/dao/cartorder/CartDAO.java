package com.fashionshop.dataaccess.dao.cartorder;

import com.fashionshop.business.entity.Cart;
import com.fashionshop.business.entity.CartItem;
import com.fashionshop.common.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import java.util.HashMap;
import java.util.Map;

public class CartDAO {

    public Cart findByUserId(Integer userId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT c FROM Cart c LEFT JOIN FETCH c.cartItems WHERE c.user.id = :userId", Cart.class)
                    .setParameter("userId", userId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    public Cart findBySessionId(String sessionId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT c FROM Cart c LEFT JOIN FETCH c.cartItems WHERE c.sessionId = :sessionId", Cart.class)
                    .setParameter("sessionId", sessionId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    public void mergeGuestCart(Integer userId, String sessionId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            Cart guestCart = null;
            try {
                guestCart = em.createQuery("SELECT c FROM Cart c LEFT JOIN FETCH c.cartItems WHERE c.sessionId = :sessionId", Cart.class)
                        .setParameter("sessionId", sessionId)
                        .getSingleResult();
            } catch (NoResultException ignored) {}

            Cart userCart = null;
            try {
                userCart = em.createQuery("SELECT c FROM Cart c LEFT JOIN FETCH c.cartItems WHERE c.user.id = :userId", Cart.class)
                        .setParameter("userId", userId)
                        .getSingleResult();
            } catch (NoResultException ignored) {}

            if (guestCart == null || guestCart.getCartItems().isEmpty()) {
                em.getTransaction().commit();
                return;
            }

            if (userCart == null) {
                userCart = new Cart();
                userCart.setUser(em.find(com.fashionshop.business.entity.User.class, userId));
                em.persist(userCart);
            }

            Map<Integer, CartItem> userItemMap = new HashMap<>();
            for (CartItem item : userCart.getCartItems()) {
                userItemMap.put(item.getVariant().getId(), item);
            }

            for (CartItem guestItem : guestCart.getCartItems()) {
                Integer variantId = guestItem.getVariant().getId();
                if (userItemMap.containsKey(variantId)) {
                    CartItem existingUserItem = userItemMap.get(variantId);
                    int mergedQty = Math.min(100, existingUserItem.getQuantity() + guestItem.getQuantity());
                    existingUserItem.setQuantity(mergedQty);
                } else {
                    guestItem.setCart(userCart);
                    userCart.getCartItems().add(guestItem);
                }
            }

            em.remove(guestCart);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}