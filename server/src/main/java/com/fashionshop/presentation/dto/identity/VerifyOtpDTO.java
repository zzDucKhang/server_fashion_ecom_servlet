package com.fashionshop.presentation.dto.identity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fashionshop.common.util.Validatable;
import com.fashionshop.common.util.ValidationUtil;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VerifyOtpDTO implements Validatable {

    private String email;
    private String code;
    private String type; // REGISTER, FORGOT_PASSWORD

    public VerifyOtpDTO() {}

    public VerifyOtpDTO(String email, String code, String type) {
        this.email = email;
        this.code = code;
        this.type = type;
    }

    @Override
    public Map<String, List<String>> validate() {
        Map<String, List<String>> errors = new LinkedHashMap<>();

        if (email == null || email.isBlank()) {
            ValidationUtil.addError(errors, "email", "Email không được để trống");
        } else if (!ValidationUtil.isValidEmail(email)) {
            ValidationUtil.addError(errors, "email", "Email không đúng định dạng");
        }

        if (code == null || code.isBlank()) {
            ValidationUtil.addError(errors, "code", "Mã xác thực không được để trống");
        } else if (!ValidationUtil.isValidOtp(code)) {
            ValidationUtil.addError(errors, "code", "Mã xác thực phải là 6 chữ số");
        }

        if (type == null || type.isBlank()) {
            ValidationUtil.addError(errors, "type", "Loại xác thực không được để trống");
        } else {
            String upper = type.trim().toUpperCase();
            if (!"REGISTER".equals(upper) && !"FORGOT_PASSWORD".equals(upper)) {
                ValidationUtil.addError(errors, "type", "Loại xác thực chỉ chấp nhận REGISTER hoặc FORGOT_PASSWORD");
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

    public String getCode() {
        return code != null ? code.trim() : null;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getType() {
        return type != null ? type.trim().toUpperCase() : null;
    }

    public void setType(String type) {
        this.type = type;
    }
}
