package com.fashionshop.common.exception;

public class BadRequestException extends AppException {
    public BadRequestException(String message) {
        super(message, 400);
    }

    public BadRequestException() {
        super("Dữ liệu đầu vào không hợp lệ", 400);
    }
}
