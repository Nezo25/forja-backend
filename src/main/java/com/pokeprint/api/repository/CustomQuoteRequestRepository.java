package com.pokeprint.api.repository;

import com.pokeprint.api.domain.entity.CustomQuoteRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomQuoteRequestRepository extends JpaRepository<CustomQuoteRequest, Long> {
}
