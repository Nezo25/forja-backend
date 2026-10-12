package com.pokeprint.api.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "customer_reviews")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_name", nullable = false, length = 150)
    private String customerName;

    @Column(nullable = false)
    private Integer rating;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String comment;

    @Column(name = "product_name", nullable = false, length = 150)
    private String productName;

    @Column(name = "customer_city", length = 100)
    private String customerCity;

    @Column(name = "is_approved", nullable = false)
    private Boolean isApproved = false;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.isApproved == null) {
            this.isApproved = false;
        }
    }
}
