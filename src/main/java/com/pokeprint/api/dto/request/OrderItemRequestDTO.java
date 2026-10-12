package com.pokeprint.api.dto.request;

import com.pokeprint.api.domain.enums.FinishType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record OrderItemRequestDTO(
    Long pokemonModelId,
    Long tcgProductId,
    Long filamentInventoryId,
    @NotNull @Min(1) Integer quantity,
    FinishType finishType,
    String customScale,
    String origin,
    BigDecimal promoPrice,
    String promoBadge
) {
    public OrderItemRequestDTO(
        Long pokemonModelId,
        Long tcgProductId,
        Long filamentInventoryId,
        Integer quantity,
        FinishType finishType,
        String customScale
    ) {
        this(pokemonModelId, tcgProductId, filamentInventoryId, quantity, finishType, customScale, "CATALOG", null, null);
    }

    public OrderItemRequestDTO(
        Long pokemonModelId,
        Long tcgProductId,
        Long filamentInventoryId,
        Integer quantity,
        FinishType finishType,
        String customScale,
        String origin
    ) {
        this(pokemonModelId, tcgProductId, filamentInventoryId, quantity, finishType, customScale, origin, null, null);
    }
}

