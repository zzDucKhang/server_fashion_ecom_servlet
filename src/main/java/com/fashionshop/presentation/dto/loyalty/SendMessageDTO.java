package com.fashionshop.presentation.dto.loyalty;

import com.fashionshop.common.util.Validatable;
import com.fashionshop.common.util.ValidationUtil;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SendMessageDTO implements Validatable {
    private Integer chatId;
    private String content;

    @Override
    public Map<String, List<String>> validate() {
        Map<String, List<String>> errors = new LinkedHashMap<>();

        if (chatId == null || chatId <= 0) {
            ValidationUtil.addError(errors, "chatId", "Mã cuộc trò chuyện phải là số nguyên dương");
        }

        if (content == null || content.isBlank()) {
            ValidationUtil.addError(errors, "content", "Nội dung tin nhắn không được để trống");
        } else if (content.trim().length() > 4000) {
            ValidationUtil.addError(errors, "content", "Nội dung tin nhắn tối đa 4000 ký tự");
        }

        return errors;
    }

    public Integer getChatId() { return chatId; }
    public void setChatId(Integer chatId) { this.chatId = chatId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}