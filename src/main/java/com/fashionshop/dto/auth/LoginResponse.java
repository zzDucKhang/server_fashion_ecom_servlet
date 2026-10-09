package com.fashionshop.dto.auth;

import com.fashionshop.business.entity.User;

/**
 * Data Transfer Object trả về kết quả đăng nhập thành công chứa token và thông tin User an toàn.
 */
public class LoginResponse {

    private String token;
    private UserResponse user;

    public LoginResponse() {
    }

    public LoginResponse(String token, UserResponse user) {
        this.token = token;
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserResponse getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = UserResponse.fromEntity(user);
    }

    public void setUser(UserResponse user) {
        this.user = user;
    }
}
