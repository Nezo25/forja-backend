CREATE TABLE pricing_configurations (
    id BIGINT PRIMARY KEY,
    machine_hourly_rate DECIMAL(10,2) NOT NULL,
    primer_fixed_rate DECIMAL(10,2) NOT NULL,
    primer_consumable_percentage DECIMAL(5,4) NOT NULL,
    painting_multiplier DECIMAL(5,4) NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Inserindo os valores padrão (que agora poderão ser editados pelo Chico no painel admin)
INSERT INTO pricing_configurations (id, machine_hourly_rate, primer_fixed_rate, primer_consumable_percentage, painting_multiplier)
VALUES (1, 10.00, 15.00, 0.0500, 1.5000);
