# SK-MarketHub Production Database Backup & Restore Guide

## 1. Automated MySQL Backup Command

To perform an on-demand compressed backup of `sk_markethub_db`:
```bash
docker exec skmarkethub-prod-db mysqldump -u root -proot123 sk_markethub_db | gzip > /var/backups/skmarkethub/sk_markethub_db_$(date +%Y%m%d_%H%M%S).sql.gz
```

---

## 2. Database Restore Procedure

To restore a database backup archive to `skmarkethub-prod-db`:
```bash
gunzip < /var/backups/skmarkethub/sk_markethub_db_20261122_150000.sql.gz | docker exec -i skmarkethub-prod-db mysql -u root -proot123 sk_markethub_db
```

---

## 3. Preservation Notice

- **Customer Account:** `sajid.test02@example.com` (User ID 4)
- **Admin Account:** `admin@skmarkethub.com` (User ID 5)
- Both existing accounts and passwords are strictly preserved.
