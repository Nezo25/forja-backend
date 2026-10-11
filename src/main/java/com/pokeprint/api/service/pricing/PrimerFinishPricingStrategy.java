package com.pokeprint.api.service.pricing;

import com.pokeprint.api.domain.entity.PricingConfiguration;
import com.pokeprint.api.domain.enums.FinishType;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class PrimerFinishPricingStrategy implements FinishPricingStrategy {

    @Override
    public BigDecimal calculateFinishCost(BigDecimal basePrintCost, FinishType finishType, PricingConfiguration config) {
        // Custo fixo + percentual base dinâmicos (editáveis pelo Chico)
        BigDecimal percentageCost = basePrintCost.multiply(config.getPrimerConsumablePercentage());
        return config.getPrimerFixedRate().add(percentageCost).setScale(2, RoundingMode.HALF_EVEN);
    }

    @Override
    public boolean supports(FinishType finishType) {
        return FinishType.PRIMER == finishType;
    }
}
