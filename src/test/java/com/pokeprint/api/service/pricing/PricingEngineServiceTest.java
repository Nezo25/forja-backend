package com.pokeprint.api.service.pricing;

import com.pokeprint.api.domain.entity.PricingConfiguration;
import com.pokeprint.api.domain.enums.FinishType;
import com.pokeprint.api.repository.PricingConfigurationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PricingEngineServiceTest {

    @Mock
    private PricingConfigurationRepository configRepository;

    private PricingEngineService pricingEngineService;

    @BeforeEach
    void setUp() {
        List<FinishPricingStrategy> strategies = List.of(
                new RawFinishPricingStrategy(),
                new PrimerFinishPricingStrategy(),
                new HandPaintedFinishPricingStrategy()
        );
        pricingEngineService = new PricingEngineService(strategies, configRepository);
    }

    @Test
    void shouldCalculateCorrectlyForRawFinish() {
        PricingConfiguration config = new PricingConfiguration();
        config.setMachineHourlyRate(new BigDecimal("10.00")); // R$ 10/hora
        when(configRepository.findById(1L)).thenReturn(Optional.of(config));

        // 100g * 0.15 = 15.00
        // 120min = 2h -> 2 * 10 = 20.00
        // Base = 35.00. Raw = +0. Total = 35.00
        BigDecimal result = pricingEngineService.calculateItemUnitPrice(
                new BigDecimal("100.00"), 
                new BigDecimal("0.15"), 
                120, 
                FinishType.RAW
        );

        assertEquals(new BigDecimal("35.00"), result);
    }

    @Test
    void shouldCalculateCorrectlyForHandPaintedFinish() {
        PricingConfiguration config = new PricingConfiguration();
        config.setMachineHourlyRate(new BigDecimal("10.00"));
        config.setPaintingMultiplier(new BigDecimal("1.50"));
        when(configRepository.findById(1L)).thenReturn(Optional.of(config));

        // Base = 35.00. Hand Painted = Base * 1.5 = 52.50. Total = 35 + 52.50 = 87.50
        BigDecimal result = pricingEngineService.calculateItemUnitPrice(
                new BigDecimal("100.00"), 
                new BigDecimal("0.15"), 
                120, 
                FinishType.HAND_PAINTED
        );

        assertEquals(new BigDecimal("87.50"), result);
    }
}
