package com.pokeprint.api.repository;

import com.pokeprint.api.domain.entity.CustomerReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerReviewRepository extends JpaRepository<CustomerReview, Long> {

    List<CustomerReview> findByIsApprovedTrueOrderByCreatedAtDesc();

    List<CustomerReview> findAllByOrderByCreatedAtDesc();

    List<CustomerReview> findByIsApprovedOrderByCreatedAtDesc(Boolean isApproved);
}
