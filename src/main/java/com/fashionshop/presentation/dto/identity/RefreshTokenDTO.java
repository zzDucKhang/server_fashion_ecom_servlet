package com.fashionshop.presentation.dto.identity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fashionshop.common.util.Validatable;
import com.fashionshop.common.util.ValidationUtil;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RefreshTokenDTO implements Validatable {

    private String refreshToken;

    public RefreshTokenDTO() {}

    public RefreshTokenDTO(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    @Override
    public Map<String, List<String>> validate() {
        Map<String, List<String>> errors = new LinkedHashMap<>();

        if (refreshToken == null || refreshToken.isBlank()) {
            ValidationUtil.addError(errors, "refreshToken", "Refresh token là bắt buộc");
        }

        return errors;
    }

    public String getRefreshToken() {
        return refreshToken != null ? refreshToken.trim() : null;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
