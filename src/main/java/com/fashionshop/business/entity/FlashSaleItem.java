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