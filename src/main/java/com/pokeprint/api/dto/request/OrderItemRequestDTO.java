package com.pokeprint.api.dto.request;

import com.pokeprint.api.domain.enums.FinishType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record OrderItemRequestDTO(
    Long pokemonModelId,
    Long tcgProductId,
    Long filamentInventoryId,
    @NotNull @Min(1) Integer quantity,
    FinishType finishType,
    String customScale
) {}
