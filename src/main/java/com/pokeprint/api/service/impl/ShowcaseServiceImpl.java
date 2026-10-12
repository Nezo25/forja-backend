package com.pokeprint.api.service.impl;

import com.pokeprint.api.domain.entity.PokemonModel;
import com.pokeprint.api.domain.entity.ShowcaseSlot;
import com.pokeprint.api.dto.request.ShowcaseSlotRequestDTO;
import com.pokeprint.api.dto.response.ShowcasePublicDTO;
import com.pokeprint.api.dto.response.ShowcaseSlotResponseDTO;
import com.pokeprint.api.repository.PokemonModelRepository;
import com.pokeprint.api.repository.ShowcaseSlotRepository;
import com.pokeprint.api.service.ShowcaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ShowcaseServiceImpl implements ShowcaseService {

    private final ShowcaseSlotRepository showcaseSlotRepository;
    private final PokemonModelRepository pokemonModelRepository;

    @Override
    @Transactional(readOnly = true)
    public ShowcasePublicDTO getPublicShowcase() {
        List<ShowcaseSlot> slots = showcaseSlotRepository.findAllByOrderBySlotNumberAsc();
        Map<Integer, ShowcaseSlot> slotMap = new HashMap<>();
        for (ShowcaseSlot s : slots) {
            slotMap.put(s.getSlotNumber(), s);
        }

        // Recupera produtos ativos para fallback se necessário
        List<PokemonModel> fallbackModels = pokemonModelRepository.findByIsActiveTrue(PageRequest.of(0, 10)).getContent();
        Set<Long> usedProductIds = new HashSet<>();

        List<ShowcasePublicDTO.PublicSlotDTO> publicSlots = new ArrayList<>();

        for (int slotNum = 1; slotNum <= 3; slotNum++) {
            ShowcaseSlot slot = slotMap.get(slotNum);
            PokemonModel product = null;
            BigDecimal promoPrice = null;
            String badge = "⚡ OFERTA DE BANCADA";
            boolean allowStacking = false;

            if (slot != null && Boolean.TRUE.equals(slot.getIsActive()) && slot.getProduct() != null && Boolean.TRUE.equals(slot.getProduct().getIsActive())) {
                product = slot.getProduct();
                promoPrice = slot.getPromotionalPrice();
                if (slot.getBadgeLabel() != null && !slot.getBadgeLabel().isBlank()) {
                    badge = slot.getBadgeLabel();
                }
                allowStacking = Boolean.TRUE.equals(slot.getAllowCouponStacking());
                usedProductIds.add(product.getId());
            } else {
                // Fallback de segurança para o primeiro produto ativo disponível
                for (PokemonModel m : fallbackModels) {
                    if (!usedProductIds.contains(m.getId())) {
                        product = m;
                        usedProductIds.add(m.getId());
                        break;
                    }
                }
            }

            if (product != null) {
                BigDecimal origPrice = product.getBasePrice() != null ? product.getBasePrice() : BigDecimal.valueOf(65.00);
                BigDecimal defaultDiscount = origPrice.compareTo(BigDecimal.valueOf(80.00)) >= 0
                        ? BigDecimal.valueOf(16.00)
                        : (origPrice.compareTo(BigDecimal.valueOf(50.00)) >= 0 ? BigDecimal.valueOf(10.00) : BigDecimal.valueOf(8.00));

                BigDecimal finalPromo = (promoPrice != null && promoPrice.compareTo(BigDecimal.ZERO) > 0 && promoPrice.compareTo(origPrice) < 0)
                        ? promoPrice
                        : origPrice.subtract(defaultDiscount);

                String finalBadge = (badge != null && !badge.isBlank() && !badge.equals("⚡ OFERTA DE BANCADA") && !badge.equals("⭐ PEÇA DA SEMANA"))
                        ? badge
                        : "🔥 OFERTA DESTAQUE DA OFICINA";

                BigDecimal discountAmount = origPrice.subtract(finalPromo);
                int discountPercent = 0;
                if (origPrice.compareTo(BigDecimal.ZERO) > 0 && discountAmount.compareTo(BigDecimal.ZERO) > 0) {
                    discountPercent = discountAmount.multiply(BigDecimal.valueOf(100))
                            .divide(origPrice, 0, RoundingMode.HALF_UP).intValue();
                }

                publicSlots.add(new ShowcasePublicDTO.PublicSlotDTO(
                        slotNum,
                        product.getId(),
                        product.getName(),
                        product.getCategory() != null ? product.getCategory().name() : "FIGURE_POKEMON",
                        product.getPrimaryType(),
                        product.getSecondaryType(),
                        product.getScale(),
                        product.getImageUrl(),
                        origPrice,
                        finalPromo,
                        discountAmount,
                        discountPercent,
                        finalBadge,
                        allowStacking
                ));
            }
        }

        // Slot 4: Drop Misterioso
        ShowcaseSlot slot4 = slotMap.get(4);
        ShowcasePublicDTO.MysteryDropPublicDTO mysteryDrop;
        if (slot4 != null) {
            String defaultSilhouette = !publicSlots.isEmpty() ? publicSlots.get(0).imageUrl() : null;
            mysteryDrop = new ShowcasePublicDTO.MysteryDropPublicDTO(
                    4,
                    slot4.getMysteryTitle() != null ? slot4.getMysteryTitle() : "Quem é esse Pokémon?",
                    slot4.getMysterySubtitle() != null ? slot4.getMysterySubtitle() : "Próximo lançamento saindo da bancada",
                    slot4.getMysteryCategory() != null ? slot4.getMysteryCategory() : "FIGURE_POKEMON",
                    slot4.getMysteryImageUrl() != null ? slot4.getMysteryImageUrl() : defaultSilhouette,
                    slot4.getMysteryWhatsappText() != null ? slot4.getMysteryWhatsappText() : "Olá Chico! Vi o drop misterioso da semana no site e quero garantir com desconto exclusivo!"
            );
        } else {
            mysteryDrop = new ShowcasePublicDTO.MysteryDropPublicDTO(
                    4,
                    "Quem é esse Pokémon?",
                    "Próximo lançamento saindo da bancada",
                    "FIGURE_POKEMON",
                    !publicSlots.isEmpty() ? publicSlots.get(0).imageUrl() : null,
                    "Olá Chico! Vi o drop misterioso da semana no site e quero garantir com desconto exclusivo!"
            );
        }

        return new ShowcasePublicDTO(publicSlots, mysteryDrop);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShowcaseSlotResponseDTO> getAdminShowcase() {
        List<ShowcaseSlot> slots = showcaseSlotRepository.findAllByOrderBySlotNumberAsc();
        List<ShowcaseSlotResponseDTO> dtoList = new ArrayList<>();

        for (ShowcaseSlot slot : slots) {
            dtoList.add(toResponseDTO(slot));
        }

        return dtoList;
    }

    @Override
    @Transactional
    public ShowcaseSlotResponseDTO updateSlot(Integer slotNumber, ShowcaseSlotRequestDTO request) {
        if (slotNumber < 1 || slotNumber > 4) {
            throw new IllegalArgumentException("Número de slot inválido. Deve ser entre 1 e 4.");
        }

        ShowcaseSlot slot = showcaseSlotRepository.findBySlotNumber(slotNumber)
                .orElseGet(() -> {
                    ShowcaseSlot s = new ShowcaseSlot();
                    s.setSlotNumber(slotNumber);
                    return s;
                });

        if (slotNumber >= 1 && slotNumber <= 3) {
            if (request.productId() != null) {
                PokemonModel product = pokemonModelRepository.findById(request.productId())
                        .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado para o ID: " + request.productId()));
                slot.setProduct(product);
            } else {
                slot.setProduct(null);
            }

            slot.setPromotionalPrice(request.promotionalPrice());
            if (request.badgeLabel() != null) slot.setBadgeLabel(request.badgeLabel());
            if (request.allowCouponStacking() != null) slot.setAllowCouponStacking(request.allowCouponStacking());
            if (request.isActive() != null) slot.setIsActive(request.isActive());
        } else if (slotNumber == 4) {
            if (request.mysteryTitle() != null) slot.setMysteryTitle(request.mysteryTitle());
            if (request.mysterySubtitle() != null) slot.setMysterySubtitle(request.mysterySubtitle());
            if (request.mysteryCategory() != null) slot.setMysteryCategory(request.mysteryCategory());
            if (request.mysteryImageUrl() != null) slot.setMysteryImageUrl(request.mysteryImageUrl());
            if (request.mysteryWhatsappText() != null) slot.setMysteryWhatsappText(request.mysteryWhatsappText());
            if (request.isActive() != null) slot.setIsActive(request.isActive());
        }

        ShowcaseSlot saved = showcaseSlotRepository.save(slot);
        return toResponseDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isCouponAllowedForProduct(Long productId) {
        if (productId == null) return true;
        List<ShowcaseSlot> slots = showcaseSlotRepository.findAllByOrderBySlotNumberAsc();
        for (ShowcaseSlot s : slots) {
            if (Boolean.TRUE.equals(s.getIsActive()) && s.getProduct() != null && productId.equals(s.getProduct().getId())) {
                if (s.getPromotionalPrice() != null && s.getPromotionalPrice().compareTo(BigDecimal.ZERO) > 0) {
                    return Boolean.TRUE.equals(s.getAllowCouponStacking());
                }
            }
        }
        return true;
    }

    private ShowcaseSlotResponseDTO toResponseDTO(ShowcaseSlot slot) {
        PokemonModel p = slot.getProduct();
        BigDecimal orig = p != null && p.getBasePrice() != null ? p.getBasePrice() : null;
        BigDecimal promo = slot.getPromotionalPrice();
        BigDecimal diff = BigDecimal.ZERO;
        int pct = 0;

        if (orig != null && promo != null && promo.compareTo(BigDecimal.ZERO) > 0 && promo.compareTo(orig) < 0) {
            diff = orig.subtract(promo);
            pct = diff.multiply(BigDecimal.valueOf(100)).divide(orig, 0, RoundingMode.HALF_UP).intValue();
        }

        return new ShowcaseSlotResponseDTO(
                slot.getId(),
                slot.getSlotNumber(),
                p != null ? p.getId() : null,
                p != null ? p.getName() : null,
                p != null && p.getCategory() != null ? p.getCategory().name() : null,
                p != null ? p.getImageUrl() : null,
                orig,
                promo,
                diff,
                pct,
                slot.getBadgeLabel(),
                slot.getAllowCouponStacking(),
                slot.getIsActive(),
                slot.getMysteryTitle(),
                slot.getMysterySubtitle(),
                slot.getMysteryCategory(),
                slot.getMysteryImageUrl(),
                slot.getMysteryWhatsappText(),
                slot.getUpdatedAt()
        );
    }
}
