#!/bin/bash
# Backup cronjob
DB_USER=${DB_USER}
DB_PASS=${DB_PASS}
DB_NAME=forja_db
BACKUP_DIR=/backups

/usr/bin/mysqldump -u $DB_USER -p$DB_PASS --single-transaction --quick --routines --triggers $DB_NAME | gzip > $BACKUP_DIR/forja_db_$(date +%F).sql.gz
find $BACKUP_DIR -type f -name "*.sql.gz" -mtime +14 -delete
