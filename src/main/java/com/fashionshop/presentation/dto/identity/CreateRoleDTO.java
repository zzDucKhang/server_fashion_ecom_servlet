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
