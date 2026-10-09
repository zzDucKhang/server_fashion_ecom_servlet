package com.fashionshop.dto.auth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Data Transfer Object cho yêu cầu đăng nhập.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoginRequest {

    private String email;
    private String password;

    public LoginRequest() {
    }

    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
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
