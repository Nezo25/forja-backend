package com.pokeprint.api.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ShippingAddressDTO(
    @NotBlank String addressLine,
    @NotBlank String city,
    @NotBlank String state,
    @NotBlank String zipCode
) {}
