package com.pokeprint.api.controller;

import com.pokeprint.api.domain.entity.FilamentInventory;
import com.pokeprint.api.dto.request.FilamentRestockRequestDTO;
import com.pokeprint.api.service.FilamentInventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/filaments")
@RequiredArgsConstructor
public class FilamentInventoryController {

    private final FilamentInventoryService filamentService;

    @PostMapping("/{id}/restock")
    public ResponseEntity<FilamentInventory> restock(
            @PathVariable Long id,
            @Valid @RequestBody FilamentRestockRequestDTO request) {
        FilamentInventory updated = filamentService.restock(id, request);
        return ResponseEntity.ok(updated);
    }
}
