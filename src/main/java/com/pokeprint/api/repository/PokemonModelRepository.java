package com.pokeprint.api.repository;

import com.pokeprint.api.domain.entity.PokemonModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PokemonModelRepository extends JpaRepository<PokemonModel, Long> {
    Page<PokemonModel> findByIsActiveTrue(Pageable pageable);
    
    @Query("SELECT p FROM PokemonModel p WHERE p.isActive = true AND (:generation IS NULL OR p.generation = :generation) AND (:type IS NULL OR p.primaryType = :type OR p.secondaryType = :type)")
    Page<PokemonModel> findActiveWithFilters(Integer generation, String type, Pageable pageable);
}
