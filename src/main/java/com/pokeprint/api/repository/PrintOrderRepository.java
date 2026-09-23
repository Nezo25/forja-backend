package com.pokeprint.api.repository;

import com.pokeprint.api.domain.entity.PrintOrder;
import com.pokeprint.api.domain.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PrintOrderRepository extends JpaRepository<PrintOrder, Long> {
    Page<PrintOrder> findByCustomerId(Long customerId, Pageable pageable);
    List<PrintOrder> findByStatus(OrderStatus status);
}
