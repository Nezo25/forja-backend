CREATE TABLE custom_quote_requests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    estimated_weight_grams DECIMAL(10,2) NOT NULL,
    estimated_print_time_minutes INT NOT NULL,
    stl_url VARCHAR(255) NOT NULL,
    finish_type VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    final_price DECIMAL(10,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_quote_customer FOREIGN KEY (customer_id) REFERENCES customers(id)
);

CREATE TABLE cash_flow_entries (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    amount DECIMAL(10,2) NOT NULL,
    type VARCHAR(50) NOT NULL,
    description VARCHAR(255) NOT NULL,
    related_order_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
