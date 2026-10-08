package com.fashionshop.util;
import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {
    // Mã hóa mật khẩu khi Register / Đổi mật khẩu
    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));
    }
    // Kiểm tra mật khẩu khi Login
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}