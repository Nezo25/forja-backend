package com.pokeprint.api.controller;

import com.pokeprint.api.domain.entity.CashFlowTransaction;
import com.pokeprint.api.domain.enums.TransactionType;
import com.pokeprint.api.dto.request.ExpenseRequestDTO;
import com.pokeprint.api.dto.response.CashFlowSummaryDTO;
import com.pokeprint.api.service.CashFlowService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/admin/cash-flow")
@RequiredArgsConstructor
public class CashFlowController {

    private final CashFlowService cashFlowService;

    @PostMapping("/expenses")
    public ResponseEntity<CashFlowTransaction> registerExpense(@Valid @RequestBody ExpenseRequestDTO request) {
        CashFlowTransaction tx = cashFlowService.registerExpense(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(tx.getId()).toUri();
        return ResponseEntity.created(location).body(tx);
    }

    @GetMapping
    public ResponseEntity<Page<CashFlowTransaction>> getStatement(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) TransactionType type,
            Pageable pageable) {
        return ResponseEntity.ok(cashFlowService.getStatement(startDate, endDate, type, pageable));
    }

    @GetMapping("/summary")
    public ResponseEntity<CashFlowSummaryDTO> getSummary(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(cashFlowService.getSummary(startDate, endDate));
    }
}
