package com.pokeprint.api.dto;

import com.pokeprint.api.domain.ProductLanguage;
import com.pokeprint.api.domain.TcgItemType;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record TcgProductRequestDTO(
    @NotBlank @Size(max = 255) String name,
    @NotNull TcgItemType itemType,
    @NotBlank String expansionName,
    @NotNull ProductLanguage language,
    @NotNull @DecimalMin("0.01") BigDecimal price,
    @NotNull @Min(0) Integer stockQuantity,
    String imageUrl,
    Boolean isActive
) {}
