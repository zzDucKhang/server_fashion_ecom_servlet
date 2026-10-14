package com.fashionshop.presentation.websocket;

import com.fashionshop.common.util.RedisUtil;
import org.redisson.api.RBatch;
import org.redisson.api.RBucket;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;

import java.time.Duration;
import java.util.Map;

public class WsSessionManager {
    private static final String WS_SESSION_PREFIX = "ws:sessions:user:";
    private static final String WS_SOCKET_PREFIX = "ws:sessions:socket:";
    private static final long WS_SESSION_TTL_SECONDS = 60;

    public static void upsertSocketSession(String socketId, int userId) {
        RedissonClient redisson = RedisUtil.getClient();
        String userKey = WS_SESSION_PREFIX + userId;
        String socketKey = WS_SOCKET_PREFIX + socketId;
        String now = String.valueOf(System.currentTimeMillis());

        RBatch batch = redisson.createBatch();
        batch.getMap(userKey).putAsync(socketId, now);
        batch.getMap(userKey).expireAsync(Duration.ofSeconds(WS_SESSION_TTL_SECONDS));
        batch.getBucket(socketKey).setAsync(String.valueOf(userId), Duration.ofSeconds(WS_SESSION_TTL_SECONDS));
        batch.execute();
    }

    public static boolean extendSocketSession(String socketId) {
        RedissonClient redisson = RedisUtil.getClient();
        String socketKey = WS_SOCKET_PREFIX + socketId;
        RBucket<String> socketBucket = redisson.getBucket(socketKey);

        String userIdStr = socketBucket.get();
        if (userIdStr == null) return false;

        String userKey = WS_SESSION_PREFIX + userIdStr;
        String now = String.valueOf(System.currentTimeMillis());

        RBatch batch = redisson.createBatch();
        batch.getBucket(socketKey).expireAsync(Duration.ofSeconds(WS_SESSION_TTL_SECONDS));
        batch.getMap(userKey).expireAsync(Duration.ofSeconds(WS_SESSION_TTL_SECONDS));
        batch.getMap(userKey).putAsync(socketId, now);
        batch.execute();

        return true;
    }

    public static void removeSocketSession(String socketId) {
        RedissonClient redisson = RedisUtil.getClient();
        String socketKey = WS_SOCKET_PREFIX + socketId;
        RBucket<String> socketBucket = redisson.getBucket(socketKey);

        String userIdStr = socketBucket.get();
        if (userIdStr != null) {
            String userKey = WS_SESSION_PREFIX + userIdStr;
            RBatch batch = redisson.createBatch();
            batch.getMap(userKey).removeAsync(socketId);
            batch.getBucket(socketKey).deleteAsync();
            batch.execute();
        }
    }

    public static boolean isUserOnline(int userId) {
        RMap<String, String> userSessions = RedisUtil.getClient().getMap(WS_SESSION_PREFIX + userId);
        return !userSessions.isEmpty();
    }

    public static Map<String, String> getSocketSessionsByUser(int userId) {
        return RedisUtil.getClient().getMap(WS_SESSION_PREFIX + userId);
    }
}