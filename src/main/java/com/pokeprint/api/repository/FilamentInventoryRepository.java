package com.pokeprint.api.repository;

import com.pokeprint.api.domain.entity.FilamentInventory;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface FilamentInventoryRepository extends JpaRepository<FilamentInventory, Long> {
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT f FROM FilamentInventory f WHERE f.id = :id")
    Optional<FilamentInventory> findByIdForUpdate(Long id);
}
