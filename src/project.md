# Project Dump

**Root:** `C:\Programing\Lap_trinh_Web\fashion_ecom\src`

**Generated:** 2026-10-11 21:09:10.205808

**Files:** 198 | **Total size:** 83,181 bytes

---

# main\java\com\fashionshop\business\entity\BaseEntity.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\business\entity\BaseEntity.java` |
| Size | 1,875 bytes |
| Extension | `.java` |
| Modified | 2026-10-07 10:26:40.669000 |

```java
package com.fashionshop.business.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@MappedSuperclass
public abstract class BaseEntity {

    @Column(name = "\"createdById\"")
    private Integer createdById;

    @Column(name = "\"updatedById\"")
    private Integer updatedById;

    @Column(name = "\"deletedById\"")
    private Integer deletedById;

    @Column(name = "\"deletedAt\"")
    private LocalDateTime deletedAt;

    @Column(name = "\"createdAt\"", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "\"updatedAt\"", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Integer getCreatedById() { return createdById; }
    public void setCreatedById(Integer createdById) { this.createdById = createdById; }

    public Integer getUpdatedById() { return updatedById; }
    public void setUpdatedById(Integer updatedById) { this.updatedById = updatedById; }

    public Integer getDeletedById() { return deletedById; }
    public void setDeletedById(Integer deletedById) { this.deletedById = deletedById; }

    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
```

# main\java\com\fashionshop\business\entity\Brand.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\business\entity\Brand.java` |
| Size | 1,184 bytes |
| Extension | `.java` |
| Modified | 2026-10-07 23:01:07.534000 |

```java
package com.fashionshop.business.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "\"Brand\"")
public class Brand extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "\"defaultName\"", nullable = false, length = 255)
    private String defaultName;

    @Column(name = "\"logoUrl\"", length = 1000)
    private String logoUrl;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getDefaultName() { return defaultName; }
    public void setDefaultName(String defaultName) { this.defaultName = defaultName; }
    public String getLogoUrl() { return logoUrl; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }

    public void updateBrandInfo(String name, String logo) {
        if (name != null && !name.isBlank()) {
            this.defaultName = name;
        }
        if (logo != null && !logo.isBlank()) {
            this.logoUrl = logo;
        }
    }

    public void softDelete() {
        setDeletedAt(LocalDateTime.now());
    }
}
```

# main\java\com\fashionshop\business\entity\Cart.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\business\entity\Cart.java` |
| Size | 2,050 bytes |
| Extension | `.java` |
| Modified | 2026-10-07 23:05:42.847000 |

```java
package com.fashionshop.business.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "\"Cart\"")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"userId\"", unique = true)
    private User user;

    @Column(name = "\"sessionId\"", unique = true, length = 255)
    private String sessionId;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> cartItems = new ArrayList<>();

    @Column(name = "\"createdAt\"", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "\"updatedAt\"", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public List<CartItem> getCartItems() { return cartItems; }
    public void setCartItems(List<CartItem> cartItems) { this.cartItems = cartItems; }

    public void addItem(ProductVariant variant, int qty) {
        for (CartItem item : this.cartItems) {
            if (item.getVariant().getId().equals(variant.getId())) {
                item.updateQuantity(item.getQuantity() + qty);
                return;
            }
        }
        CartItem newItem = new CartItem();
        newItem.setCart(this);
        newItem.setVariant(variant);
        newItem.setQuantity(qty);
        this.cartItems.add(newItem);
    }

    public void removeItem(CartItem item) {
        this.cartItems.remove(item);
    }

    public void clearCart() {
        this.cartItems.clear();
    }
}
```

# main\java\com\fashionshop\business\entity\CartItem.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\business\entity\CartItem.java` |
| Size | 1,913 bytes |
| Extension | `.java` |
| Modified | 2026-10-07 23:06:00.324000 |

```java
package com.fashionshop.business.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "\"CartItem\"", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"\"cartId\"", "\"variantId\""})
})
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"cartId\"", nullable = false)
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"variantId\"", nullable = false)
    private ProductVariant variant;

    @Column(nullable = false)
    private Integer quantity = 1;

    @Column(name = "\"createdAt\"", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "\"updatedAt\"", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Cart getCart() { return cart; }
    public void setCart(Cart cart) { this.cart = cart; }
    public ProductVariant getVariant() { return variant; }
    public void setVariant(ProductVariant variant) { this.variant = variant; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public void updateQuantity(int newQty) {
        if (newQty <= 0) {
            throw new IllegalArgumentException("Số lượng phải lớn hơn 0");
        }
        this.quantity = newQty;
    }

    public BigDecimal getSubtotal() {
        if (this.variant != null && this.variant.getSellingPrice() != null) {
            return this.variant.getSellingPrice().multiply(BigDecimal.valueOf(this.quantity));
        }
        return BigDecimal.ZERO;
    }
}
```

# main\java\com\fashionshop\business\entity\Category.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\business\entity\Category.java` |
| Size | 1,786 bytes |
| Extension | `.java` |
| Modified | 2026-10-07 23:00:51.080000 |

```java
package com.fashionshop.business.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "\"Category\"")
public class Category extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "\"defaultName\"", nullable = false, length = 255)
    private String defaultName;

    @Column(name = "\"iconUrl\"", length = 1000)
    private String iconUrl;

    @Column(name = "\"isVisible\"", nullable = false)
    private Boolean isVisible = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"parentId\"")
    private Category parent;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getDefaultName() { return defaultName; }
    public void setDefaultName(String defaultName) { this.defaultName = defaultName; }
    public String getIconUrl() { return iconUrl; }
    public void setIconUrl(String iconUrl) { this.iconUrl = iconUrl; }
    public Boolean getIsVisible() { return isVisible; }
    public void setIsVisible(Boolean isVisible) { this.isVisible = isVisible; }
    public Category getParent() { return parent; }
    public void setParent(Category parent) { this.parent = parent; }

    public void toggleVisibility() {
        this.isVisible = (this.isVisible == null) ? Boolean.FALSE : !this.isVisible;
    }

    public boolean isRootCategory() {
        return this.parent == null;
    }

    public void changeParent(Category newParent) {
        if (newParent != null && newParent.getId().equals(this.getId())) {
            throw new IllegalArgumentException("Một danh mục không thể làm cha của chính nó");
        }
        this.parent = newParent;
    }
}
```

# main\java\com\fashionshop\business\entity\Chat.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\business\entity\Chat.java` |
| Size | 1,929 bytes |
| Extension | `.java` |
| Modified | 2026-10-07 22:51:47.195000 |

```java
package com.fashionshop.business.entity;


import com.fashionshop.business.entity.enums.ChatStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "\"Chat\"")
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChatStatus status = ChatStatus.OPEN;

    @Column(name = "\"startedAt\"", nullable = false)
    private LocalDateTime startedAt = LocalDateTime.now();

    @Column(name = "\"endedAt\"")
    private LocalDateTime endedAt;

    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> messages = new ArrayList<>();

    // Business Methods theo Class Diagram
    public void addMessage(Message msg) {
        if (!isEnded()) {
            this.messages.add(msg);
            msg.setChat(this);
        }
    }

    public void closeChat() {
        this.status = ChatStatus.CLOSED;
        this.endedAt = LocalDateTime.now();
    }

    public boolean isEnded() {
        return this.status == ChatStatus.CLOSED;
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public ChatStatus getStatus() { return status; }
    public void setStatus(ChatStatus status) { this.status = status; }
    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }
    public LocalDateTime getEndedAt() { return endedAt; }
    public void setEndedAt(LocalDateTime endedAt) { this.endedAt = endedAt; }
    public List<Message> getMessages() { return messages; }
    public void setMessages(List<Message> messages) { this.messages = messages; }
}

```

# main\java\com\fashionshop\business\entity\DeviceSession.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\business\entity\DeviceSession.java` |
| Size | 2,222 bytes |
| Extension | `.java` |
| Modified | 2026-10-07 23:05:25.456000 |

```java
package com.fashionshop.business.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "\"DeviceSession\"")
public class DeviceSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"userId\"", nullable = false)
    private User user;

    @Column(name = "\"refreshToken\"", nullable = false, unique = true, length = 1000)
    private String refreshToken;

    @Column(name = "\"ipAddress\"", nullable = false, length = 100)
    private String ipAddress;

    @Column(name = "\"isActive\"", nullable = false)
    private Boolean isActive = true;

    @Column(name = "\"expiresAt\"", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "\"createdAt\"", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "\"updatedAt\"", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }

    public void revokeSession() {
        this.isActive = false;
    }

    public boolean isTokenExpired() {
        return this.expiresAt != null && LocalDateTime.now().isAfter(this.expiresAt);
    }

    public void refresh(String newToken, LocalDateTime newExpiresAt) {
        this.refreshToken = newToken;
        this.expiresAt = newExpiresAt;
        this.isActive = true;
    }
}
```

# main\java\com\fashionshop\business\entity\enums\ChatStatus.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\business\entity\enums\ChatStatus.java` |
| Size | 100 bytes |
| Extension | `.java` |
| Modified | 2026-10-07 22:51:26.461000 |

```java
package com.fashionshop.business.entity.enums;

public enum ChatStatus {
    OPEN,
    CLOSED
}
```

# main\java\com\fashionshop\business\entity\enums\UserStatus.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\business\entity\enums\UserStatus.java` |
| Size | 118 bytes |
| Extension | `.java` |
| Modified | 2026-10-05 11:08:50.092000 |

```java
package com.fashionshop.business.entity.enums;

public enum UserStatus {
    ACTIVE,
    INACTIVE,
    BLOCKED
}
```

# main\java\com\fashionshop\business\entity\FlashSaleCampaign.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\business\entity\FlashSaleCampaign.java` |
| Size | 2,156 bytes |
| Extension | `.java` |
| Modified | 2026-10-07 23:08:10.315000 |

```java
package com.fashionshop.business.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "\"FlashSaleCampaign\"")
public class FlashSaleCampaign {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(name = "\"bannerUrl\"", length = 1000)
    private String bannerUrl;

    @Column(name = "\"startTime\"", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "\"endTime\"", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "\"isActive\"", nullable = false)
    private Boolean isActive = true;

    @Column(name = "\"deletedAt\"")
    private LocalDateTime deletedAt;

    @Column(name = "\"createdAt\"", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "\"updatedAt\"", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getBannerUrl() { return bannerUrl; }
    public void setBannerUrl(String bannerUrl) { this.bannerUrl = bannerUrl; }
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public boolean isRunningNow() {
        LocalDateTime now = LocalDateTime.now();
        return Boolean.TRUE.equals(this.isActive)
                && now.isAfter(this.startTime)
                && now.isBefore(this.endTime);
    }

    public void endCampaign() {
        this.isActive = false;
        this.endTime = LocalDateTime.now();
    }
}
```

# main\java\com\fashionshop\business\entity\FlashSaleItem.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\business\entity\FlashSaleItem.java` |
| Size | 2,268 bytes |
| Extension | `.java` |
| Modified | 2026-10-07 23:08:20.213000 |

```java
package com.fashionshop.business.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "\"FlashSaleItem\"", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"\"campaignId\"", "\"variantId\""})
})
public class FlashSaleItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"campaignId\"", nullable = false)
    private FlashSaleCampaign campaign;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"variantId\"", nullable = false)
    private ProductVariant variant;

    @Column(name = "\"flashSalePrice\"", nullable = false, precision = 12, scale = 2)
    private BigDecimal flashSalePrice;

    @Column(name = "\"promoQuantity\"", nullable = false)
    private Integer promoQuantity;

    @Column(name = "\"soldQuantity\"", nullable = false)
    private Integer soldQuantity = 0;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public FlashSaleCampaign getCampaign() { return campaign; }
    public void setCampaign(FlashSaleCampaign campaign) { this.campaign = campaign; }
    public ProductVariant getVariant() { return variant; }
    public void setVariant(ProductVariant variant) { this.variant = variant; }
    public BigDecimal getFlashSalePrice() { return flashSalePrice; }
    public void setFlashSalePrice(BigDecimal flashSalePrice) { this.flashSalePrice = flashSalePrice; }
    public Integer getPromoQuantity() { return promoQuantity; }
    public void setPromoQuantity(Integer promoQuantity) { this.promoQuantity = promoQuantity; }
    public Integer getSoldQuantity() { return soldQuantity; }
    public void setSoldQuantity(Integer soldQuantity) { this.soldQuantity = soldQuantity; }

    public boolean isSoldOut() {
        return this.soldQuantity != null && this.promoQuantity != null
                && this.soldQuantity >= this.promoQuantity;
    }

    public boolean reserveItem(int qty) {
        if (qty > 0 && (this.soldQuantity + qty) <= this.promoQuantity) {
            this.soldQuantity += qty;
            return true;
        }
        return false;
    }
}
```

# main\java\com\fashionshop\business\entity\Message.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\business\entity\Message.java` |
| Size | 1,652 bytes |
| Extension | `.java` |
| Modified | 2026-10-07 22:52:27.284000 |

```java
package com.fashionshop.business.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "\"Message\"")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"chatId\"", nullable = false)
    private Chat chat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"senderId\"", nullable = false)
    private User sender;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "\"sendDate\"", nullable = false)
    private LocalDateTime sendDate = LocalDateTime.now();

    @Column(name = "\"isRead\"", nullable = false)
    private Boolean isRead = false;

    // Business Methods theo Class Diagram
    public void markAsRead() {
        this.isRead = true;
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Chat getChat() { return chat; }
    public void setChat(Chat chat) { this.chat = chat; }
    public User getSender() { return sender; }
    public void setSender(User sender) { this.sender = sender; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public LocalDateTime getSendDate() { return sendDate; }
    public void setSendDate(LocalDateTime sendDate) { this.sendDate = sendDate; }
    public Boolean getIsRead() { return isRead; }
    public void setIsRead(Boolean isRead) { this.isRead = isRead; }
}
```

# main\java\com\fashionshop\business\entity\Order.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\business\entity\Order.java` |
| Size | 5,970 bytes |
| Extension | `.java` |
| Modified | 2026-10-07 23:07:02.001000 |

```java
package com.fashionshop.business.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "\"Order\"")
public class Order extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"userId\"", nullable = false)
    private User user;

    @Column(name = "\"shippingAddress\"", nullable = false, length = 1000)
    private String shippingAddress;

    @Column(name = "\"shippingFee\"", nullable = false, precision = 12, scale = 2)
    private BigDecimal shippingFee = BigDecimal.ZERO;

    @Column(name = "\"shippingDiscount\"", nullable = false, precision = 12, scale = 2)
    private BigDecimal shippingDiscount = BigDecimal.ZERO;

    @Column(name = "\"voucherCode\"", length = 100)
    private String voucherCode;

    @Column(name = "\"voucherDiscount\"", nullable = false, precision = 12, scale = 2)
    private BigDecimal voucherDiscount = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "\"paymentMethod\"", nullable = false)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "\"paymentStatus\"", nullable = false)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.PENDING_PAYMENT;

    @Column(name = "\"customerNote\"", columnDefinition = "TEXT")
    private String customerNote;

    @Column(name = "\"orderDate\"", nullable = false)
    private LocalDateTime orderDate = LocalDateTime.now();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private PaymentTransaction paymentTransaction;

    public enum PaymentMethod { COD, VNPAY, MOMO, BANK_TRANSFER }
    public enum PaymentStatus { PENDING, SUCCESS, FAILED, REFUNDED }
    public enum OrderStatus { PENDING_PAYMENT, CONFIRMED, PROCESSING, SHIPPED, DELIVERED, CANCELLED, RETURNED }


    public BigDecimal calculateSubTotal() {
        BigDecimal subTotal = BigDecimal.ZERO;
        for (OrderItem item : orderItems) {
            subTotal = subTotal.add(item.getLineTotal());
        }
        return subTotal;
    }

    public BigDecimal calculateTotal() {
        BigDecimal total = calculateSubTotal().add(shippingFee)
                .subtract(shippingDiscount)
                .subtract(voucherDiscount);
        return total.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : total;
    }

    public void applyVoucher(String code, BigDecimal discountAmt) {
        this.voucherCode = code;
        this.voucherDiscount = discountAmt;
    }

    public void applyShippingDiscount(BigDecimal discountAmt) {
        this.shippingDiscount = discountAmt;
    }

    public void applyPoints(int points, BigDecimal discountAmt) {
        // Áp dụng giảm giá bằng điểm
        this.voucherDiscount = this.voucherDiscount.add(discountAmt);
    }

    public void markAsPaid() {
        this.paymentStatus = PaymentStatus.SUCCESS;
        this.status = OrderStatus.CONFIRMED;
    }

    public void updateStatus(OrderStatus newStatus) {
        this.status = newStatus;
    }

    public void cancelOrder() {
        if (this.status == OrderStatus.PENDING_PAYMENT || this.status == OrderStatus.CONFIRMED) {
            this.status = OrderStatus.CANCELLED;
        }
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }
    public BigDecimal getShippingFee() { return shippingFee; }
    public void setShippingFee(BigDecimal shippingFee) { this.shippingFee = shippingFee; }
    public BigDecimal getShippingDiscount() { return shippingDiscount; }
    public void setShippingDiscount(BigDecimal shippingDiscount) { this.shippingDiscount = shippingDiscount; }
    public String getVoucherCode() { return voucherCode; }
    public void setVoucherCode(String voucherCode) { this.voucherCode = voucherCode; }
    public BigDecimal getVoucherDiscount() { return voucherDiscount; }
    public void setVoucherDiscount(BigDecimal voucherDiscount) { this.voucherDiscount = voucherDiscount; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }
    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; }
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
    public String getCustomerNote() { return customerNote; }
    public void setCustomerNote(String customerNote) { this.customerNote = customerNote; }
    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }
    public List<OrderItem> getOrderItems() { return orderItems; }
    public void setOrderItems(List<OrderItem> orderItems) { this.orderItems = orderItems; }
    public PaymentTransaction getPaymentTransaction() { return paymentTransaction; }
    public void setPaymentTransaction(PaymentTransaction paymentTransaction) { this.paymentTransaction = paymentTransaction; }



}

```

# main\java\com\fashionshop\business\entity\OrderItem.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\business\entity\OrderItem.java` |
| Size | 3,243 bytes |
| Extension | `.java` |
| Modified | 2026-10-07 23:07:03.202000 |

```java
package com.fashionshop.business.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "\"OrderItem\"")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"orderId\"", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"variantId\"")
    private ProductVariant variant;

    @Column(name = "\"productName\"", nullable = false, length = 255)
    private String productName;

    @Column(name = "\"skuCode\"", nullable = false, length = 150)
    private String skuCode;

    @Column(name = "\"variantValue\"", nullable = false, length = 255)
    private String variantValue;

    @Column(name = "\"productImageUrl\"", length = 1000)
    private String productImageUrl;

    @Column(name = "\"unitPrice\"", nullable = false, precision = 12, scale = 2)
    private BigDecimal unitPrice;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "\"createdAt\"", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();


    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }
    public ProductVariant getVariant() { return variant; }
    public void setVariant(ProductVariant variant) { this.variant = variant; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public String getSkuCode() { return skuCode; }
    public void setSkuCode(String skuCode) { this.skuCode = skuCode; }
    public String getVariantValue() { return variantValue; }
    public void setVariantValue(String variantValue) { this.variantValue = variantValue; }
    public String getProductImageUrl() { return productImageUrl; }
    public void setProductImageUrl(String productImageUrl) { this.productImageUrl = productImageUrl; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public BigDecimal getLineTotal() {
        if (this.unitPrice != null && this.quantity != null) {
            return this.unitPrice.multiply(BigDecimal.valueOf(this.quantity));
        }
        return BigDecimal.ZERO;
    }

    public void freezeSnapshot(ProductVariant variant) {
        this.variant = variant;
        this.productName = variant.getProduct().getDefaultName();
        this.skuCode = variant.getSku();
        this.variantValue = (variant.getVariant() != null ? variant.getVariant() : "") + ": "
                + (variant.getVariantValue() != null ? variant.getVariantValue() : "");
        this.productImageUrl = variant.getImageUrl();
        this.unitPrice = variant.getSellingPrice();
    }
}
```

# main\java\com\fashionshop\business\entity\PaymentTransaction.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\business\entity\PaymentTransaction.java` |
| Size | 2,328 bytes |
| Extension | `.java` |
| Modified | 2026-10-07 23:07:37.056000 |

```java
package com.fashionshop.business.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "\"PaymentTransaction\"")
public class PaymentTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"orderId\"", nullable = false, unique = true)
    private Order order;

    @Column(nullable = false, length = 100)
    private String provider;

    @Column(name = "\"providerTransId\"", unique = true, length = 255)
    private String providerTransId;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Order.PaymentStatus status = Order.PaymentStatus.PENDING;

    @Column(name = "\"createdAt\"", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "\"updatedAt\"", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    // Business Methods theo Class Diagram
    public boolean processWebhook(String payload) {
        // Logic kiểm tra chữ ký payload webhook SePay / VNPay
        return payload != null && !payload.isBlank();
    }

    public void markSuccess() {
        this.status = Order.PaymentStatus.SUCCESS;
        if (this.order != null) {
            this.order.markAsPaid();
        }
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }
    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }
    public String getProviderTransId() { return providerTransId; }
    public void setProviderTransId(String providerTransId) { this.providerTransId = providerTransId; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public Order.PaymentStatus getStatus() { return status; }
    public void setStatus(Order.PaymentStatus status) { this.status = status; }


}
```

# main\java\com\fashionshop\business\entity\Permission.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\business\entity\Permission.java` |
| Size | 1,094 bytes |
| Extension | `.java` |
| Modified | 2026-10-07 23:04:48.927000 |

```java
package com.fashionshop.business.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "\"Permission\"")
public class Permission extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 150)
    private String code;

    @Column(nullable = false, length = 100)
    private String module;

    @Column(columnDefinition = "TEXT")
    private String description;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getModule() { return module; }
    public void setModule(String module) { this.module = module; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean matchModule(String mod) {
        return this.module != null && this.module.equalsIgnoreCase(mod);
    }
}
```

# main\java\com\fashionshop\business\entity\PointTransaction.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\business\entity\PointTransaction.java` |
| Size | 2,307 bytes |
| Extension | `.java` |
| Modified | 2026-10-07 23:08:44.271000 |

```java
package com.fashionshop.business.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "\"PointTransaction\"")
public class PointTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"userId\"", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"orderId\"")
    private Order order;

    @Column(name = "\"pointChange\"", nullable = false)
    private Integer pointChange;

    @Column(name = "\"currentPoint\"", nullable = false)
    private Integer currentPoint;

    @Enumerated(EnumType.STRING)
    @Column(name = "\"transactionType\"", nullable = false)
    private PointTransType transactionType;

    @Column(nullable = false, length = 255)
    private String description;

    @Column(name = "\"createdAt\"", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum PointTransType { EARN, REDEEM, REFUND, EXPIRE }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }
    public Integer getPointChange() { return pointChange; }
    public void setPointChange(Integer pointChange) { this.pointChange = pointChange; }
    public Integer getCurrentPoint() { return currentPoint; }
    public void setCurrentPoint(Integer currentPoint) { this.currentPoint = currentPoint; }
    public PointTransType getTransactionType() { return transactionType; }
    public void setTransactionType(PointTransType transactionType) { this.transactionType = transactionType; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public void recordChange(int delta, String desc) {
        this.pointChange = delta;
        this.description = desc;
    }

    public int getBalanceSnapshot() {
        return this.currentPoint != null ? this.currentPoint : 0;
    }
}
```

# main\java\com\fashionshop\business\entity\Product.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\business\entity\Product.java` |
| Size | 3,217 bytes |
| Extension | `.java` |
| Modified | 2026-10-07 23:17:02.158000 |

```java
package com.fashionshop.business.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "\"Product\"")
public class Product extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "\"defaultName\"", nullable = false, length = 255)
    private String defaultName;

    @Column(name = "\"thumbnailUrl\"", length = 1000)
    private String thumbnailUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus status = ProductStatus.DRAFT;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"categoryId\"", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"brandId\"")
    private Brand brand;

    @ManyToMany
    @JoinTable(
            name = "\"_ProductTags\"",
            joinColumns = @JoinColumn(name = "\"A\""),
            inverseJoinColumns = @JoinColumn(name = "\"B\"")
    )
    private Set<Tag> tags = new HashSet<>();

    public enum ProductStatus { DRAFT, PUBLISHED, OUT_OF_STOCK, ARCHIVED }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getDefaultName() { return defaultName; }
    public void setDefaultName(String defaultName) { this.defaultName = defaultName; }
    public String getThumbnailUrl() { return thumbnailUrl; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }
    public ProductStatus getStatus() { return status; }
    public void setStatus(ProductStatus status) { this.status = status; }
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
    public Brand getBrand() { return brand; }
    public void setBrand(Brand brand) { this.brand = brand; }
    public Set<Tag> getTags() { return tags; }
    public void setTags(Set<Tag> tags) { this.tags = tags; }

    // Cần thêm danh sách images và variants vào Product.java nếu chưa có
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductVariant> variants = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductImage> images = new ArrayList<>();

    // Business Methods
    public void updateStatus(ProductStatus newStatus) {
        this.status = newStatus;
    }

    public void assignBrand(Brand brand) {
        this.brand = brand;
    }

    public void changeCategory(Category category) {
        this.category = category;
    }

    public void addVariant(ProductVariant variant) {
        this.variants.add(variant);
        variant.setProduct(this);
    }

    public void addImage(String imageUrl, int displayOrder) {
        ProductImage img = new ProductImage();
        img.setImageUrl(imageUrl);
        img.setDisplayOrder(displayOrder);
        img.setProduct(this);
        this.images.add(img);
    }
}
```

# main\java\com\fashionshop\business\entity\ProductImage.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\business\entity\ProductImage.java` |
| Size | 1,475 bytes |
| Extension | `.java` |
| Modified | 2026-10-07 23:01:45.388000 |

```java
package com.fashionshop.business.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "\"ProductImage\"")
public class ProductImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"productId\"", nullable = false)
    private Product product;

    @Column(name = "\"imageUrl\"", nullable = false, length = 1000)
    private String imageUrl;

    @Column(name = "\"displayOrder\"", nullable = false)
    private Integer displayOrder = 0;

    @Column(name = "\"createdAt\"", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }

    public void reOrder(int newOrder) {
        this.displayOrder = newOrder;
    }

    public void updateUrl(String newUrl) {
        if (newUrl != null && !newUrl.isBlank()) {
            this.imageUrl = newUrl;
        }
    }
}
```

# main\java\com\fashionshop\business\entity\ProductReview.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\business\entity\ProductReview.java` |
| Size | 2,950 bytes |
| Extension | `.java` |
| Modified | 2026-10-07 23:09:36.629000 |

```java
package com.fashionshop.business.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "\"ProductReview\"", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"\"orderId\"", "\"productId\""})
})
public class ProductReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"productId\"", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"userId\"", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"orderId\"")
    private Order order;

    @Column(nullable = false)
    private Short rating;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "\"shopReply\"", columnDefinition = "TEXT")
    private String shopReply;

    @Column(name = "\"updateCount\"", nullable = false)
    private Integer updateCount = 0;

    @Column(name = "\"isHidden\"", nullable = false)
    private Boolean isHidden = false;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewMedia> medias = new ArrayList<>();

    @Column(name = "\"createdAt\"", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "\"updatedAt\"", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }
    public Short getRating() { return rating; }
    public void setRating(Short rating) { this.rating = rating; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getShopReply() { return shopReply; }
    public void setShopReply(String shopReply) { this.shopReply = shopReply; }
    public Boolean getIsHidden() { return isHidden; }
    public void setIsHidden(Boolean isHidden) { this.isHidden = isHidden; }
    public List<ReviewMedia> getMedias() { return medias; }
    public void setMedias(List<ReviewMedia> medias) { this.medias = medias; }

    public void replyByAdmin(String reply) {
        this.shopReply = reply;
    }

    public void hideReview() {
        this.isHidden = true;
    }

    public boolean isReported() {
        return Boolean.TRUE.equals(this.isHidden);
    }
}
```

# main\java\com\fashionshop\business\entity\ProductVariant.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\business\entity\ProductVariant.java` |
| Size | 3,536 bytes |
| Extension | `.java` |
| Modified | 2026-10-07 23:03:36.970000 |

```java
package com.fashionshop.business.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "\"ProductVariant\"")
public class ProductVariant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"productId\"", nullable = false)
    private Product product;

    @Column(nullable = false, unique = true, length = 150)
    private String sku;

    @Column(name = "\"originalPrice\"", nullable = false, precision = 12, scale = 2)
    private BigDecimal originalPrice;

    @Column(name = "\"sellingPrice\"", nullable = false, precision = 12, scale = 2)
    private BigDecimal sellingPrice;

    @Column(name = "\"weightGram\"", nullable = false)
    private Integer weightGram = 0;

    @Column(name = "\"imageUrl\"", length = 1000)
    private String imageUrl;

    @Column(name = "\"variant\"", length = 100)
    private String variant; // ví dụ: "Size", "Color"

    @Column(name = "\"variantValue\"", length = 100)
    private String variantValue; // ví dụ: "XL", "Đen"

    @Column(name = "\"stockQuantity\"", nullable = false)
    private Integer stockQuantity = 0;

    @Column(name = "\"deletedAt\"")
    private LocalDateTime deletedAt;

    @Column(name = "\"createdAt\"", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Version
    @Column(name = "\"updatedAt\"", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();


    public void updateStock(int quantityChange) {
        this.stockQuantity += quantityChange;
    }

    public void updatePrice(BigDecimal newPrice) {
        this.sellingPrice = newPrice;
    }

    public boolean isStockAvailable(int reqQty) {
        return this.stockQuantity >= reqQty;
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    public BigDecimal getOriginalPrice() { return originalPrice; }
    public void setOriginalPrice(BigDecimal originalPrice) { this.originalPrice = originalPrice; }
    public BigDecimal getSellingPrice() { return sellingPrice; }
    public void setSellingPrice(BigDecimal sellingPrice) { this.sellingPrice = sellingPrice; }
    public Integer getWeightGram() { return weightGram; }
    public void setWeightGram(Integer weightGram) { this.weightGram = weightGram; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getVariant() { return variant; }
    public void setVariant(String variant) { this.variant = variant; }
    public String getVariantValue() { return variantValue; }
    public void setVariantValue(String variantValue) { this.variantValue = variantValue; }
    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }
    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }


}
```

# main\java\com\fashionshop\business\entity\ReturnMedia.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\business\entity\ReturnMedia.java` |
| Size | 1,129 bytes |
| Extension | `.java` |
| Modified | 2026-10-07 12:00:31.408000 |

```java
package com.fashionshop.business.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "\"ReturnMedia\"")
public class ReturnMedia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"returnRequestId\"", nullable = false)
    private ReturnRequest returnRequest;

    @Column(name = "\"mediaUrl\"", nullable = false, length = 1000)
    private String mediaUrl;

    @Column(name = "\"mediaType\"", nullable = false, length = 50)
    private String mediaType;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public ReturnRequest getReturnRequest() { return returnRequest; }
    public void setReturnRequest(ReturnRequest returnRequest) { this.returnRequest = returnRequest; }
    public String getMediaUrl() { return mediaUrl; }
    public void setMediaUrl(String mediaUrl) { this.mediaUrl = mediaUrl; }
    public String getMediaType() { return mediaType; }
    public void setMediaType(String mediaType) { this.mediaType = mediaType; }
}
```

# main\java\com\fashionshop\business\entity\ReturnRequest.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\business\entity\ReturnRequest.java` |
| Size | 2,435 bytes |
| Extension | `.java` |
| Modified | 2026-10-07 23:09:11.872000 |

```java
package com.fashionshop.business.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "\"ReturnRequest\"")
public class ReturnRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"orderItemId\"", nullable = false)
    private OrderItem orderItem;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false, length = 255)
    private String reason;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReturnStatus status = ReturnStatus.PENDING;

    @OneToMany(mappedBy = "returnRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReturnMedia> medias = new ArrayList<>();

    @Column(name = "\"createdAt\"", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "\"updatedAt\"", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    public enum ReturnStatus { PENDING, APPROVED, REJECTED, COMPLETED }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public OrderItem getOrderItem() { return orderItem; }
    public void setOrderItem(OrderItem orderItem) { this.orderItem = orderItem; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public ReturnStatus getStatus() { return status; }
    public void setStatus(ReturnStatus status) { this.status = status; }
    public List<ReturnMedia> getMedias() { return medias; }
    public void setMedias(List<ReturnMedia> medias) { this.medias = medias; }

    public void approve() {
        this.status = ReturnStatus.APPROVED;
    }

    public void reject() {
        this.status = ReturnStatus.REJECTED;
    }

    public void completeRefund() {
        this.status = ReturnStatus.COMPLETED;
    }
}
```

# main\java\com\fashionshop\business\entity\ReviewMedia.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\business\entity\ReviewMedia.java` |
| Size | 1,228 bytes |
| Extension | `.java` |
| Modified | 2026-10-07 23:09:49.580000 |

```java
package com.fashionshop.business.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "\"ReviewMedia\"")
public class ReviewMedia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"reviewId\"", nullable = false)
    private ProductReview review;

    @Column(name = "\"mediaUrl\"", nullable = false, length = 1000)
    private String mediaUrl;

    @Column(name = "\"mediaType\"", nullable = false, length = 50)
    private String mediaType;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public ProductReview getReview() { return review; }
    public void setReview(ProductReview review) { this.review = review; }
    public String getMediaUrl() { return mediaUrl; }
    public void setMediaUrl(String mediaUrl) { this.mediaUrl = mediaUrl; }
    public String getMediaType() { return mediaType; }
    public void setMediaType(String mediaType) { this.mediaType = mediaType; }

    public String getDownloadLink() {
        return this.mediaUrl;
    }

    public String getDirectUrl() {
        return this.mediaUrl;
    }
}
```

# main\java\com\fashionshop\business\entity\Role.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\business\entity\Role.java` |
| Size | 1,996 bytes |
| Extension | `.java` |
| Modified | 2026-10-07 23:04:35.014000 |

```java
package com.fashionshop.business.entity;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "\"Role\"")
public class Role extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 100)
    private String code;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(name = "\"isActive\"", nullable = false)
    private Boolean isActive = true;

    @ManyToMany
    @JoinTable(
            name = "\"_RolePermissions\"",
            joinColumns = @JoinColumn(name = "\"A\""),
            inverseJoinColumns = @JoinColumn(name = "\"B\"")
    )
    private Set<Permission> permissions = new HashSet<>();

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    public Set<Permission> getPermissions() { return permissions; }
    public void setPermissions(Set<Permission> permissions) { this.permissions = permissions; }

    // Trong Role.java
    public void setActive(Boolean status) {
        this.isActive = status;
    }

    public void addPermission(Permission permission) {
        this.permissions.add(permission);
    }

    public void removePermission(Permission permission) {
        this.permissions.remove(permission);
    }

    public Boolean hasPermission(String permissionCode) {
        if (permissionCode == null) return false;
        return this.permissions.stream()
                .anyMatch(p -> permissionCode.equalsIgnoreCase(p.getCode()));
    }
}
```

# main\java\com\fashionshop\business\entity\Shipment.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\business\entity\Shipment.java` |
| Size | 2,515 bytes |
| Extension | `.java` |
| Modified | 2026-10-07 23:07:17.867000 |

```java
package com.fashionshop.business.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "\"Shipment\"")
public class Shipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"orderId\"", nullable = false, unique = true)
    private Order order;

    @Column(nullable = false, length = 100)
    private String carrier;

    @Column(name = "\"trackingCode\"", nullable = false, unique = true, length = 150)
    private String trackingCode;

    @Column(name = "\"actualShippingFee\"", nullable = false, precision = 12, scale = 2)
    private BigDecimal actualShippingFee;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShipmentStatus status = ShipmentStatus.PENDING;

    @Column(name = "\"estimatedDelivery\"")
    private LocalDateTime estimatedDelivery;

    @Column(name = "\"createdAt\"", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "\"updatedAt\"", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    public enum ShipmentStatus { PENDING, PICKED_UP, IN_TRANSIT, DELIVERED, FAILED }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }
    public String getCarrier() { return carrier; }
    public void setCarrier(String carrier) { this.carrier = carrier; }
    public String getTrackingCode() { return trackingCode; }
    public void setTrackingCode(String trackingCode) { this.trackingCode = trackingCode; }
    public BigDecimal getActualShippingFee() { return actualShippingFee; }
    public void setActualShippingFee(BigDecimal actualShippingFee) { this.actualShippingFee = actualShippingFee; }
    public ShipmentStatus getStatus() { return status; }
    public void setStatus(ShipmentStatus status) { this.status = status; }
    public LocalDateTime getEstimatedDelivery() { return estimatedDelivery; }
    public void setEstimatedDelivery(LocalDateTime estimatedDelivery) { this.estimatedDelivery = estimatedDelivery; }

    public void updateTracking(String code) {
        this.trackingCode = code;
    }

    public void changeStatus(ShipmentStatus newStatus) {
        this.status = newStatus;
    }
}
```

# main\java\com\fashionshop\business\entity\ShippingAddress.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\business\entity\ShippingAddress.java` |
| Size | 3,517 bytes |
| Extension | `.java` |
| Modified | 2026-10-07 23:34:34.335000 |

```java
package com.fashionshop.business.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "\"ShippingAddress\"")
public class ShippingAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"userId\"", nullable = false)
    private User user;

    @Column(name = "\"receiverName\"", nullable = false, length = 255)
    private String receiverName;

    @Column(name = "\"phoneNumber\"", nullable = false, length = 50)
    private String phoneNumber;

    @Column(nullable = false, length = 150)
    private String province;

    @Column(nullable = false, length = 150)
    private String district;

    @Column(nullable = false, length = 150)
    private String ward;

    @Column(name = "\"detailAddress\"", nullable = false, length = 500)
    private String detailAddress;

    @Column(name = "\"isDefault\"", nullable = false)
    private Boolean isDefault = false;

    @Column(name = "\"createdAt\"", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "\"updatedAt\"", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    // Business Methods theo Diagram
    public String getFullAddress() {
        return String.format("%s, %s, %s, %s",
                detailAddress != null ? detailAddress : "",
                ward != null ? ward : "",
                district != null ? district : "",
                province != null ? province : "");
    }

    public void updateAddress(String receiverName, String phone, String province, String district, String ward, String detail) {
        this.receiverName = receiverName;
        this.phoneNumber = phone;
        this.province = province;
        this.district = district;
        this.ward = ward;
        this.detailAddress = detail;
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getReceiverName() { return receiverName; }
    public void setReceiverName(String receiverName) { this.receiverName = receiverName; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getProvince() { return province; }
    public void setProvince(String province) { this.province = province; }
    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }
    public String getWard() { return ward; }
    public void setWard(String ward) { this.ward = ward; }
    public String getDetailAddress() { return detailAddress; }
    public void setDetailAddress(String detailAddress) { this.detailAddress = detailAddress; }
    public Boolean getIsDefault() { return isDefault; }
    public void setIsDefault(Boolean isDefault) { this.isDefault = isDefault; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
```

# main\java\com\fashionshop\business\entity\Tag.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\business\entity\Tag.java` |
| Size | 688 bytes |
| Extension | `.java` |
| Modified | 2026-10-07 23:01:23.287000 |

```java
package com.fashionshop.business.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "\"Tag\"")
public class Tag extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public void renameTag(String newName) {
        if (newName != null && !newName.isBlank()) {
            this.name = newName;
        }
    }
}
```

# main\java\com\fashionshop\business\entity\User.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\business\entity\User.java` |
| Size | 3,192 bytes |
| Extension | `.java` |
| Modified | 2026-10-07 23:34:21.541000 |

```java
package com.fashionshop.business.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "\"User\"")
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "\"fullName\"", nullable = false, length = 255)
    private String fullName;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "\"phoneNumber\"", nullable = false, length = 50)
    private String phoneNumber;

    @Column(name = "\"passwordHash\"", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "\"avatarUrl\"", length = 1000)
    private String avatarUrl;

    @Column(name = "\"rewardPoints\"", nullable = false)
    private Integer rewardPoints = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status = UserStatus.ACTIVE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"roleId\"", nullable = false)
    private Role role;

    public enum UserStatus { ACTIVE, INACTIVE, BLOCKED }

    // Business Methods theo Diagram
    public void changePassword(String newHash) {
        this.passwordHash = newHash;
    }

    public void addRewardPoints(int points) {
        if (points > 0) {
            this.rewardPoints += points;
        }
    }

    public boolean deductRewardPoints(int points) {
        if (points > 0 && this.rewardPoints >= points) {
            this.rewardPoints -= points;
            return true;
        }
        return false;
    }

    public void updateStatus(UserStatus newStatus) {
        this.status = newStatus;
    }

    public void updateProfile(String fullName, String phoneNumber, String avatarUrl) {
        if (fullName != null) this.fullName = fullName;
        if (phoneNumber != null) this.phoneNumber = phoneNumber;
        if (avatarUrl != null) this.avatarUrl = avatarUrl;
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    public Integer getRewardPoints() { return rewardPoints; }
    public void setRewardPoints(Integer rewardPoints) { this.rewardPoints = rewardPoints; }
    public UserStatus getStatus() { return status; }
    public void setStatus(UserStatus status) { this.status = status; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
}
```

# main\java\com\fashionshop\business\entity\Voucher.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\business\entity\Voucher.java` |
| Size | 4,944 bytes |
| Extension | `.java` |
| Modified | 2026-10-07 23:07:54.727000 |

```java
package com.fashionshop.business.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "\"Voucher\"")
public class Voucher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 100)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(name = "\"discountType\"", nullable = false)
    private DiscountType discountType;

    @Column(name = "\"discountValue\"", nullable = false, precision = 12, scale = 2)
    private BigDecimal discountValue;

    @Column(name = "\"minOrderValue\"", nullable = false, precision = 12, scale = 2)
    private BigDecimal minOrderValue = BigDecimal.ZERO;

    @Column(name = "\"maxDiscountAmt\"", precision = 12, scale = 2)
    private BigDecimal maxDiscountAmt;

    @Column(name = "\"totalUsageLimit\"", nullable = false)
    private Integer totalUsageLimit = 0;

    @Column(name = "\"usedCount\"", nullable = false)
    private Integer usedCount = 0;

    @Column(name = "\"userUsageLimit\"", nullable = false)
    private Integer userUsageLimit = 1;

    @Column(name = "\"startDate\"", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "\"endDate\"", nullable = false)
    private LocalDateTime endDate;

    @Column(name = "\"isActive\"", nullable = false)
    private Boolean isActive = true;

    @Column(name = "\"deletedAt\"")
    private LocalDateTime deletedAt;

    @Column(name = "\"createdAt\"", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "\"updatedAt\"", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    public enum DiscountType { PERCENTAGE, FIXED_AMOUNT }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public DiscountType getDiscountType() { return discountType; }
    public void setDiscountType(DiscountType discountType) { this.discountType = discountType; }
    public BigDecimal getDiscountValue() { return discountValue; }
    public void setDiscountValue(BigDecimal discountValue) { this.discountValue = discountValue; }
    public BigDecimal getMinOrderValue() { return minOrderValue; }
    public void setMinOrderValue(BigDecimal minOrderValue) { this.minOrderValue = minOrderValue; }
    public BigDecimal getMaxDiscountAmt() { return maxDiscountAmt; }
    public void setMaxDiscountAmt(BigDecimal maxDiscountAmt) { this.maxDiscountAmt = maxDiscountAmt; }
    public Integer getTotalUsageLimit() { return totalUsageLimit; }
    public void setTotalUsageLimit(Integer totalUsageLimit) { this.totalUsageLimit = totalUsageLimit; }
    public Integer getUsedCount() { return usedCount; }
    public void setUsedCount(Integer usedCount) { this.usedCount = usedCount; }
    public Integer getUserUsageLimit() { return userUsageLimit; }
    public void setUserUsageLimit(Integer userUsageLimit) { this.userUsageLimit = userUsageLimit; }
    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }
    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public boolean isExpired() {
        return this.endDate != null && LocalDateTime.now().isAfter(this.endDate);
    }

    public boolean isValid(BigDecimal orderAmt) {
        if (Boolean.FALSE.equals(this.isActive) || isExpired()) {
            return false;
        }
        if (this.totalUsageLimit != null && this.totalUsageLimit > 0 && this.usedCount >= this.totalUsageLimit) {
            return false;
        }
        return orderAmt != null && orderAmt.compareTo(this.minOrderValue) >= 0;
    }

    public BigDecimal calculateDiscount(BigDecimal amt) {
        if (!isValid(amt)) {
            return BigDecimal.ZERO;
        }
        BigDecimal discount = BigDecimal.ZERO;
        if (this.discountType == DiscountType.PERCENTAGE) {
            discount = amt.multiply(this.discountValue).divide(BigDecimal.valueOf(100));
            if (this.maxDiscountAmt != null && discount.compareTo(this.maxDiscountAmt) > 0) {
                discount = this.maxDiscountAmt;
            }
        } else if (this.discountType == DiscountType.FIXED_AMOUNT) {
            discount = this.discountValue.min(amt);
        }
        return discount;
    }

    public void incrementUsage() {
        this.usedCount = (this.usedCount == null ? 0 : this.usedCount) + 1;
    }
}
```

# main\java\com\fashionshop\business\service\cartorder\CartService.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\business\service\cartorder\CartService.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.940379 |

```java

```

# main\java\com\fashionshop\business\service\cartorder\OrderService.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\business\service\cartorder\OrderService.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.942378 |

```java

```

# main\java\com\fashionshop\business\service\catalog\BrandService.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\business\service\catalog\BrandService.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.932379 |

```java

```

# main\java\com\fashionshop\business\service\catalog\CategoryService.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\business\service\catalog\CategoryService.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.931377 |

```java

```

# main\java\com\fashionshop\business\service\catalog\ProductService.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\business\service\catalog\ProductService.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.934378 |

```java

```

# main\java\com\fashionshop\business\service\catalog\TagService.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\business\service\catalog\TagService.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.938377 |

```java

```

# main\java\com\fashionshop\business\service\catalog\VariantService.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\business\service\catalog\VariantService.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.936377 |

```java

```

# main\java\com\fashionshop\business\service\fulfillment\PaymentService.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\business\service\fulfillment\PaymentService.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.945377 |

```java

```

# main\java\com\fashionshop\business\service\fulfillment\ReturnService.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\business\service\fulfillment\ReturnService.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.948381 |

```java

```

# main\java\com\fashionshop\business\service\fulfillment\ShipmentService.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\business\service\fulfillment\ShipmentService.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.947378 |

```java

```

# main\java\com\fashionshop\business\service\identity\AddressService.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\business\service\identity\AddressService.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.926378 |

```java

```

# main\java\com\fashionshop\business\service\identity\AuthService.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\business\service\identity\AuthService.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.923379 |

```java

```

# main\java\com\fashionshop\business\service\identity\RolePermissionService.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\business\service\identity\RolePermissionService.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.928378 |

```java

```

# main\java\com\fashionshop\business\service\identity\UserService.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\business\service\identity\UserService.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.925377 |

```java

```

# main\java\com\fashionshop\business\service\inventory\FlashSaleService.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\business\service\inventory\FlashSaleService.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.944378 |

```java

```

# main\java\com\fashionshop\business\service\loyalty\ChatService.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\business\service\loyalty\ChatService.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.955379 |

```java

```

# main\java\com\fashionshop\business\service\loyalty\PointService.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\business\service\loyalty\PointService.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.952378 |

```java

```

# main\java\com\fashionshop\business\service\loyalty\ReviewService.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\business\service\loyalty\ReviewService.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.953379 |

```java

```

# main\java\com\fashionshop\business\service\loyalty\VoucherReconciliationWorker.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\business\service\loyalty\VoucherReconciliationWorker.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.956379 |

```java

```

# main\java\com\fashionshop\business\service\loyalty\VoucherService.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\business\service\loyalty\VoucherService.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.950379 |

```java

```

# main\java\com\fashionshop\common\exception\AppException.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\common\exception\AppException.java` |
| Size | 328 bytes |
| Extension | `.java` |
| Modified | 2026-10-07 12:10:32.164000 |

```java
package com.fashionshop.common.exception;


public class AppException extends RuntimeException {
    private final int statusCode;

    public AppException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() { return statusCode; }
}

```

# main\java\com\fashionshop\common\exception\BadRequestException.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\common\exception\BadRequestException.java` |
| Size | 303 bytes |
| Extension | `.java` |
| Modified | 2026-10-07 12:30:31.066000 |

```java
package com.fashionshop.common.exception;

public class BadRequestException extends AppException {
    public BadRequestException(String message) {
        super(message, 400);
    }

    public BadRequestException() {
        super("Dữ liệu đầu vào không hợp lệ", 400);
    }
}

```

# main\java\com\fashionshop\common\exception\ConflictException.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\common\exception\ConflictException.java` |
| Size | 310 bytes |
| Extension | `.java` |
| Modified | 2026-10-07 12:31:35.548000 |

```java
package com.fashionshop.common.exception;

public class ConflictException extends AppException {
    public ConflictException(String message) {
        super(message, 409);
    }

    public ConflictException() {
        super("Dữ liệu đã tồn tại hoặc xảy ra xung đột", 409);
    }
}
```

# main\java\com\fashionshop\common\exception\ForbiddenException.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\common\exception\ForbiddenException.java` |
| Size | 314 bytes |
| Extension | `.java` |
| Modified | 2026-10-07 12:31:03.214000 |

```java
package com.fashionshop.common.exception;

public class ForbiddenException extends AppException {
    public ForbiddenException(String message) {
        super(message, 403);
    }

    public ForbiddenException() {
        super("Bạn không có quyền thực hiện hành động này", 403);
    }
}
```

# main\java\com\fashionshop\common\exception\NotFoundException.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\common\exception\NotFoundException.java` |
| Size | 303 bytes |
| Extension | `.java` |
| Modified | 2026-10-07 12:31:16.544000 |

```java
package com.fashionshop.common.exception;

public class NotFoundException extends AppException {
    public NotFoundException(String message) {
        super(message, 404);
    }

    public NotFoundException() {
        super("Bản ghi không tồn tại trong hệ thống", 404);
    }
}

```

# main\java\com\fashionshop\common\exception\UnauthorizedException.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\common\exception\UnauthorizedException.java` |
| Size | 334 bytes |
| Extension | `.java` |
| Modified | 2026-10-07 12:30:46.277000 |

```java
package com.fashionshop.common.exception;

public class UnauthorizedException extends AppException {
    public UnauthorizedException(String message) {
        super(message, 401);
    }

    public UnauthorizedException() {
        super("Chưa xác thực danh tính hoặc phiên đăng nhập hết hạn", 401);
    }
}
```

# main\java\com\fashionshop\common\response\ApiResponse.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\common\response\ApiResponse.java` |
| Size | 1,170 bytes |
| Extension | `.java` |
| Modified | 2026-10-07 12:08:13.362000 |

```java
package com.fashionshop.common.response;


public class ApiResponse<T> {
    private boolean success;
    private int statusCode;
    private String message;
    private T data;

    public static <T> ApiResponse<T> ok(T data) {
        ApiResponse<T> res = new ApiResponse<>();
        res.success = true;
        res.statusCode = 200;
        res.data = data;
        return res;
    }

    public static <T> ApiResponse<T> created(T data, String message) {
        ApiResponse<T> res = new ApiResponse<>();
        res.success = true;
        res.statusCode = 201;
        res.message = message;
        res.data = data;
        return res;
    }

    public static <T> ApiResponse<T> error(int statusCode, String message) {
        ApiResponse<T> res = new ApiResponse<>();
        res.success = false;
        res.statusCode = statusCode;
        res.message = message;
        return res;
    }

    // Getters and Setters...
    public boolean isSuccess() { return success; }
    public int getStatusCode() { return statusCode; }
    public String getMessage() { return message; }
    public T getData() { return data; }
}

```

# main\java\com\fashionshop\common\response\PagedResponse.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\common\response\PagedResponse.java` |
| Size | 77 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:05:40.345000 |

```java
package com.fashionshop.common.response;

public class PagedResponse {
}

```

# main\java\com\fashionshop\common\util\JPAUtil.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\common\util\JPAUtil.java` |
| Size | 67 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:04:30.936000 |

```java
package com.fashionshop.common.util;

public class JPAUtil {
}

```

# main\java\com\fashionshop\common\util\JsonUtil.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\common\util\JsonUtil.java` |
| Size | 68 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:05:12.861000 |

```java
package com.fashionshop.common.util;

public class JsonUtil {
}

```

# main\java\com\fashionshop\common\util\JwtUtil.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\common\util\JwtUtil.java` |
| Size | 67 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:05:06.001000 |

```java
package com.fashionshop.common.util;

public class JwtUtil {
}

```

# main\java\com\fashionshop\common\util\PasswordUtil.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\common\util\PasswordUtil.java` |
| Size | 72 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:04:58.543000 |

```java
package com.fashionshop.common.util;

public class PasswordUtil {
}

```

# main\java\com\fashionshop\common\util\RedisUtil.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\common\util\RedisUtil.java` |
| Size | 69 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:04:45.975000 |

```java
package com.fashionshop.common.util;

public class RedisUtil {
}

```

# main\java\com\fashionshop\config\AppConfig.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\config\AppConfig.java` |
| Size | 1,710 bytes |
| Extension | `.java` |
| Modified | 2026-10-08 16:32:56.146000 |

```java
package com.fashionshop.config;

import io.github.cdimascio.dotenv.Dotenv;

public class AppConfig {
    private static final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

    // Database Configurations
    public static final String DB_URL = get("DB_URL", "jdbc:postgresql://localhost:5432/fashion_ecom_web");
    public static final String DB_USER = get("DB_USER", "postgres");
    public static final String DB_PASSWORD = get("DB_PASSWORD", "123");

    // Client Frontend (CORS)
    public static final String CLIENT_CORS_URL = get("CLIENT_CORS_URL", "http://localhost:3000");

    // JWT Configurations
    public static final String ACCESS_TOKEN_SECRET = get("ACCESS_TOKEN_SECRET", "default_secret_key_minimum_256_bits_length");
    public static final long ACCESS_TOKEN_EXPIRATION = Long.parseLong(get("ACCESS_TOKEN_EXPIRATION", "3600000"));
    public static final String REFRESH_TOKEN_SECRET = get("REFRESH_TOKEN_SECRET", "default_refresh_secret_key_minimum_256_bits");
    public static final long REFRESH_TOKEN_EXPIRATION = Long.parseLong(get("REFRESH_TOKEN_EXPIRATION", "86400000"));

    // Redis & Payment Webhooks
    public static final String REDIS_URL = get("REDIS_URL", "redis://localhost:6379");
    public static final String SEPAY_WEBHOOK_SECRET = get("SEPAY_WEBHOOK_SECRET", "");
    public static final String SEPAY_ACCOUNT_NUMBER = get("SEPAY_ACCOUNT_NUMBER", "");

    public static String get(String key, String defaultValue) {
        String val = System.getenv(key);
        if (val == null || val.isBlank()) {
            val = dotenv.get(key);
        }
        return (val != null && !val.isBlank()) ? val : defaultValue;
    }
}
```

# main\java\com\fashionshop\config\AppContextListener.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\config\AppContextListener.java` |
| Size | 921 bytes |
| Extension | `.java` |
| Modified | 2026-10-07 13:55:16.200000 |

```java
package com.fashionshop.config;
import com.fashionshop.util.JPAUtil;
import com.fashionshop.util.RedisUtil;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class AppContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println(">>> [SYSTEM] Server starting: Initializing JPA & Redis...");
        // Khởi động trước kết nối để tránh lag ở request đầu tiên
        JPAUtil.getEntityManager().close();
        RedisUtil.getClient();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println(">>> [SYSTEM] Server stopping: Closing JPA & Redis connections...");
        JPAUtil.close();
        RedisUtil.shutdown();
    }
}
```

# main\java\com\fashionshop\dataaccess\base\AbstractJpaDAO.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\dataaccess\base\AbstractJpaDAO.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.959377 |

```java

```

# main\java\com\fashionshop\dataaccess\base\GenericDAO.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\dataaccess\base\GenericDAO.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.957379 |

```java

```

# main\java\com\fashionshop\dataaccess\dao\cartorder\CartDAO.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\dataaccess\dao\cartorder\CartDAO.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.975377 |

```java

```

# main\java\com\fashionshop\dataaccess\dao\cartorder\CartItemDAO.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\dataaccess\dao\cartorder\CartItemDAO.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.976376 |

```java

```

# main\java\com\fashionshop\dataaccess\dao\cartorder\OrderDAO.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\dataaccess\dao\cartorder\OrderDAO.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.978376 |

```java

```

# main\java\com\fashionshop\dataaccess\dao\cartorder\OrderItemDAO.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\dataaccess\dao\cartorder\OrderItemDAO.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.979376 |

```java

```

# main\java\com\fashionshop\dataaccess\dao\catalog\BrandDAO.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\dataaccess\dao\catalog\BrandDAO.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.969375 |

```java

```

# main\java\com\fashionshop\dataaccess\dao\catalog\CategoryDAO.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\dataaccess\dao\catalog\CategoryDAO.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.967376 |

```java

```

# main\java\com\fashionshop\dataaccess\dao\catalog\ProductDAO.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\dataaccess\dao\catalog\ProductDAO.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.971376 |

```java

```

# main\java\com\fashionshop\dataaccess\dao\catalog\ProductImageDAO.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\dataaccess\dao\catalog\ProductImageDAO.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.974376 |

```java

```

# main\java\com\fashionshop\dataaccess\dao\catalog\ProductVariantDAO.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\dataaccess\dao\catalog\ProductVariantDAO.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.973390 |

```java

```

# main\java\com\fashionshop\dataaccess\dao\catalog\TagDAO.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\dataaccess\dao\catalog\TagDAO.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.970379 |

```java

```

# main\java\com\fashionshop\dataaccess\dao\fulfillment\PaymentTransactionDAO.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\dataaccess\dao\fulfillment\PaymentTransactionDAO.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.983376 |

```java

```

# main\java\com\fashionshop\dataaccess\dao\fulfillment\ReturnMediaDAO.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\dataaccess\dao\fulfillment\ReturnMediaDAO.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.987377 |

```java

```

# main\java\com\fashionshop\dataaccess\dao\fulfillment\ReturnRequestDAO.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\dataaccess\dao\fulfillment\ReturnRequestDAO.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.985377 |

```java

```

# main\java\com\fashionshop\dataaccess\dao\fulfillment\ShipmentDAO.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\dataaccess\dao\fulfillment\ShipmentDAO.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.984376 |

```java

```

# main\java\com\fashionshop\dataaccess\dao\identity\DeviceSessionDAO.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\dataaccess\dao\identity\DeviceSessionDAO.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.966376 |

```java

```

# main\java\com\fashionshop\dataaccess\dao\identity\PermissionDAO.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\dataaccess\dao\identity\PermissionDAO.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.963379 |

```java

```

# main\java\com\fashionshop\dataaccess\dao\identity\RoleDAO.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\dataaccess\dao\identity\RoleDAO.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.961376 |

```java

```

# main\java\com\fashionshop\dataaccess\dao\identity\ShippingAddressDAO.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\dataaccess\dao\identity\ShippingAddressDAO.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.964379 |

```java

```

# main\java\com\fashionshop\dataaccess\dao\identity\UserDAO.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\dataaccess\dao\identity\UserDAO.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.960379 |

```java

```

# main\java\com\fashionshop\dataaccess\dao\inventory\FlashSaleCampaignDAO.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\dataaccess\dao\inventory\FlashSaleCampaignDAO.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.980376 |

```java

```

# main\java\com\fashionshop\dataaccess\dao\inventory\FlashSaleItemDAO.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\dataaccess\dao\inventory\FlashSaleItemDAO.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.981376 |

```java

```

# main\java\com\fashionshop\dataaccess\dao\loyalty\ChatDAO.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\dataaccess\dao\loyalty\ChatDAO.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.993376 |

```java

```

# main\java\com\fashionshop\dataaccess\dao\loyalty\MessageDAO.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\dataaccess\dao\loyalty\MessageDAO.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.994376 |

```java

```

# main\java\com\fashionshop\dataaccess\dao\loyalty\PointTransactionDAO.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\dataaccess\dao\loyalty\PointTransactionDAO.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.989377 |

```java

```

# main\java\com\fashionshop\dataaccess\dao\loyalty\ProductReviewDAO.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\dataaccess\dao\loyalty\ProductReviewDAO.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.990376 |

```java

```

# main\java\com\fashionshop\dataaccess\dao\loyalty\ReviewMediaDAO.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\dataaccess\dao\loyalty\ReviewMediaDAO.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.992378 |

```java

```

# main\java\com\fashionshop\dataaccess\dao\loyalty\VoucherDAO.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\dataaccess\dao\loyalty\VoucherDAO.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.988378 |

```java

```

# main\java\com\fashionshop\filter\GlobalExceptionFilter.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\filter\GlobalExceptionFilter.java` |
| Size | 2,406 bytes |
| Extension | `.java` |
| Modified | 2026-10-08 16:34:37.202000 |

```java
package com.fashionshop.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fashionshop.common.exception.AppException;
import com.fashionshop.common.response.ApiResponse;
import com.fashionshop.config.AppConfig;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter("/*")
public class GlobalExceptionFilter implements Filter {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        // CORS headers đọc từ AppConfig (.env)
        res.setHeader("Access-Control-Allow-Origin", AppConfig.CLIENT_CORS_URL);
        res.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, PATCH, DELETE, OPTIONS");
        res.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, x-session-id");
        res.setHeader("Access-Control-Allow-Credentials", "true");
        res.setHeader("Access-Control-Max-Age", "3600");

        // Chặn request Preflight OPTIONS
        if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
            res.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        try {
            chain.doFilter(request, response);
        } catch (Exception ex) {
            handleException(ex, res);
        }
    }

    private void handleException(Throwable ex, HttpServletResponse res) throws IOException {
        Throwable root = ex;
        while (root.getCause() != null && !(root instanceof AppException)) {
            root = root.getCause();
        }

        int status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        String message = root.getMessage() != null ? root.getMessage() : "Internal Server Error";

        if (root instanceof AppException appEx) {
            status = appEx.getStatusCode();
        }

        res.setStatus(status);
        res.setContentType("application/json;charset=UTF-8");
        mapper.writeValue(res.getOutputStream(), ApiResponse.error(status, message));
    }
}
```

# main\java\com\fashionshop\presentation\controller\cartorder\CartServlet.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\presentation\controller\cartorder\CartServlet.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.907377 |

```java

```

# main\java\com\fashionshop\presentation\controller\cartorder\OrderServlet.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\presentation\controller\cartorder\OrderServlet.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.908377 |

```java

```

# main\java\com\fashionshop\presentation\controller\catalog\BrandServlet.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\presentation\controller\catalog\BrandServlet.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.901377 |

```java

```

# main\java\com\fashionshop\presentation\controller\catalog\CategoryServlet.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\presentation\controller\catalog\CategoryServlet.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.899378 |

```java

```

# main\java\com\fashionshop\presentation\controller\catalog\ProductServlet.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\presentation\controller\catalog\ProductServlet.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.902379 |

```java

```

# main\java\com\fashionshop\presentation\controller\catalog\TagServlet.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\presentation\controller\catalog\TagServlet.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.905380 |

```java

```

# main\java\com\fashionshop\presentation\controller\catalog\VariantServlet.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\presentation\controller\catalog\VariantServlet.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.904377 |

```java

```

# main\java\com\fashionshop\presentation\controller\fulfillment\PaymentServlet.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\presentation\controller\fulfillment\PaymentServlet.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.911380 |

```java

```

# main\java\com\fashionshop\presentation\controller\fulfillment\ReturnServlet.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\presentation\controller\fulfillment\ReturnServlet.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.916379 |

```java

```

# main\java\com\fashionshop\presentation\controller\fulfillment\SepayWebhookServlet.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\presentation\controller\fulfillment\SepayWebhookServlet.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.913380 |

```java

```

# main\java\com\fashionshop\presentation\controller\fulfillment\ShipmentServlet.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\presentation\controller\fulfillment\ShipmentServlet.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.914381 |

```java

```

# main\java\com\fashionshop\presentation\controller\identity\AddressServlet.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\presentation\controller\identity\AddressServlet.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.895379 |

```java

```

# main\java\com\fashionshop\presentation\controller\identity\AuthServlet.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\presentation\controller\identity\AuthServlet.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.892378 |

```java

```

# main\java\com\fashionshop\presentation\controller\identity\PermissionServlet.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\presentation\controller\identity\PermissionServlet.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.898378 |

```java

```

# main\java\com\fashionshop\presentation\controller\identity\RoleServlet.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\presentation\controller\identity\RoleServlet.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.897379 |

```java

```

# main\java\com\fashionshop\presentation\controller\identity\UserServlet.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\presentation\controller\identity\UserServlet.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.894380 |

```java

```

# main\java\com\fashionshop\presentation\controller\inventory\FlashSaleServlet.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\presentation\controller\inventory\FlashSaleServlet.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.910378 |

```java

```

# main\java\com\fashionshop\presentation\controller\loyalty\ChatServlet.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\presentation\controller\loyalty\ChatServlet.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.922377 |

```java

```

# main\java\com\fashionshop\presentation\controller\loyalty\PointServlet.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\presentation\controller\loyalty\PointServlet.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.919378 |

```java

```

# main\java\com\fashionshop\presentation\controller\loyalty\ReviewServlet.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\presentation\controller\loyalty\ReviewServlet.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.920377 |

```java

```

# main\java\com\fashionshop\presentation\controller\loyalty\VoucherServlet.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\presentation\controller\loyalty\VoucherServlet.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.917378 |

```java

```

# main\java\com\fashionshop\presentation\dto\cartorder\AddCartItemDTO.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\presentation\dto\cartorder\AddCartItemDTO.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.824377 |

```java

```

# main\java\com\fashionshop\presentation\dto\cartorder\CartOwnerDTO.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\presentation\dto\cartorder\CartOwnerDTO.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.826379 |

```java

```

# main\java\com\fashionshop\presentation\dto\cartorder\CreateOrderDTO.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\presentation\dto\cartorder\CreateOrderDTO.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.828377 |

```java

```

# main\java\com\fashionshop\presentation\dto\cartorder\OrderQueryDTO.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\presentation\dto\cartorder\OrderQueryDTO.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.831377 |

```java

```

# main\java\com\fashionshop\presentation\dto\cartorder\ShippingAddressDTO.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\presentation\dto\cartorder\ShippingAddressDTO.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.834384 |

```java

```

# main\java\com\fashionshop\presentation\dto\cartorder\UpdateCartItemDTO.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\presentation\dto\cartorder\UpdateCartItemDTO.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.825377 |

```java

```

# main\java\com\fashionshop\presentation\dto\cartorder\UpdateOrderStatusDTO.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\presentation\dto\cartorder\UpdateOrderStatusDTO.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.829378 |

```java

```

# main\java\com\fashionshop\presentation\dto\catalog\BrandQueryDTO.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\presentation\dto\catalog\BrandQueryDTO.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.808378 |

```java

```

# main\java\com\fashionshop\presentation\dto\catalog\CategoryQueryDTO.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\presentation\dto\catalog\CategoryQueryDTO.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.803380 |

```java

```

# main\java\com\fashionshop\presentation\dto\catalog\CreateBrandDTO.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\presentation\dto\catalog\CreateBrandDTO.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.805379 |

```java

```

# main\java\com\fashionshop\presentation\dto\catalog\CreateCategoryDTO.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\presentation\dto\catalog\CreateCategoryDTO.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.795379 |

```java

```

# main\java\com\fashionshop\presentation\dto\catalog\CreateProductDTO.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\presentation\dto\catalog\CreateProductDTO.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.809379 |

```java

```

# main\java\com\fashionshop\presentation\dto\catalog\CreateTagDTO.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\presentation\dto\catalog\CreateTagDTO.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.819377 |

```java

```

# main\java\com\fashionshop\presentation\dto\catalog\ProductQueryDTO.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\presentation\dto\catalog\ProductQueryDTO.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.812388 |

```java

```

# main\java\com\fashionshop\presentation\dto\catalog\SyncVariantsDTO.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\presentation\dto\catalog\SyncVariantsDTO.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.814384 |

```java

```

# main\java\com\fashionshop\presentation\dto\catalog\TagQueryDTO.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\presentation\dto\catalog\TagQueryDTO.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.822378 |

```java

```

# main\java\com\fashionshop\presentation\dto\catalog\UpdateBrandDTO.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\presentation\dto\catalog\UpdateBrandDTO.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.806380 |

```java

```

# main\java\com\fashionshop\presentation\dto\catalog\UpdateCategoryDTO.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\presentation\dto\catalog\UpdateCategoryDTO.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.799383 |

```java

```

# main\java\com\fashionshop\presentation\dto\catalog\UpdateProductDTO.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\presentation\dto\catalog\UpdateProductDTO.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.810378 |

```java

```

# main\java\com\fashionshop\presentation\dto\catalog\UpdateStockDTO.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\presentation\dto\catalog\UpdateStockDTO.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.818381 |

```java

```

# main\java\com\fashionshop\presentation\dto\catalog\UpdateTagDTO.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\presentation\dto\catalog\UpdateTagDTO.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.820377 |

```java

```

# main\java\com\fashionshop\presentation\dto\catalog\UpsertVariantItemDTO.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\presentation\dto\catalog\UpsertVariantItemDTO.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.816385 |

```java

```

# main\java\com\fashionshop\presentation\dto\fulfillment\CreatePaymentDTO.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\presentation\dto\fulfillment\CreatePaymentDTO.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.841377 |

```java

```

# main\java\com\fashionshop\presentation\dto\fulfillment\CreateReturnDTO.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\presentation\dto\fulfillment\CreateReturnDTO.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.852379 |

```java

```

# main\java\com\fashionshop\presentation\dto\fulfillment\CreateShipmentDTO.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\presentation\dto\fulfillment\CreateShipmentDTO.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.846378 |

```java

```

# main\java\com\fashionshop\presentation\dto\fulfillment\PaymentQueryDTO.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\presentation\dto\fulfillment\PaymentQueryDTO.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.842377 |

```java

```

# main\java\com\fashionshop\presentation\dto\fulfillment\ReturnMediaDTO.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\presentation\dto\fulfillment\ReturnMediaDTO.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.856379 |

```java

```

# main\java\com\fashionshop\presentation\dto\fulfillment\ReturnQueryDTO.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\presentation\dto\fulfillment\ReturnQueryDTO.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.854377 |

```java

```

# main\java\com\fashionshop\presentation\dto\fulfillment\SepayWebhookDTO.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\presentation\dto\fulfillment\SepayWebhookDTO.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.844379 |

```java

```

# main\java\com\fashionshop\presentation\dto\fulfillment\ShipmentQueryDTO.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\presentation\dto\fulfillment\ShipmentQueryDTO.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.850385 |

```java

```

# main\java\com\fashionshop\presentation\dto\fulfillment\UpdateReturnDTO.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\presentation\dto\fulfillment\UpdateReturnDTO.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.853378 |

```java

```

# main\java\com\fashionshop\presentation\dto\fulfillment\UpdateShipmentDTO.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\presentation\dto\fulfillment\UpdateShipmentDTO.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.848380 |

```java

```

# main\java\com\fashionshop\presentation\dto\identity\AddressQueryDTO.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\presentation\dto\identity\AddressQueryDTO.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.787379 |

```java

```

# main\java\com\fashionshop\presentation\dto\identity\ChangePasswordDTO.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\presentation\dto\identity\ChangePasswordDTO.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.775380 |

```java

```

# main\java\com\fashionshop\presentation\dto\identity\CreateAddressDTO.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\presentation\dto\identity\CreateAddressDTO.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.784390 |

```java

```

# main\java\com\fashionshop\presentation\dto\identity\CreatePermissionDTO.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\presentation\dto\identity\CreatePermissionDTO.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.791378 |

```java

```

# main\java\com\fashionshop\presentation\dto\identity\CreateRoleDTO.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\presentation\dto\identity\CreateRoleDTO.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.789380 |

```java

```

# main\java\com\fashionshop\presentation\dto\identity\ForgotPasswordDTO.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\presentation\dto\identity\ForgotPasswordDTO.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.771379 |

```java

```

# main\java\com\fashionshop\presentation\dto\identity\LoginDTO.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\presentation\dto\identity\LoginDTO.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.768381 |

```java

```

# main\java\com\fashionshop\presentation\dto\identity\RefreshTokenDTO.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\presentation\dto\identity\RefreshTokenDTO.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.769380 |

```java

```

# main\java\com\fashionshop\presentation\dto\identity\RegisterDTO.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\presentation\dto\identity\RegisterDTO.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.766443 |

```java

```

# main\java\com\fashionshop\presentation\dto\identity\ResetPasswordDTO.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\presentation\dto\identity\ResetPasswordDTO.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.773380 |

```java

```

# main\java\com\fashionshop\presentation\dto\identity\UpdateAddressDTO.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\presentation\dto\identity\UpdateAddressDTO.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.786378 |

```java

```

# main\java\com\fashionshop\presentation\dto\identity\UpdatePermissionDTO.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\presentation\dto\identity\UpdatePermissionDTO.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.794380 |

```java

```

# main\java\com\fashionshop\presentation\dto\identity\UpdateRoleDTO.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\presentation\dto\identity\UpdateRoleDTO.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.790378 |

```java

```

# main\java\com\fashionshop\presentation\dto\identity\UpdateUserDTO.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\presentation\dto\identity\UpdateUserDTO.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.777378 |

```java

```

# main\java\com\fashionshop\presentation\dto\identity\UpdateUserStatusDTO.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\presentation\dto\identity\UpdateUserStatusDTO.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.779381 |

```java

```

# main\java\com\fashionshop\presentation\dto\identity\UserQueryDTO.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\presentation\dto\identity\UserQueryDTO.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.782385 |

```java

```

# main\java\com\fashionshop\presentation\dto\identity\VerifyOtpDTO.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\presentation\dto\identity\VerifyOtpDTO.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.772378 |

```java

```

# main\java\com\fashionshop\presentation\dto\inventory\CreateFlashSaleDTO.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\presentation\dto\inventory\CreateFlashSaleDTO.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.835376 |

```java

```

# main\java\com\fashionshop\presentation\dto\inventory\FlashSaleItemDTO.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\presentation\dto\inventory\FlashSaleItemDTO.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.838377 |

```java

```

# main\java\com\fashionshop\presentation\dto\inventory\FlashSaleQueryDTO.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\presentation\dto\inventory\FlashSaleQueryDTO.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.839378 |

```java

```

# main\java\com\fashionshop\presentation\dto\inventory\UpdateFlashSaleDTO.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\presentation\dto\inventory\UpdateFlashSaleDTO.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.837377 |

```java

```

# main\java\com\fashionshop\presentation\dto\loyalty\ApplyVoucherDTO.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\presentation\dto\loyalty\ApplyVoucherDTO.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.863379 |

```java

```

# main\java\com\fashionshop\presentation\dto\loyalty\ChatHistoryQueryDTO.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\presentation\dto\loyalty\ChatHistoryQueryDTO.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.888378 |

```java

```

# main\java\com\fashionshop\presentation\dto\loyalty\CreateReviewDTO.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\presentation\dto\loyalty\CreateReviewDTO.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.878378 |

```java

```

# main\java\com\fashionshop\presentation\dto\loyalty\CreateVoucherDTO.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\presentation\dto\loyalty\CreateVoucherDTO.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.858380 |

```java

```

# main\java\com\fashionshop\presentation\dto\loyalty\EarnPointsDTO.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\presentation\dto\loyalty\EarnPointsDTO.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.872377 |

```java

```

# main\java\com\fashionshop\presentation\dto\loyalty\MarkReadDTO.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\presentation\dto\loyalty\MarkReadDTO.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.886380 |

```java

```

# main\java\com\fashionshop\presentation\dto\loyalty\PointQueryDTO.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\presentation\dto\loyalty\PointQueryDTO.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.876380 |

```java

```

# main\java\com\fashionshop\presentation\dto\loyalty\RedeemPointsDTO.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\presentation\dto\loyalty\RedeemPointsDTO.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.874378 |

```java

```

# main\java\com\fashionshop\presentation\dto\loyalty\ReplyReviewDTO.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\presentation\dto\loyalty\ReplyReviewDTO.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.880377 |

```java

```

# main\java\com\fashionshop\presentation\dto\loyalty\ReviewQueryDTO.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\presentation\dto\loyalty\ReviewQueryDTO.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.881377 |

```java

```

# main\java\com\fashionshop\presentation\dto\loyalty\SendMessageDTO.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\presentation\dto\loyalty\SendMessageDTO.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.883384 |

```java

```

# main\java\com\fashionshop\presentation\dto\loyalty\SocketSessionDTO.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\presentation\dto\loyalty\SocketSessionDTO.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.889379 |

```java

```

# main\java\com\fashionshop\presentation\dto\loyalty\UpdateVoucherDTO.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\presentation\dto\loyalty\UpdateVoucherDTO.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.859379 |

```java

```

# main\java\com\fashionshop\presentation\dto\loyalty\ValidateVoucherDTO.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\presentation\dto\loyalty\ValidateVoucherDTO.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.861379 |

```java

```

# main\java\com\fashionshop\presentation\dto\loyalty\VoucherQueryDTO.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\presentation\dto\loyalty\VoucherQueryDTO.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.867393 |

```java

```

# main\java\com\fashionshop\presentation\filter\AuthFilter.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\presentation\filter\AuthFilter.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.754827 |

```java

```

# main\java\com\fashionshop\presentation\filter\CorsFilter.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\presentation\filter\CorsFilter.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.752828 |

```java

```

# main\java\com\fashionshop\presentation\filter\GlobalExceptionFilter.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\presentation\filter\GlobalExceptionFilter.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.757830 |

```java

```

# main\java\com\fashionshop\presentation\filter\RoleFilter.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\presentation\filter\RoleFilter.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.755826 |

```java

```

# main\java\com\fashionshop\presentation\websocket\ChatEndpoint.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\presentation\websocket\ChatEndpoint.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.759828 |

```java

```

# main\java\com\fashionshop\presentation\websocket\WsSessionManager.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\presentation\websocket\WsSessionManager.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.760828 |

```java

```

# main\java\com\fashionshop\TestConnection.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\TestConnection.java` |
| Size | 1,075 bytes |
| Extension | `.java` |
| Modified | 2026-10-07 13:48:49.306000 |

```java
package com.fashionshop;

import com.fashionshop.business.entity.User;
import com.fashionshop.util.JPAUtil;
import jakarta.persistence.EntityManager;
import java.util.List;

public class TestConnection {
    public static void main(String[] args) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            System.out.println("Dang kiem tra ket noi Database qua JPA...");

            // Query thử danh sách User
            List<User> users = em.createQuery("SELECT u FROM User u", User.class)
                    .setMaxResults(5)
                    .getResultList();

            System.out.println("Ket noi thanh cong! So luong User tim thay: " + users.size());
            for (User u : users) {
                System.out.println("- Email: " + u.getEmail() + " | Ten: " + u.getFullName());
            }
        } catch (Exception e) {
            System.err.println("Ket noi that bai:");
            e.printStackTrace();
        } finally {
            em.close();
            JPAUtil.close();
        }
    }
}
```

# main\java\com\fashionshop\util\JPAUtil.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\util\JPAUtil.java` |
| Size | 1,242 bytes |
| Extension | `.java` |
| Modified | 2026-10-08 16:33:18.610000 |

```java
package com.fashionshop.util;

import com.fashionshop.config.AppConfig;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.HashMap;
import java.util.Map;

public class JPAUtil {
    private static final EntityManagerFactory emf;

    static {
        try {
            // Nạp cấu hình Database động từ AppConfig (.env)
            Map<String, Object> props = new HashMap<>();
            props.put("jakarta.persistence.jdbc.url", AppConfig.DB_URL);
            props.put("jakarta.persistence.jdbc.user", AppConfig.DB_USER);
            props.put("jakarta.persistence.jdbc.password", AppConfig.DB_PASSWORD);

            emf = Persistence.createEntityManagerFactory("FashionShopPU", props);
        } catch (Throwable ex) {
            System.err.println("Lỗi khởi tạo EntityManagerFactory: " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public static void close() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}
```

# main\java\com\fashionshop\util\JsonUtil.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\util\JsonUtil.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.748829 |

```java

```

# main\java\com\fashionshop\util\JwtUtil.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\util\JwtUtil.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.744828 |

```java

```

# main\java\com\fashionshop\util\PasswordUtil.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\util\PasswordUtil.java` |
| Size | 500 bytes |
| Extension | `.java` |
| Modified | 2026-10-07 23:41:42.622000 |

```java
package com.fashionshop.util;
import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {
    // Mã hóa mật khẩu khi Register / Đổi mật khẩu
    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));
    }
    // Kiểm tra mật khẩu khi Login
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}
```

# main\java\com\fashionshop\util\RedisUtil.java

| Property | Value |
|----------|-------|
| Path | `main\java\com\fashionshop\util\RedisUtil.java` |
| Size | 786 bytes |
| Extension | `.java` |
| Modified | 2026-10-07 12:35:58.821000 |

```java
package com.fashionshop.util;


import com.fashionshop.config.AppConfig;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

public class RedisUtil {
    private static final RedissonClient redisson;

    static {
        Config config = new Config();
        config.useSingleServer()
                .setAddress(AppConfig.REDIS_URL)
                .setConnectionMinimumIdleSize(5)
                .setConnectionPoolSize(20);
        redisson = Redisson.create(config);
    }

    public static RedissonClient getClient() {
        return redisson;
    }

    public static void shutdown() {
        if (redisson != null && !redisson.isShutdown()) {
            redisson.shutdown();
        }
    }
}

```

# main\resources\META-INF\persistence.xml

| Property | Value |
|----------|-------|
| Path | `main\resources\META-INF\persistence.xml` |
| Size | 3,524 bytes |
| Extension | `.xml` |
| Modified | 2026-10-08 16:34:37.859000 |

```xml
<?xml version="1.0" encoding="UTF-8"?>
<persistence xmlns="https://jakarta.ee/xml/ns/persistence"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="https://jakarta.ee/xml/ns/persistence https://jakarta.ee/xml/ns/persistence/persistence_3_0.xsd"
             version="3.0">

    <persistence-unit name="FashionShopPU" transaction-type="RESOURCE_LOCAL">
        <provider>org.hibernate.jpa.HibernatePersistenceProvider</provider>

        <!-- 1. Identity Module -->
        <class>com.fashionshop.business.entity.User</class>
        <class>com.fashionshop.business.entity.Role</class>
        <class>com.fashionshop.business.entity.Permission</class>
        <class>com.fashionshop.business.entity.ShippingAddress</class>
        <class>com.fashionshop.business.entity.DeviceSession</class>

        <!-- 2. Catalog Module -->
        <class>com.fashionshop.business.entity.Category</class>
        <class>com.fashionshop.business.entity.Brand</class>
        <class>com.fashionshop.business.entity.Tag</class>
        <class>com.fashionshop.business.entity.Product</class>
        <class>com.fashionshop.business.entity.ProductImage</class>
        <class>com.fashionshop.business.entity.ProductVariant</class>

        <!-- 3. Promotion & Flash Sale -->
        <class>com.fashionshop.business.entity.FlashSaleCampaign</class>
        <class>com.fashionshop.business.entity.FlashSaleItem</class>
        <class>com.fashionshop.business.entity.Voucher</class>
        <class>com.fashionshop.business.entity.PointTransaction</class>

        <!-- 4. Cart, Order & Fulfillment -->
        <class>com.fashionshop.business.entity.Cart</class>
        <class>com.fashionshop.business.entity.CartItem</class>
        <class>com.fashionshop.business.entity.Order</class>
        <class>com.fashionshop.business.entity.OrderItem</class>
        <class>com.fashionshop.business.entity.Shipment</class>
        <class>com.fashionshop.business.entity.PaymentTransaction</class>
        <class>com.fashionshop.business.entity.ReturnRequest</class>
        <class>com.fashionshop.business.entity.ReturnMedia</class>

        <!-- 5. Review & Realtime Chat -->
        <class>com.fashionshop.business.entity.ProductReview</class>
        <class>com.fashionshop.business.entity.ReviewMedia</class>
        <class>com.fashionshop.business.entity.Chat</class>
        <class>com.fashionshop.business.entity.Message</class>

        <properties>
            <!-- Driver cố định, thông tin URL, User, Pass được JPAUtil nạp động từ AppConfig -->
            <property name="jakarta.persistence.jdbc.driver" value="org.postgresql.Driver" />

            <property name="hibernate.dialect" value="org.hibernate.dialect.PostgreSQLDialect" />

            <!--            sua  value="...." : để update, create: xóa, none: không làm gì -->
            <property name="hibernate.hbm2ddl.auto" value="update" />
            <!--            -->

            <property name="hibernate.show_sql" value="true" />
            <property name="hibernate.format_sql" value="true" />

            <!-- HikariCP Connection Pool Config -->
            <property name="hibernate.hikari.minimumIdle" value="5" />
            <property name="hibernate.hikari.maximumPoolSize" value="20" />
            <property name="hibernate.hikari.idleTimeout" value="30000" />
        </properties>
    </persistence-unit>
</persistence>
```

# main\webapp\WEB-INF\web.xml

| Property | Value |
|----------|-------|
| Path | `main\webapp\WEB-INF\web.xml` |
| Size | 518 bytes |
| Extension | `.xml` |
| Modified | 2026-10-05 11:09:24.396000 |

```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee
         http://xmlns.jcp.org/xml/ns/javaee/web-app_4_0.xsd"
         version="4.0">

    <display-name>FashionShop API Server</display-name>

    <request-character-encoding>UTF-8</request-character-encoding>
    <response-character-encoding>UTF-8</response-character-encoding>
</web-app>
```

