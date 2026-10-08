package com.fashionshop.config;
import com.fashionshop.util.JPAUtil;
import com.fashionshop.util.RedisUtil;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class AppContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println(">>> [SYSTEM] Server starting: Initializing JPA & Redis...");
        // Khởi động trước kết nối để tránh lag ở request đầu tiên
        JPAUtil.getEntityManager().close();
        RedisUtil.getClient();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println(">>> [SYSTEM] Server stopping: Closing JPA & Redis connections...");
        JPAUtil.close();
        RedisUtil.shutdown();
    }
}