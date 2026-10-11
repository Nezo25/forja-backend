package com.pokeprint.api.domain.entity;

import com.pokeprint.api.domain.enums.MaterialType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "filament_inventories")
@Getter
@Setter
public class FilamentInventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "material_type", nullable = false, length = 50)
    private MaterialType materialType;

    @Column(name = "color_name", nullable = false, length = 50)
    private String colorName;

    @Column(name = "hex_code", length = 10)
    private String hexCode;

    @Column(name = "stock_grams", nullable = false, precision = 10, scale = 2)
    private BigDecimal stockGrams;

    @Column(name = "cost_per_gram", nullable = false, precision = 10, scale = 4)
    private BigDecimal costPerGram;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FilamentInventory that)) return false;
        return id != null && id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
