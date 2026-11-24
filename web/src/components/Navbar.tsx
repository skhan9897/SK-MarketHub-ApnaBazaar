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
    <header style={{ backgroundColor: '#14243C', color: '#ffffff', boxShadow: '0 2px 10px rgba(0,0,0,0.15)' }}>

      {/* Top Announcement Bar */}
      <div style={{ backgroundColor: '#203653', color: '#E7B85C', padding: '6px 16px', fontSize: '12px', textAlign: 'center', fontWeight: 600, letterSpacing: '0.5px', borderBottom: '1px solid #2d476c', display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '8px' }}>
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
          <div style={{ backgroundColor: '#E7B85C', borderRadius: '10px', padding: '8px', display: 'flex', boxShadow: '0 2px 8px rgba(231,184,92,0.3)' }}>
            <ShoppingBag size={24} color="#14243C" />
          </div>
          <div>
            <h1 style={{ fontSize: '20px', fontWeight: '800', margin: 0, color: '#ffffff', letterSpacing: '0.5px' }}>SK-MarketHub</h1>
            <span style={{ fontSize: '10px', color: '#E7B85C', fontWeight: '700', letterSpacing: '1px' }}>SHOP SMART. LIVE BETTER.</span>
          </div>
        </div>

        {/* Search Bar */}
        <div style={{ flex: '1', maxWidth: '450px', position: 'relative' }}>
          <Search size={18} color="#697386" style={{ position: 'absolute', left: '14px', top: '50%', transform: 'translateY(-50%)' }} />
          <input
            type="text"
            placeholder="Search products, brands, categories..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            style={{
              width: '100%',
              padding: '10px 14px 10px 42px',
              borderRadius: '20px',
              border: '1px solid #203653',
              backgroundColor: '#0c1626',
              color: '#ffffff',
              fontSize: '14px',
              outline: 'none'
            }}
          />
        </div>

        {/* Navigation Actions */}
        <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>

          <button
            onClick={() => setCurrentTab('catalog')}
            style={{
              background: currentTab === 'catalog' ? '#203653' : 'transparent',
              border: 'none',
              color: currentTab === 'catalog' ? '#E7B85C' : '#ffffff',
              padding: '8px 14px',
              borderRadius: '6px',
              cursor: 'pointer',
              fontSize: '14px',
              fontWeight: 600
            }}
          >
            Catalog
          </button>

          <button
            onClick={() => setCurrentTab('download-app')}
            style={{
              background: currentTab === 'download-app' ? '#203653' : 'transparent',
              border: 'none',
              color: currentTab === 'download-app' ? '#E7B85C' : '#ffffff',
              padding: '8px 14px',
              borderRadius: '6px',
              cursor: 'pointer',
              fontSize: '14px',
              fontWeight: 600,
              display: 'flex',
              alignItems: 'center',
              gap: '6px'
            }}
          >
            <Smartphone size={16} color="#E7B85C" />
            <span>Android App</span>
          </button>

          {user && (
            <>
              <button
                onClick={() => setCurrentTab('wishlist')}
                style={{
                  background: currentTab === 'wishlist' ? '#203653' : 'transparent',
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
                <Heart size={18} color="#D94B4B" />
                <span>Wishlist</span>
                {wishlistCount > 0 && (
                  <span style={{ backgroundColor: '#D94B4B', color: '#fff', fontSize: '10px', borderRadius: '10px', padding: '2px 6px', fontWeight: 'bold' }}>
                    {wishlistCount}
                  </span>
                )}
              </button>

              <button
                onClick={() => setCurrentTab('cart')}
                style={{
                  background: currentTab === 'cart' ? '#203653' : 'transparent',
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
                <ShoppingCart size={18} color="#E7B85C" />
                <span>Cart</span>
                {cartCount > 0 && (
                  <span style={{ backgroundColor: '#E7B85C', color: '#14243C', fontSize: '10px', borderRadius: '10px', padding: '2px 6px', fontWeight: 'bold' }}>
                    {cartCount}
                  </span>
                )}
              </button>

              <button
                onClick={() => setCurrentTab('orders')}
                style={{
                  background: currentTab === 'orders' ? '#203653' : 'transparent',
                  border: 'none',
                  color: '#ffffff',
                  padding: '8px 12px',
                  borderRadius: '6px',
                  cursor: 'pointer',
                  fontSize: '14px',
                  fontWeight: 500
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
                background: currentTab === 'seller' ? '#186041' : '#23865B',
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
              <Store size={15} />
              Seller
            </button>
          )}

          {hasRole('ADMIN') && (
            <button
              onClick={() => setCurrentTab('admin')}
              style={{
                background: currentTab === 'admin' ? '#b88c3a' : '#E7B85C',
                border: 'none',
                color: '#14243C',
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
                <div style={{ fontSize: '13px', fontWeight: 600 }}>{user.firstName}</div>
                <div style={{ fontSize: '10px', color: '#94a3b8' }}>{user.email}</div>
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
                background: '#E7B85C',
                color: '#14243C',
                border: 'none',
                padding: '8px 16px',
                borderRadius: '6px',
                cursor: 'pointer',
                display: 'flex',
                alignItems: 'center',
                gap: '6px',
                fontSize: '14px',
                fontWeight: 700
              }}
            >
              <UserIcon size={16} />
              Sign In
            </button>
          )}
        </div>
      </div>
    </header>
  );
};
