package com.fashionshop.config;

import io.github.cdimascio.dotenv.Dotenv;

public class AppConfig {
    private static final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

    // Database Configurations
    public static final String DB_URL = get("DB_URL", "jdbc:postgresql://localhost:5432/fashion_ecom_web");
    public static final String DB_USER = get("DB_USER", "postgres");
    public static final String DB_PASSWORD = get("DB_PASSWORD", "123");

    // Client Frontend (CORS)
    public static final String CLIENT_CORS_URL = get("CLIENT_CORS_URL", "http://localhost:3000");

    // JWT Configurations
    public static final String ACCESS_TOKEN_SECRET = get("ACCESS_TOKEN_SECRET", "default_secret_key_minimum_256_bits_length");
    public static final long ACCESS_TOKEN_EXPIRATION = Long.parseLong(get("ACCESS_TOKEN_EXPIRATION", "3600000"));
    public static final String REFRESH_TOKEN_SECRET = get("REFRESH_TOKEN_SECRET", "default_refresh_secret_key_minimum_256_bits");
    public static final long REFRESH_TOKEN_EXPIRATION = Long.parseLong(get("REFRESH_TOKEN_EXPIRATION", "86400000"));

    // Redis & Payment Webhooks
    public static final String REDIS_URL = get("REDIS_URL", "redis://localhost:6379");
    public static final String SEPAY_WEBHOOK_SECRET = get("SEPAY_WEBHOOK_SECRET", "");
    public static final String SEPAY_ACCOUNT_NUMBER = get("SEPAY_ACCOUNT_NUMBER", "");

    public static String get(String key, String defaultValue) {
        String val = System.getenv(key);
        if (val == null || val.isBlank()) {
            val = dotenv.get(key);
        }
        return (val != null && !val.isBlank()) ? val : defaultValue;
    }
}