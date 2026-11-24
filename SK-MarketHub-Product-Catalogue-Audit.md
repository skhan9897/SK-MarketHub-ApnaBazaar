# SK-MarketHub Product Catalogue System Audit

## 1. Audit Overview

This document details the architectural audit of the product catalogue, inventory management, filtering, and cross-platform display components across **Spring Boot Backend**, **React Web**, **Native Android**, and **MySQL Database**.

---

## 2. Existing Product Infrastructure Status

| Component | Status | Details |
| :--- | :--- | :--- |
| **Product Entity (`Product.java`)** | Operational | Fields: `id`, `sku`, `name`, `slug`, `description`, `basePrice`, `salePrice`, `active`, `category`, `brand`, `seller`, `createdAt`. |
| **Product Variants (`ProductVariant.java`)** | Operational | Supports size, color, SKU, price adjustment, and stock quantity per variant. |
| **Product Images (`ProductImage.java`)** | Operational | Supports image URL, primary flag (`isPrimary`), and display order. |
| **Category & Brand Entities** | Operational | Dynamic categories (`categories`) and brands (`brands`) fetched via backend REST endpoints. |
| **Web Catalogue (`CustomerCatalog.tsx`)** | Operational | Displays product grid, category/brand filters, discount labels, search, and skeleton placeholders. |
| **Android Catalogue (`fragment_catalog.xml`)** | Operational | Uses RecyclerView grid, search filter, discount labels, and Retrofit API client. |

---

## 3. Product Features Enhancement Plan

1. **Flipkart-Style Product Pricing & Discount Display:**
   - Display MRP (`basePrice`) with strike-through, selling price (`salePrice`) in bold Emerald green (`#23865B`), and calculated discount badge (`X% OFF`).
2. **Product Variants & Detailed Specs:**
   - Interactive variant selector (Color/Size), stock availability badge ("In Stock" / "Only X Left"), delivery & return policies, and quantity picker.
3. **Admin/Seller Operations:**
   - Single-click product creation, price/stock quick edits, and publication status toggling.
