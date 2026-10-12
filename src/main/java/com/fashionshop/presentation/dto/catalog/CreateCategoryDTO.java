package com.fashionshop.presentation.dto.catalog;

import com.fashionshop.common.util.Validatable;
import com.fashionshop.common.util.ValidationUtil;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CreateCategoryDTO implements Validatable {
    private String defaultName;
    private String logo;
    private String iconUrl;
    private Integer parentCategoryId;

    @Override
    public Map<String, List<String>> validate() {
        Map<String, List<String>> errors = new LinkedHashMap<>();

        if (defaultName == null || defaultName.trim().isEmpty()) {
            ValidationUtil.addError(errors, "defaultName", "Tên danh mục không được để trống");
        } else if (defaultName.trim().length() > 500) {
            ValidationUtil.addError(errors, "defaultName", "Tên danh mục tối đa 500 ký tự");
        }

        if (parentCategoryId != null && parentCategoryId <= 0) {
            ValidationUtil.addError(errors, "parentCategoryId", "ID danh mục cha phải là số nguyên dương");
        }

        return errors;
    }

    // Giữ nguyên các Getter/Setter thông minh của bạn ở đây...
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