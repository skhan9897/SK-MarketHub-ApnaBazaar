import { useEffect, useState } from 'react';
import { adminService } from '../services/api';
import type { PendingSeller } from '../types';
import { Plus } from 'lucide-react';

export const AdminDashboardPage: React.FC = () => {
  const [activeTab, setActiveTab] = useState<'sellers' | 'customers' | 'coupons' | 'audits'>('sellers');

  const [metrics, setMetrics] = useState<any>(null);
  const [pendingSellers, setPendingSellers] = useState<PendingSeller[]>([]);
  const [allSellers, setAllSellers] = useState<PendingSeller[]>([]);
  const [customers, setCustomers] = useState<any[]>([]);
  const [auditLogs, setAuditLogs] = useState<any[]>([]);

  // Category & Brand Creation Form
  const [catName, setCatName] = useState('');
  const [brandName, setBrandName] = useState('');

  // Coupon Creation Form
  const [showCouponModal, setShowCouponModal] = useState(false);
  const [couponCode, setCouponCode] = useState('');
  const [discountType, setDiscountType] = useState('PERCENTAGE');
  const [discountValue, setDiscountValue] = useState<number>(10);
  const [minOrder, setMinOrder] = useState<number>(500);

  const [loading, setLoading] = useState(false);

  useEffect(() => {
    loadAdminData();
  }, []);

  const loadAdminData = async () => {
    setLoading(true);
    try {
      const [m, pending, all, custs, logs] = await Promise.all([
        adminService.getMetrics(),
        adminService.getPendingSellers(),
        adminService.getAllSellers(),
        adminService.getCustomers(),
        adminService.getAuditLogs()
      ]);
      setMetrics(m);
      setPendingSellers(pending);
      setAllSellers(all);
      setCustomers(custs);
      setAuditLogs(logs);
    } catch (e) {
      console.error('Failed to load admin data', e);
    } finally {
      setLoading(false);
    }
  };

  const handleApproveSeller = async (sellerId: number) => {
    try {
      await adminService.approveSeller(sellerId);
      alert('Seller Approved! SELLER Role Granted.');
      loadAdminData();
    } catch (e) {
      alert('Failed to approve seller');
    }
  };

  const handleRejectSeller = async (sellerId: number) => {
    try {
      await adminService.rejectSeller(sellerId);
      alert('Seller Application Rejected.');
      loadAdminData();
    } catch (e) {
      alert('Failed to reject seller');
    }
  };

  const handleToggleUserStatus = async (userId: number, currentStatus: string) => {
    const nextStatus = currentStatus === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE';
    try {
      await adminService.updateUserStatus(userId, nextStatus);
      loadAdminData();
    } catch (e) {
      alert('Failed to update user status');
    }
  };

  const handleCreateCategory = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!catName.trim()) return;
    try {
      await adminService.createCategory({ name: catName });
      alert(`Category "${catName}" Created!`);
      setCatName('');
    } catch (err: any) {
      alert(err.response?.data?.message || 'Failed to create category');
    }
  };

  const handleCreateBrand = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!brandName.trim()) return;
    try {
      await adminService.createBrand({ name: brandName });
      alert(`Brand "${brandName}" Created!`);
      setBrandName('');
    } catch (err: any) {
      alert(err.response?.data?.message || 'Failed to create brand');
    }
  };

  const handleCreateCoupon = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await adminService.createCoupon({
        code: couponCode,
        discountType,
        discountValue,
        minimumOrder: minOrder,
        active: true
      });
      alert('Coupon Created!');
      setShowCouponModal(false);
      setCouponCode('');
    } catch (err: any) {
      alert(err.response?.data?.message || 'Failed to create coupon');
    }
  };

  if (loading) return <div style={{ textAlign: 'center', padding: '60px' }}>Loading Admin Portal...</div>;

  return (
    <div style={{ maxWidth: '1200px', margin: '24px auto', padding: '0 16px' }}>

      {/* Header Metrics */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))', gap: '16px', marginBottom: '24px' }}>
        <div style={{ backgroundColor: '#ffffff', padding: '16px', borderRadius: '12px', boxShadow: '0 2px 8px rgba(0,0,0,0.04)' }}>
          <span style={{ fontSize: '11px', color: '#64748b', textTransform: 'uppercase', fontWeight: 600 }}>Total Users</span>
          <h3 style={{ fontSize: '22px', fontWeight: 700, margin: '4px 0 0 0', color: '#3b82f6' }}>{metrics?.totalCustomers || 0}</h3>
        </div>
        <div style={{ backgroundColor: '#ffffff', padding: '16px', borderRadius: '12px', boxShadow: '0 2px 8px rgba(0,0,0,0.04)' }}>
          <span style={{ fontSize: '11px', color: '#64748b', textTransform: 'uppercase', fontWeight: 600 }}>Pending Approvals</span>
          <h3 style={{ fontSize: '22px', fontWeight: 700, margin: '4px 0 0 0', color: '#f59e0b' }}>{metrics?.pendingSellers || 0}</h3>
        </div>
        <div style={{ backgroundColor: '#ffffff', padding: '16px', borderRadius: '12px', boxShadow: '0 2px 8px rgba(0,0,0,0.04)' }}>
          <span style={{ fontSize: '11px', color: '#64748b', textTransform: 'uppercase', fontWeight: 600 }}>Total Sellers</span>
          <h3 style={{ fontSize: '22px', fontWeight: 700, margin: '4px 0 0 0', color: '#10b981' }}>{metrics?.totalSellers || 0}</h3>
        </div>
        <div style={{ backgroundColor: '#ffffff', padding: '16px', borderRadius: '12px', boxShadow: '0 2px 8px rgba(0,0,0,0.04)' }}>
          <span style={{ fontSize: '11px', color: '#64748b', textTransform: 'uppercase', fontWeight: 600 }}>Total Revenue</span>
          <h3 style={{ fontSize: '22px', fontWeight: 700, margin: '4px 0 0 0', color: '#059669' }}>₹{metrics?.totalRevenue ? Number(metrics.totalRevenue).toLocaleString('en-IN') : 0}</h3>
        </div>
      </div>

      {/* Tabs */}
      <div style={{ display: 'flex', gap: '12px', marginBottom: '20px' }}>
        <button onClick={() => setActiveTab('sellers')} style={{ padding: '10px 18px', borderRadius: '8px', border: 'none', background: activeTab === 'sellers' ? '#1e293b' : '#ffffff', color: activeTab === 'sellers' ? '#fff' : '#334155', fontWeight: 600, cursor: 'pointer' }}>
          Seller Management ({pendingSellers.length} Pending)
        </button>
        <button onClick={() => setActiveTab('customers')} style={{ padding: '10px 18px', borderRadius: '8px', border: 'none', background: activeTab === 'customers' ? '#1e293b' : '#ffffff', color: activeTab === 'customers' ? '#fff' : '#334155', fontWeight: 600, cursor: 'pointer' }}>
          Customer Accounts
        </button>
        <button onClick={() => setActiveTab('coupons')} style={{ padding: '10px 18px', borderRadius: '8px', border: 'none', background: activeTab === 'coupons' ? '#1e293b' : '#ffffff', color: activeTab === 'coupons' ? '#fff' : '#334155', fontWeight: 600, cursor: 'pointer' }}>
          Categories, Brands & Coupons
        </button>
        <button onClick={() => setActiveTab('audits')} style={{ padding: '10px 18px', borderRadius: '8px', border: 'none', background: activeTab === 'audits' ? '#1e293b' : '#ffffff', color: activeTab === 'audits' ? '#fff' : '#334155', fontWeight: 600, cursor: 'pointer' }}>
          Audit Logs
        </button>
      </div>

      {/* Sellers Tab */}
      {activeTab === 'sellers' && (
        <div style={{ backgroundColor: '#ffffff', padding: '24px', borderRadius: '12px', boxShadow: '0 2px 8px rgba(0,0,0,0.04)' }}>
          <h3 style={{ fontSize: '18px', fontWeight: 700, margin: '0 0 16px 0' }}>Seller Applications</h3>

          <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: '14px' }}>
            <thead>
              <tr style={{ backgroundColor: '#f8fafc', borderBottom: '2px solid #e2e8f0', textAlign: 'left' }}>
                <th style={{ padding: '10px' }}>Seller ID</th>
                <th style={{ padding: '10px' }}>Applicant Name</th>
                <th style={{ padding: '10px' }}>Email</th>
                <th style={{ padding: '10px' }}>Store Name</th>
                <th style={{ padding: '10px' }}>Status</th>
                <th style={{ padding: '10px' }}>Actions</th>
              </tr>
            </thead>
            <tbody>
              {allSellers.map((s) => (
                <tr key={s.sellerId} style={{ borderBottom: '1px solid #f1f5f9' }}>
                  <td style={{ padding: '10px' }}>{s.sellerId}</td>
                  <td style={{ padding: '10px', fontWeight: 600 }}>{s.firstName} {s.lastName || ''}</td>
                  <td style={{ padding: '10px' }}>{s.email}</td>
                  <td style={{ padding: '10px', color: '#0284c7', fontWeight: 600 }}>{s.storeName}</td>
                  <td style={{ padding: '10px' }}>
                    <span style={{ backgroundColor: s.verificationStatus === 'APPROVED' ? '#dcfce7' : s.verificationStatus === 'PENDING' ? '#fef3c7' : '#fee2e2', color: s.verificationStatus === 'APPROVED' ? '#15803d' : s.verificationStatus === 'PENDING' ? '#b45309' : '#b91c1c', padding: '2px 8px', borderRadius: '12px', fontSize: '12px', fontWeight: 600 }}>
                      {s.verificationStatus}
                    </span>
                  </td>
                  <td style={{ padding: '10px', display: 'flex', gap: '8px' }}>
                    {s.verificationStatus === 'PENDING' && (
                      <>
                        <button onClick={() => handleApproveSeller(s.sellerId)} style={{ backgroundColor: '#10b981', color: '#fff', border: 'none', padding: '4px 10px', borderRadius: '4px', cursor: 'pointer', fontSize: '12px', fontWeight: 600 }}>Approve</button>
                        <button onClick={() => handleRejectSeller(s.sellerId)} style={{ backgroundColor: '#ef4444', color: '#fff', border: 'none', padding: '4px 10px', borderRadius: '4px', cursor: 'pointer', fontSize: '12px', fontWeight: 600 }}>Reject</button>
                      </>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {/* Customers Tab */}
      {activeTab === 'customers' && (
        <div style={{ backgroundColor: '#ffffff', padding: '24px', borderRadius: '12px', boxShadow: '0 2px 8px rgba(0,0,0,0.04)' }}>
          <h3 style={{ fontSize: '18px', fontWeight: 700, margin: '0 0 16px 0' }}>Customer Accounts</h3>
          <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: '14px' }}>
            <thead>
              <tr style={{ backgroundColor: '#f8fafc', borderBottom: '2px solid #e2e8f0', textAlign: 'left' }}>
                <th style={{ padding: '10px' }}>User ID</th>
                <th style={{ padding: '10px' }}>Name</th>
                <th style={{ padding: '10px' }}>Email</th>
                <th style={{ padding: '10px' }}>Status</th>
                <th style={{ padding: '10px' }}>Action</th>
              </tr>
            </thead>
            <tbody>
              {customers.map((c) => (
                <tr key={c.id} style={{ borderBottom: '1px solid #f1f5f9' }}>
                  <td style={{ padding: '10px' }}>{c.id}</td>
                  <td style={{ padding: '10px', fontWeight: 600 }}>{c.firstName} {c.lastName || ''}</td>
                  <td style={{ padding: '10px' }}>{c.email}</td>
                  <td style={{ padding: '10px' }}>
                    <span style={{ backgroundColor: c.status === 'ACTIVE' ? '#dcfce7' : '#fee2e2', color: c.status === 'ACTIVE' ? '#15803d' : '#b91c1c', padding: '2px 8px', borderRadius: '12px', fontSize: '12px', fontWeight: 600 }}>{c.status}</span>
                  </td>
                  <td style={{ padding: '10px' }}>
                    <button onClick={() => handleToggleUserStatus(c.id, c.status)} style={{ padding: '4px 10px', backgroundColor: c.status === 'ACTIVE' ? '#ef4444' : '#10b981', color: '#fff', border: 'none', borderRadius: '4px', cursor: 'pointer', fontSize: '12px' }}>
                      {c.status === 'ACTIVE' ? 'Block User' : 'Activate User'}
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {/* Categories, Brands & Coupons */}
      {activeTab === 'coupons' && (
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '20px' }}>

          <div style={{ backgroundColor: '#ffffff', padding: '20px', borderRadius: '12px', boxShadow: '0 2px 8px rgba(0,0,0,0.04)' }}>
            <h4 style={{ margin: '0 0 12px 0' }}>Create Category</h4>
            <form onSubmit={handleCreateCategory} style={{ display: 'flex', gap: '8px' }}>
              <input required placeholder="Category Name (e.g. Electronics)" value={catName} onChange={(e) => setCatName(e.target.value)} style={{ flex: 1, padding: '8px', borderRadius: '6px', border: '1px solid #cbd5e1' }} />
              <button type="submit" style={{ padding: '8px 16px', backgroundColor: '#3b82f6', color: '#fff', border: 'none', borderRadius: '6px', cursor: 'pointer' }}>Create</button>
            </form>
          </div>

          <div style={{ backgroundColor: '#ffffff', padding: '20px', borderRadius: '12px', boxShadow: '0 2px 8px rgba(0,0,0,0.04)' }}>
            <h4 style={{ margin: '0 0 12px 0' }}>Create Brand</h4>
            <form onSubmit={handleCreateBrand} style={{ display: 'flex', gap: '8px' }}>
              <input required placeholder="Brand Name (e.g. Apple)" value={brandName} onChange={(e) => setBrandName(e.target.value)} style={{ flex: 1, padding: '8px', borderRadius: '6px', border: '1px solid #cbd5e1' }} />
              <button type="submit" style={{ padding: '8px 16px', backgroundColor: '#3b82f6', color: '#fff', border: 'none', borderRadius: '6px', cursor: 'pointer' }}>Create</button>
            </form>
          </div>

          <div style={{ gridColumn: 'span 2', backgroundColor: '#ffffff', padding: '20px', borderRadius: '12px', boxShadow: '0 2px 8px rgba(0,0,0,0.04)' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px' }}>
              <h4 style={{ margin: 0 }}>Discounts & Coupon Generator</h4>
              <button onClick={() => setShowCouponModal(true)} style={{ backgroundColor: '#10b981', color: '#fff', border: 'none', padding: '8px 14px', borderRadius: '6px', cursor: 'pointer', fontWeight: 600, display: 'flex', alignItems: 'center', gap: '4px' }}>
                <Plus size={16} /> New Coupon
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Audit Logs */}
      {activeTab === 'audits' && (
        <div style={{ backgroundColor: '#ffffff', padding: '24px', borderRadius: '12px', boxShadow: '0 2px 8px rgba(0,0,0,0.04)' }}>
          <h3 style={{ fontSize: '18px', fontWeight: 700, margin: '0 0 16px 0' }}>Platform Audit Log Records</h3>
          {auditLogs.length === 0 ? (
            <div style={{ color: '#64748b', fontSize: '14px' }}>No audit logs recorded yet.</div>
          ) : (
            <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
              {auditLogs.map((log) => (
                <div key={log.id} style={{ padding: '10px', backgroundColor: '#f8fafc', borderRadius: '6px', fontSize: '13px', display: 'flex', justifyContent: 'space-between' }}>
                  <span><strong>{log.action}</strong> - User #{log.userId}</span>
                  <span style={{ color: '#94a3b8' }}>{new Date(log.createdAt).toLocaleString()}</span>
                </div>
              ))}
            </div>
          )}
        </div>
      )}

      {/* Coupon Modal */}
      {showCouponModal && (
        <div style={{ position: 'fixed', top: 0, left: 0, right: 0, bottom: 0, backgroundColor: 'rgba(0,0,0,0.5)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
          <form onSubmit={handleCreateCoupon} style={{ backgroundColor: '#fff', padding: '24px', borderRadius: '12px', width: '400px', display: 'flex', flexDirection: 'column', gap: '12px' }}>
            <h3 style={{ margin: 0 }}>Create Coupon Code</h3>
            <input required placeholder="Coupon Code (e.g. SAVE10)" value={couponCode} onChange={(e) => setCouponCode(e.target.value.toUpperCase())} style={{ padding: '8px', borderRadius: '6px', border: '1px solid #cbd5e1' }} />
            <select value={discountType} onChange={(e) => setDiscountType(e.target.value)} style={{ padding: '8px', borderRadius: '6px', border: '1px solid #cbd5e1' }}>
              <option value="PERCENTAGE">PERCENTAGE (%)</option>
              <option value="FLAT">FLAT (₹)</option>
            </select>
            <input required type="number" placeholder="Discount Value" value={discountValue} onChange={(e) => setDiscountValue(Number(e.target.value))} style={{ padding: '8px', borderRadius: '6px', border: '1px solid #cbd5e1' }} />
            <input type="number" placeholder="Minimum Order Amount" value={minOrder} onChange={(e) => setMinOrder(Number(e.target.value))} style={{ padding: '8px', borderRadius: '6px', border: '1px solid #cbd5e1' }} />
            <div style={{ display: 'flex', gap: '10px', justifyContent: 'flex-end', marginTop: '8px' }}>
              <button type="button" onClick={() => setShowCouponModal(false)} style={{ padding: '8px 14px', border: 'none', background: '#e2e8f0', borderRadius: '6px', cursor: 'pointer' }}>Cancel</button>
              <button type="submit" style={{ padding: '8px 14px', border: 'none', background: '#10b981', color: '#fff', borderRadius: '6px', cursor: 'pointer', fontWeight: 600 }}>Create Coupon</button>
            </div>
          </form>
        </div>
      )}

    </div>
  );
};
