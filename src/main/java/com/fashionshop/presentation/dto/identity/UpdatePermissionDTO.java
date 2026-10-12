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
