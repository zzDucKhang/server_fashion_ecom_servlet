package com.fashionshop.presentation.dto;

import com.fashionshop.presentation.dto.cartorder.OrderQueryDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OrderQueryCompatibilityTest {

    @Test
    @DisplayName("Verify OrderQueryDTO supports pagination, status, and all-orders query flag")
    void testOrderQueryDTOFields() {
        OrderQueryDTO dto = new OrderQueryDTO();
        dto.setPage(2);
        dto.setLimit(25);
        dto.setStatus("CONFIRMED");
        dto.setAll(true);

        assertEquals(2, dto.getPage());
        assertEquals(25, dto.getLimit());
        assertEquals("CONFIRMED", dto.getStatus());
        assertTrue(dto.getAll());
    }
}
