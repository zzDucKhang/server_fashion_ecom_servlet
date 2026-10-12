package com.fashionshop.business.service.cartorder;

import com.fashionshop.business.entity.Cart;
import com.fashionshop.business.entity.CartItem;
import com.fashionshop.business.entity.ProductVariant;
import com.fashionshop.business.entity.Product;
import com.fashionshop.common.exception.BadRequestException;
import com.fashionshop.common.exception.NotFoundException;
import com.fashionshop.common.util.JPAUtil;
import com.fashionshop.dataaccess.dao.cartorder.CartDAO;
import com.fashionshop.presentation.dto.cartorder.AddCartItemDTO;
import jakarta.persistence.EntityManager;

public class CartService {
    private final CartDAO cartDAO = new CartDAO();
    private static final int MAX_CART_ITEM_QUANTITY = 100;

    private Cart resolveCart(Integer userId, String sessionId) {
        if (userId == null && (sessionId == null || sessionId.isBlank())) {
            throw new BadRequestException("Cần userId hoặc sessionId để xác định giỏ hàng");
        }
        
        if (userId != null && sessionId != null && !sessionId.isBlank()) {
            cartDAO.mergeGuestCart(userId, sessionId);
            return cartDAO.findByUserId(userId);
        }
        
        Cart cart = userId != null ? cartDAO.findByUserId(userId) : cartDAO.findBySessionId(sessionId);
        if (cart == null) {
            EntityManager em = JPAUtil.getEntityManager();
            try {
                em.getTransaction().begin();
                cart = new Cart();
                if (userId != null) cart.setUser(em.find(com.fashionshop.business.entity.User.class, userId));
                if (sessionId != null) cart.setSessionId(sessionId);
                em.persist(cart);
                em.getTransaction().commit();
            } finally {
                em.close();
            }
        }
        return cart;
    }

    public Cart getCart(Integer userId, String sessionId) {
        return resolveCart(userId, sessionId);
    }

    public Cart add(Integer userId, String sessionId, AddCartItemDTO input) {
        if (input.getQuantity() <= 0) throw new BadRequestException("Số lượng sản phẩm thêm vào phải lớn hơn 0");

        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Cart cart = resolveCart(userId, sessionId);
            cart = em.find(Cart.class, cart.getId());

            ProductVariant variant = em.find(ProductVariant.class, input.getVariantId());
            if (variant == null || variant.getDeletedAt() != null || 
                variant.getProduct().getStatus() != Product.ProductStatus.PUBLISHED) {
                throw new NotFoundException("Sản phẩm không tồn tại hoặc đã ngừng kinh doanh");
            }

            CartItem existingItem = cart.getCartItems().stream()
                    .filter(item -> item.getVariant().getId().equals(variant.getId()))
                    .findFirst()
                    .orElse(null);

            int newQuantity = (existingItem != null ? existingItem.getQuantity() : 0) + input.getQuantity();

            if (newQuantity > MAX_CART_ITEM_QUANTITY) {
                throw new BadRequestException("Số lượng tối đa cho mỗi sản phẩm trong giỏ hàng là " + MAX_CART_ITEM_QUANTITY);
            }
            if (newQuantity > variant.getStockQuantity()) {
                throw new BadRequestException("Số lượng yêu cầu vượt quá tồn kho hiện có (" + variant.getStockQuantity() + ")");
            }

            if (existingItem != null) {
                existingItem.setQuantity(newQuantity);
                em.merge(existingItem);
            } else {
                CartItem newItem = new CartItem();
                newItem.setCart(cart);
                newItem.setVariant(variant);
                newItem.setQuantity(newQuantity);
                em.persist(newItem);
                cart.getCartItems().add(newItem);
            }

            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
        // Trả về giỏ hàng đã được nạp đầy đủ variant và product
        return getCart(userId, sessionId);
    }

    public Cart update(Integer userId, String sessionId, Integer itemId, int quantity) {
        if (quantity < 0) throw new BadRequestException("Số lượng sản phẩm không hợp lệ");
        if (quantity > MAX_CART_ITEM_QUANTITY) throw new BadRequestException("Số lượng tối đa cho mỗi sản phẩm là " + MAX_CART_ITEM_QUANTITY);

        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Cart cart = resolveCart(userId, sessionId);
            
            CartItem item = em.find(CartItem.class, itemId);
            if (item == null || !item.getCart().getId().equals(cart.getId())) {
                throw new NotFoundException("Mục sản phẩm không tồn tại trong giỏ hàng");
            }

            ProductVariant variant = item.getVariant();
            if (variant.getDeletedAt() != null || variant.getProduct().getStatus() != Product.ProductStatus.PUBLISHED) {
                throw new BadRequestException("Sản phẩm hiện không còn mở bán");
            }

            if (quantity > variant.getStockQuantity()) {
                throw new BadRequestException("Số lượng vượt quá tồn kho hiện có (" + variant.getStockQuantity() + ")");
            }

            if (quantity == 0) {
                em.remove(item);
                cart.getCartItems().removeIf(ci -> ci.getId().equals(itemId));
            } else {
                item.setQuantity(quantity);
                em.merge(item);
            }

            em.getTransaction().commit();
            return cart;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public Cart remove(Integer userId, String sessionId, Integer itemId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Cart cart = resolveCart(userId, sessionId);
            
            CartItem item = em.find(CartItem.class, itemId);
            if (item != null && item.getCart().getId().equals(cart.getId())) {
                em.remove(item);
                cart.getCartItems().removeIf(ci -> ci.getId().equals(itemId));
            } else {
                throw new NotFoundException("Mục sản phẩm không tồn tại trong giỏ hàng");
            }

            em.getTransaction().commit();
            return cart;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public Cart clear(Integer userId, String sessionId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Cart cart = resolveCart(userId, sessionId);
            
            em.createQuery("DELETE FROM CartItem ci WHERE ci.cart.id = :cartId")
              .setParameter("cartId", cart.getId())
              .executeUpdate();
              
            cart.getCartItems().clear();
            em.getTransaction().commit();
            return cart;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public Cart merge(Integer userId, String sessionId) {
        if (sessionId == null || sessionId.isBlank()) {
            throw new BadRequestException("Session ID không tồn tại để thực hiện gộp giỏ hàng");
        }
        cartDAO.mergeGuestCart(userId, sessionId);
        return cartDAO.findByUserId(userId);
    }
}