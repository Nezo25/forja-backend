package com.pokeprint.api.dto.request;

import com.pokeprint.api.domain.enums.FinishType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record QuoteRequestDTO(
    @NotNull Long customerId,
    @NotNull @Positive BigDecimal estimatedWeightGrams,
    @NotNull @Positive Integer estimatedPrintTimeMinutes,
    @NotNull String stlUrl,
    @NotNull FinishType finishType
) {}
