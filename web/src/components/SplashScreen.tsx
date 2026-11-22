import React, { useEffect, useState } from 'react';
import { ShoppingBag, Sparkles } from 'lucide-react';

interface SplashScreenProps {
  onComplete: () => void;
  statusMessage?: string;
}

export const SplashScreen: React.FC<SplashScreenProps> = ({ onComplete, statusMessage }) => {
  const [fading, setFading] = useState(false);
  const [currentMessage, setCurrentMessage] = useState('Initializing SK-MarketHub...');

  const messages = [
    'Initializing SK-MarketHub...',
    'Connecting securely...',
    'Preparing your shopping experience...',
    'Loading marketplace...'
  ];

  useEffect(() => {
    let msgIdx = 0;
    const interval = setInterval(() => {
      msgIdx = (msgIdx + 1) % messages.length;
      setCurrentMessage(messages[msgIdx]);
    }, 1200);

    // Safety timeout: transition after 3.5 seconds max or when complete
    const timer = setTimeout(() => {
      setFading(true);
      setTimeout(() => {
        onComplete();
      }, 500); // 500ms fade duration
    }, 3500);

    return () => {
      clearInterval(interval);
      clearTimeout(timer);
    };
  }, []);

  return (
    <div
      style={{
        position: 'fixed',
        top: 0,
        left: 0,
        right: 0,
        bottom: 0,
        backgroundColor: '#080B14',
        zIndex: 9999,
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        justifyContent: 'center',
        color: '#FFFFFF',
        opacity: fading ? 0 : 1,
        transition: 'opacity 0.5s ease-out',
        pointerEvents: fading ? 'none' : 'auto'
      }}
    >
      {/* Background Soft Radial Gold Aura */}
      <div
        style={{
          position: 'absolute',
          width: '350px',
          height: '350px',
          borderRadius: '50%',
          background: 'radial-gradient(circle, rgba(212, 175, 55, 0.15) 0%, rgba(8, 11, 20, 0) 70%)',
          pointerEvents: 'none'
        }}
      />

      {/* Emblem Box */}
      <div
        style={{
          position: 'relative',
          backgroundColor: '#101B35',
          border: '2px solid #D4AF37',
          borderRadius: '20px',
          padding: '24px',
          boxShadow: '0 0 30px rgba(212, 175, 55, 0.3)',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          marginBottom: '24px',
          animation: 'pulse 2s infinite'
        }}
      >
        <ShoppingBag size={56} color="#E7B85C" />
        <div
          style={{
            position: 'absolute',
            bottom: '18px',
            fontSize: '11px',
            fontWeight: 800,
            color: '#080B14',
            backgroundColor: '#E7B85C',
            padding: '1px 6px',
            borderRadius: '4px',
            letterSpacing: '1px'
          }}
        >
          SK
        </div>
      </div>

      {/* Brand Title */}
      <h1
        style={{
          fontSize: '32px',
          fontWeight: 800,
          margin: '0 0 6px 0',
          color: '#E7B85C',
          letterSpacing: '1px',
          fontFamily: 'system-ui, sans-serif'
        }}
      >
        SK-MarketHub
      </h1>

      {/* Brand Tagline */}
      <div
        style={{
          fontSize: '13px',
          fontWeight: 700,
          color: '#F5D994',
          letterSpacing: '2px',
          marginBottom: '40px',
          display: 'flex',
          alignItems: 'center',
          gap: '6px'
        }}
      >
        <Sparkles size={14} color="#D4AF37" />
        <span>SHOP SMART. LIVE BETTER.</span>
        <Sparkles size={14} color="#D4AF37" />
      </div>

      {/* Metallic Gold Circular Progress Ring */}
      <div style={{ position: 'relative', width: '48px', height: '48px', marginBottom: '16px' }}>
        <div
          style={{
            width: '100%',
            height: '100%',
            borderRadius: '50%',
            border: '3px solid #172A49',
            borderTopColor: '#D4AF37',
            borderRightColor: '#E7B85C',
            animation: 'spin 1s linear infinite'
          }}
        />
      </div>

      {/* Loading Status Text */}
      <div style={{ fontSize: '13px', color: '#B9C2D1', letterSpacing: '0.5px' }}>
        {statusMessage || currentMessage}
      </div>

      <style>{`
        @keyframes spin {
          0% { transform: rotate(0deg); }
          100% { transform: rotate(360deg); }
        }
      `}</style>
    </div>
  );
};
