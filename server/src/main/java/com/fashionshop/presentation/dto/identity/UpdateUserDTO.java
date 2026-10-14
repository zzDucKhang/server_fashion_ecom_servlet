package com.fashionshop.presentation.dto.identity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fashionshop.common.util.Validatable;
import com.fashionshop.common.util.ValidationUtil;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateUserDTO implements Validatable {

    private String fullName;
    private String phoneNumber;
    private String avatarUrl;

    public UpdateUserDTO() {}

    public UpdateUserDTO(String fullName, String phoneNumber, String avatarUrl) {
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.avatarUrl = avatarUrl;
    }

    @Override
    public Map<String, List<String>> validate() {
        Map<String, List<String>> errors = new LinkedHashMap<>();

        if (fullName != null) {
            String trimmed = fullName.trim();
            if (trimmed.isEmpty() || trimmed.length() > 100) {
                ValidationUtil.addError(errors, "fullName", "Họ và tên từ 1 đến 100 ký tự");
            }
        }

        if (phoneNumber != null && !phoneNumber.isBlank()) {
            String trimmed = phoneNumber.trim();
            if (trimmed.length() > 50) {
                ValidationUtil.addError(errors, "phoneNumber", "Số điện thoại tối đa 50 ký tự");
            } else if (!ValidationUtil.isValidPhone(trimmed)) {
                ValidationUtil.addError(errors, "phoneNumber", "Số điện thoại không đúng định dạng");
            }
        }

        if (avatarUrl != null && !avatarUrl.isBlank()) {
            String trimmed = avatarUrl.trim();
            if (trimmed.length() > 1000) {
                ValidationUtil.addError(errors, "avatarUrl", "Avatar tối đa 1000 ký tự");
            } else {
                try {
                    URI uri = URI.create(trimmed);
                    if (uri.getScheme() == null || (!"http".equalsIgnoreCase(uri.getScheme()) && !"https".equalsIgnoreCase(uri.getScheme()))) {
                        ValidationUtil.addError(errors, "avatarUrl", "Avatar không hợp lệ");
                    }
                } catch (Exception e) {
                    ValidationUtil.addError(errors, "avatarUrl", "Avatar không hợp lệ");
                }
            }
        }

        return errors;
    }

    public String getFullName() {
        return fullName != null ? fullName.trim() : null;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber != null ? phoneNumber.trim() : null;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAvatarUrl() {
        return avatarUrl != null ? avatarUrl.trim() : null;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}
