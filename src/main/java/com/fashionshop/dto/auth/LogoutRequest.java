package com.fashionshop.dto.auth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Data Transfer Object tùy chọn nhận token đăng xuất từ body JSON.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LogoutRequest {

    private String refreshToken;

    public LogoutRequest() {
    }

    public LogoutRequest(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
