package com.fashionshop.common.exception;

public class NotFoundException extends AppException {
    public NotFoundException(String message) {
        super(message, 404);
    }

    public NotFoundException() {
        super("Bản ghi không tồn tại trong hệ thống", 404);
    }
}
