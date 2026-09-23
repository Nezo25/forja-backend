package com.pokeprint.api.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pokemon_models")
@Getter
@Setter
public class PokemonModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pokedex_number")
    private Integer pokedexNumber;

    @Column(nullable = false, length = 100)
    private String name;

    private Integer generation;

    @Column(name = "primary_type", nullable = false, length = 50)
    private String primaryType;

    @Column(name = "secondary_type", length = 50)
    private String secondaryType;

    @Column(nullable = false, length = 50)
    private String scale;

    @Column(name = "base_print_time_minutes", nullable = false)
    private Integer basePrintTimeMinutes;

    @Column(name = "default_filament_grams", nullable = false, precision = 10, scale = 2)
    private BigDecimal defaultFilamentGrams;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

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
        if (!(o instanceof PokemonModel that)) return false;
        return id != null && id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
