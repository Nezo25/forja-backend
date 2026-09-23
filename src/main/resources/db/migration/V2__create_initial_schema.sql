CREATE TABLE customers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20),
    address_line VARCHAR(255) NOT NULL,
    city VARCHAR(100) NOT NULL,
    state VARCHAR(50) NOT NULL,
    zip_code VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE pokemon_models (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pokedex_number INT,
    name VARCHAR(100) NOT NULL,
    generation INT,
    primary_type VARCHAR(50) NOT NULL,
    secondary_type VARCHAR(50),
    scale VARCHAR(50) NOT NULL,
    base_print_time_minutes INT NOT NULL,
    default_filament_grams DECIMAL(10,2) NOT NULL,
    image_url VARCHAR(255),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE filament_inventories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    material_type VARCHAR(50) NOT NULL,
    color_name VARCHAR(50) NOT NULL,
    hex_code VARCHAR(10),
    stock_grams DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    cost_per_gram DECIMAL(10,4) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE print_orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    tracking_code VARCHAR(100),
    shipping_address_line VARCHAR(255) NOT NULL,
    shipping_city VARCHAR(100) NOT NULL,
    shipping_state VARCHAR(50) NOT NULL,
    shipping_zip_code VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_order_customer FOREIGN KEY (customer_id) REFERENCES customers(id)
);

CREATE TABLE order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    print_order_id BIGINT NOT NULL,
    pokemon_model_id BIGINT NOT NULL,
    filament_inventory_id BIGINT NOT NULL,
    finish_type VARCHAR(50) NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    unit_price DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_item_order FOREIGN KEY (print_order_id) REFERENCES print_orders(id),
    CONSTRAINT fk_item_model FOREIGN KEY (pokemon_model_id) REFERENCES pokemon_models(id),
    CONSTRAINT fk_item_filament FOREIGN KEY (filament_inventory_id) REFERENCES filament_inventories(id)
);

CREATE INDEX idx_pokemon_models_active ON pokemon_models(is_active);
CREATE INDEX idx_orders_status ON print_orders(status);
CREATE INDEX idx_orders_customer ON print_orders(customer_id);
