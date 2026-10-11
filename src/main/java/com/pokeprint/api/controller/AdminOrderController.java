package com.pokeprint.api.controller;

import com.pokeprint.api.domain.entity.PrintOrder;
import com.pokeprint.api.domain.enums.KanbanColumn;
import com.pokeprint.api.dto.response.OrderKanbanDTO;
import com.pokeprint.api.dto.response.OrderItemKanbanDTO;
import com.pokeprint.api.repository.PrintOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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
        
        // Remove qualquer trava ou obrigatoriedade de pesagem/alocação de filamento
        // Move diretamente para a Fila 3D (SLICING_QUEUE)
        order.setKanbanColumn(KanbanColumn.SLICING_QUEUE);
        
        // Atualiza status do pedido se estiver em fase inicial
        if (order.getStatus() == com.pokeprint.api.domain.enums.OrderStatus.LEAD_WHATSAPP 
                || order.getStatus() == com.pokeprint.api.domain.enums.OrderStatus.PENDING_PAYMENT) {
            order.setStatus(com.pokeprint.api.domain.enums.OrderStatus.IN_QUEUE);
        }
        
        printOrderRepository.save(order);
        return ResponseEntity.ok(toKanbanDTO(order));
    }

    @PatchMapping("/{id}/internal-notes")
    public ResponseEntity<OrderKanbanDTO> updateInternalNotes(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        PrintOrder order = printOrderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado"));
        
        String notes = payload.get("internalNotes");
        List<String> tags = new ArrayList<>();
        if (order.getTags() != null && !order.getTags().isBlank()) {
            for (String t : order.getTags().split(",")) {
                if (!t.trim().startsWith("OBS_INTERNA:")) {
                    tags.add(t.trim());
                }
            }
        }
        if (notes != null && !notes.isBlank()) {
            tags.add("OBS_INTERNA:" + notes.replace(",", " "));
        }
        order.setTags(String.join(",", tags));
        printOrderRepository.save(order);
        return ResponseEntity.ok(toKanbanDTO(order));
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<OrderKanbanDTO> rejectOrder(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        PrintOrder order = printOrderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado"));
        order.setKanbanColumn(KanbanColumn.CANCELLED);
        order.setStatus(com.pokeprint.api.domain.enums.OrderStatus.CANCELLED);
        
        String reason = payload.get("rejectionReason");
        if (reason != null && !reason.isBlank()) {
            String currentTags = order.getTags();
            order.setTags(currentTags != null && !currentTags.isBlank() ? currentTags + ",MOTIVO:" + reason.replace(",", " ") : "MOTIVO:" + reason.replace(",", " "));
        }
        printOrderRepository.save(order);
        return ResponseEntity.ok(toKanbanDTO(order));
    }

    @org.springframework.transaction.annotation.Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        PrintOrder order = printOrderRepository.findById(id).orElse(null);
        if (order == null) return ResponseEntity.notFound().build();
        
        order.getItems().clear();
        printOrderRepository.delete(order);
        return ResponseEntity.noContent().build();
    }

    private OrderKanbanDTO toKanbanDTO(PrintOrder order) {
        String customerNotes = null;
        String internalNotes = null;
        List<String> displayTags = new ArrayList<>();

        if (order.getTags() != null && !order.getTags().isBlank()) {
            for (String tag : order.getTags().split(",")) {
                String trimmed = tag.trim();
                if (trimmed.startsWith("OBS:")) {
                    customerNotes = trimmed.substring(4).trim();
                } else if (trimmed.startsWith("OBS_INTERNA:")) {
                    internalNotes = trimmed.substring(12).trim();
                } else if (!trimmed.isEmpty()) {
                    displayTags.add(trimmed);
                }
            }
        }

        List<OrderItemKanbanDTO> items = order.getItems().stream().map(item -> {
            String name = "Item Customizado";
            String scale = "1:10 (Padrão)";
            Integer printTime = 0;

            if (item.getPokemonModel() != null) {
                name = item.getPokemonModel().getName();
                if (item.getPokemonModel().getScale() != null && !item.getPokemonModel().getScale().isBlank()) {
                    scale = item.getPokemonModel().getScale();
                }
                if (item.getPokemonModel().getBasePrintTimeMinutes() != null) {
                    printTime = item.getPokemonModel().getBasePrintTimeMinutes();
                }
            } else if (item.getTcgProduct() != null) {
                name = item.getTcgProduct().getName();
                scale = "N/A (TCG)";
                printTime = 0;
            }

            String filament = item.getFilamentInventory() != null 
                    ? item.getFilamentInventory().getColorName() 
                    : "Padrão da Forja";
            String finish = item.getFinishType() != null ? item.getFinishType().name() : "RAW";

            return new OrderItemKanbanDTO(name, item.getQuantity(), finish, filament, scale, printTime);
        }).collect(Collectors.toList());

        return new OrderKanbanDTO(
                order.getId(),
                order.getShortCode(),
                order.getCustomer().getName(),
                order.getCustomer().getPhone(),
                order.getCustomer().getEmail(),
                order.getTotalAmount(),
                order.getKanbanColumn(),
                displayTags,
                items,
                customerNotes,
                internalNotes
        );
    }
}
