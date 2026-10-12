CREATE TABLE customer_reviews (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_name VARCHAR(150) NOT NULL,
    rating INT NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comment TEXT NOT NULL,
    product_name VARCHAR(150) NOT NULL,
    customer_city VARCHAR(100) NULL,
    is_approved BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_customer_reviews_approved ON customer_reviews(is_approved, created_at DESC);
