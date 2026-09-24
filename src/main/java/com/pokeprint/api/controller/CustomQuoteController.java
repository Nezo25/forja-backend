package com.pokeprint.api.controller;

import com.pokeprint.api.domain.entity.CustomQuoteRequest;
import com.pokeprint.api.domain.entity.PrintOrder;
import com.pokeprint.api.dto.request.CustomQuoteAnalysisDTO;
import com.pokeprint.api.dto.request.CustomQuoteConvertDTO;
import com.pokeprint.api.dto.request.CustomQuoteSubmitDTO;
import com.pokeprint.api.service.CustomQuoteService;
import com.pokeprint.api.repository.CustomQuoteRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/quotes")
@RequiredArgsConstructor
public class CustomQuoteController {

    private final CustomQuoteService quoteService;
    private final CustomQuoteRepository quoteRepository;

    @GetMapping
    public ResponseEntity<List<CustomQuoteRequest>> getAllQuotes() {
        return ResponseEntity.ok(quoteRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<CustomQuoteRequest> submitQuote(@Valid @RequestBody CustomQuoteSubmitDTO request) {
        CustomQuoteRequest saved = quoteService.submitQuote(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(saved.getId())
                .toUri();
        return ResponseEntity.created(location).body(saved);
    }

    @PatchMapping("/{id}/analyze")
    public ResponseEntity<CustomQuoteRequest> analyzeQuote(
            @PathVariable Long id, 
            @Valid @RequestBody CustomQuoteAnalysisDTO request) {
        CustomQuoteRequest updated = quoteService.analyzeQuote(id, request);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/{id}/convert-to-order")
    public ResponseEntity<PrintOrder> convertToOrder(
            @PathVariable Long id, 
            @Valid @RequestBody CustomQuoteConvertDTO request) {
        PrintOrder order = quoteService.convertToOrder(id, request);
        return ResponseEntity.ok(order);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuote(@PathVariable Long id) {
        if (!quoteRepository.existsById(id)) return ResponseEntity.notFound().build();
        quoteRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}