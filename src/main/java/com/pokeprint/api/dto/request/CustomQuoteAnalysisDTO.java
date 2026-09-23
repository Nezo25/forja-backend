package com.pokeprint.api.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record CustomQuoteAnalysisDTO(
    @NotNull @Positive BigDecimal estimatedWeightGrams,
    @NotNull @Positive Integer estimatedPrintTimeMinutes,
    @NotNull Long filamentInventoryId
) {}
