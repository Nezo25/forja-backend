package com.pokeprint.api.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record PokemonModelRequestDTO(
    Integer pokedexNumber,
    @NotBlank String name,
    @NotNull @Min(1) Integer generation,
    @NotBlank String primaryType,
    String secondaryType,
    @NotBlank String scale,
    @NotNull @Positive Integer basePrintTimeMinutes,
    @NotNull @Positive BigDecimal defaultFilamentGrams,
    String imageUrl
) {}
