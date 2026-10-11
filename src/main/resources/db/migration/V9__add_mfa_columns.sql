DROP PROCEDURE IF EXISTS AddMfaColumns;

DELIMITER //

CREATE PROCEDURE AddMfaColumns()
BEGIN
    IF NOT EXISTS (
        SELECT 1 
        FROM INFORMATION_SCHEMA.COLUMNS 
        WHERE TABLE_SCHEMA = DATABASE()
        AND TABLE_NAME = 'customers' 
        AND COLUMN_NAME = 'mfa_secret'
    ) THEN
        ALTER TABLE customers ADD COLUMN mfa_secret VARCHAR(32);
    END IF;

    IF NOT EXISTS (
        SELECT 1 
        FROM INFORMATION_SCHEMA.COLUMNS 
        WHERE TABLE_SCHEMA = DATABASE()
        AND TABLE_NAME = 'customers' 
        AND COLUMN_NAME = 'mfa_enabled'
    ) THEN
        ALTER TABLE customers ADD COLUMN mfa_enabled BOOLEAN DEFAULT FALSE;
    END IF;
END //

DELIMITER ;

CALL AddMfaColumns();
DROP PROCEDURE AddMfaColumns;
