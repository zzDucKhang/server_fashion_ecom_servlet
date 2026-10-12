package com.fashionshop.presentation.dto.identity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fashionshop.common.util.Validatable;
import com.fashionshop.common.util.ValidationUtil;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ResetPasswordDTO implements Validatable {

    private String token;
    private String newPassword;
    private String confirmPassword;

    public ResetPasswordDTO() {}

    public ResetPasswordDTO(String token, String newPassword, String confirmPassword) {
        this.token = token;
        this.newPassword = newPassword;
        this.confirmPassword = confirmPassword;
    }

    @Override
    public Map<String, List<String>> validate() {
        Map<String, List<String>> errors = new LinkedHashMap<>();

        if (token == null || token.isBlank()) {
            ValidationUtil.addError(errors, "token", "Token xác thực là bắt buộc");
        }

        ValidationUtil.validatePasswordComplexity(newPassword, "newPassword", errors);

        if (confirmPassword == null || confirmPassword.isBlank()) {
            ValidationUtil.addError(errors, "confirmPassword", "Mật khẩu xác nhận không được để trống");
        } else if (newPassword != null && !newPassword.equals(confirmPassword)) {
            ValidationUtil.addError(errors, "confirmPassword", "Mật khẩu xác nhận không khớp");
        }

        return errors;
    }

    public String getToken() {
        return token != null ? token.trim() : null;
    }

    public void setToken(String token) {
        this.token = token;
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
