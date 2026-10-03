package com.pokeprint.api.dto;

import com.pokeprint.api.domain.ProductLanguage;
import com.pokeprint.api.domain.TcgItemType;
import com.pokeprint.api.domain.TcgProduct;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TcgProductResponseDTO(
    Long id,
    String name,
    TcgItemType itemType,
    String expansionName,
    ProductLanguage language,
    BigDecimal price,
    Integer stockQuantity,
    String imageUrl,
    Boolean isActive,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    boolean available
) {
    public static TcgProductResponseDTO fromEntity(TcgProduct product) {
        return new TcgProductResponseDTO(
            product.getId(),
            product.getName(),
            product.getItemType(),
            product.getExpansionName(),
            product.getLanguage(),
            product.getPrice(),
            product.getStockQuantity(),
            product.getImageUrl(),
            product.getIsActive(),
            product.getCreatedAt(),
            product.getUpdatedAt(),
            product.getStockQuantity() > 0 && product.getIsActive()
        );
    }
}
