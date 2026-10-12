package com.pokeprint.api.controller;

import com.pokeprint.api.dto.request.CustomerReviewSubmitDTO;
import com.pokeprint.api.dto.response.CustomerReviewResponseDTO;
import com.pokeprint.api.service.CustomerReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CustomerReviewController {

    private final CustomerReviewService customerReviewService;

    // --- Endpoints Públicos ---

    @GetMapping("/api/v1/reviews/public")
    public ResponseEntity<List<CustomerReviewResponseDTO>> getPublicApprovedReviews() {
        return ResponseEntity.ok(customerReviewService.getPublicApprovedReviews());
    }

    @PostMapping("/api/v1/reviews/submit")
    public ResponseEntity<CustomerReviewResponseDTO> submitReview(@Valid @RequestBody CustomerReviewSubmitDTO dto) {
        CustomerReviewResponseDTO created = customerReviewService.submitReview(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // --- Endpoints Administrativos ---

    @GetMapping("/api/v1/admin/reviews")
    public ResponseEntity<List<CustomerReviewResponseDTO>> getAdminReviews(
            @RequestParam(required = false, defaultValue = "ALL") String status) {
        return ResponseEntity.ok(customerReviewService.getAdminReviews(status));
    }

    @PatchMapping("/api/v1/admin/reviews/{id}/toggle-approval")
    public ResponseEntity<CustomerReviewResponseDTO> toggleApproval(@PathVariable Long id) {
        return ResponseEntity.ok(customerReviewService.toggleApproval(id));
    }

    @DeleteMapping("/api/v1/admin/reviews/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        customerReviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }
}
