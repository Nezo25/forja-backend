package com.pokeprint.api.controller;

import com.pokeprint.api.domain.entity.FilamentInventory;
import com.pokeprint.api.dto.request.FilamentRequestDTO;
import com.pokeprint.api.dto.request.FilamentRestockRequestDTO;
import com.pokeprint.api.repository.FilamentInventoryRepository;
import com.pokeprint.api.service.FilamentInventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/filaments")
@RequiredArgsConstructor
public class FilamentInventoryController {

    private final FilamentInventoryService filamentService;
    private final FilamentInventoryRepository repository;

    @GetMapping
    public ResponseEntity<List<FilamentInventory>> getAll() {
        return ResponseEntity.ok(repository.findAll());
    }
    
    @PostMapping
    public ResponseEntity<FilamentInventory> createFilament(@Valid @RequestBody FilamentRequestDTO request) {
        FilamentInventory fil = new FilamentInventory();
        fil.setColor(request.color());
        fil.setMaterial(request.material());
        fil.setStockGrams(request.stockGrams());
        fil.setMinStockGrams(request.minStockGrams());
        FilamentInventory saved = repository.save(fil);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(saved.getId()).toUri();
        return ResponseEntity.created(location).body(saved);
    }

    @PostMapping("/{id}/restock")
    public ResponseEntity<FilamentInventory> restock(
            @PathVariable Long id,
            @Valid @RequestBody FilamentRestockRequestDTO request) {
        FilamentInventory updated = filamentService.restock(id, request);
        return ResponseEntity.ok(updated);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFilament(@PathVariable Long id) {
        if (!repository.existsById(id)) return ResponseEntity.notFound().build();
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}