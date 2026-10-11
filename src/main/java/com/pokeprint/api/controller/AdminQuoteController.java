package com.pokeprint.api.controller;

import com.pokeprint.api.domain.entity.CustomQuoteRequest;
import com.pokeprint.api.domain.entity.PrintOrder;
import com.pokeprint.api.domain.enums.QuoteStatus;
import com.pokeprint.api.domain.enums.KanbanColumn;
import com.pokeprint.api.repository.CustomQuoteRequestRepository;
import com.pokeprint.api.repository.PrintOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/quotes")
@RequiredArgsConstructor
public class AdminQuoteController {

    private final CustomQuoteRequestRepository quoteRepository;
    private final PrintOrderRepository printOrderRepository;

    @GetMapping
    public ResponseEntity<List<CustomQuoteRequest>> getAllQuotes() {
        return ResponseEntity.ok(quoteRepository.findAll());
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<CustomQuoteRequest> rejectQuote(@PathVariable Long id) {
        CustomQuoteRequest quote = quoteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Quote n\u00e3o encontrado"));
        
        quote.setStatus(QuoteStatus.REJECTED);
        quoteRepository.save(quote);
        return ResponseEntity.ok(quote);
    }

    @PostMapping("/{id}/convert-to-kanban")
    public ResponseEntity<PrintOrder> convertToKanban(@PathVariable Long id) {
        CustomQuoteRequest quote = quoteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Quote n\u00e3o encontrado"));
        
        quote.setStatus(QuoteStatus.CONVERTED_TO_ORDER);
        quoteRepository.save(quote);

        PrintOrder order = new PrintOrder();
        order.setShortCode("OS-2026-Q" + id);
        order.setCustomer(quote.getCustomer()); // Assuming quote has getCustomer()
        order.setKanbanColumn(KanbanColumn.NEW_LEAD);
        order.setTags("[STL_CUSTOM]");
        // Other fields like totalAmount would normally come from Quote approval analysis
        order.setTotalAmount(java.math.BigDecimal.ZERO);
        order.setShippingAddressLine("Endere\u00e7o a combinar");
        order.setShippingCity("-");
        order.setShippingState("-");
        order.setShippingZipCode("-");
        order.setStatus(com.pokeprint.api.domain.enums.OrderStatus.LEAD_WHATSAPP);
        
        PrintOrder saved = printOrderRepository.save(order);
        return ResponseEntity.ok(saved);
    }
}
