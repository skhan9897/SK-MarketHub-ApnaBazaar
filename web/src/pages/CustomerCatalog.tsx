import React, { useEffect, useState } from 'react';
import { catalogService, cartService, wishlistService } from '../services/api';
import type { Category, Brand, Product, User } from '../types';
import { ShoppingCart, Heart, Tag, Filter, ShoppingBag, Sparkles, Flame, Zap, ChevronRight, Smartphone, Shirt, Home, Tv, Smile, Dumbbell } from 'lucide-react';
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
  const [heroIndex, setHeroIndex] = useState(0);

  const heroBanners = [
    {
      title: "EXCLUSIVE MARKETPLACE COLLECTION",
      subtitle: "Up to 70% OFF on Top Tech & Fashion Brands",
      badge: "LIMITED TIME DEALS",
      buttonText: "SHOP NEW ARRIVALS"
    },
    {
      title: "NEXT-GEN SMARTPHONES & ACCESSORIES",
      subtitle: "Verified Official Sellers | Free Express Shipping",
      badge: "FESTIVE SALE LIVE",
      buttonText: "EXPLORE GADGETS"
    },
    {
      title: "ELEVATE YOUR WARDROBE & HOME",
      subtitle: "Premium Quality Guaranteed | Easy 7-Day Returns",
      badge: "TRENDING LIFESTYLE",
      buttonText: "DISCOVER LIFESTYLE"
    }
  ];

  const categoryIcons = [
    { name: 'All Products', id: undefined, icon: Sparkles },
    { name: 'Electronics', id: 1, icon: Smartphone },
    { name: 'Fashion', id: 2, icon: Shirt },
    { name: 'Home & Living', id: 3, icon: Home },
    { name: 'Appliances', id: 4, icon: Tv },
    { name: 'Beauty', id: 5, icon: Smile },
    { name: 'Sports', id: 6, icon: Dumbbell }
  ];

  useEffect(() => {
    loadCategoriesAndBrands();
  }, []);

  useEffect(() => {
    loadProducts();
  }, [searchQuery, selectedCategory, selectedBrand]);

  useEffect(() => {
    const timer = setInterval(() => {
      setHeroIndex((prev) => (prev + 1) % heroBanners.length);
    }, 5000);
    return () => clearInterval(timer);
  }, []);

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
    <div style={{ backgroundColor: '#F8F7F3', minHeight: '100vh', paddingBottom: '60px' }}>

      {/* Top Horizontal Category Icon Bar */}
      <div style={{ backgroundColor: '#ffffff', borderBottom: '1px solid #e2e8f0', boxShadow: '0 2px 8px rgba(0,0,0,0.03)', padding: '12px 16px', marginBottom: '20px' }}>
        <div style={{ maxWidth: '1280px', margin: '0 auto', display: 'flex', gap: '16px', overflowX: 'auto', paddingBottom: '4px', alignItems: 'center' }}>
          {categoryIcons.map((item, idx) => {
            const Icon = item.icon;
            const isSelected = selectedCategory === item.id;

            return (
              <button
                key={idx}
                onClick={() => setSelectedCategory(item.id)}
                style={{
                  display: 'flex',
                  alignItems: 'center',
                  gap: '8px',
                  padding: '8px 16px',
                  borderRadius: '20px',
                  border: isSelected ? '2px solid #E7B85C' : '1px solid #e2e8f0',
                  backgroundColor: isSelected ? '#14243C' : '#f8fafc',
                  color: isSelected ? '#E7B85C' : '#334155',
                  fontWeight: 700,
                  fontSize: '13px',
                  cursor: 'pointer',
                  whiteSpace: 'nowrap',
                  boxShadow: isSelected ? '0 2px 8px rgba(231,184,92,0.3)' : 'none',
                  transition: 'all 0.2s ease'
                }}
              >
                <Icon size={16} color={isSelected ? '#E7B85C' : '#14243C'} />
                <span>{item.name}</span>
              </button>
            );
          })}
        </div>
      </div>

      {/* Hero Showcase Banner Carousel */}
      {!searchQuery && !selectedCategory && !selectedBrand && (
        <div style={{ maxWidth: '1280px', margin: '0 auto 28px auto', padding: '0 16px' }}>
          <div style={{ backgroundColor: '#14243C', color: '#ffffff', borderRadius: '16px', padding: '40px', position: 'relative', overflow: 'hidden', borderBottom: '4px solid #E7B85C', boxShadow: '0 4px 20px rgba(0,0,0,0.12)' }}>

            {/* Background Radial Glow */}
            <div style={{ position: 'absolute', right: '-50px', top: '-50px', width: '300px', height: '300px', borderRadius: '50%', background: 'radial-gradient(circle, rgba(231,184,92,0.2) 0%, rgba(20,36,60,0) 70%)', pointerEvents: 'none' }} />

            <div style={{ maxWidth: '650px', position: 'relative', zIndex: 2 }}>
              <div style={{ display: 'inline-flex', alignItems: 'center', gap: '6px', backgroundColor: '#203653', color: '#E7B85C', padding: '4px 12px', borderRadius: '20px', fontSize: '11px', fontWeight: 800, marginBottom: '16px', letterSpacing: '1px' }}>
                <Flame size={14} /> {heroBanners[heroIndex].badge}
              </div>
              <h1 style={{ fontSize: '32px', fontWeight: 800, margin: '0 0 12px 0', letterSpacing: '0.5px', lineHeight: 1.2 }}>
                {heroBanners[heroIndex].title}
              </h1>
              <p style={{ fontSize: '16px', color: '#B9C2D1', margin: '0 0 24px 0' }}>
                {heroBanners[heroIndex].subtitle}
              </p>

              <button
                onClick={() => window.scrollTo({ top: 400, behavior: 'smooth' })}
                style={{
                  backgroundColor: '#E7B85C',
                  color: '#14243C',
                  border: 'none',
                  padding: '12px 24px',
                  borderRadius: '8px',
                  fontWeight: 800,
                  fontSize: '14px',
                  cursor: 'pointer',
                  display: 'inline-flex',
                  alignItems: 'center',
                  gap: '8px',
                  boxShadow: '0 4px 12px rgba(231,184,92,0.4)'
                }}
              >
                <span>{heroBanners[heroIndex].buttonText}</span>
                <ChevronRight size={16} />
              </button>
            </div>

            {/* Banner Indicator Dots */}
            <div style={{ position: 'absolute', bottom: '16px', right: '24px', display: 'flex', gap: '6px' }}>
              {heroBanners.map((_, i) => (
                <div
                  key={i}
                  onClick={() => setHeroIndex(i)}
                  style={{
                    width: i === heroIndex ? '20px' : '8px',
                    height: '8px',
                    borderRadius: '4px',
                    backgroundColor: i === heroIndex ? '#E7B85C' : '#203653',
                    cursor: 'pointer',
                    transition: 'all 0.3s ease'
                  }}
                />
              ))}
            </div>

          </div>
        </div>
      )}

      {/* Main Content Area */}
      <div style={{ maxWidth: '1280px', margin: '0 auto', padding: '0 16px', display: 'flex', gap: '24px' }}>

        {/* Sidebar Filters */}
        <div style={{ width: '250px', flexShrink: 0, backgroundColor: '#ffffff', padding: '20px', borderRadius: '12px', boxShadow: '0 2px 8px rgba(0,0,0,0.04)', alignSelf: 'flex-start', border: '1px solid #e2e8f0' }}>

          <div style={{ display: 'flex', alignItems: 'center', gap: '8px', borderBottom: '1px solid #e2e8f0', paddingBottom: '12px', marginBottom: '16px' }}>
            <Filter size={18} color="#14243C" />
            <h3 style={{ fontSize: '15px', fontWeight: 800, margin: 0, color: '#14243C' }}>Filters</h3>
          </div>

          {/* Categories */}
          <div style={{ marginBottom: '20px' }}>
            <h4 style={{ fontSize: '13px', fontWeight: 700, color: '#202938', marginBottom: '10px', textTransform: 'uppercase', letterSpacing: '0.5px' }}>Categories</h4>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '4px' }}>
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
            <h4 style={{ fontSize: '13px', fontWeight: 700, color: '#202938', marginBottom: '10px', textTransform: 'uppercase', letterSpacing: '0.5px' }}>Brands</h4>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '4px' }}>
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

        {/* Product Grid Area */}
        <div style={{ flex: 1 }}>

          {/* Section Header */}
          <div style={{ backgroundColor: '#ffffff', padding: '16px 20px', borderRadius: '12px', border: '1px solid #e2e8f0', marginBottom: '20px', display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
              <Zap size={20} color="#E7B85C" />
              <h2 style={{ fontSize: '18px', fontWeight: 800, margin: 0, color: '#14243C' }}>
                Deals of the Day ({products.length})
              </h2>
            </div>
            <span style={{ fontSize: '12px', fontWeight: 700, color: '#23865B', backgroundColor: '#ecfdf5', padding: '4px 10px', borderRadius: '12px' }}>
              Verified Inventory
            </span>
          </div>

          {loading ? (
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(230px, 1fr))', gap: '20px' }}>
              {[1, 2, 3, 4, 5, 6].map((n) => <ProductSkeleton key={n} />)}
            </div>
          ) : products.length === 0 ? (
            <div style={{ textAlign: 'center', padding: '60px', backgroundColor: '#ffffff', borderRadius: '12px', boxShadow: '0 2px 8px rgba(0,0,0,0.04)' }}>
              <Tag size={40} color="#cbd5e1" style={{ marginBottom: '12px' }} />
              <h3 style={{ fontSize: '16px', color: '#202938', margin: 0 }}>No products found</h3>
              <p style={{ fontSize: '13px', color: '#697386' }}>Try adjusting your search terms or category filters.</p>
            </div>
          ) : (
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(230px, 1fr))', gap: '20px' }}>
              {products.map((p: Product) => {
                const effectivePrice = p.salePrice || p.basePrice;
                const hasDiscount = p.salePrice && p.salePrice < p.basePrice;
                const discountPercent = hasDiscount ? Math.round(((p.basePrice - p.salePrice!) / p.basePrice) * 100) : 0;

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
                    {/* Discount Badge */}
                    {hasDiscount && (
                      <div style={{ position: 'absolute', top: '12px', left: '12px', backgroundColor: '#E7B85C', color: '#14243C', fontSize: '10px', fontWeight: 800, padding: '3px 8px', borderRadius: '12px', letterSpacing: '0.5px', boxShadow: '0 2px 6px rgba(231,184,92,0.4)' }}>
                        {discountPercent}% OFF
                      </div>
                    )}

                    {/* Wishlist Heart */}
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

                    {/* Image Container */}
                    <div style={{ width: '100%', height: '140px', backgroundColor: '#f8fafc', borderRadius: '8px', display: 'flex', alignItems: 'center', justifyContent: 'center', marginBottom: '12px' }}>
                      <ShoppingBag size={48} color="#cbd5e1" />
                    </div>

                    {/* Product Info */}
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

                    {/* Add to Cart CTA */}
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
