package com.fashionshop.presentation.dto.loyalty;

public class ChatHistoryQueryDTO {
    private Integer chatId;
    private int page = 1;
    private int limit = 20;
    private Integer cursor;

    public Integer getChatId() { return chatId; }
    public void setChatId(Integer chatId) { this.chatId = chatId; }
    public int getPage() { return page; }
    public void setPage(int page) { this.page = page > 0 ? page : 1; }
    public int getLimit() { return limit; }
    public void setLimit(int limit) { this.limit = (limit > 0 && limit <= 100) ? limit : 20; }
    public Integer getCursor() { return cursor; }
    public void setCursor(Integer cursor) { this.cursor = cursor; }
}