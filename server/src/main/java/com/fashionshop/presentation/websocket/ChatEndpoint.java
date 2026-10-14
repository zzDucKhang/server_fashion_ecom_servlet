package com.fashionshop.presentation.websocket;

import com.fashionshop.business.entity.Message;
import com.fashionshop.common.util.JsonUtil;
import com.fashionshop.common.util.JwtUtil;
import com.fashionshop.dataaccess.dao.loyalty.ChatDAO;

import io.jsonwebtoken.Claims;
import jakarta.websocket.CloseReason;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

@ServerEndpoint("/ws/chat")
public class ChatEndpoint {
    private static final Logger logger = Logger.getLogger(ChatEndpoint.class.getName());
    private static final Map<String, Session> userSessions = new ConcurrentHashMap<>();
    private static final Map<String, Session> shopSessions = new ConcurrentHashMap<>();
    private static final ChatDAO chatDAO = new ChatDAO();

    private Integer userId;
    private String roleCode;
    private String socketId;

    @OnOpen
    public void onOpen(Session session) throws IOException {
        this.socketId = session.getId();
        String query = session.getRequestURI().getQuery();

        // Trích xuất token từ URL: ws://host/ws/chat?token=xxx
        String token = null;
        if (query != null && query.startsWith("token=")) {
            token = query.substring(6);
        }

        if (token == null || token.isBlank()) {
            session.close(new CloseReason(CloseReason.CloseCodes.CANNOT_ACCEPT, "Authentication required"));
            return;
        }

        try {
            Claims claims = JwtUtil.verifyToken(token);
            this.userId = claims.get("userId", Integer.class);
            this.roleCode = chatDAO.findUserRoleCode(this.userId);

            // Cập nhật session vào Redis với TTL 60s
            WsSessionManager.upsertSocketSession(this.socketId, this.userId);
            session.getUserProperties().put("userId", this.userId);
            session.getUserProperties().put("roleCode", this.roleCode);

            // Phân loại vào Room
            userSessions.put(this.socketId, session);
            if ("ADMIN".equals(this.roleCode) || "STAFF".equals(this.roleCode)) {
                shopSessions.put(this.socketId, session);
            }

            session.getBasicRemote().sendText(JsonUtil.getMapper().writeValueAsString(
                    Map.of("event", "connected", "socketId", this.socketId, "userId", this.userId)
            ));
        } catch (Exception ex) {
            session.close(new CloseReason(CloseReason.CloseCodes.CANNOT_ACCEPT, "Invalid token"));
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        // Heartbeat Ping từ Client
        if ("ping".equalsIgnoreCase(message.trim())) {
            WsSessionManager.extendSocketSession(this.socketId);
            try {
                session.getBasicRemote().sendText("pong");
            } catch (IOException e) {
                logger.log(Level.FINE, "Failed to send pong heartbeat", e);
            }
        }
    }

    @OnClose
    public void onClose(Session session) {
        if (this.socketId != null) {
            userSessions.remove(this.socketId);
            shopSessions.remove(this.socketId);
            WsSessionManager.removeSocketSession(this.socketId);
        }
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        logger.log(Level.WARNING, "WebSocket error on session " + (session != null ? session.getId() : "null"), throwable);
        onClose(session);
    }

    /**
     * Bắn tin nhắn mới tới Room Shop (Nhân viên)
     */
    public static void broadcastToShop(Message msg) {
        sendPayloadToMap(shopSessions, Map.of("event", "new_message", "data", msg));
    }

    /**
     * Bắn tin nhắn mới tới Room User (Khách hàng)
     */
    public static void sendToUser(int targetUserId, Message msg) {
        String payload;
        try {
            payload = JsonUtil.getMapper().writeValueAsString(Map.of("event", "new_message", "data", msg));
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to serialize chat message payload", e);
            return;
        }

        userSessions.values().forEach(s -> {
            Object sUserId = s.getUserProperties().get("userId");
            if (Integer.valueOf(targetUserId).equals(sUserId) && s.isOpen()) {
                try {
                    s.getBasicRemote().sendText(payload);
                } catch (IOException e) {
                    logger.log(Level.FINE, "Failed to send message to user session", e);
                }
            }
        });
    }

    private static void sendPayloadToMap(Map<String, Session> map, Object data) {
        try {
            String json = JsonUtil.getMapper().writeValueAsString(data);
            for (Session s : map.values()) {
                if (s.isOpen()) {
                    s.getBasicRemote().sendText(json);
                }
            }
        } catch (Exception e) {
            logger.log(Level.FINE, "Failed to send payload to sessions", e);
        }
    }
}