package com.fashionshop.presentation.dto.identity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fashionshop.common.util.Validatable;
import com.fashionshop.common.util.ValidationUtil;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RegisterDTO implements Validatable {

    private String email;
    private String password;
    private String fullName;
    private String phoneNumber;

    public RegisterDTO() {}

    public RegisterDTO(String email, String password, String fullName, String phoneNumber) {
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
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

        ValidationUtil.validatePasswordComplexity(password, "password", errors);

        if (fullName == null || fullName.isBlank()) {
            ValidationUtil.addError(errors, "fullName", "Họ và tên không được để trống");
        } else if (fullName.trim().length() > 100) {
            ValidationUtil.addError(errors, "fullName", "Họ và tên từ 1 đến 100 ký tự");
        }

        if (phoneNumber != null && !phoneNumber.isBlank()) {
            if (phoneNumber.trim().length() > 50) {
                ValidationUtil.addError(errors, "phoneNumber", "Số điện thoại tối đa 50 ký tự");
            } else if (!ValidationUtil.isValidPhone(phoneNumber)) {
                ValidationUtil.addError(errors, "phoneNumber", "Số điện thoại không đúng định dạng");
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName != null ? fullName.trim() : null;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber != null ? phoneNumber.trim() : "";
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
