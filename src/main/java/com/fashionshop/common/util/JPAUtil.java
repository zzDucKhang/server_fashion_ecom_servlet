package com.fashionshop.common.util;

import com.fashionshop.config.AppConfig;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.HashMap;
import java.util.Map;

public class JPAUtil {
    private static final EntityManagerFactory emf;

    static {
        try {
            Map<String, Object> props = new HashMap<>();
            props.put("jakarta.persistence.jdbc.url", AppConfig.DB_URL);
            props.put("jakarta.persistence.jdbc.user", AppConfig.DB_USER);
            props.put("jakarta.persistence.jdbc.password", AppConfig.DB_PASSWORD);

            System.out.println(">>> [JPAUtil] Đang kết nối DB: " + AppConfig.DB_URL);
            System.out.println(">>> [JPAUtil] User DB: " + AppConfig.DB_USER);

            emf = Persistence.createEntityManagerFactory("FashionShopPU", props);
        } catch (Throwable ex) {
            System.err.println("❌ LỖI KHỞI TẠO ENTITY MANAGER FACTORY CHI TIẾT:");
            ex.printStackTrace(); // Bắt buộc phải có dòng này để thấy nguyên nhân gốc
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public static void close() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}