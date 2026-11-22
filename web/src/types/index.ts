export interface User {
  id: number;
  email: string;
  firstName: string;
  lastName?: string;
  mobile?: string;
  roles: string[];
}

export interface AuthResponse {
  accessToken: string;
  token?: string;
  tokenType: string;
  userId: number;
  id?: number;
  email: string;
  firstName: string;
  roles: string[];
}

export interface Category {
  id: number;
  name: string;
  slug: string;
  description?: string;
  imageUrl?: string;
  active: boolean;
}

export interface Brand {
  id: number;
  name: string;
  slug: string;
  logoUrl?: string;
  active: boolean;
}

export interface Product {
  id: number;
  sellerId?: number;
  categoryId: number;
  brandId?: number;
  name: string;
  slug: string;
  sku: string;
  description?: string;
  shortDescription?: string;
  basePrice: number;
  salePrice?: number;
  taxRate?: number;
  status: string;
  featured: boolean;
}

export interface ProductVariant {
  id: number;
  productId: number;
  sku: string;
  variantName: string;
  price: number;
  salePrice?: number;
}

export interface ProductImage {
  id: number;
  productId: number;
  imageUrl: string;
  altText?: string;
  displayOrder: number;
  primary: boolean;
}

export interface CartItemDetail {
  itemId: number;
  productId: number;
  productName: string;
  sku: string;
  variantId?: number;
  variantName?: string;
  imageUrl?: string;
  unitPrice: number;
  quantity: number;
  totalPrice: number;
}

export interface CartSummary {
  cartId: number;
  items: CartItemDetail[];
  subtotal: number;
  itemCount: number;
}

export interface Address {
  id: number;
  userId: number;
  fullName: string;
  mobile: string;
  addressLine1: string;
  addressLine2?: string;
  city: string;
  state: string;
  country: string;
  pincode: string;
  addressType?: string;
  isDefault?: boolean;
}

export interface OrderItem {
  id: number;
  orderId: number;
  productId: number;
  productName: string;
  sku: string;
  quantity: number;
  unitPrice: number;
  totalPrice: number;
}

export interface Payment {
  id: number;
  orderId: number;
  paymentReference: string;
  provider: string;
  paymentMethod?: string;
  amount: number;
  currency: string;
  paymentStatus: string;
  gatewayTransactionId?: string;
}

export interface Shipment {
  id: number;
  orderId: number;
  courierName?: string;
  trackingNumber?: string;
  shipmentStatus: string;
  shippedAt?: string;
  deliveredAt?: string;
}

export interface Order {
  orderId: number;
  orderNumber: string;
  subtotal: number;
  discountAmount: number;
  shippingAmount: number;
  totalAmount: number;
  orderStatus: string;
  paymentStatus: string;
  createdAt: string;
  items: OrderItem[];
  payment?: Payment;
  shipment?: Shipment;
}

export interface PendingSeller {
  sellerId: number;
  userId: number;
  email: string;
  mobile?: string;
  firstName: string;
  lastName?: string;
  storeName: string;
  businessName?: string;
  gstNumber?: string;
  verificationStatus: string;
  status: string;
  createdAt: string;
}

export interface Coupon {
  id: number;
  code: string;
  discountType: string;
  discountValue: number;
  minimumOrder?: number;
  maximumDiscount?: number;
  usageLimit?: number;
  usedCount?: number;
  active: boolean;
}

export interface Ticket {
  ticketId: string;
  userId: number;
  userEmail: string;
  subject: string;
  message: string;
  status: string;
  createdAt: string;
  replies: Array<{ sender: string; reply: string; timestamp: string }>;
}
