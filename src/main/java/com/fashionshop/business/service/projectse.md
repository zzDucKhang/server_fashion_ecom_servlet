# Project Dump

**Root:** `C:\Programing\Lap_trinh_Web\fashion_ecom\src\main\java\com\fashionshop\business\service`

**Generated:** 2026-10-12 17:11:36.069016

**Files:** 20 | **Total size:** 141,415 bytes

---

# cartorder\CartService.java

| Property | Value |
|----------|-------|
| Path | `cartorder\CartService.java` |
| Size | 8,246 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:28.348151 |

```java
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
            // Re-attach cart to current persistence context
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
            return cart;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
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
```

# cartorder\OrderService.java

| Property | Value |
|----------|-------|
| Path | `cartorder\OrderService.java` |
| Size | 19,674 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 16:06:15.281838 |

```java
package com.fashionshop.business.service.cartorder;

import com.fashionshop.business.entity.*;
import com.fashionshop.business.service.loyalty.PointService;
import com.fashionshop.business.service.loyalty.VoucherService;
import com.fashionshop.common.exception.BadRequestException;
import com.fashionshop.common.exception.ForbiddenException;
import com.fashionshop.common.exception.NotFoundException;
import com.fashionshop.common.response.PagedResponse;
import com.fashionshop.common.util.JPAUtil;
import com.fashionshop.dataaccess.dao.cartorder.OrderDAO;
import com.fashionshop.presentation.dto.cartorder.CreateOrderDTO;
import com.fashionshop.presentation.dto.cartorder.OrderQueryDTO;
import jakarta.persistence.EntityManager;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class OrderService {

    private final VoucherService voucherService = new VoucherService();
    private final PointService pointService = new PointService();
    private final OrderDAO orderDAO = new OrderDAO();

    private String generateOrderCode() {
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[4];
        random.nextBytes(bytes);
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return "ORD-" + dateStr + "-" + sb.toString();
    }

    public BigDecimal calculateShippingFee(BigDecimal subTotal, String shippingMethod, int totalWeightGram) {
        if ("FREE".equalsIgnoreCase(shippingMethod) ||
                (subTotal.compareTo(new BigDecimal("500000")) >= 0 && !"EXPRESS".equalsIgnoreCase(shippingMethod))) {
            return BigDecimal.ZERO;
        }

        if ("EXPRESS".equalsIgnoreCase(shippingMethod)) {
            BigDecimal baseFee = new BigDecimal("50000");
            int extraWeightKg = Math.max(0, (int) Math.ceil((totalWeightGram - 2000) / 1000.0));
            return baseFee.add(new BigDecimal(extraWeightKg * 10000));
        }

        BigDecimal baseStandardFee = new BigDecimal("30000");
        int extraWeightKg = Math.max(0, (int) Math.ceil((totalWeightGram - 2000) / 1000.0));
        return baseStandardFee.add(new BigDecimal(extraWeightKg * 5000));
    }

    public Order createOrder(Integer userId, CreateOrderDTO input) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            User user = em.find(User.class, userId);
            if (user == null) throw new NotFoundException("Người dùng không tồn tại");

            List<CartItem> itemsToProcess = new ArrayList<>();
            Cart cart = null;

            if (input.getDirectBuy() != null) {
                if (input.getDirectBuy().getVariantId() == null) {
                    throw new BadRequestException("Mã biến thể không được để trống");
                }
                if (input.getDirectBuy().getQuantity() == null || input.getDirectBuy().getQuantity() <= 0) {
                    throw new BadRequestException("Số lượng mua phải lớn hơn 0");
                }
                ProductVariant variant = em.find(ProductVariant.class, input.getDirectBuy().getVariantId());
                if (variant == null) throw new NotFoundException("Biến thể sản phẩm không tồn tại");

                CartItem tempItem = new CartItem();
                tempItem.setVariant(variant);
                tempItem.setQuantity(input.getDirectBuy().getQuantity());
                itemsToProcess.add(tempItem);
            } else {
                try {
                    cart = em.createQuery("SELECT c FROM Cart c LEFT JOIN FETCH c.cartItems WHERE c.user.id = :userId", Cart.class)
                            .setParameter("userId", userId)
                            .getSingleResult();
                } catch (Exception e) {
                    throw new BadRequestException("Giỏ hàng đang trống");
                }

                if (cart == null || cart.getCartItems().isEmpty()) {
                    throw new BadRequestException("Giỏ hàng của bạn đang trống");
                }

                for (CartItem ci : cart.getCartItems()) {
                    if (input.getCartItemIds() == null || input.getCartItemIds().contains(ci.getId())) {
                        itemsToProcess.add(ci);
                    }
                }
            }

            if (itemsToProcess.isEmpty()) {
                throw new BadRequestException("Không có sản phẩm nào được chọn để đặt hàng");
            }

            // Xử lý Địa chỉ Snapshot
            String addressSnapshot;
            if (input.getShippingAddressId() != null) {
                ShippingAddress sa = em.find(ShippingAddress.class, input.getShippingAddressId());
                if (sa == null) throw new BadRequestException("Địa chỉ không tồn tại");
                if (!sa.getUser().getId().equals(userId)) {
                    throw new ForbiddenException("Không có quyền sử dụng địa chỉ này");
                }
                addressSnapshot = sa.getReceiverName() + " - " + sa.getPhoneNumber() + " | "
                        + sa.getDetailAddress() + ", " + sa.getWard() + ", " + sa.getDistrict() + ", " + sa.getProvince();
            } else if (input.getShippingAddress() != null) {
                var addr = input.getShippingAddress();
                addressSnapshot = addr.getReceiverName() + " - " + addr.getPhoneNumber() + " | "
                        + addr.getDetailAddress() + ", " + addr.getWard() + ", " + addr.getDistrict() + ", " + addr.getProvince();
            } else {
                throw new BadRequestException("Thông tin địa chỉ giao hàng không hợp lệ");
            }

            // Tính tiền & trừ tồn kho với PESSIMISTIC_WRITE chống bán âm kho
            BigDecimal subTotal = BigDecimal.ZERO;
            int totalWeightGram = 0;
            List<OrderItem> orderItems = new ArrayList<>();

            for (CartItem item : itemsToProcess) {
                if (item.getQuantity() == null || item.getQuantity() <= 0) {
                    throw new BadRequestException("Số lượng sản phẩm không hợp lệ");
                }
                ProductVariant variant = em.find(ProductVariant.class, item.getVariant().getId(), jakarta.persistence.LockModeType.PESSIMISTIC_WRITE);
                if (variant == null || variant.getDeletedAt() != null) {
                    throw new BadRequestException("Sản phẩm không hợp lệ hoặc đã bị ngưng bán");
                }

                if (variant.getStockQuantity() < item.getQuantity()) {
                    throw new BadRequestException("Sản phẩm " + variant.getProduct().getDefaultName() + " không đủ tồn kho");
                }

                // Trừ tồn kho
                variant.setStockQuantity(variant.getStockQuantity() - item.getQuantity());

                BigDecimal unitPrice = variant.getSellingPrice();
                subTotal = subTotal.add(unitPrice.multiply(BigDecimal.valueOf(item.getQuantity())));
                totalWeightGram += (variant.getWeightGram() != null ? variant.getWeightGram() : 0) * item.getQuantity();

                OrderItem orderItem = new OrderItem();
                orderItem.freezeSnapshot(variant);
                orderItem.setUnitPrice(unitPrice);
                orderItem.setQuantity(item.getQuantity());
                orderItems.add(orderItem);
            }

            BigDecimal shippingFee = calculateShippingFee(subTotal, input.getShippingMethod(), totalWeightGram);
            BigDecimal voucherDiscount = BigDecimal.ZERO;

            // ĐÃ SỬA LỖI Ở ĐÂY: Lấy Object Voucher về và tự tính Discount y hệt Node.js
            if (input.getVoucherCode() != null && !input.getVoucherCode().isBlank()) {
                Voucher voucher = voucherService.applyVoucherSafely(input.getVoucherCode(), userId, em);
                
                if (subTotal.compareTo(voucher.getMinOrderValue()) < 0) {
                    voucherService.releaseVoucherSafely(input.getVoucherCode(), userId, em);
                    throw new BadRequestException("Đơn hàng tối thiểu " + voucher.getMinOrderValue().longValue() + "đ");
                }
                
                if (voucher.getDiscountType() == Voucher.DiscountType.PERCENTAGE) {
                    voucherDiscount = subTotal.multiply(voucher.getDiscountValue())
                            .divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP);
                    if (voucher.getMaxDiscountAmt() != null && voucher.getMaxDiscountAmt().compareTo(BigDecimal.ZERO) > 0) {
                        voucherDiscount = voucherDiscount.min(voucher.getMaxDiscountAmt());
                    }
                } else {
                    voucherDiscount = voucher.getDiscountValue();
                }
                
                voucherDiscount = voucherDiscount.min(subTotal);
            }

            BigDecimal pointDiscount = BigDecimal.ZERO;
            int pointUsed = 0;
            if (input.getPointUsed() != null && input.getPointUsed() > 0) {
                int userPoints = user.getRewardPoints() != null ? user.getRewardPoints() : 0;
                if (input.getPointUsed() > userPoints) {
                    throw new BadRequestException("Bạn không đủ điểm thưởng");
                }
                BigDecimal remainingPayable = subTotal.add(shippingFee).subtract(voucherDiscount).max(BigDecimal.ZERO);
                pointDiscount = new BigDecimal(input.getPointUsed()).min(remainingPayable);
                pointUsed = pointDiscount.intValue();
                user.setRewardPoints(userPoints - pointUsed);
            }

            BigDecimal totalAmount = subTotal.add(shippingFee)
                    .subtract(voucherDiscount)
                    .subtract(pointDiscount)
                    .max(BigDecimal.ZERO);

            int pointEarned = totalAmount.multiply(new BigDecimal("0.01")).setScale(0, RoundingMode.DOWN).intValue();

            // Validate và Parse PaymentMethod
            Order.PaymentMethod paymentMethod;
            try {
                if (input.getPaymentMethod() == null || input.getPaymentMethod().isBlank()) {
                    throw new BadRequestException("Phương thức thanh toán không được để trống");
                }
                paymentMethod = Order.PaymentMethod.valueOf(input.getPaymentMethod().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Phương thức thanh toán không hợp lệ: " + input.getPaymentMethod());
            }

            // Lưu đơn hàng
            Order order = new Order();
            order.setOrderCode(generateOrderCode());
            order.setUser(user);
            order.setShippingAddress(addressSnapshot);
            order.setSubTotal(subTotal);
            order.setShippingFee(shippingFee);
            order.setShippingMethod(input.getShippingMethod() != null ? input.getShippingMethod() : "STANDARD");
            order.setVoucherCode(input.getVoucherCode());
            order.setVoucherDiscount(voucherDiscount);
            order.setPointUsed(pointUsed);
            order.setPointDiscount(pointDiscount);
            order.setPointEarned(pointEarned);
            order.setTotalAmount(totalAmount);
            order.setPaymentMethod(paymentMethod);
            order.setPaymentStatus(Order.PaymentStatus.PENDING);
            order.setStatus(Order.OrderStatus.PENDING_PAYMENT);
            order.setCustomerNote(input.getCustomerNote());

            for (OrderItem oi : orderItems) {
                oi.setOrder(order);
            }
            order.setOrderItems(orderItems);

            em.persist(order);

            // Ghi nhận PointTransaction nếu có sử dụng điểm
            if (pointUsed > 0) {
                PointTransaction pt = new PointTransaction();
                pt.setUser(user);
                pt.setOrder(order);
                pt.setPointChange(-pointUsed);
                pt.setCurrentPoint(user.getRewardPoints());
                pt.setTransactionType(PointTransaction.PointTransType.REDEEM);
                pt.setDescription("Sử dụng điểm cho đơn hàng #" + order.getOrderCode());
                em.persist(pt);
            }

            // Xóa triệt để cart_items trong database bằng JPQL DELETE
            if (input.getDirectBuy() == null && !itemsToProcess.isEmpty()) {
                List<Integer> itemIds = itemsToProcess.stream()
                        .map(CartItem::getId)
                        .filter(java.util.Objects::nonNull)
                        .toList();
                if (!itemIds.isEmpty()) {
                    em.createQuery("DELETE FROM CartItem ci WHERE ci.id IN :ids")
                            .setParameter("ids", itemIds)
                            .executeUpdate();
                }
            }

            em.getTransaction().commit();
            return order;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public PagedResponse<Order> getOrders(Integer userId, OrderQueryDTO query) {
        int page = Math.max(1, query.getPage());
        int limit = Math.max(1, Math.min(100, query.getLimit()));
        int offset = (page - 1) * limit;

        List<Order> orders = orderDAO.findByUserId(userId, offset, limit, query.getStatus());
        long total = orderDAO.countByUserId(userId, query.getStatus());

        return new PagedResponse<>(orders, total, page, limit);
    }

    public Order getOrderById(Integer orderId, Integer userId) {
        Order order = orderDAO.findByIdWithItems(orderId);
        if (order == null) throw new NotFoundException("Đơn hàng không tồn tại");

        if (!order.getUser().getId().equals(userId)) {
            throw new ForbiddenException("Không có quyền truy cập đơn hàng này");
        }

        return order;
    }

    public Order cancelOrder(Integer orderId, Integer userId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            Order order = em.find(Order.class, orderId);
            if (order == null) throw new NotFoundException("Đơn hàng không tồn tại");

            if (!order.getUser().getId().equals(userId)) {
                throw new ForbiddenException("Không có quyền hủy đơn hàng này");
            }

            if (order.getStatus() != Order.OrderStatus.PENDING_PAYMENT &&
                    order.getStatus() != Order.OrderStatus.CONFIRMED &&
                    order.getStatus() != Order.OrderStatus.PROCESSING) {
                throw new BadRequestException("Không thể hủy đơn hàng ở trạng thái này");
            }

            // Hoàn lại kho cho từng item
            for (OrderItem oi : order.getOrderItems()) {
                ProductVariant variant = oi.getVariant();
                if (variant != null) {
                    variant.updateStock(oi.getQuantity());
                }
            }

            // Hoàn lại điểm nếu có dùng
            if (order.getPointUsed() != null && order.getPointUsed() > 0) {
                User user = order.getUser();
                user.addRewardPoints(order.getPointUsed());
                em.merge(user);

                PointTransaction pt = new PointTransaction();
                pt.setUser(user);
                pt.setOrder(order);
                pt.setPointChange(order.getPointUsed());
                pt.setCurrentPoint(user.getRewardPoints());
                pt.setTransactionType(PointTransaction.PointTransType.REFUND);
                pt.setDescription("Hoàn điểm do hủy đơn hàng #" + order.getOrderCode());
                em.persist(pt);
            }

            // Hoàn lại voucher nếu có dùng
            if (order.getVoucherCode() != null && !order.getVoucherCode().isBlank()) {
                voucherService.releaseVoucherSafely(order.getVoucherCode(), userId, em);
            }

            // Cập nhật paymentStatus khi hủy đơn
            if (order.getPaymentStatus() == Order.PaymentStatus.SUCCESS) {
                order.setPaymentStatus(Order.PaymentStatus.REFUNDED);
            } else if (order.getPaymentStatus() == Order.PaymentStatus.PENDING) {
                order.setPaymentStatus(Order.PaymentStatus.FAILED);
            }

            order.cancelOrder();
            em.getTransaction().commit();

            return order;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public Order updateOrderStatus(Integer orderId, String newStatusStr) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            Order order = em.find(Order.class, orderId);
            if (order == null) throw new NotFoundException("Đơn hàng không tồn tại");

            Order.OrderStatus newStatus = Order.OrderStatus.valueOf(newStatusStr);
            Order.OrderStatus oldStatus = order.getStatus();

            order.updateStatus(newStatus);

            // Tự động cộng rewardPoints cho User khi đơn hoàn thành (DELIVERED)
            if (newStatus == Order.OrderStatus.DELIVERED && oldStatus != Order.OrderStatus.DELIVERED) {
                if (order.getPointEarned() != null && order.getPointEarned() > 0) {
                    User user = order.getUser();
                    if (user != null) {
                        user.addRewardPoints(order.getPointEarned());
                        em.merge(user);

                        PointTransaction pt = new PointTransaction();
                        pt.setUser(user);
                        pt.setOrder(order);
                        pt.setPointChange(order.getPointEarned());
                        pt.setCurrentPoint(user.getRewardPoints());
                        pt.setTransactionType(PointTransaction.PointTransType.EARN);
                        pt.setDescription("Tích điểm từ đơn hàng #" + order.getOrderCode());
                        em.persist(pt);
                    }
                }
            }

            em.getTransaction().commit();
            return order;
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Trạng thái không hợp lệ: " + newStatusStr);
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}
```

# catalog\BrandService.java

| Property | Value |
|----------|-------|
| Path | `catalog\BrandService.java` |
| Size | 3,085 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:28.353152 |

```java
package com.fashionshop.business.service.catalog;

import com.fashionshop.business.entity.Brand;
import com.fashionshop.common.exception.AppException;
import com.fashionshop.common.exception.BadRequestException;
import com.fashionshop.common.exception.NotFoundException;
import com.fashionshop.common.response.PagedResponse;
import com.fashionshop.dataaccess.dao.catalog.BrandDAO;
import com.fashionshop.presentation.dto.catalog.BrandQueryDTO;
import com.fashionshop.presentation.dto.catalog.CreateBrandDTO;
import com.fashionshop.presentation.dto.catalog.UpdateBrandDTO;

import java.time.LocalDateTime;
import java.util.List;

public class BrandService {
    private final BrandDAO brandDAO = new BrandDAO();

    public PagedResponse<Brand> list(BrandQueryDTO query) {
        List<Brand> list = brandDAO.findPaginatedActive(query.getPage(), query.getLimit(), query.getSearch());
        long total = brandDAO.countActive(query.getSearch());
        return new PagedResponse<>(list, total, query.getPage(), query.getLimit());
    }

    public List<Brand> findAllActive() {
        return brandDAO.findAllActive();
    }

    public Brand findById(Integer id) {
        return brandDAO.findById(id)
                .filter(b -> b.getDeletedAt() == null)
                .orElseThrow(() -> new NotFoundException("Thương hiệu không tồn tại hoặc đã bị xóa"));
    }

    public Brand create(Integer createdById, CreateBrandDTO dto) {
        if (dto.getDefaultName() == null || dto.getDefaultName().trim().isEmpty()) {
            throw new BadRequestException("Tên thương hiệu không được để trống");
        }

        String name = dto.getDefaultName().trim();
        if (brandDAO.findByName(name).isPresent()) {
            throw new AppException("Tên thương hiệu đã tồn tại", 422);
        }

        Brand brand = new Brand();
        brand.setDefaultName(name);
        brand.setLogoUrl(dto.getLogo());
        brand.setCreatedById(createdById);

        return brandDAO.save(brand);
    }

    public Brand update(Integer id, Integer updatedById, UpdateBrandDTO dto) {
        Brand brand = findById(id);

        if (dto.getDefaultName() != null && !dto.getDefaultName().trim().isEmpty()) {
            String newName = dto.getDefaultName().trim();
            brandDAO.findByName(newName).ifPresent(existing -> {
                if (!existing.getId().equals(id)) {
                    throw new AppException("Tên thương hiệu đã tồn tại", 422);
                }
            });
            brand.setDefaultName(newName);
        }

        if (dto.getLogo() != null) {
            brand.setLogoUrl(dto.getLogo());
        }

        brand.setUpdatedById(updatedById);
        return brandDAO.update(brand);
    }

    public void delete(Integer id, Integer deletedById) {
        Brand brand = findById(id);
        brand.setDeletedAt(LocalDateTime.now());
        brand.setDeletedById(deletedById);
        brandDAO.update(brand);
    }
}


```

# catalog\CategoryService.java

| Property | Value |
|----------|-------|
| Path | `catalog\CategoryService.java` |
| Size | 3,496 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:28.354151 |

```java
package com.fashionshop.business.service.catalog;

import com.fashionshop.business.entity.Category;
import com.fashionshop.common.exception.AppException;
import com.fashionshop.common.exception.BadRequestException;
import com.fashionshop.common.exception.NotFoundException;
import com.fashionshop.dataaccess.dao.catalog.CategoryDAO;
import com.fashionshop.presentation.dto.catalog.CreateCategoryDTO;
import com.fashionshop.presentation.dto.catalog.UpdateCategoryDTO;

import java.time.LocalDateTime;
import java.util.List;

public class CategoryService {
    private final CategoryDAO categoryDAO = new CategoryDAO();

    public List<Category> findAll(Integer parentCategoryId) {
        if (parentCategoryId != null) {
            return categoryDAO.findByParentId(parentCategoryId);
        }
        return categoryDAO.findAllActive();
    }

    public Category findById(Integer id) {
        return categoryDAO.findById(id)
                .filter(c -> c.getDeletedAt() == null)
                .orElseThrow(() -> new NotFoundException("Danh mục không tồn tại hoặc đã bị xóa"));
    }

    public Category create(Integer createdById, CreateCategoryDTO dto) {
        if (dto.getDefaultName() == null || dto.getDefaultName().trim().isEmpty()) {
            throw new BadRequestException("Tên danh mục không được để trống");
        }

        String name = dto.getDefaultName().trim();
        if (categoryDAO.findByName(name).isPresent()) {
            throw new AppException("Danh mục đã tồn tại", 422);
        }

        Category category = new Category();
        category.setDefaultName(name);
        category.setIconUrl(dto.getIconUrl());
        category.setCreatedById(createdById);

        if (dto.getParentCategoryId() != null) {
            Category parent = findById(dto.getParentCategoryId());
            category.setParent(parent);
        }

        return categoryDAO.save(category);
    }

    public Category update(Integer id, Integer updatedById, UpdateCategoryDTO dto) {
        Category category = findById(id);

        if (dto.getDefaultName() != null && !dto.getDefaultName().trim().isEmpty()) {
            String newName = dto.getDefaultName().trim();
            categoryDAO.findByName(newName).ifPresent(existing -> {
                if (!existing.getId().equals(id)) {
                    throw new AppException("Danh mục đã tồn tại", 422);
                }
            });
            category.setDefaultName(newName);
        }

        if (dto.getIconUrl() != null) {
            category.setIconUrl(dto.getIconUrl());
        }

        if (dto.getIsVisible() != null) {
            category.setIsVisible(dto.getIsVisible());
        }

        if (dto.getParentCategoryId() != null) {
            if (dto.getParentCategoryId().equals(id)) {
                throw new BadRequestException("Danh mục cha không thể là chính nó");
            }
            Category newParent = findById(dto.getParentCategoryId());
            category.setParent(newParent);
        }

        category.setUpdatedById(updatedById);
        return categoryDAO.update(category);
    }

    public void delete(Integer id, Integer deletedById) {
        Category category = findById(id);
        category.setDeletedAt(LocalDateTime.now());
        category.setDeletedById(deletedById);
        categoryDAO.update(category);
    }
}


```

# catalog\ProductService.java

| Property | Value |
|----------|-------|
| Path | `catalog\ProductService.java` |
| Size | 5,525 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 11:39:34.670985 |

```java
package com.fashionshop.business.service.catalog;

import com.fashionshop.business.entity.*;
import com.fashionshop.common.exception.AppException;
import com.fashionshop.common.exception.BadRequestException;
import com.fashionshop.common.exception.NotFoundException;
import com.fashionshop.common.response.PagedResponse;
import com.fashionshop.dataaccess.dao.catalog.BrandDAO;
import com.fashionshop.dataaccess.dao.catalog.CategoryDAO;
import com.fashionshop.dataaccess.dao.catalog.ProductDAO;
import com.fashionshop.dataaccess.dao.catalog.TagDAO;
import com.fashionshop.presentation.dto.catalog.CreateProductDTO;
import com.fashionshop.presentation.dto.catalog.ProductQueryDTO;
import com.fashionshop.presentation.dto.catalog.UpdateProductDTO;

import java.time.LocalDateTime;
import java.util.List;

public class ProductService {
    private final ProductDAO productDAO = new ProductDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final BrandDAO brandDAO = new BrandDAO();
    private final TagDAO tagDAO = new TagDAO();

    public PagedResponse<Product> list(ProductQueryDTO query) {
        List<Product> list = productDAO.listProducts(query);
        long total = productDAO.countProducts(query);
        return new PagedResponse<>(list, total, query.getPage(), query.getLimit());
    }

    public Product findById(Integer id) {
        return productDAO.findDetailById(id)
                .orElseThrow(() -> new NotFoundException("Sản phẩm không tồn tại hoặc đã bị xóa"));
    }

    public Product create(Integer createdById, CreateProductDTO dto) {
        if (dto.getDefaultName() == null || dto.getDefaultName().trim().isEmpty()) {
            throw new BadRequestException("Tên sản phẩm không được để trống");
        }
        if (dto.getCategoryId() == null) {
            throw new BadRequestException("Danh mục là bắt buộc");
        }

        // Bắt lỗi trùng lặp (Status 422 giống Node.js)
        if (productDAO.findByName(dto.getDefaultName().trim()).isPresent()) {
            throw new AppException("Tên sản phẩm bị trùng lặp", 422);
        }

        Category category = categoryDAO.findById(dto.getCategoryId())
                .orElseThrow(() -> new NotFoundException("Danh mục không tồn tại"));

        Product product = new Product();
        product.setDefaultName(dto.getDefaultName().trim());
        product.setThumbnailUrl(dto.getThumbnailUrl());
        product.setStatus(dto.getStatus() != null ? dto.getStatus() : Product.ProductStatus.DRAFT);
        product.setCategory(category);
        product.setCreatedById(createdById);

        if (dto.getBrandId() != null) {
            Brand brand = brandDAO.findById(dto.getBrandId())
                    .orElseThrow(() -> new NotFoundException("Thương hiệu không tồn tại"));
            product.setBrand(brand);
        }

        // Gán danh sách hình ảnh (DisplayOrder từ 0 -> n)
        if (dto.getImages() != null) {
            for (int i = 0; i < dto.getImages().size(); i++) {
                product.addImage(dto.getImages().get(i), i);
            }
        }

        // Gán Tags
        if (dto.getTagIds() != null && !dto.getTagIds().isEmpty()) {
            product.setTags(tagDAO.findByIds(dto.getTagIds()));
        }

        return productDAO.save(product);
    }

    public Product update(Integer id, Integer updatedById, UpdateProductDTO dto) {
        Product product = findById(id);

        if (dto.getDefaultName() != null && !dto.getDefaultName().trim().isEmpty()) {
            String newName = dto.getDefaultName().trim();
            productDAO.findByName(newName).ifPresent(existing -> {
                if (!existing.getId().equals(id)) {
                    throw new AppException("Tên sản phẩm bị trùng lặp", 422);
                }
            });
            product.setDefaultName(newName);
        }

        if (dto.getThumbnailUrl() != null) product.setThumbnailUrl(dto.getThumbnailUrl());
        if (dto.getStatus() != null) product.setStatus(dto.getStatus());

        if (dto.getCategoryId() != null) {
            Category category = categoryDAO.findById(dto.getCategoryId())
                    .orElseThrow(() -> new NotFoundException("Danh mục không tồn tại"));
            product.setCategory(category);
        }

        if (dto.getBrandId() != null) {
            Brand brand = brandDAO.findById(dto.getBrandId())
                    .orElseThrow(() -> new NotFoundException("Thương hiệu không tồn tại"));
            product.setBrand(brand);
        }

        // Cập nhật lại Tags
        if (dto.getTagIds() != null) {
            product.setTags(tagDAO.findByIds(dto.getTagIds()));
        }

        // Cập nhật lại danh sách hình ảnh
        if (dto.getImages() != null) {
            product.getImages().clear();
            for (int i = 0; i < dto.getImages().size(); i++) {
                product.addImage(dto.getImages().get(i), i);
            }
        }

        product.setUpdatedById(updatedById);
        return productDAO.update(product);
    }

    public void delete(Integer id, Integer deletedById) {
        Product product = findById(id);
        product.setDeletedAt(LocalDateTime.now());
        product.setDeletedById(deletedById);
        productDAO.update(product);
    }
}
```

# catalog\TagService.java

| Property | Value |
|----------|-------|
| Path | `catalog\TagService.java` |
| Size | 2,685 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:28.356153 |

```java
package com.fashionshop.business.service.catalog;

import com.fashionshop.business.entity.Tag;
import com.fashionshop.common.exception.AppException;
import com.fashionshop.common.exception.BadRequestException;
import com.fashionshop.common.exception.NotFoundException;
import com.fashionshop.common.response.PagedResponse;
import com.fashionshop.dataaccess.dao.catalog.TagDAO;
import com.fashionshop.presentation.dto.catalog.CreateTagDTO;
import com.fashionshop.presentation.dto.catalog.TagQueryDTO;
import com.fashionshop.presentation.dto.catalog.UpdateTagDTO;

import java.time.LocalDateTime;
import java.util.List;

public class TagService {
    private final TagDAO tagDAO = new TagDAO();

    public PagedResponse<Tag> list(TagQueryDTO query) {
        List<Tag> list = tagDAO.findPaginatedActive(query.getPage(), query.getLimit(), query.getSearch());
        long total = tagDAO.countActive(query.getSearch());
        return new PagedResponse<>(list, total, query.getPage(), query.getLimit());
    }

    public Tag findById(Integer id) {
        return tagDAO.findById(id)
                .filter(t -> t.getDeletedAt() == null)
                .orElseThrow(() -> new NotFoundException("Thẻ Tag không tồn tại hoặc đã bị xóa"));
    }

    public Tag create(Integer createdById, CreateTagDTO dto) {
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new BadRequestException("Tên tag không được để trống");
        }

        String name = dto.getName().trim();
        if (tagDAO.findByName(name).isPresent()) {
            throw new AppException("Tên thẻ Tag đã tồn tại", 422);
        }

        Tag tag = new Tag();
        tag.setName(name);
        tag.setCreatedById(createdById);

        return tagDAO.save(tag);
    }

    public Tag update(Integer id, Integer updatedById, UpdateTagDTO dto) {
        Tag tag = findById(id);

        if (dto.getName() != null && !dto.getName().trim().isEmpty()) {
            String newName = dto.getName().trim();
            tagDAO.findByName(newName).ifPresent(existing -> {
                if (!existing.getId().equals(id)) {
                    throw new AppException("Tên thẻ Tag đã tồn tại", 422);
                }
            });
            tag.setName(newName);
        }

        tag.setUpdatedById(updatedById);
        return tagDAO.update(tag);
    }

    public void delete(Integer id, Integer deletedById) {
        Tag tag = findById(id);
        tag.setDeletedAt(LocalDateTime.now());
        tag.setDeletedById(deletedById);
        tagDAO.update(tag);
    }
}


```

# catalog\VariantService.java

| Property | Value |
|----------|-------|
| Path | `catalog\VariantService.java` |
| Size | 3,692 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:28.358150 |

```java
package com.fashionshop.business.service.catalog;

import com.fashionshop.business.entity.ProductVariant;
import com.fashionshop.common.exception.AppException;
import com.fashionshop.common.exception.BadRequestException;
import com.fashionshop.common.exception.NotFoundException;
import com.fashionshop.dataaccess.dao.catalog.ProductDAO;
import com.fashionshop.dataaccess.dao.catalog.ProductVariantDAO;
import com.fashionshop.presentation.dto.catalog.SyncVariantsDTO;
import com.fashionshop.presentation.dto.catalog.UpsertVariantItemDTO;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class VariantService {
    private final ProductVariantDAO variantDAO = new ProductVariantDAO();
    private final ProductDAO productDAO = new ProductDAO();

    public List<ProductVariant> getVariantsByProductId(Integer productId) {
        productDAO.findById(productId)
                .filter(p -> p.getDeletedAt() == null)
                .orElseThrow(() -> new NotFoundException("Sản phẩm không tồn tại hoặc đã bị xóa"));
        return variantDAO.findByProductId(productId);
    }

    public ProductVariant getVariantById(Integer variantId) {
        return variantDAO.findActiveById(variantId)
                .orElseThrow(() -> new NotFoundException("Biến thể không tồn tại hoặc đã bị vô hiệu hóa"));
    }

    public List<ProductVariant> syncProductVariants(SyncVariantsDTO data, Integer userId) {
        if (data.getProductId() == null) {
            throw new BadRequestException("productId phải là số nguyên dương");
        }
        if (data.getVariants() == null || data.getVariants().isEmpty()) {
            throw new BadRequestException("Danh sách biến thể không được để trống");
        }

        productDAO.findById(data.getProductId())
                .filter(p -> p.getDeletedAt() == null)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy sản phẩm tương ứng để đồng bộ biến thể"));

        Set<String> skuSet = new HashSet<>();
        for (int i = 0; i < data.getVariants().size(); i++) {
            UpsertVariantItemDTO item = data.getVariants().get(i);
            if (item.getSku() == null || item.getSku().trim().isEmpty()) {
                throw new BadRequestException("Mã SKU không được để trống tại dòng " + (i + 1));
            }
            String skuLower = item.getSku().trim().toLowerCase();
            if (skuSet.contains(skuLower)) {
                throw new AppException("Mã SKU bị trùng lặp trong danh sách: " + item.getSku(), 422);
            }
            skuSet.add(skuLower);

            if (item.getOriginalPrice() == null || item.getOriginalPrice().doubleValue() <= 0) {
                throw new BadRequestException("Giá gốc phải lớn hơn 0 tại SKU " + item.getSku());
            }
            if (item.getSellingPrice() == null || item.getSellingPrice().doubleValue() <= 0) {
                throw new BadRequestException("Giá bán phải lớn hơn 0 tại SKU " + item.getSku());
            }
            if (item.getSellingPrice().compareTo(item.getOriginalPrice()) > 0) {
                throw new BadRequestException("Giá bán (" + item.getSellingPrice() + ") không được lớn hơn giá gốc (" + item.getOriginalPrice() + ") tại SKU " + item.getSku());
            }
        }

        return variantDAO.syncVariants(data, userId);
    }

    public ProductVariant updateStock(Integer variantId, int delta) {
        return variantDAO.updateStockOptimistic(variantId, delta);
    }
}


```

# fulfillment\PaymentService.java

| Property | Value |
|----------|-------|
| Path | `fulfillment\PaymentService.java` |
| Size | 12,070 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:28.360153 |

```java
package com.fashionshop.business.service.fulfillment;

import com.fashionshop.business.entity.Order;
import com.fashionshop.business.entity.PaymentTransaction;
import com.fashionshop.common.exception.BadRequestException;
import com.fashionshop.common.exception.ForbiddenException;
import com.fashionshop.common.exception.NotFoundException;
import com.fashionshop.common.util.JPAUtil;
import com.fashionshop.config.AppConfig;
import com.fashionshop.dataaccess.dao.fulfillment.PaymentTransactionDAO;
import com.fashionshop.presentation.dto.fulfillment.PaymentInfoDTO;
import com.fashionshop.presentation.dto.fulfillment.PaymentStatusDTO;
import com.fashionshop.presentation.dto.fulfillment.SepayWebhookDTO;
import jakarta.persistence.EntityManager;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PaymentService {

    private final PaymentTransactionDAO paymentDAO = new PaymentTransactionDAO();
    
    public boolean verifySePaySignature(String signatureHeader, String rawPayload) {
        if (signatureHeader == null || signatureHeader.trim().isEmpty() || rawPayload == null) {
            return false;
        }
        
        String secret = AppConfig.SEPAY_WEBHOOK_SECRET;
        if (secret == null || secret.trim().isEmpty()) {
            return false; // Bắt buộc cấu hình SEPAY_WEBHOOK_SECRET
        }

        try {
            Mac hmacSha256 = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            hmacSha256.init(secretKey);
            byte[] hash = hmacSha256.doFinal(rawPayload.getBytes(StandardCharsets.UTF_8));

            String calculatedBase64 = Base64.getEncoder().encodeToString(hash);
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            String calculatedHex = sb.toString();

            String cleanSig = signatureHeader.trim();
            if (cleanSig.startsWith("sha256=")) {
                cleanSig = cleanSig.substring(7);
            }

            return MessageDigest.isEqual(cleanSig.getBytes(StandardCharsets.UTF_8), calculatedBase64.getBytes(StandardCharsets.UTF_8))
                    || MessageDigest.isEqual(cleanSig.toLowerCase().getBytes(StandardCharsets.UTF_8), calculatedHex.toLowerCase().getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            return false;
        }
    }

    public boolean verifySePaySignature(byte[] rawBody, String signatureHeader, String timestamp) {
        if (rawBody == null || signatureHeader == null || signatureHeader.isBlank()) {
            return false;
        }

        // Bắt buộc xác thực timestamp webhook nếu có gửi (chống replay attack trong 5 phút)
        if (timestamp != null && !timestamp.isBlank()) {
            try {
                long reqTime = Long.parseLong(timestamp.trim());
                if (reqTime < 10000000000L) {
                    reqTime *= 1000L;
                }
                long currentTime = System.currentTimeMillis();
                if (Math.abs(currentTime - reqTime) > 5 * 60 * 1000L) {
                    return false;
                }
            } catch (NumberFormatException e) {
                return false;
            }
        }

        String rawPayload = new String(rawBody, StandardCharsets.UTF_8);
        return verifySePaySignature(signatureHeader, rawPayload);
    }

    public void handleSePayWebhook(SepayWebhookDTO payload) {
        if (!"in".equalsIgnoreCase(payload.getTransferType())) {
            throw new BadRequestException("Không phải giao dịch tiền vào");
        }

        // Kiểm tra số tài khoản nhận tiền
        String configuredAccount = AppConfig.SEPAY_ACCOUNT_NUMBER;
        if (configuredAccount != null && !configuredAccount.isBlank()) {
            if (payload.getAccountNumber() == null || !configuredAccount.trim().equalsIgnoreCase(payload.getAccountNumber().trim())) {
                throw new BadRequestException("Số tài khoản thụ hưởng không chính xác");
            }
        }

        String content = payload.getCode() != null && !payload.getCode().isBlank() ? payload.getCode() : payload.getContent();
        if (content == null || content.isBlank()) {
            throw new BadRequestException("Không tìm thấy nội dung chuyển khoản");
        }

        Pattern pattern = Pattern.compile("DH(\\d+)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(content);
        if (!matcher.find()) {
            throw new NotFoundException("Mã đơn hàng không hợp lệ trong nội dung chuyển khoản");
        }

        Integer orderId = Integer.parseInt(matcher.group(1));

        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            // Kiểm tra Idempotency theo ID giao dịch SePay (tránh xử lý webhook trùng)
            if (payload.getId() != null) {
                String providerTransId = String.valueOf(payload.getId());
                Long existingCount = em.createQuery(
                        "SELECT COUNT(p) FROM PaymentTransaction p WHERE p.providerTransId = :providerTransId", Long.class)
                        .setParameter("providerTransId", providerTransId)
                        .getSingleResult();
                if (existingCount != null && existingCount > 0) {
                    em.getTransaction().rollback();
                    return; // Giao dịch này đã được ghi nhận trước đó (Idempotent)
                }
            }

            Order order = em.find(Order.class, orderId);
            if (order == null) {
                throw new NotFoundException("Không tìm thấy đơn hàng cho mã DH" + orderId);
            }

            if (order.getPaymentStatus() == Order.PaymentStatus.SUCCESS) {
                em.getTransaction().rollback();
                return; // Đã xử lý thanh toán
            }

            if (order.getTotalAmount().compareTo(payload.getTransferAmount()) > 0) {
                throw new BadRequestException("Số tiền thanh toán không đủ");
            }

            PaymentTransaction payment = paymentDAO.findPendingByOrderId(orderId);
            if (payment != null) {
                // Đã có pending: cập nhật trạng thái SUCCESS
                payment.setStatus(Order.PaymentStatus.SUCCESS);
                payment.setProviderTransId(String.valueOf(payload.getId()));
                em.merge(payment);
            } else {
                // Khách quét QR thanh toán trực tiếp không qua /initiate: tự tạo PaymentTransaction
                PaymentTransaction newTx = new PaymentTransaction();
                newTx.setOrder(order);
                newTx.setProvider("SEPAY");
                newTx.setAmount(payload.getTransferAmount());
                newTx.setStatus(Order.PaymentStatus.SUCCESS);
                newTx.setProviderTransId(String.valueOf(payload.getId()));
                em.persist(newTx);
            }

            // Cập nhật trạng thái Order
            order.setPaymentStatus(Order.PaymentStatus.SUCCESS);
            if (order.getStatus() == Order.OrderStatus.PENDING_PAYMENT) {
                order.setStatus(Order.OrderStatus.PROCESSING);
            }
            em.merge(order);

            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public PaymentTransaction initiatePayment(Integer orderId, Integer userId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Order order = em.find(Order.class, orderId);
            if (order == null) throw new NotFoundException("Đơn hàng không tồn tại");

            if (!order.getUser().getId().equals(userId)) {
                throw new ForbiddenException("Không có quyền truy cập đơn hàng này");
            }

            if (order.getPaymentStatus() == Order.PaymentStatus.SUCCESS) {
                throw new BadRequestException("Đơn hàng đã được thanh toán");
            }

            PaymentTransaction existingTx = paymentDAO.findByOrderId(orderId);
            if (existingTx != null) {
                if (existingTx.getStatus() == Order.PaymentStatus.PENDING) {
                    return existingTx;
                }
                existingTx.setStatus(Order.PaymentStatus.PENDING);
                existingTx.setAmount(order.getTotalAmount());
                existingTx.setProvider("SEPAY");
                return paymentDAO.update(existingTx);
            }

            PaymentTransaction tx = new PaymentTransaction();
            tx.setOrder(order);
            tx.setProvider("SEPAY");
            tx.setAmount(order.getTotalAmount());
            tx.setStatus(Order.PaymentStatus.PENDING);

            return paymentDAO.create(tx);
        } finally {
            em.close();
        }
    }

    public PaymentInfoDTO getPaymentInfo(Integer orderId, Integer userId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Order order = em.find(Order.class, orderId);
            if (order == null) throw new NotFoundException("Đơn hàng không tồn tại");

            if (!order.getUser().getId().equals(userId)) {
                throw new ForbiddenException("Không có quyền truy cập đơn hàng này");
            }

            String acc = AppConfig.get("SEPAY_ACCOUNT_NUMBER", "");
            String bank = AppConfig.get("SEPAY_BANK_CODE", "MBBank");
            String qrBaseUrl = AppConfig.get("SEPAY_QR_BASE_URL", "https://qr.sepay.vn/img");
            String content = "DH" + order.getId();

            String qrUrl = String.format("%s?acc=%s&bank=%s&amount=%s&des=%s",
                    qrBaseUrl, acc, bank, order.getTotalAmount().toPlainString(),
                    URLEncoder.encode(content, StandardCharsets.UTF_8));

            PaymentInfoDTO dto = new PaymentInfoDTO();
            dto.setOrderId(order.getId());
            dto.setOrderCode(order.getOrderCode());
            dto.setAmount(order.getTotalAmount());
            dto.setPaymentMethod(order.getPaymentMethod().name());
            dto.setPaymentStatus(order.getPaymentStatus().name());
            dto.setQrUrl(qrUrl);
            dto.setBankName(bank);
            dto.setAccountNumber(acc);
            dto.setTransferContent(content);

            return dto;
        } finally {
            em.close();
        }
    }

    public PaymentStatusDTO getPaymentStatus(Integer orderId, Integer userId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Order order = em.find(Order.class, orderId);
            if (order == null) throw new NotFoundException("Đơn hàng không tồn tại");

            if (!order.getUser().getId().equals(userId)) {
                throw new ForbiddenException("Không có quyền truy cập đơn hàng này");
            }

            PaymentStatusDTO dto = new PaymentStatusDTO();
            dto.setOrderId(order.getId());
            dto.setPaymentStatus(order.getPaymentStatus().name());
            dto.setOrderStatus(order.getStatus().name());

            return dto;
        } finally {
            em.close();
        }
    }
}
```

# fulfillment\ReturnService.java

| Property | Value |
|----------|-------|
| Path | `fulfillment\ReturnService.java` |
| Size | 2,356 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:28.361153 |

```java
package com.fashionshop.business.service.fulfillment;

import com.fashionshop.business.entity.OrderItem;
import com.fashionshop.business.entity.ReturnRequest;
import com.fashionshop.common.exception.AppException;
import com.fashionshop.common.exception.NotFoundException;
import com.fashionshop.dataaccess.dao.cartorder.OrderItemDAO;
import com.fashionshop.dataaccess.dao.fulfillment.ReturnRequestDAO;
import com.fashionshop.presentation.dto.fulfillment.CreateReturnDTO;
import com.fashionshop.presentation.dto.fulfillment.ReturnQueryDTO;
import com.fashionshop.presentation.dto.fulfillment.UpdateReturnDTO;

import java.util.List;

public class ReturnService {
    private final ReturnRequestDAO returnRepo = new ReturnRequestDAO();
    private final OrderItemDAO orderItemDAO = new OrderItemDAO();

    public List<ReturnRequest> findAll(ReturnQueryDTO query) {
        return returnRepo.findAllWithFilter(query);
    }

    public ReturnRequest findById(int id) {
        return returnRepo.findDetailById(id)
                .orElseThrow(() -> new NotFoundException("Yêu cầu trả hàng không tồn tại"));
    }

    public ReturnRequest create(CreateReturnDTO data) {
        OrderItem orderItem = orderItemDAO.findById(data.getOrderItemId())
                .orElseThrow(() -> new NotFoundException("Dòng đơn hàng không tồn tại"));

        if (data.getQuantity() > orderItem.getQuantity()) {
            throw new AppException("Số lượng trả không được vượt quá số lượng đã mua", 422);
        }

        return returnRepo.create(data);
    }

    public ReturnRequest update(int id, UpdateReturnDTO data) {
        ReturnRequest existing = findById(id);

        if (data.getQuantity() != null && data.getQuantity() > existing.getOrderItem().getQuantity()) {
            throw new AppException("Số lượng trả không được vượt quá số lượng đã mua", 422);
        }

        return returnRepo.update(id, data);
    }

    public void delete(int id) {
        ReturnRequest existing = findById(id);

        if (existing.getStatus() != ReturnRequest.ReturnStatus.PENDING) {
            throw new AppException("Chỉ có thể xóa yêu cầu trả hàng đang chờ xử lý", 422);
        }

        returnRepo.softDelete(id);
    }
}
```

# fulfillment\ShipmentService.java

| Property | Value |
|----------|-------|
| Path | `fulfillment\ShipmentService.java` |
| Size | 5,668 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:28.362153 |

```java
package com.fashionshop.business.service.fulfillment;

import com.fashionshop.business.entity.Order;
import com.fashionshop.business.entity.Shipment;
import com.fashionshop.business.service.cartorder.OrderService;
import com.fashionshop.common.exception.BadRequestException;
import com.fashionshop.common.exception.ForbiddenException;
import com.fashionshop.common.exception.NotFoundException;
import com.fashionshop.common.response.PagedResponse;
import com.fashionshop.common.util.JPAUtil;
import com.fashionshop.dataaccess.dao.fulfillment.ShipmentDAO;
import com.fashionshop.presentation.dto.fulfillment.CreateShipmentDTO;
import com.fashionshop.presentation.dto.fulfillment.ShipmentQueryDTO;
import com.fashionshop.presentation.dto.fulfillment.UpdateShipmentDTO;
import jakarta.persistence.EntityManager;

import java.time.LocalDateTime;
import java.util.List;

public class ShipmentService {

    private final ShipmentDAO shipmentDAO = new ShipmentDAO();
    private final OrderService orderService = new OrderService();

    public Shipment createShipment(CreateShipmentDTO dto) {
        if (dto.getOrderId() == null) {
            throw new BadRequestException("Mã đơn hàng không được để trống");
        }
        if (dto.getCarrier() == null || dto.getCarrier().isBlank()) {
            throw new BadRequestException("Đơn vị vận chuyển không được để trống");
        }
        if (dto.getTrackingCode() == null || dto.getTrackingCode().isBlank()) {
            throw new BadRequestException("Mã vận đơn không được để trống");
        }

        EntityManager em = JPAUtil.getEntityManager();
        try {
            Order order = em.find(Order.class, dto.getOrderId());
            if (order == null) throw new NotFoundException("Đơn hàng không tồn tại");

            Shipment existing = shipmentDAO.findByOrderId(dto.getOrderId());
            if (existing != null) {
                throw new BadRequestException("Đơn hàng đã có thông tin vận chuyển");
            }

            Shipment shipment = new Shipment();
            shipment.setOrder(order);
            shipment.setCarrier(dto.getCarrier());
            shipment.setTrackingCode(dto.getTrackingCode());
            shipment.setActualShippingFee(dto.getActualShippingFee() != null ? dto.getActualShippingFee() : order.getShippingFee());
            shipment.setStatus(Shipment.ShipmentStatus.PENDING);

            return shipmentDAO.create(shipment);
        } finally {
            em.close();
        }
    }

    public Shipment getShipmentByOrderId(Integer orderId, Integer userId, boolean isAdmin) {
        Shipment shipment = shipmentDAO.findByOrderId(orderId);
        if (shipment == null) throw new NotFoundException("Thông tin vận chuyển không tồn tại");
        if (!isAdmin && (userId == null || !shipment.getOrder().getUser().getId().equals(userId))) {
            throw new ForbiddenException("Không có quyền xem thông tin vận chuyển này");
        }
        return shipment;
    }

    public Shipment getShipmentByOrderId(Integer orderId) {
        return getShipmentByOrderId(orderId, null, true);
    }

    public Shipment getShipmentById(Integer id, Integer userId, boolean isAdmin) {
        Shipment shipment = shipmentDAO.findById(id);
        if (shipment == null) throw new NotFoundException("Thông tin vận chuyển không tồn tại");
        if (!isAdmin && (userId == null || !shipment.getOrder().getUser().getId().equals(userId))) {
            throw new ForbiddenException("Không có quyền xem thông tin vận đơn này");
        }
        return shipment;
    }

    public Shipment getShipmentById(Integer id) {
        return getShipmentById(id, null, true);
    }

    public void updateShipmentStatus(Integer shipmentId, UpdateShipmentDTO dto) {
        Shipment shipment = shipmentDAO.findById(shipmentId);
        if (shipment == null) throw new NotFoundException("Vận đơn không tồn tại");

        if (dto.getStatus() == null || dto.getStatus().isBlank()) {
            throw new BadRequestException("Trạng thái vận đơn không được để trống");
        }

        try {
            Shipment.ShipmentStatus newStatus = Shipment.ShipmentStatus.valueOf(dto.getStatus().toUpperCase());
            shipmentDAO.updateStatus(shipmentId, newStatus, dto.getTrackingCode());
            if (shipment.getOrder() != null) {
                if (newStatus == Shipment.ShipmentStatus.DELIVERED) {
                    orderService.updateOrderStatus(shipment.getOrder().getId(), Order.OrderStatus.DELIVERED.name());
                } else if (newStatus == Shipment.ShipmentStatus.IN_TRANSIT || newStatus == Shipment.ShipmentStatus.PICKED_UP) {
                    orderService.updateOrderStatus(shipment.getOrder().getId(), Order.OrderStatus.SHIPPED.name());
                }
            }
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Trạng thái vận đơn không hợp lệ: " + dto.getStatus());
        }
    }

    public PagedResponse<Shipment> listShipments(ShipmentQueryDTO query) {
        int page = Math.max(1, query.getPage());
        int limit = Math.max(1, Math.min(100, query.getLimit()));
        int offset = (page - 1) * limit;

        List<Shipment> shipments = shipmentDAO.findAll(offset, limit, query.getStatus());
        long total = shipmentDAO.countAll(query.getStatus());

        return new PagedResponse<>(shipments, total, page, limit);
    }
}


```

# identity\AddressService.java

| Property | Value |
|----------|-------|
| Path | `identity\AddressService.java` |
| Size | 6,495 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:40.149786 |

```java
package com.fashionshop.business.service.identity;

import com.fashionshop.business.entity.ShippingAddress;
import com.fashionshop.business.entity.User;
import com.fashionshop.common.exception.BadRequestException;
import com.fashionshop.common.exception.NotFoundException;
import com.fashionshop.common.exception.UnauthorizedException;
import com.fashionshop.dataaccess.dao.identity.ShippingAddressDAO;
import com.fashionshop.dataaccess.dao.identity.UserDAO;
import com.fashionshop.presentation.dto.identity.CreateAddressDTO;
import com.fashionshop.presentation.dto.identity.UpdateAddressDTO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AddressService {

    private final ShippingAddressDAO shippingAddressDAO;
    private final UserDAO userDAO;

    public AddressService() {
        this(new ShippingAddressDAO(), new UserDAO());
    }

    public AddressService(ShippingAddressDAO shippingAddressDAO, UserDAO userDAO) {
        this.shippingAddressDAO = shippingAddressDAO;
        this.userDAO = userDAO;
    }

    public List<Map<String, Object>> getAddressesByUserId(Integer userId) {
        if (userId == null) {
            throw new UnauthorizedException("Chưa xác thực người dùng");
        }

        List<ShippingAddress> addresses = shippingAddressDAO.findByUserId(userId);
        List<Map<String, Object>> result = new ArrayList<>();
        for (ShippingAddress addr : addresses) {
            result.add(toAddressMap(addr));
        }
        return result;
    }

    public Map<String, Object> getAddressById(Integer addressId, Integer userId) {
        if (userId == null) {
            throw new UnauthorizedException("Chưa xác thực người dùng");
        }
        if (addressId == null) {
            throw new NotFoundException("Địa chỉ không tồn tại");
        }

        // Anti-IDOR: findByIdAndUserId
        ShippingAddress address = shippingAddressDAO.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new NotFoundException("Địa chỉ không tồn tại"));

        return toAddressMap(address);
    }

    public Map<String, Object> createAddress(CreateAddressDTO dto, Integer userId) {
        if (userId == null) {
            throw new UnauthorizedException("Chưa xác thực người dùng");
        }

        int currentCount = shippingAddressDAO.countByUserId(userId);
        if (currentCount >= 10) {
            throw new BadRequestException("Mỗi tài khoản chỉ được lưu tối đa 10 địa chỉ nhận hàng");
        }

        User user = userDAO.findById(userId)
                .orElseThrow(() -> new NotFoundException("Người dùng không tồn tại"));

        boolean makeDefault = currentCount == 0 || Boolean.TRUE.equals(dto.getIsDefault());

        ShippingAddress address = new ShippingAddress();
        address.setUser(user);
        address.setReceiverName(dto.getReceiverName());
        address.setPhoneNumber(dto.getPhoneNumber());
        address.setProvince(dto.getProvince());
        address.setDistrict(dto.getDistrict());
        address.setWard(dto.getWard());
        address.setDetailAddress(dto.getDetailAddress());
        address.setIsDefault(makeDefault);

        ShippingAddress saved = shippingAddressDAO.createWithDefault(address);
        return toAddressMap(saved);
    }

    public Map<String, Object> updateAddress(Integer addressId, UpdateAddressDTO dto, Integer userId) {
        if (userId == null) {
            throw new UnauthorizedException("Chưa xác thực người dùng");
        }
        if (addressId == null) {
            throw new NotFoundException("Địa chỉ không tồn tại");
        }

        // Anti-IDOR check
        ShippingAddress address = shippingAddressDAO.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new NotFoundException("Địa chỉ không tồn tại"));

        if (dto.getReceiverName() != null) {
            address.setReceiverName(dto.getReceiverName());
        }
        if (dto.getPhoneNumber() != null) {
            address.setPhoneNumber(dto.getPhoneNumber());
        }
        if (dto.getProvince() != null) {
            address.setProvince(dto.getProvince());
        }
        if (dto.getDistrict() != null) {
            address.setDistrict(dto.getDistrict());
        }
        if (dto.getWard() != null) {
            address.setWard(dto.getWard());
        }
        if (dto.getDetailAddress() != null) {
            address.setDetailAddress(dto.getDetailAddress());
        }
        if (dto.getIsDefault() != null) {
            address.setIsDefault(dto.getIsDefault());
        }
        address.setUpdatedAt(LocalDateTime.now());

        ShippingAddress updated = shippingAddressDAO.updateWithDefault(address);
        return toAddressMap(updated);
    }

    public Map<String, Object> deleteAddress(Integer addressId, Integer userId) {
        if (userId == null) {
            throw new UnauthorizedException("Chưa xác thực người dùng");
        }
        if (addressId == null) {
            throw new NotFoundException("Địa chỉ không tồn tại");
        }

        boolean deleted = shippingAddressDAO.deleteByIdAndUserId(addressId, userId);
        if (!deleted) {
            throw new NotFoundException("Địa chỉ không tồn tại");
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Xóa địa chỉ thành công");
        return response;
    }

    private Map<String, Object> toAddressMap(ShippingAddress addr) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", addr.getId());
        map.put("userId", addr.getUser() != null ? addr.getUser().getId() : null);
        map.put("receiverName", addr.getReceiverName());
        map.put("phoneNumber", addr.getPhoneNumber());
        map.put("province", addr.getProvince());
        map.put("district", addr.getDistrict());
        map.put("ward", addr.getWard());
        map.put("detailAddress", addr.getDetailAddress());
        map.put("isDefault", addr.getIsDefault());
        map.put("createdAt", addr.getCreatedAt());
        map.put("updatedAt", addr.getUpdatedAt());
        return map;
    }
}

```

# identity\AuthService.java

| Property | Value |
|----------|-------|
| Path | `identity\AuthService.java` |
| Size | 17,920 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 16:00:22.614000 |

```java
package com.fashionshop.business.service.identity;

import com.fashionshop.business.entity.DeviceSession;
import com.fashionshop.business.entity.Role;
import com.fashionshop.business.entity.User;
import com.fashionshop.common.exception.*;
import com.fashionshop.common.util.JwtUtil;
import com.fashionshop.common.util.MailUtil;
import com.fashionshop.common.util.PasswordUtil;
import com.fashionshop.common.util.RedisUtil;
import com.fashionshop.config.AppConfig;
import com.fashionshop.dataaccess.dao.identity.DeviceSessionDAO;
import com.fashionshop.dataaccess.dao.identity.RoleDAO;
import com.fashionshop.dataaccess.dao.identity.UserDAO;
import com.fashionshop.presentation.dto.identity.*;
import io.jsonwebtoken.Claims;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class AuthService {

    private final UserDAO userDAO;
    private final RoleDAO roleDAO;
    private final DeviceSessionDAO deviceSessionDAO;
    private final RedissonClient redisson;
    private final SecureRandom random = new SecureRandom();

    public AuthService() {
        this(new UserDAO(), new RoleDAO(), new DeviceSessionDAO(), RedisUtil.getClient());
    }

    public AuthService(UserDAO userDAO, RoleDAO roleDAO, DeviceSessionDAO deviceSessionDAO, RedissonClient redisson) {
        this.userDAO = userDAO;
        this.roleDAO = roleDAO;
        this.deviceSessionDAO = deviceSessionDAO;
        this.redisson = redisson;
    }

    public Map<String, Object> register(RegisterDTO dto) {
        String email = dto.getEmail();

        Optional<User> existingUserOpt = userDAO.findByEmail(email);
        if (existingUserOpt.isPresent()) {
            User existing = existingUserOpt.get();
            if (existing.getStatus() != User.UserStatus.INACTIVE) {
                throw new ConflictException("Email đã được sử dụng");
            }
        }

        // Check Redis cooldown: otp_cooldown:REGISTER:{email}
        checkOtpCooldown("REGISTER", email);

        // Find CUSTOMER role
        Role customerRole = roleDAO.findByCode("CUSTOMER")
                .orElseThrow(() -> new AppException("Hệ thống chưa cấu hình vai trò CUSTOMER", 500));

        String passwordHash = PasswordUtil.hashPassword(dto.getPassword());
        User user;
        if (existingUserOpt.isPresent()) {
            user = existingUserOpt.get();
            user.setFullName(dto.getFullName());
            user.setPhoneNumber(dto.getPhoneNumber());
            user.setPasswordHash(passwordHash);
            user.setRole(customerRole);
            user.setStatus(User.UserStatus.INACTIVE);
            user = userDAO.update(user);
        } else {
            user = new User();
            user.setEmail(email);
            user.setFullName(dto.getFullName());
            user.setPhoneNumber(dto.getPhoneNumber());
            user.setPasswordHash(passwordHash);
            user.setRole(customerRole);
            user.setStatus(User.UserStatus.INACTIVE);
            user.setRewardPoints(0);
            user = userDAO.save(user);
        }

        // Generate and store OTP
        String otpCode = generateAndStoreOtp("REGISTER", email);
        MailUtil.sendVerificationEmail(email, otpCode);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("user", toUserResponseMap(user));
        response.put("message", "Đăng ký thành công. Vui lòng kiểm tra email để lấy mã xác thực.");
        return response;
    }

    public Map<String, Object> login(LoginDTO dto, String clientIp) {
        String email = dto.getEmail();

        User user = userDAO.findByEmailWithRole(email)
                .orElseThrow(() -> new UnauthorizedException("Email hoặc mật khẩu không chính xác"));

        if (user.getStatus() == User.UserStatus.INACTIVE) {
            throw new ForbiddenException("Tài khoản chưa được kích hoạt. Vui lòng xác thực mã OTP.");
        }
        if (user.getStatus() == User.UserStatus.BLOCKED) {
            throw new ForbiddenException("Tài khoản của bạn đã bị khóa. Vui lòng liên hệ CSKH.");
        }

        if (!PasswordUtil.checkPassword(dto.getPassword(), user.getPasswordHash())) {
            throw new UnauthorizedException("Email hoặc mật khẩu không chính xác");
        }

        String roleCode = user.getRole() != null ? user.getRole().getCode() : "CUSTOMER";
        Integer roleId = user.getRole() != null ? user.getRole().getId() : null;

        String accessToken = JwtUtil.generateAccessToken(user.getId(), roleId, roleCode, user.getEmail());
        String refreshToken = JwtUtil.generateRefreshToken(user.getId(), user.getEmail(), roleCode);

        // Create DeviceSession
        DeviceSession session = new DeviceSession();
        session.setUser(user);
        session.setRefreshToken(refreshToken);
        session.setIpAddress(clientIp != null && !clientIp.isBlank() ? clientIp : "127.0.0.1");
        session.setIsActive(true);
        session.setExpiresAt(LocalDateTime.now().plus(Duration.ofMillis(AppConfig.REFRESH_TOKEN_EXPIRATION)));
        deviceSessionDAO.save(session);

        // Cache session in Redis
        setRedisSession(user.getId(), refreshToken);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("accessToken", accessToken);
        response.put("refreshToken", refreshToken);
        response.put("user", toUserResponseMap(user));
        return response;
    }

    public Map<String, Object> verifyCode(VerifyOtpDTO dto) {
        String email = dto.getEmail();
        String type = dto.getType();

        String redisKey = "otp:" + type + ":" + email;
        String cachedOtp = getRedisValue(redisKey);

        if (cachedOtp == null) {
            throw new AppException("Mã xác thực đã hết hạn hoặc không tồn tại. Vui lòng gửi lại mã mới.", 410);
        }

        if (!cachedOtp.equals(dto.getCode())) {
            throw new BadRequestException("Mã xác thực không chính xác");
        }

        // Delete OTP immediately to prevent replay attacks
        deleteRedisKey(redisKey);

        Map<String, Object> response = new LinkedHashMap<>();

        if ("REGISTER".equalsIgnoreCase(type)) {
            User user = userDAO.findByEmail(email)
                    .orElseThrow(() -> new NotFoundException("Tài khoản không tồn tại"));
            user.setStatus(User.UserStatus.ACTIVE);
            userDAO.update(user);
            response.put("message", "Xác thực tài khoản thành công!");
        } else if ("FORGOT_PASSWORD".equalsIgnoreCase(type)) {
            userDAO.findByEmail(email)
                    .orElseThrow(() -> new NotFoundException("Tài khoản không tồn tại"));
            String resetToken = JwtUtil.generateResetToken(email);
            response.put("resetToken", resetToken);
            response.put("message", "Xác thực mã OTP thành công. Vui lòng đặt lại mật khẩu mới.");
        }

        return response;
    }

    public Map<String, Object> forgotPassword(ForgotPasswordDTO dto) {
        String email = dto.getEmail();

        User user = userDAO.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy tài khoản với địa chỉ email này"));

        if (user.getStatus() == User.UserStatus.BLOCKED) {
            throw new ForbiddenException("Tài khoản này đã bị khóa");
        }

        checkOtpCooldown("FORGOT_PASSWORD", email);

        String otpCode = generateAndStoreOtp("FORGOT_PASSWORD", email);
        MailUtil.sendVerificationEmail(email, otpCode);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Mã xác thực đã được gửi về email của bạn.");
        return response;
    }

    public Map<String, Object> resetPassword(ResetPasswordDTO dto) {
        Claims claims;
        try {
            claims = JwtUtil.verifyResetToken(dto.getToken());
        } catch (Exception e) {
            throw new UnauthorizedException("Mã token đặt lại mật khẩu đã hết hạn hoặc không hợp lệ");
        }

        String purpose = claims.get("purpose", String.class);
        if (!"RESET_PASSWORD".equals(purpose)) {
            throw new BadRequestException("Token không đúng mục đích");
        }

        String email = claims.get("email", String.class);
        User user = userDAO.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy tài khoản với địa chỉ email này"));

        user.setPasswordHash(PasswordUtil.hashPassword(dto.getNewPassword()));
        userDAO.update(user);

        // Deactivate all DeviceSessions
        deviceSessionDAO.deactivateAllByUserId(user.getId());

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Đặt lại mật khẩu thành công. Vui lòng đăng nhập lại.");
        return response;
    }

    public Map<String, Object> changePassword(ChangePasswordDTO dto, Integer currentUserId) {
        if (currentUserId == null) {
            throw new UnauthorizedException("Chưa xác thực người dùng");
        }

        User user = userDAO.findById(currentUserId)
                .orElseThrow(() -> new NotFoundException("Người dùng không tồn tại"));

        if (!PasswordUtil.checkPassword(dto.getOldPassword(), user.getPasswordHash())) {
            throw new BadRequestException("Mật khẩu hiện tại không chính xác");
        }

        user.setPasswordHash(PasswordUtil.hashPassword(dto.getNewPassword()));
        userDAO.update(user);

        // Deactivate all active device sessions
        deviceSessionDAO.deactivateAllByUserId(currentUserId);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Đổi mật khẩu thành công. Vui lòng đăng nhập lại.");
        return response;
    }

    public Map<String, Object> refreshToken(RefreshTokenDTO dto, String clientIp) {
        String token = dto.getRefreshToken();
        if (token == null || token.isBlank()) {
            throw new UnauthorizedException("Vui lòng cung cấp Refresh Token");
        }

        try {
            JwtUtil.verifyRefreshToken(token);
        } catch (Exception e) {
            throw new UnauthorizedException("Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.");
        }

        DeviceSession session = deviceSessionDAO.findByRefreshToken(token)
                .orElseThrow(() -> new UnauthorizedException("Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại."));

        if (!Boolean.TRUE.equals(session.getIsActive()) || session.isTokenExpired()) {
            throw new UnauthorizedException("Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.");
        }

        User user = session.getUser();
        if (user == null || user.getStatus() != User.UserStatus.ACTIVE) {
            throw new ForbiddenException("Tài khoản không hợp lệ hoặc đã bị khóa");
        }

        String roleCode = user.getRole() != null ? user.getRole().getCode() : "CUSTOMER";
        Integer roleId = user.getRole() != null ? user.getRole().getId() : null;

        String newAccessToken = JwtUtil.generateAccessToken(user.getId(), roleId, roleCode, user.getEmail());
        String newRefreshToken = JwtUtil.generateRefreshToken(user.getId(), user.getEmail(), roleCode);

        // Rotate session in database
        session.revokeSession();
        deviceSessionDAO.update(session);

        DeviceSession newSession = new DeviceSession();
        newSession.setUser(user);
        newSession.setRefreshToken(newRefreshToken);
        newSession.setIpAddress(clientIp != null && !clientIp.isBlank() ? clientIp : session.getIpAddress());
        newSession.setIsActive(true);
        newSession.setExpiresAt(LocalDateTime.now().plus(Duration.ofMillis(AppConfig.REFRESH_TOKEN_EXPIRATION)));
        deviceSessionDAO.save(newSession);

        // Rotate Redis session
        deleteRedisKey("session:" + user.getId() + ":" + token);
        setRedisSession(user.getId(), newRefreshToken);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("accessToken", newAccessToken);
        response.put("refreshToken", newRefreshToken);
        return response;
    }

    public Map<String, Object> logout(String accessToken, String refreshToken) {
        if (refreshToken != null && !refreshToken.isBlank()) {
            deviceSessionDAO.deactivateByRefreshToken(refreshToken);
        }

        if (accessToken != null && !accessToken.isBlank()) {
            long remainingMillis = JwtUtil.getRemainingMillis(accessToken);
            if (remainingMillis > 0) {
                setRedisBlacklist(accessToken, remainingMillis);
            }
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Đăng xuất thành công");
        return response;
    }

    public static Map<String, Object> toUserResponseMap(User user) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", user.getId());
        map.put("email", user.getEmail());
        map.put("fullName", user.getFullName());
        map.put("phoneNumber", user.getPhoneNumber());
        map.put("avatarUrl", user.getAvatarUrl());
        map.put("rewardPoints", user.getRewardPoints());
        map.put("status", user.getStatus() != null ? user.getStatus().name() : null);

        if (user.getRole() != null) {
            map.put("roleId", user.getRole().getId());
            Map<String, Object> roleMap = new LinkedHashMap<>();
            roleMap.put("id", user.getRole().getId());
            roleMap.put("code", user.getRole().getCode());
            roleMap.put("name", user.getRole().getName());
            map.put("role", roleMap);
        }

        map.put("createdAt", user.getCreatedAt());
        map.put("updatedAt", user.getUpdatedAt());
        return map;
    }

    private void checkOtpCooldown(String type, String email) {
        try {
            if (redisson != null) {
                RBucket<String> cooldownBucket = redisson.getBucket("otp_cooldown:" + type + ":" + email);
                if (cooldownBucket.isExists()) {
                    long ttlMillis = cooldownBucket.remainTimeToLive();
                    long seconds = ttlMillis > 0 ? (ttlMillis / 1000) : 60;
                    throw new AppException(String.format("Vui lòng chờ %d giây trước khi yêu cầu gửi lại mã", Math.max(seconds, 1)), 429);
                }
            }
        } catch (AppException ae) {
            throw ae;
        } catch (Exception e) {
            System.err.println("Warning: Redis cooldown check failed: " + e.getMessage());
        }
    }

    private String generateAndStoreOtp(String type, String email) {
        int code = 100000 + random.nextInt(900000);
        String codeStr = String.valueOf(code);

        try {
            if (redisson != null) {
                RBucket<String> otpBucket = redisson.getBucket("otp:" + type + ":" + email);
                otpBucket.set(codeStr, Duration.ofSeconds(600)); // 10 minutes

                RBucket<String> cooldownBucket = redisson.getBucket("otp_cooldown:" + type + ":" + email);
                cooldownBucket.set("1", Duration.ofSeconds(60)); // 60 seconds cooldown
            }
        } catch (Exception e) {
            System.err.println("Warning: Redis store OTP failed: " + e.getMessage());
        }

        return codeStr;
    }

    private String getRedisValue(String key) {
        try {
            if (redisson != null) {
                RBucket<String> bucket = redisson.getBucket(key);
                return bucket.get();
            }
        } catch (Exception e) {
            System.err.println("Warning: Redis get failed: " + e.getMessage());
        }
        return null;
    }

    private void deleteRedisKey(String key) {
        try {
            if (redisson != null) {
                redisson.getBucket(key).delete();
            }
        } catch (Exception e) {
            System.err.println("Warning: Redis delete failed: " + e.getMessage());
        }
    }

    private void setRedisSession(Integer userId, String refreshToken) {
        try {
            if (redisson != null) {
                RBucket<String> sessionBucket = redisson.getBucket("session:" + userId + ":" + refreshToken);
                sessionBucket.set("active", Duration.ofMillis(AppConfig.REFRESH_TOKEN_EXPIRATION));
            }
        } catch (Exception e) {
            System.err.println("Warning: Redis set session failed: " + e.getMessage());
        }
    }

    private void setRedisBlacklist(String accessToken, long remainingMillis) {
        try {
            if (redisson != null) {
                RBucket<String> blacklistBucket = redisson.getBucket("blacklist:token:" + accessToken);
                blacklistBucket.set("revoked", Duration.ofMillis(remainingMillis));
            }
        } catch (Exception e) {
            System.err.println("Warning: Redis set blacklist failed: " + e.getMessage());
        }
    }
}

```

# identity\RolePermissionService.java

| Property | Value |
|----------|-------|
| Path | `identity\RolePermissionService.java` |
| Size | 9,827 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:40.196788 |

```java
package com.fashionshop.business.service.identity;

import com.fashionshop.business.entity.Permission;
import com.fashionshop.business.entity.Role;
import com.fashionshop.common.exception.BadRequestException;
import com.fashionshop.common.exception.ConflictException;
import com.fashionshop.common.exception.ForbiddenException;
import com.fashionshop.common.exception.NotFoundException;
import com.fashionshop.dataaccess.dao.identity.PermissionDAO;
import com.fashionshop.dataaccess.dao.identity.RoleDAO;
import com.fashionshop.dataaccess.dao.identity.UserDAO;
import com.fashionshop.presentation.dto.identity.CreatePermissionDTO;
import com.fashionshop.presentation.dto.identity.CreateRoleDTO;
import com.fashionshop.presentation.dto.identity.UpdatePermissionDTO;
import com.fashionshop.presentation.dto.identity.UpdateRoleDTO;

import java.util.*;

public class RolePermissionService {

    private final RoleDAO roleDAO;
    private final PermissionDAO permissionDAO;
    private final UserDAO userDAO;

    public RolePermissionService() {
        this(new RoleDAO(), new PermissionDAO(), new UserDAO());
    }

    public RolePermissionService(RoleDAO roleDAO, PermissionDAO permissionDAO, UserDAO userDAO) {
        this.roleDAO = roleDAO;
        this.permissionDAO = permissionDAO;
        this.userDAO = userDAO;
    }

    // ==========================================
    // Role Operations
    // ==========================================

    public List<Map<String, Object>> getAllRoles(String currentRoleCode) {
        ensureAdmin(currentRoleCode);
        List<Role> roles = roleDAO.findAllWithPermissions();
        List<Map<String, Object>> result = new ArrayList<>();
        for (Role r : roles) {
            result.add(toRoleMap(r));
        }
        return result;
    }

    public Map<String, Object> getRoleById(Integer roleId, String currentRoleCode) {
        ensureAdmin(currentRoleCode);
        if (roleId == null) {
            throw new NotFoundException("Vai trò không tồn tại");
        }
        Role role = roleDAO.findByIdWithPermissions(roleId)
                .orElseThrow(() -> new NotFoundException("Vai trò không tồn tại"));
        return toRoleMap(role);
    }

    public Map<String, Object> createRole(CreateRoleDTO dto, Integer createdById, String currentRoleCode) {
        ensureAdmin(currentRoleCode);

        String code = dto.getCode();
        if (roleDAO.existsByCode(code)) {
            throw new ConflictException("Mã vai trò đã tồn tại");
        }

        Role role = new Role();
        role.setName(dto.getName());
        role.setCode(code);
        role.setIsActive(true);
        role.setCreatedById(createdById);

        if (dto.getPermissionIds() != null && !dto.getPermissionIds().isEmpty()) {
            List<Permission> perms = permissionDAO.findByIds(dto.getPermissionIds());
            role.setPermissions(new HashSet<>(perms));
        }

        Role saved = roleDAO.save(role);
        return toRoleMap(saved);
    }

    public Map<String, Object> updateRole(Integer roleId, UpdateRoleDTO dto, Integer updatedById, String currentRoleCode) {
        ensureAdmin(currentRoleCode);
        if (roleId == null) {
            throw new NotFoundException("Vai trò không tồn tại");
        }

        Role role = roleDAO.findByIdWithPermissions(roleId)
                .orElseThrow(() -> new NotFoundException("Vai trò không tồn tại"));

        if (dto.getName() != null) {
            role.setName(dto.getName());
        }
        if (dto.getIsActive() != null) {
            role.setIsActive(dto.getIsActive());
        }
        if (dto.getPermissionIds() != null) {
            List<Permission> perms = permissionDAO.findByIds(dto.getPermissionIds());
            role.setPermissions(new HashSet<>(perms));
        }
        role.setUpdatedById(updatedById);

        Role updated = roleDAO.update(role);
        return toRoleMap(updated);
    }

    public Map<String, Object> softDeleteRole(Integer roleId, Integer deletedById, String currentRoleCode) {
        ensureAdmin(currentRoleCode);
        if (roleId == null) {
            throw new NotFoundException("Vai trò không tồn tại");
        }

        Role role = roleDAO.findById(roleId)
                .orElseThrow(() -> new NotFoundException("Vai trò không tồn tại"));

        String code = role.getCode();
        if ("ADMIN".equalsIgnoreCase(code) || "CUSTOMER".equalsIgnoreCase(code)) {
            throw new BadRequestException("Không được xóa role hệ thống");
        }

        long userCount = userDAO.countByRoleId(roleId);
        if (userCount > 0) {
            throw new BadRequestException("Không thể xóa vai trò đang có người dùng sử dụng");
        }

        roleDAO.softDelete(roleId, deletedById);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Xóa vai trò thành công");
        return response;
    }

    // ==========================================
    // Permission Operations
    // ==========================================

    public List<Map<String, Object>> getAllPermissions(String module, String currentRoleCode) {
        ensureAdmin(currentRoleCode);
        List<Permission> permissions = permissionDAO.findByModule(module);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Permission p : permissions) {
            result.add(toPermissionMap(p));
        }
        return result;
    }

    public Map<String, Object> getPermissionById(Integer permissionId, String currentRoleCode) {
        ensureAdmin(currentRoleCode);
        if (permissionId == null) {
            throw new NotFoundException("Permission không tồn tại");
        }

        Permission permission = permissionDAO.findById(permissionId)
                .orElseThrow(() -> new NotFoundException("Permission không tồn tại"));
        return toPermissionMap(permission);
    }

    public Map<String, Object> createPermission(CreatePermissionDTO dto, Integer createdById, String currentRoleCode) {
        ensureAdmin(currentRoleCode);

        String code = dto.getCode();
        if (permissionDAO.existsByCode(code)) {
            throw new ConflictException("Mã permission đã tồn tại");
        }

        Permission permission = new Permission();
        permission.setCode(code);
        permission.setModule(dto.getModule());
        permission.setDescription(dto.getDescription());
        permission.setCreatedById(createdById);

        Permission saved = permissionDAO.save(permission);
        return toPermissionMap(saved);
    }

    public Map<String, Object> updatePermission(Integer permissionId, UpdatePermissionDTO dto, Integer updatedById, String currentRoleCode) {
        ensureAdmin(currentRoleCode);
        if (permissionId == null) {
            throw new NotFoundException("Permission không tồn tại");
        }

        Permission permission = permissionDAO.findById(permissionId)
                .orElseThrow(() -> new NotFoundException("Permission không tồn tại"));

        if (dto.getModule() != null) {
            permission.setModule(dto.getModule());
        }
        if (dto.getDescription() != null) {
            permission.setDescription(dto.getDescription());
        }
        permission.setUpdatedById(updatedById);

        Permission updated = permissionDAO.update(permission);
        return toPermissionMap(updated);
    }

    public Map<String, Object> softDeletePermission(Integer permissionId, Integer deletedById, String currentRoleCode) {
        ensureAdmin(currentRoleCode);
        if (permissionId == null) {
            throw new NotFoundException("Permission không tồn tại");
        }

        permissionDAO.findById(permissionId)
                .orElseThrow(() -> new NotFoundException("Permission không tồn tại"));

        permissionDAO.softDelete(permissionId, deletedById);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Xóa quyền hạn thành công");
        return response;
    }

    // ==========================================
    // Helpers
    // ==========================================

    private void ensureAdmin(String roleCode) {
        if (!"ADMIN".equalsIgnoreCase(roleCode)) {
            throw new ForbiddenException("Bạn không có quyền thực hiện hành động này");
        }
    }

    private Map<String, Object> toRoleMap(Role role) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", role.getId());
        map.put("code", role.getCode());
        map.put("name", role.getName());
        map.put("isActive", role.getIsActive());

        List<Map<String, Object>> permList = new ArrayList<>();
        if (role.getPermissions() != null) {
            for (Permission p : role.getPermissions()) {
                permList.add(toPermissionMap(p));
            }
        }
        map.put("permissions", permList);
        map.put("createdAt", role.getCreatedAt());
        map.put("updatedAt", role.getUpdatedAt());
        return map;
    }

    private Map<String, Object> toPermissionMap(Permission p) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", p.getId());
        map.put("code", p.getCode());
        map.put("module", p.getModule());
        map.put("description", p.getDescription());
        map.put("createdAt", p.getCreatedAt());
        map.put("updatedAt", p.getUpdatedAt());
        return map;
    }
}

```

# identity\UserService.java

| Property | Value |
|----------|-------|
| Path | `identity\UserService.java` |
| Size | 7,156 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:40.278786 |

```java
package com.fashionshop.business.service.identity;

import com.fashionshop.business.entity.ShippingAddress;
import com.fashionshop.business.entity.User;
import com.fashionshop.common.exception.BadRequestException;
import com.fashionshop.common.exception.ForbiddenException;
import com.fashionshop.common.exception.NotFoundException;
import com.fashionshop.common.response.PagedResponse;
import com.fashionshop.dataaccess.dao.identity.DeviceSessionDAO;
import com.fashionshop.dataaccess.dao.identity.ShippingAddressDAO;
import com.fashionshop.dataaccess.dao.identity.UserDAO;
import com.fashionshop.presentation.dto.identity.UpdateUserDTO;
import com.fashionshop.presentation.dto.identity.UpdateUserStatusDTO;
import com.fashionshop.presentation.dto.identity.UserQueryDTO;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class UserService {

    private final UserDAO userDAO;
    private final ShippingAddressDAO shippingAddressDAO;
    private final DeviceSessionDAO deviceSessionDAO;

    public UserService() {
        this(new UserDAO(), new ShippingAddressDAO(), new DeviceSessionDAO());
    }

    public UserService(UserDAO userDAO, ShippingAddressDAO shippingAddressDAO, DeviceSessionDAO deviceSessionDAO) {
        this.userDAO = userDAO;
        this.shippingAddressDAO = shippingAddressDAO;
        this.deviceSessionDAO = deviceSessionDAO;
    }

    public Map<String, Object> getUserById(Integer userId, Integer currentUserId, String currentRoleCode) {
        if (userId == null) {
            throw new NotFoundException("Người dùng không tồn tại");
        }

        // Anti-IDOR: Only ADMIN or the account owner can view profile
        boolean isAdmin = "ADMIN".equalsIgnoreCase(currentRoleCode);
        if (!isAdmin && !userId.equals(currentUserId)) {
            throw new ForbiddenException("Bạn không có quyền xem thông tin người dùng này");
        }

        User user = userDAO.findByIdWithRole(userId)
                .orElseThrow(() -> new NotFoundException("Người dùng không tồn tại"));

        Map<String, Object> userMap = AuthService.toUserResponseMap(user);

        // Include addresses in profile
        List<ShippingAddress> addresses = shippingAddressDAO.findByUserId(userId);
        List<Map<String, Object>> addressMaps = new ArrayList<>();
        for (ShippingAddress addr : addresses) {
            addressMaps.add(toAddressMap(addr));
        }
        userMap.put("addresses", addressMaps);

        return userMap;
    }

    public PagedResponse<Map<String, Object>> getUsers(UserQueryDTO query, String currentRoleCode) {
        boolean isAdmin = "ADMIN".equalsIgnoreCase(currentRoleCode);
        if (!isAdmin) {
            throw new ForbiddenException("Bạn không có quyền thực hiện hành động này");
        }

        List<User> users = userDAO.findFiltered(
                query.getSearch(),
                query.getStatus(),
                query.getRoleId(),
                query.getPage(),
                query.getLimit()
        );

        long total = userDAO.countFiltered(
                query.getSearch(),
                query.getStatus(),
                query.getRoleId()
        );

        List<Map<String, Object>> userMaps = new ArrayList<>();
        for (User user : users) {
            userMaps.add(AuthService.toUserResponseMap(user));
        }

        return new PagedResponse<>(userMaps, total, query.getPage(), query.getLimit());
    }

    public Map<String, Object> updateUser(Integer userId, UpdateUserDTO dto, Integer currentUserId, String currentRoleCode) {
        if (userId == null) {
            throw new NotFoundException("Người dùng không tồn tại");
        }

        boolean isAdmin = "ADMIN".equalsIgnoreCase(currentRoleCode);
        if (!isAdmin && !userId.equals(currentUserId)) {
            throw new ForbiddenException("Bạn không có quyền cập nhật thông tin người dùng này");
        }

        User user = userDAO.findById(userId)
                .orElseThrow(() -> new NotFoundException("Người dùng không tồn tại"));

        user.updateProfile(dto.getFullName(), dto.getPhoneNumber(), dto.getAvatarUrl());
        user.setUpdatedById(currentUserId);
        User updated = userDAO.update(user);

        return AuthService.toUserResponseMap(updated);
    }

    public Map<String, Object> updateUserStatus(Integer userId, UpdateUserStatusDTO dto, Integer currentUserId, String currentRoleCode) {
        boolean isAdmin = "ADMIN".equalsIgnoreCase(currentRoleCode);
        if (!isAdmin) {
            throw new ForbiddenException("Bạn không có quyền thực hiện hành động này");
        }

        User user = userDAO.findById(userId)
                .orElseThrow(() -> new NotFoundException("Người dùng không tồn tại"));

        User.UserStatus newStatus = dto.toUserStatus();
        user.updateStatus(newStatus);
        user.setUpdatedById(currentUserId);
        User updated = userDAO.update(user);

        // If status changed to BLOCKED, revoke all active sessions immediately
        if (newStatus == User.UserStatus.BLOCKED) {
            deviceSessionDAO.deactivateAllByUserId(userId);
        }

        return AuthService.toUserResponseMap(updated);
    }

    public Map<String, Object> softDeleteUser(Integer userId, Integer currentUserId, String currentRoleCode) {
        boolean isAdmin = "ADMIN".equalsIgnoreCase(currentRoleCode);
        if (!isAdmin) {
            throw new ForbiddenException("Bạn không có quyền thực hiện hành động này");
        }

        if (userId != null && userId.equals(currentUserId)) {
            throw new BadRequestException("Bạn không thể tự xóa tài khoản của chính mình");
        }

        userDAO.findById(userId)
                .orElseThrow(() -> new NotFoundException("Người dùng không tồn tại"));

        userDAO.softDelete(userId, currentUserId);
        deviceSessionDAO.deactivateAllByUserId(userId);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Xóa người dùng thành công");
        return response;
    }

    private Map<String, Object> toAddressMap(ShippingAddress addr) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", addr.getId());
        map.put("userId", addr.getUser() != null ? addr.getUser().getId() : null);
        map.put("receiverName", addr.getReceiverName());
        map.put("phoneNumber", addr.getPhoneNumber());
        map.put("province", addr.getProvince());
        map.put("district", addr.getDistrict());
        map.put("ward", addr.getWard());
        map.put("detailAddress", addr.getDetailAddress());
        map.put("isDefault", addr.getIsDefault());
        map.put("createdAt", addr.getCreatedAt());
        map.put("updatedAt", addr.getUpdatedAt());
        return map;
    }
}

```

# inventory\FlashSaleService.java

| Property | Value |
|----------|-------|
| Path | `inventory\FlashSaleService.java` |
| Size | 5,413 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:28.370150 |

```java
package com.fashionshop.business.service.inventory;

import com.fashionshop.business.entity.FlashSaleCampaign;
import com.fashionshop.business.entity.FlashSaleItem;
import com.fashionshop.business.entity.ProductVariant;
import com.fashionshop.common.exception.AppException;
import com.fashionshop.common.exception.NotFoundException;
import com.fashionshop.common.response.PagedResponse;
import com.fashionshop.dataaccess.dao.inventory.FlashSaleCampaignDAO;
import com.fashionshop.presentation.dto.inventory.CreateFlashSaleDTO;
import com.fashionshop.presentation.dto.inventory.FlashSaleItemDTO;
import com.fashionshop.presentation.dto.inventory.FlashSaleQueryDTO;
import com.fashionshop.presentation.dto.inventory.UpdateFlashSaleDTO;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class FlashSaleService {
    private final FlashSaleCampaignDAO flashSaleDAO = new FlashSaleCampaignDAO();

    public PagedResponse<FlashSaleCampaign> list(FlashSaleQueryDTO query) {
        List<FlashSaleCampaign> data = flashSaleDAO.list(query);
        long total = flashSaleDAO.count(query);
        return new PagedResponse<>(data, total, query.getPage(), query.getLimit());
    }

    public List<FlashSaleCampaign> findActiveCampaigns() {
        return flashSaleDAO.findActiveCampaigns();
    }

    public FlashSaleCampaign findById(int campaignId) {
        return flashSaleDAO.findDetailById(campaignId)
                .orElseThrow(() -> new AppException("Chiến dịch Flash Sale không tồn tại hoặc đã bị xóa", 404));
    }

    private void validateUniqueVariantIds(List<FlashSaleItemDTO> items) {
        Set<Integer> seen = new HashSet<>();
        for (FlashSaleItemDTO item : items) {
            if (!seen.add(item.getVariantId())) {
                throw new AppException("Không được có sản phẩm trùng trong Flash Sale", 422);
            }
        }
    }

    private void validateVariants(List<FlashSaleItemDTO> items) {
        validateUniqueVariantIds(items);

        List<Integer> variantIds = items.stream().map(FlashSaleItemDTO::getVariantId).toList();
        List<ProductVariant> variants = flashSaleDAO.findVariantsForFlashSale(variantIds);

        Map<Integer, ProductVariant> variantMap = variants.stream()
                .collect(Collectors.toMap(ProductVariant::getId, v -> v));

        for (FlashSaleItemDTO item : items) {
            ProductVariant variant = variantMap.get(item.getVariantId());

            if (variant == null) {
                throw new NotFoundException("Biến thể SKU ID " + item.getVariantId() + " không tồn tại");
            }

            if (variant.getStockQuantity() < item.getPromoQuantity()) {
                throw new AppException("Tồn kho SKU " + variant.getSku() + " không đủ. Tồn kho hiện có: "
                        + variant.getStockQuantity() + ", cấu hình sale: " + item.getPromoQuantity(), 422);
            }

            if (item.getFlashSalePrice().compareTo(variant.getSellingPrice()) >= 0) {
                throw new AppException("Giá Flash Sale của SKU " + variant.getSku() + " phải thấp hơn giá bán hiện tại", 422);
            }
        }
    }

    public FlashSaleCampaign create(CreateFlashSaleDTO body) {
        if (!body.getStartTime().isBefore(body.getEndTime())) {
            throw new AppException("Thời gian kết thúc phải sau thời gian bắt đầu", 422);
        }

        validateVariants(body.getItems());
        return flashSaleDAO.create(body);
    }

    public FlashSaleCampaign update(int campaignId, UpdateFlashSaleDTO body) {
        FlashSaleCampaign campaign = findById(campaignId);

        LocalDateTime startTime = body.getStartTime() != null ? body.getStartTime() : campaign.getStartTime();
        LocalDateTime endTime = body.getEndTime() != null ? body.getEndTime() : campaign.getEndTime();

        if (!startTime.isBefore(endTime)) {
            throw new AppException("Thời gian kết thúc phải sau thời gian bắt đầu", 422);
        }

        if (body.getItems() != null) {
            validateVariants(body.getItems());

            Map<Integer, FlashSaleItem> existingItemMap = campaign.getItems().stream()
                    .collect(Collectors.toMap(
                            item -> item.getVariant().getId(),
                            item -> item,
                            (a, b) -> a
                    ));

            for (FlashSaleItemDTO item : body.getItems()) {
                FlashSaleItem existing = existingItemMap.get(item.getVariantId());
                if (existing != null && item.getPromoQuantity() < existing.getSoldQuantity()) {
                    throw new AppException("Không thể giảm số lượng khuyến mãi của variant "
                            + item.getVariantId() + " xuống " + item.getPromoQuantity()
                            + " vì đã bán " + existing.getSoldQuantity(), 422);
                }
            }
        }

        return flashSaleDAO.update(campaignId, body);
    }

    public void delete(int campaignId) {
        FlashSaleCampaign campaign = findById(campaignId);
        campaign.setDeletedAt(LocalDateTime.now());
        flashSaleDAO.update(campaign);
    }
}
```

# loyalty\ChatService.java

| Property | Value |
|----------|-------|
| Path | `loyalty\ChatService.java` |
| Size | 4,090 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:28.371153 |

```java
package com.fashionshop.business.service.loyalty;

import com.fashionshop.business.entity.Chat;
import com.fashionshop.business.entity.Message;
import com.fashionshop.business.entity.enums.ChatStatus;
import com.fashionshop.common.exception.BadRequestException;
import com.fashionshop.common.exception.ForbiddenException;
import com.fashionshop.common.exception.NotFoundException;
import com.fashionshop.dataaccess.dao.loyalty.ChatDAO;
import com.fashionshop.dataaccess.dao.loyalty.MessageDAO;
import com.fashionshop.presentation.dto.loyalty.ChatHistoryQueryDTO;
import com.fashionshop.presentation.dto.loyalty.MarkReadDTO;
import com.fashionshop.presentation.dto.loyalty.SendMessageDTO;
import com.fashionshop.presentation.websocket.ChatEndpoint;

import java.util.List;
import java.util.Map;

public class ChatService {
    private final ChatDAO chatDAO = new ChatDAO();
    private final MessageDAO messageDAO = new MessageDAO();

    public Chat getOrCreateActiveChat(int userId) {
        return chatDAO.findActiveChatByUserId(userId)
                .orElseGet(() -> chatDAO.createChat(userId));
    }

    private Chat authorizeChatAccess(int userId, int chatId) {
        Chat chat = chatDAO.findDetailById(chatId)
                .orElseThrow(() -> new NotFoundException("Cuộc trò chuyện không tồn tại"));

        if (chat.getUser().getId().equals(userId)) {
            return chat;
        }

        String roleCode = chatDAO.findUserRoleCode(userId);
        if (!"ADMIN".equals(roleCode) && !"STAFF".equals(roleCode)) {
            throw new ForbiddenException("Bạn không có quyền truy cập cuộc trò chuyện này");
        }

        return chat;
    }

    private void authorizeShopStaff(int userId) {
        String roleCode = chatDAO.findUserRoleCode(userId);
        if (!"ADMIN".equals(roleCode) && !"STAFF".equals(roleCode)) {
            throw new ForbiddenException("Chỉ nhân viên cửa hàng mới được thực hiện thao tác này");
        }
    }

    public Message sendMessage(int senderId, SendMessageDTO body) {
        Chat chat = authorizeChatAccess(senderId, body.getChatId());
        if (chat.getStatus() == ChatStatus.CLOSED) {
            throw new BadRequestException("Cuộc trò chuyện đã kết thúc");
        }

        Message message = messageDAO.createMessage(body.getChatId(), senderId, body.getContent());

        // Phát realtime tin nhắn qua WebSocket
        boolean isCustomer = senderId == chat.getUser().getId();
        if (isCustomer) {
            ChatEndpoint.broadcastToShop(message);
        } else {
            ChatEndpoint.sendToUser(chat.getUser().getId(), message);
        }

        return message;
    }

    public MessageDAO.CursorResult getChatHistory(int userId, ChatHistoryQueryDTO query) {
        authorizeChatAccess(userId, query.getChatId());
        return messageDAO.findConversation(query.getChatId(), query.getCursor(), query.getLimit());
    }

    public int markAsRead(int receiverId, MarkReadDTO body) {
        authorizeChatAccess(receiverId, body.getChatId());
        return messageDAO.markMessagesAsRead(body.getChatId(), receiverId);
    }

    public int getUnreadCount(int userId) {
        return messageDAO.countUnread(userId);
    }

    public List<Chat> getConversationList(int userId) {
        return chatDAO.findConversationList(userId);
    }

    public List<Chat> getShopConversationList(int userId) {
        authorizeShopStaff(userId);
        return chatDAO.findConversationList(null);
    }

    public Chat closeConversation(int userId, int chatId) {
        authorizeShopStaff(userId);
        Chat chat = chatDAO.findDetailById(chatId)
                .orElseThrow(() -> new NotFoundException("Cuộc trò chuyện không tồn tại"));

        if (chat.getStatus() == ChatStatus.CLOSED) {
            throw new BadRequestException("Cuộc trò chuyện đã kết thúc");
        }

        return chatDAO.closeChat(chatId);
    }
}
```

# loyalty\PointService.java

| Property | Value |
|----------|-------|
| Path | `loyalty\PointService.java` |
| Size | 6,286 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:28.373153 |

```java
package com.fashionshop.business.service.loyalty;

import com.fashionshop.business.entity.Order;
import com.fashionshop.business.entity.PointTransaction;
import com.fashionshop.business.entity.User;
import com.fashionshop.common.exception.BadRequestException;
import com.fashionshop.common.exception.NotFoundException;
import com.fashionshop.common.response.PagedResponse;
import com.fashionshop.dataaccess.dao.cartorder.OrderDAO;
import com.fashionshop.dataaccess.dao.identity.UserDAO;
import com.fashionshop.dataaccess.dao.loyalty.PointTransactionDAO;
import com.fashionshop.presentation.dto.loyalty.EarnPointsDTO;
import com.fashionshop.presentation.dto.loyalty.PointQueryDTO;
import com.fashionshop.presentation.dto.loyalty.RedeemPointsDTO;
import jakarta.persistence.EntityManager;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

public class PointService {
    private final PointTransactionDAO pointTransactionDAO = new PointTransactionDAO();
    private final OrderDAO orderDAO = new OrderDAO();
    private final UserDAO userDAO = new UserDAO();

    public Map<String, Object> earnPoints(Integer adminId, EarnPointsDTO body) {
        Order order = orderDAO.findById(body.getOrderId())
                .filter(o -> o.getDeletedAt() == null)
                .orElseThrow(() -> new NotFoundException("Đơn hàng không tồn tại"));

        if (order.getStatus() != Order.OrderStatus.DELIVERED) {
            throw new BadRequestException("Chỉ cộng điểm khi đơn hàng đã giao thành công (DELIVERED)");
        }

        int targetUserId = order.getUser().getId();
        // Tính 1% giá trị đơn hàng
        int pointChange = order.getTotalAmount().multiply(new BigDecimal("0.01")).setScale(0, RoundingMode.FLOOR).intValue();
        if (pointChange <= 0) {
            throw new BadRequestException("Giá trị đơn hàng quá nhỏ, không đủ điều kiện cộng điểm");
        }

        String desc = (body.getDescription() != null && !body.getDescription().isBlank())
                ? body.getDescription()
                : "Cộng điểm từ đơn hàng #" + order.getId();

        PointTransactionDAO.AtomicEarnResult result = pointTransactionDAO.atomicEarnPoints(targetUserId, body.getOrderId(), pointChange, desc);
        if (result == null) {
            throw new NotFoundException("Người dùng không tồn tại");
        }

        if (result.isIdempotent()) {
            return Map.of(
                    "message", "Đơn hàng #" + body.getOrderId() + " đã được cộng điểm trước đó (idempotent)",
                    "currentPoint", result.currentPoint(),
                    "transaction", result.transaction()
            );
        }

        return Map.of(
                "message", "Cộng " + pointChange + " điểm thành công cho user #" + targetUserId,
                "currentPoint", result.currentPoint(),
                "transaction", result.transaction()
        );
    }

    public Map<String, Object> redeemPoints(Integer userId, RedeemPointsDTO body) {
        Order order = orderDAO.findById(body.getOrderId())
                .filter(o -> o.getDeletedAt() == null)
                .orElseThrow(() -> new NotFoundException("Đơn hàng không tồn tại"));

        if (!order.getUser().getId().equals(userId)) {
            throw new BadRequestException("Đơn hàng không thuộc về người dùng này");
        }

        String desc = "Dùng " + body.getPointsToRedeem() + " điểm (tương đương " + body.getPointsToRedeem() + " VNĐ) thanh toán đơn hàng #" + order.getId();

        PointTransactionDAO.AtomicRedeemResult result = pointTransactionDAO.atomicRedeemPoints(
                userId, body.getOrderId(), body.getPointsToRedeem(), order.getTotalAmount(), desc);

        if (result.error() != null) {
            switch (result.error()) {
                case USER_NOT_FOUND -> throw new NotFoundException("Người dùng không tồn tại");
                case INSUFFICIENT_POINTS -> throw new BadRequestException(
                        "Số điểm không đủ. Bạn đang có " + result.currentPoint() + " điểm, cần " + body.getPointsToRedeem() + " điểm");
                case EXCEEDS_ORDER_TOTAL -> throw new BadRequestException(
                        "Số điểm sử dụng (" + body.getPointsToRedeem() + " điểm) vượt quá giá trị đơn hàng (" + order.getTotalAmount().toPlainString() + " VNĐ)");
            }
        }

        if (result.isIdempotent()) {
            return Map.of(
                    "message", "Điểm đã được áp dụng cho đơn hàng #" + body.getOrderId() + " trước đó (idempotent)",
                    "discountAmount", result.discountAmount(),
                    "currentPoint", result.currentPoint(),
                    "transaction", result.transaction()
            );
        }

        return Map.of(
                "message", "Đã dùng " + body.getPointsToRedeem() + " điểm (tương đương " + body.getPointsToRedeem() + " VNĐ)",
                "discountAmount", result.discountAmount(),
                "currentPoint", result.currentPoint(),
                "transaction", result.transaction()
        );
    }

    public PagedResponse<PointTransaction> getHistory(Integer userId, PointQueryDTO query) {
        List<PointTransaction> data = pointTransactionDAO.findByUserId(userId, query.getPage(), query.getLimit());
        long total = pointTransactionDAO.countByUserId(userId);
        return new PagedResponse<>(data, total, query.getPage(), query.getLimit());
    }

    public int getBalance(Integer userId) {
        User user = userDAO.findById(userId)
                .filter(u -> u.getDeletedAt() == null)
                .orElseThrow(() -> new NotFoundException("Người dùng không tồn tại"));
        return user.getRewardPoints() != null ? user.getRewardPoints() : 0;
    }

    public void compensatePointsForCancelledOrder(int orderId, EntityManager em) {
        pointTransactionDAO.compensatePointsForCancelledOrder(orderId, em);
    }
}
```

# loyalty\ReviewService.java

| Property | Value |
|----------|-------|
| Path | `loyalty\ReviewService.java` |
| Size | 4,748 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:28.375152 |

```java
package com.fashionshop.business.service.loyalty;

import com.fashionshop.business.entity.Product;
import com.fashionshop.business.entity.ProductReview;
import com.fashionshop.common.exception.BadRequestException;
import com.fashionshop.common.exception.ConflictException;
import com.fashionshop.common.exception.NotFoundException;
import com.fashionshop.dataaccess.dao.catalog.ProductDAO;
import com.fashionshop.dataaccess.dao.loyalty.ProductReviewDAO;
import com.fashionshop.presentation.dto.loyalty.CreateReviewDTO;
import com.fashionshop.presentation.dto.loyalty.ReplyReviewDTO;
import com.fashionshop.presentation.dto.loyalty.ReviewQueryDTO;

import java.util.List;
import java.util.Map;

public class ReviewService {
    private final ProductReviewDAO reviewDAO = new ProductReviewDAO();
    private final ProductDAO productDAO = new ProductDAO();

    public ProductReview createReview(int userId, CreateReviewDTO body) {
        if (body.getOrderId() == null) {
            throw new BadRequestException("Bạn chỉ có thể đánh giá sản phẩm từ đơn hàng đã giao thành công");
        }
        if (body.getRating() == null || body.getRating() < 1 || body.getRating() > 5) {
            throw new BadRequestException("Số sao đánh giá phải từ 1 đến 5");
        }
        if (body.getContent() == null || body.getContent().trim().isEmpty()) {
            throw new BadRequestException("Nội dung đánh giá không được để trống");
        }

        Product product = productDAO.findById(body.getProductId())
                .filter(p -> p.getDeletedAt() == null)
                .orElseThrow(() -> new NotFoundException("Sản phẩm không tồn tại"));

        boolean validOrder = reviewDAO.verifyDeliveredOrderProduct(userId, body.getOrderId(), body.getProductId());
        if (!validOrder) {
            throw new BadRequestException("Bạn chỉ có thể đánh giá sản phẩm đã mua từ đơn hàng đã giao thành công (DELIVERED)");
        }

        // Chặn trùng lặp (P2002)
        if (reviewDAO.existsByOrderIdAndProductId(body.getOrderId(), body.getProductId())) {
            throw new ConflictException("Bạn đã đánh giá sản phẩm này trong đơn hàng này rồi");
        }

        ProductReview created = reviewDAO.createReview(userId, body);

        // Kích hoạt hook đồng bộ Redis cache rating
        reviewDAO.syncProductRatingHook(body.getProductId());

        return created;
    }

    public Map<String, Object> getReviewsByProduct(ReviewQueryDTO query) {
        productDAO.findById(query.getProductId())
                .filter(p -> p.getDeletedAt() == null)
                .orElseThrow(() -> new NotFoundException("Sản phẩm không tồn tại"));

        List<ProductReview> data = reviewDAO.findReviewsByProduct(query);
        long total = reviewDAO.countReviewsByProduct(query);
        ProductReviewDAO.RatingStats ratingStats = reviewDAO.getAverageRating(query.getProductId());

        int totalPages = (query.getLimit() > 0) ? (int) Math.ceil((double) total / query.getLimit()) : 1;

        return Map.of(
                "data", data,
                "ratingStats", Map.of(
                        "averageRating", ratingStats.averageRating(),
                        "totalReviews", ratingStats.totalReviews()
                ),
                "pagination", Map.of(
                        "page", query.getPage(),
                        "limit", query.getLimit(),
                        "total", total,
                        "totalPages", totalPages
                )
        );
    }

    public ProductReview getReviewById(int id) {
        return reviewDAO.findDetailById(id)
                .orElseThrow(() -> new NotFoundException("Đánh giá không tồn tại"));
    }

    public ProductReview replyToReview(int id, ReplyReviewDTO body) {
        if (body.getShopReply() == null || body.getShopReply().trim().isEmpty()) {
            throw new BadRequestException("Nội dung phản hồi không được để trống");
        }

        ProductReview review = getReviewById(id);
        review.setShopReply(body.getShopReply().trim());

        return reviewDAO.update(review);
    }

    public ProductReview toggleHideReview(int id, boolean isHidden) {
        ProductReview review = getReviewById(id);
        review.setIsHidden(isHidden);

        ProductReview updated = reviewDAO.update(review);

        // Cập nhật lại rating snapshot trên Redis khi ẩn/hiện review
        reviewDAO.syncProductRatingHook(review.getProduct().getId());

        return updated;
    }
}
```

# loyalty\VoucherReconciliationWorker.java

| Property | Value |
|----------|-------|
| Path | `loyalty\VoucherReconciliationWorker.java` |
| Size | 3,065 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:28.377152 |

```java
package com.fashionshop.business.service.loyalty;

import com.fashionshop.business.entity.Voucher;
import com.fashionshop.common.util.JPAUtil;
import com.fashionshop.common.util.RedisUtil;
import com.fashionshop.dataaccess.dao.loyalty.VoucherDAO;

import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class VoucherReconciliationWorker {
    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public static void start() {
        scheduler.scheduleAtFixedRate(VoucherReconciliationWorker::reconcileVouchers, 1, 24, TimeUnit.HOURS);
    }

    public static void stop() {
        scheduler.shutdown();
    }

    public static void reconcileVouchers() {
        System.out.println(">>> [CRON] Starting nightly voucher reconciliation job...");
        RedissonClient redisson = RedisUtil.getClient();
        var em = JPAUtil.getEntityManager();

        try {
            List<Voucher> activeVouchers = em.createQuery(
                    "SELECT v FROM Voucher v WHERE v.deletedAt IS NULL AND v.isActive = true", Voucher.class)
                    .getResultList();

            for (Voucher v : activeVouchers) {
                String totalKey = VoucherDAO.VOUCHER_TOTAL_KEY_PREFIX + v.getCode().trim().toUpperCase();
                RAtomicLong redisCounter = redisson.getAtomicLong(totalKey);

                if (!redisCounter.isExists()) {
                    long ttlSeconds = Math.max(Duration.between(LocalDateTime.now(), v.getEndDate()).getSeconds() + 86400, 3600);
                    redisCounter.set(v.getUsedCount());
                    redisCounter.expire(Duration.ofSeconds(ttlSeconds));
                    continue;
                }

                long redisCount = redisCounter.get();
                int dbCount = v.getUsedCount();

                if (redisCount != dbCount) {
                    System.out.printf(">>> [CRON] Discrepancy detected for Voucher %s. Redis: %d, DB: %d. Syncing DB...%n",
                            v.getCode(), redisCount, dbCount);
                    em.getTransaction().begin();
                    em.createQuery("UPDATE Voucher v SET v.usedCount = :used WHERE v.id = :id")
                            .setParameter("used", (int) redisCount)
                            .setParameter("id", v.getId())
                            .executeUpdate();
                    em.getTransaction().commit();
                }
            }
            System.out.println(">>> [CRON] Voucher reconciliation job finished successfully.");
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
}
```

# loyalty\VoucherService.java

| Property | Value |
|----------|-------|
| Path | `loyalty\VoucherService.java` |
| Size | 9,918 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 16:21:14.611759 |

```java
package com.fashionshop.business.service.loyalty;

import com.fashionshop.business.entity.Voucher;
import com.fashionshop.common.exception.BadRequestException;
import com.fashionshop.common.exception.ConflictException;
import com.fashionshop.common.exception.NotFoundException;
import com.fashionshop.common.response.PagedResponse;
import com.fashionshop.dataaccess.dao.loyalty.VoucherDAO;
import com.fashionshop.presentation.dto.loyalty.*;
import jakarta.persistence.EntityManager;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class VoucherService {
    private final VoucherDAO voucherDAO = new VoucherDAO();

    public PagedResponse<Voucher> listVouchers(VoucherQueryDTO query) {
        List<Voucher> data = voucherDAO.findAllPaginated(query.getPage(), query.getLimit(), query.getIsActive());
        long total = voucherDAO.countAll(query.getIsActive());
        return new PagedResponse<>(data, total, query.getPage(), query.getLimit());
    }

    public Voucher getVoucherById(Integer id) {
        return voucherDAO.findById(id)
                .filter(v -> v.getDeletedAt() == null)
                .orElseThrow(() -> new NotFoundException("Voucher không tồn tại"));
    }

    public Voucher createVoucher(CreateVoucherDTO body) {
        if (body.getCode() == null || body.getCode().trim().length() < 3) {
            throw new BadRequestException("Mã voucher tối thiểu 3 ký tự");
        }
        if (body.getEndDate().isBefore(body.getStartDate())) {
            throw new BadRequestException("Ngày kết thúc phải sau ngày bắt đầu");
        }
        if (body.getDiscountType() == Voucher.DiscountType.PERCENTAGE) {
            if (body.getDiscountValue().compareTo(BigDecimal.ZERO) <= 0 || body.getDiscountValue().compareTo(BigDecimal.valueOf(100)) > 0) {
                throw new BadRequestException("Phần trăm giảm phải từ 1 đến 100");
            }
        }

        String normalizedCode = body.getCode().trim().toUpperCase();
        if (voucherDAO.findByCode(normalizedCode).isPresent()) {
            throw new ConflictException("Mã voucher \"" + normalizedCode + "\" đã tồn tại trong hệ thống");
        }

        Voucher voucher = new Voucher();
        voucher.setCode(normalizedCode);
        voucher.setDiscountType(body.getDiscountType());
        voucher.setDiscountValue(body.getDiscountValue());
        voucher.setMinOrderValue(body.getMinOrderValue() != null ? body.getMinOrderValue() : BigDecimal.ZERO);
        voucher.setMaxDiscountAmt(body.getMaxDiscountAmt());
        voucher.setTotalUsageLimit(body.getTotalUsageLimit() != null ? body.getTotalUsageLimit() : 0);
        voucher.setUserUsageLimit(body.getUserUsageLimit() != null ? body.getUserUsageLimit() : 1);
        voucher.setStartDate(body.getStartDate());
        voucher.setEndDate(body.getEndDate());
        voucher.setIsActive(body.getIsActive() != null ? body.getIsActive() : true);

        return voucherDAO.save(voucher);
    }

    public Voucher updateVoucher(Integer id, UpdateVoucherDTO body) {
        Voucher voucher = getVoucherById(id);

        if (body.getCode() != null) {
            String normalizedCode = body.getCode().trim().toUpperCase();
            voucherDAO.findByCode(normalizedCode).ifPresent(existing -> {
                if (!existing.getId().equals(id)) {
                    throw new ConflictException("Mã voucher \"" + normalizedCode + "\" đã tồn tại trong hệ thống");
                }
            });
            voucher.setCode(normalizedCode);
        }

        if (body.getDiscountType() != null) voucher.setDiscountType(body.getDiscountType());
        if (body.getDiscountValue() != null) voucher.setDiscountValue(body.getDiscountValue());
        if (body.getMinOrderValue() != null) voucher.setMinOrderValue(body.getMinOrderValue());
        if (body.getMaxDiscountAmt() != null) voucher.setMaxDiscountAmt(body.getMaxDiscountAmt());
        if (body.getTotalUsageLimit() != null) voucher.setTotalUsageLimit(body.getTotalUsageLimit());
        if (body.getUserUsageLimit() != null) voucher.setUserUsageLimit(body.getUserUsageLimit());
        if (body.getStartDate() != null) voucher.setStartDate(body.getStartDate());
        if (body.getEndDate() != null) voucher.setEndDate(body.getEndDate());
        if (body.getIsActive() != null) voucher.setIsActive(body.getIsActive());

        Voucher updated = voucherDAO.update(voucher);
        voucherDAO.invalidateVoucherCache(updated.getCode());
        return updated;
    }

    public void deleteVoucher(Integer id) {
        Voucher voucher = getVoucherById(id);
        voucher.setDeletedAt(LocalDateTime.now());
        voucherDAO.update(voucher);
        voucherDAO.invalidateVoucherCache(voucher.getCode());
    }

    public Map<String, Object> validateAndCalculate(ValidateVoucherDTO body, Integer userId) {
        Voucher voucher = voucherDAO.findByCode(body.getCode())
                .orElseThrow(() -> new NotFoundException("Mã voucher không hợp lệ hoặc không tồn tại"));

        if (Boolean.FALSE.equals(voucher.getIsActive())) {
            throw new BadRequestException("Voucher đã bị vô hiệu hóa");
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(voucher.getStartDate())) throw new BadRequestException("Voucher chưa có hiệu lực");
        if (now.isAfter(voucher.getEndDate())) throw new BadRequestException("Voucher đã hết hạn sử dụng");

        if (voucher.getTotalUsageLimit() > 0 && voucher.getUsedCount() >= voucher.getTotalUsageLimit()) {
            throw new BadRequestException("Voucher đã hết lượt sử dụng");
        }

        if (voucher.getUserUsageLimit() > 0) {
            int usedCount = voucherDAO.countUserVoucherUsage(userId, voucher.getCode());
            if (usedCount >= voucher.getUserUsageLimit()) {
                throw new BadRequestException("Bạn đã sử dụng mã voucher này " + usedCount + " lần (tối đa " + voucher.getUserUsageLimit() + " lần/người)");
            }
        }

        BigDecimal minOrder = voucher.getMinOrderValue();
        if (body.getOrderTotal().compareTo(minOrder) < 0) {
            throw new BadRequestException("Đơn hàng tối thiểu " + minOrder.toPlainString() + " VNĐ để áp dụng");
        }

        BigDecimal discountAmount;
        if (voucher.getDiscountType() == Voucher.DiscountType.PERCENTAGE) {
            discountAmount = body.getOrderTotal().multiply(voucher.getDiscountValue()).divide(BigDecimal.valueOf(100));
            if (voucher.getMaxDiscountAmt() != null && discountAmount.compareTo(voucher.getMaxDiscountAmt()) > 0) {
                discountAmount = voucher.getMaxDiscountAmt();
            }
        } else {
            discountAmount = voucher.getDiscountValue();
        }

        discountAmount = discountAmount.min(body.getOrderTotal());

        return Map.of(
                "code", voucher.getCode(),
                "discountType", voucher.getDiscountType(),
                "discountAmount", discountAmount,
                "finalTotal", body.getOrderTotal().subtract(discountAmount),
                "voucher", voucher
        );
    }

    public void applyVoucher(String code, Integer userId) {
        VoucherDAO.AtomicApplyResult res = voucherDAO.atomicApplyVoucher(code, userId);
        if (!res.success()) {
            switch (res.reason()) {
                case VOUCHER_NOT_FOUND -> throw new NotFoundException("Voucher không tồn tại");
                case VOUCHER_INACTIVE -> throw new BadRequestException("Voucher đã bị vô hiệu hóa");
                case VOUCHER_NOT_STARTED -> throw new BadRequestException("Voucher chưa có hiệu lực");
                case VOUCHER_EXPIRED -> throw new BadRequestException("Voucher đã hết hạn sử dụng");
                case VOUCHER_USAGE_LIMIT_REACHED -> throw new BadRequestException("Voucher đã hết lượt sử dụng");
                case USER_USAGE_LIMIT_REACHED -> throw new BadRequestException("Bạn đã dùng hết lượt sử dụng mã voucher này");
            }
        }
    }

    public BigDecimal applyVoucherSafely(String code, Integer userId, BigDecimal orderTotal, EntityManager em) {
        Voucher voucher = voucherDAO.applyVoucherSafely(code, userId, em);
        if (voucher == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal minOrder = voucher.getMinOrderValue() != null ? voucher.getMinOrderValue() : BigDecimal.ZERO;
        if (orderTotal.compareTo(minOrder) < 0) {
            throw new BadRequestException("Đơn hàng tối thiểu " + minOrder.toPlainString() + " VNĐ để áp dụng");
        }

        BigDecimal discountAmount;
        if (voucher.getDiscountType() == Voucher.DiscountType.PERCENTAGE) {
            discountAmount = orderTotal.multiply(voucher.getDiscountValue()).divide(BigDecimal.valueOf(100));
            if (voucher.getMaxDiscountAmt() != null && discountAmount.compareTo(voucher.getMaxDiscountAmt()) > 0) {
                discountAmount = voucher.getMaxDiscountAmt();
            }
        } else {
            discountAmount = voucher.getDiscountValue() != null ? voucher.getDiscountValue() : BigDecimal.ZERO;
        }

        return discountAmount.min(orderTotal);
    }

    public Voucher applyVoucherSafely(String code, Integer userId, EntityManager em) {
        return voucherDAO.applyVoucherSafely(code, userId, em);
    }

    public void releaseVoucherSafely(String code, Integer userId, EntityManager em) {
        voucherDAO.releaseVoucherSafely(code, userId, em);
    }
}
```

