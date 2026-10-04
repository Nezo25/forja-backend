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
