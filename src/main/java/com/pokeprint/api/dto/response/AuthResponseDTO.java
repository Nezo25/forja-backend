package com.pokeprint.api.dto.response;

public record AuthResponseDTO(
    String accessToken,
    String tokenType,
    Long userId,
    String userRole,
    String email,
    String fullName
) {}
