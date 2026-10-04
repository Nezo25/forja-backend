package com.pokeprint.api.dto;

import com.pokeprint.api.dto.request.OrderItemRequestDTO;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record LeadCaptureRequestDTO(
    @NotBlank String customerName,
    @NotBlank String customerPhone,
    @NotBlank @Email String customerEmail,
    String couponCode,
    String observations,
    @NotBlank String shippingAddressLine,
    @NotBlank String shippingCity,
    @NotBlank String shippingState,
    @NotBlank String shippingZipCode,
    @NotNull List<OrderItemRequestDTO> items
) {}
