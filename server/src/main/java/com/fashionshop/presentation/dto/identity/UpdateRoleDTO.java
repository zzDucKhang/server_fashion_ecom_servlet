package com.fashionshop.presentation.dto.identity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fashionshop.common.util.Validatable;
import com.fashionshop.common.util.ValidationUtil;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateRoleDTO implements Validatable {

    private String name;
    private Boolean isActive;
    private Set<Integer> permissionIds;

    public UpdateRoleDTO() {}

    public UpdateRoleDTO(String name, Boolean isActive, Set<Integer> permissionIds) {
        this.name = name;
        this.isActive = isActive;
        this.permissionIds = permissionIds;
    }

    @Override
    public Map<String, List<String>> validate() {
        Map<String, List<String>> errors = new LinkedHashMap<>();

        if (name != null) {
            String trimmed = name.trim();
            if (trimmed.isEmpty() || trimmed.length() > 100) {
                ValidationUtil.addError(errors, "name", "Tên vai trò từ 1 đến 100 ký tự");
            }
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

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Set<Integer> getPermissionIds() {
        return permissionIds;
    }

    public void setPermissionIds(Set<Integer> permissionIds) {
        this.permissionIds = permissionIds;
    }
}
