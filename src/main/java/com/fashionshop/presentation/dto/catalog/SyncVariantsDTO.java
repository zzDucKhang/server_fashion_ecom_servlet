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

