# Project Dump

**Root:** `C:\Programing\Lap_trinh_Web\fashion_ecom\src\main\java\com\fashionshop\business\entity`

**Generated:** 2026-10-12 14:37:38.538287

**Files:** 30 | **Total size:** 72,036 bytes

---

# BaseEntity.java

| Property | Value |
|----------|-------|
| Path | `BaseEntity.java` |
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

# Brand.java

| Property | Value |
|----------|-------|
| Path | `Brand.java` |
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

# Cart.java

| Property | Value |
|----------|-------|
| Path | `Cart.java` |
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

# CartItem.java

| Property | Value |
|----------|-------|
| Path | `CartItem.java` |
| Size | 2,142 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 11:39:34.640395 |

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

    @Column(name = "\"deletedAt\"")
    private LocalDateTime deletedAt;

    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }

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

# Category.java

| Property | Value |
|----------|-------|
| Path | `Category.java` |
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

# Chat.java

| Property | Value |
|----------|-------|
| Path | `Chat.java` |
| Size | 2,836 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:28.329180 |

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"userId\"", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChatStatus status = ChatStatus.OPEN;

    @Column(name = "\"startedAt\"", nullable = false)
    private LocalDateTime startedAt = LocalDateTime.now();

    @Column(name = "\"endedAt\"")
    private LocalDateTime endedAt;

    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> messages = new ArrayList<>();

    @Column(name = "\"createdAt\"", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "\"updatedAt\"", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();


    
    public void setUpdatedAt(LocalDateTime updatedAt) { 
        this.updatedAt = updatedAt; 
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

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
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public ChatStatus getStatus() { return status; }
    public void setStatus(ChatStatus status) { this.status = status; }
    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }
    public LocalDateTime getEndedAt() { return endedAt; }
    public void setEndedAt(LocalDateTime endedAt) { this.endedAt = endedAt; }
    public List<Message> getMessages() { return messages; }
    public void setMessages(List<Message> messages) { this.messages = messages; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
```

# DeviceSession.java

| Property | Value |
|----------|-------|
| Path | `DeviceSession.java` |
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

# enums\ChatStatus.java

| Property | Value |
|----------|-------|
| Path | `enums\ChatStatus.java` |
| Size | 114 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 11:39:34.665982 |

```java
package com.fashionshop.business.entity.enums;

public enum ChatStatus {
    OPEN,
    WAITING,
    CLOSED
}
```

# enums\UserStatus.java

| Property | Value |
|----------|-------|
| Path | `enums\UserStatus.java` |
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

# FlashSaleCampaign.java

| Property | Value |
|----------|-------|
| Path | `FlashSaleCampaign.java` |
| Size | 2,679 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:28.331152 |

```java
package com.fashionshop.business.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "campaign", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FlashSaleItem> items = new ArrayList<>();

    public List<FlashSaleItem> getItems() { return items; }
    public void setItems(List<FlashSaleItem> items) { this.items = items; }

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
    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

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

# FlashSaleItem.java

| Property | Value |
|----------|-------|
| Path | `FlashSaleItem.java` |
| Size | 2,318 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:28.334154 |

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

    @com.fasterxml.jackson.annotation.JsonIgnore
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

# Message.java

| Property | Value |
|----------|-------|
| Path | `Message.java` |
| Size | 1,702 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:28.336156 |

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

    @com.fasterxml.jackson.annotation.JsonIgnore
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

# Order.java

| Property | Value |
|----------|-------|
| Path | `Order.java` |
| Size | 7,156 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:28.339151 |

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

    @Column(name = "\"orderCode\"", nullable = false, unique = true, length = 100)
    private String orderCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"userId\"", nullable = false)
    private User user;

    @Column(name = "\"shippingAddress\"", nullable = false, length = 500)
    private String shippingAddress;

    @Column(name = "\"subTotal\"", nullable = false, precision = 12, scale = 2)
    private BigDecimal subTotal = BigDecimal.ZERO;

    @Column(name = "\"shippingFee\"", nullable = false, precision = 12, scale = 2)
    private BigDecimal shippingFee = BigDecimal.ZERO;

    @Column(name = "\"shippingMethod\"", nullable = false, length = 50)
    private String shippingMethod = "STANDARD";

    @Column(name = "\"shippingDiscount\"", nullable = false, precision = 12, scale = 2)
    private BigDecimal shippingDiscount = BigDecimal.ZERO;

    @Column(name = "\"voucherCode\"", length = 100)
    private String voucherCode;

    @Column(name = "\"voucherDiscount\"", nullable = false, precision = 12, scale = 2)
    private BigDecimal voucherDiscount = BigDecimal.ZERO;

    @Column(name = "\"pointUsed\"", nullable = false)
    private Integer pointUsed = 0;

    @Column(name = "\"pointDiscount\"", nullable = false, precision = 12, scale = 2)
    private BigDecimal pointDiscount = BigDecimal.ZERO;

    @Column(name = "\"pointEarned\"", nullable = false)
    private Integer pointEarned = 0;

    @Column(name = "\"totalAmount\"", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

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
    private Shipment shipment;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private PaymentTransaction paymentTransaction;

    public enum PaymentMethod { COD, VNPAY, MOMO, BANK_TRANSFER }
    public enum PaymentStatus { PENDING, SUCCESS, FAILED, REFUNDED }
    public enum OrderStatus { PENDING_PAYMENT, CONFIRMED, PROCESSING, SHIPPED, DELIVERED, CANCELLED, RETURNED }

    // Business Methods
    public void markAsPaid() {
        this.paymentStatus = PaymentStatus.SUCCESS;
        this.status = OrderStatus.CONFIRMED;
    }

    public void updateStatus(OrderStatus newStatus) {
        this.status = newStatus;
    }

    public void cancelOrder() {
        if (this.status == OrderStatus.PENDING_PAYMENT || this.status == OrderStatus.CONFIRMED || this.status == OrderStatus.PROCESSING) {
            this.status = OrderStatus.CANCELLED;
        }
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getOrderCode() { return orderCode; }
    public void setOrderCode(String orderCode) { this.orderCode = orderCode; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }
    public BigDecimal getSubTotal() { return subTotal; }
    public void setSubTotal(BigDecimal subTotal) { this.subTotal = subTotal; }
    public BigDecimal getShippingFee() { return shippingFee; }
    public void setShippingFee(BigDecimal shippingFee) { this.shippingFee = shippingFee; }
    public String getShippingMethod() { return shippingMethod; }
    public void setShippingMethod(String shippingMethod) { this.shippingMethod = shippingMethod; }
    public BigDecimal getShippingDiscount() { return shippingDiscount; }
    public void setShippingDiscount(BigDecimal shippingDiscount) { this.shippingDiscount = shippingDiscount; }
    public String getVoucherCode() { return voucherCode; }
    public void setVoucherCode(String voucherCode) { this.voucherCode = voucherCode; }
    public BigDecimal getVoucherDiscount() { return voucherDiscount; }
    public void setVoucherDiscount(BigDecimal voucherDiscount) { this.voucherDiscount = voucherDiscount; }
    public Integer getPointUsed() { return pointUsed; }
    public void setPointUsed(Integer pointUsed) { this.pointUsed = pointUsed; }
    public BigDecimal getPointDiscount() { return pointDiscount; }
    public void setPointDiscount(BigDecimal pointDiscount) { this.pointDiscount = pointDiscount; }
    public Integer getPointEarned() { return pointEarned; }
    public void setPointEarned(Integer pointEarned) { this.pointEarned = pointEarned; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
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
    public Shipment getShipment() { return shipment; }
    public void setShipment(Shipment shipment) { this.shipment = shipment; }
    public PaymentTransaction getPaymentTransaction() { return paymentTransaction; }
    public void setPaymentTransaction(PaymentTransaction paymentTransaction) { this.paymentTransaction = paymentTransaction; }
}
```

# OrderItem.java

| Property | Value |
|----------|-------|
| Path | `OrderItem.java` |
| Size | 3,472 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 11:39:34.652515 |

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

    @Column(name = "\"deletedAt\"")
    private LocalDateTime deletedAt;

    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }


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

# PaymentTransaction.java

| Property | Value |
|----------|-------|
| Path | `PaymentTransaction.java` |
| Size | 2,557 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 11:39:34.654984 |

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

    @Column(name = "\"deletedAt\"")
    private LocalDateTime deletedAt;

    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }

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

# Permission.java

| Property | Value |
|----------|-------|
| Path | `Permission.java` |
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

# PointTransaction.java

| Property | Value |
|----------|-------|
| Path | `PointTransaction.java` |
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

# Product.java

| Property | Value |
|----------|-------|
| Path | `Product.java` |
| Size | 3,710 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 11:39:34.656986 |

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

    // Thêm Getter và Setter cho images
    public List<ProductImage> getImages() {
        return images;
    }

    public void setImages(List<ProductImage> images) {
        this.images = images;
    }

    // Thêm Getter và Setter cho variants (để tránh lỗi ở các module sau)
    public List<ProductVariant> getVariants() {
        return variants;
    }

    public void setVariants(List<ProductVariant> variants) {
        this.variants = variants;
    }
}
```

# ProductImage.java

| Property | Value |
|----------|-------|
| Path | `ProductImage.java` |
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

# ProductReview.java

| Property | Value |
|----------|-------|
| Path | `ProductReview.java` |
| Size | 3,369 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:28.341152 |

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

    @Column(name = "\"deletedAt\"")
    private LocalDateTime deletedAt;

    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }

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

    public Integer getUpdateCount() { 
        return updateCount; 
    }
    
    public void setUpdateCount(Integer updateCount) { 
        this.updateCount = updateCount; 
    }
}
```

# ProductVariant.java

| Property | Value |
|----------|-------|
| Path | `ProductVariant.java` |
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

# ReturnMedia.java

| Property | Value |
|----------|-------|
| Path | `ReturnMedia.java` |
| Size | 1,179 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:28.343151 |

```java
package com.fashionshop.business.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "\"ReturnMedia\"")
public class ReturnMedia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @com.fasterxml.jackson.annotation.JsonIgnore
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

# ReturnRequest.java

| Property | Value |
|----------|-------|
| Path | `ReturnRequest.java` |
| Size | 2,664 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 11:39:34.660985 |

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

    @Column(name = "\"deletedAt\"")
    private LocalDateTime deletedAt;

    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }

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

# ReviewMedia.java

| Property | Value |
|----------|-------|
| Path | `ReviewMedia.java` |
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

# Role.java

| Property | Value |
|----------|-------|
| Path | `Role.java` |
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

# Shipment.java

| Property | Value |
|----------|-------|
| Path | `Shipment.java` |
| Size | 2,744 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 11:39:34.664044 |

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

    @Column(name = "\"deletedAt\"")
    private LocalDateTime deletedAt;

    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }

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

# ShippingAddress.java

| Property | Value |
|----------|-------|
| Path | `ShippingAddress.java` |
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

# Tag.java

| Property | Value |
|----------|-------|
| Path | `Tag.java` |
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

# User.java

| Property | Value |
|----------|-------|
| Path | `User.java` |
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

# Voucher.java

| Property | Value |
|----------|-------|
| Path | `Voucher.java` |
| Size | 5,126 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:28.345155 |

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

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

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

