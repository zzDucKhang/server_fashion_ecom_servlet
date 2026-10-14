package com.fashionshop.presentation.dto.catalog;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fashionshop.common.util.Validatable;
import com.fashionshop.common.util.ValidationUtil;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class UpdateCategoryDTO implements Validatable {
    @JsonAlias({"name"})
    private String defaultName;
    private String logo;
    private String iconUrl;
    @JsonAlias({"parentId"})
    private Integer parentCategoryId;
    private boolean parentCategoryIdProvided = false;
    private Boolean isVisible;

    @Override
    public Map<String, List<String>> validate() {
        Map<String, List<String>> errors = new LinkedHashMap<>();

        if (defaultName != null) {
            if (defaultName.isBlank()) {
                ValidationUtil.addError(errors, "defaultName", "Tên danh mục không được để trống");
            } else if (defaultName.trim().length() > 500) {
                ValidationUtil.addError(errors, "defaultName", "Tên danh mục tối đa 500 ký tự");
            }
        }

        String resolvedIcon = getIconUrl();
        if (resolvedIcon != null && resolvedIcon.trim().length() > 1000) {
            ValidationUtil.addError(errors, "iconUrl", "Đường dẫn biểu tượng tối đa 1000 ký tự");
        }

        if (parentCategoryId != null && parentCategoryId < 0) {
            ValidationUtil.addError(errors, "parentCategoryId", "ID danh mục cha phải là số nguyên dương");
        }

        return errors;
    }

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
    public void setParentCategoryId(Integer parentCategoryId) {
        this.parentCategoryId = parentCategoryId;
        this.parentCategoryIdProvided = true;
    }

    public boolean isParentCategoryIdProvided() { return parentCategoryIdProvided; }

    public Boolean getIsVisible() { return isVisible; }
    public void setIsVisible(Boolean isVisible) { this.isVisible = isVisible; }
}