package com.pokeprint.api.dto.request;

import com.pokeprint.api.domain.enums.TransactionCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ExpenseRequestDTO(
    @NotNull @Positive BigDecimal amount,
    @NotNull TransactionCategory category,
    @NotBlank String description,
    LocalDateTime transactionDate
) {}
