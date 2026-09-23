package com.pokeprint.api.dto.response;

import com.pokeprint.api.domain.enums.FinishType;
import java.math.BigDecimal;

public record OrderItemResponseDTO(
    Long id,
    Long pokemonModelId,
    String pokemonName,
    Long filamentInventoryId,
    FinishType finishType,
    Integer quantity,
    BigDecimal unitPrice,
    BigDecimal subtotal
) {}
