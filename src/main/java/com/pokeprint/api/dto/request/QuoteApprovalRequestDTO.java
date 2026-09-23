package com.pokeprint.api.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record QuoteApprovalRequestDTO(
    @NotNull @Positive BigDecimal finalPrice
) {}
