package com.pokeprint.api.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PokemonModelResponseDTO(
    Long id,
    Integer pokedexNumber,
    String name,
    Integer generation,
    String primaryType,
    String secondaryType,
    String scale,
    Integer basePrintTimeMinutes,
    BigDecimal defaultFilamentGrams,
    String imageUrl,
    Boolean isActive,
    LocalDateTime createdAt
) {}
