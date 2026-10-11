package com.pokeprint.api.service;

import com.pokeprint.api.domain.entity.CashFlowTransaction;
import com.pokeprint.api.domain.entity.FilamentInventory;
import com.pokeprint.api.domain.entity.OrderItem;
import com.pokeprint.api.domain.entity.PrintOrder;
import com.pokeprint.api.domain.enums.TransactionCategory;
import com.pokeprint.api.domain.enums.TransactionType;
import com.pokeprint.api.domain.enums.OrderStatus;
import com.pokeprint.api.infra.exception.InvalidStatusTransitionException;
import com.pokeprint.api.infra.exception.ResourceNotFoundException;
import com.pokeprint.api.repository.CashFlowTransactionRepository;
import com.pokeprint.api.repository.FilamentInventoryRepository;
import com.pokeprint.api.repository.PrintOrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class OrderStatusTransitionService {

    private final PrintOrderRepository printOrderRepository;
    private final FilamentInventoryRepository filamentInventoryRepository;
    private final CashFlowTransactionRepository cashFlowTransactionRepository;

    public OrderStatusTransitionService(PrintOrderRepository printOrderRepository, 
                                        FilamentInventoryRepository filamentInventoryRepository, 
                                        CashFlowTransactionRepository cashFlowTransactionRepository) {
        this.printOrderRepository = printOrderRepository;
        this.filamentInventoryRepository = filamentInventoryRepository;
        this.cashFlowTransactionRepository = cashFlowTransactionRepository;
    }

    @Transactional
    public PrintOrder transitionTo(Long orderId, OrderStatus newStatus) {
        PrintOrder order = printOrderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        
        OrderStatus currentStatus = order.getStatus();
        validateTransition(currentStatus, newStatus);
        
        if (newStatus == OrderStatus.CANCELLED && (currentStatus == OrderStatus.PENDING_PAYMENT || currentStatus == OrderStatus.IN_QUEUE)) {
            for (OrderItem item : order.getItems()) {
                FilamentInventory filament = filamentInventoryRepository.findByIdForUpdate(item.getFilamentInventory().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Filament not found"));
                BigDecimal defaultGrams = item.getPokemonModel() != null ? item.getPokemonModel().getDefaultFilamentGrams() : BigDecimal.ZERO; 
                BigDecimal gramsToReturn = defaultGrams.multiply(new BigDecimal(item.getQuantity()));
                filament.setStockGrams(filament.getStockGrams().add(gramsToReturn));
                filamentInventoryRepository.save(filament);
            }
        }
        
        if (newStatus == OrderStatus.IN_QUEUE && currentStatus == OrderStatus.PENDING_PAYMENT) {
            CashFlowTransaction entry = new CashFlowTransaction();
            entry.setAmount(order.getTotalAmount());
            entry.setTransactionType(TransactionType.INCOME);
            
            boolean hasCustom = order.getItems().stream().anyMatch(i -> i.getPokemonModel() == null);
            entry.setCategory(hasCustom ? TransactionCategory.CUSTOM_QUOTE : TransactionCategory.ORDER_PAYMENT);
            
            entry.setDescription("Pagamento do pedido #" + order.getId());
            entry.setReferenceOrderId(order.getId());
            entry.setTransactionDate(LocalDateTime.now());
            cashFlowTransactionRepository.save(entry);
        }

        order.setStatus(newStatus);
        return printOrderRepository.save(order);
    }

    private void validateTransition(OrderStatus current, OrderStatus next) {
        if (next == OrderStatus.CANCELLED) {
            if (current != OrderStatus.PENDING_PAYMENT && current != OrderStatus.IN_QUEUE) {
                throw new InvalidStatusTransitionException("Cannot cancel order from status " + current);
            }
            return;
        }

        boolean valid = false;
        switch(current) {
            case PENDING_PAYMENT -> valid = (next == OrderStatus.IN_QUEUE);
            case IN_QUEUE -> valid = (next == OrderStatus.PRINTING);
            case PRINTING -> valid = (next == OrderStatus.PAINTING_POST_PROCESSING || next == OrderStatus.SHIPPED);
            case PAINTING_POST_PROCESSING -> valid = (next == OrderStatus.SHIPPED);
            case SHIPPED -> valid = (next == OrderStatus.DELIVERED);
            default -> valid = false;
        }

        if (!valid) {
            throw new InvalidStatusTransitionException("Invalid transition from " + current + " to " + next);
        }
    }
}
