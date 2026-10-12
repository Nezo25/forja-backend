package com.pokeprint.api.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record FinanceSummaryResponseDTO(
    BigDecimal totalRealRevenue,
    Long totalConfirmedOrders,
    BigDecimal pipelinePotentialRevenue,
    Long activeLeadsCount,
    BigDecimal averageTicket,
    List<MonthlySalesDTO> monthlySales
) {}
