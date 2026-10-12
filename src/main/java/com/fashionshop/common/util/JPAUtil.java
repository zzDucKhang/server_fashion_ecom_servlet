package com.fashionshop.common.util;

import com.fashionshop.config.AppConfig;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JPAUtil {
    private static final Logger logger = Logger.getLogger(JPAUtil.class.getName());
    private static final EntityManagerFactory emf;

    static {
        try {
            Map<String, Object> props = new HashMap<>();
            props.put("jakarta.persistence.jdbc.url", AppConfig.DB_URL);
            props.put("jakarta.persistence.jdbc.user", AppConfig.DB_USER);
            props.put("jakarta.persistence.jdbc.password", AppConfig.DB_PASSWORD);

            emf = Persistence.createEntityManagerFactory("FashionShopPU", props);
        } catch (Throwable ex) {
            logger.log(Level.SEVERE, "Lỗi khởi tạo EntityManagerFactory", ex);
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