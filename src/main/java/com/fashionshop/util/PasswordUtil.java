package com.fashionshop.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utility class xử lý băm mật khẩu và kiểm tra mật khẩu sử dụng thuật toán BCrypt.
 */
public final class PasswordUtil {

    private static final int BCRYPT_WORK_FACTOR = 12;

    // Ngăn chặn khởi tạo instance
    private PasswordUtil() {
        throw new UnsupportedOperationException("PasswordUtil is a utility class and cannot be instantiated");
    }

    /**
     * Băm mật khẩu dạng plain text sử dụng BCrypt với cost factor = 12.
     *
     * @param plainText Mật khẩu người dùng nhập
     * @return Chuỗi băm BCrypt an toàn
     * @throws IllegalArgumentException nếu mật khẩu rỗng hoặc null
     */
    public static String hashPassword(String plainText) {
        if (plainText == null || plainText.isBlank()) {
            throw new IllegalArgumentException("Mật khẩu không được để trống.");
        }
        return BCrypt.hashpw(plainText, BCrypt.gensalt(BCRYPT_WORK_FACTOR));
    }

    /**
     * Kiểm tra tính hợp lệ của mật khẩu plain text so với mật khẩu đã băm trong database.
     *
     * @param plainText Mật khẩu chưa mã hóa người dùng nhập vào
     * @param hashed Mật khẩu đã băm lưu trong database
     * @return true nếu mật khẩu trùng khớp, ngược lại false
     */
    public static boolean checkPassword(String plainText, String hashed) {
        if (plainText == null || hashed == null || plainText.isBlank() || hashed.isBlank()) {
            return false;
        }
        try {
            return BCrypt.checkpw(plainText, hashed);
        } catch (IllegalArgumentException ex) {
            // Trường hợp hash format không hợp lệ (không phải chuẩn BCrypt)
            return false;
        }
    }
}