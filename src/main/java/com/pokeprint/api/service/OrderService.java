package com.pokeprint.api.service;

import com.pokeprint.api.dto.request.CreateOrderRequestDTO;
import com.pokeprint.api.dto.response.OrderResponseDTO;
import com.pokeprint.api.dto.LeadCaptureRequestDTO;
import com.pokeprint.api.dto.LeadCaptureResponseDTO;

public interface OrderService {
    OrderResponseDTO createOrder(CreateOrderRequestDTO request);
    LeadCaptureResponseDTO createLeadCapture(LeadCaptureRequestDTO request);
}
