package com.pokeprint.api.service.pricing;

import com.pokeprint.api.domain.entity.PricingConfiguration;
import com.pokeprint.api.domain.enums.FinishType;
import com.pokeprint.api.repository.PricingConfigurationRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class PricingEngineService {

    private final List<FinishPricingStrategy> finishPricingStrategies;
    private final PricingConfigurationRepository configRepository;

    public PricingEngineService(List<FinishPricingStrategy> finishPricingStrategies, 
                                PricingConfigurationRepository configRepository) {
        this.finishPricingStrategies = finishPricingStrategies;
        this.configRepository = configRepository;
    }

    public BigDecimal calculateItemUnitPrice(BigDecimal gramsUsed, BigDecimal costPerGram, 
                                             Integer printTimeMinutes, FinishType finishType) {
        
        // Carrega as taxas customizadas do painel admin (ID=1 fixo para singleton config)
        PricingConfiguration config = configRepository.findById(1L)
                .orElseThrow(() -> new IllegalStateException("Pricing configuration is missing in database."));

        // 1. Custo do material
        BigDecimal materialCost = gramsUsed.multiply(costPerGram);

        // 2. Custo de máquina
        BigDecimal printHours = new BigDecimal(printTimeMinutes).divide(new BigDecimal("60"), 4, RoundingMode.HALF_EVEN);
        BigDecimal machineCost = printHours.multiply(config.getMachineHourlyRate());

        // 3. Custo Base
        BigDecimal baseCost = materialCost.add(machineCost).setScale(2, RoundingMode.HALF_EVEN);

        // 4. Estratégia
        FinishPricingStrategy strategy = finishPricingStrategies.stream()
                .filter(s -> s.supports(finishType))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No pricing strategy found for finish type: " + finishType));

        // 5. Custo do Acabamento dinâmico
        BigDecimal finishCost = strategy.calculateFinishCost(baseCost, finishType, config);

        // 6. Custo Final Unitário
        return baseCost.add(finishCost).setScale(2, RoundingMode.HALF_EVEN);
    }
}
