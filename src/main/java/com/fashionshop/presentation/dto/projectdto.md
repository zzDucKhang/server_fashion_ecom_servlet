# Project Dump

**Root:** `C:\Programing\Lap_trinh_Web\fashion_ecom\src\main\java\com\fashionshop\presentation\dto`

**Generated:** 2026-10-12 14:39:36.045410

**Files:** 70 | **Total size:** 80,759 bytes

---

# cartorder\AddCartItemDTO.java

| Property | Value |
|----------|-------|
| Path | `cartorder\AddCartItemDTO.java` |
| Size | 422 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:28.475154 |

```java
package com.fashionshop.presentation.dto.cartorder;

public class AddCartItemDTO {
    private Integer variantId;
    private Integer quantity;

    public Integer getVariantId() { return variantId; }
    public void setVariantId(Integer variantId) { this.variantId = variantId; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}
```

# cartorder\CartOwnerDTO.java

| Property | Value |
|----------|-------|
| Path | `cartorder\CartOwnerDTO.java` |
| Size | 0 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:02:38.826379 |

```java

```

# cartorder\CreateOrderDTO.java

| Property | Value |
|----------|-------|
| Path | `cartorder\CreateOrderDTO.java` |
| Size | 2,363 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:28.476153 |

```java
package com.fashionshop.presentation.dto.cartorder;

import java.util.List;

public class CreateOrderDTO {
    private List<Integer> cartItemIds;
    private DirectBuyDTO directBuy;
    private Integer shippingAddressId;
    private ShippingAddressDTO shippingAddress;
    private String paymentMethod; 
    private String shippingMethod = "STANDARD";
    private String customerNote;
    private String voucherCode;
    private Integer pointUsed = 0;

    public static class DirectBuyDTO {
        private Integer variantId;
        private Integer quantity;

        public Integer getVariantId() { return variantId; }
        public void setVariantId(Integer variantId) { this.variantId = variantId; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }

    // Getters and Setters
    public List<Integer> getCartItemIds() { return cartItemIds; }
    public void setCartItemIds(List<Integer> cartItemIds) { this.cartItemIds = cartItemIds; }
    public DirectBuyDTO getDirectBuy() { return directBuy; }
    public void setDirectBuy(DirectBuyDTO directBuy) { this.directBuy = directBuy; }
    public Integer getShippingAddressId() { return shippingAddressId; }
    public void setShippingAddressId(Integer shippingAddressId) { this.shippingAddressId = shippingAddressId; }
    public ShippingAddressDTO getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(ShippingAddressDTO shippingAddress) { this.shippingAddress = shippingAddress; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public String getShippingMethod() { return shippingMethod; }
    public void setShippingMethod(String shippingMethod) { this.shippingMethod = shippingMethod; }
    public String getCustomerNote() { return customerNote; }
    public void setCustomerNote(String customerNote) { this.customerNote = customerNote; }
    public String getVoucherCode() { return voucherCode; }
    public void setVoucherCode(String voucherCode) { this.voucherCode = voucherCode; }
    public Integer getPointUsed() { return pointUsed; }
    public void setPointUsed(Integer pointUsed) { this.pointUsed = pointUsed; }
}
```

# cartorder\OrderQueryDTO.java

| Property | Value |
|----------|-------|
| Path | `cartorder\OrderQueryDTO.java` |
| Size | 500 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:28.477154 |

```java
package com.fashionshop.presentation.dto.cartorder;

public class OrderQueryDTO {
    private int page = 1;
    private int limit = 10;
    private String status;

    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }
    public int getLimit() { return limit; }
    public void setLimit(int limit) { this.limit = limit; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}


```

# cartorder\ShippingAddressDTO.java

| Property | Value |
|----------|-------|
| Path | `cartorder\ShippingAddressDTO.java` |
| Size | 1,113 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:28.479185 |

```java
package com.fashionshop.presentation.dto.cartorder;

public class ShippingAddressDTO {
    private String receiverName;
    private String phoneNumber;
    private String province;
    private String district;
    private String ward;
    private String detailAddress;

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
}


```

# cartorder\UpdateCartItemDTO.java

| Property | Value |
|----------|-------|
| Path | `cartorder\UpdateCartItemDTO.java` |
| Size | 255 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:28.481155 |

```java
package com.fashionshop.presentation.dto.cartorder;

public class UpdateCartItemDTO {
    private Integer quantity;

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}
```

# cartorder\UpdateOrderStatusDTO.java

| Property | Value |
|----------|-------|
| Path | `cartorder\UpdateOrderStatusDTO.java` |
| Size | 245 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:28.482154 |

```java
package com.fashionshop.presentation.dto.cartorder;

public class UpdateOrderStatusDTO {
    private String status;

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}


```

# catalog\BrandQueryDTO.java

| Property | Value |
|----------|-------|
| Path | `catalog\BrandQueryDTO.java` |
| Size | 566 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:28.484154 |

```java
package com.fashionshop.presentation.dto.catalog;

public class BrandQueryDTO {
    private int page = 1;
    private int limit = 10;
    private String search;

    public int getPage() { return page > 0 ? page : 1; }
    public void setPage(int page) { this.page = page > 0 ? page : 1; }

    public int getLimit() { return limit > 0 ? limit : 10; }
    public void setLimit(int limit) { this.limit = limit > 0 ? limit : 10; }

    public String getSearch() { return search; }
    public void setSearch(String search) { this.search = search; }
}


```

# catalog\CategoryQueryDTO.java

| Property | Value |
|----------|-------|
| Path | `catalog\CategoryQueryDTO.java` |
| Size | 460 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:28.485152 |

```java
package com.fashionshop.presentation.dto.catalog;

public class CategoryQueryDTO {
    private Integer parentCategoryId;
    private String search;

    public Integer getParentCategoryId() { return parentCategoryId; }
    public void setParentCategoryId(Integer parentCategoryId) { this.parentCategoryId = parentCategoryId; }

    public String getSearch() { return search; }
    public void setSearch(String search) { this.search = search; }
}


```

# catalog\CreateBrandDTO.java

| Property | Value |
|----------|-------|
| Path | `catalog\CreateBrandDTO.java` |
| Size | 400 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:28.487151 |

```java
package com.fashionshop.presentation.dto.catalog;

public class CreateBrandDTO {
    private String defaultName;
    private String logo;

    public String getDefaultName() { return defaultName; }
    public void setDefaultName(String defaultName) { this.defaultName = defaultName; }
    public String getLogo() { return logo; }
    public void setLogo(String logo) { this.logo = logo; }
}
```

# catalog\CreateCategoryDTO.java

| Property | Value |
|----------|-------|
| Path | `catalog\CreateCategoryDTO.java` |
| Size | 923 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:28.490153 |

```java
package com.fashionshop.presentation.dto.catalog;

public class CreateCategoryDTO {
    private String defaultName;
    private String logo;
    private String iconUrl;
    private Integer parentCategoryId;

    public String getDefaultName() { return defaultName; }
    public void setDefaultName(String defaultName) { this.defaultName = defaultName; }

    public String getLogo() { return logo != null ? logo : iconUrl; }
    public void setLogo(String logo) {
        this.logo = logo;
        this.iconUrl = logo;
    }

    public String getIconUrl() { return iconUrl != null ? iconUrl : logo; }
    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
        this.logo = iconUrl;
    }

    public Integer getParentCategoryId() { return parentCategoryId; }
    public void setParentCategoryId(Integer parentCategoryId) { this.parentCategoryId = parentCategoryId; }
}


```

# catalog\CreateProductDTO.java

| Property | Value |
|----------|-------|
| Path | `catalog\CreateProductDTO.java` |
| Size | 1,418 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 11:39:34.800060 |

```java
package com.fashionshop.presentation.dto.catalog;

import com.fashionshop.business.entity.Product;
import java.util.List;

public class CreateProductDTO {
    private String defaultName;
    private String thumbnailUrl;
    private Product.ProductStatus status = Product.ProductStatus.DRAFT;
    private Integer categoryId;
    private Integer brandId;
    private List<String> images;
    private List<Integer> tagIds;

    public String getDefaultName() { return defaultName; }
    public void setDefaultName(String defaultName) { this.defaultName = defaultName; }
    public String getThumbnailUrl() { return thumbnailUrl; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }
    public Product.ProductStatus getStatus() { return status; }
    public void setStatus(Product.ProductStatus status) { this.status = status; }
    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }
    public Integer getBrandId() { return brandId; }
    public void setBrandId(Integer brandId) { this.brandId = brandId; }
    public List<String> getImages() { return images; }
    public void setImages(List<String> images) { this.images = images; }
    public List<Integer> getTagIds() { return tagIds; }
    public void setTagIds(List<Integer> tagIds) { this.tagIds = tagIds; }
}
```

# catalog\CreateTagDTO.java

| Property | Value |
|----------|-------|
| Path | `catalog\CreateTagDTO.java` |
| Size | 221 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:28.495148 |

```java
package com.fashionshop.presentation.dto.catalog;

public class CreateTagDTO {
    private String name;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}


```

# catalog\ProductQueryDTO.java

| Property | Value |
|----------|-------|
| Path | `catalog\ProductQueryDTO.java` |
| Size | 1,242 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 11:39:34.803061 |

```java
package com.fashionshop.presentation.dto.catalog;

import com.fashionshop.business.entity.Product;

public class ProductQueryDTO {
    private int page = 1;
    private int limit = 10;
    private String search;
    private Integer categoryId;
    private Integer brandId;
    private Product.ProductStatus status;
    private String lang = "vi";

    public int getPage() { return page; }
    public void setPage(int page) { this.page = page > 0 ? page : 1; }
    public int getLimit() { return limit; }
    public void setLimit(int limit) { this.limit = limit > 0 ? limit : 10; }
    public String getSearch() { return search; }
    public void setSearch(String search) { this.search = search; }
    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }
    public Integer getBrandId() { return brandId; }
    public void setBrandId(Integer brandId) { this.brandId = brandId; }
    public Product.ProductStatus getStatus() { return status; }
    public void setStatus(Product.ProductStatus status) { this.status = status; }
    public String getLang() { return lang; }
    public void setLang(String lang) { this.lang = lang; }
}
```

# catalog\SyncVariantsDTO.java

| Property | Value |
|----------|-------|
| Path | `catalog\SyncVariantsDTO.java` |
| Size | 510 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:28.496150 |

```java
package com.fashionshop.presentation.dto.catalog;

import java.util.List;

public class SyncVariantsDTO {
    private Integer productId;
    private List<UpsertVariantItemDTO> variants;

    public Integer getProductId() { return productId; }
    public void setProductId(Integer productId) { this.productId = productId; }

    public List<UpsertVariantItemDTO> getVariants() { return variants; }
    public void setVariants(List<UpsertVariantItemDTO> variants) { this.variants = variants; }
}


```

# catalog\TagQueryDTO.java

| Property | Value |
|----------|-------|
| Path | `catalog\TagQueryDTO.java` |
| Size | 564 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:28.502156 |

```java
package com.fashionshop.presentation.dto.catalog;

public class TagQueryDTO {
    private int page = 1;
    private int limit = 20;
    private String search;

    public int getPage() { return page > 0 ? page : 1; }
    public void setPage(int page) { this.page = page > 0 ? page : 1; }

    public int getLimit() { return limit > 0 ? limit : 20; }
    public void setLimit(int limit) { this.limit = limit > 0 ? limit : 20; }

    public String getSearch() { return search; }
    public void setSearch(String search) { this.search = search; }
}


```

# catalog\UpdateBrandDTO.java

| Property | Value |
|----------|-------|
| Path | `catalog\UpdateBrandDTO.java` |
| Size | 699 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:28.506153 |

```java
package com.fashionshop.presentation.dto.catalog;

public class UpdateBrandDTO {
    private String defaultName;
    private String logo;
    private String logoUrl;

    public String getDefaultName() { return defaultName; }
    public void setDefaultName(String defaultName) { this.defaultName = defaultName; }

    public String getLogo() { return logo != null ? logo : logoUrl; }
    public void setLogo(String logo) {
        this.logo = logo;
        this.logoUrl = logo;
    }

    public String getLogoUrl() { return logoUrl != null ? logoUrl : logo; }
    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
        this.logo = logoUrl;
    }
}


```

# catalog\UpdateCategoryDTO.java

| Property | Value |
|----------|-------|
| Path | `catalog\UpdateCategoryDTO.java` |
| Size | 1,095 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:28.509153 |

```java
package com.fashionshop.presentation.dto.catalog;

public class UpdateCategoryDTO {
    private String defaultName;
    private String logo;
    private String iconUrl;
    private Integer parentCategoryId;
    private Boolean isVisible;

    public String getDefaultName() { return defaultName; }
    public void setDefaultName(String defaultName) { this.defaultName = defaultName; }

    public String getLogo() { return logo != null ? logo : iconUrl; }
    public void setLogo(String logo) {
        this.logo = logo;
        this.iconUrl = logo;
    }

    public String getIconUrl() { return iconUrl != null ? iconUrl : logo; }
    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
        this.logo = iconUrl;
    }

    public Integer getParentCategoryId() { return parentCategoryId; }
    public void setParentCategoryId(Integer parentCategoryId) { this.parentCategoryId = parentCategoryId; }

    public Boolean getIsVisible() { return isVisible; }
    public void setIsVisible(Boolean isVisible) { this.isVisible = isVisible; }
}


```

# catalog\UpdateProductDTO.java

| Property | Value |
|----------|-------|
| Path | `catalog\UpdateProductDTO.java` |
| Size | 1,388 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 11:39:34.807062 |

```java
package com.fashionshop.presentation.dto.catalog;

import com.fashionshop.business.entity.Product;
import java.util.List;

public class UpdateProductDTO {
    private String defaultName;
    private String thumbnailUrl;
    private Product.ProductStatus status;
    private Integer categoryId;
    private Integer brandId;
    private List<String> images;
    private List<Integer> tagIds;

    public String getDefaultName() { return defaultName; }
    public void setDefaultName(String defaultName) { this.defaultName = defaultName; }
    public String getThumbnailUrl() { return thumbnailUrl; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }
    public Product.ProductStatus getStatus() { return status; }
    public void setStatus(Product.ProductStatus status) { this.status = status; }
    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }
    public Integer getBrandId() { return brandId; }
    public void setBrandId(Integer brandId) { this.brandId = brandId; }
    public List<String> getImages() { return images; }
    public void setImages(List<String> images) { this.images = images; }
    public List<Integer> getTagIds() { return tagIds; }
    public void setTagIds(List<Integer> tagIds) { this.tagIds = tagIds; }
}
```

# catalog\UpdateStockDTO.java

| Property | Value |
|----------|-------|
| Path | `catalog\UpdateStockDTO.java` |
| Size | 284 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:28.510154 |

```java
package com.fashionshop.presentation.dto.catalog;

public class UpdateStockDTO {
    private int quantityChange;

    public int getQuantityChange() { return quantityChange; }
    public void setQuantityChange(int quantityChange) { this.quantityChange = quantityChange; }
}


```

# catalog\UpdateTagDTO.java

| Property | Value |
|----------|-------|
| Path | `catalog\UpdateTagDTO.java` |
| Size | 221 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:28.511153 |

```java
package com.fashionshop.presentation.dto.catalog;

public class UpdateTagDTO {
    private String name;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}


```

# catalog\UpsertVariantItemDTO.java

| Property | Value |
|----------|-------|
| Path | `catalog\UpsertVariantItemDTO.java` |
| Size | 1,682 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:28.512153 |

```java
package com.fashionshop.presentation.dto.catalog;

import java.math.BigDecimal;

public class UpsertVariantItemDTO {
    private Integer id;
    private String sku;
    private BigDecimal originalPrice;
    private BigDecimal sellingPrice;
    private Integer stockQuantity = 0;
    private Integer weightGram = 0;
    private String imageUrl;
    private String variant;
    private String variantValue;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public BigDecimal getOriginalPrice() { return originalPrice; }
    public void setOriginalPrice(BigDecimal originalPrice) { this.originalPrice = originalPrice; }

    public BigDecimal getSellingPrice() { return sellingPrice; }
    public void setSellingPrice(BigDecimal sellingPrice) { this.sellingPrice = sellingPrice; }

    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }

    public Integer getWeightGram() { return weightGram; }
    public void setWeightGram(Integer weightGram) { this.weightGram = weightGram; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getVariant() { return variant; }
    public void setVariant(String variant) { this.variant = variant; }

    public String getVariantValue() { return variantValue; }
    public void setVariantValue(String variantValue) { this.variantValue = variantValue; }
}


```

# fulfillment\CreatePaymentDTO.java

| Property | Value |
|----------|-------|
| Path | `fulfillment\CreatePaymentDTO.java` |
| Size | 253 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:28.513152 |

```java
package com.fashionshop.presentation.dto.fulfillment;

public class CreatePaymentDTO {
    private Integer orderId;

    public Integer getOrderId() { return orderId; }
    public void setOrderId(Integer orderId) { this.orderId = orderId; }
}


```

# fulfillment\CreateReturnDTO.java

| Property | Value |
|----------|-------|
| Path | `fulfillment\CreateReturnDTO.java` |
| Size | 1,225 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:28.517153 |

```java
package com.fashionshop.presentation.dto.fulfillment;

<<<<<<< HEAD
import java.util.ArrayList;
=======
>>>>>>> feature/checkout
import java.util.List;

public class CreateReturnDTO {
    private Integer orderItemId;
    private Integer quantity;
    private String reason;
    private String description;
<<<<<<< HEAD
    private List<ReturnMediaDTO> medias = new ArrayList<>();
=======
    private List<ReturnMediaDTO> medias;
>>>>>>> feature/checkout

    public Integer getOrderItemId() { return orderItemId; }
    public void setOrderItemId(Integer orderItemId) { this.orderItemId = orderItemId; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public List<ReturnMediaDTO> getMedias() { return medias; }
    public void setMedias(List<ReturnMediaDTO> medias) { this.medias = medias; }
<<<<<<< HEAD
}
=======
}

>>>>>>> feature/checkout

```

# fulfillment\CreateShipmentDTO.java

| Property | Value |
|----------|-------|
| Path | `fulfillment\CreateShipmentDTO.java` |
| Size | 1,085 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:28.524153 |

```java
package com.fashionshop.presentation.dto.fulfillment;

import java.math.BigDecimal;

public class CreateShipmentDTO {
    private Integer orderId;
    private String carrier;
    private String trackingCode;
    private BigDecimal actualShippingFee;
    private String estimatedDelivery;

    public Integer getOrderId() { return orderId; }
    public void setOrderId(Integer orderId) { this.orderId = orderId; }
    public String getCarrier() { return carrier; }
    public void setCarrier(String carrier) { this.carrier = carrier; }
    public String getTrackingCode() { return trackingCode; }
    public void setTrackingCode(String trackingCode) { this.trackingCode = trackingCode; }
    public BigDecimal getActualShippingFee() { return actualShippingFee; }
    public void setActualShippingFee(BigDecimal actualShippingFee) { this.actualShippingFee = actualShippingFee; }
    public String getEstimatedDelivery() { return estimatedDelivery; }
    public void setEstimatedDelivery(String estimatedDelivery) { this.estimatedDelivery = estimatedDelivery; }
}


```

# fulfillment\PaymentInfoDTO.java

| Property | Value |
|----------|-------|
| Path | `fulfillment\PaymentInfoDTO.java` |
| Size | 1,699 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:28.530155 |

```java
package com.fashionshop.presentation.dto.fulfillment;

import java.math.BigDecimal;

public class PaymentInfoDTO {
    private Integer orderId;
    private String orderCode;
    private BigDecimal amount;
    private String paymentMethod;
    private String paymentStatus;
    private String qrUrl;
    private String bankName;
    private String accountNumber;
    private String transferContent;

    public Integer getOrderId() { return orderId; }
    public void setOrderId(Integer orderId) { this.orderId = orderId; }
    public String getOrderCode() { return orderCode; }
    public void setOrderCode(String orderCode) { this.orderCode = orderCode; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
    public String getQrUrl() { return qrUrl; }
    public void setQrUrl(String qrUrl) { this.qrUrl = qrUrl; }
    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }
    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
    public String getTransferContent() { return transferContent; }
    public void setTransferContent(String transferContent) { this.transferContent = transferContent; }
}

```

# fulfillment\PaymentQueryDTO.java

| Property | Value |
|----------|-------|
| Path | `fulfillment\PaymentQueryDTO.java` |
| Size | 504 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:28.537152 |

```java
package com.fashionshop.presentation.dto.fulfillment;

public class PaymentQueryDTO {
    private int page = 1;
    private int limit = 10;
    private String status;

    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }
    public int getLimit() { return limit; }
    public void setLimit(int limit) { this.limit = limit; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}


```

# fulfillment\PaymentStatusDTO.java

| Property | Value |
|----------|-------|
| Path | `fulfillment\PaymentStatusDTO.java` |
| Size | 627 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:28.542154 |

```java
package com.fashionshop.presentation.dto.fulfillment;

public class PaymentStatusDTO {
    private Integer orderId;
    private String paymentStatus;
    private String orderStatus;

    public Integer getOrderId() { return orderId; }
    public void setOrderId(Integer orderId) { this.orderId = orderId; }
    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
    public String getOrderStatus() { return orderStatus; }
    public void setOrderStatus(String orderStatus) { this.orderStatus = orderStatus; }
}

```

# fulfillment\ReturnMediaDTO.java

| Property | Value |
|----------|-------|
| Path | `fulfillment\ReturnMediaDTO.java` |
| Size | 474 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:28.549154 |

```java
package com.fashionshop.presentation.dto.fulfillment;

public class ReturnMediaDTO {
    private String mediaUrl;
    private String mediaType;

    public String getMediaUrl() { return mediaUrl; }
    public void setMediaUrl(String mediaUrl) { this.mediaUrl = mediaUrl; }
    public String getMediaType() { return mediaType; }
    public void setMediaType(String mediaType) { this.mediaType = mediaType; }
<<<<<<< HEAD
}
=======
}

>>>>>>> feature/checkout

```

# fulfillment\ReturnQueryDTO.java

| Property | Value |
|----------|-------|
| Path | `fulfillment\ReturnQueryDTO.java` |
| Size | 1,035 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:28.551156 |

```java
package com.fashionshop.presentation.dto.fulfillment;

<<<<<<< HEAD
import com.fashionshop.business.entity.ReturnRequest;

public class ReturnQueryDTO {
    private ReturnRequest.ReturnStatus status;
    private Integer orderItemId;

    public ReturnRequest.ReturnStatus getStatus() { return status; }
    public void setStatus(ReturnRequest.ReturnStatus status) { this.status = status; }
    public Integer getOrderItemId() { return orderItemId; }
    public void setOrderItemId(Integer orderItemId) { this.orderItemId = orderItemId; }
}
=======
public class ReturnQueryDTO {
    private int page = 1;
    private int limit = 10;
    private String status;

    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }
    public int getLimit() { return limit; }
    public void setLimit(int limit) { this.limit = limit; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}

>>>>>>> feature/checkout

```

# fulfillment\SepayWebhookDTO.java

| Property | Value |
|----------|-------|
| Path | `fulfillment\SepayWebhookDTO.java` |
| Size | 1,777 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:28.552156 |

```java
package com.fashionshop.presentation.dto.fulfillment;

import java.math.BigDecimal;

public class SepayWebhookDTO {
    private Long id;
    private String gateway;
    private String transactionDate;
    private String accountNumber;
    private String subAccount;
    private String code;
    private String content;
    private String transferType; // "in" | "out"
    private String description;
    private BigDecimal transferAmount;
    private BigDecimal accumulated;
    private String referenceCode;

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getGateway() { return gateway; }
    public void setGateway(String gateway) { this.gateway = gateway; }
    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
    public String getSubAccount() { return subAccount; }
    public void setSubAccount(String subAccount) { this.subAccount = subAccount; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getTransferType() { return transferType; }
    public void setTransferType(String transferType) { this.transferType = transferType; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getTransferAmount() { return transferAmount; }
    public void setTransferAmount(BigDecimal transferAmount) { this.transferAmount = transferAmount; }
}
```

# fulfillment\ShipmentQueryDTO.java

| Property | Value |
|----------|-------|
| Path | `fulfillment\ShipmentQueryDTO.java` |
| Size | 505 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:28.553157 |

```java
package com.fashionshop.presentation.dto.fulfillment;

public class ShipmentQueryDTO {
    private int page = 1;
    private int limit = 10;
    private String status;

    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }
    public int getLimit() { return limit; }
    public void setLimit(int limit) { this.limit = limit; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}


```

# fulfillment\UpdateReturnDTO.java

| Property | Value |
|----------|-------|
| Path | `fulfillment\UpdateReturnDTO.java` |
| Size | 1,114 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:28.555156 |

```java
package com.fashionshop.presentation.dto.fulfillment;

<<<<<<< HEAD
import com.fashionshop.business.entity.ReturnRequest;

public class UpdateReturnDTO {
    private Integer quantity;
    private String reason;
    private String description;
    private ReturnRequest.ReturnStatus status;

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public ReturnRequest.ReturnStatus getStatus() { return status; }
    public void setStatus(ReturnRequest.ReturnStatus status) { this.status = status; }
}
=======
public class UpdateReturnDTO {
    private String status; // APPROVED, REJECTED, COMPLETED

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}

>>>>>>> feature/checkout

```

# fulfillment\UpdateShipmentDTO.java

| Property | Value |
|----------|-------|
| Path | `fulfillment\UpdateShipmentDTO.java` |
| Size | 432 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:28.556155 |

```java
package com.fashionshop.presentation.dto.fulfillment;

public class UpdateShipmentDTO {
    private String status;
    private String trackingCode;

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getTrackingCode() { return trackingCode; }
    public void setTrackingCode(String trackingCode) { this.trackingCode = trackingCode; }
}


```

# identity\AddressQueryDTO.java

| Property | Value |
|----------|-------|
| Path | `identity\AddressQueryDTO.java` |
| Size | 682 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:40.520789 |

```java
package com.fashionshop.presentation.dto.identity;

public class AddressQueryDTO {

    private int page = 1;
    private int limit = 10;

    public AddressQueryDTO() {}

    public AddressQueryDTO(int page, int limit) {
        setPage(page);
        setLimit(limit);
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page > 0 ? page : 1;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        if (limit <= 0) {
            this.limit = 10;
        } else {
            this.limit = Math.min(limit, 100);
        }
    }
}

```

# identity\ChangePasswordDTO.java

| Property | Value |
|----------|-------|
| Path | `identity\ChangePasswordDTO.java` |
| Size | 2,174 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:40.521857 |

```java
package com.fashionshop.presentation.dto.identity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fashionshop.common.util.Validatable;
import com.fashionshop.common.util.ValidationUtil;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ChangePasswordDTO implements Validatable {

    private String oldPassword;
    private String newPassword;
    private String confirmPassword;

    public ChangePasswordDTO() {}

    public ChangePasswordDTO(String oldPassword, String newPassword, String confirmPassword) {
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
        this.confirmPassword = confirmPassword;
    }

    @Override
    public Map<String, List<String>> validate() {
        Map<String, List<String>> errors = new LinkedHashMap<>();

        if (oldPassword == null || oldPassword.isBlank()) {
            ValidationUtil.addError(errors, "oldPassword", "Mật khẩu cũ là bắt buộc");
        }

        ValidationUtil.validatePasswordComplexity(newPassword, "newPassword", errors);

        if (confirmPassword == null || confirmPassword.isBlank()) {
            ValidationUtil.addError(errors, "confirmPassword", "Mật khẩu xác nhận không được để trống");
        } else if (newPassword != null && !newPassword.equals(confirmPassword)) {
            ValidationUtil.addError(errors, "confirmPassword", "Mật khẩu xác nhận không khớp");
        }

        return errors;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}

```

# identity\CreateAddressDTO.java

| Property | Value |
|----------|-------|
| Path | `identity\CreateAddressDTO.java` |
| Size | 4,984 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:40.522786 |

```java
package com.fashionshop.presentation.dto.identity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fashionshop.common.util.Validatable;
import com.fashionshop.common.util.ValidationUtil;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateAddressDTO implements Validatable {

    private String receiverName;
    private String phoneNumber;
    private String province;
    private String district;
    private String ward;
    private String detailAddress;
    private Boolean isDefault = false;

    public CreateAddressDTO() {}

    public CreateAddressDTO(String receiverName, String phoneNumber, String province, String district, String ward, String detailAddress, Boolean isDefault) {
        this.receiverName = receiverName;
        this.phoneNumber = phoneNumber;
        this.province = province;
        this.district = district;
        this.ward = ward;
        this.detailAddress = detailAddress;
        this.isDefault = isDefault != null ? isDefault : false;
    }

    @Override
    public Map<String, List<String>> validate() {
        Map<String, List<String>> errors = new LinkedHashMap<>();

        if (receiverName == null || receiverName.isBlank()) {
            ValidationUtil.addError(errors, "receiverName", "Tên người nhận là bắt buộc");
        } else if (receiverName.trim().length() > 255) {
            ValidationUtil.addError(errors, "receiverName", "Tên người nhận tối đa 255 ký tự");
        }

        if (phoneNumber == null || phoneNumber.isBlank()) {
            ValidationUtil.addError(errors, "phoneNumber", "Số điện thoại là bắt buộc");
        } else {
            String trimmedPhone = phoneNumber.trim();
            if (trimmedPhone.length() > 50) {
                ValidationUtil.addError(errors, "phoneNumber", "Số điện thoại tối đa 50 ký tự");
            } else if (!ValidationUtil.isValidPhone(trimmedPhone)) {
                ValidationUtil.addError(errors, "phoneNumber", "Số điện thoại không đúng định dạng");
            }
        }

        if (province == null || province.isBlank()) {
            ValidationUtil.addError(errors, "province", "Tỉnh/Thành phố là bắt buộc");
        } else if (province.trim().length() > 150) {
            ValidationUtil.addError(errors, "province", "Tỉnh/Thành phố tối đa 150 ký tự");
        }

        if (district == null || district.isBlank()) {
            ValidationUtil.addError(errors, "district", "Quận/Huyện là bắt buộc");
        } else if (district.trim().length() > 150) {
            ValidationUtil.addError(errors, "district", "Quận/Huyện tối đa 150 ký tự");
        }

        if (ward == null || ward.isBlank()) {
            ValidationUtil.addError(errors, "ward", "Phường/Xã là bắt buộc");
        } else if (ward.trim().length() > 150) {
            ValidationUtil.addError(errors, "ward", "Phường/Xã tối đa 150 ký tự");
        }

        if (detailAddress == null || detailAddress.isBlank()) {
            ValidationUtil.addError(errors, "detailAddress", "Địa chỉ là bắt buộc");
        } else if (detailAddress.trim().length() > 500) {
            ValidationUtil.addError(errors, "detailAddress", "Địa chỉ chi tiết tối đa 500 ký tự");
        }

        return errors;
    }

    public String getReceiverName() {
        return receiverName != null ? receiverName.trim() : null;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getPhoneNumber() {
        return phoneNumber != null ? phoneNumber.trim() : null;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getProvince() {
        return province != null ? province.trim() : null;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getDistrict() {
        return district != null ? district.trim() : null;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getWard() {
        return ward != null ? ward.trim() : null;
    }

    public void setWard(String ward) {
        this.ward = ward;
    }

    public String getDetailAddress() {
        return detailAddress != null ? detailAddress.trim() : null;
    }

    public void setDetailAddress(String detailAddress) {
        this.detailAddress = detailAddress;
    }

    public Boolean getIsDefault() {
        return isDefault != null ? isDefault : false;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault != null ? isDefault : false;
    }
}

```

# identity\CreatePermissionDTO.java

| Property | Value |
|----------|-------|
| Path | `identity\CreatePermissionDTO.java` |
| Size | 2,285 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:40.524789 |

```java
package com.fashionshop.presentation.dto.identity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fashionshop.common.util.Validatable;
import com.fashionshop.common.util.ValidationUtil;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CreatePermissionDTO implements Validatable {

    private String code;
    private String module;
    private String description;

    public CreatePermissionDTO() {}

    public CreatePermissionDTO(String code, String module, String description) {
        this.code = code;
        this.module = module;
        this.description = description;
    }

    @Override
    public Map<String, List<String>> validate() {
        Map<String, List<String>> errors = new LinkedHashMap<>();

        if (code == null || code.isBlank()) {
            ValidationUtil.addError(errors, "code", "Mã quyền hạn không được để trống");
        } else if (code.trim().length() > 150) {
            ValidationUtil.addError(errors, "code", "Mã quyền hạn tối đa 150 ký tự");
        }

        if (module == null || module.isBlank()) {
            ValidationUtil.addError(errors, "module", "Module không được để trống");
        } else if (module.trim().length() > 100) {
            ValidationUtil.addError(errors, "module", "Module tối đa 100 ký tự");
        }

        if (description != null && description.trim().length() > 1000) {
            ValidationUtil.addError(errors, "description", "Mô tả tối đa 1000 ký tự");
        }

        return errors;
    }

    public String getCode() {
        return code != null ? code.trim().toUpperCase() : null;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getModule() {
        return module != null ? module.trim().toUpperCase() : null;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getDescription() {
        return description != null ? description.trim() : null;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

```

# identity\CreateRoleDTO.java

| Property | Value |
|----------|-------|
| Path | `identity\CreateRoleDTO.java` |
| Size | 2,403 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:40.525786 |

```java
package com.fashionshop.presentation.dto.identity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fashionshop.common.util.Validatable;
import com.fashionshop.common.util.ValidationUtil;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateRoleDTO implements Validatable {

    private String name;
    private String code;
    private Set<Integer> permissionIds = new HashSet<>();

    public CreateRoleDTO() {}

    public CreateRoleDTO(String name, String code, Set<Integer> permissionIds) {
        this.name = name;
        this.code = code;
        if (permissionIds != null) {
            this.permissionIds = permissionIds;
        }
    }

    @Override
    public Map<String, List<String>> validate() {
        Map<String, List<String>> errors = new LinkedHashMap<>();

        if (name == null || name.isBlank()) {
            ValidationUtil.addError(errors, "name", "Tên vai trò không được để trống");
        } else if (name.trim().length() > 100) {
            ValidationUtil.addError(errors, "name", "Tên vai trò tối đa 100 ký tự");
        }

        if (code == null || code.isBlank()) {
            ValidationUtil.addError(errors, "code", "Mã vai trò không được để trống");
        } else if (code.trim().length() > 50) {
            ValidationUtil.addError(errors, "code", "Mã vai trò tối đa 50 ký tự");
        }

        if (permissionIds != null && permissionIds.size() > 100) {
            ValidationUtil.addError(errors, "permissionIds", "Số lượng quyền hạn tối đa là 100");
        }

        return errors;
    }

    public String getName() {
        return name != null ? name.trim() : null;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code != null ? code.trim().toUpperCase() : null;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Set<Integer> getPermissionIds() {
        return permissionIds;
    }

    public void setPermissionIds(Set<Integer> permissionIds) {
        this.permissionIds = permissionIds != null ? permissionIds : new HashSet<>();
    }
}

```

# identity\ForgotPasswordDTO.java

| Property | Value |
|----------|-------|
| Path | `identity\ForgotPasswordDTO.java` |
| Size | 1,409 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:40.526787 |

```java
package com.fashionshop.presentation.dto.identity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fashionshop.common.util.Validatable;
import com.fashionshop.common.util.ValidationUtil;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ForgotPasswordDTO implements Validatable {

    private String email;

    public ForgotPasswordDTO() {}

    public ForgotPasswordDTO(String email) {
        this.email = email;
    }

    @Override
    public Map<String, List<String>> validate() {
        Map<String, List<String>> errors = new LinkedHashMap<>();

        if (email == null || email.isBlank()) {
            ValidationUtil.addError(errors, "email", "Email không được để trống");
        } else {
            if (!ValidationUtil.isValidEmail(email)) {
                ValidationUtil.addError(errors, "email", "Email không đúng định dạng");
            }
            if (email.trim().length() > 255) {
                ValidationUtil.addError(errors, "email", "Email tối đa 255 ký tự");
            }
        }

        return errors;
    }

    public String getEmail() {
        return email != null ? email.trim().toLowerCase() : null;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

```

# identity\LoginDTO.java

| Property | Value |
|----------|-------|
| Path | `identity\LoginDTO.java` |
| Size | 1,976 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:40.528786 |

```java
package com.fashionshop.presentation.dto.identity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fashionshop.common.util.Validatable;
import com.fashionshop.common.util.ValidationUtil;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LoginDTO implements Validatable {

    private String email;
    private String password;

    public LoginDTO() {}

    public LoginDTO(String email, String password) {
        this.email = email;
        this.password = password;
    }

    @Override
    public Map<String, List<String>> validate() {
        Map<String, List<String>> errors = new LinkedHashMap<>();

        if (email == null || email.isBlank()) {
            ValidationUtil.addError(errors, "email", "Email không được để trống");
        } else {
            if (!ValidationUtil.isValidEmail(email)) {
                ValidationUtil.addError(errors, "email", "Email không đúng định dạng");
            }
            if (email.trim().length() > 255) {
                ValidationUtil.addError(errors, "email", "Email tối đa 255 ký tự");
            }
        }

        if (password == null || password.isBlank()) {
            ValidationUtil.addError(errors, "password", "Mật khẩu không được để trống");
        } else if (password.length() < 8 || password.length() > 72) {
            ValidationUtil.addError(errors, "password", "Mật khẩu phải từ 8 đến 72 ký tự");
        }

        return errors;
    }

    public String getEmail() {
        return email != null ? email.trim().toLowerCase() : null;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

```

# identity\RefreshTokenDTO.java

| Property | Value |
|----------|-------|
| Path | `identity\RefreshTokenDTO.java` |
| Size | 1,142 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:40.529787 |

```java
package com.fashionshop.presentation.dto.identity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fashionshop.common.util.Validatable;
import com.fashionshop.common.util.ValidationUtil;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RefreshTokenDTO implements Validatable {

    private String refreshToken;

    public RefreshTokenDTO() {}

    public RefreshTokenDTO(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    @Override
    public Map<String, List<String>> validate() {
        Map<String, List<String>> errors = new LinkedHashMap<>();

        if (refreshToken == null || refreshToken.isBlank()) {
            ValidationUtil.addError(errors, "refreshToken", "Refresh token là bắt buộc");
        }

        return errors;
    }

    public String getRefreshToken() {
        return refreshToken != null ? refreshToken.trim() : null;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}

```

# identity\RegisterDTO.java

| Property | Value |
|----------|-------|
| Path | `identity\RegisterDTO.java` |
| Size | 3,074 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:40.531787 |

```java
package com.fashionshop.presentation.dto.identity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fashionshop.common.util.Validatable;
import com.fashionshop.common.util.ValidationUtil;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RegisterDTO implements Validatable {

    private String email;
    private String password;
    private String fullName;
    private String phoneNumber;

    public RegisterDTO() {}

    public RegisterDTO(String email, String password, String fullName, String phoneNumber) {
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
    }

    @Override
    public Map<String, List<String>> validate() {
        Map<String, List<String>> errors = new LinkedHashMap<>();

        if (email == null || email.isBlank()) {
            ValidationUtil.addError(errors, "email", "Email không được để trống");
        } else {
            if (!ValidationUtil.isValidEmail(email)) {
                ValidationUtil.addError(errors, "email", "Email không đúng định dạng");
            }
            if (email.trim().length() > 255) {
                ValidationUtil.addError(errors, "email", "Email tối đa 255 ký tự");
            }
        }

        ValidationUtil.validatePasswordComplexity(password, "password", errors);

        if (fullName == null || fullName.isBlank()) {
            ValidationUtil.addError(errors, "fullName", "Họ và tên không được để trống");
        } else if (fullName.trim().length() > 100) {
            ValidationUtil.addError(errors, "fullName", "Họ và tên từ 1 đến 100 ký tự");
        }

        if (phoneNumber != null && !phoneNumber.isBlank()) {
            if (phoneNumber.trim().length() > 50) {
                ValidationUtil.addError(errors, "phoneNumber", "Số điện thoại tối đa 50 ký tự");
            } else if (!ValidationUtil.isValidPhone(phoneNumber)) {
                ValidationUtil.addError(errors, "phoneNumber", "Số điện thoại không đúng định dạng");
            }
        }

        return errors;
    }

    public String getEmail() {
        return email != null ? email.trim().toLowerCase() : null;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName != null ? fullName.trim() : null;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber != null ? phoneNumber.trim() : "";
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}

```

# identity\ResetPasswordDTO.java

| Property | Value |
|----------|-------|
| Path | `identity\ResetPasswordDTO.java` |
| Size | 2,124 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:40.533786 |

```java
package com.fashionshop.presentation.dto.identity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fashionshop.common.util.Validatable;
import com.fashionshop.common.util.ValidationUtil;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ResetPasswordDTO implements Validatable {

    private String token;
    private String newPassword;
    private String confirmPassword;

    public ResetPasswordDTO() {}

    public ResetPasswordDTO(String token, String newPassword, String confirmPassword) {
        this.token = token;
        this.newPassword = newPassword;
        this.confirmPassword = confirmPassword;
    }

    @Override
    public Map<String, List<String>> validate() {
        Map<String, List<String>> errors = new LinkedHashMap<>();

        if (token == null || token.isBlank()) {
            ValidationUtil.addError(errors, "token", "Token xác thực là bắt buộc");
        }

        ValidationUtil.validatePasswordComplexity(newPassword, "newPassword", errors);

        if (confirmPassword == null || confirmPassword.isBlank()) {
            ValidationUtil.addError(errors, "confirmPassword", "Mật khẩu xác nhận không được để trống");
        } else if (newPassword != null && !newPassword.equals(confirmPassword)) {
            ValidationUtil.addError(errors, "confirmPassword", "Mật khẩu xác nhận không khớp");
        }

        return errors;
    }

    public String getToken() {
        return token != null ? token.trim() : null;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}

```

# identity\UpdateAddressDTO.java

| Property | Value |
|----------|-------|
| Path | `identity\UpdateAddressDTO.java` |
| Size | 4,560 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:40.534786 |

```java
package com.fashionshop.presentation.dto.identity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fashionshop.common.util.Validatable;
import com.fashionshop.common.util.ValidationUtil;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateAddressDTO implements Validatable {

    private String receiverName;
    private String phoneNumber;
    private String province;
    private String district;
    private String ward;
    private String detailAddress;
    private Boolean isDefault;

    public UpdateAddressDTO() {}

    public UpdateAddressDTO(String receiverName, String phoneNumber, String province, String district, String ward, String detailAddress, Boolean isDefault) {
        this.receiverName = receiverName;
        this.phoneNumber = phoneNumber;
        this.province = province;
        this.district = district;
        this.ward = ward;
        this.detailAddress = detailAddress;
        this.isDefault = isDefault;
    }

    @Override
    public Map<String, List<String>> validate() {
        Map<String, List<String>> errors = new LinkedHashMap<>();

        if (receiverName != null) {
            String trimmed = receiverName.trim();
            if (trimmed.isEmpty() || trimmed.length() > 255) {
                ValidationUtil.addError(errors, "receiverName", "Tên người nhận từ 1 đến 255 ký tự");
            }
        }

        if (phoneNumber != null) {
            String trimmed = phoneNumber.trim();
            if (trimmed.isEmpty() || trimmed.length() > 50) {
                ValidationUtil.addError(errors, "phoneNumber", "Số điện thoại tối đa 50 ký tự");
            } else if (!ValidationUtil.isValidPhone(trimmed)) {
                ValidationUtil.addError(errors, "phoneNumber", "Số điện thoại không đúng định dạng");
            }
        }

        if (province != null) {
            String trimmed = province.trim();
            if (trimmed.isEmpty() || trimmed.length() > 150) {
                ValidationUtil.addError(errors, "province", "Tỉnh/Thành phố từ 1 đến 150 ký tự");
            }
        }

        if (district != null) {
            String trimmed = district.trim();
            if (trimmed.isEmpty() || trimmed.length() > 150) {
                ValidationUtil.addError(errors, "district", "Quận/Huyện từ 1 đến 150 ký tự");
            }
        }

        if (ward != null) {
            String trimmed = ward.trim();
            if (trimmed.isEmpty() || trimmed.length() > 150) {
                ValidationUtil.addError(errors, "ward", "Phường/Xã từ 1 đến 150 ký tự");
            }
        }

        if (detailAddress != null) {
            String trimmed = detailAddress.trim();
            if (trimmed.isEmpty() || trimmed.length() > 500) {
                ValidationUtil.addError(errors, "detailAddress", "Địa chỉ chi tiết từ 1 đến 500 ký tự");
            }
        }

        return errors;
    }

    public String getReceiverName() {
        return receiverName != null ? receiverName.trim() : null;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getPhoneNumber() {
        return phoneNumber != null ? phoneNumber.trim() : null;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getProvince() {
        return province != null ? province.trim() : null;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getDistrict() {
        return district != null ? district.trim() : null;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getWard() {
        return ward != null ? ward.trim() : null;
    }

    public void setWard(String ward) {
        this.ward = ward;
    }

    public String getDetailAddress() {
        return detailAddress != null ? detailAddress.trim() : null;
    }

    public void setDetailAddress(String detailAddress) {
        this.detailAddress = detailAddress;
    }

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }
}

```

# identity\UpdatePermissionDTO.java

| Property | Value |
|----------|-------|
| Path | `identity\UpdatePermissionDTO.java` |
| Size | 1,689 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:40.535787 |

```java
package com.fashionshop.presentation.dto.identity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fashionshop.common.util.Validatable;
import com.fashionshop.common.util.ValidationUtil;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdatePermissionDTO implements Validatable {

    private String module;
    private String description;

    public UpdatePermissionDTO() {}

    public UpdatePermissionDTO(String module, String description) {
        this.module = module;
        this.description = description;
    }

    @Override
    public Map<String, List<String>> validate() {
        Map<String, List<String>> errors = new LinkedHashMap<>();

        if (module != null) {
            String trimmed = module.trim();
            if (trimmed.isEmpty() || trimmed.length() > 100) {
                ValidationUtil.addError(errors, "module", "Module từ 1 đến 100 ký tự");
            }
        }

        if (description != null && description.trim().length() > 1000) {
            ValidationUtil.addError(errors, "description", "Mô tả tối đa 1000 ký tự");
        }

        return errors;
    }

    public String getModule() {
        return module != null ? module.trim().toUpperCase() : null;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getDescription() {
        return description != null ? description.trim() : null;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

```

# identity\UpdateRoleDTO.java

| Property | Value |
|----------|-------|
| Path | `identity\UpdateRoleDTO.java` |
| Size | 1,924 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:40.536787 |

```java
package com.fashionshop.presentation.dto.identity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fashionshop.common.util.Validatable;
import com.fashionshop.common.util.ValidationUtil;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateRoleDTO implements Validatable {

    private String name;
    private Boolean isActive;
    private Set<Integer> permissionIds;

    public UpdateRoleDTO() {}

    public UpdateRoleDTO(String name, Boolean isActive, Set<Integer> permissionIds) {
        this.name = name;
        this.isActive = isActive;
        this.permissionIds = permissionIds;
    }

    @Override
    public Map<String, List<String>> validate() {
        Map<String, List<String>> errors = new LinkedHashMap<>();

        if (name != null) {
            String trimmed = name.trim();
            if (trimmed.isEmpty() || trimmed.length() > 100) {
                ValidationUtil.addError(errors, "name", "Tên vai trò từ 1 đến 100 ký tự");
            }
        }

        if (permissionIds != null && permissionIds.size() > 100) {
            ValidationUtil.addError(errors, "permissionIds", "Số lượng quyền hạn tối đa là 100");
        }

        return errors;
    }

    public String getName() {
        return name != null ? name.trim() : null;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Set<Integer> getPermissionIds() {
        return permissionIds;
    }

    public void setPermissionIds(Set<Integer> permissionIds) {
        this.permissionIds = permissionIds;
    }
}

```

# identity\UpdateUserDTO.java

| Property | Value |
|----------|-------|
| Path | `identity\UpdateUserDTO.java` |
| Size | 3,088 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:40.538788 |

```java
package com.fashionshop.presentation.dto.identity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fashionshop.common.util.Validatable;
import com.fashionshop.common.util.ValidationUtil;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateUserDTO implements Validatable {

    private String fullName;
    private String phoneNumber;
    private String avatarUrl;

    public UpdateUserDTO() {}

    public UpdateUserDTO(String fullName, String phoneNumber, String avatarUrl) {
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.avatarUrl = avatarUrl;
    }

    @Override
    public Map<String, List<String>> validate() {
        Map<String, List<String>> errors = new LinkedHashMap<>();

        if (fullName != null) {
            String trimmed = fullName.trim();
            if (trimmed.isEmpty() || trimmed.length() > 100) {
                ValidationUtil.addError(errors, "fullName", "Họ và tên từ 1 đến 100 ký tự");
            }
        }

        if (phoneNumber != null && !phoneNumber.isBlank()) {
            String trimmed = phoneNumber.trim();
            if (trimmed.length() > 50) {
                ValidationUtil.addError(errors, "phoneNumber", "Số điện thoại tối đa 50 ký tự");
            } else if (!ValidationUtil.isValidPhone(trimmed)) {
                ValidationUtil.addError(errors, "phoneNumber", "Số điện thoại không đúng định dạng");
            }
        }

        if (avatarUrl != null && !avatarUrl.isBlank()) {
            String trimmed = avatarUrl.trim();
            if (trimmed.length() > 1000) {
                ValidationUtil.addError(errors, "avatarUrl", "Avatar tối đa 1000 ký tự");
            } else {
                try {
                    URI uri = URI.create(trimmed);
                    if (uri.getScheme() == null || (!"http".equalsIgnoreCase(uri.getScheme()) && !"https".equalsIgnoreCase(uri.getScheme()))) {
                        ValidationUtil.addError(errors, "avatarUrl", "Avatar không hợp lệ");
                    }
                } catch (Exception e) {
                    ValidationUtil.addError(errors, "avatarUrl", "Avatar không hợp lệ");
                }
            }
        }

        return errors;
    }

    public String getFullName() {
        return fullName != null ? fullName.trim() : null;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber != null ? phoneNumber.trim() : null;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAvatarUrl() {
        return avatarUrl != null ? avatarUrl.trim() : null;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}

```

# identity\UpdateUserStatusDTO.java

| Property | Value |
|----------|-------|
| Path | `identity\UpdateUserStatusDTO.java` |
| Size | 1,579 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:40.539790 |

```java
package com.fashionshop.presentation.dto.identity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fashionshop.business.entity.User;
import com.fashionshop.common.util.Validatable;
import com.fashionshop.common.util.ValidationUtil;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateUserStatusDTO implements Validatable {

    private String status;

    public UpdateUserStatusDTO() {}

    public UpdateUserStatusDTO(String status) {
        this.status = status;
    }

    @Override
    public Map<String, List<String>> validate() {
        Map<String, List<String>> errors = new LinkedHashMap<>();

        if (status == null || status.isBlank()) {
            ValidationUtil.addError(errors, "status", "Trạng thái người dùng không được để trống");
        } else {
            try {
                User.UserStatus.valueOf(status.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                ValidationUtil.addError(errors, "status", "Trạng thái người dùng không hợp lệ (ACTIVE, INACTIVE, BLOCKED)");
            }
        }

        return errors;
    }

    public String getStatus() {
        return status != null ? status.trim().toUpperCase() : null;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public User.UserStatus toUserStatus() {
        return User.UserStatus.valueOf(getStatus());
    }
}

```

# identity\UserQueryDTO.java

| Property | Value |
|----------|-------|
| Path | `identity\UserQueryDTO.java` |
| Size | 1,425 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:40.540788 |

```java
package com.fashionshop.presentation.dto.identity;

public class UserQueryDTO {

    private int page = 1;
    private int limit = 10;
    private String search;
    private String status;
    private Integer roleId;

    public UserQueryDTO() {}

    public UserQueryDTO(int page, int limit, String search, String status, Integer roleId) {
        setPage(page);
        setLimit(limit);
        this.search = search;
        this.status = status;
        this.roleId = roleId;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page > 0 ? page : 1;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        if (limit <= 0) {
            this.limit = 10;
        } else {
            this.limit = Math.min(limit, 100);
        }
    }

    public String getSearch() {
        return search != null ? search.trim() : null;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getStatus() {
        return status != null ? status.trim().toUpperCase() : null;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }
}

```

# identity\VerifyOtpDTO.java

| Property | Value |
|----------|-------|
| Path | `identity\VerifyOtpDTO.java` |
| Size | 2,499 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:40.542790 |

```java
package com.fashionshop.presentation.dto.identity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fashionshop.common.util.Validatable;
import com.fashionshop.common.util.ValidationUtil;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VerifyOtpDTO implements Validatable {

    private String email;
    private String code;
    private String type; // REGISTER, FORGOT_PASSWORD

    public VerifyOtpDTO() {}

    public VerifyOtpDTO(String email, String code, String type) {
        this.email = email;
        this.code = code;
        this.type = type;
    }

    @Override
    public Map<String, List<String>> validate() {
        Map<String, List<String>> errors = new LinkedHashMap<>();

        if (email == null || email.isBlank()) {
            ValidationUtil.addError(errors, "email", "Email không được để trống");
        } else if (!ValidationUtil.isValidEmail(email)) {
            ValidationUtil.addError(errors, "email", "Email không đúng định dạng");
        }

        if (code == null || code.isBlank()) {
            ValidationUtil.addError(errors, "code", "Mã xác thực không được để trống");
        } else if (!ValidationUtil.isValidOtp(code)) {
            ValidationUtil.addError(errors, "code", "Mã xác thực phải là 6 chữ số");
        }

        if (type == null || type.isBlank()) {
            ValidationUtil.addError(errors, "type", "Loại xác thực không được để trống");
        } else {
            String upper = type.trim().toUpperCase();
            if (!"REGISTER".equals(upper) && !"FORGOT_PASSWORD".equals(upper)) {
                ValidationUtil.addError(errors, "type", "Loại xác thực chỉ chấp nhận REGISTER hoặc FORGOT_PASSWORD");
            }
        }

        return errors;
    }

    public String getEmail() {
        return email != null ? email.trim().toLowerCase() : null;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCode() {
        return code != null ? code.trim() : null;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getType() {
        return type != null ? type.trim().toUpperCase() : null;
    }

    public void setType(String type) {
        this.type = type;
    }
}

```

# inventory\CreateFlashSaleDTO.java

| Property | Value |
|----------|-------|
| Path | `inventory\CreateFlashSaleDTO.java` |
| Size | 1,219 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:28.614152 |

```java
package com.fashionshop.presentation.dto.inventory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CreateFlashSaleDTO {
    private String name;
    private String bannerUrl;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Boolean isActive = true;
    private List<FlashSaleItemDTO> items = new ArrayList<>();

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
    public List<FlashSaleItemDTO> getItems() { return items; }
    public void setItems(List<FlashSaleItemDTO> items) { this.items = items; }
}
```

# inventory\FlashSaleItemDTO.java

| Property | Value |
|----------|-------|
| Path | `inventory\FlashSaleItemDTO.java` |
| Size | 705 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:28.616151 |

```java
package com.fashionshop.presentation.dto.inventory;

import java.math.BigDecimal;

public class FlashSaleItemDTO {
    private Integer variantId;
    private BigDecimal flashSalePrice;
    private Integer promoQuantity;

    public Integer getVariantId() { return variantId; }
    public void setVariantId(Integer variantId) { this.variantId = variantId; }
    public BigDecimal getFlashSalePrice() { return flashSalePrice; }
    public void setFlashSalePrice(BigDecimal flashSalePrice) { this.flashSalePrice = flashSalePrice; }
    public Integer getPromoQuantity() { return promoQuantity; }
    public void setPromoQuantity(Integer promoQuantity) { this.promoQuantity = promoQuantity; }
}
```

# inventory\FlashSaleQueryDTO.java

| Property | Value |
|----------|-------|
| Path | `inventory\FlashSaleQueryDTO.java` |
| Size | 549 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:28.618152 |

```java
package com.fashionshop.presentation.dto.inventory;

public class FlashSaleQueryDTO {
    private int page = 1;
    private int limit = 10;
    private Boolean isActive;

    public int getPage() { return page; }
    public void setPage(int page) { this.page = page > 0 ? page : 1; }
    public int getLimit() { return limit; }
    public void setLimit(int limit) { this.limit = limit > 0 ? limit : 10; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}
```

# inventory\UpdateFlashSaleDTO.java

| Property | Value |
|----------|-------|
| Path | `inventory\UpdateFlashSaleDTO.java` |
| Size | 1,163 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:28.621152 |

```java
package com.fashionshop.presentation.dto.inventory;

import java.time.LocalDateTime;
import java.util.List;

public class UpdateFlashSaleDTO {
    private String name;
    private String bannerUrl;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Boolean isActive;
    private List<FlashSaleItemDTO> items;

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
    public List<FlashSaleItemDTO> getItems() { return items; }
    public void setItems(List<FlashSaleItemDTO> items) { this.items = items; }
}
```

# loyalty\ApplyVoucherDTO.java

| Property | Value |
|----------|-------|
| Path | `loyalty\ApplyVoucherDTO.java` |
| Size | 218 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:28.622151 |

```java
package com.fashionshop.presentation.dto.loyalty;

public class ApplyVoucherDTO {
    private String code;
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
}
```

# loyalty\ChatHistoryQueryDTO.java

| Property | Value |
|----------|-------|
| Path | `loyalty\ChatHistoryQueryDTO.java` |
| Size | 702 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:28.624152 |

```java
package com.fashionshop.presentation.dto.loyalty;

public class ChatHistoryQueryDTO {
    private Integer chatId;
    private int page = 1;
    private int limit = 20;
    private Integer cursor;

    public Integer getChatId() { return chatId; }
    public void setChatId(Integer chatId) { this.chatId = chatId; }
    public int getPage() { return page; }
    public void setPage(int page) { this.page = page > 0 ? page : 1; }
    public int getLimit() { return limit; }
    public void setLimit(int limit) { this.limit = (limit > 0 && limit <= 100) ? limit : 20; }
    public Integer getCursor() { return cursor; }
    public void setCursor(Integer cursor) { this.cursor = cursor; }
}
```

# loyalty\CreateReviewDTO.java

| Property | Value |
|----------|-------|
| Path | `loyalty\CreateReviewDTO.java` |
| Size | 1,330 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:28.626155 |

```java
package com.fashionshop.presentation.dto.loyalty;

import java.util.ArrayList;
import java.util.List;

public class CreateReviewDTO {
    private Integer productId;
    private Integer orderId;
    private Short rating;
    private String content;
    private List<MediaItemDTO> mediaUrls = new ArrayList<>();

    public static class MediaItemDTO {
        private String url;
        private String type = "image";

        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
    }

    public Integer getProductId() { return productId; }
    public void setProductId(Integer productId) { this.productId = productId; }
    public Integer getOrderId() { return orderId; }
    public void setOrderId(Integer orderId) { this.orderId = orderId; }
    public Short getRating() { return rating; }
    public void setRating(Short rating) { this.rating = rating; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public List<MediaItemDTO> getMediaUrls() { return mediaUrls; }
    public void setMediaUrls(List<MediaItemDTO> mediaUrls) { this.mediaUrls = mediaUrls; }
}
```

# loyalty\CreateVoucherDTO.java

| Property | Value |
|----------|-------|
| Path | `loyalty\CreateVoucherDTO.java` |
| Size | 2,168 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:28.630152 |

```java
package com.fashionshop.presentation.dto.loyalty;

import com.fashionshop.business.entity.Voucher;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CreateVoucherDTO {
    private String code;
    private Voucher.DiscountType discountType;
    private BigDecimal discountValue;
    private BigDecimal minOrderValue = BigDecimal.ZERO;
    private BigDecimal maxDiscountAmt;
    private Integer totalUsageLimit = 0;
    private Integer userUsageLimit = 1;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Boolean isActive = true;

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public Voucher.DiscountType getDiscountType() { return discountType; }
    public void setDiscountType(Voucher.DiscountType discountType) { this.discountType = discountType; }
    public BigDecimal getDiscountValue() { return discountValue; }
    public void setDiscountValue(BigDecimal discountValue) { this.discountValue = discountValue; }
    public BigDecimal getMinOrderValue() { return minOrderValue; }
    public void setMinOrderValue(BigDecimal minOrderValue) { this.minOrderValue = minOrderValue; }
    public BigDecimal getMaxDiscountAmt() { return maxDiscountAmt; }
    public void setMaxDiscountAmt(BigDecimal maxDiscountAmt) { this.maxDiscountAmt = maxDiscountAmt; }
    public Integer getTotalUsageLimit() { return totalUsageLimit; }
    public void setTotalUsageLimit(Integer totalUsageLimit) { this.totalUsageLimit = totalUsageLimit; }
    public Integer getUserUsageLimit() { return userUsageLimit; }
    public void setUserUsageLimit(Integer userUsageLimit) { this.userUsageLimit = userUsageLimit; }
    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }
    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}
```

# loyalty\EarnPointsDTO.java

| Property | Value |
|----------|-------|
| Path | `loyalty\EarnPointsDTO.java` |
| Size | 461 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:28.632151 |

```java
package com.fashionshop.presentation.dto.loyalty;

public class EarnPointsDTO {
    private Integer orderId;
    private String description = "Cộng điểm từ đơn hàng";

    public Integer getOrderId() { return orderId; }
    public void setOrderId(Integer orderId) { this.orderId = orderId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
```

# loyalty\MarkReadDTO.java

| Property | Value |
|----------|-------|
| Path | `loyalty\MarkReadDTO.java` |
| Size | 231 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:28.634150 |

```java
package com.fashionshop.presentation.dto.loyalty;

public class MarkReadDTO {
    private Integer chatId;
    public Integer getChatId() { return chatId; }
    public void setChatId(Integer chatId) { this.chatId = chatId; }
}
```

# loyalty\PointQueryDTO.java

| Property | Value |
|----------|-------|
| Path | `loyalty\PointQueryDTO.java` |
| Size | 382 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:28.636151 |

```java
package com.fashionshop.presentation.dto.loyalty;

public class PointQueryDTO {
    private int page = 1;
    private int limit = 10;

    public int getPage() { return page; }
    public void setPage(int page) { this.page = page > 0 ? page : 1; }

    public int getLimit() { return limit; }
    public void setLimit(int limit) { this.limit = limit > 0 ? limit : 10; }
}
```

# loyalty\RedeemPointsDTO.java

| Property | Value |
|----------|-------|
| Path | `loyalty\RedeemPointsDTO.java` |
| Size | 451 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:28.638151 |

```java
package com.fashionshop.presentation.dto.loyalty;

public class RedeemPointsDTO {
    private Integer orderId;
    private Integer pointsToRedeem;

    public Integer getOrderId() { return orderId; }
    public void setOrderId(Integer orderId) { this.orderId = orderId; }

    public Integer getPointsToRedeem() { return pointsToRedeem; }
    public void setPointsToRedeem(Integer pointsToRedeem) { this.pointsToRedeem = pointsToRedeem; }
}
```

# loyalty\ReplyReviewDTO.java

| Property | Value |
|----------|-------|
| Path | `loyalty\ReplyReviewDTO.java` |
| Size | 254 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:28.640151 |

```java
package com.fashionshop.presentation.dto.loyalty;

public class ReplyReviewDTO {
    private String shopReply;

    public String getShopReply() { return shopReply; }
    public void setShopReply(String shopReply) { this.shopReply = shopReply; }
}
```

# loyalty\ReviewQueryDTO.java

| Property | Value |
|----------|-------|
| Path | `loyalty\ReviewQueryDTO.java` |
| Size | 700 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:28.642152 |

```java
package com.fashionshop.presentation.dto.loyalty;

public class ReviewQueryDTO {
    private Integer productId;
    private int page = 1;
    private int limit = 10;
    private Integer rating;

    public Integer getProductId() { return productId; }
    public void setProductId(Integer productId) { this.productId = productId; }
    public int getPage() { return page; }
    public void setPage(int page) { this.page = page > 0 ? page : 1; }
    public int getLimit() { return limit; }
    public void setLimit(int limit) { this.limit = limit > 0 ? limit : 10; }
    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }
}
```

# loyalty\SendMessageDTO.java

| Property | Value |
|----------|-------|
| Path | `loyalty\SendMessageDTO.java` |
| Size | 389 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:28.644151 |

```java
package com.fashionshop.presentation.dto.loyalty;

public class SendMessageDTO {
    private Integer chatId;
    private String content;

    public Integer getChatId() { return chatId; }
    public void setChatId(Integer chatId) { this.chatId = chatId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
```

# loyalty\SocketSessionDTO.java

| Property | Value |
|----------|-------|
| Path | `loyalty\SocketSessionDTO.java` |
| Size | 398 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:28.645153 |

```java
package com.fashionshop.presentation.dto.loyalty;

public class SocketSessionDTO {
    private String socketId;
    private Integer userId;

    public String getSocketId() { return socketId; }
    public void setSocketId(String socketId) { this.socketId = socketId; }
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
}
```

# loyalty\UpdateVoucherDTO.java

| Property | Value |
|----------|-------|
| Path | `loyalty\UpdateVoucherDTO.java` |
| Size | 2,135 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:28.647153 |

```java
package com.fashionshop.presentation.dto.loyalty;

import com.fashionshop.business.entity.Voucher;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class UpdateVoucherDTO {
    private String code;
    private Voucher.DiscountType discountType;
    private BigDecimal discountValue;
    private BigDecimal minOrderValue;
    private BigDecimal maxDiscountAmt;
    private Integer totalUsageLimit;
    private Integer userUsageLimit;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Boolean isActive;

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public Voucher.DiscountType getDiscountType() { return discountType; }
    public void setDiscountType(Voucher.DiscountType discountType) { this.discountType = discountType; }
    public BigDecimal getDiscountValue() { return discountValue; }
    public void setDiscountValue(BigDecimal discountValue) { this.discountValue = discountValue; }
    public BigDecimal getMinOrderValue() { return minOrderValue; }
    public void setMinOrderValue(BigDecimal minOrderValue) { this.minOrderValue = minOrderValue; }
    public BigDecimal getMaxDiscountAmt() { return maxDiscountAmt; }
    public void setMaxDiscountAmt(BigDecimal maxDiscountAmt) { this.maxDiscountAmt = maxDiscountAmt; }
    public Integer getTotalUsageLimit() { return totalUsageLimit; }
    public void setTotalUsageLimit(Integer totalUsageLimit) { this.totalUsageLimit = totalUsageLimit; }
    public Integer getUserUsageLimit() { return userUsageLimit; }
    public void setUserUsageLimit(Integer userUsageLimit) { this.userUsageLimit = userUsageLimit; }
    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }
    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}
```

# loyalty\ValidateVoucherDTO.java

| Property | Value |
|----------|-------|
| Path | `loyalty\ValidateVoucherDTO.java` |
| Size | 441 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:28.649153 |

```java
package com.fashionshop.presentation.dto.loyalty;

import java.math.BigDecimal;

public class ValidateVoucherDTO {
    private String code;
    private BigDecimal orderTotal;

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public BigDecimal getOrderTotal() { return orderTotal; }
    public void setOrderTotal(BigDecimal orderTotal) { this.orderTotal = orderTotal; }
}
```

# loyalty\VoucherQueryDTO.java

| Property | Value |
|----------|-------|
| Path | `loyalty\VoucherQueryDTO.java` |
| Size | 545 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:28.651151 |

```java
package com.fashionshop.presentation.dto.loyalty;

public class VoucherQueryDTO {
    private int page = 1;
    private int limit = 10;
    private Boolean isActive;

    public int getPage() { return page; }
    public void setPage(int page) { this.page = page > 0 ? page : 1; }
    public int getLimit() { return limit; }
    public void setLimit(int limit) { this.limit = limit > 0 ? limit : 10; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}
```

