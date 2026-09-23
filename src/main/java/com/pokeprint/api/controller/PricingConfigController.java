package com.pokeprint.api.controller;

import com.pokeprint.api.domain.entity.PricingConfiguration;
import com.pokeprint.api.repository.PricingConfigurationRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/pricing-config")
@RequiredArgsConstructor
public class PricingConfigController {

    private final PricingConfigurationRepository configRepository;

    @GetMapping
    public ResponseEntity<PricingConfiguration> getConfig() {
        return configRepository.findById(1L)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping
    public ResponseEntity<PricingConfiguration> updateConfig(@Valid @RequestBody PricingConfiguration request) {
        PricingConfiguration config = configRepository.findById(1L).orElse(new PricingConfiguration());
        config.setId(1L);
        config.setMachineHourlyRate(request.getMachineHourlyRate());
        config.setPrimerFixedRate(request.getPrimerFixedRate());
        config.setPrimerConsumablePercentage(request.getPrimerConsumablePercentage());
        config.setPaintingMultiplier(request.getPaintingMultiplier());
        
        return ResponseEntity.ok(configRepository.save(config));
    }
}
