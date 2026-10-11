package com.fashionshop.common.util;


import com.fashionshop.config.AppConfig;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

public class RedisUtil {
    private static final RedissonClient redisson;

    static {
        Config config = new Config();
        config.useSingleServer()
                .setAddress(AppConfig.REDIS_URL)
                .setConnectionMinimumIdleSize(5)
                .setConnectionPoolSize(20);
        redisson = Redisson.create(config);
    }

    public static RedissonClient getClient() {
        return redisson;
    }

    public static void shutdown() {
        if (redisson != null && !redisson.isShutdown()) {
            redisson.shutdown();
        }
    }
}
