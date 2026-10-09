package com.fashionshop.util;

import com.fashionshop.config.AppConfig;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class providing thread-safe Singleton access to {@link EntityManagerFactory}
 * and {@link EntityManager} instances.
 */
public final class JPAUtil {

    private static final Logger LOGGER = Logger.getLogger(JPAUtil.class.getName());
    private static final String PERSISTENCE_UNIT_NAME = "FashionShopPU";
    private static final EntityManagerFactory emf;

    // Ngăn chặn khởi tạo instance (Utility Class / Singleton Pattern)
    private JPAUtil() {
        throw new UnsupportedOperationException("JPAUtil is a utility class and cannot be instantiated");
    }

    static {
        try {
            LOGGER.info("Dang khoi tao EntityManagerFactory cho Persistence Unit: " + PERSISTENCE_UNIT_NAME);

            // Nạp cấu hình Database động từ AppConfig (.env)
            Map<String, Object> props = new HashMap<>();
            props.put("jakarta.persistence.jdbc.url", AppConfig.DB_URL);
            props.put("jakarta.persistence.jdbc.user", AppConfig.DB_USER);
            props.put("jakarta.persistence.jdbc.password", AppConfig.DB_PASSWORD);

            emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME, props);
            LOGGER.info("EntityManagerFactory da khoi tao thanh cong.");
        } catch (Throwable ex) {
            LOGGER.log(Level.SEVERE, "Loi nghiem trong khi khoi tao EntityManagerFactory: " + ex.getMessage(), ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    /**
     * Lấy Singleton EntityManagerFactory instance.
     *
     * @return active EntityManagerFactory
     * @throws IllegalStateException nếu EntityManagerFactory đã bị đóng hoặc chưa khởi tạo
     */
    public static EntityManagerFactory getEntityManagerFactory() {
        if (emf == null || !emf.isOpen()) {
            throw new IllegalStateException("EntityManagerFactory chua duoc khoi tao hoac da bi dong.");
        }
        return emf;
    }

    /**
     * Khởi tạo và trả về một EntityManager mới cho từng tác vụ / transaction.
     *
     * @return EntityManager instance mới
     */
    public static EntityManager getEntityManager() {
        return getEntityManagerFactory().createEntityManager();
    }

    /**
     * Đóng EntityManagerFactory an toàn và đồng bộ (thread-safe),
     * giải phóng toàn bộ connection pool (HikariCP) để chống Connection Leak khi shutdown/redeploy.
     */
    public static synchronized void close() {
        if (emf != null && emf.isOpen()) {
            try {
                LOGGER.info("Dang dong EntityManagerFactory va giai phong Connection Pool...");
                emf.close();
                LOGGER.info("EntityManagerFactory da duoc dong an toan.");
            } catch (Exception ex) {
                LOGGER.log(Level.WARNING, "Loi trong qua trinh dong EntityManagerFactory: " + ex.getMessage(), ex);
            }
        }
    }
}