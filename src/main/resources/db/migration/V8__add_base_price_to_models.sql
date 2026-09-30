ALTER TABLE pokemon_models
ADD COLUMN base_price DECIMAL(10, 2);

ALTER TABLE pokemon_models
MODIFY COLUMN image_url LONGTEXT;
