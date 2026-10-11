package com.pokeprint.api.dto.response;

import com.pokeprint.api.domain.enums.OrderStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponseDTO(
    Long id,
    Long customerId,
    OrderStatus status,
    BigDecimal totalAmount,
    String trackingCode,
    String shippingAddressLine,
    String shippingCity,
    String shippingState,
    String shippingZipCode,
    List<OrderItemResponseDTO> items,
    LocalDateTime createdAt
) {}
