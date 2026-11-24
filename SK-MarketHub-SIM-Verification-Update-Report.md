# SK-MarketHub SIM Verification & Automatic In-App Update System Report

## 1. Executive Summary

This report documents the implementation and verification of **Device SIM Card Verification** and **Automatic In-App Update Notifications** across **Spring Boot Backend**, **React Web**, and **Native Android**.

---

## 2. Integrated Features & Architecture

1. **Native Android SIM Card Verification ([SimVerificationUtil.java](file:///C:/Users/Sajid%20Khan/SK-MarketHub/android/app/src/main/java/com/sk/markethub/util/SimVerificationUtil.java)):**
   - Configured `READ_PHONE_STATE` and `READ_PHONE_NUMBERS` permissions in [AndroidManifest.xml](file:///C:/Users/Sajid%20Khan/SK-MarketHub/android/app/src/main/AndroidManifest.xml).
   - Validates that the mobile number entered during login/registration matches the active SIM card inserted inside the device.
   - Displays *"Mobile number mismatch! Please enter the number of the SIM currently inserted in this phone."* if the user enters a non-matching number.

2. **Automatic In-App Update Notification ([MainActivity.java](file:///C:/Users/Sajid%20Khan/SK-MarketHub/android/app/src/main/java/com/sk/markethub/ui/main/MainActivity.java)):**
   - [AppReleaseController.java](file:///C:/Users/Sajid%20Khan/SK-MarketHub/backend/src/main/java/com/skmarkethub/backend/controller/AppReleaseController.java) increments `latestVersionCode` to `2` (`v1.1.0`).
   - On launch, `MainActivity.java` checks `GET /api/app/latest`.
   - Compares server `latestVersionCode` (`2`) with installed local version code (`1`).
   - Displays pop-up alert: *"Update Available: v1.1.0 — Tap 'Update Now' to update"*.
   - Clicking **"Update Now"** opens `https://sk-markethub-apnabazaar.onrender.com/download-app` directly.

---

## 3. Build & Verification Summary

| Component | Build Command | Result | Artifact Location |
| :--- | :--- | :--- | :--- |
| **Spring Boot Backend** | `./mvnw test-compile` | `BUILD SUCCESS` | `backend/target/classes/` |
| **React Web SPA** | `npm run build` | `BUILT in 486ms` | `web/dist/` & `backend/src/main/resources/static/` |
| **Native Android App** | `gradle assembleDebug` | `BUILD SUCCESSFUL in 4s` | `android/app/build/outputs/apk/debug/app-debug.apk` (6.3 MB) |
