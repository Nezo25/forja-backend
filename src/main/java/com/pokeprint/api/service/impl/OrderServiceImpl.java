package com.pokeprint.api.service.impl;

import com.pokeprint.api.domain.entity.*;
import com.pokeprint.api.domain.enums.OrderStatus;
import com.pokeprint.api.dto.request.CreateOrderRequestDTO;
import com.pokeprint.api.dto.request.OrderItemRequestDTO;
import com.pokeprint.api.dto.response.OrderItemResponseDTO;
import com.pokeprint.api.dto.response.OrderResponseDTO;
import com.pokeprint.api.infra.exception.BusinessRuleException;
import com.pokeprint.api.infra.exception.InsufficientStockException;
import com.pokeprint.api.infra.exception.ResourceNotFoundException;
import com.pokeprint.api.repository.CustomerRepository;
import com.pokeprint.api.repository.FilamentInventoryRepository;
import com.pokeprint.api.repository.PokemonModelRepository;
import com.pokeprint.api.repository.PrintOrderRepository;
import com.pokeprint.api.service.OrderService;
import com.pokeprint.api.service.pricing.PricingEngineService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final PrintOrderRepository printOrderRepository;
    private final CustomerRepository customerRepository;
    private final PokemonModelRepository pokemonModelRepository;
    private final FilamentInventoryRepository filamentInventoryRepository;
    private final PricingEngineService pricingEngineService;

    public OrderServiceImpl(PrintOrderRepository printOrderRepository, 
                            CustomerRepository customerRepository, 
                            PokemonModelRepository pokemonModelRepository, 
                            FilamentInventoryRepository filamentInventoryRepository, 
                            PricingEngineService pricingEngineService) {
        this.printOrderRepository = printOrderRepository;
        this.customerRepository = customerRepository;
        this.pokemonModelRepository = pokemonModelRepository;
        this.filamentInventoryRepository = filamentInventoryRepository;
        this.pricingEngineService = pricingEngineService;
    }

    @Override
    @Transactional
    public OrderResponseDTO createOrder(CreateOrderRequestDTO request) {
        // 1. Validar e recuperar Customer
        Customer customer = customerRepository.findById(request.customerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + request.customerId()));

        PrintOrder order = new PrintOrder();
        order.setCustomer(customer);
        order.setStatus(OrderStatus.PENDING_PAYMENT);
        order.setShippingAddressLine(request.shippingAddress().addressLine());
        order.setShippingCity(request.shippingAddress().city());
        order.setShippingState(request.shippingAddress().state());
        order.setShippingZipCode(request.shippingAddress().zipCode());

        BigDecimal totalAmount = BigDecimal.ZERO;

        // 2. Processar cada item do pedido
        for (OrderItemRequestDTO itemDTO : request.items()) {
            // a. Carregar PokemonModel e validar se está ativo
            PokemonModel model = pokemonModelRepository.findById(itemDTO.pokemonModelId())
                    .orElseThrow(() -> new ResourceNotFoundException("PokemonModel not found with id: " + itemDTO.pokemonModelId()));
            
            if (!model.getIsActive()) {
                throw new BusinessRuleException("PokemonModel is not active: " + model.getName());
            }

            // b. Carregar FilamentInventory com Pessimistic Write Lock para evitar problemas de concorrência
            FilamentInventory filament = filamentInventoryRepository.findByIdForUpdate(itemDTO.filamentInventoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("FilamentInventory not found with id: " + itemDTO.filamentInventoryId()));

            // c. Validar e abater o estoque do filamento temporariamente
            BigDecimal gramsNeeded = model.getDefaultFilamentGrams().multiply(new BigDecimal(itemDTO.quantity()));
            if (filament.getStockGrams().compareTo(gramsNeeded) < 0) {
                throw new InsufficientStockException(
                        String.format("Insufficient stock for filament %s %s. Needed: %sg, Available: %sg",
                                filament.getMaterialType(), filament.getColorName(), gramsNeeded, filament.getStockGrams())
                );
            }
            filament.setStockGrams(filament.getStockGrams().subtract(gramsNeeded));
            // NOTA: A persistência do filamento (baixa do estoque) ocorrerá automaticamente no fim da transação (Dirty Checking)

            // d. Calcular valor individual de cada item através da Engine de Precificação
            BigDecimal unitPrice = pricingEngineService.calculateItemUnitPrice(
                    model.getDefaultFilamentGrams(),
                    filament.getCostPerGram(),
                    model.getBasePrintTimeMinutes(),
                    itemDTO.finishType()
            );

            BigDecimal subtotal = unitPrice.multiply(new BigDecimal(itemDTO.quantity()));
            totalAmount = totalAmount.add(subtotal);

            OrderItem orderItem = new OrderItem();
            orderItem.setPokemonModel(model);
            orderItem.setFilamentInventory(filament);
            orderItem.setFinishType(itemDTO.finishType());
            orderItem.setQuantity(itemDTO.quantity());
            orderItem.setUnitPrice(unitPrice);
            orderItem.setSubtotal(subtotal);

            order.addItem(orderItem);
        }

        order.setTotalAmount(totalAmount);

        // 3. Persistir o pedido e seus itens associados
        PrintOrder savedOrder = printOrderRepository.save(order);

        // 4. Mapear e retornar o DTO de resposta
        return mapToResponse(savedOrder);
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
