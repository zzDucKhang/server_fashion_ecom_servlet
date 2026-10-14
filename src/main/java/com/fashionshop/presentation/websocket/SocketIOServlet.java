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
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    private ScheduledExecutorService cleanupScheduler;

    @Override
    public void init() throws ServletException {
        super.init();
        cleanupScheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "SocketIO-Session-Cleaner");
            t.setDaemon(true);
            return t;
        });
        cleanupScheduler.scheduleAtFixedRate(() -> {
            long now = System.currentTimeMillis();
            activeSessions.entrySet().removeIf(entry -> (now - entry.getValue().lastActive) > 60000);
        }, 1, 1, TimeUnit.MINUTES);
    }

    @Override
    public void destroy() {
        if (cleanupScheduler != null && !cleanupScheduler.isShutdown()) {
            cleanupScheduler.shutdownNow();
        }
        super.destroy();
    }

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
            String newSid = UUID.randomUUID().toString().replace("-", "");
            SocketSession session = new SocketSession(newSid);
            activeSessions.put(newSid, session);

            String handshakePayload = "0{\"sid\":\"" + newSid + "\",\"upgrades\":[],\"pingInterval\":25000,\"pingTimeout\":20000,\"maxPayload\":1000000}";
            resp.getWriter().write(handshakePayload);
            return;
        }

        SocketSession session = activeSessions.computeIfAbsent(sid, SocketSession::new);
        session.lastActive = System.currentTimeMillis();

        try {
            String packet = session.queue.poll(1500, TimeUnit.MILLISECONDS);
            if (packet != null) {
                StringBuilder sb = new StringBuilder(packet);
                while (!session.queue.isEmpty()) {
                    String next = session.queue.poll();
                    if (next != null) {
                        sb.append("\u001e").append(next);
                    }
                }
                resp.getWriter().write(sb.toString());
            } else {
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
            handleConnectPacket(session, payload);
        } else if (payload.contains("join_shop")) {
            session.joinedShop = true;
            logger.info("Session " + session.sid + " joined shop room");
        } else if ("2".equals(payload)) {
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

        session.queue.offer("40{\"sid\":\"" + session.sid + "\"}");
    }

    private void addCorsHeaders(HttpServletRequest req, HttpServletResponse resp) {
        String origin = req.getHeader("Origin");
        resp.setHeader("Access-Control-Allow-Origin", origin != null ? origin : "*");
        resp.setHeader("Access-Control-Allow-Credentials", "true");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS, PUT, DELETE, PATCH");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Requested-With, Accept");
    }

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

                if (session.joinedShop) {
                    shouldDeliver = true;
                } else if (session.userId != null) {
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