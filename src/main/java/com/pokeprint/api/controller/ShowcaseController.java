package com.pokeprint.api.controller;

import com.pokeprint.api.dto.request.ShowcaseSlotRequestDTO;
import com.pokeprint.api.dto.response.ShowcasePublicDTO;
import com.pokeprint.api.dto.response.ShowcaseSlotResponseDTO;
import com.pokeprint.api.service.ShowcaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ShowcaseController {

    private final ShowcaseService showcaseService;

    /**
     * Endpoint público para o Hero Carousel da vitrine
     */
    @GetMapping("/showcase")
    public ResponseEntity<ShowcasePublicDTO> getPublicShowcase() {
        return ResponseEntity.ok(showcaseService.getPublicShowcase());
    }

    /**
     * Endpoint administrativo para visualização completa dos slots
     */
    @GetMapping("/admin/showcase")
    public ResponseEntity<List<ShowcaseSlotResponseDTO>> getAdminShowcase() {
        return ResponseEntity.ok(showcaseService.getAdminShowcase());
    }

    /**
     * Atualização individual de um slot de vitrine
     */
    @PutMapping("/admin/showcase/slot/{slotNumber}")
    public ResponseEntity<ShowcaseSlotResponseDTO> updateSlot(
            @PathVariable Integer slotNumber,
            @RequestBody ShowcaseSlotRequestDTO request
    ) {
        return ResponseEntity.ok(showcaseService.updateSlot(slotNumber, request));
    }
}
