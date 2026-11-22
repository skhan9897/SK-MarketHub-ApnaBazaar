#!/bin/bash
# SK-MarketHub Automated MySQL Database Backup Script

BACKUP_DIR="/var/backups/skmarkethub"
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
BACKUP_FILE="${BACKUP_DIR}/sk_markethub_db_${TIMESTAMP}.sql.gz"

mkdir -p "$BACKUP_DIR"

echo "[$(date)] Starting MySQL Database Backup..."
docker exec skmarkethub-prod-db mysqldump -u root -proot123 sk_markethub_db | gzip > "$BACKUP_FILE"

if [ $? -eq 0 ]; then
    echo "[$(date)] MySQL Database Backup Successful: $BACKUP_FILE"
    # Retain backups for 30 days
    find "$BACKUP_DIR" -type f -name "*.sql.gz" -mtime +30 -exec rm -f {} \;
else
    echo "[$(date)] ERROR: MySQL Database Backup Failed!" >&2
    exit 1
fi
