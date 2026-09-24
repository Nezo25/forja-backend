package com.pokeprint.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

public record FilamentRequestDTO(
    @NotBlank String color,
    @NotBlank String material,
    @NotNull @PositiveOrZero BigDecimal stockGrams,
    @NotNull @PositiveOrZero BigDecimal minStockGrams
) {}