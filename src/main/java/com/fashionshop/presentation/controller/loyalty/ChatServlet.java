package com.fashionshop.presentation.controller.loyalty;

import com.fashionshop.business.entity.Chat;
import com.fashionshop.business.entity.Message;
import com.fashionshop.business.service.loyalty.ChatService;
import com.fashionshop.common.exception.BadRequestException;
import com.fashionshop.common.response.ApiResponse;
import com.fashionshop.common.util.JsonUtil;
import com.fashionshop.common.util.SecurityGuard;
import com.fashionshop.dataaccess.dao.loyalty.MessageDAO;
import com.fashionshop.presentation.dto.loyalty.*;
import com.fashionshop.common.util.ValidationUtil;
import com.fashionshop.presentation.websocket.WsSessionManager;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

@WebServlet(urlPatterns = {"/api/v1/loyalty-engage/chat/*", "/api/v1/chat/*"})
public class ChatServlet extends HttpServlet {
    private final ChatService chatService = new ChatService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();

        if ("/messages/unread".equals(pathInfo)) {
            // GET /messages/unread
            Integer userId = SecurityGuard.requireAuth(req);
            int unread = chatService.getUnreadCount(userId);
            JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of("statusCode", 200, "data", Map.of("unreadCount", unread)));
            return;
        }

        if ("/messages/active".equals(pathInfo)) {
            // GET /messages/active
            Integer userId = SecurityGuard.requireAuth(req);
            Chat chat = chatService.getOrCreateActiveChat(userId);
            JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of("statusCode", 200, "data", chat));
            return;
        }

        if ("/messages".equals(pathInfo)) {
            // GET /messages?chatId=&cursor=&limit=&page=
            Integer userId = SecurityGuard.requireAuth(req);
            ChatHistoryQueryDTO query = new ChatHistoryQueryDTO();
            if (req.getParameter("chatId") != null) query.setChatId(Integer.parseInt(req.getParameter("chatId")));
            if (req.getParameter("cursor") != null) query.setCursor(Integer.parseInt(req.getParameter("cursor")));
            if (req.getParameter("limit") != null) query.setLimit(Integer.parseInt(req.getParameter("limit")));
            if (req.getParameter("page") != null) query.setPage(Integer.parseInt(req.getParameter("page")));

            MessageDAO.CursorResult result = chatService.getChatHistory(userId, query);
            JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of(
                    "statusCode", 200,
                    "data", result.data(),
                    "pagination", Map.of(
                            "page", query.getPage(),
                            "limit", query.getLimit(),
                            "nextCursor", result.nextCursor() != null ? result.nextCursor() : false,
                            "hasMore", result.hasMore()
                    )
            ));
            return;
        }

        if ("/conversations".equals(pathInfo)) {
            // GET /conversations (Shop inbox)
            Integer userId = SecurityGuard.requireAuth(req);
            var list = chatService.getShopConversationList(userId);
            JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of("statusCode", 200, "data", list));
            return;
        }

        if (pathInfo != null && pathInfo.matches("/sessions/\\d+/online")) {
            // GET /sessions/:userId/online (Admin only)
            SecurityGuard.requireAdmin(req);
            int targetUserId = Integer.parseInt(pathInfo.split("/")[2]);
            boolean isOnline = WsSessionManager.isUserOnline(targetUserId);
            JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of("statusCode", 200, "data", Map.of("userId", targetUserId, "isOnline", isOnline)));
            return;
        }

        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();

        if ("/messages".equals(pathInfo)) {
            // POST /messages
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
            // POST /messages/read
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
            // POST /conversations/:chatId/close
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

        if ("/sessions/heartbeat".equals(pathInfo)) {
            // POST /sessions/heartbeat
            Map<?, ?> body = JsonUtil.fromJson(req, Map.class);
            String socketId = (String) body.get("socketId");
            if (socketId == null || socketId.isBlank()) throw new BadRequestException("socketId không được để trống");
            boolean extended = WsSessionManager.extendSocketSession(socketId);
            JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of(
                    "statusCode", 200,
                    "message", "Gia hạn socket session thành công",
                    "data", Map.of("socketId", socketId, "extended", extended)
            ));
            return;
        }

        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
    }
}