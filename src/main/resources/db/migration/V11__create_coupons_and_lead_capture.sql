CREATE TABLE IF NOT EXISTS coupons (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(30) NOT NULL UNIQUE,
    discount_type VARCHAR(20) NOT NULL,
    discount_value DECIMAL(10,2) NOT NULL,
    min_order_value DECIMAL(10,2),
    max_uses INT,
    current_uses INT DEFAULT 0,
    is_first_purchase_only BOOLEAN DEFAULT FALSE,
    valid_until DATETIME,
    is_active BOOLEAN DEFAULT TRUE
);

ALTER TABLE print_orders 
ADD COLUMN short_code VARCHAR(15) UNIQUE,
ADD COLUMN coupon_id BIGINT,
ADD COLUMN discount_amount DECIMAL(10,2) DEFAULT 0.00,
ADD COLUMN kanban_column VARCHAR(50) NOT NULL DEFAULT 'NEW_LEAD',
ADD COLUMN tags VARCHAR(255);

ALTER TABLE print_orders
ADD CONSTRAINT fk_print_orders_coupon FOREIGN KEY (coupon_id) REFERENCES coupons(id);


ALTER TABLE order_items ADD COLUMN tcg_product_id BIGINT;
ALTER TABLE order_items MODIFY filament_inventory_id BIGINT;
