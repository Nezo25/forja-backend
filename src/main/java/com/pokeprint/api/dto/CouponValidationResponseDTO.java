package com.pokeprint.api.dto;

import java.math.BigDecimal;

public record CouponValidationResponseDTO(
    String code,
    BigDecimal discountAmount,
    BigDecimal newSubtotal
) {}
