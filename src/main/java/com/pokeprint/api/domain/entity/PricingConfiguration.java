package com.pokeprint.api.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "pricing_configurations")
@Getter
@Setter
public class PricingConfiguration {

    @Id
    private Long id;

    @Column(name = "machine_hourly_rate", nullable = false, precision = 10, scale = 2)
    private BigDecimal machineHourlyRate;

    @Column(name = "primer_fixed_rate", nullable = false, precision = 10, scale = 2)
    private BigDecimal primerFixedRate;

    @Column(name = "primer_consumable_percentage", nullable = false, precision = 5, scale = 4)
    private BigDecimal primerConsumablePercentage;

    @Column(name = "painting_multiplier", nullable = false, precision = 5, scale = 4)
    private BigDecimal paintingMultiplier;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PricingConfiguration that)) return false;
        return id != null && id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
