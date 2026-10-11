package com.pokeprint.api.repository;

import com.pokeprint.api.domain.entity.PrintOrder;
import com.pokeprint.api.domain.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PrintOrderRepository extends JpaRepository<PrintOrder, Long> {
    Page<PrintOrder> findByCustomerId(Long customerId, Pageable pageable);
    @org.springframework.data.jpa.repository.Query("SELECT COUNT(p) > 0 FROM PrintOrder p WHERE p.customer.email = :email")
    boolean existsByCustomerEmail(@org.springframework.data.repository.query.Param("email") String email);

    boolean existsByCustomerId(Long customerId);
    Optional<PrintOrder> findByShortCode(String shortCode);

    @org.springframework.data.jpa.repository.Query("SELECT p FROM PrintOrder p JOIN p.customer c WHERE " +
           "(:email IS NULL OR c.email = :email) AND " +
           "(:phone IS NULL OR c.phone = :phone) AND " +
           "(:shortCode IS NULL OR p.shortCode = :shortCode) " +
           "ORDER BY p.createdAt DESC")
    List<PrintOrder> searchOrders(@org.springframework.data.repository.query.Param("email") String email,
                                  @org.springframework.data.repository.query.Param("phone") String phone,
                                  @org.springframework.data.repository.query.Param("shortCode") String shortCode);

}
