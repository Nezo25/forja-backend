package com.pokeprint.api.dto.response;

import com.pokeprint.api.domain.enums.ProductCategory;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PokemonModelResponseDTO(
    Long id,
    Integer pokedexNumber,
    String name,
    ProductCategory category,
    Integer generation,
    String primaryType,
    String secondaryType,
    String scale,
    Integer basePrintTimeMinutes,
    BigDecimal defaultFilamentGrams,
    String imageUrl,
    BigDecimal basePrice,
    Boolean isActive,
    LocalDateTime createdAt
) {}
