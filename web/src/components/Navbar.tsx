import React from 'react';
import { ShoppingBag, Heart, ShoppingCart, User as UserIcon, Shield, Store, LifeBuoy, LogOut, Search, Sparkles, Smartphone } from 'lucide-react';
import type { User } from '../types';

interface NavbarProps {
  user: User | null;
  currentTab: string;
  setCurrentTab: (tab: string) => void;
  cartCount: number;
  wishlistCount: number;
  searchQuery: string;
  setSearchQuery: (q: string) => void;
  onLogout: () => void;
}

export const Navbar: React.FC<NavbarProps> = ({
  user,
  currentTab,
  setCurrentTab,
  cartCount,
  wishlistCount,
  searchQuery,
  setSearchQuery,
  onLogout
}) => {
  const hasRole = (role: string) => user?.roles?.includes(role);

  return (
    <header style={{ backgroundColor: '#2874F0', color: '#ffffff', boxShadow: '0 2px 10px rgba(0,0,0,0.12)' }}>

      {/* Top Announcement Bar */}
      <div style={{ backgroundColor: '#1751A8', color: '#FFE11B', padding: '6px 16px', fontSize: '12px', textAlign: 'center', fontWeight: 700, letterSpacing: '0.5px', display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '8px' }}>
        <Sparkles size={14} />
        <span>Free Express Delivery on orders above ₹500 | SHOP SMART. LIVE BETTER.</span>
      </div>

      {/* Main Navbar */}
      <div style={{ maxWidth: '1280px', margin: '0 auto', padding: '12px 24px', display: 'flex', alignItems: 'center', justifyContent: 'space-between', flexWrap: 'wrap', gap: '16px' }}>

        {/* Brand Logo */}
        <div
          onClick={() => setCurrentTab('catalog')}
          style={{ display: 'flex', alignItems: 'center', gap: '10px', cursor: 'pointer' }}
        >
          <div style={{ backgroundColor: '#FFE11B', borderRadius: '10px', padding: '8px', display: 'flex', boxShadow: '0 2px 8px rgba(0,0,0,0.15)' }}>
            <ShoppingBag size={24} color="#2874F0" />
          </div>
          <div>
            <h1 style={{ fontSize: '20px', fontWeight: '800', margin: 0, color: '#ffffff', letterSpacing: '0.5px' }}>SK-MarketHub</h1>
            <span style={{ fontSize: '10px', color: '#FFE11B', fontWeight: '700', letterSpacing: '1px' }}>SHOP SMART. LIVE BETTER.</span>
          </div>
        </div>

        {/* Search Bar */}
        <div style={{ flex: '1', maxWidth: '480px', position: 'relative', display: 'flex', alignItems: 'center' }}>
          <input
            type="text"
            placeholder="Search products, brands, categories..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            style={{
              width: '100%',
              padding: '10px 48px 10px 16px',
              borderRadius: '4px',
              border: 'none',
              backgroundColor: '#ffffff',
              color: '#212121',
              fontSize: '14px',
              outline: 'none',
              boxShadow: '0 2px 4px rgba(0,0,0,0.1)'
            }}
          />
          <button
            style={{
              position: 'absolute',
              right: '2px',
              top: '2px',
              bottom: '2px',
              backgroundColor: '#FFE11B',
              border: 'none',
              borderRadius: '2px',
              padding: '0 12px',
              cursor: 'pointer',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center'
            }}
          >
            <Search size={18} color="#2874F0" />
          </button>
        </div>

        {/* Navigation Actions */}
        <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>

          <button
            onClick={() => setCurrentTab('catalog')}
            style={{
              background: currentTab === 'catalog' ? '#1751A8' : 'transparent',
              border: 'none',
              color: currentTab === 'catalog' ? '#FFE11B' : '#ffffff',
              padding: '8px 14px',
              borderRadius: '6px',
              cursor: 'pointer',
              fontSize: '14px',
              fontWeight: 700
            }}
          >
            Catalog
          </button>

          <button
            onClick={() => setCurrentTab('download-app')}
            style={{
              background: currentTab === 'download-app' ? '#1751A8' : 'transparent',
              border: 'none',
              color: currentTab === 'download-app' ? '#FFE11B' : '#ffffff',
              padding: '8px 14px',
              borderRadius: '6px',
              cursor: 'pointer',
              fontSize: '14px',
              fontWeight: 700,
              display: 'flex',
              alignItems: 'center',
              gap: '6px'
            }}
          >
            <Smartphone size={16} color="#FFE11B" />
            <span>Android App</span>
          </button>

          {user && (
            <>
              <button
                onClick={() => setCurrentTab('wishlist')}
                style={{
                  background: currentTab === 'wishlist' ? '#1751A8' : 'transparent',
                  border: 'none',
                  color: '#ffffff',
                  padding: '8px 12px',
                  borderRadius: '6px',
                  cursor: 'pointer',
                  position: 'relative',
                  display: 'flex',
                  alignItems: 'center',
                  gap: '6px'
                }}
              >
                <Heart size={18} color="#ffffff" />
                <span>Wishlist</span>
                {wishlistCount > 0 && (
                  <span style={{ backgroundColor: '#FFE11B', color: '#2874F0', fontSize: '10px', borderRadius: '10px', padding: '2px 6px', fontWeight: 'bold' }}>
                    {wishlistCount}
                  </span>
                )}
              </button>

              <button
                onClick={() => setCurrentTab('cart')}
                style={{
                  background: currentTab === 'cart' ? '#1751A8' : 'transparent',
                  border: 'none',
                  color: '#ffffff',
                  padding: '8px 12px',
                  borderRadius: '6px',
                  cursor: 'pointer',
                  display: 'flex',
                  alignItems: 'center',
                  gap: '6px'
                }}
              >
                <ShoppingCart size={18} color="#FFE11B" />
                <span>Cart</span>
                {cartCount > 0 && (
                  <span style={{ backgroundColor: '#FFE11B', color: '#2874F0', fontSize: '10px', borderRadius: '10px', padding: '2px 6px', fontWeight: 'bold' }}>
                    {cartCount}
                  </span>
                )}
              </button>

              <button
                onClick={() => setCurrentTab('orders')}
                style={{
                  background: currentTab === 'orders' ? '#1751A8' : 'transparent',
                  border: 'none',
                  color: '#ffffff',
                  padding: '8px 12px',
                  borderRadius: '6px',
                  cursor: 'pointer',
                  fontSize: '14px',
                  fontWeight: 600
                }}
              >
                My Orders
              </button>
            </>
          )}

          {hasRole('SELLER') && (
            <button
              onClick={() => setCurrentTab('seller')}
              style={{
                background: currentTab === 'seller' ? '#2e7d32' : '#388E3C',
                border: 'none',
                color: '#ffffff',
                padding: '8px 14px',
                borderRadius: '6px',
                cursor: 'pointer',
                display: 'flex',
                alignItems: 'center',
                gap: '6px',
                fontSize: '13px',
                fontWeight: 700
              }}
            >
              <Store size={15} />
              Seller
            </button>
          )}

          {hasRole('ADMIN') && (
            <button
              onClick={() => setCurrentTab('admin')}
              style={{
                background: currentTab === 'admin' ? '#e6c310' : '#FFE11B',
                border: 'none',
                color: '#2874F0',
                padding: '8px 14px',
                borderRadius: '6px',
                cursor: 'pointer',
                display: 'flex',
                alignItems: 'center',
                gap: '6px',
                fontSize: '13px',
                fontWeight: 800
              }}
            >
              <Shield size={15} />
              Admin
            </button>
          )}

          {(hasRole('SUPPORT') || hasRole('ADMIN')) && (
            <button
              onClick={() => setCurrentTab('support')}
              style={{
                background: currentTab === 'support' ? '#6d39cb' : '#8B5CF6',
                border: 'none',
                color: '#ffffff',
                padding: '8px 14px',
                borderRadius: '6px',
                cursor: 'pointer',
                display: 'flex',
                alignItems: 'center',
                gap: '6px',
                fontSize: '13px',
                fontWeight: 600
              }}
            >
              <LifeBuoy size={15} />
              Support
            </button>
          )}

          {user ? (
            <div style={{ display: 'flex', alignItems: 'center', gap: '10px', marginLeft: '6px' }}>
              <div style={{ textAlign: 'right' }}>
                <div style={{ fontSize: '13px', fontWeight: 700 }}>{user.firstName}</div>
                <div style={{ fontSize: '10px', color: '#e0e0e0' }}>{user.email}</div>
              </div>
              <button
                onClick={onLogout}
                title="Sign Out"
                style={{
                  background: '#D94B4B',
                  border: 'none',
                  color: '#ffffff',
                  padding: '8px',
                  borderRadius: '6px',
                  cursor: 'pointer',
                  display: 'flex'
                }}
              >
                <LogOut size={16} />
              </button>
            </div>
          ) : (
            <button
              onClick={() => setCurrentTab('auth')}
              style={{
                background: '#ffffff',
                color: '#2874F0',
                border: '1px solid #ffffff',
                padding: '8px 20px',
                borderRadius: '4px',
                cursor: 'pointer',
                display: 'flex',
                alignItems: 'center',
                gap: '6px',
                fontSize: '14px',
                fontWeight: 800,
                boxShadow: '0 2px 4px rgba(0,0,0,0.1)'
              }}
            >
              <UserIcon size={16} />
              Login
            </button>
          )}
        </div>
      </div>
    </header>
  );
};
