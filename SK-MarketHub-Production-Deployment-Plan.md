# SK-MarketHub Production Deployment & Cloud Integration Plan

## 1. Executive Summary

This document specifies the deployment architecture and execution plan for launching **SK-MarketHub** on Render and cloud infrastructure with **Spring Boot 4**, **MySQL 8 Database**, **Apache Kafka Event Processing**, **Email OTP Verification**, **React Web SPA**, and **Native Android App**.

---

## 2. Cloud Architecture Diagram

```
                        +------------------------------------+
                        |         Render Web Service         |
                        |      (React + TypeScript SPA)      |
                        +------------------------------------+
                                           |
                                       HTTPS / API
                                           v
                        +------------------------------------+
                        |     Render Spring Boot Backend     |
                        |      (Java 21 - Port :8080)        |
                        +------------------------------------+
                         /                 |                \
            Database JDC/                  |                 \ Event Bus
                        v                  v                  v
         +--------------------+  +------------------+  +--------------------+
         |  MySQL 8 Database  |  |  SMTP / Email    |  |    Apache Kafka    |
         | (sk_markethub_db)  |  |  (Brevo / Gmail) |  |   Event Cluster    |
         +--------------------+  +------------------+  +--------------------+
```

---

## 3. Integrated Services Architecture

1. **Database Persistence:** MySQL 8 with Flyway schema migrations (`V1` initial schema, `V2` `email_otps` table).
2. **Event Processing (Apache Kafka):** Topics `user-registered`, `email-otp-requested`, `email-otp-sent`, `email-otp-verified`, `order-created`, `payment-confirmed`, `inventory-updated`, `shipment-status-updated`, `notification-requested`.
3. **Email OTP Service:** Cryptographically secure 6-digit OTP generation, hashed storage with BCrypt, 5-minute expiration, and luxury Black-Navy + Metallic Gold HTML templates.
