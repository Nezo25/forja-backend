package com.pokeprint.api.repository;

import com.pokeprint.api.domain.entity.CashFlowEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CashFlowEntryRepository extends JpaRepository<CashFlowEntry, Long> {
}
