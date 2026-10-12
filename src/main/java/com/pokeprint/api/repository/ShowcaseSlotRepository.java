package com.pokeprint.api.repository;

import com.pokeprint.api.domain.entity.ShowcaseSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ShowcaseSlotRepository extends JpaRepository<ShowcaseSlot, Integer> {
    Optional<ShowcaseSlot> findBySlotNumber(Integer slotNumber);
    List<ShowcaseSlot> findAllByOrderBySlotNumberAsc();
}
