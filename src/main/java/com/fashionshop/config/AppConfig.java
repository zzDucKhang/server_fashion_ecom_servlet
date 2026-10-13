package com.fashionshop.config;

import io.github.cdimascio.dotenv.Dotenv;

import java.util.logging.Logger;

/**
 * Production-grade Application Configuration.
 * Loads variables strictly from Environment or .env file with fail-safe parsing.
 */
public class AppConfig {
    private static final Logger logger = Logger.getLogger(AppConfig.class.getName());
    private static final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

    // Database Configurations
    public static final String DB_URL = resolveDbUrl();
    public static final String DB_USER = get("DB_USER", "postgres");
    public static final String DB_PASSWORD = get("DB_PASSWORD", "");

    // Client Frontend & CORS
    public static final String CLIENT_CORS_URL = get("CLIENT_CORS_URL", "http://localhost:3000");

    // JWT Configurations
    public static final String ACCESS_TOKEN_SECRET = get("ACCESS_TOKEN_SECRET", "access_secret_key_change_in_production_min_256_bits");
    public static final long ACCESS_TOKEN_EXPIRATION = parseDuration(get("ACCESS_TOKEN_EXPIRATION", get("ACCESS_TOKEN_EXPIRES_IN", "3600000")));
    public static final String REFRESH_TOKEN_SECRET = get("REFRESH_TOKEN_SECRET", "refresh_secret_key_change_in_production_min_256_bits");
    public static final long REFRESH_TOKEN_EXPIRATION = parseDuration(get("REFRESH_TOKEN_EXPIRATION", get("REFRESH_TOKEN_EXPIRES_IN", "86400000")));

    // Server & Upload Configurations
    public static final int SERVER_PORT = Integer.parseInt(get("PORT", get("SERVER_PORT", "5000")));
    public static final String SERVER_BASE_URL = get("SERVER_BASE_URL", "http://localhost:" + SERVER_PORT);
    public static final String UPLOAD_DIR = get("UPLOAD_DIR", "uploads");

    // Redis Caching
    public static final String REDIS_URL = get("REDIS_URL", "redis://localhost:6379");

    // SePay Payment Gateway
    public static final String SEPAY_QR_BASE_URL = get("SEPAY_QR_BASE_URL", "https://qr.sepay.vn/img");
    public static final String SEPAY_ACCOUNT_NUMBER = get("SEPAY_ACCOUNT_NUMBER", "");
    public static final String SEPAY_BANK_CODE = get("SEPAY_BANK_CODE", "MBBank");
    public static final String SEPAY_WEBHOOK_SECRET = get("SEPAY_WEBHOOK_SECRET", "");

    // S3 Cloud Storage (Supabase Storage / AWS S3)
    public static final String S3_ENDPOINT = get("S3_ENDPOINT", "");
    public static final String S3_REGION = get("S3_REGION", "ap-southeast-1");
    public static final String S3_ACCESS_KEY = get("S3_ACCESS_KEY", "");
    public static final String S3_SECRET_KEY = get("S3_SECRET_KEY", "");
    public static final String S3_BUCKET_NAME = get("S3_BUCKET_NAME", "fashion-uploads");

    // Email Configuration (Brevo REST API + Gmail SMTP fallback)
    public static final String BREVO_API_KEY = get("BREVO_API_KEY", "");
    public static final String BREVO_SENDER_EMAIL = get("BREVO_SENDER_EMAIL", get("MAIL_USER", ""));
    public static final String BREVO_SENDER_NAME = get("BREVO_SENDER_NAME", "Fashion Store");
    public static final String MAIL_USER = get("MAIL_USER", "");
    public static final String MAIL_PASS = get("MAIL_PASS", "");

    public static String get(String key, String defaultValue) {
        String val = System.getenv(key);
        if (val == null || val.isBlank()) {
            val = dotenv.get(key);
        }
        return (val != null && !val.isBlank()) ? val.trim() : defaultValue;
    }

    public static String get(String key) {
        return get(key, null);
    }

    private static String resolveDbUrl() {
        String url = get("DATABASE_URL", get("DB_URL", "jdbc:postgresql://localhost:5432/fashion_ecommerce"));
        if (url != null && url.startsWith("postgresql://")) {
            url = "jdbc:" + url;
        }
        return url;
    }

    private static long parseDuration(String value) {
        if (value == null || value.isBlank()) {
            return 3600000L;
        }
        String clean = value.trim().toLowerCase();
        try {
            if (clean.endsWith("d")) {
                long days = Long.parseLong(clean.substring(0, clean.length() - 1));
                return days * 24 * 60 * 60 * 1000L;
            } else if (clean.endsWith("h")) {
                long hours = Long.parseLong(clean.substring(0, clean.length() - 1));
                return hours * 60 * 60 * 1000L;
            } else if (clean.endsWith("m")) {
                long minutes = Long.parseLong(clean.substring(0, clean.length() - 1));
                return minutes * 60 * 1000L;
            } else if (clean.endsWith("s")) {
                long seconds = Long.parseLong(clean.substring(0, clean.length() - 1));
                return seconds * 1000L;
            }
            return Long.parseLong(clean);
        } catch (NumberFormatException e) {
            logger.warning("Không thể parse cấu hình thời gian: " + value + ", sử dụng mặc định 1h");
            return 3600000L;
        }
    }
}