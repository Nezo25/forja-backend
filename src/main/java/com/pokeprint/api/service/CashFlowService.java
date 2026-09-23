package com.pokeprint.api.service;

import com.pokeprint.api.domain.entity.CashFlowTransaction;
import com.pokeprint.api.domain.enums.TransactionType;
import com.pokeprint.api.dto.request.ExpenseRequestDTO;
import com.pokeprint.api.dto.response.CashFlowSummaryDTO;
import com.pokeprint.api.infra.exception.BusinessRuleException;
import com.pokeprint.api.repository.CashFlowTransactionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
public class CashFlowService {

    private final CashFlowTransactionRepository cashFlowRepository;

    public CashFlowService(CashFlowTransactionRepository cashFlowRepository) {
        this.cashFlowRepository = cashFlowRepository;
    }

    @Transactional
    public CashFlowTransaction registerExpense(ExpenseRequestDTO dto) {
        if (dto.category().name().equals("ORDER_PAYMENT") || dto.category().name().equals("CUSTOM_QUOTE")) {
            throw new BusinessRuleException("Category reserved for income transactions.");
        }

        CashFlowTransaction tx = new CashFlowTransaction();
        tx.setTransactionType(TransactionType.EXPENSE);
        tx.setCategory(dto.category());
        tx.setAmount(dto.amount().setScale(2, RoundingMode.HALF_EVEN));
        tx.setDescription(dto.description());
        tx.setTransactionDate(dto.transactionDate() != null ? dto.transactionDate() : LocalDateTime.now());

        return cashFlowRepository.save(tx);
    }

    @Transactional(readOnly = true)
    public Page<CashFlowTransaction> getStatement(LocalDateTime startDate, LocalDateTime endDate, TransactionType type, Pageable pageable) {
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new BusinessRuleException("startDate cannot be after endDate.");
        }
        return cashFlowRepository.findWithFilters(startDate, endDate, type, pageable);
    }

    @Transactional(readOnly = true)
    public CashFlowSummaryDTO getSummary(LocalDateTime startDate, LocalDateTime endDate) {
        BigDecimal income = cashFlowRepository.sumAmountByTypeAndPeriod(TransactionType.INCOME, startDate, endDate)
                .setScale(2, RoundingMode.HALF_EVEN);
        BigDecimal expense = cashFlowRepository.sumAmountByTypeAndPeriod(TransactionType.EXPENSE, startDate, endDate)
                .setScale(2, RoundingMode.HALF_EVEN);
        
        BigDecimal net = income.subtract(expense);

        return new CashFlowSummaryDTO(startDate, endDate, income, expense, net);
    }
}
