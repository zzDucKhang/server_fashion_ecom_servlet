package com.fashionshop.presentation.dto.identity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fashionshop.business.entity.User;
import com.fashionshop.common.util.Validatable;
import com.fashionshop.common.util.ValidationUtil;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateUserStatusDTO implements Validatable {

    private String status;

    public UpdateUserStatusDTO() {}

    public UpdateUserStatusDTO(String status) {
        this.status = status;
    }

    @Override
    public Map<String, List<String>> validate() {
        Map<String, List<String>> errors = new LinkedHashMap<>();

        if (status == null || status.isBlank()) {
            ValidationUtil.addError(errors, "status", "Trạng thái người dùng không được để trống");
        } else {
            try {
                User.UserStatus.valueOf(status.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                ValidationUtil.addError(errors, "status", "Trạng thái người dùng không hợp lệ (ACTIVE, INACTIVE, BLOCKED)");
            }
        }

        return errors;
    }

    public String getStatus() {
        return status != null ? status.trim().toUpperCase() : null;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public User.UserStatus toUserStatus() {
        return User.UserStatus.valueOf(getStatus());
    }
}
