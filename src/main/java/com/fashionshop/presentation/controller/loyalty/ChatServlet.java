package com.fashionshop.presentation.controller.loyalty;

import com.fashionshop.business.entity.Chat;
import com.fashionshop.business.entity.Message;
import com.fashionshop.business.service.loyalty.ChatService;
import com.fashionshop.common.exception.BadRequestException;
import com.fashionshop.common.exception.NotFoundException;
import com.fashionshop.common.util.JsonUtil;
import com.fashionshop.common.util.SecurityGuard;
import com.fashionshop.common.util.ValidationUtil;
import com.fashionshop.dataaccess.dao.loyalty.MessageDAO;
import com.fashionshop.presentation.controller.base.BaseHttpServlet;
import com.fashionshop.presentation.dto.loyalty.ChatHistoryQueryDTO;
import com.fashionshop.presentation.dto.loyalty.MarkReadDTO;
import com.fashionshop.presentation.dto.loyalty.SendMessageDTO;
import com.fashionshop.presentation.dto.loyalty.SocketSessionDTO;
import com.fashionshop.presentation.websocket.WsSessionManager;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = {"/api/v1/loyalty-engage/chat/*", "/api/v1/chat/*"})
public class ChatServlet extends BaseHttpServlet {
    private final ChatService chatService = new ChatService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();

        if ("/messages/unread".equals(pathInfo)) {
            Integer userId = SecurityGuard.requireAuth(req);
            int unread = chatService.getUnreadCount(userId);
            JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of(
                    "statusCode", 200,
                    "data", Map.of("unreadCount", unread)
            ));
            return;
        }

        if ("/messages/active".equals(pathInfo)) {
            Integer userId = SecurityGuard.requireAuth(req);
            Chat chat = chatService.getOrCreateActiveChat(userId);
            JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of(
                    "statusCode", 200,
                    "data", chat
            ));
            return;
        }

        if ("/messages".equals(pathInfo)) {
            Integer userId = SecurityGuard.requireAuth(req);
            ChatHistoryQueryDTO query = new ChatHistoryQueryDTO();
            Integer chatId = getIntParameter(req, "chatId", null);
            if (chatId == null || chatId <= 0) {
                throw new BadRequestException("chatId phải là số nguyên dương");
            }
            query.setChatId(chatId);
            query.setCursor(getIntParameter(req, "cursor", null));
            query.setLimit(getIntParameter(req, "limit", 20));
            query.setPage(getIntParameter(req, "page", 1));

            MessageDAO.CursorResult result = chatService.getChatHistory(userId, query);

            // Dùng LinkedHashMap để giữ nguyên giá trị null cho nextCursor giống hệt Node.js
            Map<String, Object> pagination = new LinkedHashMap<>();
            pagination.put("page", query.getPage());
            pagination.put("limit", query.getLimit());
            pagination.put("nextCursor", result.nextCursor());
            pagination.put("hasMore", result.hasMore());

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("statusCode", 200);
            response.put("data", result.data());
            response.put("pagination", pagination);

            JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, response);
            return;
        }

        if ("/conversations".equals(pathInfo)) {
            Integer userId = SecurityGuard.requireAuth(req);
            var list = chatService.getShopConversationList(userId);
            JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of(
                    "statusCode", 200,
                    "data", list
            ));
            return;
        }

        if (pathInfo != null && pathInfo.matches("/sessions/\\d+/online")) {
            SecurityGuard.requireAdmin(req);
            int targetUserId = Integer.parseInt(pathInfo.split("/")[2]);
            boolean isOnline = WsSessionManager.isUserOnline(targetUserId);
            JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of(
                    "statusCode", 200,
                    "data", Map.of("userId", targetUserId, "isOnline", isOnline)
            ));
            return;
        }

        if (pathInfo != null && pathInfo.matches("/sessions/\\d+")) {
            SecurityGuard.requireAdmin(req);
            int targetUserId = Integer.parseInt(pathInfo.split("/")[2]);
            Map<String, String> rawSessions = WsSessionManager.getSocketSessionsByUser(targetUserId);
            List<Map<String, Object>> sessionList = new ArrayList<>();
            if (rawSessions != null) {
                for (Map.Entry<String, String> entry : rawSessions.entrySet()) {
                    long ts = Long.parseLong(entry.getValue());
                    sessionList.add(Map.of(
                            "socketId", entry.getKey(),
                            "userId", targetUserId,
                            "connectedAt", Instant.ofEpochMilli(ts).toString()
                    ));
                }
            }
            JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of(
                    "statusCode", 200,
                    "data", sessionList
            ));
            return;
        }

        sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Endpoint không tồn tại");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();

        if ("/messages".equals(pathInfo)) {
            Integer senderId = SecurityGuard.requireAuth(req);
            SendMessageDTO dto = JsonUtil.fromJson(req, SendMessageDTO.class);
            ValidationUtil.validate(dto);
            Message msg = chatService.sendMessage(senderId, dto);
            JsonUtil.sendJson(resp, HttpServletResponse.SC_CREATED, Map.of(
                    "statusCode", 201,
                    "message", "Tin nhắn đã được gửi",
                    "data", msg
            ));
            return;
        }

        if ("/messages/read".equals(pathInfo)) {
            Integer receiverId = SecurityGuard.requireAuth(req);
            MarkReadDTO dto = JsonUtil.fromJson(req, MarkReadDTO.class);
            ValidationUtil.validate(dto);
            int count = chatService.markAsRead(receiverId, dto);
            JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of(
                    "statusCode", 200,
                    "message", "Đã đọc " + count + " tin nhắn",
                    "updatedCount", count
            ));
            return;
        }

        if (pathInfo != null && pathInfo.matches("/conversations/\\d+/close")) {
            Integer staffId = SecurityGuard.requireAuth(req);
            int chatId = Integer.parseInt(pathInfo.split("/")[2]);
            Chat closed = chatService.closeConversation(staffId, chatId);
            JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of(
                    "statusCode", 200,
                    "message", "Đã đóng cuộc trò chuyện",
                    "data", closed
            ));
            return;
        }

        if ("/sessions".equals(pathInfo)) {
            SecurityGuard.requireAdmin(req);
            SocketSessionDTO dto = JsonUtil.fromJson(req, SocketSessionDTO.class);
            if (dto == null || dto.getSocketId() == null || dto.getSocketId().isBlank() || dto.getUserId() == null) {
                throw new BadRequestException("socketId và userId là bắt buộc");
            }
            WsSessionManager.upsertSocketSession(dto.getSocketId().trim(), dto.getUserId());
            JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of(
                    "statusCode", 200,
                    "message", "Socket session đã được lưu",
                    "data", Map.of(
                            "socketId", dto.getSocketId().trim(),
                            "userId", dto.getUserId(),
                            "connectedAt", Instant.now().toString()
                    )
            ));
            return;
        }

        if ("/sessions/heartbeat".equals(pathInfo)) {
            Map<?, ?> body = JsonUtil.fromJson(req, Map.class);
            String socketId = body != null && body.get("socketId") != null ? String.valueOf(body.get("socketId")) : null;
            if (socketId == null || socketId.isBlank()) {
                throw new BadRequestException("socketId không được để trống");
            }
            boolean extended = WsSessionManager.extendSocketSession(socketId.trim());
            if (!extended) {
                throw new NotFoundException("Socket session không tồn tại hoặc đã hết hạn");
            }
            JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of(
                    "statusCode", 200,
                    "message", "Gia hạn socket session thành công",
                    "data", Map.of("socketId", socketId.trim(), "extended", true)
            ));
            return;
        }

        sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Endpoint không tồn tại");
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();

        if ("/sessions".equals(pathInfo)) {
            SecurityGuard.requireAdmin(req);
            Map<?, ?> body = JsonUtil.fromJson(req, Map.class);
            String socketId = body != null && body.get("socketId") != null ? String.valueOf(body.get("socketId")) : null;
            if (socketId == null || socketId.isBlank()) {
                throw new BadRequestException("socketId không được để trống");
            }
            WsSessionManager.removeSocketSession(socketId.trim());
            JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of(
                    "statusCode", 200,
                    "message", "Socket session đã được xoá"
            ));
            return;
        }

        sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Endpoint không tồn tại");
    }
}