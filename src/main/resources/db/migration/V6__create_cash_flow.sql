DROP TABLE IF EXISTS cash_flow_entries;

CREATE TABLE cash_flow_transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    transaction_type VARCHAR(20) NOT NULL,
    category VARCHAR(50) NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    transaction_date DATETIME NOT NULL,
    description VARCHAR(255) NOT NULL,
    reference_order_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_cash_flow_date ON cash_flow_transactions(transaction_date);
CREATE INDEX idx_cash_flow_type ON cash_flow_transactions(transaction_type);
