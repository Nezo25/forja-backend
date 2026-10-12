package com.pokeprint.api.controller;

import com.pokeprint.api.dto.response.FinanceSummaryResponseDTO;
import com.pokeprint.api.service.FinanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/finances")
@RequiredArgsConstructor
public class FinanceController {

    private final FinanceService financeService;

    @GetMapping("/summary")
    public ResponseEntity<FinanceSummaryResponseDTO> getFinanceSummary(
            @RequestParam(required = false) Integer year) {
        return ResponseEntity.ok(financeService.getFinanceSummary(year));
    }
}
