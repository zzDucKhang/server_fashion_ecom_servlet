package com.fashionshop.common.util;


import com.fashionshop.config.AppConfig;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RedisUtil {
    private static final Logger logger = Logger.getLogger(RedisUtil.class.getName());
    private static RedissonClient redisson;

    static {
        try {
            Config config = new Config();
            config.useSingleServer()
                    .setAddress(AppConfig.REDIS_URL)
                    .setConnectionMinimumIdleSize(2)
                    .setConnectionPoolSize(10)
                    .setConnectTimeout(3000)
                    .setTimeout(3000);
            redisson = Redisson.create(config);
            logger.info("Redisson connected successfully to " + AppConfig.REDIS_URL);
        } catch (Throwable e) {
            logger.log(Level.WARNING, "Failed to connect to Redis at " + AppConfig.REDIS_URL + " - running with Redis disabled: " + e.getMessage());
            redisson = null;
        }
    }

    public static RedissonClient getClient() {
        return redisson;
    }

    public static boolean isAvailable() {
        return redisson != null && !redisson.isShutdown();
    }

    public static void shutdown() {
        if (redisson != null && !redisson.isShutdown()) {
            try {
                redisson.shutdown();
            } catch (Exception ignored) {
            }
        }
    }
}
