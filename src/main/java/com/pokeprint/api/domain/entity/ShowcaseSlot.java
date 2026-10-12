package com.pokeprint.api.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "showcase_slots")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShowcaseSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "slot_number", nullable = false, unique = true)
    private Integer slotNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id")
    private PokemonModel product;

    @Column(name = "promotional_price", precision = 10, scale = 2)
    private BigDecimal promotionalPrice;

    @Column(name = "badge_label", length = 60)
    private String badgeLabel = "⚡ OFERTA DE BANCADA";

    @Column(name = "allow_coupon_stacking", nullable = false)
    private Boolean allowCouponStacking = false;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // Campos exclusivos do Slot 4 (Drop Misterioso)
    @Column(name = "mystery_title", length = 100)
    private String mysteryTitle = "Quem é esse Pokémon?";

    @Column(name = "mystery_subtitle", length = 150)
    private String mysterySubtitle = "Próximo lançamento saindo da bancada";

    @Column(name = "mystery_category", length = 50)
    private String mysteryCategory = "FIGURE_POKEMON";

    @Column(name = "mystery_image_url", length = 500)
    private String mysteryImageUrl;

    @Column(name = "mystery_whatsapp_text", length = 255)
    private String mysteryWhatsappText = "Olá Chico! Vi o drop misterioso da semana no site e quero garantir com desconto exclusivo!";

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
