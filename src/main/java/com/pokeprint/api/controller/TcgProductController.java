package com.pokeprint.api.controller;

import com.pokeprint.api.dto.TcgProductRequestDTO;
import com.pokeprint.api.dto.TcgProductResponseDTO;
import com.pokeprint.api.service.TcgProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class TcgProductController {

    private final TcgProductService service;

    public TcgProductController(TcgProductService service) {
        this.service = service;
    }

    @GetMapping("/tcg-products")
    public ResponseEntity<Page<TcgProductResponseDTO>> getActiveProducts(
            @RequestParam(required = false) String expansion,
            Pageable pageable) {
        return ResponseEntity.ok(service.findAllActive(expansion, pageable));
    }

    @GetMapping("/admin/tcg-products")
    public ResponseEntity<Page<TcgProductResponseDTO>> getAllProductsAdmin(Pageable pageable) {
        return ResponseEntity.ok(service.findAllAdmin(pageable));
    }

    @PostMapping("/admin/tcg-products")
    public ResponseEntity<TcgProductResponseDTO> createProduct(@Valid @RequestBody TcgProductRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PatchMapping("/admin/tcg-products/{id}/stock")
    public ResponseEntity<TcgProductResponseDTO> updateStock(
            @PathVariable Long id,
            @RequestParam int delta) {
        return ResponseEntity.ok(service.updateStock(id, delta));
    }

    @PatchMapping("/admin/tcg-products/{id}/toggle-active")
    public ResponseEntity<Void> toggleActive(@PathVariable Long id) {
        service.toggleActiveStatus(id);
        return ResponseEntity.noContent().build();
    }
}
