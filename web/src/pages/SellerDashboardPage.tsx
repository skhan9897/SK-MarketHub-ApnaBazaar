import { useEffect, useState } from 'react';
import { sellerService, catalogService } from '../services/api';
import type { Product, Category, Brand, Order } from '../types';
import { Plus } from 'lucide-react';

export const SellerDashboardPage: React.FC = () => {
  const [activeTab, setActiveTab] = useState<'products' | 'inventory' | 'orders' | 'analytics'>('products');
  const [analytics, setAnalytics] = useState<any>(null);
  const [products, setProducts] = useState<Product[]>([]);
  const [inventory, setInventory] = useState<any[]>([]);
  const [orders, setOrders] = useState<Order[]>([]);

  const [categories, setCategories] = useState<Category[]>([]);
  const [brands, setBrands] = useState<Brand[]>([]);

  // New Product Modal
  const [showProductModal, setShowProductModal] = useState(false);
  const [prodName, setProdName] = useState('');
  const [prodSku, setProdSku] = useState('');
  const [prodCategoryId, setProdCategoryId] = useState<number>(0);
  const [prodBrandId, setProdBrandId] = useState<number | undefined>(undefined);
  const [prodBasePrice, setProdBasePrice] = useState<number>(1000);
  const [prodSalePrice, setProdSalePrice] = useState<number>(899);
  const [prodDescription, setProdDescription] = useState('');
  const [prodInitialStock, setProdInitialStock] = useState<number>(10);

  // Add Variant & Image Modal
  const [variantProductId, setVariantProductId] = useState<number | null>(null);
  const [varSku, setVarSku] = useState('');
  const [varName, setVarName] = useState('');
  const [varPrice, setVarPrice] = useState<number>(1000);
  const [varSalePrice] = useState<number>(899);

  const [imageProductId, setImageProductId] = useState<number | null>(null);
  const [imgUrl, setImgUrl] = useState('');

  // Shipment Tracking Modal
  const [selectedOrderShipment, setSelectedOrderShipment] = useState<number | null>(null);
  const [courierName, setCourierName] = useState('BlueDart');
  const [trackingNumber, setTrackingNumber] = useState('');
  const [shipmentStatus, setShipmentStatus] = useState('SHIPPED');

  const [loading, setLoading] = useState(false);

  useEffect(() => {
    loadSellerData();
  }, []);

  const loadSellerData = async () => {
    setLoading(true);
    try {
      const [an, prods, inv, ords, cats, brs] = await Promise.all([
        sellerService.getAnalytics(),
        sellerService.getProducts(),
        sellerService.getInventory(),
        sellerService.getOrders(),
        catalogService.getCategories(),
        catalogService.getBrands()
      ]);
      setAnalytics(an);
      setProducts(prods);
      setInventory(inv);
      setOrders(ords);
      setCategories(cats);
      setBrands(brs);
      if (cats.length > 0) setProdCategoryId(cats[0].id);
    } catch (e) {
      console.error('Failed to load seller portal data', e);
    } finally {
      setLoading(false);
    }
  };

  const handleCreateProduct = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await sellerService.createProduct({
        name: prodName,
        sku: prodSku,
        categoryId: prodCategoryId,
        brandId: prodBrandId,
        basePrice: prodBasePrice,
        salePrice: prodSalePrice,
        description: prodDescription,
        status: 'ACTIVE',
        featured: false,
        initialStock: prodInitialStock
      });
      alert('Product Created Successfully!');
      setShowProductModal(false);
      loadSellerData();
    } catch (err: any) {
      alert(err.response?.data?.message || 'Failed to create product');
    }
  };

  const handleAddVariant = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!variantProductId) return;
    try {
      await sellerService.addVariant(variantProductId, {
        sku: varSku,
        variantName: varName,
        price: varPrice,
        salePrice: varSalePrice,
        initialStock: 10
      });
      alert('Variant Added!');
      setVariantProductId(null);
      loadSellerData();
    } catch (err: any) {
      alert(err.response?.data?.message || 'Failed to add variant');
    }
  };

  const handleAddImage = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!imageProductId) return;
    try {
      await sellerService.addImage(imageProductId, {
        imageUrl: imgUrl,
        displayOrder: 1,
        primary: true
      });
      alert('Image Added!');
      setImageProductId(null);
      loadSellerData();
    } catch (e) {
      alert('Failed to add image');
    }
  };

  const handleUpdateStock = async (inventoryId: number, currentQty: number) => {
    const newQtyStr = prompt('Enter new stock quantity:', String(currentQty));
    if (newQtyStr === null) return;
    const newQty = parseInt(newQtyStr, 10);
    if (isNaN(newQty) || newQty < 0) return;
    try {
      await sellerService.updateStock(inventoryId, newQty);
      loadSellerData();
    } catch (e) {
      alert('Failed to update stock');
    }
  };

  const handleShipmentSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedOrderShipment) return;
    try {
      await sellerService.updateShipment(selectedOrderShipment, courierName, trackingNumber, shipmentStatus);
      alert('Shipment & Tracking details updated!');
      setSelectedOrderShipment(null);
      loadSellerData();
    } catch (e) {
      alert('Failed to update shipment');
    }
  };

  if (loading) return <div style={{ textAlign: 'center', padding: '60px' }}>Loading Seller Portal...</div>;

  return (
    <div style={{ maxWidth: '1200px', margin: '24px auto', padding: '0 16px' }}>

      {/* Header stats */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))', gap: '16px', marginBottom: '24px' }}>
        <div style={{ backgroundColor: '#ffffff', padding: '20px', borderRadius: '12px', boxShadow: '0 2px 8px rgba(0,0,0,0.04)' }}>
          <span style={{ fontSize: '12px', color: '#64748b', textTransform: 'uppercase', fontWeight: 600 }}>Store Name</span>
          <h3 style={{ fontSize: '20px', fontWeight: 700, margin: '4px 0 0 0', color: '#0f172a' }}>{analytics?.storeName || 'Official Store'}</h3>
        </div>
        <div style={{ backgroundColor: '#ffffff', padding: '20px', borderRadius: '12px', boxShadow: '0 2px 8px rgba(0,0,0,0.04)' }}>
          <span style={{ fontSize: '12px', color: '#64748b', textTransform: 'uppercase', fontWeight: 600 }}>Total Products</span>
          <h3 style={{ fontSize: '24px', fontWeight: 700, margin: '4px 0 0 0', color: '#3b82f6' }}>{products.length}</h3>
        </div>
        <div style={{ backgroundColor: '#ffffff', padding: '20px', borderRadius: '12px', boxShadow: '0 2px 8px rgba(0,0,0,0.04)' }}>
          <span style={{ fontSize: '12px', color: '#64748b', textTransform: 'uppercase', fontWeight: 600 }}>Total Revenue</span>
          <h3 style={{ fontSize: '24px', fontWeight: 700, margin: '4px 0 0 0', color: '#059669' }}>₹{analytics?.totalRevenue ? Number(analytics.totalRevenue).toLocaleString('en-IN') : 0}</h3>
        </div>
      </div>

      {/* Tabs bar */}
      <div style={{ display: 'flex', gap: '12px', marginBottom: '20px' }}>
        <button onClick={() => setActiveTab('products')} style={{ padding: '10px 18px', borderRadius: '8px', border: 'none', background: activeTab === 'products' ? '#1e293b' : '#ffffff', color: activeTab === 'products' ? '#fff' : '#334155', fontWeight: 600, cursor: 'pointer' }}>Products Catalog</button>
        <button onClick={() => setActiveTab('inventory')} style={{ padding: '10px 18px', borderRadius: '8px', border: 'none', background: activeTab === 'inventory' ? '#1e293b' : '#ffffff', color: activeTab === 'inventory' ? '#fff' : '#334155', fontWeight: 600, cursor: 'pointer' }}>Inventory Stock</button>
        <button onClick={() => setActiveTab('orders')} style={{ padding: '10px 18px', borderRadius: '8px', border: 'none', background: activeTab === 'orders' ? '#1e293b' : '#ffffff', color: activeTab === 'orders' ? '#fff' : '#334155', fontWeight: 600, cursor: 'pointer' }}>Customer Orders ({orders.length})</button>
      </div>

      {/* Products Tab */}
      {activeTab === 'products' && (
        <div style={{ backgroundColor: '#ffffff', padding: '24px', borderRadius: '12px', boxShadow: '0 2px 8px rgba(0,0,0,0.04)' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
            <h3 style={{ fontSize: '18px', fontWeight: 700, margin: 0 }}>My Products List</h3>
            <button onClick={() => setShowProductModal(true)} style={{ backgroundColor: '#10b981', color: '#fff', border: 'none', padding: '8px 16px', borderRadius: '6px', cursor: 'pointer', fontWeight: 600, display: 'flex', alignItems: 'center', gap: '6px' }}>
              <Plus size={16} /> Add New Product
            </button>
          </div>

          <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: '14px' }}>
            <thead>
              <tr style={{ backgroundColor: '#f8fafc', borderBottom: '2px solid #e2e8f0', textAlign: 'left' }}>
                <th style={{ padding: '10px' }}>ID</th>
                <th style={{ padding: '10px' }}>SKU</th>
                <th style={{ padding: '10px' }}>Name</th>
                <th style={{ padding: '10px' }}>Price</th>
                <th style={{ padding: '10px' }}>Status</th>
                <th style={{ padding: '10px' }}>Actions</th>
              </tr>
            </thead>
            <tbody>
              {products.map((p) => (
                <tr key={p.id} style={{ borderBottom: '1px solid #f1f5f9' }}>
                  <td style={{ padding: '10px' }}>{p.id}</td>
                  <td style={{ padding: '10px', fontWeight: 600 }}>{p.sku}</td>
                  <td style={{ padding: '10px' }}>{p.name}</td>
                  <td style={{ padding: '10px', color: '#059669', fontWeight: 600 }}>₹{p.salePrice || p.basePrice}</td>
                  <td style={{ padding: '10px' }}><span style={{ backgroundColor: '#dcfce7', color: '#166534', padding: '2px 8px', borderRadius: '12px', fontSize: '12px' }}>{p.status}</span></td>
                  <td style={{ padding: '10px', display: 'flex', gap: '6px' }}>
                    <button onClick={() => setVariantProductId(p.id)} style={{ padding: '4px 8px', border: '1px solid #cbd5e1', background: '#fff', borderRadius: '4px', cursor: 'pointer', fontSize: '12px' }}>+ Variant</button>
                    <button onClick={() => setImageProductId(p.id)} style={{ padding: '4px 8px', border: '1px solid #cbd5e1', background: '#fff', borderRadius: '4px', cursor: 'pointer', fontSize: '12px' }}>+ Image</button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {/* Inventory Tab */}
      {activeTab === 'inventory' && (
        <div style={{ backgroundColor: '#ffffff', padding: '24px', borderRadius: '12px', boxShadow: '0 2px 8px rgba(0,0,0,0.04)' }}>
          <h3 style={{ fontSize: '18px', fontWeight: 700, margin: '0 0 20px 0' }}>Inventory Stock Management</h3>
          <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: '14px' }}>
            <thead>
              <tr style={{ backgroundColor: '#f8fafc', borderBottom: '2px solid #e2e8f0', textAlign: 'left' }}>
                <th style={{ padding: '10px' }}>Product</th>
                <th style={{ padding: '10px' }}>SKU</th>
                <th style={{ padding: '10px' }}>Available Quantity</th>
                <th style={{ padding: '10px' }}>Action</th>
              </tr>
            </thead>
            <tbody>
              {inventory.map((inv) => (
                <tr key={inv.inventoryId} style={{ borderBottom: '1px solid #f1f5f9' }}>
                  <td style={{ padding: '10px', fontWeight: 600 }}>{inv.productName}</td>
                  <td style={{ padding: '10px' }}>{inv.sku}</td>
                  <td style={{ padding: '10px', fontWeight: 700, color: inv.availableQuantity < 5 ? '#ef4444' : '#059669' }}>{inv.availableQuantity} units</td>
                  <td style={{ padding: '10px' }}>
                    <button onClick={() => handleUpdateStock(inv.inventoryId, inv.availableQuantity)} style={{ padding: '4px 10px', backgroundColor: '#3b82f6', color: '#fff', border: 'none', borderRadius: '4px', cursor: 'pointer', fontSize: '12px' }}>
                      Update Stock
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {/* Orders Tab */}
      {activeTab === 'orders' && (
        <div style={{ backgroundColor: '#ffffff', padding: '24px', borderRadius: '12px', boxShadow: '0 2px 8px rgba(0,0,0,0.04)' }}>
          <h3 style={{ fontSize: '18px', fontWeight: 700, margin: '0 0 20px 0' }}>Orders Fulfillment</h3>
          <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
            {orders.map((o) => (
              <div key={o.orderId} style={{ border: '1px solid #e2e8f0', padding: '16px', borderRadius: '8px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <div>
                  <div style={{ fontWeight: 700 }}>ORDER #{o.orderNumber}</div>
                  <div style={{ fontSize: '13px', color: '#64748b' }}>Total Amount: ₹{o.totalAmount} | Status: {o.orderStatus}</div>
                </div>
                <button onClick={() => setSelectedOrderShipment(o.orderId)} style={{ padding: '8px 14px', backgroundColor: '#10b981', color: '#fff', border: 'none', borderRadius: '6px', cursor: 'pointer', fontWeight: 600, fontSize: '13px' }}>
                  Update Tracking / Courier
                </button>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Create Product Modal */}
      {showProductModal && (
        <div style={{ position: 'fixed', top: 0, left: 0, right: 0, bottom: 0, backgroundColor: 'rgba(0,0,0,0.5)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
          <form onSubmit={handleCreateProduct} style={{ backgroundColor: '#fff', padding: '24px', borderRadius: '12px', width: '500px', display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px' }}>
            <h3 style={{ gridColumn: 'span 2', margin: 0 }}>Add New Product</h3>
            <input required placeholder="Product Name" value={prodName} onChange={(e) => setProdName(e.target.value)} style={{ padding: '8px', borderRadius: '6px', border: '1px solid #cbd5e1' }} />
            <input required placeholder="SKU" value={prodSku} onChange={(e) => setProdSku(e.target.value)} style={{ padding: '8px', borderRadius: '6px', border: '1px solid #cbd5e1' }} />

            <select value={prodCategoryId} onChange={(e) => setProdCategoryId(Number(e.target.value))} style={{ padding: '8px', borderRadius: '6px', border: '1px solid #cbd5e1' }}>
              {categories.map((c) => <option key={c.id} value={c.id}>{c.name}</option>)}
            </select>

            <select value={prodBrandId || ''} onChange={(e) => setProdBrandId(e.target.value ? Number(e.target.value) : undefined)} style={{ padding: '8px', borderRadius: '6px', border: '1px solid #cbd5e1' }}>
              <option value="">No Brand</option>
              {brands.map((b) => <option key={b.id} value={b.id}>{b.name}</option>)}
            </select>

            <input required type="number" placeholder="Base Price" value={prodBasePrice} onChange={(e) => setProdBasePrice(Number(e.target.value))} style={{ padding: '8px', borderRadius: '6px', border: '1px solid #cbd5e1' }} />
            <input type="number" placeholder="Sale Price" value={prodSalePrice} onChange={(e) => setProdSalePrice(Number(e.target.value))} style={{ padding: '8px', borderRadius: '6px', border: '1px solid #cbd5e1' }} />
            <input type="number" placeholder="Initial Stock" value={prodInitialStock} onChange={(e) => setProdInitialStock(Number(e.target.value))} style={{ gridColumn: 'span 2', padding: '8px', borderRadius: '6px', border: '1px solid #cbd5e1' }} />
            <textarea placeholder="Description" value={prodDescription} onChange={(e) => setProdDescription(e.target.value)} style={{ gridColumn: 'span 2', padding: '8px', borderRadius: '6px', border: '1px solid #cbd5e1' }} />

            <div style={{ gridColumn: 'span 2', display: 'flex', gap: '10px', justifyContent: 'flex-end', marginTop: '8px' }}>
              <button type="button" onClick={() => setShowProductModal(false)} style={{ padding: '8px 14px', border: 'none', background: '#e2e8f0', borderRadius: '6px', cursor: 'pointer' }}>Cancel</button>
              <button type="submit" style={{ padding: '8px 14px', border: 'none', background: '#10b981', color: '#fff', borderRadius: '6px', cursor: 'pointer', fontWeight: 600 }}>Create Product</button>
            </div>
          </form>
        </div>
      )}

      {/* Variant Modal */}
      {variantProductId && (
        <div style={{ position: 'fixed', top: 0, left: 0, right: 0, bottom: 0, backgroundColor: 'rgba(0,0,0,0.5)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
          <form onSubmit={handleAddVariant} style={{ backgroundColor: '#fff', padding: '24px', borderRadius: '12px', width: '400px', display: 'flex', flexDirection: 'column', gap: '12px' }}>
            <h3 style={{ margin: 0 }}>Add Product Variant</h3>
            <input required placeholder="Variant SKU" value={varSku} onChange={(e) => setVarSku(e.target.value)} style={{ padding: '8px', borderRadius: '6px', border: '1px solid #cbd5e1' }} />
            <input required placeholder="Variant Name (e.g. Red / 64GB)" value={varName} onChange={(e) => setVarName(e.target.value)} style={{ padding: '8px', borderRadius: '6px', border: '1px solid #cbd5e1' }} />
            <input required type="number" placeholder="Price" value={varPrice} onChange={(e) => setVarPrice(Number(e.target.value))} style={{ padding: '8px', borderRadius: '6px', border: '1px solid #cbd5e1' }} />
            <div style={{ display: 'flex', gap: '10px', justifyContent: 'flex-end' }}>
              <button type="button" onClick={() => setVariantProductId(null)} style={{ padding: '8px 14px', border: 'none', background: '#e2e8f0', borderRadius: '6px', cursor: 'pointer' }}>Cancel</button>
              <button type="submit" style={{ padding: '8px 14px', border: 'none', background: '#3b82f6', color: '#fff', borderRadius: '6px', cursor: 'pointer', fontWeight: 600 }}>Save Variant</button>
            </div>
          </form>
        </div>
      )}

      {/* Image Modal */}
      {imageProductId && (
        <div style={{ position: 'fixed', top: 0, left: 0, right: 0, bottom: 0, backgroundColor: 'rgba(0,0,0,0.5)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
          <form onSubmit={handleAddImage} style={{ backgroundColor: '#fff', padding: '24px', borderRadius: '12px', width: '400px', display: 'flex', flexDirection: 'column', gap: '12px' }}>
            <h3 style={{ margin: 0 }}>Add Product Image URL</h3>
            <input required placeholder="Image URL (e.g. https://...)" value={imgUrl} onChange={(e) => setImgUrl(e.target.value)} style={{ padding: '8px', borderRadius: '6px', border: '1px solid #cbd5e1' }} />
            <div style={{ display: 'flex', gap: '10px', justifyContent: 'flex-end' }}>
              <button type="button" onClick={() => setImageProductId(null)} style={{ padding: '8px 14px', border: 'none', background: '#e2e8f0', borderRadius: '6px', cursor: 'pointer' }}>Cancel</button>
              <button type="submit" style={{ padding: '8px 14px', border: 'none', background: '#3b82f6', color: '#fff', borderRadius: '6px', cursor: 'pointer', fontWeight: 600 }}>Save Image</button>
            </div>
          </form>
        </div>
      )}

      {/* Shipment Modal */}
      {selectedOrderShipment && (
        <div style={{ position: 'fixed', top: 0, left: 0, right: 0, bottom: 0, backgroundColor: 'rgba(0,0,0,0.5)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
          <form onSubmit={handleShipmentSubmit} style={{ backgroundColor: '#fff', padding: '24px', borderRadius: '12px', width: '400px', display: 'flex', flexDirection: 'column', gap: '12px' }}>
            <h3 style={{ margin: 0 }}>Update Shipment Tracking</h3>
            <input required placeholder="Courier Name" value={courierName} onChange={(e) => setCourierName(e.target.value)} style={{ padding: '8px', borderRadius: '6px', border: '1px solid #cbd5e1' }} />
            <input required placeholder="Tracking Number" value={trackingNumber} onChange={(e) => setTrackingNumber(e.target.value)} style={{ padding: '8px', borderRadius: '6px', border: '1px solid #cbd5e1' }} />
            <select value={shipmentStatus} onChange={(e) => setShipmentStatus(e.target.value)} style={{ padding: '8px', borderRadius: '6px', border: '1px solid #cbd5e1' }}>
              <option value="SHIPPED">SHIPPED</option>
              <option value="IN_TRANSIT">IN_TRANSIT</option>
              <option value="DELIVERED">DELIVERED</option>
            </select>
            <div style={{ display: 'flex', gap: '10px', justifyContent: 'flex-end' }}>
              <button type="button" onClick={() => setSelectedOrderShipment(null)} style={{ padding: '8px 14px', border: 'none', background: '#e2e8f0', borderRadius: '6px', cursor: 'pointer' }}>Cancel</button>
              <button type="submit" style={{ padding: '8px 14px', border: 'none', background: '#10b981', color: '#fff', borderRadius: '6px', cursor: 'pointer', fontWeight: 600 }}>Update Shipment</button>
            </div>
          </form>
        </div>
      )}

    </div>
  );
};
