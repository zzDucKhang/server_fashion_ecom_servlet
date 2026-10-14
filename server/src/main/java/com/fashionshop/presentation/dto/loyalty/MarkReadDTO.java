package com.fashionshop.presentation.dto.loyalty;

import com.fashionshop.common.util.Validatable;
import com.fashionshop.common.util.ValidationUtil;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MarkReadDTO implements Validatable {
    private Integer chatId;

    @Override
    public Map<String, List<String>> validate() {
        Map<String, List<String>> errors = new LinkedHashMap<>();

        if (chatId == null || chatId <= 0) {
            ValidationUtil.addError(errors, "chatId", "Mã cuộc trò chuyện phải là số nguyên dương");
        }

        return errors;
    }

    public Integer getChatId() { return chatId; }
    public void setChatId(Integer chatId) { this.chatId = chatId; }
}