package com.pokeprint.api.domain.entity;

import com.pokeprint.api.domain.enums.FinishType;
import com.pokeprint.api.domain.enums.QuoteStatus;
import com.pokeprint.api.domain.enums.MaterialType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "custom_quote_requests")
@Getter
@Setter
public class CustomQuoteRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "stl_url", nullable = false)
    private String stlUrl;

    @Column(length = 50)
    private String scale;

    @Enumerated(EnumType.STRING)
    @Column(name = "intended_material", length = 50)
    private MaterialType intendedMaterial;

    @Column(name = "intended_color", length = 50)
    private String intendedColor;

    @Enumerated(EnumType.STRING)
    @Column(name = "finish_type", nullable = false, length = 50)
    private FinishType finishType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private QuoteStatus status;

    @Column(name = "estimated_weight_grams", precision = 10, scale = 2)
    private BigDecimal estimatedWeightGrams;

    @Column(name = "estimated_print_time_minutes")
    private Integer estimatedPrintTimeMinutes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "filament_inventory_id")
    private FilamentInventory filamentInventory;

    @Column(name = "final_price", precision = 10, scale = 2)
    private BigDecimal finalPrice;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
