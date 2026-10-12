package com.pokeprint.api.dto.response;

import java.time.LocalDateTime;

public record CustomerReviewResponseDTO(
    Long id,
    String customerName,
    Integer rating,
    String comment,
    String productName,
    String customerCity,
    Boolean isApproved,
    LocalDateTime createdAt
) {}
