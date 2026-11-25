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
      title: "BIG SAVINGS DAYS ARE LIVE!",
      subtitle: "Up to 70% OFF on Top Electronics, Smartphones & Fashion Brands",
      badge: "FESTIVE SALE LIVE",
      buttonText: "SHOP DEALS NOW"
    },
    {
      title: "NEXT-GEN SMARTPHONES & LAPTOPS",
      subtitle: "Verified Official Sellers | Free Express Shipping & No-Cost EMI",
      badge: "EXCLUSIVE LAUNCH",
      buttonText: "EXPLORE GADGETS"
    },
    {
      title: "ELEVATE YOUR LIFESTYLE & HOME",
      subtitle: "Premium Quality Guaranteed | Easy 7-Day Returns & Instant Refunds",
      badge: "TRENDING SELECTION",
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

  const productImageMap: Record<number, string> = {
    100: 'https://images.unsplash.com/photo-1695048133142-1a20484d2569?w=600&auto=format&fit=crop&q=80',
    101: 'https://images.unsplash.com/photo-1610945265064-0e34e5519bbf?w=600&auto=format&fit=crop&q=80',
    102: 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=600&auto=format&fit=crop&q=80',
    103: 'https://images.unsplash.com/photo-1595950653106-6c9ebd614d3a?w=600&auto=format&fit=crop&q=80',
    104: 'https://images.unsplash.com/photo-1593359677879-a4bb92f829d1?w=600&auto=format&fit=crop&q=80',
    105: 'https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=600&auto=format&fit=crop&q=80',
    106: 'https://images.unsplash.com/photo-1580481072645-022f9a6d1270?w=600&auto=format&fit=crop&q=80',
    107: 'https://images.unsplash.com/photo-1584622650111-993a426fbf0a?w=600&auto=format&fit=crop&q=80'
  };

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
    <div style={{ backgroundColor: '#F1F3F6', minHeight: '100vh', paddingBottom: '60px' }}>

      {/* Top Horizontal Category Icon Bar */}
      <div style={{ backgroundColor: '#ffffff', borderBottom: '1px solid #e0e0e0', boxShadow: '0 2px 4px rgba(0,0,0,0.04)', padding: '10px 16px', marginBottom: '16px' }}>
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
                  padding: '8px 18px',
                  borderRadius: '20px',
                  border: isSelected ? '2px solid #2874F0' : '1px solid #e0e0e0',
                  backgroundColor: isSelected ? '#2874F0' : '#ffffff',
                  color: isSelected ? '#ffffff' : '#212121',
                  fontWeight: 700,
                  fontSize: '13px',
                  cursor: 'pointer',
                  whiteSpace: 'nowrap',
                  boxShadow: isSelected ? '0 2px 8px rgba(40,116,240,0.3)' : 'none',
                  transition: 'all 0.2s ease'
                }}
              >
                <Icon size={16} color={isSelected ? '#FFE11B' : '#2874F0'} />
                <span>{item.name}</span>
              </button>
            );
          })}
        </div>
      </div>

      {/* Hero Showcase Banner Carousel */}
      {!searchQuery && !selectedCategory && !selectedBrand && (
        <div style={{ maxWidth: '1280px', margin: '0 auto 20px auto', padding: '0 16px' }}>
          <div style={{ backgroundColor: '#2874F0', color: '#ffffff', borderRadius: '8px', padding: '36px', position: 'relative', overflow: 'hidden', boxShadow: '0 4px 12px rgba(0,0,0,0.1)' }}>

            <div style={{ maxWidth: '650px', position: 'relative', zIndex: 2 }}>
              <div style={{ display: 'inline-flex', alignItems: 'center', gap: '6px', backgroundColor: '#1751A8', color: '#FFE11B', padding: '4px 12px', borderRadius: '20px', fontSize: '11px', fontWeight: 800, marginBottom: '12px', letterSpacing: '1px' }}>
                <Flame size={14} /> {heroBanners[heroIndex].badge}
              </div>
              <h1 style={{ fontSize: '32px', fontWeight: 800, margin: '0 0 10px 0', letterSpacing: '0.5px', lineHeight: 1.2 }}>
                {heroBanners[heroIndex].title}
              </h1>
              <p style={{ fontSize: '15px', color: '#e0e0e0', margin: '0 0 20px 0' }}>
                {heroBanners[heroIndex].subtitle}
              </p>

              <button
                onClick={() => window.scrollTo({ top: 400, behavior: 'smooth' })}
                style={{
                  backgroundColor: '#FFE11B',
                  color: '#2874F0',
                  border: 'none',
                  padding: '12px 24px',
                  borderRadius: '4px',
                  fontWeight: 800,
                  fontSize: '14px',
                  cursor: 'pointer',
                  display: 'inline-flex',
                  alignItems: 'center',
                  gap: '8px',
                  boxShadow: '0 4px 10px rgba(0,0,0,0.15)'
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
                    backgroundColor: i === heroIndex ? '#FFE11B' : '#1751A8',
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
      <div style={{ maxWidth: '1280px', margin: '0 auto', padding: '0 16px', display: 'flex', gap: '20px' }}>

        {/* Sidebar Filters */}
        <div style={{ width: '250px', flexShrink: 0, backgroundColor: '#ffffff', padding: '20px', borderRadius: '8px', boxShadow: '0 1px 4px rgba(0,0,0,0.08)', alignSelf: 'flex-start', border: '1px solid #e0e0e0' }}>

          <div style={{ display: 'flex', alignItems: 'center', gap: '8px', borderBottom: '1px solid #e0e0e0', paddingBottom: '12px', marginBottom: '16px' }}>
            <Filter size={18} color="#2874F0" />
            <h3 style={{ fontSize: '15px', fontWeight: 800, margin: 0, color: '#212121' }}>Filters</h3>
          </div>

          {/* Categories */}
          <div style={{ marginBottom: '20px' }}>
            <h4 style={{ fontSize: '13px', fontWeight: 700, color: '#212121', marginBottom: '10px', textTransform: 'uppercase', letterSpacing: '0.5px' }}>Categories</h4>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '4px' }}>
              <button
                onClick={() => setSelectedCategory(undefined)}
                style={{
                  textAlign: 'left',
                  padding: '8px 12px',
                  borderRadius: '4px',
                  border: 'none',
                  background: selectedCategory === undefined ? '#2874F0' : 'transparent',
                  color: selectedCategory === undefined ? '#ffffff' : '#878787',
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
                    borderRadius: '4px',
                    border: 'none',
                    background: selectedCategory === c.id ? '#2874F0' : 'transparent',
                    color: selectedCategory === c.id ? '#ffffff' : '#878787',
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
            <h4 style={{ fontSize: '13px', fontWeight: 700, color: '#212121', marginBottom: '10px', textTransform: 'uppercase', letterSpacing: '0.5px' }}>Brands</h4>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '4px' }}>
              <button
                onClick={() => setSelectedBrand(undefined)}
                style={{
                  textAlign: 'left',
                  padding: '8px 12px',
                  borderRadius: '4px',
                  border: 'none',
                  background: selectedBrand === undefined ? '#1751A8' : 'transparent',
                  color: selectedBrand === undefined ? '#ffffff' : '#878787',
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
                    borderRadius: '4px',
                    border: 'none',
                    background: selectedBrand === b.id ? '#1751A8' : 'transparent',
                    color: selectedBrand === b.id ? '#ffffff' : '#878787',
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
          <div style={{ backgroundColor: '#ffffff', padding: '16px 20px', borderRadius: '8px', border: '1px solid #e0e0e0', marginBottom: '16px', display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
              <Zap size={20} color="#388E3C" />
              <h2 style={{ fontSize: '18px', fontWeight: 800, margin: 0, color: '#212121' }}>
                Deals of the Day ({products.length})
              </h2>
            </div>
            <span style={{ fontSize: '12px', fontWeight: 700, color: '#388E3C', backgroundColor: '#e8f5e9', padding: '4px 10px', borderRadius: '12px' }}>
              Assured Quality
            </span>
          </div>

          {loading ? (
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(230px, 1fr))', gap: '16px' }}>
              {[1, 2, 3, 4, 5, 6].map((n) => <ProductSkeleton key={n} />)}
            </div>
          ) : products.length === 0 ? (
            <div style={{ textAlign: 'center', padding: '60px', backgroundColor: '#ffffff', borderRadius: '8px', border: '1px solid #e0e0e0' }}>
              <Tag size={40} color="#cbd5e1" style={{ marginBottom: '12px' }} />
              <h3 style={{ fontSize: '16px', color: '#212121', margin: 0 }}>No products found</h3>
              <p style={{ fontSize: '13px', color: '#878787' }}>Try adjusting your search terms or category filters.</p>
            </div>
          ) : (
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(230px, 1fr))', gap: '16px' }}>
              {products.map((p: Product) => {
                const effectivePrice = p.salePrice || p.basePrice;
                const hasDiscount = p.salePrice && p.salePrice < p.basePrice;
                const discountPercent = hasDiscount ? Math.round(((p.basePrice - p.salePrice!) / p.basePrice) * 100) : 0;

                return (
                  <div
                    key={p.id}
                    style={{
                      backgroundColor: '#ffffff',
                      borderRadius: '8px',
                      padding: '16px',
                      boxShadow: '0 1px 4px rgba(0,0,0,0.06)',
                      display: 'flex',
                      flexDirection: 'column',
                      justifyContent: 'space-between',
                      position: 'relative',
                      border: '1px solid #f0f0f0'
                    }}
                  >
                    {/* Discount Badge */}
                    {hasDiscount && (
                      <div style={{ position: 'absolute', top: '12px', left: '12px', backgroundColor: '#388E3C', color: '#ffffff', fontSize: '10px', fontWeight: 800, padding: '3px 8px', borderRadius: '2px', letterSpacing: '0.5px' }}>
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
                        background: '#ffffff',
                        border: '1px solid #e0e0e0',
                        borderRadius: '50%',
                        padding: '6px',
                        cursor: 'pointer',
                        display: 'flex',
                        boxShadow: '0 2px 4px rgba(0,0,0,0.08)'
                      }}
                    >
                      <Heart size={16} color="#D94B4B" />
                    </button>

                    {/* Image Container */}
                    <div style={{ width: '100%', height: '160px', backgroundColor: '#ffffff', borderRadius: '4px', display: 'flex', alignItems: 'center', justifyContent: 'center', marginBottom: '12px', overflow: 'hidden' }}>
                      {productImageMap[p.id] ? (
                        <img src={productImageMap[p.id]} alt={p.name} style={{ width: '100%', height: '100%', objectFit: 'contain' }} />
                      ) : (
                        <ShoppingBag size={48} color="#cbd5e1" />
                      )}
                    </div>

                    {/* Product Info */}
                    <div>
                      <span style={{ fontSize: '10px', fontWeight: 800, color: '#2874F0', textTransform: 'uppercase', letterSpacing: '0.5px', backgroundColor: '#e8f0fe', padding: '2px 6px', borderRadius: '2px' }}>SKU: {p.sku}</span>
                      <h3 style={{ fontSize: '14px', fontWeight: 700, color: '#212121', margin: '8px 0 8px 0', height: '38px', overflow: 'hidden' }}>{p.name}</h3>

                      <div style={{ display: 'flex', alignItems: 'baseline', gap: '8px', marginBottom: '14px' }}>
                        <span style={{ fontSize: '18px', fontWeight: 800, color: '#212121' }}>₹{effectivePrice.toLocaleString('en-IN')}</span>
                        {hasDiscount && (
                          <span style={{ fontSize: '13px', color: '#878787', textDecoration: 'line-through' }}>₹{p.basePrice.toLocaleString('en-IN')}</span>
                        )}
                        {hasDiscount && (
                          <span style={{ fontSize: '12px', color: '#388E3C', fontWeight: 700 }}>{discountPercent}% off</span>
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
                        backgroundColor: '#2874F0',
                        color: '#FFE11B',
                        border: 'none',
                        borderRadius: '4px',
                        fontWeight: 800,
                        fontSize: '13px',
                        cursor: 'pointer',
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                        gap: '6px',
                        boxShadow: '0 2px 4px rgba(40,116,240,0.2)'
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
      <footer style={{ backgroundColor: '#172A49', color: '#ffffff', marginTop: '60px', padding: '32px 24px 16px 24px', borderTop: '4px solid #2874F0' }}>
        <div style={{ maxWidth: '1280px', margin: '0 auto', display: 'flex', justifyContent: 'space-between', flexWrap: 'wrap', gap: '24px', borderBottom: '1px solid #203653', paddingBottom: '24px' }}>
          <div>
            <h3 style={{ fontSize: '18px', fontWeight: 800, color: '#ffffff', margin: '0 0 8px 0' }}>SK-MarketHub</h3>
            <p style={{ fontSize: '12px', color: '#FFE11B', fontWeight: 700, margin: 0 }}>SHOP SMART. LIVE BETTER.</p>
          </div>
          <div style={{ fontSize: '12px', color: '#878787' }}>
            &copy; 2026 SK-MarketHub. All rights reserved. Built with Spring Boot, React, and Native Android.
          </div>
        </div>
      </footer>
    </div>
  );
};
