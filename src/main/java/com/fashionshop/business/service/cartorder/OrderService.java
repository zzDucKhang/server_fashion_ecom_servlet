package com.fashionshop.business.service.cartorder;

import com.fashionshop.business.entity.*;
import com.fashionshop.business.service.loyalty.VoucherService;
import com.fashionshop.common.exception.BadRequestException;
import com.fashionshop.common.exception.ForbiddenException;
import com.fashionshop.common.exception.NotFoundException;
import com.fashionshop.common.response.PagedResponse;
import com.fashionshop.common.util.JPAUtil;
import com.fashionshop.dataaccess.dao.cartorder.OrderDAO;
import com.fashionshop.dataaccess.dao.inventory.FlashSaleCampaignDAO;
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
import java.util.Optional;

public class OrderService {

    private final VoucherService voucherService = new VoucherService();
    private final OrderDAO orderDAO = new OrderDAO();
    private final FlashSaleCampaignDAO flashSaleDAO = new FlashSaleCampaignDAO();

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

                // Kiểm tra và giữ suất Flash Sale nếu đang trong đợt khuyến mãi
                Optional<FlashSaleItem> flashSaleOpt = flashSaleDAO.findActiveFlashSaleItemWithLock(variant.getId(), em);
                if (flashSaleOpt.isPresent()) {
                    FlashSaleItem fsi = flashSaleOpt.get();
                    if (fsi.reserveItem(item.getQuantity())) {
                        unitPrice = fsi.getFlashSalePrice();
                    } else {
                        throw new BadRequestException("Sản phẩm " + (variant.getProduct() != null ? variant.getProduct().getDefaultName() : "") + " đã hết suất khuyến mãi Flash Sale");
                    }
                }

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

            // Hoàn lại kho cho từng item và trả lại suất Flash Sale nếu có
            for (OrderItem oi : order.getOrderItems()) {
                ProductVariant variant = oi.getVariant();
                if (variant != null) {
                    variant.updateStock(oi.getQuantity());

                    Optional<FlashSaleItem> flashSaleOpt = flashSaleDAO.findActiveFlashSaleItemWithLock(variant.getId(), em);
                    if (flashSaleOpt.isPresent()) {
                        flashSaleOpt.get().releaseItem(oi.getQuantity());
                    }
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

    public java.util.Map<String, Object> getDashboardStats() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Long totalUsers = em.createQuery("SELECT COUNT(u) FROM User u WHERE u.deletedAt IS NULL", Long.class).getSingleResult();
            Long totalOrders = em.createQuery("SELECT COUNT(o) FROM Order o WHERE o.deletedAt IS NULL", Long.class).getSingleResult();
            BigDecimal totalRevenue = em.createQuery("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.status != com.fashionshop.business.entity.Order.OrderStatus.CANCELLED AND o.deletedAt IS NULL", BigDecimal.class).getSingleResult();
            Long totalProducts = em.createQuery("SELECT COUNT(p) FROM Product p WHERE p.deletedAt IS NULL", Long.class).getSingleResult();

            LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
            Long newUsersThisMonth = em.createQuery("SELECT COUNT(u) FROM User u WHERE u.createdAt >= :startOfMonth AND u.deletedAt IS NULL", Long.class)
                    .setParameter("startOfMonth", startOfMonth)
                    .getSingleResult();
            Long ordersPending = em.createQuery("SELECT COUNT(o) FROM Order o WHERE o.status = com.fashionshop.business.entity.Order.OrderStatus.PENDING AND o.deletedAt IS NULL", Long.class).getSingleResult();

            return java.util.Map.of(
                    "totalUsers", totalUsers != null ? totalUsers : 0L,
                    "totalOrders", totalOrders != null ? totalOrders : 0L,
                    "totalRevenue", totalRevenue != null ? totalRevenue : BigDecimal.ZERO,
                    "totalProducts", totalProducts != null ? totalProducts : 0L,
                    "newUsersThisMonth", newUsersThisMonth != null ? newUsersThisMonth : 0L,
                    "ordersPending", ordersPending != null ? ordersPending : 0L
            );
        } finally {
            em.close();
        }
    }
}