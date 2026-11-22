import { useEffect, useState } from 'react';
import { cartService, addressService, couponService, orderService } from '../services/api';
import type { CartSummary, Address, Order } from '../types';
import { ShoppingCart, Trash2, MapPin, CreditCard, CheckCircle, Plus } from 'lucide-react';

interface CartCheckoutPageProps {
  onCartUpdated: () => void;
  onOrderPlaced: () => void;
}

export const CartCheckoutPage: React.FC<CartCheckoutPageProps> = ({ onCartUpdated, onOrderPlaced }) => {
  const [cart, setCart] = useState<CartSummary | null>(null);
  const [addresses, setAddresses] = useState<Address[]>([]);
  const [selectedAddressId, setSelectedAddressId] = useState<number | null>(null);

  // New address form state
  const [showAddressForm, setShowAddressForm] = useState(false);
  const [fullName, setFullName] = useState('');
  const [mobile, setMobile] = useState('');
  const [addressLine1, setAddressLine1] = useState('');
  const [city, setCity] = useState('');
  const [state, setState] = useState('');
  const [pincode, setPincode] = useState('');

  // Coupon state
  const [couponCode, setCouponCode] = useState('');
  const [appliedDiscount, setAppliedDiscount] = useState<number>(0);
  const [couponMsg, setCouponMsg] = useState<string | null>(null);

  const [loading, setLoading] = useState(false);
  const [placingOrder, setPlacingOrder] = useState(false);
  const [createdOrder, setCreatedOrder] = useState<Order | null>(null);

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    setLoading(true);
    try {
      const [c, addrs] = await Promise.all([
        cartService.getCart(),
        addressService.getAddresses()
      ]);
      setCart(c);
      setAddresses(addrs);
      if (addrs.length > 0) {
        setSelectedAddressId(addrs[0].id);
      }
    } catch (e) {
      console.error('Failed to load cart/addresses', e);
    } finally {
      setLoading(false);
    }
  };

  const handleUpdateQty = async (itemId: number, qty: number) => {
    try {
      const updated = await cartService.updateQuantity(itemId, qty);
      setCart(updated);
      onCartUpdated();
    } catch (e) {
      alert('Failed to update quantity');
    }
  };

  const handleCreateAddress = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const newAddr = await addressService.createAddress({
        fullName,
        mobile,
        addressLine1,
        city,
        state,
        country: 'India',
        pincode,
        isDefault: true
      });
      setAddresses([newAddr, ...addresses]);
      setSelectedAddressId(newAddr.id);
      setShowAddressForm(false);
    } catch (e) {
      alert('Failed to create address');
    }
  };

  const handleApplyCoupon = async () => {
    if (!cart || !couponCode.trim()) return;
    try {
      const res = await couponService.validateCoupon(couponCode, cart.subtotal);
      setAppliedDiscount(res.discountAmount);
      setCouponMsg(`Coupon ${res.code} applied! Saved ₹${res.discountAmount}`);
    } catch (err: any) {
      setCouponMsg(err.response?.data?.message || 'Invalid coupon');
      setAppliedDiscount(0);
    }
  };

  const handleCheckout = async () => {
    if (!selectedAddressId) {
      alert('Please select or add a delivery address.');
      return;
    }
    setPlacingOrder(true);
    try {
      const order = await orderService.checkout(selectedAddressId, couponCode);
      setCreatedOrder(order);
      onCartUpdated();
      onOrderPlaced();
    } catch (err: any) {
      alert(err.response?.data?.message || 'Checkout failed');
    } finally {
      setPlacingOrder(false);
    }
  };

  const handlePaySandbox = async () => {
    if (!createdOrder) return;
    try {
      const updated = await orderService.payOrder(createdOrder.orderId, 'UPI');
      setCreatedOrder(updated);
      alert('Sandbox Payment Complete! Order status updated to PAID/PROCESSING.');
    } catch (e) {
      alert('Payment failed');
    }
  };

  if (loading) {
    return <div style={{ textAlign: 'center', padding: '60px' }}>Loading Cart & Checkout...</div>;
  }

  if (createdOrder) {
    return (
      <div style={{ maxWidth: '600px', margin: '40px auto', padding: '32px', backgroundColor: '#ffffff', borderRadius: '12px', textAlign: 'center', boxShadow: '0 4px 20px rgba(0,0,0,0.06)' }}>
        <CheckCircle size={56} color="#10b981" style={{ marginBottom: '16px' }} />
        <h2 style={{ fontSize: '24px', fontWeight: 700, margin: '0 0 8px 0', color: '#0f172a' }}>Order Placed Successfully!</h2>
        <p style={{ color: '#64748b', fontSize: '14px', marginBottom: '24px' }}>Order Number: <strong>{createdOrder.orderNumber}</strong></p>

        <div style={{ backgroundColor: '#f8fafc', padding: '16px', borderRadius: '8px', textAlign: 'left', marginBottom: '24px' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '8px' }}>
            <span>Order Total:</span>
            <strong>₹{createdOrder.totalAmount.toLocaleString('en-IN')}</strong>
          </div>
          <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '8px' }}>
            <span>Payment Status:</span>
            <strong style={{ color: createdOrder.paymentStatus === 'PAID' ? '#10b981' : '#f59e0b' }}>{createdOrder.paymentStatus}</strong>
          </div>
        </div>

        {createdOrder.paymentStatus !== 'PAID' && (
          <button
            onClick={handlePaySandbox}
            style={{
              width: '100%',
              padding: '12px',
              backgroundColor: '#10b981',
              color: '#ffffff',
              border: 'none',
              borderRadius: '8px',
              fontWeight: 600,
              fontSize: '15px',
              cursor: 'pointer',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              gap: '8px'
            }}
          >
            <CreditCard size={18} />
            Simulate Sandbox Payment (Instant Approval)
          </button>
        )}
      </div>
    );
  }

  if (!cart || cart.items.length === 0) {
    return (
      <div style={{ maxWidth: '600px', margin: '40px auto', padding: '60px', backgroundColor: '#ffffff', borderRadius: '12px', textAlign: 'center' }}>
        <ShoppingCart size={48} color="#cbd5e1" style={{ marginBottom: '16px' }} />
        <h2 style={{ fontSize: '20px', color: '#334155', margin: '0 0 8px 0' }}>Your Shopping Cart is Empty</h2>
        <p style={{ color: '#94a3b8', fontSize: '14px' }}>Browse products and add items to your cart to proceed.</p>
      </div>
    );
  }

  const grandTotal = Math.max(0, cart.subtotal - appliedDiscount + (cart.subtotal > 500 ? 0 : 50));

  return (
    <div style={{ maxWidth: '1100px', margin: '24px auto', padding: '0 16px', display: 'flex', gap: '24px', flexWrap: 'wrap' }}>

      {/* Items & Address */}
      <div style={{ flex: 2, minWidth: '320px' }}>

        {/* Cart Items */}
        <div style={{ backgroundColor: '#ffffff', padding: '20px', borderRadius: '12px', boxShadow: '0 2px 8px rgba(0,0,0,0.04)', marginBottom: '20px' }}>
          <h3 style={{ fontSize: '18px', fontWeight: 700, margin: '0 0 16px 0', color: '#0f172a' }}>Shopping Cart ({cart.itemCount} items)</h3>

          <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
            {cart.items.map((item) => (
              <div key={item.itemId} style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', paddingBottom: '16px', borderBottom: '1px solid #f1f5f9' }}>
                <div>
                  <h4 style={{ fontSize: '15px', fontWeight: 600, margin: '0 0 4px 0', color: '#1e293b' }}>{item.productName}</h4>
                  <div style={{ fontSize: '12px', color: '#64748b' }}>SKU: {item.sku}</div>
                  <div style={{ fontSize: '14px', fontWeight: 700, color: '#059669', marginTop: '4px' }}>₹{item.unitPrice.toLocaleString('en-IN')}</div>
                </div>

                <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
                  <div style={{ display: 'flex', alignItems: 'center', border: '1px solid #cbd5e1', borderRadius: '6px' }}>
                    <button onClick={() => handleUpdateQty(item.itemId, item.quantity - 1)} style={{ border: 'none', background: 'none', padding: '4px 10px', cursor: 'pointer' }}>-</button>
                    <span style={{ padding: '0 8px', fontSize: '14px', fontWeight: 600 }}>{item.quantity}</span>
                    <button onClick={() => handleUpdateQty(item.itemId, item.quantity + 1)} style={{ border: 'none', background: 'none', padding: '4px 10px', cursor: 'pointer' }}>+</button>
                  </div>
                  <button onClick={() => handleUpdateQty(item.itemId, 0)} style={{ border: 'none', background: 'none', color: '#ef4444', cursor: 'pointer' }}>
                    <Trash2 size={18} />
                  </button>
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* Address Selection */}
        <div style={{ backgroundColor: '#ffffff', padding: '20px', borderRadius: '12px', boxShadow: '0 2px 8px rgba(0,0,0,0.04)' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px' }}>
            <h3 style={{ fontSize: '18px', fontWeight: 700, margin: 0, color: '#0f172a', display: 'flex', alignItems: 'center', gap: '8px' }}>
              <MapPin size={20} color="#3b82f6" /> Delivery Address
            </h3>
            <button
              onClick={() => setShowAddressForm(!showAddressForm)}
              style={{ background: 'none', border: '1px solid #3b82f6', color: '#3b82f6', padding: '6px 12px', borderRadius: '6px', cursor: 'pointer', fontSize: '13px', fontWeight: 600, display: 'flex', alignItems: 'center', gap: '4px' }}
            >
              <Plus size={14} /> Add Address
            </button>
          </div>

          {showAddressForm && (
            <form onSubmit={handleCreateAddress} style={{ backgroundColor: '#f8fafc', padding: '16px', borderRadius: '8px', marginBottom: '16px', display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px' }}>
              <input required placeholder="Full Name" value={fullName} onChange={(e) => setFullName(e.target.value)} style={{ padding: '8px', borderRadius: '6px', border: '1px solid #cbd5e1' }} />
              <input required placeholder="Mobile" value={mobile} onChange={(e) => setMobile(e.target.value)} style={{ padding: '8px', borderRadius: '6px', border: '1px solid #cbd5e1' }} />
              <input required placeholder="Address Line 1" value={addressLine1} onChange={(e) => setAddressLine1(e.target.value)} style={{ gridColumn: 'span 2', padding: '8px', borderRadius: '6px', border: '1px solid #cbd5e1' }} />
              <input required placeholder="City" value={city} onChange={(e) => setCity(e.target.value)} style={{ padding: '8px', borderRadius: '6px', border: '1px solid #cbd5e1' }} />
              <input required placeholder="State" value={state} onChange={(e) => setState(e.target.value)} style={{ padding: '8px', borderRadius: '6px', border: '1px solid #cbd5e1' }} />
              <input required placeholder="Pincode" value={pincode} onChange={(e) => setPincode(e.target.value)} style={{ padding: '8px', borderRadius: '6px', border: '1px solid #cbd5e1' }} />
              <button type="submit" style={{ gridColumn: 'span 2', padding: '10px', backgroundColor: '#3b82f6', color: '#fff', border: 'none', borderRadius: '6px', fontWeight: 600, cursor: 'pointer' }}>Save Address</button>
            </form>
          )}

          {addresses.length === 0 ? (
            <div style={{ fontSize: '14px', color: '#64748b' }}>No addresses found. Add a delivery address above.</div>
          ) : (
            <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
              {addresses.map((a) => (
                <label key={a.id} style={{ display: 'flex', gap: '12px', alignItems: 'flex-start', padding: '12px', borderRadius: '8px', border: selectedAddressId === a.id ? '2px solid #3b82f6' : '1px solid #e2e8f0', cursor: 'pointer', backgroundColor: selectedAddressId === a.id ? '#eff6ff' : '#ffffff' }}>
                  <input type="radio" name="addr" checked={selectedAddressId === a.id} onChange={() => setSelectedAddressId(a.id)} style={{ marginTop: '4px' }} />
                  <div>
                    <div style={{ fontWeight: 600, fontSize: '14px' }}>{a.fullName} ({a.mobile})</div>
                    <div style={{ fontSize: '13px', color: '#475569' }}>{a.addressLine1}, {a.city}, {a.state} - {a.pincode}</div>
                  </div>
                </label>
              ))}
            </div>
          )}
        </div>
      </div>

      {/* Order Summary */}
      <div style={{ flex: 1, minWidth: '280px' }}>
        <div style={{ backgroundColor: '#ffffff', padding: '20px', borderRadius: '12px', boxShadow: '0 2px 8px rgba(0,0,0,0.04)', position: 'sticky', top: '24px' }}>
          <h3 style={{ fontSize: '18px', fontWeight: 700, margin: '0 0 16px 0', color: '#0f172a' }}>Order Summary</h3>

          {/* Coupon Input */}
          <div style={{ marginBottom: '16px' }}>
            <div style={{ display: 'flex', gap: '8px' }}>
              <input
                type="text"
                placeholder="Coupon Code"
                value={couponCode}
                onChange={(e) => setCouponCode(e.target.value.toUpperCase())}
                style={{ flex: 1, padding: '8px', borderRadius: '6px', border: '1px solid #cbd5e1', fontSize: '13px' }}
              />
              <button onClick={handleApplyCoupon} style={{ padding: '8px 14px', backgroundColor: '#334155', color: '#fff', border: 'none', borderRadius: '6px', cursor: 'pointer', fontSize: '13px', fontWeight: 600 }}>Apply</button>
            </div>
            {couponMsg && <div style={{ fontSize: '12px', color: appliedDiscount > 0 ? '#10b981' : '#ef4444', marginTop: '6px' }}>{couponMsg}</div>}
          </div>

          <div style={{ borderTop: '1px solid #f1f5f9', paddingTop: '16px', display: 'flex', flexDirection: 'column', gap: '10px', fontSize: '14px' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between' }}>
              <span style={{ color: '#64748b' }}>Subtotal:</span>
              <span>₹{cart.subtotal.toLocaleString('en-IN')}</span>
            </div>

            {appliedDiscount > 0 && (
              <div style={{ display: 'flex', justifyContent: 'space-between', color: '#10b981' }}>
                <span>Discount:</span>
                <span>-₹{appliedDiscount.toLocaleString('en-IN')}</span>
              </div>
            )}

            <div style={{ display: 'flex', justifyContent: 'space-between' }}>
              <span style={{ color: '#64748b' }}>Delivery Fee:</span>
              <span>{cart.subtotal > 500 ? 'FREE' : '₹50'}</span>
            </div>

            <div style={{ borderTop: '1px solid #e2e8f0', paddingTop: '12px', marginTop: '6px', display: 'flex', justifyContent: 'space-between', fontSize: '18px', fontWeight: 700, color: '#0f172a' }}>
              <span>Total:</span>
              <span style={{ color: '#059669' }}>₹{grandTotal.toLocaleString('en-IN')}</span>
            </div>
          </div>

          <button
            onClick={handleCheckout}
            disabled={placingOrder}
            style={{
              width: '100%',
              padding: '14px',
              backgroundColor: '#059669',
              color: '#ffffff',
              border: 'none',
              borderRadius: '8px',
              fontWeight: 700,
              fontSize: '15px',
              cursor: 'pointer',
              marginTop: '20px'
            }}
          >
            {placingOrder ? 'Processing Order...' : 'Place Order & Pay'}
          </button>
        </div>
      </div>
    </div>
  );
};
