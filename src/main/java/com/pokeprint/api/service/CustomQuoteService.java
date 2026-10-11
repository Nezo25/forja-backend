package com.pokeprint.api.service;

import com.pokeprint.api.domain.entity.*;
import com.pokeprint.api.domain.enums.OrderStatus;
import com.pokeprint.api.domain.enums.QuoteStatus;
import com.pokeprint.api.dto.request.CustomQuoteAnalysisDTO;
import com.pokeprint.api.dto.request.CustomQuoteConvertDTO;
import com.pokeprint.api.dto.request.CustomQuoteSubmitDTO;
import com.pokeprint.api.infra.exception.BusinessRuleException;
import com.pokeprint.api.infra.exception.ResourceNotFoundException;
import com.pokeprint.api.repository.CustomerRepository;
import com.pokeprint.api.repository.CustomQuoteRequestRepository;
import com.pokeprint.api.repository.FilamentInventoryRepository;
import com.pokeprint.api.repository.PrintOrderRepository;
import com.pokeprint.api.service.pricing.PricingEngineService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class CustomQuoteService {

    private final CustomQuoteRequestRepository quoteRepository;
    private final CustomerRepository customerRepository;
    private final FilamentInventoryRepository filamentRepository;
    private final PrintOrderRepository printOrderRepository;
    private final PricingEngineService pricingEngineService;

    public CustomQuoteService(CustomQuoteRequestRepository quoteRepository, CustomerRepository customerRepository, 
                              FilamentInventoryRepository filamentRepository, PrintOrderRepository printOrderRepository,
                              PricingEngineService pricingEngineService) {
        this.quoteRepository = quoteRepository;
        this.customerRepository = customerRepository;
        this.filamentRepository = filamentRepository;
        this.printOrderRepository = printOrderRepository;
        this.pricingEngineService = pricingEngineService;
    }

    @Transactional
    public CustomQuoteRequest submitQuote(CustomQuoteSubmitDTO dto) {
        Customer customer = customerRepository.findById(dto.customerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        CustomQuoteRequest quote = new CustomQuoteRequest();
        quote.setCustomer(customer);
        quote.setStlUrl(dto.stlUrl());
        quote.setScale(dto.scale());
        quote.setIntendedMaterial(dto.intendedMaterial());
        quote.setIntendedColor(dto.intendedColor());
        quote.setFinishType(dto.finishType());
        quote.setStatus(QuoteStatus.UNDER_REVIEW);

        return quoteRepository.save(quote);
    }

    @Transactional
    public CustomQuoteRequest analyzeQuote(Long quoteId, CustomQuoteAnalysisDTO dto) {
        CustomQuoteRequest quote = quoteRepository.findById(quoteId)
                .orElseThrow(() -> new ResourceNotFoundException("Quote not found"));

        if (quote.getStatus() != QuoteStatus.UNDER_REVIEW) {
            throw new BusinessRuleException("Only quotes UNDER_REVIEW can be analyzed.");
        }

        FilamentInventory filament = filamentRepository.findById(dto.filamentInventoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Filament not found"));

        BigDecimal finalPrice = pricingEngineService.calculateItemUnitPrice(
                dto.estimatedWeightGrams(),
                filament.getCostPerGram(),
                dto.estimatedPrintTimeMinutes(),
                quote.getFinishType()
        );

        quote.setEstimatedWeightGrams(dto.estimatedWeightGrams());
        quote.setEstimatedPrintTimeMinutes(dto.estimatedPrintTimeMinutes());
        quote.setFilamentInventory(filament);
        quote.setFinalPrice(finalPrice);
        quote.setStatus(QuoteStatus.APPROVED);

        return quoteRepository.save(quote);
    }

    @Transactional
    public PrintOrder convertToOrder(Long quoteId, CustomQuoteConvertDTO dto) {
        CustomQuoteRequest quote = quoteRepository.findById(quoteId)
                .orElseThrow(() -> new ResourceNotFoundException("Quote not found"));

        if (quote.getStatus() != QuoteStatus.APPROVED) {
            throw new BusinessRuleException("Only APPROVED quotes can be converted to an order.");
        }

        PrintOrder order = new PrintOrder();
        order.setCustomer(quote.getCustomer());
        order.setStatus(OrderStatus.PENDING_PAYMENT);
        order.setShippingAddressLine(dto.shippingAddress().addressLine());
        order.setShippingCity(dto.shippingAddress().city());
        order.setShippingState(dto.shippingAddress().state());
        order.setShippingZipCode(dto.shippingAddress().zipCode());
        order.setTotalAmount(quote.getFinalPrice());

        OrderItem item = new OrderItem();
        item.setPokemonModel(null); // It's a custom STL, not in catalog
        item.setFilamentInventory(quote.getFilamentInventory());
        item.setFinishType(quote.getFinishType());
        item.setQuantity(1);
        item.setUnitPrice(quote.getFinalPrice());
        item.setSubtotal(quote.getFinalPrice());

        order.addItem(item);
        
        // Abater estoque (Lock pessimista foi usado idealmente)
        FilamentInventory filament = filamentRepository.findByIdForUpdate(quote.getFilamentInventory().getId())
                .orElseThrow();
        filament.setStockGrams(filament.getStockGrams().subtract(quote.getEstimatedWeightGrams()));

        PrintOrder savedOrder = printOrderRepository.save(order);

        quote.setStatus(QuoteStatus.CONVERTED_TO_ORDER);
        quoteRepository.save(quote);

        return savedOrder;
    }
}
