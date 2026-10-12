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

