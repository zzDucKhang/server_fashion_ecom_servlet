package com.fashionshop.presentation.dto.identity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fashionshop.common.util.Validatable;
import com.fashionshop.common.util.ValidationUtil;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ForgotPasswordDTO implements Validatable {

    private String email;

    public ForgotPasswordDTO() {}

    public ForgotPasswordDTO(String email) {
        this.email = email;
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

        return errors;
    }

    public String getEmail() {
        return email != null ? email.trim().toLowerCase() : null;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
