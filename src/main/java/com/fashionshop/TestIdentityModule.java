package com.fashionshop;

import com.fashionshop.common.exception.ValidationException;
import com.fashionshop.common.response.ApiResponse;
import com.fashionshop.common.response.PagedResponse;
import com.fashionshop.common.util.JwtUtil;
import com.fashionshop.common.util.PasswordUtil;
import com.fashionshop.common.util.ValidationUtil;
import com.fashionshop.presentation.dto.identity.*;
import io.jsonwebtoken.Claims;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class TestIdentityModule {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("  IDENTITY & SECURITY MODULE VERIFICATION SUITE   ");
        System.out.println("==================================================");

        int passed = 0;
        int failed = 0;

        // 1. PasswordUtil
        try {
            System.out.print("[TEST 1] PasswordUtil hashing & BCrypt verification: ");
            String raw = "StrongPass123@";
            String hash = PasswordUtil.hashPassword(raw);
            assert !raw.equals(hash) : "Hash must not equal raw";
            assert PasswordUtil.checkPassword(raw, hash) : "Matching password must return true";
            assert !PasswordUtil.checkPassword("WrongPass123@", hash) : "Wrong password must return false";
            System.out.println("PASSED");
            passed++;
        } catch (Throwable t) {
            System.out.println("FAILED: " + t.getMessage());
            failed++;
        }

        // 2. JwtUtil Access Token
        try {
            System.out.print("[TEST 2] JwtUtil Access Token with claims: ");
            String token = JwtUtil.generateAccessToken(42, 1, "ADMIN", "admin@fashion.com");
            Claims claims = JwtUtil.verifyAccessToken(token);
            assert "42".equals(claims.getSubject()) : "Subject mismatch";
            assert Integer.valueOf(42).equals(claims.get("userId", Integer.class)) : "userId claim mismatch";
            assert Integer.valueOf(1).equals(claims.get("roleId", Integer.class)) : "roleId claim mismatch";
            assert "ADMIN".equals(claims.get("roleCode", String.class)) : "roleCode claim mismatch";
            assert "admin@fashion.com".equals(claims.get("email", String.class)) : "email claim mismatch";
            System.out.println("PASSED");
            passed++;
        } catch (Throwable t) {
            System.out.println("FAILED: " + t.getMessage());
            failed++;
        }

        // 3. JwtUtil Refresh & Reset Tokens
        try {
            System.out.print("[TEST 3] JwtUtil Refresh & Reset Tokens: ");
            String refreshToken = JwtUtil.generateRefreshToken(99, "customer@fashion.com", "CUSTOMER");
            Claims refreshClaims = JwtUtil.verifyRefreshToken(refreshToken);
            assert "CUSTOMER".equals(refreshClaims.get("roleCode", String.class)) : "roleCode mismatch";

            String resetToken = JwtUtil.generateResetToken("user@fashion.com");
            Claims resetClaims = JwtUtil.verifyResetToken(resetToken);
            assert "RESET_PASSWORD".equals(resetClaims.get("purpose", String.class)) : "purpose mismatch";
            assert "user@fashion.com".equals(resetClaims.get("email", String.class)) : "email mismatch";

            long remaining = JwtUtil.getRemainingMillis(resetClaims);
            assert remaining > 0 : "Remaining millis must be positive";
            System.out.println("PASSED");
            passed++;
        } catch (Throwable t) {
            System.out.println("FAILED: " + t.getMessage());
            failed++;
        }

        // 4. RegisterDTO Validation (Valid)
        try {
            System.out.print("[TEST 4] RegisterDTO validation (Valid payload): ");
            RegisterDTO dto = new RegisterDTO("user@fashion.com", "Password123@", "Nguyễn Văn A", "0901234567");
            ValidationUtil.validate(dto);
            System.out.println("PASSED");
            passed++;
        } catch (Throwable t) {
            System.out.println("FAILED: " + t.getMessage());
            failed++;
        }

        // 5. RegisterDTO Validation (Invalid - weak password, bad email)
        try {
            System.out.print("[TEST 5] RegisterDTO validation (Invalid payload): ");
            RegisterDTO dto = new RegisterDTO("invalid-email", "simple", "", "");
            boolean caught = false;
            try {
                ValidationUtil.validate(dto);
            } catch (ValidationException ve) {
                caught = true;
                Map<String, List<String>> errors = ve.getErrors();
                assert errors.containsKey("email") : "Missing email error";
                assert errors.containsKey("password") : "Missing password error";
                assert errors.containsKey("fullName") : "Missing fullName error";
            }
            assert caught : "ValidationException was not thrown";
            System.out.println("PASSED");
            passed++;
        } catch (Throwable t) {
            System.out.println("FAILED: " + t.getMessage());
            failed++;
        }

        // 6. ResetPasswordDTO Cross-field Check
        try {
            System.out.print("[TEST 6] ResetPasswordDTO cross-field password matching: ");
            ResetPasswordDTO mismatchDto = new ResetPasswordDTO("token123", "Pass1234@", "DifferentPass123@");
            boolean caughtMismatch = false;
            try {
                ValidationUtil.validate(mismatchDto);
            } catch (ValidationException ve) {
                caughtMismatch = true;
                assert ve.getErrors().containsKey("confirmPassword") : "Expected confirmPassword error";
            }
            assert caughtMismatch : "Should fail when passwords do not match";

            ResetPasswordDTO matchDto = new ResetPasswordDTO("token123", "Pass1234@", "Pass1234@");
            ValidationUtil.validate(matchDto);
            System.out.println("PASSED");
            passed++;
        } catch (Throwable t) {
            System.out.println("FAILED: " + t.getMessage());
            failed++;
        }

        // 7. VerifyOtpDTO Validation
        try {
            System.out.print("[TEST 7] VerifyOtpDTO validation (6-digit check & type): ");
            VerifyOtpDTO invalidOtp = new VerifyOtpDTO("a@b.com", "12345", "INVALID_TYPE");
            boolean caught = false;
            try {
                ValidationUtil.validate(invalidOtp);
            } catch (ValidationException ve) {
                caught = true;
                assert ve.getErrors().containsKey("code") : "Expected code error";
                assert ve.getErrors().containsKey("type") : "Expected type error";
            }
            assert caught : "Expected OTP validation error";

            VerifyOtpDTO validOtp = new VerifyOtpDTO("a@b.com", "123456", "REGISTER");
            ValidationUtil.validate(validOtp);
            System.out.println("PASSED");
            passed++;
        } catch (Throwable t) {
            System.out.println("FAILED: " + t.getMessage());
            failed++;
        }

        // 8. CreateAddressDTO & UpdateAddressDTO Validation
        try {
            System.out.print("[TEST 8] Address DTOs validation: ");
            CreateAddressDTO addrDto = new CreateAddressDTO("Người Nhận", "0901234567", "TP.HCM", "Quận 1", "Phường 1", "123 Đường A", true);
            ValidationUtil.validate(addrDto);

            UpdateAddressDTO updateAddrDto = new UpdateAddressDTO("Người Nhận Mới", null, null, null, null, null, false);
            ValidationUtil.validate(updateAddrDto);
            System.out.println("PASSED");
            passed++;
        } catch (Throwable t) {
            System.out.println("FAILED: " + t.getMessage());
            failed++;
        }

        // 9. CreateRoleDTO & CreatePermissionDTO Validation
        try {
            System.out.print("[TEST 9] Role & Permission DTOs validation: ");
            CreateRoleDTO roleDto = new CreateRoleDTO("Quản Trị", "admin_role", Set.of(1, 2, 3));
            ValidationUtil.validate(roleDto);
            assert "ADMIN_ROLE".equals(roleDto.getCode()) : "Role code should be uppercase";

            CreatePermissionDTO permDto = new CreatePermissionDTO("order:cancel", "orders", "Hủy đơn hàng");
            ValidationUtil.validate(permDto);
            assert "ORDER:CANCEL".equals(permDto.getCode()) : "Permission code should be uppercase";
            System.out.println("PASSED");
            passed++;
        } catch (Throwable t) {
            System.out.println("FAILED: " + t.getMessage());
            failed++;
        }

        // 10. Response Wrappers
        try {
            System.out.print("[TEST 10] ApiResponse and PagedResponse wrappers: ");
            ApiResponse<String> okRes = ApiResponse.ok("test-data");
            assert okRes.isSuccess() && okRes.getStatusCode() == 200 : "ApiResponse ok failed";

            PagedResponse<String> paged = new PagedResponse<>(List.of("a", "b"), 25, 1, 10);
            assert paged.getTotalPages() == 3 : "Total pages should be 3 for 25 items with limit 10";
            System.out.println("PASSED");
            passed++;
        } catch (Throwable t) {
            System.out.println("FAILED: " + t.getMessage());
            failed++;
        }

        System.out.println("==================================================");
        System.out.printf("  SUMMARY: %d PASSED, %d FAILED%n", passed, failed);
        System.out.println("==================================================");

        if (failed > 0) {
            System.exit(1);
        }
    }
}
