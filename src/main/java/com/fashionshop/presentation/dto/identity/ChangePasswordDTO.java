package com.fashionshop.presentation.dto.identity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fashionshop.common.util.Validatable;
import com.fashionshop.common.util.ValidationUtil;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ChangePasswordDTO implements Validatable {

    private String oldPassword;
    private String newPassword;
    private String confirmPassword;

    public ChangePasswordDTO() {}

    public ChangePasswordDTO(String oldPassword, String newPassword, String confirmPassword) {
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
        this.confirmPassword = confirmPassword;
    }

    @Override
    public Map<String, List<String>> validate() {
        Map<String, List<String>> errors = new LinkedHashMap<>();

        if (oldPassword == null || oldPassword.isBlank()) {
            ValidationUtil.addError(errors, "oldPassword", "Mật khẩu cũ là bắt buộc");
        }

        ValidationUtil.validatePasswordComplexity(newPassword, "newPassword", errors);

        if (confirmPassword == null || confirmPassword.isBlank()) {
            ValidationUtil.addError(errors, "confirmPassword", "Mật khẩu xác nhận không được để trống");
        } else if (newPassword != null && !newPassword.equals(confirmPassword)) {
            ValidationUtil.addError(errors, "confirmPassword", "Mật khẩu xác nhận không khớp");
        }

        return errors;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
