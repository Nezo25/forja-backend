package com.pokeprint.api.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record CreateOrderRequestDTO(
    @NotNull Long customerId,
    @NotEmpty @Valid List<OrderItemRequestDTO> items,
    @NotNull @Valid ShippingAddressDTO shippingAddress,
    String notes
) {}
