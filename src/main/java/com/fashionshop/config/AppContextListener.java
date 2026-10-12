package com.fashionshop.config;

import com.fashionshop.common.util.JPAUtil;
import com.fashionshop.common.util.RedisUtil;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.util.logging.Logger;

@WebListener
public class AppContextListener implements ServletContextListener {
    private static final Logger logger = Logger.getLogger(AppContextListener.class.getName());

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        logger.info("[SYSTEM] Khởi tạo Servlet Context: Kết nối JPA & Redis...");
        try {
            JPAUtil.getEntityManager().close();
            RedisUtil.getClient();
            logger.info("[SYSTEM] Sẵn sàng phục vụ yêu cầu.");
        } catch (Exception e) {
            logger.warning("[SYSTEM] Cảnh báo kết nối ban đầu: " + e.getMessage());
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logger.info("[SYSTEM] Dừng Servlet Context: Đóng kết nối JPA & Redis...");
        JPAUtil.close();
        RedisUtil.shutdown();
    }
}