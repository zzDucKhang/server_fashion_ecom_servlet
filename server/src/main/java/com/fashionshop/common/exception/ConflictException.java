package com.fashionshop.common.exception;

public class ConflictException extends AppException {
    public ConflictException(String message) {
        super(message, 409);
    }

    public ConflictException() {
        super("Dữ liệu đã tồn tại hoặc xảy ra xung đột", 409);
    }
}