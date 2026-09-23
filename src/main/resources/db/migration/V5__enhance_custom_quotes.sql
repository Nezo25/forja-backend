ALTER TABLE custom_quote_requests
    ADD COLUMN scale VARCHAR(50),
    ADD COLUMN intended_material VARCHAR(50),
    ADD COLUMN intended_color VARCHAR(50),
    ADD COLUMN filament_inventory_id BIGINT,
    MODIFY COLUMN estimated_weight_grams DECIMAL(10,2) NULL,
    MODIFY COLUMN estimated_print_time_minutes INT NULL;

ALTER TABLE custom_quote_requests 
    ADD CONSTRAINT fk_quote_filament FOREIGN KEY (filament_inventory_id) REFERENCES filament_inventories(id);

ALTER TABLE order_items 
    MODIFY COLUMN pokemon_model_id BIGINT NULL;
