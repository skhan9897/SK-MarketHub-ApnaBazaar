# SK-MarketHub Shared REST API Contract Specification

**Backend Server URL:** `https://sk-markethub-backend.onrender.com/api` / `https://skmarkethub.duckdns.org/api/`  
**Authentication Header:** `Authorization: Bearer <JWT_ACCESS_TOKEN>`

---

## 1. Authentication & User Management Endpoints

### 1.1 User Login (`POST /auth/login`)
- **Request:** `{ "email": "string", "password": "string" }`
- **Response (200 OK):**
```json
{
  "accessToken": "eyJhbGciOi...",
  "tokenType": "Bearer",
  "userId": 10,
  "email": "customer@skmarkethub.com",
  "firstName": "John",
  "roles": ["CUSTOMER"]
}
```

### 1.2 User Registration (`POST /auth/register`)
- **Request:** `{ "email": "string", "mobile": "string", "password": "string", "firstName": "string", "lastName": "string" }`
- **Response (201 Created):** Same as `AuthResponse`.

### 1.3 Send Email OTP (`POST /auth/send-otp`)
- **Request:** `{ "email": "string" }`
- **Response (200 OK):** `{ "message": "Verification OTP sent to user@example.com" }`

### 1.4 Verify Email OTP (`POST /auth/verify-otp`)
- **Request:** `{ "email": "string", "otpCode": "123456" }`
- **Response (200 OK):** `{ "message": "Email verified successfully." }`

### 1.5 Forgot Password (`POST /auth/forgot-password`)
- **Request:** `{ "email": "string" }`
- **Response (200 OK):** `{ "message": "Password reset request initiated..." }`

### 1.6 Reset Password (`POST /auth/reset-password`)
- **Request:** `{ "email": "string", "newPassword": "string" }`
- **Response (200 OK):** `{ "message": "Password reset successfully." }`

### 1.7 Current User Profile (`GET /auth/me`)
- **Headers:** `Authorization: Bearer <token>`
- **Response (200 OK):** `{ "id": 10, "email": "string", "firstName": "string", "lastName": "string", "mobile": "string", "roles": ["CUSTOMER"] }`

---

## 2. Catalog & Products Endpoints

### 2.1 Get Categories (`GET /categories`)
- **Response (200 OK):** `[ { "id": 1, "name": "Electronics", "slug": "electronics" } ]`

### 2.2 Get Brands (`GET /brands`)
- **Response (200 OK):** `[ { "id": 1, "name": "Apple", "slug": "apple" } ]`

### 2.3 Get Products (`GET /products?search={q}&categoryId={id}&brandId={id}`)
- **Response (200 OK):**
```json
[
  {
    "id": 100,
    "sku": "SKU-100",
    "name": "Wireless Noise Cancelling Headphones",
    "description": "High fidelity audio",
    "basePrice": 4999.00,
    "salePrice": 3999.00,
    "active": true
  }
]
```

---

## 3. Shopping Cart Endpoints

### 3.1 Get Cart (`GET /cart`)
- **Response (200 OK):**
```json
{
  "cartId": 5,
  "itemCount": 2,
  "totalPrice": 7998.00,
  "items": [
    { "id": 10, "productId": 100, "productName": "Wireless Headphones", "quantity": 2, "unitPrice": 3999.00 }
  ]
}
```

### 3.2 Add Cart Item (`POST /cart/items`)
- **Request:** `{ "productId": 100, "quantity": 1 }`
- **Response (200 OK):** Returns updated `CartSummary`.

---

## 4. Checkout & Orders Endpoints

### 4.1 Checkout Order (`POST /orders/checkout`)
- **Request:** `{ "addressId": 1, "couponCode": "SK10" }`
- **Response (200 OK):**
```json
{
  "id": 50,
  "orderNumber": "ORD-20261122-50",
  "status": "PENDING",
  "totalAmount": 3999.00,
  "paymentStatus": "UNPAID"
}
```

### 4.2 Pay Order (`POST /orders/{orderId}/pay`)
- **Request:** `{ "paymentMethod": "UPI" }`
- **Response (200 OK):** Returns updated `Order` with `paymentStatus: "PAID"`.
