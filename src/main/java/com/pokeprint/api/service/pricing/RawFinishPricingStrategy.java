package com.pokeprint.api.service.pricing;

import com.pokeprint.api.domain.entity.PricingConfiguration;
import com.pokeprint.api.domain.enums.FinishType;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class RawFinishPricingStrategy implements FinishPricingStrategy {
    @Override
    public BigDecimal calculateFinishCost(BigDecimal basePrintCost, FinishType finishType, PricingConfiguration config) {
        return BigDecimal.ZERO; // Raw tem acréscimo zero
    }

    @Override
    public boolean supports(FinishType finishType) {
        return FinishType.RAW == finishType;
    }
}
