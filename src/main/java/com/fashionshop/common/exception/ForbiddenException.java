package com.fashionshop.common.exception;

public class ForbiddenException extends AppException {
    public ForbiddenException(String message) {
        super(message, 403);
    }

    public ForbiddenException() {
        super("Bạn không có quyền thực hiện hành động này", 403);
    }
}