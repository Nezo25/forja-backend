package com.pokeprint.api.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ShowcaseSlotResponseDTO(
    Integer id,
    Integer slotNumber,
    Long productId,
    String productName,
    String productCategory,
    String productImageUrl,
    BigDecimal originalPrice,
    BigDecimal promotionalPrice,
    BigDecimal discountAmount,
    Integer discountPercentage,
    String badgeLabel,
    Boolean allowCouponStacking,
    Boolean isActive,
    String mysteryTitle,
    String mysterySubtitle,
    String mysteryCategory,
    String mysteryImageUrl,
    String mysteryWhatsappText,
    LocalDateTime updatedAt
) {}
