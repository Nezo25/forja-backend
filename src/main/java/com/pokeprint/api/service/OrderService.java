package com.pokeprint.api.service;

import com.pokeprint.api.dto.request.CreateOrderRequestDTO;
import com.pokeprint.api.dto.response.OrderResponseDTO;

public interface OrderService {
    OrderResponseDTO createOrder(CreateOrderRequestDTO request);
}
