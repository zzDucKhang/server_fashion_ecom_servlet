package com.fashionshop.presentation.dto.identity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fashionshop.common.util.Validatable;
import com.fashionshop.common.util.ValidationUtil;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LoginDTO implements Validatable {

    private String email;
    private String password;

    public LoginDTO() {}

    public LoginDTO(String email, String password) {
        this.email = email;
        this.password = password;
    }

    @Override
    public Map<String, List<String>> validate() {
        Map<String, List<String>> errors = new LinkedHashMap<>();

        if (email == null || email.isBlank()) {
            ValidationUtil.addError(errors, "email", "Email không được để trống");
        } else {
            if (!ValidationUtil.isValidEmail(email)) {
                ValidationUtil.addError(errors, "email", "Email không đúng định dạng");
            }
            if (email.trim().length() > 255) {
                ValidationUtil.addError(errors, "email", "Email tối đa 255 ký tự");
            }
        }

        if (password == null || password.isBlank()) {
            ValidationUtil.addError(errors, "password", "Mật khẩu không được để trống");
        } else if (password.length() < 8 || password.length() > 72) {
            ValidationUtil.addError(errors, "password", "Mật khẩu phải từ 8 đến 72 ký tự");
        }

        return errors;
    }

    public String getEmail() {
        return email != null ? email.trim().toLowerCase() : null;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
