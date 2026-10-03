package com.pokeprint.api.repository;

import com.pokeprint.api.domain.TcgProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TcgProductRepository extends JpaRepository<TcgProduct, Long> {
    Page<TcgProduct> findByIsActiveTrue(Pageable pageable);
    Page<TcgProduct> findByIsActiveTrueAndExpansionNameContainingIgnoreCase(String expansionName, Pageable pageable);
}
