package com.fashionshop.common.util;

import com.fashionshop.common.exception.ValidationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public final class ValidationUtil {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^(0[35789])[0-9]{8}$"
    );

    private static final Pattern OTP_PATTERN = Pattern.compile(
            "^\\d{6}$"
    );

    private ValidationUtil() {}

    public static void validate(Validatable dto) {
        if (dto == null) {
            throw new ValidationException(Map.of("body", List.of("Dữ liệu đầu vào không được để trống")));
        }
        Map<String, List<String>> errors = dto.validate();
        if (errors != null && !errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

    public static void addError(Map<String, List<String>> errors, String field, String message) {
        errors.computeIfAbsent(field, k -> new ArrayList<>()).add(message);
    }

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone.trim()).matches();
    }

    public static boolean isValidOtp(String otp) {
        return otp != null && OTP_PATTERN.matcher(otp.trim()).matches();
    }

    public static void validatePasswordComplexity(String password, String fieldName, Map<String, List<String>> errors) {
        if (password == null || password.isBlank()) {
            addError(errors, fieldName, "Mật khẩu không được để trống");
            return;
        }
        if (password.length() < 8) {
            addError(errors, fieldName, "Mật khẩu tối thiểu 8 ký tự");
        }
        if (password.length() > 72) {
            addError(errors, fieldName, "Mật khẩu tối đa 72 ký tự");
        }
        if (!password.matches(".*[A-Z].*")) {
            addError(errors, fieldName, "Mật khẩu cần ít nhất 1 chữ in hoa");
        }
        if (!password.matches(".*[a-z].*")) {
            addError(errors, fieldName, "Mật khẩu cần ít nhất 1 chữ in thường");
        }
        if (!password.matches(".*[0-9].*")) {
            addError(errors, fieldName, "Mật khẩu cần ít nhất 1 chữ số");
        }
        if (!password.matches(".*[^A-Za-z0-9].*")) {
            addError(errors, fieldName, "Mật khẩu cần ít nhất 1 ký tự đặc biệt");
        }
    }
}
