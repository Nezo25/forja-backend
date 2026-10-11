package com.pokeprint.api.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record FilamentRestockRequestDTO(
    @NotNull @Positive BigDecimal addedGrams,
    @NotNull @Positive BigDecimal totalCost
) {}
