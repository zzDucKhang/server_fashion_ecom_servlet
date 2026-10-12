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

