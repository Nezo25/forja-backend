package com.pokeprint.api.repository;

import com.pokeprint.api.domain.entity.PricingConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PricingConfigurationRepository extends JpaRepository<PricingConfiguration, Long> {
}
