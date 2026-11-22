# SK-MarketHub Email OTP Integration Report

## 1. System Overview

The Email OTP System verifies user ownership of email addresses prior to registration completion on both **React Web** and **Native Android**.

---

## 2. Security Architecture

1. **Cryptographic Generation:** 6-digit random OTP generated using `SecureRandom`.
2. **BCrypt Hash Storage:** Plaintext OTP is NEVER stored in database. Stored in `email_otps` table as a BCrypt hash (`otp_hash`).
3. **Expiration:** Configurable 5-minute expiration (`expires_at`).
4. **Attempt Limits:** Maximum 5 verification attempts per OTP.
5. **Branded HTML Template:** Rendered with Navy (`#14243C`) & Metallic Gold (`#D4AF37`) palette and tagline ***"SHOP SMART. LIVE BETTER."***.

---

## 3. Endpoints Implemented

- `POST /api/auth/send-otp` (body: `{ "email": "user@example.com" }`)
- `POST /api/auth/verify-otp` (body: `{ "email": "user@example.com", "otpCode": "123456" }`)
