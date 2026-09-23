package com.pokeprint.api.dto.request;

import com.pokeprint.api.domain.enums.FinishType;
import com.pokeprint.api.domain.enums.MaterialType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CustomQuoteSubmitDTO(
    @NotNull Long customerId,
    @NotBlank String stlUrl,
    @NotBlank String scale,
    @NotNull MaterialType intendedMaterial,
    @NotBlank String intendedColor,
    @NotNull FinishType finishType
) {}
