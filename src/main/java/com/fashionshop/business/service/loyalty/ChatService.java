package com.fashionshop.business.service.loyalty;

import com.fashionshop.business.entity.Chat;
import com.fashionshop.business.entity.Message;
import com.fashionshop.business.entity.enums.ChatStatus;
import com.fashionshop.common.exception.BadRequestException;
import com.fashionshop.common.exception.ForbiddenException;
import com.fashionshop.common.exception.NotFoundException;
import com.fashionshop.dataaccess.dao.loyalty.ChatDAO;
import com.fashionshop.dataaccess.dao.loyalty.MessageDAO;
import com.fashionshop.presentation.dto.loyalty.ChatHistoryQueryDTO;
import com.fashionshop.presentation.dto.loyalty.MarkReadDTO;
import com.fashionshop.presentation.dto.loyalty.SendMessageDTO;
import com.fashionshop.presentation.websocket.ChatEndpoint;

import java.util.List;
import java.util.Map;

public class ChatService {
    private final ChatDAO chatDAO = new ChatDAO();
    private final MessageDAO messageDAO = new MessageDAO();

    public Chat getOrCreateActiveChat(int userId) {
        return chatDAO.findActiveChatByUserId(userId)
                .orElseGet(() -> chatDAO.createChat(userId));
    }

    private Chat authorizeChatAccess(int userId, int chatId) {
        Chat chat = chatDAO.findDetailById(chatId)
                .orElseThrow(() -> new NotFoundException("Cuộc trò chuyện không tồn tại"));

        if (chat.getUser().getId().equals(userId)) {
            return chat;
        }

        String roleCode = chatDAO.findUserRoleCode(userId);
        if (!"ADMIN".equals(roleCode) && !"STAFF".equals(roleCode)) {
            throw new ForbiddenException("Bạn không có quyền truy cập cuộc trò chuyện này");
        }

        return chat;
    }

    private void authorizeShopStaff(int userId) {
        String roleCode = chatDAO.findUserRoleCode(userId);
        if (!"ADMIN".equals(roleCode) && !"STAFF".equals(roleCode)) {
            throw new ForbiddenException("Chỉ nhân viên cửa hàng mới được thực hiện thao tác này");
        }
    }

    public Message sendMessage(int senderId, SendMessageDTO body) {
        Chat chat = authorizeChatAccess(senderId, body.getChatId());
        if (chat.getStatus() == ChatStatus.CLOSED) {
            throw new BadRequestException("Cuộc trò chuyện đã kết thúc");
        }

        Message message = messageDAO.createMessage(body.getChatId(), senderId, body.getContent());

        // Phát realtime tin nhắn qua WebSocket
        boolean isCustomer = senderId == chat.getUser().getId();
        if (isCustomer) {
            ChatEndpoint.broadcastToShop(message);
        } else {
            ChatEndpoint.sendToUser(chat.getUser().getId(), message);
        }
        com.fashionshop.presentation.websocket.SocketIOServlet.broadcastNewMessage(message);

        return message;
    }

    public MessageDAO.CursorResult getChatHistory(int userId, ChatHistoryQueryDTO query) {
        authorizeChatAccess(userId, query.getChatId());
        return messageDAO.findConversation(query.getChatId(), query.getCursor(), query.getLimit());
    }

    public int markAsRead(int receiverId, MarkReadDTO body) {
        authorizeChatAccess(receiverId, body.getChatId());
        return messageDAO.markMessagesAsRead(body.getChatId(), receiverId);
    }

    public int getUnreadCount(int userId) {
        return messageDAO.countUnread(userId);
    }

    public List<Chat> getConversationList(int userId) {
        return chatDAO.findConversationList(userId);
    }

    public List<Chat> getShopConversationList(int userId) {
        authorizeShopStaff(userId);
        return chatDAO.findConversationList(null);
    }

    public Chat closeConversation(int userId, int chatId) {
        authorizeShopStaff(userId);
        Chat chat = chatDAO.findDetailById(chatId)
                .orElseThrow(() -> new NotFoundException("Cuộc trò chuyện không tồn tại"));

        if (chat.getStatus() == ChatStatus.CLOSED) {
            throw new BadRequestException("Cuộc trò chuyện đã kết thúc");
        }

        return chatDAO.closeChat(chatId);
    }
}