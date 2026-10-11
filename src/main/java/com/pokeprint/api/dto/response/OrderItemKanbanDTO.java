package com.pokeprint.api.dto.response;

public record OrderItemKanbanDTO(
    String itemName,
    Integer quantity,
    String finishType,
    String filamentColor
) {}
