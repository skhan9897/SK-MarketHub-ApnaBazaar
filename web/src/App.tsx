import { useEffect, useState } from 'react';
import { SplashScreen } from './components/SplashScreen';
import { Navbar } from './components/Navbar';
import { AuthPage } from './pages/AuthPage';
import { CustomerCatalog } from './pages/CustomerCatalog';
import { CartCheckoutPage } from './pages/CartCheckoutPage';
import { CustomerOrdersPage } from './pages/CustomerOrdersPage';
import { SellerDashboardPage } from './pages/SellerDashboardPage';
import { AdminDashboardPage } from './pages/AdminDashboardPage';
import { SupportDashboardPage } from './pages/SupportDashboardPage';
import { DownloadAppPage } from './pages/DownloadAppPage';
import { authService, cartService, wishlistService } from './services/api';
import type { User } from './types';
import './App.css';

export function App() {
  const [showSplash, setShowSplash] = useState(true);
  const [user, setUser] = useState<User | null>(null);
  const [currentTab, setCurrentTab] = useState<string>('catalog');
  const [searchQuery, setSearchQuery] = useState<string>('');

  const [cartCount, setCartCount] = useState<number>(0);
  const [wishlistCount, setWishlistCount] = useState<number>(0);

  useEffect(() => {
    checkAuth();
  }, []);

  useEffect(() => {
    if (user) {
      updateCounts();
    } else {
      setCartCount(0);
      setWishlistCount(0);
    }
  }, [user]);

  const checkAuth = async () => {
    const token = localStorage.getItem('sk_token');
    if (!token) return;
    try {
      const data = await authService.getMe();
      setUser({
        id: data.id,
        email: data.email,
        firstName: data.firstName,
        lastName: data.lastName,
        mobile: data.mobile,
        roles: Array.from(data.roles || [])
      });
    } catch (e) {
      localStorage.removeItem('sk_token');
      setUser(null);
    }
  };

  const updateCounts = async () => {
    try {
      const [c, w] = await Promise.all([
        cartService.getCart().catch(() => null),
        wishlistService.getWishlist().catch(() => [])
      ]);
      if (c) setCartCount(c.itemCount);
      if (w) setWishlistCount(w.length);
    } catch (e) {
      console.error('Failed to update counts', e);
    }
  };

  const handleLogout = () => {
    localStorage.removeItem('sk_token');
    setUser(null);
    setCurrentTab('catalog');
  };

  const handleLoginSuccess = (u: User) => {
    setUser(u);
    setCurrentTab('catalog');
  };

  return (
    <div style={{ minHeight: '100vh', backgroundColor: '#F8F7F3', fontFamily: 'system-ui, -apple-system, sans-serif' }}>
      {showSplash && <SplashScreen onComplete={() => setShowSplash(false)} />}

      <Navbar
        user={user}
        currentTab={currentTab}
        setCurrentTab={setCurrentTab}
        cartCount={cartCount}
        wishlistCount={wishlistCount}
        searchQuery={searchQuery}
        setSearchQuery={setSearchQuery}
        onLogout={handleLogout}
      />

      <main>
        {currentTab === 'catalog' && (
          <CustomerCatalog
            user={user}
            searchQuery={searchQuery}
            onCartUpdated={updateCounts}
            onWishlistUpdated={updateCounts}
          />
        )}

        {currentTab === 'auth' && (
          <AuthPage user={user} onLoginSuccess={handleLoginSuccess} />
        )}

        {currentTab === 'cart' && (
          <CartCheckoutPage
            onCartUpdated={updateCounts}
            onOrderPlaced={() => {
              updateCounts();
            }}
          />
        )}

        {currentTab === 'orders' && <CustomerOrdersPage />}
        {currentTab === 'wishlist' && (
          <CustomerCatalog
            user={user}
            searchQuery={searchQuery}
            onCartUpdated={updateCounts}
            onWishlistUpdated={updateCounts}
          />
        )}

        {currentTab === 'seller' && <SellerDashboardPage />}
        {currentTab === 'admin' && <AdminDashboardPage />}
        {currentTab === 'support' && <SupportDashboardPage user={user} />}
        {currentTab === 'download-app' && <DownloadAppPage />}
      </main>
    </div>
  );
}

export default App;
