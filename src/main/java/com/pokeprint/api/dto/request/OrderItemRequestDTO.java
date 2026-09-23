package com.pokeprint.api.dto.request;

import com.pokeprint.api.domain.enums.FinishType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record OrderItemRequestDTO(
    @NotNull Long pokemonModelId,
    @NotNull Long filamentInventoryId,
    @NotNull @Min(1) Integer quantity,
    @NotNull FinishType finishType,
    String customScale
) {}
