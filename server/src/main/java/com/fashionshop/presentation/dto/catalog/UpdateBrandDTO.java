package com.fashionshop.presentation.dto.catalog;

import com.fashionshop.common.util.Validatable;
import com.fashionshop.common.util.ValidationUtil;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class UpdateBrandDTO implements Validatable {
    private String defaultName;
    private String logo;
    private String logoUrl;

    @Override
    public Map<String, List<String>> validate() {
        Map<String, List<String>> errors = new LinkedHashMap<>();

        if (defaultName != null) {
            if (defaultName.isBlank()) {
                ValidationUtil.addError(errors, "defaultName", "Tên thương hiệu không được để trống");
            } else if (defaultName.trim().length() > 255) {
                ValidationUtil.addError(errors, "defaultName", "Tên thương hiệu tối đa 255 ký tự");
            }
        }

        String resolvedLogo = getLogo();
        if (resolvedLogo != null && resolvedLogo.trim().length() > 1000) {
            ValidationUtil.addError(errors, "logo", "Đường dẫn logo tối đa 1000 ký tự");
        }

        return errors;
    }

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

