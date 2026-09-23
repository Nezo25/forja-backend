package com.pokeprint.api.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CashFlowSummaryDTO(
    LocalDateTime startDate,
    LocalDateTime endDate,
    BigDecimal totalIncome,
    BigDecimal totalExpense,
    BigDecimal netBalance
) {}
