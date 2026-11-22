# SK-MarketHub Complete Production Deployment Report

## 1. Production Architecture Overview

This document specifies the complete production deployment architecture for **SK-MarketHub Web**, **Spring Boot Backend**, **MySQL 8 Database**, and **Native Android**.

```
                         +-----------------------------------+
                         |           DuckDNS Domain          |
                         |     (skmarkethub.duckdns.org)     |
                         +-----------------------------------+
                                           |
                                     HTTP / HTTPS
                                           v
                         +-----------------------------------+
                         |         Caddy Reverse Proxy       |
                         |   (Automatic Let's Encrypt TLS)   |
                         +-----------------------------------+
                                   /                 \
                          /                           \
                         v                             v
           +--------------------------+   +--------------------------+
           |   React Web Container    |   |  Spring Boot 4 Backend   |
           |      (Nginx - :80)       |   |      (Java 21 - :8080)   |
           +--------------------------+   +--------------------------+
                                                       |
                                                  MySQL Driver
                                                       v
                                          +--------------------------+
                                          |     MySQL 8 Database     |
                                          | (Isolated - Private :3306|
                                          +--------------------------+
```

---

## 2. Docker Production Configuration Files

1. **`Caddyfile` ([Caddyfile](file:///C:/Users/Sajid%20Khan/SK-MarketHub/Caddyfile)):**
   Routes `https://DOMAIN/api/*` -> Spring Boot backend container (`:8080`) and `https://DOMAIN/*` -> React Web frontend container (`:80`) with automatic Let's Encrypt TLS issuance.

2. **`docker-compose.prod.yml` ([docker-compose.prod.yml](file:///C:/Users/Sajid%20Khan/SK-MarketHub/docker-compose.prod.yml)):**
   Orchestrates `mysql` (isolated network, persistent volume), `backend`, `web`, `caddy`, and `duckdns` auto-IP updater.

3. **`application-prod.properties` ([application-prod.properties](file:///C:/Users/Sajid%20Khan/SK-MarketHub/backend/src/main/resources/application-prod.properties)):**
   Configures Spring Boot production database connection, Hikari connection pool, and JWT secret bindings.

4. **`deploy/backup_db.sh` ([backup_db.sh](file:///C:/Users/Sajid%20Khan/SK-MarketHub/deploy/backup_db.sh)):**
   Automated MySQL database backup script with a 30-day retention policy.

5. **`deploy/deploy.sh` ([deploy.sh](file:///C:/Users/Sajid%20Khan/SK-MarketHub/deploy/deploy.sh)):**
   Automated EC2 deployment script that installs Docker, generates `.env`, and launches all production containers.

---

## 3. Step-by-Step AWS EC2 & DuckDNS Deployment Guide

### Step 1: AWS EC2 Instance Setup
1. Launch an **Ubuntu 24.04 LTS** EC2 instance (Recommended: `t3.medium` - 2 vCPU, 4 GB RAM).
2. Configure EC2 Security Group inbound rules:
   - **SSH (22):** Restricted to My IP
   - **HTTP (80):** Anywhere (`0.0.0.0/0`)
   - **HTTPS (443):** Anywhere (`0.0.0.0/0`)
   - *Note:* Do NOT expose port `3306` or `3307`.

### Step 2: Configure DuckDNS
1. Go to [duckdns.org](https://www.duckdns.org) and log in.
2. Create a subdomain (e.g. `skmarkethub.duckdns.org`).
3. Copy your **DuckDNS Token**.

### Step 3: EC2 Deployment Execution
SSH into your EC2 instance and run:
```bash
git clone https://github.com/your-username/SK-MarketHub.git
cd SK-MarketHub
chmod +x deploy/deploy.sh deploy/backup_db.sh
./deploy/deploy.sh
```

Edit the `.env` file on EC2 with your DuckDNS domain and token:
```ini
DOMAIN=skmarkethub.duckdns.org
DUCKDNS_SUBDOMAINS=skmarkethub
DUCKDNS_TOKEN=YOUR_ACTUAL_DUCKDNS_TOKEN
MYSQL_DATABASE=sk_markethub_db
MYSQL_USER=skmarket
MYSQL_PASSWORD=your_secure_password
MYSQL_ROOT_PASSWORD=your_secure_root_password
JWT_SECRET=your_secure_32byte_jwt_secret
```

Launch the production stack:
```bash
docker compose -f docker-compose.prod.yml up -d --build
```

---

## 4. Verification Results

- **Backend Unit Tests:** `./mvnw test` -> `BUILD SUCCESS` (9/9 tests passed).
- **React Web Production Build:** `npm run build` -> `BUILT in 447ms` (0 errors).
- **Native Android APK:** `gradle assembleDebug` -> `BUILD SUCCESSFUL in 5s` (Size: 6.3 MB).
- **Preserved Database Records:** Customer account `sajid.test02@example.com` (ID 4) and Admin account `admin@skmarkethub.com` (ID 5) strictly preserved.
