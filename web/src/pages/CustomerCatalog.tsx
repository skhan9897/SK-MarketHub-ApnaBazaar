import React, { useEffect, useState } from 'react';
import { catalogService, cartService, wishlistService } from '../services/api';
import type { Category, Brand, Product, User } from '../types';
import { ShoppingCart, Heart, Tag, Filter, ShoppingBag, ShieldCheck, Truck, Sparkles } from 'lucide-react';
import { ProductSkeleton } from '../components/Skeleton';

interface CustomerCatalogProps {
  user: User | null;
  searchQuery: string;
  onCartUpdated: () => void;
  onWishlistUpdated: () => void;
}

export const CustomerCatalog: React.FC<CustomerCatalogProps> = ({
  user,
  searchQuery,
  onCartUpdated,
  onWishlistUpdated
}) => {
  const [categories, setCategories] = useState<Category[]>([]);
  const [brands, setBrands] = useState<Brand[]>([]);
  const [products, setProducts] = useState<Product[]>([]);

  const [selectedCategory, setSelectedCategory] = useState<number | undefined>(undefined);
  const [selectedBrand, setSelectedBrand] = useState<number | undefined>(undefined);

  const [loading, setLoading] = useState(false);
  const [addingProduct, setAddingProduct] = useState<number | null>(null);

  useEffect(() => {
    loadCategoriesAndBrands();
  }, []);

  useEffect(() => {
    loadProducts();
  }, [searchQuery, selectedCategory, selectedBrand]);

  const loadCategoriesAndBrands = async () => {
    try {
      const [cats, brs] = await Promise.all([
        catalogService.getCategories(),
        catalogService.getBrands()
      ]);
      setCategories(cats);
      setBrands(brs);
    } catch (e) {
      console.error('Failed to load categories/brands', e);
    }
  };

  const loadProducts = async () => {
    setLoading(true);
    try {
      const prods = await catalogService.getProducts(searchQuery, selectedCategory, selectedBrand);
      setProducts(prods);
    } catch (e) {
      console.error('Failed to load products', e);
    } finally {
      setLoading(false);
    }
  };

  const handleAddToCart = async (product: Product) => {
    if (!user) {
      alert('Please sign in to add items to your cart.');
      return;
    }
    setAddingProduct(product.id);
    try {
      await cartService.addItem(product.id, undefined, 1);
      onCartUpdated();
    } catch (e) {
      alert('Failed to add product to cart');
    } finally {
      setAddingProduct(null);
    }
  };

  const handleToggleWishlist = async (product: Product) => {
    if (!user) {
      alert('Please sign in to manage wishlist.');
      return;
    }
    try {
      await wishlistService.addToWishlist(product.id);
      onWishlistUpdated();
      alert('Added to Wishlist!');
    } catch (e) {
      alert('Failed to update wishlist');
    }
  };

  return (
    <div style={{ backgroundColor: '#F8F7F3', minHeight: '100vh', paddingBottom: '40px' }}>

      {/* Hero Banner */}
      {!searchQuery && !selectedCategory && !selectedBrand && (
        <div style={{ backgroundColor: '#14243C', color: '#ffffff', padding: '40px 24px', marginBottom: '32px', borderBottom: '4px solid #E7B85C' }}>
          <div style={{ maxWidth: '1280px', margin: '0 auto', display: 'flex', alignItems: 'center', justifyContent: 'space-between', flexWrap: 'wrap', gap: '24px' }}>
            <div style={{ maxWidth: '600px' }}>
              <div style={{ display: 'inline-flex', alignItems: 'center', gap: '6px', backgroundColor: '#203653', color: '#E7B85C', padding: '4px 12px', borderRadius: '20px', fontSize: '12px', fontWeight: 700, marginBottom: '16px' }}>
                <Sparkles size={14} /> PREMIUM SELECTION
              </div>
              <h1 style={{ fontSize: '32px', fontWeight: 800, margin: '0 0 12px 0', letterSpacing: '0.5px' }}>SHOP SMART. LIVE BETTER.</h1>
              <p style={{ fontSize: '16px', color: '#94a3b8', margin: '0 0 24px 0', lineHeight: 1.5 }}>
                Discover verified products from official sellers with fast express delivery and seamless payment protection.
              </p>
            </div>

            {/* Trust Badges Bar */}
            <div style={{ display: 'flex', gap: '16px', flexWrap: 'wrap' }}>
              <div style={{ backgroundColor: '#203653', padding: '16px 20px', borderRadius: '12px', border: '1px solid #2d476c', display: 'flex', alignItems: 'center', gap: '12px' }}>
                <Truck size={28} color="#E7B85C" />
                <div>
                  <div style={{ fontSize: '14px', fontWeight: 700 }}>Express Shipping</div>
                  <div style={{ fontSize: '11px', color: '#94a3b8' }}>Fast delivery to your door</div>
                </div>
              </div>
              <div style={{ backgroundColor: '#203653', padding: '16px 20px', borderRadius: '12px', border: '1px solid #2d476c', display: 'flex', alignItems: 'center', gap: '12px' }}>
                <ShieldCheck size={28} color="#E7B85C" />
                <div>
                  <div style={{ fontSize: '14px', fontWeight: 700 }}>100% Genuine</div>
                  <div style={{ fontSize: '11px', color: '#94a3b8' }}>Verified official sellers</div>
                </div>
              </div>
            </div>
          </div>
        </div>
      )}

      <div style={{ maxWidth: '1280px', margin: '0 auto', padding: '0 16px', display: 'flex', gap: '24px' }}>

        {/* Sidebar Filters */}
        <div style={{ width: '260px', flexShrink: 0, backgroundColor: '#ffffff', padding: '20px', borderRadius: '12px', boxShadow: '0 2px 8px rgba(0,0,0,0.04)', alignSelf: 'flex-start' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px', borderBottom: '1px solid #e2e8f0', paddingBottom: '12px', marginBottom: '16px' }}>
            <Filter size={18} color="#14243C" />
            <h3 style={{ fontSize: '16px', fontWeight: 700, margin: 0, color: '#14243C' }}>Filters</h3>
          </div>

          {/* Categories */}
          <div style={{ marginBottom: '20px' }}>
            <h4 style={{ fontSize: '14px', fontWeight: 700, color: '#202938', marginBottom: '10px' }}>Categories</h4>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '6px' }}>
              <button
                onClick={() => setSelectedCategory(undefined)}
                style={{
                  textAlign: 'left',
                  padding: '8px 12px',
                  borderRadius: '6px',
                  border: 'none',
                  background: selectedCategory === undefined ? '#14243C' : 'transparent',
                  color: selectedCategory === undefined ? '#E7B85C' : '#697386',
                  fontWeight: selectedCategory === undefined ? 700 : 500,
                  cursor: 'pointer',
                  fontSize: '13px'
                }}
              >
                All Categories
              </button>
              {categories.map((c: Category) => (
                <button
                  key={c.id}
                  onClick={() => setSelectedCategory(c.id)}
                  style={{
                    textAlign: 'left',
                    padding: '8px 12px',
                    borderRadius: '6px',
                    border: 'none',
                    background: selectedCategory === c.id ? '#14243C' : 'transparent',
                    color: selectedCategory === c.id ? '#E7B85C' : '#697386',
                    fontWeight: selectedCategory === c.id ? 700 : 500,
                    cursor: 'pointer',
                    fontSize: '13px'
                  }}
                >
                  {c.name}
                </button>
              ))}
            </div>
          </div>

          {/* Brands */}
          <div>
            <h4 style={{ fontSize: '14px', fontWeight: 700, color: '#202938', marginBottom: '10px' }}>Brands</h4>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '6px' }}>
              <button
                onClick={() => setSelectedBrand(undefined)}
                style={{
                  textAlign: 'left',
                  padding: '8px 12px',
                  borderRadius: '6px',
                  border: 'none',
                  background: selectedBrand === undefined ? '#203653' : 'transparent',
                  color: selectedBrand === undefined ? '#ffffff' : '#697386',
                  fontWeight: selectedBrand === undefined ? 700 : 500,
                  cursor: 'pointer',
                  fontSize: '13px'
                }}
              >
                All Brands
              </button>
              {brands.map((b: Brand) => (
                <button
                  key={b.id}
                  onClick={() => setSelectedBrand(b.id)}
                  style={{
                    textAlign: 'left',
                    padding: '8px 12px',
                    borderRadius: '6px',
                    border: 'none',
                    background: selectedBrand === b.id ? '#203653' : 'transparent',
                    color: selectedBrand === b.id ? '#ffffff' : '#697386',
                    fontWeight: selectedBrand === b.id ? 700 : 500,
                    cursor: 'pointer',
                    fontSize: '13px'
                  }}
                >
                  {b.name}
                </button>
              ))}
            </div>
          </div>
        </div>

        {/* Main Product Grid */}
        <div style={{ flex: 1 }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px' }}>
            <h2 style={{ fontSize: '20px', fontWeight: 800, margin: 0, color: '#14243C' }}>
              Products Catalog ({products.length})
            </h2>
          </div>

          {loading ? (
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(230px, 1fr))', gap: '20px' }}>
              {[1, 2, 3, 4, 5, 6].map((n) => <ProductSkeleton key={n} />)}
            </div>
          ) : products.length === 0 ? (
            <div style={{ textAlign: 'center', padding: '60px', backgroundColor: '#ffffff', borderRadius: '12px', boxShadow: '0 2px 8px rgba(0,0,0,0.04)' }}>
              <Tag size={40} color="#cbd5e1" style={{ marginBottom: '12px' }} />
              <h3 style={{ fontSize: '16px', color: '#202938', margin: 0 }}>No products found</h3>
              <p style={{ fontSize: '13px', color: '#697386' }}>Try adjusting your search terms or filters.</p>
            </div>
          ) : (
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(230px, 1fr))', gap: '20px' }}>
              {products.map((p: Product) => {
                const effectivePrice = p.salePrice || p.basePrice;
                const hasDiscount = p.salePrice && p.salePrice < p.basePrice;

                return (
                  <div
                    key={p.id}
                    style={{
                      backgroundColor: '#ffffff',
                      borderRadius: '12px',
                      padding: '16px',
                      boxShadow: '0 2px 10px rgba(0,0,0,0.04)',
                      display: 'flex',
                      flexDirection: 'column',
                      justifyContent: 'space-between',
                      position: 'relative',
                      border: '1px solid #f1f5f9'
                    }}
                  >
                    <button
                      onClick={() => handleToggleWishlist(p)}
                      title="Add to Wishlist"
                      style={{
                        position: 'absolute',
                        top: '12px',
                        right: '12px',
                        background: '#f8fafc',
                        border: '1px solid #e2e8f0',
                        borderRadius: '50%',
                        padding: '6px',
                        cursor: 'pointer',
                        display: 'flex'
                      }}
                    >
                      <Heart size={16} color="#D94B4B" />
                    </button>

                    <div style={{ width: '100%', height: '140px', backgroundColor: '#f8fafc', borderRadius: '8px', display: 'flex', alignItems: 'center', justifyContent: 'center', marginBottom: '12px' }}>
                      <ShoppingBag size={48} color="#cbd5e1" />
                    </div>

                    <div>
                      <span style={{ fontSize: '10px', fontWeight: 800, color: '#E7B85C', textTransform: 'uppercase', letterSpacing: '0.5px', backgroundColor: '#14243C', padding: '2px 6px', borderRadius: '4px' }}>SKU: {p.sku}</span>
                      <h3 style={{ fontSize: '15px', fontWeight: 700, color: '#202938', margin: '8px 0 8px 0', height: '40px', overflow: 'hidden' }}>{p.name}</h3>

                      <div style={{ display: 'flex', alignItems: 'baseline', gap: '8px', marginBottom: '14px' }}>
                        <span style={{ fontSize: '18px', fontWeight: 800, color: '#23865B' }}>₹{effectivePrice.toLocaleString('en-IN')}</span>
                        {hasDiscount && (
                          <span style={{ fontSize: '13px', color: '#697386', textDecoration: 'line-through' }}>₹{p.basePrice.toLocaleString('en-IN')}</span>
                        )}
                      </div>
                    </div>

                    <button
                      onClick={() => handleAddToCart(p)}
                      disabled={addingProduct === p.id}
                      style={{
                        width: '100%',
                        padding: '10px',
                        backgroundColor: '#14243C',
                        color: '#E7B85C',
                        border: 'none',
                        borderRadius: '8px',
                        fontWeight: 700,
                        fontSize: '13px',
                        cursor: 'pointer',
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                        gap: '6px'
                      }}
                    >
                      <ShoppingCart size={16} />
                      {addingProduct === p.id ? 'Adding...' : 'Add to Cart'}
                    </button>
                  </div>
                );
              })}
            </div>
          )}
        </div>
      </div>

      {/* Footer */}
      <footer style={{ backgroundColor: '#14243C', color: '#ffffff', marginTop: '60px', padding: '32px 24px 16px 24px', borderTop: '4px solid #E7B85C' }}>
        <div style={{ maxWidth: '1280px', margin: '0 auto', display: 'flex', justifyContent: 'space-between', flexWrap: 'wrap', gap: '24px', borderBottom: '1px solid #203653', paddingBottom: '24px' }}>
          <div>
            <h3 style={{ fontSize: '18px', fontWeight: 800, color: '#ffffff', margin: '0 0 8px 0' }}>SK-MarketHub</h3>
            <p style={{ fontSize: '12px', color: '#E7B85C', fontWeight: 700, margin: 0 }}>SHOP SMART. LIVE BETTER.</p>
          </div>
          <div style={{ fontSize: '12px', color: '#697386' }}>
            &copy; 2026 SK-MarketHub. All rights reserved. Built with Spring Boot, React, and Native Android.
          </div>
        </div>
      </footer>
    </div>
  );
};
