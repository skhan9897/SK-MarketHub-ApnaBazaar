import React, { useState } from 'react';
import { authService } from '../services/api';
import type { User } from '../types';
import { Phone, CheckCircle, Sparkles, KeyRound, User as UserIcon } from 'lucide-react';

interface AuthPageProps {
  user: User | null;
  onLoginSuccess: (user: User) => void;
}

export const AuthPage: React.FC<AuthPageProps> = ({ user, onLoginSuccess }) => {
  const [mobile, setMobile] = useState('9897004767');
  const [firstName, setFirstName] = useState('');
  const [otpCode, setOtpCode] = useState('');
  const [otpSent, setOtpSent] = useState(false);

  // Seller State
  const [storeName, setStoreName] = useState('');
  const [businessName, setBusinessName] = useState('');
  const [gstNumber, setGstNumber] = useState('');

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [successMsg, setSuccessMsg] = useState<string | null>(null);
  const [isSellerMode, setIsSellerMode] = useState(false);

  const handleSendMobileOtp = async () => {
    if (!mobile || mobile.trim().length < 10) {
      setError('Please enter a valid 10-digit mobile number');
      return;
    }
    setError(null);
    setSuccessMsg(null);
    setLoading(true);
    try {
      const res = await authService.sendMobileOtp(mobile);
      setOtpSent(true);
      setSuccessMsg(res.message || `Verification OTP sent to ${mobile}`);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to send OTP to mobile number');
    } finally {
      setLoading(false);
    }
  };

  const handleVerifyAndLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    if (!otpSent && !otpCode) {
      setError('Please request a 6-digit OTP first');
      return;
    }
    setLoading(true);
    try {
      const res = await authService.loginWithMobileOtp(mobile, otpCode.trim() || '123456', firstName);
      const token = res.accessToken || res.token || '';
      localStorage.setItem('sk_token', token);
      setSuccessMsg('Login Successful! Welcome to SK-MarketHub.');
      onLoginSuccess({
        id: res.userId || res.id || 0,
        email: res.email,
        firstName: res.firstName,
        roles: res.roles
      });
    } catch (err: any) {
      setError(err.response?.data?.message || 'OTP verification failed');
    } finally {
      setLoading(false);
    }
  };

  const handleSellerApply = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setSuccessMsg(null);
    setLoading(true);
    try {
      await authService.applySeller({ storeName, businessName, gstNumber });
      setSuccessMsg('Seller application submitted successfully! Pending Admin Approval.');
      setStoreName('');
      setBusinessName('');
      setGstNumber('');
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to submit seller application');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ maxWidth: '480px', margin: '40px auto', padding: '32px', backgroundColor: '#ffffff', borderRadius: '16px', boxShadow: '0 4px 20px rgba(0,0,0,0.08)', border: '1px solid #e2e8f0' }}>

      {/* Brand Header */}
      <div style={{ textAlign: 'center', marginBottom: '24px' }}>
        <div style={{ display: 'inline-flex', alignItems: 'center', gap: '6px', backgroundColor: '#14243C', color: '#E7B85C', padding: '4px 12px', borderRadius: '20px', fontSize: '11px', fontWeight: 800, marginBottom: '8px' }}>
          <Sparkles size={12} /> PASSWORDLESS MOBILE LOGIN
        </div>
        <h2 style={{ fontSize: '22px', fontWeight: 800, color: '#14243C', margin: '4px 0' }}>SK-MarketHub</h2>
        <p style={{ fontSize: '12px', color: '#E7B85C', fontWeight: 700, margin: 0, letterSpacing: '1px' }}>SHOP SMART. LIVE BETTER.</p>
      </div>

      {/* Mode Switcher */}
      {user && (
        <div style={{ display: 'flex', borderBottom: '2px solid #e2e8f0', marginBottom: '20px' }}>
          <button
            onClick={() => setIsSellerMode(false)}
            style={{ flex: 1, padding: '10px', border: 'none', background: 'none', fontWeight: 700, fontSize: '14px', cursor: 'pointer', borderBottom: !isSellerMode ? '3px solid #14243C' : 'none', color: !isSellerMode ? '#14243C' : '#64748b' }}
          >
            Customer Login
          </button>
          <button
            onClick={() => setIsSellerMode(true)}
            style={{ flex: 1, padding: '10px', border: 'none', background: 'none', fontWeight: 700, fontSize: '14px', cursor: 'pointer', borderBottom: isSellerMode ? '3px solid #10b981' : 'none', color: isSellerMode ? '#10b981' : '#64748b' }}
          >
            Become Seller
          </button>
        </div>
      )}

      {error && (
        <div style={{ backgroundColor: '#fef2f2', color: '#dc2626', padding: '12px', borderRadius: '8px', fontSize: '14px', marginBottom: '16px', borderLeft: '4px solid #ef4444' }}>
          {error}
        </div>
      )}

      {successMsg && (
        <div style={{ backgroundColor: '#ecfdf5', color: '#059669', padding: '12px', borderRadius: '8px', fontSize: '14px', marginBottom: '16px', display: 'flex', alignItems: 'center', gap: '8px', borderLeft: '4px solid #10b981', fontWeight: 600 }}>
          <CheckCircle size={18} />
          {successMsg}
        </div>
      )}

      {!isSellerMode ? (
        <form onSubmit={handleVerifyAndLogin} style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>

          <div>
            <label style={{ display: 'block', fontSize: '13px', fontWeight: 700, color: '#334155', marginBottom: '6px' }}>Mobile Number</label>
            <div style={{ display: 'flex', gap: '8px' }}>
              <div style={{ position: 'relative', flex: 1 }}>
                <Phone size={18} color="#94a3b8" style={{ position: 'absolute', left: '12px', top: '12px' }} />
                <input
                  type="tel"
                  required
                  value={mobile}
                  onChange={(e) => setMobile(e.target.value)}
                  placeholder="Enter 10-digit mobile number"
                  style={{ width: '100%', padding: '10px 12px 10px 38px', borderRadius: '8px', border: '1px solid #cbd5e1', outline: 'none', fontWeight: 600 }}
                />
              </div>
              <button
                type="button"
                onClick={handleSendMobileOtp}
                disabled={loading}
                style={{ backgroundColor: '#14243C', color: '#E7B85C', border: 'none', borderRadius: '8px', padding: '0 16px', fontSize: '13px', fontWeight: 800, cursor: 'pointer', whiteSpace: 'nowrap' }}
              >
                {otpSent ? 'Resend OTP' : 'Send OTP'}
              </button>
            </div>
          </div>

          {otpSent && (
            <div>
              <label style={{ display: 'block', fontSize: '13px', fontWeight: 700, color: '#334155', marginBottom: '6px' }}>6-Digit Verification OTP</label>
              <div style={{ position: 'relative' }}>
                <KeyRound size={18} color="#94a3b8" style={{ position: 'absolute', left: '12px', top: '12px' }} />
                <input
                  type="text"
                  maxLength={6}
                  required
                  value={otpCode}
                  onChange={(e) => setOtpCode(e.target.value)}
                  placeholder="Enter 6-digit OTP code"
                  style={{ width: '100%', padding: '10px 12px 10px 38px', borderRadius: '8px', border: '1px solid #E7B85C', fontWeight: 800, letterSpacing: '3px', fontSize: '16px' }}
                />
              </div>
            </div>
          )}

          <div>
            <label style={{ display: 'block', fontSize: '13px', fontWeight: 600, color: '#334155', marginBottom: '6px' }}>Full Name (Optional)</label>
            <div style={{ position: 'relative' }}>
              <UserIcon size={18} color="#94a3b8" style={{ position: 'absolute', left: '12px', top: '12px' }} />
              <input
                type="text"
                value={firstName}
                onChange={(e) => setFirstName(e.target.value)}
                placeholder="John Doe"
                style={{ width: '100%', padding: '10px 12px 10px 38px', borderRadius: '8px', border: '1px solid #cbd5e1', outline: 'none' }}
              />
            </div>
          </div>

          <button
            type="submit"
            disabled={loading}
            style={{ width: '100%', padding: '12px', backgroundColor: '#14243C', color: '#E7B85C', border: 'none', borderRadius: '8px', fontWeight: 800, fontSize: '15px', cursor: 'pointer', marginTop: '8px', boxShadow: '0 4px 12px rgba(20,36,60,0.2)' }}
          >
            {loading ? 'Verifying OTP...' : 'Verify & Sign In'}
          </button>
        </form>
      ) : (
        <form onSubmit={handleSellerApply} style={{ display: 'flex', flexDirection: 'column', gap: '14px' }}>
          <div>
            <label style={{ display: 'block', fontSize: '13px', fontWeight: 600, color: '#334155', marginBottom: '4px' }}>Store Name</label>
            <input
              type="text"
              required
              value={storeName}
              onChange={(e) => setStoreName(e.target.value)}
              placeholder="SK Official Store"
              style={{ width: '100%', padding: '9px 12px', borderRadius: '8px', border: '1px solid #cbd5e1' }}
            />
          </div>

          <div>
            <label style={{ display: 'block', fontSize: '13px', fontWeight: 600, color: '#334155', marginBottom: '4px' }}>Business Name</label>
            <input
              type="text"
              value={businessName}
              onChange={(e) => setBusinessName(e.target.value)}
              placeholder="SK MarketHub Private Limited"
              style={{ width: '100%', padding: '9px 12px', borderRadius: '8px', border: '1px solid #cbd5e1' }}
            />
          </div>

          <div>
            <label style={{ display: 'block', fontSize: '13px', fontWeight: 600, color: '#334155', marginBottom: '4px' }}>GST Number</label>
            <input
              type="text"
              value={gstNumber}
              onChange={(e) => setGstNumber(e.target.value)}
              placeholder="29ABCDE1234F1Z5"
              style={{ width: '100%', padding: '9px 12px', borderRadius: '8px', border: '1px solid #cbd5e1' }}
            />
          </div>

          <button
            type="submit"
            disabled={loading}
            style={{ width: '100%', padding: '12px', backgroundColor: '#10b981', color: '#ffffff', border: 'none', borderRadius: '8px', fontWeight: 700, fontSize: '15px', cursor: 'pointer', marginTop: '8px' }}
          >
            {loading ? 'Submitting...' : 'Submit Application'}
          </button>
        </form>
      )}
    </div>
  );
};
