package com.fashionshop.common.exception;

public class UnauthorizedException extends AppException {
    public UnauthorizedException(String message) {
        super(message, 401);
    }

    public UnauthorizedException() {
        super("Chưa xác thực danh tính hoặc phiên đăng nhập hết hạn", 401);
    }
}