package com.fashionshop.presentation.dto.loyalty;

public class SendMessageDTO {
    private Integer chatId;
    private String content;

    public Integer getChatId() { return chatId; }
    public void setChatId(Integer chatId) { this.chatId = chatId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}