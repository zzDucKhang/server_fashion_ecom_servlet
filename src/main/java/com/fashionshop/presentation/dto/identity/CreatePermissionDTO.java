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
