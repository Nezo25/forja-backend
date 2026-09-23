package com.pokeprint.api.domain.entity;

import com.pokeprint.api.domain.enums.FinishType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "order_items")
@Getter
@Setter
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "print_order_id", nullable = false)
    private PrintOrder printOrder;

    // Modificacao: Pode ser nulo para o modulo Custom Quote (orcamento de fora do catalogo)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pokemon_model_id")
    private PokemonModel pokemonModel;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "filament_inventory_id", nullable = false)
    private FilamentInventory filamentInventory;

    @Enumerated(EnumType.STRING)
    @Column(name = "finish_type", nullable = false, length = 50)
    private FinishType finishType;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
