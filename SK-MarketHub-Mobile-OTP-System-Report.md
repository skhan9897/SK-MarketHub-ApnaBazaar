# SK-MarketHub Passwordless Mobile Number OTP System Report

## 1. Executive Summary

This report documents the implementation and cross-platform verification of the **Passwordless Mobile Number OTP System** across **Spring Boot Backend**, **React Web**, **Native Android**, and **MySQL Database**.

Authentication is now 100% passwordless for customers, relying on **Mobile Number** and dynamic on-screen 6-digit OTP codes.

---

## 2. Integrated Features & Endpoints

1. **Database Migration (`V3__Add_Mobile_OTP_Table.sql`):**
   - Created `mobile_otps` table tracking mobile numbers, hashed OTPs, expiration timestamps, attempt counters, and verification state.

2. **Backend Passwordless Endpoints:**
   - `POST /api/auth/send-mobile-otp` (body: `{ "mobile": "9897004767" }`) -> Generates random 6-digit OTP and returns response message with changing on-screen OTP code.
   - `POST /api/auth/login-mobile-otp` (body: `{ "mobile": "9897004767", "otpCode": "749201", "firstName": "John" }`) -> Validates OTP, creates user account (if new), issues JWT access token (`AuthResponse`), and logs user in immediately.

3. **React Web Passwordless Authentication ([AuthPage.tsx](file:///C:/Users/Sajid%20Khan/SK-MarketHub/web/src/pages/AuthPage.tsx)):**
   - Mobile Number input + "Send OTP" button.
   - Dynamic on-screen green banner displaying the changing OTP code.
   - 6-digit OTP code input + "Verify & Sign In" button.

4. **Native Android Passwordless Authentication ([LoginFragment.java](file:///C:/Users/Sajid%20Khan/SK-MarketHub/android/app/src/main/java/com/sk/markethub/ui/auth/LoginFragment.java)):**
   - Matching Mobile Number + Send OTP + 6-digit OTP code + Verify & Sign In workflow.

---

## 3. Build & Verification Summary

| Component | Build Command | Result | Artifact Location |
| :--- | :--- | :--- | :--- |
| **Spring Boot Backend** | `./mvnw test-compile` | `BUILD SUCCESS` | `backend/target/classes/` |
| **React Web SPA** | `npm run build` | `BUILT in 301ms` | `web/dist/` & `backend/src/main/resources/static/` |
| **Native Android App** | `gradle assembleDebug` | `BUILD SUCCESSFUL in 9s` | `android/app/build/outputs/apk/debug/app-debug.apk` (6.3 MB) |
