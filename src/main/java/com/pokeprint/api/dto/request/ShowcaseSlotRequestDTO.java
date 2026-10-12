package com.pokeprint.api.dto.request;

import java.math.BigDecimal;

public record ShowcaseSlotRequestDTO(
    Long productId,
    BigDecimal promotionalPrice,
    String badgeLabel,
    Boolean allowCouponStacking,
    Boolean isActive,
    String mysteryTitle,
    String mysterySubtitle,
    String mysteryCategory,
    String mysteryImageUrl,
    String mysteryWhatsappText
) {}
