package com.pokeprint.api.repository;

import com.pokeprint.api.domain.entity.CashFlowTransaction;
import com.pokeprint.api.domain.enums.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface CashFlowTransactionRepository extends JpaRepository<CashFlowTransaction, Long> {
    
    @Query("SELECT c FROM CashFlowTransaction c WHERE " +
           "(:startDate IS NULL OR c.transactionDate >= :startDate) AND " +
           "(:endDate IS NULL OR c.transactionDate <= :endDate) AND " +
           "(:type IS NULL OR c.transactionType = :type)")
    Page<CashFlowTransaction> findWithFilters(
            @Param("startDate") LocalDateTime startDate, 
            @Param("endDate") LocalDateTime endDate, 
            @Param("type") TransactionType type, 
            Pageable pageable);

    @Query("SELECT COALESCE(SUM(c.amount), 0) FROM CashFlowTransaction c WHERE " +
           "c.transactionType = :type AND " +
           "(:startDate IS NULL OR c.transactionDate >= :startDate) AND " +
           "(:endDate IS NULL OR c.transactionDate <= :endDate)")
    BigDecimal sumAmountByTypeAndPeriod(
            @Param("type") TransactionType type,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}
