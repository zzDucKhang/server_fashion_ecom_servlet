package com.fashionshop.dataaccess.dao.loyalty;

import com.fashionshop.business.entity.Chat;
import com.fashionshop.business.entity.User;
import com.fashionshop.business.entity.enums.ChatStatus;
import com.fashionshop.dataaccess.base.AbstractJpaDAO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class ChatDAO extends AbstractJpaDAO<Chat, Integer> {

    public ChatDAO() {
        super(Chat.class);
    }

    public Chat createChat(int userId) {
        return executeTransaction(em -> {
            User user = em.find(User.class, userId);
            Chat chat = new Chat();
            chat.setUser(user);
            chat.setStatus(ChatStatus.OPEN);
            chat.setStartedAt(LocalDateTime.now());
            em.persist(chat);
            return chat;
        });
    }

    public Optional<Chat> findActiveChatByUserId(int userId) {
        return execute(em -> {
            String jpql = "SELECT DISTINCT c FROM Chat c " +
                    "LEFT JOIN FETCH c.messages " +
                    "WHERE c.user.id = :userId AND c.status IN (:openStatus, :waitingStatus) " +
                    "ORDER BY c.createdAt DESC";

            List<Chat> list = em.createQuery(jpql, Chat.class)
                    .setParameter("userId", userId)
                    .setParameter("openStatus", ChatStatus.OPEN)
                    .setParameter("waitingStatus", ChatStatus.WAITING)
                    .setMaxResults(1)
                    .getResultList();

            return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
        });
    }

    public Optional<Chat> findDetailById(int chatId) {
        return execute(em -> {
            List<Chat> list = em.createQuery(
                    "SELECT c FROM Chat c LEFT JOIN FETCH c.user WHERE c.id = :id", Chat.class)
                    .setParameter("id", chatId)
                    .setMaxResults(1)
                    .getResultList();
            return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
        });
    }

    public String findUserRoleCode(int userId) {
        return execute(em -> {
            List<String> codes = em.createQuery(
                    "SELECT r.code FROM User u JOIN u.role r " +
                    "WHERE u.id = :userId AND r.isActive = true AND r.deletedAt IS NULL", String.class)
                    .setParameter("userId", userId)
                    .setMaxResults(1)
                    .getResultList();
            return codes.isEmpty() ? null : codes.get(0);
        });
    }

    public Chat closeChat(int chatId) {
        return executeTransaction(em -> {
            Chat chat = em.find(Chat.class, chatId);
            if (chat != null) {
                chat.setStatus(ChatStatus.CLOSED);
                chat.setEndedAt(LocalDateTime.now());
            }
            return chat;
        });
    }

    public List<Chat> findConversationList(Integer userId) {
        return execute(em -> {
            StringBuilder jpql = new StringBuilder(
                    "SELECT DISTINCT c FROM Chat c " +
                    "LEFT JOIN FETCH c.user " +
                    "WHERE c.status <> :closedStatus");

            if (userId != null) {
                jpql.append(" AND c.user.id = :userId");
            }
            jpql.append(" ORDER BY c.updatedAt DESC");

            var q = em.createQuery(jpql.toString(), Chat.class)
                    .setParameter("closedStatus", ChatStatus.CLOSED);

            if (userId != null) {
                q.setParameter("userId", userId);
            }
            return q.getResultList();
        });
    }
}