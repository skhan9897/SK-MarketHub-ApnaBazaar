import axios from 'axios';
import type {
  AuthResponse,
  Category,
  Brand,
  Product,
  ProductVariant,
  ProductImage,
  CartSummary,
  Address,
  Order,
  PendingSeller,
  Coupon,
  Ticket
} from '../types';

const API_BASE_URL = import.meta.env.VITE_API_URL || 'https://sk-markethub-apnabazaar.onrender.com/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('sk_token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export const authService = {
  login: async (email: string, password: string): Promise<AuthResponse> => {
    const res = await api.post<AuthResponse>('/auth/login', { email, password });
    return res.data;
  },
  register: async (payload: { email: string; password: string; firstName: string; lastName?: string; mobile?: string }): Promise<AuthResponse> => {
    const res = await api.post<AuthResponse>('/auth/register', payload);
    return res.data;
  },
  getMe: async () => {
    const res = await api.get('/auth/me');
    return res.data;
  },
  applySeller: async (payload: { storeName: string; businessName?: string; gstNumber?: string }) => {
    const res = await api.post('/auth/seller-application', payload);
    return res.data;
  },
  forgotPassword: async (email: string) => {
    const res = await api.post('/auth/forgot-password', { email });
    return res.data;
  },
  resetPassword: async (email: string, newPassword: string) => {
    const res = await api.post('/auth/reset-password', { email, newPassword });
    return res.data;
  },
  sendOtp: async (email: string) => {
    const res = await api.post('/auth/send-otp', { email });
    return res.data;
  },
  verifyOtp: async (email: string, otpCode: string) => {
    const res = await api.post('/auth/verify-otp', { email, otpCode });
    return res.data;
  }
};

export const catalogService = {
  getCategories: async (): Promise<Category[]> => {
    const res = await api.get<Category[]>('/categories');
    return res.data;
  },
  getBrands: async (): Promise<Brand[]> => {
    const res = await api.get<Brand[]>('/brands');
    return res.data;
  },
  getProducts: async (search?: string, categoryId?: number, brandId?: number): Promise<Product[]> => {
    const params: Record<string, any> = {};
    if (search) params.search = search;
    if (categoryId) params.categoryId = categoryId;
    if (brandId) params.brandId = brandId;
    const res = await api.get<Product[]>('/products', { params });
    return res.data;
  },
  getProductDetails: async (productId: number): Promise<Product> => {
    const res = await api.get<Product>(`/products/${productId}`);
    return res.data;
  },
  getVariants: async (productId: number): Promise<ProductVariant[]> => {
    const res = await api.get<ProductVariant[]>(`/products/${productId}/variants`);
    return res.data;
  },
  getImages: async (productId: number): Promise<ProductImage[]> => {
    const res = await api.get<ProductImage[]>(`/products/${productId}/images`);
    return res.data;
  }
};

export const cartService = {
  getCart: async (): Promise<CartSummary> => {
    const res = await api.get<CartSummary>('/cart');
    return res.data;
  },
  addItem: async (productId: number, variantId?: number, quantity: number = 1): Promise<CartSummary> => {
    const res = await api.post<CartSummary>('/cart/items', { productId, variantId, quantity });
    return res.data;
  },
  updateQuantity: async (itemId: number, quantity: number): Promise<CartSummary> => {
    const res = await api.put<CartSummary>(`/cart/items/${itemId}`, { quantity });
    return res.data;
  },
  removeItem: async (itemId: number): Promise<CartSummary> => {
    const res = await api.delete<CartSummary>(`/cart/items/${itemId}`);
    return res.data;
  },
  clearCart: async (): Promise<CartSummary> => {
    const res = await api.delete<CartSummary>('/cart');
    return res.data;
  }
};

export const addressService = {
  getAddresses: async (): Promise<Address[]> => {
    const res = await api.get<Address[]>('/addresses');
    return res.data;
  },
  createAddress: async (payload: Omit<Address, 'id' | 'userId'>): Promise<Address> => {
    const res = await api.post<Address>('/addresses', payload);
    return res.data;
  },
  updateAddress: async (id: number, payload: Omit<Address, 'id' | 'userId'>): Promise<Address> => {
    const res = await api.put<Address>(`/addresses/${id}`, payload);
    return res.data;
  },
  deleteAddress: async (id: number): Promise<void> => {
    await api.delete(`/addresses/${id}`);
  }
};

export const orderService = {
  checkout: async (addressId: number, couponCode?: string): Promise<Order> => {
    const res = await api.post<Order>('/orders/checkout', { addressId, couponCode });
    return res.data;
  },
  getUserOrders: async (): Promise<Order[]> => {
    const res = await api.get<Order[]>('/orders');
    return res.data;
  },
  payOrder: async (orderId: number, paymentMethod: string): Promise<Order> => {
    const res = await api.post<Order>(`/orders/${orderId}/pay`, { paymentMethod });
    return res.data;
  },
  requestRefund: async (orderId: number, reason: string): Promise<Order> => {
    const res = await api.post<Order>(`/orders/${orderId}/refund`, { reason });
    return res.data;
  },
  createReview: async (payload: { productId: number; orderItemId?: number; rating: number; title?: string; text?: string }) => {
    const res = await api.post('/orders/reviews', payload);
    return res.data;
  }
};

export const wishlistService = {
  getWishlist: async (): Promise<Product[]> => {
    const res = await api.get<Product[]>('/wishlist');
    return res.data;
  },
  addToWishlist: async (productId: number) => {
    const res = await api.post(`/wishlist/${productId}`);
    return res.data;
  },
  removeFromWishlist: async (productId: number) => {
    const res = await api.delete(`/wishlist/${productId}`);
    return res.data;
  }
};

export const couponService = {
  validateCoupon: async (code: string, orderTotal: number) => {
    const res = await api.post('/coupons/validate', { code, orderTotal });
    return res.data;
  }
};

export const sellerService = {
  getProducts: async (): Promise<Product[]> => {
    const res = await api.get<Product[]>('/seller/products');
    return res.data;
  },
  createProduct: async (payload: any): Promise<Product> => {
    const res = await api.post<Product>('/seller/products', payload);
    return res.data;
  },
  addVariant: async (productId: number, payload: any) => {
    const res = await api.post(`/seller/products/${productId}/variants`, payload);
    return res.data;
  },
  addImage: async (productId: number, payload: any) => {
    const res = await api.post(`/seller/products/${productId}/images`, payload);
    return res.data;
  },
  getInventory: async () => {
    const res = await api.get('/seller/inventory');
    return res.data;
  },
  updateStock: async (inventoryId: number, quantity: number) => {
    const res = await api.put(`/seller/inventory/${inventoryId}`, { quantity });
    return res.data;
  },
  getOrders: async (): Promise<Order[]> => {
    const res = await api.get<Order[]>('/seller/orders');
    return res.data;
  },
  updateShipment: async (orderId: number, courierName: string, trackingNumber: string, status?: string) => {
    const res = await api.put(`/seller/orders/${orderId}/shipment`, { courierName, trackingNumber, status });
    return res.data;
  },
  getAnalytics: async () => {
    const res = await api.get('/seller/analytics');
    return res.data;
  }
};

export const adminService = {
  getMetrics: async () => {
    const res = await api.get('/admin/metrics');
    return res.data;
  },
  getPendingSellers: async (): Promise<PendingSeller[]> => {
    const res = await api.get<PendingSeller[]>('/admin/sellers/pending');
    return res.data;
  },
  getAllSellers: async (): Promise<PendingSeller[]> => {
    const res = await api.get<PendingSeller[]>('/admin/sellers');
    return res.data;
  },
  approveSeller: async (sellerId: number) => {
    const res = await api.post(`/admin/sellers/${sellerId}/approve`);
    return res.data;
  },
  rejectSeller: async (sellerId: number) => {
    const res = await api.post(`/admin/sellers/${sellerId}/reject`);
    return res.data;
  },
  getCustomers: async () => {
    const res = await api.get('/admin/customers');
    return res.data;
  },
  updateUserStatus: async (userId: number, status: string) => {
    const res = await api.put(`/admin/customers/${userId}/status`, { status });
    return res.data;
  },
  createCategory: async (payload: { name: string; slug?: string; description?: string; imageUrl?: string }) => {
    const res = await api.post('/admin/categories', payload);
    return res.data;
  },
  createBrand: async (payload: { name: string; slug?: string; logoUrl?: string }) => {
    const res = await api.post('/admin/brands', payload);
    return res.data;
  },
  createCoupon: async (payload: Omit<Coupon, 'id' | 'usedCount'>) => {
    const res = await api.post('/coupons/admin', payload);
    return res.data;
  },
  getAuditLogs: async () => {
    const res = await api.get('/admin/audit-logs');
    return res.data;
  }
};

export const supportService = {
  getTickets: async (): Promise<Ticket[]> => {
    const res = await api.get<Ticket[]>('/support/tickets');
    return res.data;
  },
  createTicket: async (subject: string, message: string): Promise<Ticket> => {
    const res = await api.post<Ticket>('/support/tickets', { subject, message });
    return res.data;
  },
  replyTicket: async (ticketId: string, reply: string): Promise<Ticket> => {
    const res = await api.post<Ticket>(`/support/tickets/${ticketId}/reply`, { reply });
    return res.data;
  }
};
