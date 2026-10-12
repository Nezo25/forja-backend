package com.pokeprint.api.service;

import com.pokeprint.api.dto.request.ShowcaseSlotRequestDTO;
import com.pokeprint.api.dto.response.ShowcasePublicDTO;
import com.pokeprint.api.dto.response.ShowcaseSlotResponseDTO;
import java.util.List;

public interface ShowcaseService {
    ShowcasePublicDTO getPublicShowcase();
    List<ShowcaseSlotResponseDTO> getAdminShowcase();
    ShowcaseSlotResponseDTO updateSlot(Integer slotNumber, ShowcaseSlotRequestDTO request);
    boolean isCouponAllowedForProduct(Long productId);
}
