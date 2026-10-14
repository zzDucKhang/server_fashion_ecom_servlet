package com.fashionshop.common.exception;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ValidationException extends AppException {
    private final Map<String, List<String>> errors;

    public ValidationException(Map<String, List<String>> errors) {
        super("Dữ liệu đầu vào không hợp lệ", 422);
        this.errors = errors != null ? errors : Collections.emptyMap();
    }

    public ValidationException(String field, String message) {
        super("Dữ liệu đầu vào không hợp lệ", 422);
        this.errors = Map.of(field, List.of(message));
    }

    public Map<String, List<String>> getErrors() {
        return errors;
    }
}
