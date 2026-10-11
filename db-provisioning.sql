CREATE USER 'forja_app'@'%' IDENTIFIED BY 'senha_segura';
GRANT SELECT, INSERT, UPDATE, DELETE ON forja_db.* TO 'forja_app'@'%';
FLUSH PRIVILEGES;
