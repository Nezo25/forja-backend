package com.pokeprint.api.dto.response;

import com.pokeprint.api.domain.enums.KanbanColumn;
import java.math.BigDecimal;
import java.util.List;

public record OrderKanbanDTO(
    Long id,
    String shortCode,
    String customerName,
    String customerPhone,
    String customerEmail,
    BigDecimal totalAmount,
    KanbanColumn kanbanColumn,
    List<String> tags,
    List<OrderItemKanbanDTO> items
) {}
