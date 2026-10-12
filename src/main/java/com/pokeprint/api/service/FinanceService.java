package com.pokeprint.api.service;

import com.pokeprint.api.dto.response.FinanceSummaryResponseDTO;

public interface FinanceService {
    FinanceSummaryResponseDTO getFinanceSummary(Integer year);
}
