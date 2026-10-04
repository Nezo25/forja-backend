package com.pokeprint.api.dto;

import java.math.BigDecimal;
import java.util.List;

public record LeadCaptureResponseDTO(
    String shortCode,
    String customerName,
    BigDecimal subtotal,
    BigDecimal discountAmount,
    BigDecimal totalAmount,
    String whatsappLink,
    List<String> tags
) {}
