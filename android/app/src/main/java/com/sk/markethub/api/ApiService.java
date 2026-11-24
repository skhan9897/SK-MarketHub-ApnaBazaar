package com.sk.markethub.api;

import com.sk.markethub.model.*;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.http.*;

public interface ApiService {

    // Auth
    @POST("auth/login")
    Call<AuthResponse> login(@Body LoginRequest request);

    @POST("auth/register")
    Call<AuthResponse> register(@Body RegisterRequest request);

    @GET("auth/me")
    Call<User> getMe();

    @POST("auth/seller-application")
    Call<Map<String, Object>> submitSellerApplication(@Body SellerApplicationRequest request);

    @POST("auth/forgot-password")
    Call<Map<String, String>> forgotPassword(@Body Map<String, Object> body);

    @POST("auth/reset-password")
    Call<Map<String, String>> resetPassword(@Body Map<String, Object> body);

    @POST("auth/send-otp")
    Call<Map<String, String>> sendOtp(@Body Map<String, Object> body);

    @POST("auth/verify-otp")
    Call<Map<String, String>> verifyOtp(@Body Map<String, Object> body);

    @GET("app/latest")
    Call<Map<String, Object>> getLatestAppRelease();

    // Catalog
    @GET("categories")
    Call<List<Category>> getCategories();

    @GET("brands")
    Call<List<Brand>> getBrands();

    @GET("products")
    Call<List<Product>> getProducts(
            @Query("search") String search,
            @Query("categoryId") Long categoryId,
            @Query("brandId") Long brandId
    );

    @GET("products/{id}")
    Call<Product> getProductDetails(@Path("id") Long id);

    @GET("products/{id}/variants")
    Call<List<ProductVariant>> getProductVariants(@Path("id") Long id);

    @GET("products/{id}/images")
    Call<List<ProductImage>> getProductImages(@Path("id") Long id);

    // Cart
    @GET("cart")
    Call<CartSummary> getCart();

    @POST("cart/items")
    Call<CartSummary> addCartItem(@Body Map<String, Object> body);

    @PUT("cart/items/{itemId}")
    Call<CartSummary> updateCartQuantity(@Path("itemId") Long itemId, @Body Map<String, Object> body);

    @DELETE("cart/items/{itemId}")
    Call<CartSummary> removeCartItem(@Path("itemId") Long itemId);

    @DELETE("cart")
    Call<CartSummary> clearCart();

    // Address
    @GET("addresses")
    Call<List<Address>> getAddresses();

    @POST("addresses")
    Call<Address> createAddress(@Body Address address);

    @PUT("addresses/{id}")
    Call<Address> updateAddress(@Path("id") Long id, @Body Address address);

    @DELETE("addresses/{id}")
    Call<Void> deleteAddress(@Path("id") Long id);

    // Orders & Checkout
    @POST("orders/checkout")
    Call<Order> checkout(@Body Map<String, Object> body);

    @GET("orders")
    Call<List<Order>> getUserOrders();

    @POST("orders/{orderId}/pay")
    Call<Order> payOrder(@Path("orderId") Long orderId, @Body Map<String, Object> body);

    @POST("orders/{orderId}/refund")
    Call<Order> requestRefund(@Path("orderId") Long orderId, @Body Map<String, Object> body);

    @POST("orders/reviews")
    Call<Review> createReview(@Body Map<String, Object> body);

    // Wishlist
    @GET("wishlist")
    Call<List<Product>> getWishlist();

    @POST("wishlist/{productId}")
    Call<Map<String, String>> addToWishlist(@Path("productId") Long productId);

    @DELETE("wishlist/{productId}")
    Call<Map<String, String>> removeFromWishlist(@Path("productId") Long productId);

    // Coupons
    @POST("coupons/validate")
    Call<Map<String, Object>> validateCoupon(@Body Map<String, Object> body);

    // Seller
    @GET("seller/products")
    Call<List<Product>> getSellerProducts();

    @POST("seller/products")
    Call<Product> createSellerProduct(@Body Map<String, Object> body);

    @POST("seller/products/{productId}/variants")
    Call<ProductVariant> addSellerVariant(@Path("productId") Long productId, @Body Map<String, Object> body);

    @POST("seller/products/{productId}/images")
    Call<ProductImage> addSellerImage(@Path("productId") Long productId, @Body Map<String, Object> body);

    @GET("seller/inventory")
    Call<List<Map<String, Object>>> getSellerInventory();

    @PUT("seller/inventory/{inventoryId}")
    Call<Inventory> updateSellerStock(@Path("inventoryId") Long inventoryId, @Body Map<String, Object> body);

    @GET("seller/orders")
    Call<List<Order>> getSellerOrders();

    @PUT("seller/orders/{orderId}/shipment")
    Call<Shipment> updateSellerShipment(@Path("orderId") Long orderId, @Body Map<String, Object> body);

    @GET("seller/analytics")
    Call<Map<String, Object>> getSellerAnalytics();

    // Admin
    @GET("admin/metrics")
    Call<Map<String, Object>> getAdminMetrics();

    @GET("admin/sellers/pending")
    Call<List<PendingSeller>> getPendingSellers();

    @GET("admin/sellers")
    Call<List<PendingSeller>> getAllSellers();

    @POST("admin/sellers/{id}/approve")
    Call<Map<String, Object>> approveSeller(@Path("id") Long id);

    @POST("admin/sellers/{id}/reject")
    Call<Map<String, Object>> rejectSeller(@Path("id") Long id);

    @GET("admin/customers")
    Call<List<User>> getCustomers();

    @PUT("admin/customers/{userId}/status")
    Call<User> updateUserStatus(@Path("userId") Long userId, @Body Map<String, Object> body);

    @POST("admin/categories")
    Call<Category> createCategory(@Body Map<String, Object> body);

    @POST("admin/brands")
    Call<Brand> createBrand(@Body Map<String, Object> body);

    @POST("coupons/admin")
    Call<Coupon> createCoupon(@Body Map<String, Object> body);

    @GET("admin/audit-logs")
    Call<List<AuditLog>> getAuditLogs();

    // Support
    @GET("support/tickets")
    Call<List<Ticket>> getSupportTickets();

    @POST("support/tickets")
    Call<Ticket> createSupportTicket(@Body Map<String, Object> body);

    @POST("support/tickets/{ticketId}/reply")
    Call<Ticket> replySupportTicket(@Path("ticketId") String ticketId, @Body Map<String, Object> body);
}
