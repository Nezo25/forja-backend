package com.pokeprint.api.controller;

import com.pokeprint.api.domain.entity.PrintOrder;
import com.pokeprint.api.dto.request.CreateOrderRequestDTO;
import com.pokeprint.api.dto.request.StatusUpdateRequestDTO;
import com.pokeprint.api.dto.response.OrderItemResponseDTO;
import com.pokeprint.api.dto.response.OrderResponseDTO;
import com.pokeprint.api.infra.exception.ResourceNotFoundException;
import com.pokeprint.api.repository.PrintOrderRepository;
import com.pokeprint.api.service.OrderService;
import com.pokeprint.api.service.OrderStatusTransitionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class PrintOrderController {

    private final OrderService orderService;
    private final OrderStatusTransitionService statusTransitionService;
    private final PrintOrderRepository printOrderRepository;

    @GetMapping
    public ResponseEntity<List<OrderResponseDTO>> getAllOrders() {
        List<OrderResponseDTO> orders = printOrderRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(orders);
    }

    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder(@Valid @RequestBody CreateOrderRequestDTO request) {
        OrderResponseDTO response = orderService.createOrder(request);
        
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
                
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> getOrder(@PathVariable Long id) {
        PrintOrder order = printOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        
        return ResponseEntity.ok(mapToResponse(order));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponseDTO> updateStatus(
            @PathVariable Long id, 
            @Valid @RequestBody StatusUpdateRequestDTO request) {
        PrintOrder updatedOrder = statusTransitionService.transitionTo(id, request.status());
        return ResponseEntity.ok(mapToResponse(updatedOrder));
    }

    private OrderResponseDTO mapToResponse(PrintOrder order) {
        List<OrderItemResponseDTO> itemDTOs = order.getItems().stream()
                .map(item -> new OrderItemResponseDTO(
                        item.getId(),
                        item.getPokemonModel().getId(),
                        item.getPokemonModel().getName(),
                        item.getFilamentInventory().getId(),
                        item.getFinishType(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getSubtotal()
                ))
                .toList();

        return new OrderResponseDTO(
                order.getId(),
                order.getCustomer().getId(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getTrackingCode(),
                order.getShippingAddressLine(),
                order.getShippingCity(),
                order.getShippingState(),
                order.getShippingZipCode(),
                itemDTOs,
                order.getCreatedAt()
        );
    }
}
