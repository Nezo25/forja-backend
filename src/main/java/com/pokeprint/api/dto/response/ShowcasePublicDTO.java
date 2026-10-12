package com.pokeprint.api.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record ShowcasePublicDTO(
    List<PublicSlotDTO> slots,
    MysteryDropPublicDTO mysteryDrop
) {
    public record PublicSlotDTO(
        Integer slotNumber,
        Long productId,
        String name,
        String category,
        String primaryType,
        String secondaryType,
        String scale,
        String imageUrl,
        BigDecimal originalPrice,
        BigDecimal promotionalPrice,
        BigDecimal discountAmount,
        Integer discountPercentage,
        String badgeLabel,
        Boolean allowCouponStacking
    ) {}

    public record MysteryDropPublicDTO(
        Integer slotNumber,
        String title,
        String subtitle,
        String category,
        String imageUrl,
        String whatsappText
    ) {}
}
