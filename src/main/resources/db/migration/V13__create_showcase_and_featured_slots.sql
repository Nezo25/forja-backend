CREATE TABLE IF NOT EXISTS showcase_slots (
    id INT AUTO_INCREMENT PRIMARY KEY,
    slot_number INT NOT NULL UNIQUE,
    product_id BIGINT NULL,
    promotional_price DECIMAL(10,2) NULL,
    badge_label VARCHAR(60) DEFAULT '⚡ OFERTA DE BANCADA',
    allow_coupon_stacking BOOLEAN NOT NULL DEFAULT FALSE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    mystery_title VARCHAR(100) DEFAULT 'Quem é esse Pokémon?',
    mystery_subtitle VARCHAR(150) DEFAULT 'Próximo lançamento saindo da bancada',
    mystery_category VARCHAR(50) DEFAULT 'FIGURE_POKEMON',
    mystery_image_url VARCHAR(500) NULL,
    mystery_whatsapp_text VARCHAR(255) DEFAULT 'Olá Chico! Vi o drop misterioso da semana no site e quero garantir com desconto exclusivo!',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_showcase_product FOREIGN KEY (product_id) REFERENCES pokemon_models(id) ON DELETE SET NULL
);

-- Inserir os 4 slots padrão
INSERT INTO showcase_slots (slot_number, product_id, promotional_price, badge_label, allow_coupon_stacking, is_active)
VALUES 
(1, NULL, NULL, '⚡ OFERTA DE BANCADA', FALSE, TRUE),
(2, NULL, NULL, '⚡ OFERTA DE BANCADA', FALSE, TRUE),
(3, NULL, NULL, '⚡ OFERTA DE BANCADA', FALSE, TRUE)
ON DUPLICATE KEY UPDATE updated_at = CURRENT_TIMESTAMP;

INSERT INTO showcase_slots (slot_number, product_id, promotional_price, badge_label, allow_coupon_stacking, is_active, mystery_title, mystery_subtitle, mystery_category, mystery_whatsapp_text)
VALUES 
(4, NULL, NULL, '❓ DROP MISTERIOSO', FALSE, TRUE, 'Quem é esse Pokémon?', 'Próximo lançamento saindo da bancada', 'FIGURE_POKEMON', 'Olá Chico! Vi o drop misterioso da semana no site e quero garantir com desconto exclusivo!')
ON DUPLICATE KEY UPDATE updated_at = CURRENT_TIMESTAMP;
