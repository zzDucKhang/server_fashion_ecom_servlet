package com.fashionshop.dataaccess.dao.loyalty;

import com.fashionshop.business.entity.Chat;
import com.fashionshop.business.entity.Message;
import com.fashionshop.business.entity.User;
import com.fashionshop.dataaccess.base.AbstractJpaDAO;
import jakarta.persistence.TypedQuery;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public class MessageDAO extends AbstractJpaDAO<Message, Integer> {

    public record CursorResult(List<Message> data, Integer nextCursor, boolean hasMore) {}

    public MessageDAO() {
        super(Message.class);
    }

    public Message createMessage(int chatId, int senderId, String content) {
        return executeTransaction(em -> {
            Chat chat = em.find(Chat.class, chatId);
            User sender = em.find(User.class, senderId);

            Message message = new Message();
            message.setChat(chat);
            message.setSender(sender);
            message.setContent(content.trim());
            message.setIsRead(false);
            message.setSendDate(LocalDateTime.now());

            em.persist(message);

            // Đồng bộ updatedAt của Chat
            chat.setUpdatedAt(LocalDateTime.now());
            em.merge(chat);

            return message;
        });
    }

    /**
     * Cursor-based pagination (O(limit) seek time) khớp 100% logic findConversation Node.js
     */
    public CursorResult findConversation(int chatId, Integer cursor, int take) {
        return execute(em -> {
            StringBuilder jpql = new StringBuilder(
                    "SELECT m FROM Message m " +
                    "LEFT JOIN FETCH m.sender " +
                    "WHERE m.chat.id = :chatId");

            if (cursor != null) {
                jpql.append(" AND m.id < :cursor");
            }
            jpql.append(" ORDER BY m.id DESC");

            TypedQuery<Message> q = em.createQuery(jpql.toString(), Message.class)
                    .setParameter("chatId", chatId);

            if (cursor != null) {
                q.setParameter("cursor", cursor);
            }

            // Lấy take + 1 để xác định có trang tiếp theo không
            q.setMaxResults(take + 1);
            List<Message> messages = q.getResultList();

            boolean hasMore = messages.size() > take;
            List<Message> data = hasMore ? messages.subList(0, take) : messages;

            Integer nextCursor = (hasMore && !data.isEmpty()) ? data.get(data.size() - 1).getId() : null;

            // Đảo ngược danh sách để hiển thị theo thứ tự thời gian tăng dần
            Collections.reverse(data);

            return new CursorResult(data, nextCursor, hasMore);
        });
    }

    public int markMessagesAsRead(int chatId, int currentUserId) {
        return executeTransaction(em -> em.createQuery(
                "UPDATE Message m SET m.isRead = true " +
                "WHERE m.chat.id = :chatId AND m.isRead = false AND m.sender.id <> :currentUserId")
                .setParameter("chatId", chatId)
                .setParameter("currentUserId", currentUserId)
                .executeUpdate());
    }

    public int countUnread(int userId) {
        return execute(em -> {
            Long count = em.createQuery(
                    "SELECT COUNT(m) FROM Message m " +
                    "WHERE m.chat.user.id = :userId AND m.sender.id <> :userId AND m.isRead = false", Long.class)
                    .setParameter("userId", userId)
                    .getSingleResult();
            return count.intValue();
        });
    }
}