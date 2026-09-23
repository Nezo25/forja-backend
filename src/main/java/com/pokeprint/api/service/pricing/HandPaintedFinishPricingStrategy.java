package com.pokeprint.api.service.pricing;

import com.pokeprint.api.domain.entity.PricingConfiguration;
import com.pokeprint.api.domain.enums.FinishType;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class HandPaintedFinishPricingStrategy implements FinishPricingStrategy {
    
    @Override
    public BigDecimal calculateFinishCost(BigDecimal basePrintCost, FinishType finishType, PricingConfiguration config) {
        // Multiplicador customizável diretamente pelo banco
        return basePrintCost.multiply(config.getPaintingMultiplier()).setScale(2, RoundingMode.HALF_EVEN);
    }

    @Override
    public boolean supports(FinishType finishType) {
        return FinishType.HAND_PAINTED == finishType;
    }
}
