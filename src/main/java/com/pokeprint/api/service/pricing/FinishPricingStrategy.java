package com.pokeprint.api.service.pricing;

import com.pokeprint.api.domain.entity.PricingConfiguration;
import com.pokeprint.api.domain.enums.FinishType;
import java.math.BigDecimal;

public interface FinishPricingStrategy {
    BigDecimal calculateFinishCost(BigDecimal basePrintCost, FinishType finishType, PricingConfiguration config);
    boolean supports(FinishType finishType);
}
