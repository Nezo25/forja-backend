package com.pokeprint.api.dto.request;

import com.pokeprint.api.domain.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record StatusUpdateRequestDTO(
    @NotNull OrderStatus status
) {}
