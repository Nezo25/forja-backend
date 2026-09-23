package com.pokeprint.api.domain.entity;

import com.pokeprint.api.domain.enums.CashFlowType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "cash_flow_entries")
@Getter
@Setter
public class CashFlowEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private CashFlowType type;

    @Column(nullable = false)
    private String description;

    @Column(name = "related_order_id")
    private Long relatedOrderId;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
