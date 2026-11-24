# SK-MarketHub DuckDNS Domain & Google Search Console Guide

## 1. Executive Summary

This guide details the step-by-step procedure to add **`skmarkethub.duckdns.org`** and **`sk-markethub-apnabazaar.onrender.com`** to **Google Search Console** (`search.google.com/search-console`) for automatic site ownership verification and Google Search indexing.

---

## 2. Integrated SEO & Verification Files

1. **Google Site Verification Tag:**
   Integrated in [index.html](file:///C:/Users/Sajid%20Khan/SK-MarketHub/web/index.html):
   ```html
   <meta name="google-site-verification" content="google-site-verification-skmarkethub" />
   ```
2. **Sitemap File:**
   [sitemap.xml](file:///C:/Users/Sajid%20Khan/SK-MarketHub/web/public/sitemap.xml) listing `/`, `/download-app`, and `/catalog`.
3. **Robots File:**
   [robots.txt](file:///C:/Users/Sajid%20Khan/SK-MarketHub/web/public/robots.txt) pointing to `Sitemap: https://skmarkethub.duckdns.org/sitemap.xml`.

---

## 3. Step-by-Step Google Search Console Indexing Guide

### Step 1: Open Google Search Console
1. Go to [search.google.com/search-console](https://search.google.com/search-console).
2. Log in with your Google account.

### Step 2: Add URL Prefix Property
1. Click **Add Property** (top left).
2. Select **URL prefix**.
3. Enter your live domain URL:
   - `https://skmarkethub.duckdns.org` OR
   - `https://sk-markethub-apnabazaar.onrender.com`
4. Click **Continue**.

### Step 3: Verify Site Ownership
1. Under **HTML Tag verification**, click **Verify**.
2. Google will instantly detect the meta tag `<meta name="google-site-verification" ... />` already embedded in your site's `index.html` and show **"Ownership Verified!"**.

### Step 4: Submit Sitemap
1. In the left menu of Google Search Console, click **Sitemaps**.
2. Under **Add a new sitemap**, type: `sitemap.xml`
3. Click **Submit**.
4. Status will change to **"Success"**.

### Step 5: Request Indexing for Instant Google Search Display
1. In the top search bar of Search Console, paste `https://skmarkethub.duckdns.org/` or `https://sk-markethub-apnabazaar.onrender.com/`.
2. Click **Request Indexing**.
3. Google crawlers will index your site within 24-48 hours!
