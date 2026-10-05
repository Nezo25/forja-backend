package com.pokeprint.api.controller;

import com.pokeprint.api.domain.entity.PrintOrder;
import com.pokeprint.api.domain.enums.KanbanColumn;
import com.pokeprint.api.dto.response.OrderKanbanDTO;
import com.pokeprint.api.repository.PrintOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private final PrintOrderRepository printOrderRepository;

    @GetMapping("/search")
    public ResponseEntity<List<OrderKanbanDTO>> searchOrders(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String shortCode) {
        
        List<PrintOrder> orders = printOrderRepository.searchOrders(email, phone, shortCode);
        List<OrderKanbanDTO> dtos = orders.stream().map(this::toKanbanDTO).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/kanban")
    public ResponseEntity<Map<KanbanColumn, List<OrderKanbanDTO>>> getKanbanBoard() {
        List<PrintOrder> orders = printOrderRepository.findAll();
        
        Map<KanbanColumn, List<OrderKanbanDTO>> board = orders.stream()
                .map(this::toKanbanDTO)
                .collect(Collectors.groupingBy(OrderKanbanDTO::kanbanColumn));
                
        // Ensure all columns are present even if empty
        for (KanbanColumn column : KanbanColumn.values()) {
            board.putIfAbsent(column, List.of());
        }

        return ResponseEntity.ok(board);
    }

    @PatchMapping("/{id}/kanban-column")
    public ResponseEntity<OrderKanbanDTO> updateKanbanColumn(
            @PathVariable Long id,
            @RequestParam KanbanColumn column) {
        
        PrintOrder order = printOrderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado"));
                
        order.setKanbanColumn(column);
        printOrderRepository.save(order);
        
        return ResponseEntity.ok(toKanbanDTO(order));
    }


    @PatchMapping("/{id}/approve")
    public ResponseEntity<OrderKanbanDTO> approveOrder(@PathVariable Long id) {
        PrintOrder order = printOrderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado"));
        order.setKanbanColumn(KanbanColumn.NEGOTIATING_APPROVED);
        printOrderRepository.save(order);
        return ResponseEntity.ok(toKanbanDTO(order));
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<OrderKanbanDTO> rejectOrder(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        PrintOrder order = printOrderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado"));
        order.setKanbanColumn(KanbanColumn.CANCELLED);
        
        String reason = payload.get("rejectionReason");
        if (reason != null && !reason.isBlank()) {
            String currentTags = order.getTags();
            order.setTags(currentTags != null && !currentTags.isBlank() ? currentTags + ",MOTIVO:" + reason.replace(",", " ") : "MOTIVO:" + reason.replace(",", " "));
        }
        printOrderRepository.save(order);
        return ResponseEntity.ok(toKanbanDTO(order));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        if (!printOrderRepository.existsById(id)) return ResponseEntity.notFound().build();
        printOrderRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private OrderKanbanDTO toKanbanDTO(PrintOrder order) {

        List<String> tagList = order.getTags() != null && !order.getTags().isBlank() 
                ? Arrays.asList(order.getTags().split(",")) 
                : List.of();
                
        return new OrderKanbanDTO(
                order.getId(),
                order.getShortCode(),
                order.getCustomer().getName(),
                order.getCustomer().getPhone(),
                order.getCustomer().getEmail(),
                order.getTotalAmount(),
                order.getKanbanColumn(),
                tagList
        );
    }
}
