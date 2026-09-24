package com.pokeprint.api.controller;

import com.pokeprint.api.domain.entity.PokemonModel;
import com.pokeprint.api.dto.request.PokemonModelRequestDTO;
import com.pokeprint.api.dto.response.PokemonModelResponseDTO;
import com.pokeprint.api.repository.PokemonModelRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/models")
@RequiredArgsConstructor
public class PokemonModelController {

    private final PokemonModelRepository pokemonModelRepository;

    @GetMapping
    public ResponseEntity<Page<PokemonModelResponseDTO>> getModels(
            @RequestParam(required = false) Integer generation,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String scale,
            Pageable pageable) {
        
        Page<PokemonModel> page = pokemonModelRepository.findActiveWithFilters(generation, type, pageable);
        return ResponseEntity.ok(page.map(this::toResponseDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PokemonModelResponseDTO> getModelById(@PathVariable Long id) {
        return pokemonModelRepository.findById(id)
                .map(this::toResponseDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<PokemonModelResponseDTO> createModel(@Valid @RequestBody PokemonModelRequestDTO request) {
        PokemonModel model = new PokemonModel();
        model.setPokedexNumber(request.pokedexNumber());
        model.setName(request.name());
        model.setGeneration(request.generation());
        model.setPrimaryType(request.primaryType());
        model.setSecondaryType(request.secondaryType());
        model.setScale(request.scale());
        model.setBasePrintTimeMinutes(request.basePrintTimeMinutes());
        model.setDefaultFilamentGrams(request.defaultFilamentGrams());
        model.setImageUrl(request.imageUrl());
        model.setIsActive(true);

        PokemonModel saved = pokemonModelRepository.save(model);
        
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(saved.getId())
                .toUri();
                
        return ResponseEntity.created(location).body(toResponseDTO(saved));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<PokemonModelResponseDTO> updateModel(@PathVariable Long id, @Valid @RequestBody PokemonModelRequestDTO request) {
        return pokemonModelRepository.findById(id).map(model -> {
            model.setPokedexNumber(request.pokedexNumber());
            model.setName(request.name());
            model.setGeneration(request.generation());
            model.setPrimaryType(request.primaryType());
            model.setSecondaryType(request.secondaryType());
            model.setScale(request.scale());
            model.setBasePrintTimeMinutes(request.basePrintTimeMinutes());
            model.setDefaultFilamentGrams(request.defaultFilamentGrams());
            model.setImageUrl(request.imageUrl());
            PokemonModel saved = pokemonModelRepository.save(model);
            return ResponseEntity.ok(toResponseDTO(saved));
        }).orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteModel(@PathVariable Long id) {
        if (!pokemonModelRepository.existsById(id)) return ResponseEntity.notFound().build();
        pokemonModelRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private PokemonModelResponseDTO toResponseDTO(PokemonModel model) {
        return new PokemonModelResponseDTO(
                model.getId(),
                model.getPokedexNumber(),
                model.getName(),
                model.getGeneration(),
                model.getPrimaryType(),
                model.getSecondaryType(),
                model.getScale(),
                model.getBasePrintTimeMinutes(),
                model.getDefaultFilamentGrams(),
                model.getImageUrl(),
                model.getIsActive(),
                model.getCreatedAt()
        );
    }
}