# Project Dump

**Root:** `C:\Programing\Lap_trinh_Web\fashion_ecom\src\main\java\com\fashionshop\dataaccess`

**Generated:** 2026-10-12 17:12:49.108751

**Files:** 29 | **Total size:** 114,733 bytes

---

# base\AbstractJpaDAO.java

| Property | Value |
|----------|-------|
| Path | `base\AbstractJpaDAO.java` |
| Size | 2,771 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:30:36.748000 |

```java
package com.fashionshop.dataaccess.base;


import com.fashionshop.common.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public abstract class AbstractJpaDAO<T, ID> implements GenericDAO<T, ID> {
    protected final Class<T> entityClass;

    protected AbstractJpaDAO(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    protected <R> R execute(Function<EntityManager, R> function) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return function.apply(em);
        } finally {
            em.close();
        }
    }

    protected <R> R executeTransaction(Function<EntityManager, R> function) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            R result = function.apply(em);
            tx.commit();
            return result;
        } catch (Exception ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw ex;
        } finally {
            em.close();
        }
    }

    @Override
    public T save(T entity) {
        return executeTransaction(em -> {
            em.persist(entity);
            return entity;
        });
    }

    @Override
    public T update(T entity) {
        return executeTransaction(em -> em.merge(entity));
    }

    @Override
    public void delete(ID id) {
        executeTransaction(em -> {
            T entity = em.find(entityClass, id);
            if (entity != null) {
                em.remove(entity);
            }
            return null;
        });
    }

    @Override
    public Optional<T> findById(ID id) {
        return execute(em -> Optional.ofNullable(em.find(entityClass, id)));
    }

    @Override
    public List<T> findAll() {
        return execute(em -> em.createQuery("FROM " + entityClass.getSimpleName(), entityClass).getResultList());
    }

    @Override
    public List<T> findPaginated(int page, int limit) {
        return execute(em -> {
            TypedQuery<T> query = em.createQuery("FROM " + entityClass.getSimpleName(), entityClass);
            query.setFirstResult((page - 1) * limit);
            query.setMaxResults(limit);
            return query.getResultList();
        });
    }

    @Override
    public long count() {
        return execute(em -> em.createQuery(
                "SELECT COUNT(e) FROM " + entityClass.getSimpleName() + " e", Long.class
        ).getSingleResult());
    }
}
```

# base\GenericDAO.java

| Property | Value |
|----------|-------|
| Path | `base\GenericDAO.java` |
| Size | 336 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:22:16.221000 |

```java
package com.fashionshop.dataaccess.base;

import java.util.List;
import java.util.Optional;

public interface GenericDAO<T, ID> {
    T save(T entity);
    T update(T entity);
    void delete(ID id);
    Optional<T> findById(ID id);
    List<T> findAll();
    List<T> findPaginated(int page, int limit);
    long count();
}
```

# dao\cartorder\CartDAO.java

| Property | Value |
|----------|-------|
| Path | `dao\cartorder\CartDAO.java` |
| Size | 3,706 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:28.389265 |

```java
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
```

# dao\cartorder\CartItemDAO.java

| Property | Value |
|----------|-------|
| Path | `dao\cartorder\CartItemDAO.java` |
| Size | 821 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:28.390153 |

```java
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


```

# dao\cartorder\OrderDAO.java

| Property | Value |
|----------|-------|
| Path | `dao\cartorder\OrderDAO.java` |
| Size | 5,390 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 16:21:14.614758 |

```java
package com.fashionshop.dataaccess.dao.cartorder;

import com.fashionshop.business.entity.Order;
import com.fashionshop.common.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import com.fashionshop.dataaccess.base.AbstractJpaDAO;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class OrderDAO extends AbstractJpaDAO<Order, Integer>{

    public OrderDAO() {
        super(Order.class);
    }

    public Optional<Order> findById(Integer id) {
        if (id == null) return Optional.empty();
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Order o = em.find(Order.class, id);
            return Optional.ofNullable(o);
        } finally {
            em.close();
        }
    }

    public Order findByIdWithItems(Integer orderId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT o FROM Order o LEFT JOIN FETCH o.orderItems WHERE o.id = :id",
                    Order.class)
                    .setParameter("id", orderId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    public List<Order> findByUserId(Integer userId, int offset, int limit, String status) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String idJpql = "SELECT o.id FROM Order o WHERE o.user.id = :userId";
            if (status != null && !status.isBlank()) {
                idJpql += " AND o.status = :status";
            }
            idJpql += " ORDER BY o.orderDate DESC";

            TypedQuery<Integer> idQuery = em.createQuery(idJpql, Integer.class)
                    .setParameter("userId", userId)
                    .setFirstResult(offset)
                    .setMaxResults(limit);

            if (status != null && !status.isBlank()) {
                idQuery.setParameter("status", Order.OrderStatus.valueOf(status.trim().toUpperCase()));
            }

            List<Integer> ids = idQuery.getResultList();
            if (ids.isEmpty()) {
                return Collections.emptyList();
            }

            return em.createQuery(
                    "SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.orderItems WHERE o.id IN :ids ORDER BY o.orderDate DESC",
                    Order.class)
                    .setParameter("ids", ids)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public long countByUserId(Integer userId, String status) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT COUNT(o) FROM Order o WHERE o.user.id = :userId";
            if (status != null && !status.isBlank()) {
                jpql += " AND o.status = :status";
            }

            TypedQuery<Long> query = em.createQuery(jpql, Long.class)
                    .setParameter("userId", userId);

            if (status != null && !status.isBlank()) {
                query.setParameter("status", Order.OrderStatus.valueOf(status.trim().toUpperCase()));
            }

            return query.getSingleResult();
        } finally {
            em.close();
        }
    }

    public List<Order> findAll(int offset, int limit, String status) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String idJpql = "SELECT o.id FROM Order o";
            if (status != null && !status.isBlank()) {
                idJpql += " WHERE o.status = :status";
            }
            idJpql += " ORDER BY o.orderDate DESC";

            TypedQuery<Integer> idQuery = em.createQuery(idJpql, Integer.class)
                    .setFirstResult(offset)
                    .setMaxResults(limit);

            if (status != null && !status.isBlank()) {
                idQuery.setParameter("status", Order.OrderStatus.valueOf(status.trim().toUpperCase()));
            }

            List<Integer> ids = idQuery.getResultList();
            if (ids.isEmpty()) {
                return Collections.emptyList();
            }

            return em.createQuery(
                    "SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.orderItems WHERE o.id IN :ids ORDER BY o.orderDate DESC",
                    Order.class)
                    .setParameter("ids", ids)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public long countAll(String status) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT COUNT(o) FROM Order o";
            if (status != null && !status.isBlank()) {
                jpql += " WHERE o.status = :status";
            }

            TypedQuery<Long> query = em.createQuery(jpql, Long.class);
            if (status != null && !status.isBlank()) {
                query.setParameter("status", Order.OrderStatus.valueOf(status.trim().toUpperCase()));
            }

            return query.getSingleResult();
        } finally {
            em.close();
        }
    }
}


```

# dao\cartorder\OrderItemDAO.java

| Property | Value |
|----------|-------|
| Path | `dao\cartorder\OrderItemDAO.java` |
| Size | 1,412 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:28.393152 |

```java
package com.fashionshop.dataaccess.dao.cartorder;

import com.fashionshop.business.entity.OrderItem;
import com.fashionshop.dataaccess.base.AbstractJpaDAO;

import java.util.List;
import java.util.Optional;

public class OrderItemDAO extends AbstractJpaDAO<OrderItem, Integer> {

    public OrderItemDAO() {
        super(OrderItem.class);
    }

    public List<OrderItem> findByOrderId(int orderId) {
        return execute(em -> em.createQuery(
                "SELECT DISTINCT oi FROM OrderItem oi " +
                "LEFT JOIN FETCH oi.variant v " +
                "LEFT JOIN FETCH v.product p " +
                "WHERE oi.order.id = :orderId AND oi.deletedAt IS NULL", OrderItem.class)
                .setParameter("orderId", orderId)
                .getResultList());
    }

    public Optional<OrderItem> findDetailById(int id) {
        return execute(em -> {
            var list = em.createQuery(
                    "SELECT oi FROM OrderItem oi " +
                    "LEFT JOIN FETCH oi.order o " +
                    "LEFT JOIN FETCH oi.variant v " +
                    "WHERE oi.id = :id AND oi.deletedAt IS NULL", OrderItem.class)
                    .setParameter("id", id)
                    .setMaxResults(1)
                    .getResultList();
            return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
        });
    }
}
```

# dao\catalog\BrandDAO.java

| Property | Value |
|----------|-------|
| Path | `dao\catalog\BrandDAO.java` |
| Size | 2,519 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:38:31.199000 |

```java
package com.fashionshop.dataaccess.dao.catalog;

import com.fashionshop.business.entity.Brand;
import com.fashionshop.dataaccess.base.AbstractJpaDAO;

import java.util.List;
import java.util.Optional;

public class BrandDAO extends AbstractJpaDAO<Brand, Integer> {

    public BrandDAO() {
        super(Brand.class);
    }

    public Optional<Brand> findByName(String name) {
        return execute(em -> {
            List<Brand> list = em.createQuery(
                            "SELECT b FROM Brand b WHERE LOWER(b.defaultName) = LOWER(:name) AND b.deletedAt IS NULL", Brand.class)
                    .setParameter("name", name.trim())
                    .setMaxResults(1)
                    .getResultList();
            return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
        });
    }

    public List<Brand> findAllActive() {
        return execute(em -> em.createQuery(
                        "SELECT b FROM Brand b WHERE b.deletedAt IS NULL ORDER BY b.createdAt DESC", Brand.class)
                .getResultList());
    }

    public List<Brand> findPaginatedActive(int page, int limit, String search) {
        return execute(em -> {
            StringBuilder jpql = new StringBuilder("SELECT b FROM Brand b WHERE b.deletedAt IS NULL");
            if (search != null && !search.isBlank()) {
                jpql.append(" AND LOWER(b.defaultName) LIKE LOWER(:search)");
            }
            jpql.append(" ORDER BY b.createdAt DESC");

            var query = em.createQuery(jpql.toString(), Brand.class);
            if (search != null && !search.isBlank()) {
                query.setParameter("search", "%" + search.trim() + "%");
            }

            query.setFirstResult((page - 1) * limit);
            query.setMaxResults(limit);
            return query.getResultList();
        });
    }

    public long countActive(String search) {
        return execute(em -> {
            StringBuilder jpql = new StringBuilder("SELECT COUNT(b) FROM Brand b WHERE b.deletedAt IS NULL");
            if (search != null && !search.isBlank()) {
                jpql.append(" AND LOWER(b.defaultName) LIKE LOWER(:search)");
            }

            var query = em.createQuery(jpql.toString(), Long.class);
            if (search != null && !search.isBlank()) {
                query.setParameter("search", "%" + search.trim() + "%");
            }
            return query.getSingleResult();
        });
    }
}
```

# dao\catalog\CategoryDAO.java

| Property | Value |
|----------|-------|
| Path | `dao\catalog\CategoryDAO.java` |
| Size | 1,857 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:38:43.187000 |

```java
package com.fashionshop.dataaccess.dao.catalog;

import com.fashionshop.business.entity.Category;
import com.fashionshop.dataaccess.base.AbstractJpaDAO;

import java.util.List;
import java.util.Optional;

public class CategoryDAO extends AbstractJpaDAO<Category, Integer> {

    public CategoryDAO() {
        super(Category.class);
    }

    public Optional<Category> findByName(String name) {
        return execute(em -> {
            List<Category> list = em.createQuery(
                            "SELECT c FROM Category c WHERE LOWER(c.defaultName) = LOWER(:name) AND c.deletedAt IS NULL", Category.class)
                    .setParameter("name", name.trim())
                    .setMaxResults(1)
                    .getResultList();
            return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
        });
    }

    public List<Category> findAllActive() {
        return execute(em -> em.createQuery(
                        "SELECT c FROM Category c WHERE c.deletedAt IS NULL ORDER BY c.createdAt DESC", Category.class)
                .getResultList());
    }

    public List<Category> findByParentId(Integer parentId) {
        return execute(em -> {
            String jpql = parentId == null
                    ? "SELECT c FROM Category c WHERE c.parent IS NULL AND c.deletedAt IS NULL ORDER BY c.createdAt DESC"
                    : "SELECT c FROM Category c WHERE c.parent.id = :parentId AND c.deletedAt IS NULL ORDER BY c.createdAt DESC";

            var query = em.createQuery(jpql, Category.class);
            if (parentId != null) {
                query.setParameter("parentId", parentId);
            }
            return query.getResultList();
        });
    }

    public List<Category> findRootCategories() {
        return findByParentId(null);
    }
}
```

# dao\catalog\ProductDAO.java

| Property | Value |
|----------|-------|
| Path | `dao\catalog\ProductDAO.java` |
| Size | 4,418 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 11:39:34.696819 |

```java
package com.fashionshop.dataaccess.dao.catalog;

import com.fashionshop.business.entity.Product;
import com.fashionshop.dataaccess.base.AbstractJpaDAO;
import com.fashionshop.presentation.dto.catalog.ProductQueryDTO;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

public class ProductDAO extends AbstractJpaDAO<Product, Integer> {

    public ProductDAO() {
        super(Product.class);
    }

    public Optional<Product> findDetailById(Integer id) {
        return execute(em -> {
            List<Product> list = em.createQuery(
                            "SELECT DISTINCT p FROM Product p " +
                                    "LEFT JOIN FETCH p.category " +
                                    "LEFT JOIN FETCH p.brand " +
                                    "LEFT JOIN FETCH p.tags " +
                                    "WHERE p.id = :id AND p.deletedAt IS NULL", Product.class)
                    .setParameter("id", id)
                    .getResultList();

            if (list.isEmpty()) return Optional.empty();
            Product p = list.get(0);

            // Fetch nạp thêm images và variants để tránh N+1 query
            em.createQuery("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.images WHERE p.id = :id ORDER BY p.id", Product.class)
                    .setParameter("id", id).getResultList();
            em.createQuery("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.variants WHERE p.id = :id ORDER BY p.id", Product.class)
                    .setParameter("id", id).getResultList();

            return Optional.of(p);
        });
    }

    public Optional<Product> findByName(String name) {
        return execute(em -> {
            List<Product> list = em.createQuery(
                            "SELECT p FROM Product p WHERE LOWER(p.defaultName) = LOWER(:name) AND p.deletedAt IS NULL", Product.class)
                    .setParameter("name", name.trim())
                    .setMaxResults(1)
                    .getResultList();
            return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
        });
    }

    public List<Product> listProducts(ProductQueryDTO queryDTO) {
        return execute(em -> {
            StringBuilder jpql = new StringBuilder("SELECT DISTINCT p FROM Product p WHERE p.deletedAt IS NULL");
            appendFilters(jpql, queryDTO);
            jpql.append(" ORDER BY p.createdAt DESC");

            TypedQuery<Product> query = em.createQuery(jpql.toString(), Product.class);
            bindParams(query, queryDTO);

            query.setFirstResult((queryDTO.getPage() - 1) * queryDTO.getLimit());
            query.setMaxResults(queryDTO.getLimit());
            return query.getResultList();
        });
    }

    public long countProducts(ProductQueryDTO queryDTO) {
        return execute(em -> {
            StringBuilder jpql = new StringBuilder("SELECT COUNT(DISTINCT p) FROM Product p WHERE p.deletedAt IS NULL");
            appendFilters(jpql, queryDTO);

            TypedQuery<Long> query = em.createQuery(jpql.toString(), Long.class);
            bindParams(query, queryDTO);
            return query.getSingleResult();
        });
    }

    private void appendFilters(StringBuilder jpql, ProductQueryDTO q) {
        if (q.getStatus() != null) {
            jpql.append(" AND p.status = :status");
        }
        if (q.getCategoryId() != null) {
            jpql.append(" AND p.category.id = :categoryId");
        }
        if (q.getBrandId() != null) {
            jpql.append(" AND p.brand.id = :brandId");
        }
        if (q.getSearch() != null && !q.getSearch().isBlank()) {
            jpql.append(" AND LOWER(p.defaultName) LIKE LOWER(:search)");
        }
    }

    private void bindParams(TypedQuery<?> query, ProductQueryDTO q) {
        if (q.getStatus() != null) {
            query.setParameter("status", q.getStatus());
        }
        if (q.getCategoryId() != null) {
            query.setParameter("categoryId", q.getCategoryId());
        }
        if (q.getBrandId() != null) {
            query.setParameter("brandId", q.getBrandId());
        }
        if (q.getSearch() != null && !q.getSearch().isBlank()) {
            query.setParameter("search", "%" + q.getSearch().trim() + "%");
        }
    }
}
```

# dao\catalog\ProductImageDAO.java

| Property | Value |
|----------|-------|
| Path | `dao\catalog\ProductImageDAO.java` |
| Size | 689 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:28.394151 |

```java
package com.fashionshop.dataaccess.dao.catalog;

import com.fashionshop.business.entity.ProductImage;
import com.fashionshop.dataaccess.base.AbstractJpaDAO;

import java.util.List;

public class ProductImageDAO extends AbstractJpaDAO<ProductImage, Integer> {
    public ProductImageDAO() {
        super(ProductImage.class);
    }

    public List<ProductImage> findByProductId(Integer productId) {
        return execute(em -> em.createQuery(
                "SELECT img FROM ProductImage img WHERE img.product.id = :productId ORDER BY img.displayOrder ASC",
                ProductImage.class
        ).setParameter("productId", productId).getResultList());
    }
}


```

# dao\catalog\ProductVariantDAO.java

| Property | Value |
|----------|-------|
| Path | `dao\catalog\ProductVariantDAO.java` |
| Size | 6,999 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:28.395150 |

```java
package com.fashionshop.dataaccess.dao.catalog;

import com.fashionshop.business.entity.Product;
import com.fashionshop.business.entity.ProductVariant;
import com.fashionshop.common.exception.AppException;
import com.fashionshop.common.exception.BadRequestException;
import com.fashionshop.common.exception.NotFoundException;
import com.fashionshop.dataaccess.base.AbstractJpaDAO;
import com.fashionshop.presentation.dto.catalog.SyncVariantsDTO;
import com.fashionshop.presentation.dto.catalog.UpsertVariantItemDTO;
import jakarta.persistence.LockModeType;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.TypedQuery;

import java.time.LocalDateTime;
import java.util.*;

public class ProductVariantDAO extends AbstractJpaDAO<ProductVariant, Integer> {

    public ProductVariantDAO() {
        super(ProductVariant.class);
    }

    public List<ProductVariant> findByProductId(Integer productId) {
        return execute(em -> {
            TypedQuery<ProductVariant> query = em.createQuery(
                    "SELECT v FROM ProductVariant v WHERE v.product.id = :productId AND v.deletedAt IS NULL ORDER BY v.id ASC",
                    ProductVariant.class
            );
            query.setParameter("productId", productId);
            return query.getResultList();
        });
    }

    public Optional<ProductVariant> findActiveById(Integer id) {
        return execute(em -> {
            TypedQuery<ProductVariant> query = em.createQuery(
                    "SELECT v FROM ProductVariant v LEFT JOIN FETCH v.product WHERE v.id = :id AND v.deletedAt IS NULL",
                    ProductVariant.class
            );
            query.setParameter("id", id);
            List<ProductVariant> list = query.getResultList();
            return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
        });
    }

    public Optional<ProductVariant> findBySku(String sku) {
        return execute(em -> {
            List<ProductVariant> list = em.createQuery(
                            "SELECT v FROM ProductVariant v WHERE LOWER(v.sku) = LOWER(:sku) AND v.deletedAt IS NULL",
                            ProductVariant.class)
                    .setParameter("sku", sku.trim())
                    .setMaxResults(1)
                    .getResultList();
            return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
        });
    }

    /**
     * Đồng bộ biến thể theo lô sử dụng Transaction và Pessimistic Locking chống Race Condition
     */
    public List<ProductVariant> syncVariants(SyncVariantsDTO dto, Integer userId) {
        return executeTransaction(em -> {
            Integer productId = dto.getProductId();
            Product product = em.find(Product.class, productId, LockModeType.PESSIMISTIC_WRITE);
            if (product == null || product.getDeletedAt() != null) {
                throw new NotFoundException("Không tìm thấy sản phẩm tương ứng để đồng bộ biến thể");
            }

            TypedQuery<ProductVariant> existingQuery = em.createQuery(
                    "SELECT v FROM ProductVariant v WHERE v.product.id = :productId AND v.deletedAt IS NULL",
                    ProductVariant.class
            );
            existingQuery.setParameter("productId", productId);
            existingQuery.setLockMode(LockModeType.PESSIMISTIC_WRITE);
            List<ProductVariant> existingVariants = existingQuery.getResultList();

            Set<String> payloadSkus = new HashSet<>();
            for (UpsertVariantItemDTO item : dto.getVariants()) {
                payloadSkus.add(item.getSku().trim().toLowerCase());
            }

            // Xóa mềm các SKU cũ không còn trong payload
            for (ProductVariant existing : existingVariants) {
                if (!payloadSkus.contains(existing.getSku().trim().toLowerCase())) {
                    existing.setDeletedAt(LocalDateTime.now());
                    em.merge(existing);
                }
            }

            List<ProductVariant> result = new ArrayList<>();
            for (UpsertVariantItemDTO item : dto.getVariants()) {
                String cleanSku = item.getSku().trim();
                Optional<ProductVariant> matched = existingVariants.stream()
                        .filter(v -> v.getSku().equalsIgnoreCase(cleanSku) && v.getDeletedAt() == null)
                        .findFirst();

                if (matched.isPresent()) {
                    ProductVariant v = matched.get();
                    v.setOriginalPrice(item.getOriginalPrice());
                    v.setSellingPrice(item.getSellingPrice());
                    v.setStockQuantity(item.getStockQuantity());
                    v.setWeightGram(item.getWeightGram());
                    v.setImageUrl(item.getImageUrl());
                    v.setVariant(item.getVariant());
                    v.setVariantValue(item.getVariantValue());
                    result.add(em.merge(v));
                } else {
                    ProductVariant newVariant = new ProductVariant();
                    newVariant.setProduct(product);
                    newVariant.setSku(cleanSku);
                    newVariant.setOriginalPrice(item.getOriginalPrice());
                    newVariant.setSellingPrice(item.getSellingPrice());
                    newVariant.setStockQuantity(item.getStockQuantity());
                    newVariant.setWeightGram(item.getWeightGram());
                    newVariant.setImageUrl(item.getImageUrl());
                    newVariant.setVariant(item.getVariant());
                    newVariant.setVariantValue(item.getVariantValue());
                    em.persist(newVariant);
                    result.add(newVariant);
                }
            }
            return result;
        });
    }

    /**
     * Cập nhật tồn kho với Optimistic Concurrency Control
     */
    public ProductVariant updateStockOptimistic(Integer variantId, int delta) {
        try {
            return executeTransaction(em -> {
                ProductVariant variant = em.find(ProductVariant.class, variantId);
                if (variant == null || variant.getDeletedAt() != null) {
                    throw new NotFoundException("Biến thể không tồn tại");
                }

                if (delta < 0 && variant.getStockQuantity() < Math.abs(delta)) {
                    throw new BadRequestException("Số lượng tồn kho không đủ để thực hiện khấu trừ");
                }

                variant.updateStock(delta);
                return em.merge(variant);
            });
        } catch (OptimisticLockException e) {
            throw new AppException("Xung đột phiên bản dữ liệu (Version Conflict). Vui lòng thử lại!", 409);
        }
    }
}


```

# dao\catalog\TagDAO.java

| Property | Value |
|----------|-------|
| Path | `dao\catalog\TagDAO.java` |
| Size | 2,732 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:28.396151 |

```java
package com.fashionshop.dataaccess.dao.catalog;

import com.fashionshop.business.entity.Tag;
import com.fashionshop.dataaccess.base.AbstractJpaDAO;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class TagDAO extends AbstractJpaDAO<Tag, Integer> {
    public TagDAO() {
        super(Tag.class);
    }

    public Optional<Tag> findByName(String name) {
        return execute(em -> {
            List<Tag> list = em.createQuery(
                            "SELECT t FROM Tag t WHERE LOWER(t.name) = LOWER(:name) AND t.deletedAt IS NULL", Tag.class)
                    .setParameter("name", name.trim())
                    .setMaxResults(1)
                    .getResultList();
            return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
        });
    }

    public List<Tag> findPaginatedActive(int page, int limit, String search) {
        return execute(em -> {
            StringBuilder jpql = new StringBuilder("SELECT t FROM Tag t WHERE t.deletedAt IS NULL");
            if (search != null && !search.isBlank()) {
                jpql.append(" AND LOWER(t.name) LIKE LOWER(:search)");
            }
            jpql.append(" ORDER BY t.createdAt DESC");

            var query = em.createQuery(jpql.toString(), Tag.class);
            if (search != null && !search.isBlank()) {
                query.setParameter("search", "%" + search.trim() + "%");
            }

            query.setFirstResult((page - 1) * limit);
            query.setMaxResults(limit);
            return query.getResultList();
        });
    }

    public long countActive(String search) {
        return execute(em -> {
            StringBuilder jpql = new StringBuilder("SELECT COUNT(t) FROM Tag t WHERE t.deletedAt IS NULL");
            if (search != null && !search.isBlank()) {
                jpql.append(" AND LOWER(t.name) LIKE LOWER(:search)");
            }

            var query = em.createQuery(jpql.toString(), Long.class);
            if (search != null && !search.isBlank()) {
                query.setParameter("search", "%" + search.trim() + "%");
            }
            return query.getSingleResult();
        });
    }

    public Set<Tag> findByIds(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) return Collections.emptySet();
        return execute(em -> {
            List<Tag> tags = em.createQuery("SELECT t FROM Tag t WHERE t.id IN :ids AND t.deletedAt IS NULL", Tag.class)
                    .setParameter("ids", ids)
                    .getResultList();
            return new HashSet<>(tags);
        });
    }
}
```

# dao\fulfillment\PaymentTransactionDAO.java

| Property | Value |
|----------|-------|
| Path | `dao\fulfillment\PaymentTransactionDAO.java` |
| Size | 3,447 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:28.398152 |

```java
package com.fashionshop.dataaccess.dao.fulfillment;

import com.fashionshop.business.entity.Order;
import com.fashionshop.business.entity.PaymentTransaction;
import com.fashionshop.common.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

public class PaymentTransactionDAO {

    public PaymentTransaction findPendingByOrderId(Integer orderId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT p FROM PaymentTransaction p WHERE p.order.id = :orderId AND p.status = 'PENDING' AND p.provider = 'SEPAY'",
                            PaymentTransaction.class)
                    .setParameter("orderId", orderId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    public PaymentTransaction findByOrderId(Integer orderId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT p FROM PaymentTransaction p WHERE p.order.id = :orderId",
                            PaymentTransaction.class)
                    .setParameter("orderId", orderId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    public PaymentTransaction create(PaymentTransaction payment) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(payment);
            em.getTransaction().commit();
            return payment;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public PaymentTransaction update(PaymentTransaction payment) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            PaymentTransaction merged = em.merge(payment);
            em.getTransaction().commit();
            return merged;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public void markSuccess(Integer paymentId, String providerTransId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            PaymentTransaction payment = em.find(PaymentTransaction.class, paymentId);
            if (payment != null) {
                payment.setStatus(Order.PaymentStatus.SUCCESS);
                payment.setProviderTransId(providerTransId);

                Order order = payment.getOrder();
                if (order != null) {
                    order.setPaymentStatus(Order.PaymentStatus.SUCCESS);
                    order.setStatus(Order.OrderStatus.CONFIRMED);
                }
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}
```

# dao\fulfillment\ReturnMediaDAO.java

| Property | Value |
|----------|-------|
| Path | `dao\fulfillment\ReturnMediaDAO.java` |
| Size | 320 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:28.399154 |

```java
package com.fashionshop.dataaccess.dao.fulfillment;

import com.fashionshop.dataaccess.base.AbstractJpaDAO;
import com.fashionshop.business.entity.ReturnMedia;

public class ReturnMediaDAO extends AbstractJpaDAO<ReturnMedia, Integer> {
    public ReturnMediaDAO() {
        super(ReturnMedia.class);
    }
}


```

# dao\fulfillment\ReturnRequestDAO.java

| Property | Value |
|----------|-------|
| Path | `dao\fulfillment\ReturnRequestDAO.java` |
| Size | 4,512 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:28.401152 |

```java
package com.fashionshop.dataaccess.dao.fulfillment;

import com.fashionshop.business.entity.OrderItem;
import com.fashionshop.business.entity.ReturnMedia;
import com.fashionshop.business.entity.ReturnRequest;
import com.fashionshop.dataaccess.base.AbstractJpaDAO;
import com.fashionshop.presentation.dto.fulfillment.CreateReturnDTO;
import com.fashionshop.presentation.dto.fulfillment.ReturnMediaDTO;
import com.fashionshop.presentation.dto.fulfillment.ReturnQueryDTO;
import com.fashionshop.presentation.dto.fulfillment.UpdateReturnDTO;
import jakarta.persistence.TypedQuery;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class ReturnRequestDAO extends AbstractJpaDAO<ReturnRequest, Integer> {

    public ReturnRequestDAO() {
        super(ReturnRequest.class);
    }

    public List<ReturnRequest> findAllWithFilter(ReturnQueryDTO query) {
        return execute(em -> {
            StringBuilder jpql = new StringBuilder(
                    "SELECT DISTINCT rr FROM ReturnRequest rr " +
                    "LEFT JOIN FETCH rr.orderItem " +
                    "LEFT JOIN FETCH rr.medias " +
                    "WHERE rr.deletedAt IS NULL");

            if (query.getStatus() != null) {
                jpql.append(" AND rr.status = :status");
            }
            if (query.getOrderItemId() != null) {
                jpql.append(" AND rr.orderItem.id = :orderItemId");
            }
            jpql.append(" ORDER BY rr.createdAt DESC");

            TypedQuery<ReturnRequest> tq = em.createQuery(jpql.toString(), ReturnRequest.class);
            if (query.getStatus() != null) {
                tq.setParameter("status", query.getStatus());
            }
            if (query.getOrderItemId() != null) {
                tq.setParameter("orderItemId", query.getOrderItemId());
            }

            return tq.getResultList();
        });
    }

    public Optional<ReturnRequest> findDetailById(int id) {
        return execute(em -> {
            String jpql = "SELECT DISTINCT rr FROM ReturnRequest rr " +
                    "LEFT JOIN FETCH rr.orderItem " +
                    "LEFT JOIN FETCH rr.medias " +
                    "WHERE rr.id = :id AND rr.deletedAt IS NULL";

            List<ReturnRequest> list = em.createQuery(jpql, ReturnRequest.class)
                    .setParameter("id", id)
                    .setMaxResults(1)
                    .getResultList();

            return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
        });
    }

    public ReturnRequest create(CreateReturnDTO dto) {
        return executeTransaction(em -> {
            OrderItem orderItem = em.find(OrderItem.class, dto.getOrderItemId());

            ReturnRequest rr = new ReturnRequest();
            rr.setOrderItem(orderItem);
            rr.setQuantity(dto.getQuantity());
            rr.setReason(dto.getReason().trim());
            rr.setDescription(dto.getDescription());
            rr.setStatus(ReturnRequest.ReturnStatus.PENDING);

            em.persist(rr);

            if (dto.getMedias() != null && !dto.getMedias().isEmpty()) {
                for (ReturnMediaDTO mDto : dto.getMedias()) {
                    ReturnMedia media = new ReturnMedia();
                    media.setReturnRequest(rr);
                    media.setMediaUrl(mDto.getMediaUrl());
                    media.setMediaType(mDto.getMediaType());
                    rr.getMedias().add(media);
                    em.persist(media);
                }
            }

            return rr;
        });
    }

    public ReturnRequest update(int id, UpdateReturnDTO dto) {
        return executeTransaction(em -> {
            ReturnRequest rr = em.find(ReturnRequest.class, id);

            if (dto.getQuantity() != null) rr.setQuantity(dto.getQuantity());
            if (dto.getReason() != null) rr.setReason(dto.getReason().trim());
            if (dto.getDescription() != null) rr.setDescription(dto.getDescription());
            if (dto.getStatus() != null) rr.setStatus(dto.getStatus());

            return rr;
        });
    }

    public void softDelete(int id) {
        executeTransaction(em -> {
            ReturnRequest rr = em.find(ReturnRequest.class, id);
            if (rr != null) {
                rr.setDeletedAt(LocalDateTime.now());
            }
            return null;
        });
    }
}
```

# dao\fulfillment\ShipmentDAO.java

| Property | Value |
|----------|-------|
| Path | `dao\fulfillment\ShipmentDAO.java` |
| Size | 4,225 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:28.403152 |

```java
package com.fashionshop.dataaccess.dao.fulfillment;

import com.fashionshop.business.entity.Order;
import com.fashionshop.business.entity.Shipment;
import com.fashionshop.common.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class ShipmentDAO {

    public Shipment findByOrderId(Integer orderId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT s FROM Shipment s LEFT JOIN FETCH s.order o LEFT JOIN FETCH o.user WHERE s.order.id = :orderId",
                    Shipment.class)
                    .setParameter("orderId", orderId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    public Shipment findById(Integer id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT s FROM Shipment s LEFT JOIN FETCH s.order o LEFT JOIN FETCH o.user WHERE s.id = :id",
                    Shipment.class)
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    public Shipment create(Shipment shipment) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(shipment);
            em.getTransaction().commit();
            return shipment;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public void updateStatus(Integer shipmentId, Shipment.ShipmentStatus status, String trackingCode) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Shipment shipment = em.find(Shipment.class, shipmentId);
            if (shipment != null) {
                shipment.setStatus(status);
                if (trackingCode != null && !trackingCode.isBlank()) {
                    shipment.setTrackingCode(trackingCode);
                }
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public List<Shipment> findAll(int offset, int limit, String status) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT s FROM Shipment s";
            if (status != null && !status.isBlank()) {
                jpql += " WHERE s.status = :status";
            }
            jpql += " ORDER BY s.createdAt DESC";

            TypedQuery<Shipment> query = em.createQuery(jpql, Shipment.class)
                    .setFirstResult(offset)
                    .setMaxResults(limit);

            if (status != null && !status.isBlank()) {
                query.setParameter("status", Shipment.ShipmentStatus.valueOf(status.trim().toUpperCase()));
            }

            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public long countAll(String status) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT COUNT(s) FROM Shipment s";
            if (status != null && !status.isBlank()) {
                jpql += " WHERE s.status = :status";
            }

            TypedQuery<Long> query = em.createQuery(jpql, Long.class);
            if (status != null && !status.isBlank()) {
                query.setParameter("status", Shipment.ShipmentStatus.valueOf(status.trim().toUpperCase()));
            }

            return query.getSingleResult();
        } finally {
            em.close();
        }
    }
}


```

# dao\identity\DeviceSessionDAO.java

| Property | Value |
|----------|-------|
| Path | `dao\identity\DeviceSessionDAO.java` |
| Size | 3,391 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:40.462788 |

```java
package com.fashionshop.dataaccess.dao.identity;

import com.fashionshop.business.entity.DeviceSession;
import com.fashionshop.dataaccess.base.AbstractJpaDAO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class DeviceSessionDAO extends AbstractJpaDAO<DeviceSession, Integer> {

    public DeviceSessionDAO() {
        super(DeviceSession.class);
    }

    public Optional<DeviceSession> findByRefreshToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            return Optional.empty();
        }
        return execute(em -> {
            List<DeviceSession> list = em.createQuery(
                            "SELECT DISTINCT s FROM DeviceSession s " +
                                    "JOIN FETCH s.user u " +
                                    "JOIN FETCH u.role r " +
                                    "LEFT JOIN FETCH r.permissions " +
                                    "WHERE s.refreshToken = :token", DeviceSession.class)
                    .setParameter("token", refreshToken)
                    .setMaxResults(1)
                    .getResultList();
            return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
        });
    }

    public List<DeviceSession> findActiveByUserId(Integer userId) {
        if (userId == null) {
            return List.of();
        }
        return execute(em -> em.createQuery(
                        "SELECT s FROM DeviceSession s " +
                                "WHERE s.user.id = :userId AND s.isActive = true AND s.expiresAt > :now " +
                                "ORDER BY s.createdAt DESC", DeviceSession.class)
                .setParameter("userId", userId)
                .setParameter("now", LocalDateTime.now())
                .getResultList());
    }

    public void deactivateAllByUserId(Integer userId) {
        if (userId == null) {
            return;
        }
        executeTransaction(em -> {
            em.createQuery(
                            "UPDATE DeviceSession s SET s.isActive = false, s.updatedAt = :now " +
                                    "WHERE s.user.id = :userId AND s.isActive = true")
                    .setParameter("now", LocalDateTime.now())
                    .setParameter("userId", userId)
                    .executeUpdate();
            return null;
        });
    }

    public void deactivateByRefreshToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            return;
        }
        executeTransaction(em -> {
            em.createQuery(
                            "UPDATE DeviceSession s SET s.isActive = false, s.updatedAt = :now " +
                                    "WHERE s.refreshToken = :token")
                    .setParameter("now", LocalDateTime.now())
                    .setParameter("token", refreshToken)
                    .executeUpdate();
            return null;
        });
    }

    public void deleteExpired() {
        executeTransaction(em -> {
            em.createQuery("DELETE FROM DeviceSession s WHERE s.expiresAt < :now OR s.isActive = false")
                    .setParameter("now", LocalDateTime.now())
                    .executeUpdate();
            return null;
        });
    }
}

```

# dao\identity\PermissionDAO.java

| Property | Value |
|----------|-------|
| Path | `dao\identity\PermissionDAO.java` |
| Size | 3,477 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:40.464787 |

```java
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

```

# dao\identity\RoleDAO.java

| Property | Value |
|----------|-------|
| Path | `dao\identity\RoleDAO.java` |
| Size | 3,366 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:40.465787 |

```java
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

```

# dao\identity\ShippingAddressDAO.java

| Property | Value |
|----------|-------|
| Path | `dao\identity\ShippingAddressDAO.java` |
| Size | 4,884 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:40.466787 |

```java
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

```

# dao\identity\UserDAO.java

| Property | Value |
|----------|-------|
| Path | `dao\identity\UserDAO.java` |
| Size | 6,530 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 16:22:03.023040 |

```java
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

```

# dao\inventory\FlashSaleCampaignDAO.java

| Property | Value |
|----------|-------|
| Path | `dao\inventory\FlashSaleCampaignDAO.java` |
| Size | 8,781 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:28.413152 |

```java
package com.fashionshop.dataaccess.dao.inventory;

import com.fashionshop.business.entity.FlashSaleCampaign;
import com.fashionshop.business.entity.FlashSaleItem;
import com.fashionshop.business.entity.ProductVariant;
import com.fashionshop.dataaccess.base.AbstractJpaDAO;
import com.fashionshop.presentation.dto.inventory.CreateFlashSaleDTO;
import com.fashionshop.presentation.dto.inventory.FlashSaleItemDTO;
import com.fashionshop.presentation.dto.inventory.FlashSaleQueryDTO;
import com.fashionshop.presentation.dto.inventory.UpdateFlashSaleDTO;
import jakarta.persistence.TypedQuery;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class FlashSaleCampaignDAO extends AbstractJpaDAO<FlashSaleCampaign, Integer> {

    public FlashSaleCampaignDAO() {
        super(FlashSaleCampaign.class);
    }

    public List<FlashSaleCampaign> findActiveCampaigns() {
        return execute(em -> {
            LocalDateTime now = LocalDateTime.now();
            String jpql = "SELECT DISTINCT c FROM FlashSaleCampaign c " +
                    "LEFT JOIN FETCH c.items i " +
                    "LEFT JOIN FETCH i.variant v " +
                    "LEFT JOIN FETCH v.product p " +
                    "WHERE c.deletedAt IS NULL " +
                    "  AND c.isActive = true " +
                    "  AND :now >= c.startTime AND :now <= c.endTime " +
                    "ORDER BY c.endTime ASC";

            return em.createQuery(jpql, FlashSaleCampaign.class)
                    .setParameter("now", now)
                    .getResultList();
        });
    }

    public Optional<FlashSaleCampaign> findDetailById(int id) {
        return execute(em -> {
            String jpql = "SELECT DISTINCT c FROM FlashSaleCampaign c " +
                    "LEFT JOIN FETCH c.items i " +
                    "LEFT JOIN FETCH i.variant v " +
                    "LEFT JOIN FETCH v.product p " +
                    "WHERE c.id = :id AND c.deletedAt IS NULL";

            List<FlashSaleCampaign> list = em.createQuery(jpql, FlashSaleCampaign.class)
                    .setParameter("id", id)
                    .setMaxResults(1)
                    .getResultList();

            return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
        });
    }

    public List<FlashSaleCampaign> list(FlashSaleQueryDTO query) {
        return execute(em -> {
            StringBuilder jpql = new StringBuilder(
                    "SELECT DISTINCT c FROM FlashSaleCampaign c " +
                    "LEFT JOIN FETCH c.items i " +
                    "LEFT JOIN FETCH i.variant v " +
                    "WHERE c.deletedAt IS NULL");

            if (query.getIsActive() != null) {
                jpql.append(" AND c.isActive = :isActive");
            }
            jpql.append(" ORDER BY c.createdAt DESC");

            TypedQuery<FlashSaleCampaign> tq = em.createQuery(jpql.toString(), FlashSaleCampaign.class);
            if (query.getIsActive() != null) {
                tq.setParameter("isActive", query.getIsActive());
            }

            tq.setFirstResult((query.getPage() - 1) * query.getLimit());
            tq.setMaxResults(query.getLimit());
            return tq.getResultList();
        });
    }

    public long count(FlashSaleQueryDTO query) {
        return execute(em -> {
            StringBuilder jpql = new StringBuilder("SELECT COUNT(c) FROM FlashSaleCampaign c WHERE c.deletedAt IS NULL");
            if (query.getIsActive() != null) {
                jpql.append(" AND c.isActive = :isActive");
            }

            TypedQuery<Long> tq = em.createQuery(jpql.toString(), Long.class);
            if (query.getIsActive() != null) {
                tq.setParameter("isActive", query.getIsActive());
            }
            return tq.getSingleResult();
        });
    }

    public List<ProductVariant> findVariantsForFlashSale(List<Integer> variantIds) {
        if (variantIds == null || variantIds.isEmpty()) return Collections.emptyList();
        return execute(em -> em.createQuery(
                "SELECT v FROM ProductVariant v WHERE v.id IN :ids AND v.deletedAt IS NULL", ProductVariant.class)
                .setParameter("ids", variantIds)
                .getResultList());
    }

    public FlashSaleCampaign create(CreateFlashSaleDTO dto) {
        return executeTransaction(em -> {
            FlashSaleCampaign campaign = new FlashSaleCampaign();
            campaign.setName(dto.getName().trim());
            campaign.setBannerUrl(dto.getBannerUrl());
            campaign.setStartTime(dto.getStartTime());
            campaign.setEndTime(dto.getEndTime());
            campaign.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);

            em.persist(campaign);

            for (FlashSaleItemDTO itemDto : dto.getItems()) {
                ProductVariant variant = em.find(ProductVariant.class, itemDto.getVariantId());
                FlashSaleItem item = new FlashSaleItem();
                item.setCampaign(campaign);
                item.setVariant(variant);
                item.setFlashSalePrice(itemDto.getFlashSalePrice());
                item.setPromoQuantity(itemDto.getPromoQuantity());
                item.setSoldQuantity(0);

                campaign.getItems().add(item);
                em.persist(item);
            }

            return campaign;
        });
    }

    public FlashSaleCampaign update(int campaignId, UpdateFlashSaleDTO dto) {
        return executeTransaction(em -> {
            FlashSaleCampaign campaign = em.find(FlashSaleCampaign.class, campaignId);

            if (dto.getName() != null) campaign.setName(dto.getName().trim());
            if (dto.getBannerUrl() != null) campaign.setBannerUrl(dto.getBannerUrl());
            if (dto.getStartTime() != null) campaign.setStartTime(dto.getStartTime());
            if (dto.getEndTime() != null) campaign.setEndTime(dto.getEndTime());
            if (dto.getIsActive() != null) campaign.setIsActive(dto.getIsActive());

            if (dto.getItems() != null) {
                // Map lưu lại soldQuantity hiện tại của từng variant
                Map<Integer, Integer> soldQuantityMap = campaign.getItems().stream()
                        .collect(Collectors.toMap(
                                item -> item.getVariant().getId(),
                                FlashSaleItem::getSoldQuantity,
                                (a, b) -> a
                        ));

                Set<Integer> incomingVariantIds = dto.getItems().stream()
                        .map(FlashSaleItemDTO::getVariantId)
                        .collect(Collectors.toSet());

                // Xóa các items không còn nằm trong payload cập nhật
                Iterator<FlashSaleItem> iterator = campaign.getItems().iterator();
                while (iterator.hasNext()) {
                    FlashSaleItem item = iterator.next();
                    if (!incomingVariantIds.contains(item.getVariant().getId())) {
                        em.remove(item);
                        iterator.remove();
                    }
                }

                // Cập nhật hoặc thêm mới các items
                for (FlashSaleItemDTO itemDto : dto.getItems()) {
                    Optional<FlashSaleItem> existingOpt = campaign.getItems().stream()
                            .filter(i -> i.getVariant().getId().equals(itemDto.getVariantId()))
                            .findFirst();

                    if (existingOpt.isPresent()) {
                        FlashSaleItem existing = existingOpt.get();
                        existing.setFlashSalePrice(itemDto.getFlashSalePrice());
                        existing.setPromoQuantity(itemDto.getPromoQuantity());
                    } else {
                        ProductVariant variant = em.find(ProductVariant.class, itemDto.getVariantId());
                        FlashSaleItem newItem = new FlashSaleItem();
                        newItem.setCampaign(campaign);
                        newItem.setVariant(variant);
                        newItem.setFlashSalePrice(itemDto.getFlashSalePrice());
                        newItem.setPromoQuantity(itemDto.getPromoQuantity());
                        newItem.setSoldQuantity(soldQuantityMap.getOrDefault(itemDto.getVariantId(), 0));

                        campaign.getItems().add(newItem);
                        em.persist(newItem);
                    }
                }
            }

            return campaign;
        });
    }
}
```

# dao\inventory\FlashSaleItemDAO.java

| Property | Value |
|----------|-------|
| Path | `dao\inventory\FlashSaleItemDAO.java` |
| Size | 324 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:28.415153 |

```java
package com.fashionshop.dataaccess.dao.inventory;

import com.fashionshop.business.entity.FlashSaleItem;
import com.fashionshop.dataaccess.base.AbstractJpaDAO;

public class FlashSaleItemDAO extends AbstractJpaDAO<FlashSaleItem, Integer> {
    public FlashSaleItemDAO() {
        super(FlashSaleItem.class);
    }
}
```

# dao\loyalty\ChatDAO.java

| Property | Value |
|----------|-------|
| Path | `dao\loyalty\ChatDAO.java` |
| Size | 3,753 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:28.417153 |

```java
package com.fashionshop.dataaccess.dao.loyalty;

import com.fashionshop.business.entity.Chat;
import com.fashionshop.business.entity.User;
import com.fashionshop.business.entity.enums.ChatStatus;
import com.fashionshop.dataaccess.base.AbstractJpaDAO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class ChatDAO extends AbstractJpaDAO<Chat, Integer> {

    public ChatDAO() {
        super(Chat.class);
    }

    public Chat createChat(int userId) {
        return executeTransaction(em -> {
            User user = em.find(User.class, userId);
            Chat chat = new Chat();
            chat.setUser(user);
            chat.setStatus(ChatStatus.OPEN);
            chat.setStartedAt(LocalDateTime.now());
            em.persist(chat);
            return chat;
        });
    }

    public Optional<Chat> findActiveChatByUserId(int userId) {
        return execute(em -> {
            String jpql = "SELECT DISTINCT c FROM Chat c " +
                    "LEFT JOIN FETCH c.messages " +
                    "WHERE c.user.id = :userId AND c.status IN (:openStatus, :waitingStatus) " +
                    "ORDER BY c.createdAt DESC";

            List<Chat> list = em.createQuery(jpql, Chat.class)
                    .setParameter("userId", userId)
                    .setParameter("openStatus", ChatStatus.OPEN)
                    .setParameter("waitingStatus", ChatStatus.WAITING)
                    .setMaxResults(1)
                    .getResultList();

            return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
        });
    }

    public Optional<Chat> findDetailById(int chatId) {
        return execute(em -> {
            List<Chat> list = em.createQuery(
                    "SELECT c FROM Chat c LEFT JOIN FETCH c.user WHERE c.id = :id", Chat.class)
                    .setParameter("id", chatId)
                    .setMaxResults(1)
                    .getResultList();
            return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
        });
    }

    public String findUserRoleCode(int userId) {
        return execute(em -> {
            List<String> codes = em.createQuery(
                    "SELECT r.code FROM User u JOIN u.role r " +
                    "WHERE u.id = :userId AND r.isActive = true AND r.deletedAt IS NULL", String.class)
                    .setParameter("userId", userId)
                    .setMaxResults(1)
                    .getResultList();
            return codes.isEmpty() ? null : codes.get(0);
        });
    }

    public Chat closeChat(int chatId) {
        return executeTransaction(em -> {
            Chat chat = em.find(Chat.class, chatId);
            if (chat != null) {
                chat.setStatus(ChatStatus.CLOSED);
                chat.setEndedAt(LocalDateTime.now());
            }
            return chat;
        });
    }

    public List<Chat> findConversationList(Integer userId) {
        return execute(em -> {
            StringBuilder jpql = new StringBuilder(
                    "SELECT DISTINCT c FROM Chat c " +
                    "LEFT JOIN FETCH c.user " +
                    "WHERE c.status <> :closedStatus");

            if (userId != null) {
                jpql.append(" AND c.user.id = :userId");
            }
            jpql.append(" ORDER BY c.updatedAt DESC");

            var q = em.createQuery(jpql.toString(), Chat.class)
                    .setParameter("closedStatus", ChatStatus.CLOSED);

            if (userId != null) {
                q.setParameter("userId", userId);
            }
            return q.getResultList();
        });
    }
}
```

# dao\loyalty\MessageDAO.java

| Property | Value |
|----------|-------|
| Path | `dao\loyalty\MessageDAO.java` |
| Size | 3,706 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:28.420152 |

```java
package com.fashionshop.dataaccess.dao.loyalty;

import com.fashionshop.business.entity.Chat;
import com.fashionshop.business.entity.Message;
import com.fashionshop.business.entity.User;
import com.fashionshop.dataaccess.base.AbstractJpaDAO;
import jakarta.persistence.TypedQuery;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public class MessageDAO extends AbstractJpaDAO<Message, Integer> {

    public record CursorResult(List<Message> data, Integer nextCursor, boolean hasMore) {}

    public MessageDAO() {
        super(Message.class);
    }

    public Message createMessage(int chatId, int senderId, String content) {
        return executeTransaction(em -> {
            Chat chat = em.find(Chat.class, chatId);
            User sender = em.find(User.class, senderId);

            Message message = new Message();
            message.setChat(chat);
            message.setSender(sender);
            message.setContent(content.trim());
            message.setIsRead(false);
            message.setSendDate(LocalDateTime.now());

            em.persist(message);

            // Đồng bộ updatedAt của Chat
            chat.setUpdatedAt(LocalDateTime.now());
            em.merge(chat);

            return message;
        });
    }

    /**
     * Cursor-based pagination (O(limit) seek time) khớp 100% logic findConversation Node.js
     */
    public CursorResult findConversation(int chatId, Integer cursor, int take) {
        return execute(em -> {
            StringBuilder jpql = new StringBuilder(
                    "SELECT m FROM Message m " +
                    "LEFT JOIN FETCH m.sender " +
                    "WHERE m.chat.id = :chatId");

            if (cursor != null) {
                jpql.append(" AND m.id < :cursor");
            }
            jpql.append(" ORDER BY m.id DESC");

            TypedQuery<Message> q = em.createQuery(jpql.toString(), Message.class)
                    .setParameter("chatId", chatId);

            if (cursor != null) {
                q.setParameter("cursor", cursor);
            }

            // Lấy take + 1 để xác định có trang tiếp theo không
            q.setMaxResults(take + 1);
            List<Message> messages = q.getResultList();

            boolean hasMore = messages.size() > take;
            List<Message> data = hasMore ? messages.subList(0, take) : messages;

            Integer nextCursor = (hasMore && !data.isEmpty()) ? data.get(data.size() - 1).getId() : null;

            // Đảo ngược danh sách để hiển thị theo thứ tự thời gian tăng dần
            Collections.reverse(data);

            return new CursorResult(data, nextCursor, hasMore);
        });
    }

    public int markMessagesAsRead(int chatId, int currentUserId) {
        return executeTransaction(em -> em.createQuery(
                "UPDATE Message m SET m.isRead = true " +
                "WHERE m.chat.id = :chatId AND m.isRead = false AND m.sender.id <> :currentUserId")
                .setParameter("chatId", chatId)
                .setParameter("currentUserId", currentUserId)
                .executeUpdate());
    }

    public int countUnread(int userId) {
        return execute(em -> {
            Long count = em.createQuery(
                    "SELECT COUNT(m) FROM Message m " +
                    "WHERE m.chat.user.id = :userId AND m.sender.id <> :userId AND m.isRead = false", Long.class)
                    .setParameter("userId", userId)
                    .getSingleResult();
            return count.intValue();
        });
    }
}
```

# dao\loyalty\PointTransactionDAO.java

| Property | Value |
|----------|-------|
| Path | `dao\loyalty\PointTransactionDAO.java` |
| Size | 10,224 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:28.422155 |

```java
package com.fashionshop.dataaccess.dao.loyalty;

import com.fashionshop.business.entity.Order;
import com.fashionshop.business.entity.PointTransaction;
import com.fashionshop.business.entity.User;
import com.fashionshop.common.exception.BadRequestException;
import com.fashionshop.common.exception.NotFoundException;
import com.fashionshop.dataaccess.base.AbstractJpaDAO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class PointTransactionDAO extends AbstractJpaDAO<PointTransaction, Integer> {

    public enum RedeemFailureReason {
        USER_NOT_FOUND,
        INSUFFICIENT_POINTS,
        EXCEEDS_ORDER_TOTAL
    }

    public record AtomicEarnResult(boolean isIdempotent, PointTransaction transaction, int currentPoint, int pointChange) {}
    public record AtomicRedeemResult(boolean isIdempotent, PointTransaction transaction, int currentPoint, int discountAmount, RedeemFailureReason error) {
        public static AtomicRedeemResult ok(boolean idempotent, PointTransaction tx, int points, int discount) {
            return new AtomicRedeemResult(idempotent, tx, points, discount, null);
        }
        public static AtomicRedeemResult fail(RedeemFailureReason reason, int currentPoints) {
            return new AtomicRedeemResult(false, null, currentPoints, 0, reason);
        }
    }

    public PointTransactionDAO() {
        super(PointTransaction.class);
    }

    public List<PointTransaction> findByUserId(int userId, int page, int limit) {
        return execute(em -> em.createQuery(
                "SELECT pt FROM PointTransaction pt WHERE pt.user.id = :userId ORDER BY pt.createdAt DESC", PointTransaction.class)
                .setParameter("userId", userId)
                .setFirstResult((page - 1) * limit)
                .setMaxResults(limit)
                .getResultList());
    }

    public long countByUserId(int userId) {
        return execute(em -> em.createQuery(
                "SELECT COUNT(pt) FROM PointTransaction pt WHERE pt.user.id = :userId", Long.class)
                .setParameter("userId", userId)
                .getSingleResult());
    }

    public Optional<PointTransaction> findExistingByOrderAndType(EntityManager em, int orderId, PointTransaction.PointTransType type) {
        List<PointTransaction> list = em.createQuery(
                "SELECT pt FROM PointTransaction pt WHERE pt.order.id = :orderId AND pt.transactionType = :type", PointTransaction.class)
                .setParameter("orderId", orderId)
                .setParameter("type", type)
                .setMaxResults(1)
                .getResultList();
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    /**
     * Tích điểm với Pessimistic Lock trên User và Idempotency check trên Order
     */
    public AtomicEarnResult atomicEarnPoints(int userId, int orderId, int pointChange, String description) {
        return executeTransaction(em -> {
            // Khóa Pessimistic Write chống Race Condition (tương đương FOR UPDATE)
            User lockedUser = em.find(User.class, userId, LockModeType.PESSIMISTIC_WRITE);
            if (lockedUser == null || lockedUser.getDeletedAt() != null) {
                return null;
            }

            // Kiểm tra tính Idempotent
            Optional<PointTransaction> existing = findExistingByOrderAndType(em, orderId, PointTransaction.PointTransType.EARN);
            if (existing.isPresent()) {
                return new AtomicEarnResult(true, existing.get(), lockedUser.getRewardPoints(), 0);
            }

            int newPoints = lockedUser.getRewardPoints() + pointChange;
            lockedUser.setRewardPoints(newPoints);
            em.merge(lockedUser);

            Order order = em.find(Order.class, orderId);

            PointTransaction transaction = new PointTransaction();
            transaction.setUser(lockedUser);
            transaction.setOrder(order);
            transaction.setPointChange(pointChange);
            transaction.setCurrentPoint(newPoints);
            transaction.setTransactionType(PointTransaction.PointTransType.EARN);
            transaction.setDescription(description);

            em.persist(transaction);

            return new AtomicEarnResult(false, transaction, newPoints, pointChange);
        });
    }

    /**
     * Tiêu điểm thưởng an toàn (Atomic Redeem)
     */
    public AtomicRedeemResult atomicRedeemPoints(int userId, int orderId, int pointsToRedeem, BigDecimal orderTotal, String description) {
        return executeTransaction(em -> {
            User lockedUser = em.find(User.class, userId, LockModeType.PESSIMISTIC_WRITE);
            if (lockedUser == null || lockedUser.getDeletedAt() != null) {
                return AtomicRedeemResult.fail(RedeemFailureReason.USER_NOT_FOUND, 0);
            }

            // Kiểm tra tính Idempotent
            Optional<PointTransaction> existing = findExistingByOrderAndType(em, orderId, PointTransaction.PointTransType.REDEEM);
            if (existing.isPresent()) {
                return AtomicRedeemResult.ok(true, existing.get(), lockedUser.getRewardPoints(), Math.abs(existing.get().getPointChange()));
            }

            if (lockedUser.getRewardPoints() < pointsToRedeem) {
                return AtomicRedeemResult.fail(RedeemFailureReason.INSUFFICIENT_POINTS, lockedUser.getRewardPoints());
            }

            if (BigDecimal.valueOf(pointsToRedeem).compareTo(orderTotal) > 0) {
                return AtomicRedeemResult.fail(RedeemFailureReason.EXCEEDS_ORDER_TOTAL, lockedUser.getRewardPoints());
            }

            int newPoints = lockedUser.getRewardPoints() - pointsToRedeem;
            lockedUser.setRewardPoints(newPoints);
            em.merge(lockedUser);

            Order order = em.find(Order.class, orderId);

            PointTransaction transaction = new PointTransaction();
            transaction.setUser(lockedUser);
            transaction.setOrder(order);
            transaction.setPointChange(-pointsToRedeem);
            transaction.setCurrentPoint(newPoints);
            transaction.setTransactionType(PointTransaction.PointTransType.REDEEM);
            transaction.setDescription(description);

            em.persist(transaction);

            return AtomicRedeemResult.ok(false, transaction, newPoints, pointsToRedeem);
        });
    }

    /**
     * Bồi hoàn điểm khi hủy đơn hoặc trả hàng (Defensive Cross-Module Contract)
     */
    public void compensatePointsForCancelledOrder(int orderId, EntityManager em) {
        Order order = em.find(Order.class, orderId);
        if (order == null) throw new NotFoundException("Đơn hàng #" + orderId + " không tồn tại");

        int userId = order.getUser().getId();
        User lockedUser = em.find(User.class, userId, LockModeType.PESSIMISTIC_WRITE);
        if (lockedUser == null) throw new NotFoundException("Người dùng #" + userId + " không tồn tại");

        int currentPoints = lockedUser.getRewardPoints();

        // 1. Hoàn lại số điểm khách đã dùng
        if (order.getPointUsed() != null && order.getPointUsed() > 0) {
            Optional<PointTransaction> existingRefund = findExistingByOrderAndType(em, order.getId(), PointTransaction.PointTransType.REFUND);
            if (existingRefund.isEmpty()) {
                currentPoints += order.getPointUsed();
                lockedUser.setRewardPoints(currentPoints);
                em.merge(lockedUser);

                PointTransaction refundTx = new PointTransaction();
                refundTx.setUser(lockedUser);
                refundTx.setOrder(order);
                refundTx.setPointChange(order.getPointUsed());
                refundTx.setCurrentPoint(currentPoints);
                refundTx.setTransactionType(PointTransaction.PointTransType.REFUND);
                refundTx.setDescription("Hoàn " + order.getPointUsed() + " điểm do hủy/hoàn đơn hàng #" + order.getOrderCode());
                em.persist(refundTx);
            }
        }

        // 2. Thu hồi số điểm đã tích từ đơn hàng này (nếu đã tích)
        if (order.getPointEarned() != null && order.getPointEarned() > 0) {
            Optional<PointTransaction> existingEarn = findExistingByOrderAndType(em, order.getId(), PointTransaction.PointTransType.EARN);
            if (existingEarn.isPresent()) {
                List<PointTransaction> existingRevokes = em.createQuery(
                        "SELECT pt FROM PointTransaction pt WHERE pt.order.id = :orderId AND pt.transactionType = :type AND pt.description LIKE :desc", PointTransaction.class)
                        .setParameter("orderId", order.getId())
                        .setParameter("type", PointTransaction.PointTransType.REDEEM)
                        .setParameter("desc", "%Thu hồi%")
                        .setMaxResults(1)
                        .getResultList();

                if (existingRevokes.isEmpty()) {
                    int pointsToDeduct = Math.min(currentPoints, order.getPointEarned());
                    currentPoints -= pointsToDeduct;
                    lockedUser.setRewardPoints(currentPoints);
                    em.merge(lockedUser);

                    PointTransaction revokeTx = new PointTransaction();
                    revokeTx.setUser(lockedUser);
                    revokeTx.setOrder(order);
                    revokeTx.setPointChange(-pointsToDeduct);
                    revokeTx.setCurrentPoint(currentPoints);
                    revokeTx.setTransactionType(PointTransaction.PointTransType.REDEEM);
                    revokeTx.setDescription("Thu hồi " + pointsToDeduct + " điểm thưởng do hủy/hoàn đơn hàng #" + order.getOrderCode());
                    em.persist(revokeTx);
                }
            }
        }
    }
}
```

# dao\loyalty\ProductReviewDAO.java

| Property | Value |
|----------|-------|
| Path | `dao\loyalty\ProductReviewDAO.java` |
| Size | 8,365 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:28.424151 |

```java
package com.fashionshop.dataaccess.dao.loyalty;

import com.fashionshop.business.entity.*;
import com.fashionshop.common.util.JsonUtil;
import com.fashionshop.common.util.RedisUtil;
import com.fashionshop.dataaccess.base.AbstractJpaDAO;
import com.fashionshop.presentation.dto.loyalty.CreateReviewDTO;
import com.fashionshop.presentation.dto.loyalty.ReviewQueryDTO;

import jakarta.persistence.TypedQuery;
import org.redisson.api.RBucket;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ProductReviewDAO extends AbstractJpaDAO<ProductReview, Integer> {

    public record RatingStats(double averageRating, long totalReviews) {}

    public ProductReviewDAO() {
        super(ProductReview.class);
    }

    /**
     * Xác thực người dùng đã mua sản phẩm trong đơn hàng DELIVERED (tương đương verifyDeliveredOrderProduct bên Node.js)
     */
    public boolean verifyDeliveredOrderProduct(int userId, int orderId, int productId) {
        return execute(em -> {
            String jpql = "SELECT COUNT(o) FROM Order o " +
                    "JOIN o.orderItems oi " +
                    "WHERE o.id = :orderId " +
                    "  AND o.user.id = :userId " +
                    "  AND o.status = :status " +
                    "  AND o.deletedAt IS NULL " +
                    "  AND oi.deletedAt IS NULL " +
                    "  AND oi.variant.product.id = :productId";

            Long count = em.createQuery(jpql, Long.class)
                    .setParameter("orderId", orderId)
                    .setParameter("userId", userId)
                    .setParameter("status", Order.OrderStatus.DELIVERED)
                    .setParameter("productId", productId)
                    .getSingleResult();

            return count > 0;
        });
    }

    /**
     * Kiểm tra xem đơn hàng đã từng đánh giá sản phẩm này chưa (chống P2002 Unique Constraint)
     */
    public boolean existsByOrderIdAndProductId(int orderId, int productId) {
        return execute(em -> {
            Long count = em.createQuery(
                    "SELECT COUNT(pr) FROM ProductReview pr " +
                    "WHERE pr.order.id = :orderId AND pr.product.id = :productId", Long.class)
                    .setParameter("orderId", orderId)
                    .setParameter("productId", productId)
                    .getSingleResult();
            return count > 0;
        });
    }

    /**
     * Tạo đánh giá kèm Media trong một Transaction an toàn
     */
    public ProductReview createReview(int userId, CreateReviewDTO dto) {
        return executeTransaction(em -> {
            User user = em.find(User.class, userId);
            Product product = em.find(Product.class, dto.getProductId());
            Order order = em.find(Order.class, dto.getOrderId());

            ProductReview review = new ProductReview();
            review.setUser(user);
            review.setProduct(product);
            review.setOrder(order);
            review.setRating(dto.getRating());
            review.setContent(dto.getContent().trim());
            review.setIsHidden(false);
            review.setUpdateCount(0);

            if (dto.getMediaUrls() != null) {
                for (CreateReviewDTO.MediaItemDTO item : dto.getMediaUrls()) {
                    ReviewMedia media = new ReviewMedia();
                    media.setReview(review);
                    media.setMediaUrl(item.getUrl());
                    media.setMediaType(item.getType());
                    review.getMedias().add(media);
                }
            }

            em.persist(review);
            return review;
        });
    }

    public List<ProductReview> findReviewsByProduct(ReviewQueryDTO query) {
        return execute(em -> {
            StringBuilder jpql = new StringBuilder(
                    "SELECT DISTINCT pr FROM ProductReview pr " +
                    "LEFT JOIN FETCH pr.user " +
                    "LEFT JOIN FETCH pr.medias " +
                    "WHERE pr.product.id = :productId " +
                    "  AND pr.isHidden = false " +
                    "  AND pr.deletedAt IS NULL");

            if (query.getRating() != null) {
                jpql.append(" AND pr.rating = :rating");
            }
            jpql.append(" ORDER BY pr.createdAt DESC");

            TypedQuery<ProductReview> tq = em.createQuery(jpql.toString(), ProductReview.class)
                    .setParameter("productId", query.getProductId());

            if (query.getRating() != null) {
                tq.setParameter("rating", query.getRating().shortValue());
            }

            tq.setFirstResult((query.getPage() - 1) * query.getLimit());
            tq.setMaxResults(query.getLimit());
            return tq.getResultList();
        });
    }

    public long countReviewsByProduct(ReviewQueryDTO query) {
        return execute(em -> {
            StringBuilder jpql = new StringBuilder(
                    "SELECT COUNT(pr) FROM ProductReview pr " +
                    "WHERE pr.product.id = :productId " +
                    "  AND pr.isHidden = false " +
                    "  AND pr.deletedAt IS NULL");

            if (query.getRating() != null) {
                jpql.append(" AND pr.rating = :rating");
            }

            TypedQuery<Long> tq = em.createQuery(jpql.toString(), Long.class)
                    .setParameter("productId", query.getProductId());

            if (query.getRating() != null) {
                tq.setParameter("rating", query.getRating().shortValue());
            }

            return tq.getSingleResult();
        });
    }

    public RatingStats getAverageRating(int productId) {
        return execute(em -> {
            String jpql = "SELECT AVG(pr.rating), COUNT(pr.id) FROM ProductReview pr " +
                    "WHERE pr.product.id = :productId AND pr.isHidden = false AND pr.deletedAt IS NULL";

            Object[] result = (Object[]) em.createQuery(jpql).setParameter("productId", productId).getSingleResult();

            Double avg = (Double) result[0];
            Long count = (Long) result[1];

            double roundedAvg = (avg != null && count > 0)
                    ? BigDecimal.valueOf(avg).setScale(1, RoundingMode.HALF_UP).doubleValue()
                    : 0.0;

            return new RatingStats(roundedAvg, count != null ? count : 0L);
        });
    }

    public Optional<ProductReview> findDetailById(int id) {
        return execute(em -> {
            List<ProductReview> list = em.createQuery(
                    "SELECT pr FROM ProductReview pr " +
                    "LEFT JOIN FETCH pr.user " +
                    "LEFT JOIN FETCH pr.medias " +
                    "WHERE pr.id = :id AND pr.deletedAt IS NULL", ProductReview.class)
                    .setParameter("id", id)
                    .setMaxResults(1)
                    .getResultList();
            return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
        });
    }

    /**
     * Đồng bộ Rating Snapshot vào Redis key `catalog:product:{productId}:rating` (TTL 24h)
     */
    public RatingStats syncProductRatingHook(int productId) {
        RatingStats stats = getAverageRating(productId);
        try {
            String cacheKey = "catalog:product:" + productId + ":rating";
            RBucket<String> bucket = RedisUtil.getClient().getBucket(cacheKey);

            Map<String, Object> payload = Map.of(
                    "productId", productId,
                    "averageRating", stats.averageRating(),
                    "totalReviews", stats.totalReviews(),
                    "updatedAt", LocalDateTime.now().toString()
            );

            bucket.set(JsonUtil.getMapper().writeValueAsString(payload), Duration.ofSeconds(86400));
        } catch (Exception e) {
            System.err.println(">>> [WARN] syncProductRatingHook: Redis cache sync warning: " + e.getMessage());
        }
        return stats;
    }
}
```

# dao\loyalty\ReviewMediaDAO.java

| Property | Value |
|----------|-------|
| Path | `dao\loyalty\ReviewMediaDAO.java` |
| Size | 312 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:28.426151 |

```java
package com.fashionshop.dataaccess.dao.loyalty;

import com.fashionshop.business.entity.ReviewMedia;
import com.fashionshop.dataaccess.base.AbstractJpaDAO;

public class ReviewMediaDAO extends AbstractJpaDAO<ReviewMedia, Integer> {
    public ReviewMediaDAO() {
        super(ReviewMedia.class);
    }
}
```

# dao\loyalty\VoucherDAO.java

| Property | Value |
|----------|-------|
| Path | `dao\loyalty\VoucherDAO.java` |
| Size | 11,466 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:28.428151 |

```java
package com.fashionshop.dataaccess.dao.loyalty;

import com.fashionshop.business.entity.Voucher;
import com.fashionshop.common.exception.BadRequestException;
import com.fashionshop.common.exception.NotFoundException;
import com.fashionshop.common.util.JsonUtil;
import com.fashionshop.common.util.RedisUtil;
import com.fashionshop.dataaccess.base.AbstractJpaDAO;

import jakarta.persistence.EntityManager;
import org.redisson.api.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class VoucherDAO extends AbstractJpaDAO<Voucher, Integer> {

    public static final String VOUCHER_TOTAL_KEY_PREFIX = "voucher:total:";
    public static final String VOUCHER_USAGE_KEY_PREFIX = "voucher:usage:";
    public static final String VOUCHER_META_KEY_PREFIX = "voucher:meta:";
    private static final long COLD_START_LOCK_TTL_MS = 3000;
    private static final int VOUCHER_META_CACHE_TTL_SECONDS = 30;

    public enum VoucherFailureReason {
        VOUCHER_NOT_FOUND,
        VOUCHER_INACTIVE,
        VOUCHER_NOT_STARTED,
        VOUCHER_EXPIRED,
        VOUCHER_USAGE_LIMIT_REACHED,
        USER_USAGE_LIMIT_REACHED
    }

    public record AtomicApplyResult(boolean success, Integer voucherId, String code, VoucherFailureReason reason) {
        public static AtomicApplyResult ok(Integer id, String code) {
            return new AtomicApplyResult(true, id, code, null);
        }
        public static AtomicApplyResult fail(VoucherFailureReason reason) {
            return new AtomicApplyResult(false, null, null, reason);
        }
    }

    public VoucherDAO() {
        super(Voucher.class);
    }

    public List<Voucher> findAllPaginated(int page, int limit, Boolean isActive) {
        return execute(em -> {
            StringBuilder jpql = new StringBuilder("SELECT v FROM Voucher v WHERE v.deletedAt IS NULL");
            if (isActive != null) jpql.append(" AND v.isActive = :isActive");
            jpql.append(" ORDER BY v.createdAt DESC");

            var q = em.createQuery(jpql.toString(), Voucher.class);
            if (isActive != null) q.setParameter("isActive", isActive);
            q.setFirstResult((page - 1) * limit);
            q.setMaxResults(limit);
            return q.getResultList();
        });
    }

    public long countAll(Boolean isActive) {
        return execute(em -> {
            StringBuilder jpql = new StringBuilder("SELECT COUNT(v) FROM Voucher v WHERE v.deletedAt IS NULL");
            if (isActive != null) jpql.append(" AND v.isActive = :isActive");

            var q = em.createQuery(jpql.toString(), Long.class);
            if (isActive != null) q.setParameter("isActive", isActive);
            return q.getSingleResult();
        });
    }

    public Optional<Voucher> findByCode(String code) {
        if (code == null) return Optional.empty();
        String normalized = code.trim().toUpperCase();
        return execute(em -> {
            List<Voucher> list = em.createQuery(
                    "SELECT v FROM Voucher v WHERE UPPER(v.code) = :code AND v.deletedAt IS NULL", Voucher.class)
                    .setParameter("code", normalized)
                    .setMaxResults(1)
                    .getResultList();
            return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
        });
    }

    public int countUserVoucherUsage(int userId, String voucherCode) {
        String normalized = voucherCode.trim().toUpperCase();
        String usageKey = VOUCHER_USAGE_KEY_PREFIX + normalized;

        try {
            RMap<String, Integer> map = RedisUtil.getClient().getMap(usageKey);
            Integer count = map.get(String.valueOf(userId));
            if (count != null) return count;
        } catch (Exception ignored) {}

        return execute(em -> {
            Long count = em.createQuery(
                    "SELECT COUNT(o) FROM Order o WHERE o.user.id = :userId AND UPPER(o.voucherCode) = :code AND o.deletedAt IS NULL", Long.class)
                    .setParameter("userId", userId)
                    .setParameter("code", normalized)
                    .getSingleResult();
            return count.intValue();
        });
    }

    public Voucher getVoucherMetadata(String code) {
        String normalized = code.trim().toUpperCase();
        String metaKey = VOUCHER_META_KEY_PREFIX + normalized;
        RedissonClient redisson = RedisUtil.getClient();

        try {
            RBucket<String> bucket = redisson.getBucket(metaKey);
            String cachedJson = bucket.get();
            if (cachedJson != null) {
                return JsonUtil.getMapper().readValue(cachedJson, Voucher.class);
            }
        } catch (Exception ignored) {}

        Optional<Voucher> opt = findByCode(normalized);
        if (opt.isPresent()) {
            Voucher v = opt.get();
            try {
                redisson.getBucket(metaKey).set(
                JsonUtil.getMapper().writeValueAsString(v), 
                Duration.ofSeconds(VOUCHER_META_CACHE_TTL_SECONDS)
);
            } catch (Exception ignored) {}
            return v;
        }
        return null;
    }

    public void invalidateVoucherCache(String code) {
        if (code != null) {
            RedisUtil.getClient().getBucket(VOUCHER_META_KEY_PREFIX + code.trim().toUpperCase()).delete();
        }
    }

    /**
     * Áp dụng Voucher bảo đảm Atomic & Concurrency Safe qua Redisson
     */
    public AtomicApplyResult atomicApplyVoucher(String code, int userId) {
        String normalized = code.trim().toUpperCase();
        Voucher voucher = getVoucherMetadata(normalized);

        if (voucher == null) return AtomicApplyResult.fail(VoucherFailureReason.VOUCHER_NOT_FOUND);
        if (Boolean.FALSE.equals(voucher.getIsActive())) return AtomicApplyResult.fail(VoucherFailureReason.VOUCHER_INACTIVE);

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(voucher.getStartDate())) return AtomicApplyResult.fail(VoucherFailureReason.VOUCHER_NOT_STARTED);
        if (now.isAfter(voucher.getEndDate())) return AtomicApplyResult.fail(VoucherFailureReason.VOUCHER_EXPIRED);

        RedissonClient redisson = RedisUtil.getClient();
        long ttlSeconds = Math.max(Duration.between(now, voucher.getEndDate()).getSeconds() + 86400, 3600);

        String totalKey = VOUCHER_TOTAL_KEY_PREFIX + normalized;
        String usageKey = VOUCHER_USAGE_KEY_PREFIX + normalized;
        String userStr = String.valueOf(userId);

        RAtomicLong totalCounter = redisson.getAtomicLong(totalKey);
        RMap<String, Integer> usageMap = redisson.getMap(usageKey);

        // Cold-start seed Redis nếu chưa tồn tại
        if (!totalCounter.isExists()) {
            totalCounter.set(voucher.getUsedCount());
            totalCounter.expire(Duration.ofSeconds(ttlSeconds));
        }

        if (voucher.getUserUsageLimit() > 0 && !usageMap.containsKey(userStr)) {
            RLock lock = redisson.getLock("lock:voucher:seed:" + normalized + ":" + userStr);
            try {
                if (lock.tryLock(COLD_START_LOCK_TTL_MS, TimeUnit.MILLISECONDS)) {
                    if (!usageMap.containsKey(userStr)) {
                        int priorUsage = countUserVoucherUsage(userId, normalized);
                        usageMap.put(userStr, priorUsage);
                        usageMap.expire(Duration.ofSeconds(ttlSeconds));
                    }
                }
            } catch (Exception ignored) {
            } finally {
                if (lock.isHeldByCurrentThread()) lock.unlock();
            }
        }

        // Tăng Atomic Counter
        long newTotal = totalCounter.incrementAndGet();
        Integer rawUserUsage = usageMap.addAndGet(userStr, 1);
        int newUserUsage = rawUserUsage != null ? rawUserUsage : 1;

        // Rollback nếu vượt giới hạn tổng
        if (voucher.getTotalUsageLimit() > 0 && newTotal > voucher.getTotalUsageLimit()) {
            totalCounter.decrementAndGet();
            usageMap.addAndGet(userStr, -1);
            return AtomicApplyResult.fail(VoucherFailureReason.VOUCHER_USAGE_LIMIT_REACHED);
        }

        // Rollback nếu vượt giới hạn lượt dùng của User
        if (voucher.getUserUsageLimit() > 0 && newUserUsage > voucher.getUserUsageLimit()) {
            totalCounter.decrementAndGet();
            usageMap.addAndGet(userStr, -1);
            return AtomicApplyResult.fail(VoucherFailureReason.USER_USAGE_LIMIT_REACHED);
        }

        // Cập nhật DB
        try {
            executeTransaction(em -> {
                em.createQuery("UPDATE Voucher v SET v.usedCount = v.usedCount + 1 WHERE v.id = :id")
                        .setParameter("id", voucher.getId())
                        .executeUpdate();
                return null;
            });
            return AtomicApplyResult.ok(voucher.getId(), voucher.getCode());
        } catch (Exception ex) {
            // Two-Phase Rollback
            totalCounter.decrementAndGet();
            usageMap.addAndGet(userStr, -1);
            throw ex;
        }
    }

    public void releaseVoucher(String code, int userId) {
        String normalized = code.trim().toUpperCase();
        RedissonClient redisson = RedisUtil.getClient();

        RAtomicLong totalCounter = redisson.getAtomicLong(VOUCHER_TOTAL_KEY_PREFIX + normalized);
        RMap<String, Integer> usageMap = redisson.getMap(VOUCHER_USAGE_KEY_PREFIX + normalized);

        if (totalCounter.isExists()) totalCounter.decrementAndGet();
        usageMap.addAndGet(String.valueOf(userId), -1);

        executeTransaction(em -> {
            em.createQuery("UPDATE Voucher v SET v.usedCount = v.usedCount - 1 WHERE UPPER(v.code) = :code AND v.usedCount > 0")
                    .setParameter("code", normalized)
                    .executeUpdate();
            return null;
        });
    }

    public Voucher applyVoucherSafely(String voucherCode, int userId, EntityManager em) {
        String normalized = voucherCode.trim().toUpperCase();
        Voucher voucher = getVoucherMetadata(normalized);
        if (voucher == null) throw new NotFoundException("Mã voucher không tồn tại");
        if (Boolean.FALSE.equals(voucher.getIsActive())) throw new BadRequestException("Voucher đã bị vô hiệu hóa");

        AtomicApplyResult res = atomicApplyVoucher(normalized, userId);
        if (!res.success()) {
            if (res.reason() == VoucherFailureReason.VOUCHER_USAGE_LIMIT_REACHED)
                throw new BadRequestException("Mã giảm giá đã hết lượt sử dụng toàn hệ thống");
            if (res.reason() == VoucherFailureReason.USER_USAGE_LIMIT_REACHED)
                throw new BadRequestException("Bạn đã dùng hết lượt sử dụng mã voucher này");
            throw new BadRequestException("Voucher không thể áp dụng: " + res.reason());
        }

        return em.find(Voucher.class, voucher.getId());
    }

    public void releaseVoucherSafely(String voucherCode, int userId, EntityManager em) {
        releaseVoucher(voucherCode, userId);
    }
}
```

