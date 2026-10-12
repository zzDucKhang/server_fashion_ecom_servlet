package com.fashionshop.presentation.websocket;

import com.fashionshop.business.entity.Message;
import com.fashionshop.common.util.JsonUtil;
import com.fashionshop.common.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * SocketIOServlet implements Engine.IO v4 / Socket.IO v4 protocol over HTTP polling
 * to seamlessly synchronize with the Next.js socket-provider and live chat components.
 */
@WebServlet(urlPatterns = {"/socket.io/*", "/socket.io"})
public class SocketIOServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(SocketIOServlet.class.getName());

    public static class SocketSession {
        public final String sid;
        public volatile Integer userId;
        public volatile String roleCode;
        public volatile boolean joinedShop;
        public final BlockingQueue<String> queue = new LinkedBlockingQueue<>();
        public volatile long lastActive = System.currentTimeMillis();

        public SocketSession(String sid) {
            this.sid = sid;
        }
    }

    private static final Map<String, SocketSession> activeSessions = new ConcurrentHashMap<>();

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        addCorsHeaders(req, resp);
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        addCorsHeaders(req, resp);
        resp.setContentType("text/plain; charset=UTF-8");

        String sid = req.getParameter("sid");
        if (sid == null || sid.isBlank()) {
            // Engine.IO v4 Handshake: return open packet (type 0)
            String newSid = UUID.randomUUID().toString().replace("-", "");
            SocketSession session = new SocketSession(newSid);
            activeSessions.put(newSid, session);

            String handshakePayload = "0{\"sid\":\"" + newSid + "\",\"upgrades\":[],\"pingInterval\":25000,\"pingTimeout\":20000,\"maxPayload\":1000000}";
            resp.getWriter().write(handshakePayload);
            return;
        }

        SocketSession session = activeSessions.get(sid);
        if (session == null) {
            // Re-create session if expired or not found
            session = new SocketSession(sid);
            activeSessions.put(sid, session);
        }
        session.lastActive = System.currentTimeMillis();

        try {
            // Poll for outbound packets up to 1500ms
            String packet = session.queue.poll(1500, TimeUnit.MILLISECONDS);
            if (packet != null) {
                StringBuilder sb = new StringBuilder(packet);
                // Drain any additional queued packets into batch separated by delimiter
                while (!session.queue.isEmpty()) {
                    String next = session.queue.poll();
                    if (next != null) {
                        sb.append("\u001e").append(next);
                    }
                }
                resp.getWriter().write(sb.toString());
            } else {
                // Return Engine.IO NOOP packet (type 6) to keep polling loop lively
                resp.getWriter().write("6");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            resp.getWriter().write("6");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        addCorsHeaders(req, resp);
        resp.setContentType("text/plain; charset=UTF-8");

        String sid = req.getParameter("sid");
        if (sid == null || sid.isBlank()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("Missing sid");
            return;
        }

        SocketSession session = activeSessions.computeIfAbsent(sid, SocketSession::new);
        session.lastActive = System.currentTimeMillis();

        StringBuilder body = new StringBuilder();
        try (BufferedReader reader = req.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                body.append(line);
            }
        }
        String payload = body.toString();

        if (payload.startsWith("40")) {
            // Socket.IO CONNECT packet, e.g. 40{"token":"..."}
            handleConnectPacket(session, payload);
        } else if (payload.contains("join_shop")) {
            // Client joined shop room
            session.joinedShop = true;
            logger.info("Session " + session.sid + " joined shop room");
        } else if ("2".equals(payload)) {
            // Ping -> Pong
            session.queue.offer("3");
        }

        resp.getWriter().write("ok");
    }

    private void handleConnectPacket(SocketSession session, String payload) {
        try {
            int jsonStart = payload.indexOf('{');
            if (jsonStart != -1) {
                int jsonEnd = payload.lastIndexOf('}');
                if (jsonEnd > jsonStart) {
                    String json = payload.substring(jsonStart, jsonEnd + 1);
                    Map<?, ?> map = JsonUtil.fromJson(json, Map.class);
                    Object tokenObj = map.get("token");
                    if (tokenObj instanceof String token && !token.isBlank()) {
                        Claims claims = JwtUtil.verifyToken(token);
                        Integer userId = claims.get("userId", Integer.class);
                        String roleCode = claims.get("roleCode", String.class);
                        session.userId = userId;
                        session.roleCode = roleCode;

                        WsSessionManager.upsertSocketSession(session.sid, userId);
                        logger.info("Socket.IO authenticated user " + userId + " (" + roleCode + ")");
                    }
                }
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to authenticate Socket.IO connection: " + e.getMessage());
        }

        // Send Socket.IO CONNECT ACK (40{"sid":"..."})
        session.queue.offer("40{\"sid\":\"" + session.sid + "\"}");
    }

    private void addCorsHeaders(HttpServletRequest req, HttpServletResponse resp) {
        String origin = req.getHeader("Origin");
        resp.setHeader("Access-Control-Allow-Origin", origin != null ? origin : "*");
        resp.setHeader("Access-Control-Allow-Credentials", "true");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS, PUT, DELETE, PATCH");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Requested-With, Accept");
    }

    /**
     * Broadcasts a new message event to all matching active Socket.IO clients and shop rooms.
     */
    public static void broadcastNewMessage(Message message) {
        if (message == null) return;
        try {
            String messageJson = JsonUtil.toJson(message);
            String socketPacket = "42[\"new_message\"," + messageJson + "]";

            Integer chatOwnerId = null;
            if (message.getChat() != null && message.getChat().getUser() != null) {
                chatOwnerId = message.getChat().getUser().getId();
            }

            Integer senderId = message.getSenderId();

            for (SocketSession session : activeSessions.values()) {
                boolean shouldDeliver = false;

                // 1. Deliver if session is in shop room (admin / staff)
                if (session.joinedShop) {
                    shouldDeliver = true;
                }
                // 2. Deliver if session belongs to chat customer or sender
                else if (session.userId != null) {
                    if (session.userId.equals(senderId) || (chatOwnerId != null && session.userId.equals(chatOwnerId))) {
                        shouldDeliver = true;
                    }
                }

                if (shouldDeliver) {
                    session.queue.offer(socketPacket);
                }
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to broadcast message via Socket.IO: " + e.getMessage(), e);
        }
    }
}
