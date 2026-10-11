-- Add auth and user identity fields
ALTER TABLE customers ADD COLUMN password VARCHAR(255);
ALTER TABLE customers ADD COLUMN role VARCHAR(50) DEFAULT 'ROLE_CLIENT';
ALTER TABLE customers ADD COLUMN cpf VARCHAR(14);

-- Make address fields optional since users can register before providing an address
ALTER TABLE customers MODIFY COLUMN address_line VARCHAR(255) NULL;
ALTER TABLE customers MODIFY COLUMN city VARCHAR(100) NULL;
ALTER TABLE customers MODIFY COLUMN state VARCHAR(50) NULL;
ALTER TABLE customers MODIFY COLUMN zip_code VARCHAR(20) NULL;

-- Insert the default admin user with password 'admin123' (bcrypt cost 12)
INSERT INTO customers (name, email, password, role, phone, created_at)
VALUES ('Admin Forja', 'admin@forja.com', '$2a$12$R.O.1R1wD8W9D.o5H9Bv/eu5YF0.f.gQZ9D7.9/R/zQYQ4QZ0K8vO', 'ROLE_ADMIN', '0000000000', NOW());
