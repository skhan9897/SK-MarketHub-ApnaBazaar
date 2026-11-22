import { useEffect, useState } from 'react';
import { orderService } from '../services/api';
import type { Order } from '../types';
import { Package, Truck, Star } from 'lucide-react';

export const CustomerOrdersPage: React.FC = () => {
  const [orders, setOrders] = useState<Order[]>([]);
  const [loading, setLoading] = useState(false);

  // Refund modal state
  const [selectedOrderForRefund, setSelectedOrderForRefund] = useState<number | null>(null);
  const [refundReason, setRefundReason] = useState('');

  // Review modal state
  const [selectedProductForReview, setSelectedProductForReview] = useState<{ productId: number; orderItemId: number } | null>(null);
  const [rating, setRating] = useState(5);
  const [reviewTitle, setReviewTitle] = useState('');
  const [reviewText, setReviewText] = useState('');

  useEffect(() => {
    loadOrders();
  }, []);

  const loadOrders = async () => {
    setLoading(true);
    try {
      const data = await orderService.getUserOrders();
      setOrders(data);
    } catch (e) {
      console.error('Failed to load orders', e);
    } finally {
      setLoading(false);
    }
  };

  const handlePayOrder = async (orderId: number) => {
    try {
      await orderService.payOrder(orderId, 'CARD');
      alert('Payment Completed via Sandbox Gateway!');
      loadOrders();
    } catch (e) {
      alert('Payment failed');
    }
  };

  const handleRefundSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedOrderForRefund) return;
    try {
      await orderService.requestRefund(selectedOrderForRefund, refundReason);
      alert('Refund Request Submitted to Admin!');
      setSelectedOrderForRefund(null);
      setRefundReason('');
      loadOrders();
    } catch (e) {
      alert('Failed to request refund');
    }
  };

  const handleReviewSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedProductForReview) return;
    try {
      await orderService.createReview({
        productId: selectedProductForReview.productId,
        orderItemId: selectedProductForReview.orderItemId,
        rating,
        title: reviewTitle,
        text: reviewText
      });
      alert('Review Submitted Successfully!');
      setSelectedProductForReview(null);
      setReviewTitle('');
      setReviewText('');
    } catch (e) {
      alert('Failed to submit review');
    }
  };

  if (loading) return <div style={{ textAlign: 'center', padding: '60px' }}>Loading Order History...</div>;

  return (
    <div style={{ maxWidth: '900px', margin: '24px auto', padding: '0 16px' }}>
      <h2 style={{ fontSize: '22px', fontWeight: 700, margin: '0 0 20px 0', color: '#0f172a' }}>My Order History</h2>

      {orders.length === 0 ? (
        <div style={{ backgroundColor: '#ffffff', padding: '40px', borderRadius: '12px', textAlign: 'center' }}>
          <Package size={48} color="#cbd5e1" style={{ marginBottom: '12px' }} />
          <h3 style={{ fontSize: '16px', color: '#475569' }}>No orders placed yet</h3>
        </div>
      ) : (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
          {orders.map((o) => (
            <div key={o.orderId} style={{ backgroundColor: '#ffffff', padding: '20px', borderRadius: '12px', boxShadow: '0 2px 10px rgba(0,0,0,0.04)' }}>

              {/* Order Header */}
              <div style={{ display: 'flex', justifyContent: 'space-between', flexWrap: 'wrap', gap: '12px', borderBottom: '1px solid #f1f5f9', paddingBottom: '12px', marginBottom: '16px' }}>
                <div>
                  <span style={{ fontSize: '12px', fontWeight: 600, color: '#64748b' }}>ORDER #{o.orderNumber}</span>
                  <div style={{ fontSize: '12px', color: '#94a3b8' }}>{new Date(o.createdAt).toLocaleString()}</div>
                </div>

                <div style={{ display: 'flex', gap: '10px', alignItems: 'center' }}>
                  <span style={{ padding: '4px 10px', borderRadius: '20px', fontSize: '12px', fontWeight: 600, backgroundColor: o.paymentStatus === 'PAID' ? '#dcfce7' : '#fef3c7', color: o.paymentStatus === 'PAID' ? '#15803d' : '#b45309' }}>
                    Payment: {o.paymentStatus}
                  </span>
                  <span style={{ padding: '4px 10px', borderRadius: '20px', fontSize: '12px', fontWeight: 600, backgroundColor: '#e0f2fe', color: '#0369a1' }}>
                    Status: {o.orderStatus}
                  </span>
                </div>
              </div>

              {/* Order Items */}
              <div style={{ display: 'flex', flexDirection: 'column', gap: '10px', marginBottom: '16px' }}>
                {o.items.map((item) => (
                  <div key={item.id} style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', backgroundColor: '#f8fafc', padding: '10px 14px', borderRadius: '8px' }}>
                    <div>
                      <div style={{ fontWeight: 600, fontSize: '14px' }}>{item.productName} (x{item.quantity})</div>
                      <div style={{ fontSize: '12px', color: '#64748b' }}>SKU: {item.sku} | Unit Price: ₹{item.unitPrice}</div>
                    </div>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
                      <span style={{ fontWeight: 700, fontSize: '15px' }}>₹{item.totalPrice}</span>
                      <button
                        onClick={() => setSelectedProductForReview({ productId: item.productId, orderItemId: item.id })}
                        style={{ border: '1px solid #cbd5e1', background: '#fff', padding: '4px 8px', borderRadius: '4px', cursor: 'pointer', fontSize: '12px', display: 'flex', alignItems: 'center', gap: '4px' }}
                      >
                        <Star size={12} color="#f59e0b" /> Review
                      </button>
                    </div>
                  </div>
                ))}
              </div>

              {/* Shipment Info */}
              {o.shipment && o.shipment.trackingNumber && (
                <div style={{ backgroundColor: '#eff6ff', padding: '10px 14px', borderRadius: '8px', fontSize: '13px', color: '#1e40af', marginBottom: '16px', display: 'flex', alignItems: 'center', gap: '8px' }}>
                  <Truck size={16} /> Shipment Courier: {o.shipment.courierName} | Tracking Number: <strong>{o.shipment.trackingNumber}</strong> ({o.shipment.shipmentStatus})
                </div>
              )}

              {/* Footer Actions */}
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', borderTop: '1px solid #f1f5f9', paddingTop: '12px' }}>
                <span style={{ fontSize: '16px', fontWeight: 700, color: '#0f172a' }}>Total Amount: ₹{o.totalAmount.toLocaleString('en-IN')}</span>

                <div style={{ display: 'flex', gap: '10px' }}>
                  {o.paymentStatus !== 'PAID' && (
                    <button onClick={() => handlePayOrder(o.orderId)} style={{ padding: '8px 16px', backgroundColor: '#10b981', color: '#fff', border: 'none', borderRadius: '6px', cursor: 'pointer', fontSize: '13px', fontWeight: 600 }}>
                      Pay Now
                    </button>
                  )}

                  {o.paymentStatus === 'PAID' && o.orderStatus !== 'REFUND_REQUESTED' && o.orderStatus !== 'REFUNDED' && (
                    <button onClick={() => setSelectedOrderForRefund(o.orderId)} style={{ padding: '8px 14px', backgroundColor: '#ef4444', color: '#fff', border: 'none', borderRadius: '6px', cursor: 'pointer', fontSize: '13px', fontWeight: 600 }}>
                      Request Refund
                    </button>
                  )}
                </div>
              </div>

            </div>
          ))}
        </div>
      )}

      {/* Refund Modal */}
      {selectedOrderForRefund && (
        <div style={{ position: 'fixed', top: 0, left: 0, right: 0, bottom: 0, backgroundColor: 'rgba(0,0,0,0.5)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
          <form onSubmit={handleRefundSubmit} style={{ backgroundColor: '#fff', padding: '24px', borderRadius: '12px', width: '400px' }}>
            <h3 style={{ margin: '0 0 12px 0' }}>Request Refund</h3>
            <textarea
              required
              rows={3}
              placeholder="Reason for refund request..."
              value={refundReason}
              onChange={(e) => setRefundReason(e.target.value)}
              style={{ width: '100%', padding: '8px', borderRadius: '6px', border: '1px solid #cbd5e1', marginBottom: '16px' }}
            />
            <div style={{ display: 'flex', gap: '10px', justifyContent: 'flex-end' }}>
              <button type="button" onClick={() => setSelectedOrderForRefund(null)} style={{ padding: '8px 14px', border: 'none', background: '#e2e8f0', borderRadius: '6px', cursor: 'pointer' }}>Cancel</button>
              <button type="submit" style={{ padding: '8px 14px', border: 'none', background: '#ef4444', color: '#fff', borderRadius: '6px', cursor: 'pointer', fontWeight: 600 }}>Submit Request</button>
            </div>
          </form>
        </div>
      )}

      {/* Review Modal */}
      {selectedProductForReview && (
        <div style={{ position: 'fixed', top: 0, left: 0, right: 0, bottom: 0, backgroundColor: 'rgba(0,0,0,0.5)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
          <form onSubmit={handleReviewSubmit} style={{ backgroundColor: '#fff', padding: '24px', borderRadius: '12px', width: '400px', display: 'flex', flexDirection: 'column', gap: '12px' }}>
            <h3 style={{ margin: 0 }}>Write Product Review</h3>
            <div>
              <label style={{ fontSize: '13px', fontWeight: 600 }}>Rating (1 to 5 Stars):</label>
              <select value={rating} onChange={(e) => setRating(Number(e.target.value))} style={{ width: '100%', padding: '8px', borderRadius: '6px', border: '1px solid #cbd5e1', marginTop: '4px' }}>
                <option value={5}>5 Stars - Excellent</option>
                <option value={4}>4 Stars - Good</option>
                <option value={3}>3 Stars - Average</option>
                <option value={2}>2 Stars - Poor</option>
                <option value={1}>1 Star - Terrible</option>
              </select>
            </div>
            <input placeholder="Review Title" value={reviewTitle} onChange={(e) => setReviewTitle(e.target.value)} style={{ padding: '8px', borderRadius: '6px', border: '1px solid #cbd5e1' }} />
            <textarea rows={3} placeholder="Write your review experience..." value={reviewText} onChange={(e) => setReviewText(e.target.value)} style={{ padding: '8px', borderRadius: '6px', border: '1px solid #cbd5e1' }} />
            <div style={{ display: 'flex', gap: '10px', justifyContent: 'flex-end', marginTop: '8px' }}>
              <button type="button" onClick={() => setSelectedProductForReview(null)} style={{ padding: '8px 14px', border: 'none', background: '#e2e8f0', borderRadius: '6px', cursor: 'pointer' }}>Cancel</button>
              <button type="submit" style={{ padding: '8px 14px', border: 'none', background: '#3b82f6', color: '#fff', borderRadius: '6px', cursor: 'pointer', fontWeight: 600 }}>Submit Review</button>
            </div>
          </form>
        </div>
      )}

    </div>
  );
};
