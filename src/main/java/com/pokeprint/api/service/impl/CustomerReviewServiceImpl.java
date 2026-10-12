package com.pokeprint.api.service.impl;

import com.pokeprint.api.domain.entity.CustomerReview;
import com.pokeprint.api.dto.request.CustomerReviewSubmitDTO;
import com.pokeprint.api.dto.response.CustomerReviewResponseDTO;
import com.pokeprint.api.repository.CustomerReviewRepository;
import com.pokeprint.api.service.CustomerReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerReviewServiceImpl implements CustomerReviewService {

    private final CustomerReviewRepository customerReviewRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CustomerReviewResponseDTO> getPublicApprovedReviews() {
        return customerReviewRepository.findByIsApprovedTrueOrderByCreatedAtDesc()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    @Transactional
    public CustomerReviewResponseDTO submitReview(CustomerReviewSubmitDTO dto) {
        CustomerReview review = new CustomerReview();
        review.setCustomerName(dto.customerName().trim());
        review.setRating(dto.rating());
        review.setComment(dto.comment().trim());
        review.setProductName(dto.productName().trim());
        review.setCustomerCity(dto.customerCity() != null && !dto.customerCity().isBlank() ? dto.customerCity().trim() : null);
        review.setIsApproved(false);
        review.setCreatedAt(LocalDateTime.now());

        CustomerReview saved = customerReviewRepository.save(review);
        return toDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerReviewResponseDTO> getAdminReviews(String status) {
        List<CustomerReview> list;
        if ("PENDING".equalsIgnoreCase(status)) {
            list = customerReviewRepository.findByIsApprovedOrderByCreatedAtDesc(false);
        } else if ("APPROVED".equalsIgnoreCase(status)) {
            list = customerReviewRepository.findByIsApprovedOrderByCreatedAtDesc(true);
        } else {
            list = customerReviewRepository.findAllByOrderByCreatedAtDesc();
        }

        return list.stream().map(this::toDTO).toList();
    }

    @Override
    @Transactional
    public CustomerReviewResponseDTO toggleApproval(Long id) {
        CustomerReview review = customerReviewRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Avaliação não encontrada com ID: " + id));

        review.setIsApproved(!Boolean.TRUE.equals(review.getIsApproved()));
        CustomerReview updated = customerReviewRepository.save(review);
        return toDTO(updated);
    }

    @Override
    @Transactional
    public void deleteReview(Long id) {
        if (!customerReviewRepository.existsById(id)) {
            throw new IllegalArgumentException("Avaliação não encontrada com ID: " + id);
        }
        customerReviewRepository.deleteById(id);
    }

    private CustomerReviewResponseDTO toDTO(CustomerReview entity) {
        return new CustomerReviewResponseDTO(
                entity.getId(),
                entity.getCustomerName(),
                entity.getRating(),
                entity.getComment(),
                entity.getProductName(),
                entity.getCustomerCity(),
                entity.getIsApproved(),
                entity.getCreatedAt()
        );
    }
}
