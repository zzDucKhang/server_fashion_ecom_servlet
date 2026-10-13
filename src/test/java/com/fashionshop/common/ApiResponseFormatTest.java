package com.fashionshop.common;

import com.fashionshop.common.response.ApiResponse;
import com.fashionshop.common.response.PagedResponse;
import com.fashionshop.common.util.JsonUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ApiResponseFormatTest {

    @Test
    @DisplayName("Verify ApiResponse matches client expected schema: statusCode, message, data")
    void testApiResponseContract() throws Exception {
        ApiResponse<Map<String, String>> response = ApiResponse.success(200, "Thành công", Map.of("key", "value"));
        String json = JsonUtil.toJson(response);

        assertNotNull(json);
        assertTrue(json.contains("\"statusCode\":200"));
        assertTrue(json.contains("\"message\":\"Thành công\""));
        assertTrue(json.contains("\"data\":{\"key\":\"value\"}"));
    }

    @Test
    @DisplayName("Verify PagedResponse matches client pagination schema: data, totalItems, totalPages, page, limit")
    void testPagedResponseContract() throws Exception {
        List<String> items = List.of("item1", "item2");
        PagedResponse<String> paged = new PagedResponse<>(items, 50, 2, 10);
        String json = JsonUtil.toJson(paged);

        assertNotNull(json);
        assertEquals(5, paged.getTotalPages());
        assertEquals(2, paged.getPage());
        assertEquals(10, paged.getLimit());
        assertEquals(50, paged.getTotalItems());
        assertTrue(json.contains("\"totalItems\":50"));
        assertTrue(json.contains("\"totalPages\":5"));
    }
}
