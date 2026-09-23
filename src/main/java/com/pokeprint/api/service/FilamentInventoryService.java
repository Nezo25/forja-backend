package com.pokeprint.api.service;

import com.pokeprint.api.domain.entity.CashFlowTransaction;
import com.pokeprint.api.domain.entity.FilamentInventory;
import com.pokeprint.api.domain.enums.TransactionCategory;
import com.pokeprint.api.domain.enums.TransactionType;
import com.pokeprint.api.dto.request.FilamentRestockRequestDTO;
import com.pokeprint.api.infra.exception.ResourceNotFoundException;
import com.pokeprint.api.repository.CashFlowTransactionRepository;
import com.pokeprint.api.repository.FilamentInventoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
public class FilamentInventoryService {

    private final FilamentInventoryRepository filamentRepository;
    private final CashFlowTransactionRepository cashFlowRepository;

    public FilamentInventoryService(FilamentInventoryRepository filamentRepository, CashFlowTransactionRepository cashFlowRepository) {
        this.filamentRepository = filamentRepository;
        this.cashFlowRepository = cashFlowRepository;
    }

    @Transactional
    public FilamentInventory restock(Long id, FilamentRestockRequestDTO dto) {
        FilamentInventory filament = filamentRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new ResourceNotFoundException("Filament not found"));

        BigDecimal currentStock = filament.getStockGrams();
        BigDecimal currentTotalValue = currentStock.multiply(filament.getCostPerGram());
        
        filament.setStockGrams(currentStock.add(dto.addedGrams()));
        
        BigDecimal newTotalValue = currentTotalValue.add(dto.totalCost());
        filament.setCostPerGram(newTotalValue.divide(filament.getStockGrams(), 4, RoundingMode.HALF_EVEN));

        filamentRepository.save(filament);

        CashFlowTransaction expense = new CashFlowTransaction();
        expense.setTransactionType(TransactionType.EXPENSE);
        expense.setCategory(TransactionCategory.FILAMENT_PURCHASE);
        expense.setAmount(dto.totalCost().setScale(2, RoundingMode.HALF_EVEN));
        expense.setDescription(String.format("Reposição de %.2fg de %s %s", dto.addedGrams(), filament.getMaterialType(), filament.getColorName()));
        expense.setTransactionDate(LocalDateTime.now());
        
        cashFlowRepository.save(expense);

        return filament;
    }
}
