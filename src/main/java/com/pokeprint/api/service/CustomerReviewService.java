package com.pokeprint.api.service;

import com.pokeprint.api.dto.request.CustomerReviewSubmitDTO;
import com.pokeprint.api.dto.response.CustomerReviewResponseDTO;

import java.util.List;

public interface CustomerReviewService {

    List<CustomerReviewResponseDTO> getPublicApprovedReviews();

    CustomerReviewResponseDTO submitReview(CustomerReviewSubmitDTO dto);

    List<CustomerReviewResponseDTO> getAdminReviews(String status);

    CustomerReviewResponseDTO toggleApproval(Long id);

    void deleteReview(Long id);
}
