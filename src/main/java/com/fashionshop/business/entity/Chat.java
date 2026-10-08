package com.fashionshop.business.entity;


import com.fashionshop.business.entity.enums.ChatStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "\"Chat\"")
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChatStatus status = ChatStatus.OPEN;

    @Column(name = "\"startedAt\"", nullable = false)
    private LocalDateTime startedAt = LocalDateTime.now();

    @Column(name = "\"endedAt\"")
    private LocalDateTime endedAt;

    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> messages = new ArrayList<>();

    // Business Methods theo Class Diagram
    public void addMessage(Message msg) {
        if (!isEnded()) {
            this.messages.add(msg);
            msg.setChat(this);
        }
    }

    public void closeChat() {
        this.status = ChatStatus.CLOSED;
        this.endedAt = LocalDateTime.now();
    }

    public boolean isEnded() {
        return this.status == ChatStatus.CLOSED;
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public ChatStatus getStatus() { return status; }
    public void setStatus(ChatStatus status) { this.status = status; }
    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }
    public LocalDateTime getEndedAt() { return endedAt; }
    public void setEndedAt(LocalDateTime endedAt) { this.endedAt = endedAt; }
    public List<Message> getMessages() { return messages; }
    public void setMessages(List<Message> messages) { this.messages = messages; }
}
