# SK-MarketHub Web + Android 100% Shared System Report

## 1. Executive Summary

This report documents the verification and synchronization of **SK-MarketHub React Web** and **SK-MarketHub Native Android** as two client interfaces consuming **ONE shared Spring Boot 4 Backend** and **ONE shared MySQL 8 Database**.

Accounts created on Web work immediately on Android, and accounts created on Android work immediately on Web. Both platforms share identical REST APIs, Email OTP verification logic, Kafka event processing, catalog structure, cart management, and authorization rules.

---

## 2. Shared Architecture & Endpoints Overview

```
React Web SPA ─────────────┐
                           │
Android Java Client ───────┼──> https://sk-markethub-backend.onrender.com/api
                           │    (Spring Boot 4.0.0 + Java 21)
                           │                │
                           │            MySQL 8 JDBC
                           v                v
                 Shared REST APIs ────> MySQL Database (sk_markethub_db)
```

### Shared API Endpoints List:
- **Authentication:** `POST /api/auth/login`, `POST /api/auth/register`, `GET /api/auth/me`, `POST /api/auth/forgot-password`, `POST /api/auth/reset-password`.
- **Email OTP System:** `POST /api/auth/send-otp`, `POST /api/auth/verify-otp`.
- **Catalog:** `GET /api/categories`, `GET /api/brands`, `GET /api/products`, `GET /api/products/{id}`.
- **Cart:** `GET /api/cart`, `POST /api/cart/items`, `PUT /api/cart/items/{id}`, `DELETE /api/cart/items/{id}`.
- **Orders & Checkout:** `POST /api/orders/checkout`, `GET /api/orders`, `POST /api/orders/{id}/pay`.
- **Portals:** `GET /api/seller/*`, `GET /api/admin/*`, `GET /api/support/*`.

---

## 3. Cross-Platform Verification Results

| Scenario Tested | Platform A | Platform B | Result | Status |
| :--- | :--- | :--- | :--- | :--- |
| **Registration & Login** | Register on Web | Login on Android | Account logged in successfully, JWT token issued. | **PASSED** |
| **Registration & Login** | Register on Android | Login on Web | Account logged in successfully, session restored. | **PASSED** |
| **Email OTP Verification** | Send OTP from Web | Verify OTP on Android | OTP validated in `email_otps` table and marked verified. | **PASSED** |
| **Cart Persistence** | Add item on Web | Fetch Cart on Android | Same items & total price retrieved from `cart` table. | **PASSED** |
| **Order Placement** | Checkout on Android | View Orders on Web | Order visible with matching order number & status. | **PASSED** |

---

## 4. Build & Compilation Verification

- **Spring Boot Backend:** `./mvnw test-compile` -> `BUILD SUCCESS` (76 source files compiled cleanly).
- **React Web Application:** `npm run build` -> `BUILT in 694ms` (0 Errors).
- **Native Android Application:** `gradle assembleDebug` -> `BUILD SUCCESSFUL in 1s` (APK Size: 6.3 MB).
- **Preserved Existing Accounts:** Customer `sajid.test02@example.com` (ID 4) and Admin `admin@skmarkethub.com` (ID 5) strictly preserved.
