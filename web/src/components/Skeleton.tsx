import React from 'react';

export const ProductSkeleton: React.FC = () => {
  return (
    <div
      style={{
        backgroundColor: '#ffffff',
        borderRadius: '12px',
        padding: '16px',
        boxShadow: '0 2px 10px rgba(0,0,0,0.04)',
        display: 'flex',
        flexDirection: 'column',
        gap: '12px'
      }}
    >
      <div className="skeleton-shimmer" style={{ width: '100%', height: '140px', borderRadius: '8px' }} />
      <div className="skeleton-shimmer" style={{ width: '40%', height: '12px' }} />
      <div className="skeleton-shimmer" style={{ width: '85%', height: '16px' }} />
      <div className="skeleton-shimmer" style={{ width: '50%', height: '20px' }} />
      <div className="skeleton-shimmer" style={{ width: '100%', height: '40px', borderRadius: '8px' }} />
    </div>
  );
};

export const CartSkeleton: React.FC = () => {
  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
      {[1, 2, 3].map((i) => (
        <div key={i} className="skeleton-shimmer" style={{ width: '100%', height: '70px', borderRadius: '8px' }} />
      ))}
    </div>
  );
};
