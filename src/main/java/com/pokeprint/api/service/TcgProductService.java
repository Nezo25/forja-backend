package com.pokeprint.api.service;

import com.pokeprint.api.domain.TcgProduct;
import com.pokeprint.api.dto.TcgProductRequestDTO;
import com.pokeprint.api.dto.TcgProductResponseDTO;
import com.pokeprint.api.repository.TcgProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityNotFoundException;
import com.pokeprint.api.infra.exception.BusinessException;

@Service
public class TcgProductService {

    private final TcgProductRepository repository;

    public TcgProductService(TcgProductRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public TcgProductResponseDTO create(TcgProductRequestDTO dto) {
        TcgProduct product = new TcgProduct();
        product.setName(dto.name());
        product.setItemType(dto.itemType());
        product.setExpansionName(dto.expansionName());
        product.setLanguage(dto.language());
        product.setPrice(dto.price());
        product.setStockQuantity(dto.stockQuantity());
        product.setImageUrl(dto.imageUrl());
        if (dto.isActive() != null) {
            product.setIsActive(dto.isActive());
        }

        TcgProduct saved = repository.save(product);
        return TcgProductResponseDTO.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public Page<TcgProductResponseDTO> findAllActive(String expansion, Pageable pageable) {
        Page<TcgProduct> page;
        if (expansion != null && !expansion.isBlank()) {
            page = repository.findByIsActiveTrueAndExpansionNameContainingIgnoreCase(expansion, pageable);
        } else {
            page = repository.findByIsActiveTrue(pageable);
        }
        return page.map(TcgProductResponseDTO::fromEntity);
    }

    @Transactional(readOnly = true)
    public Page<TcgProductResponseDTO> findAllAdmin(Pageable pageable) {
        return repository.findAll(pageable).map(TcgProductResponseDTO::fromEntity);
    }

    @Transactional
    public TcgProductResponseDTO updateStock(Long id, int quantityDelta) {
        TcgProduct product = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto TCG não encontrado."));
        
        int newStock = product.getStockQuantity() + quantityDelta;
        if (newStock < 0) {
            throw new BusinessException("Estoque insuficiente.", HttpStatus.BAD_REQUEST);
        }
        
        product.setStockQuantity(newStock);
        return TcgProductResponseDTO.fromEntity(repository.save(product));
    }

    @Transactional
    public void toggleActiveStatus(Long id) {
        TcgProduct product = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto TCG não encontrado."));
        product.setIsActive(!product.getIsActive());
        repository.save(product);
    }
}
