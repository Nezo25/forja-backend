package com.pokeprint.api.service.impl;

import com.pokeprint.api.domain.entity.PrintOrder;
import com.pokeprint.api.dto.response.FinanceSummaryResponseDTO;
import com.pokeprint.api.dto.response.MonthlySalesDTO;
import com.pokeprint.api.repository.PrintOrderRepository;
import com.pokeprint.api.service.FinanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FinanceServiceImpl implements FinanceService {

    private final PrintOrderRepository printOrderRepository;

    private static final String[] MONTH_NAMES = {
        "Jan", "Fev", "Mar", "Abr", "Mai", "Jun", "Jul", "Ago", "Set", "Out", "Nov", "Dez"
    };

    @Override
    @Transactional(readOnly = true)
    public FinanceSummaryResponseDTO getFinanceSummary(Integer year) {
        BigDecimal totalRealRevenue = printOrderRepository.calculateTotalRevenue();
        long totalConfirmedOrders = printOrderRepository.countCompletedOrders();
        BigDecimal pipelinePotentialRevenue = printOrderRepository.calculatePipelineRevenue();
        long activeLeadsCount = printOrderRepository.countActiveLeads();

        BigDecimal averageTicket = totalConfirmedOrders > 0
                ? totalRealRevenue.divide(BigDecimal.valueOf(totalConfirmedOrders), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        List<PrintOrder> confirmedOrders = printOrderRepository.findAllConfirmedOrders();

        int currentMonthIdx = LocalDate.now().getMonthValue() - 1;
        List<MonthlySalesDTO> monthlySales = new ArrayList<>();

        for (int i = 5; i >= 0; i--) {
            int mIdx = (currentMonthIdx - i + 12) % 12;
            int monthNum = mIdx + 1;
            BigDecimal sum = BigDecimal.ZERO;

            for (PrintOrder order : confirmedOrders) {
                if (order.getCreatedAt() != null && order.getCreatedAt().getMonthValue() == monthNum) {
                    if (year == null || order.getCreatedAt().getYear() == year) {
                        sum = sum.add(order.getTotalAmount() != null ? order.getTotalAmount() : BigDecimal.ZERO);
                    }
                }
            }

            monthlySales.add(new MonthlySalesDTO(MONTH_NAMES[mIdx], monthNum, sum));
        }

        return new FinanceSummaryResponseDTO(
                totalRealRevenue,
                totalConfirmedOrders,
                pipelinePotentialRevenue,
                activeLeadsCount,
                averageTicket,
                monthlySales
        );
    }
}
