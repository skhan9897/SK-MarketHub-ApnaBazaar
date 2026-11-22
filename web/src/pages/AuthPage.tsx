import { useState } from 'react';
import { authService } from '../services/api';
import type { User } from '../types';
import { Lock, Mail, CheckCircle, KeyRound } from 'lucide-react';

interface AuthPageProps {
  user: User | null;
  onLoginSuccess: (user: User) => void;
}

export const AuthPage: React.FC<AuthPageProps> = ({ user, onLoginSuccess }) => {
  const [mode, setMode] = useState<'login' | 'register' | 'forgot' | 'reset' | 'seller'>('login');

  // Login State
  const [loginEmail, setLoginEmail] = useState('');
  const [loginPassword, setLoginPassword] = useState('');

  // Register & OTP State
  const [regEmail, setRegEmail] = useState('');
  const [regPassword, setRegPassword] = useState('');
  const [regFirstName, setRegFirstName] = useState('');
  const [regLastName, setRegLastName] = useState('');
  const [regMobile, setRegMobile] = useState('');
  const [otpCode, setOtpCode] = useState('');
  const [otpSent, setOtpSent] = useState(false);
  const [otpVerified, setOtpVerified] = useState(false);

  // Forgot / Reset Password State
  const [recoveryEmail, setRecoveryEmail] = useState('');
  const [newPassword, setNewPassword] = useState('');

  // Seller State
  const [storeName, setStoreName] = useState('');
  const [businessName, setBusinessName] = useState('');
  const [gstNumber, setGstNumber] = useState('');

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [successMsg, setSuccessMsg] = useState<string | null>(null);

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setLoading(true);
    try {
      const res = await authService.login(loginEmail, loginPassword);
      const token = res.accessToken || res.token || '';
      localStorage.setItem('sk_token', token);
      onLoginSuccess({
        id: res.userId || res.id || 0,
        email: res.email,
        firstName: res.firstName,
        roles: res.roles
      });
    } catch (err: any) {
      setError(err.response?.data?.message || 'Invalid email or password');
    } finally {
      setLoading(false);
    }
  };

  const handleSendOtp = async () => {
    if (!regEmail || !regEmail.includes('@')) {
      setError('Please enter a valid email address first');
      return;
    }
    setError(null);
    setLoading(true);
    try {
      await authService.sendOtp(regEmail);
      setOtpSent(true);
      setSuccessMsg(`Verification code sent to ${regEmail}`);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to send OTP');
    } finally {
      setLoading(false);
    }
  };

  const handleRegister = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    if (regPassword.length < 8) {
      setError('Password must be at least 8 characters long');
      return;
    }
    if (!otpVerified && !otpSent) {
      setError('Please request and verify your Email OTP code first');
      return;
    }
    setLoading(true);
    try {
      if (!otpVerified) {
        await authService.verifyOtp(regEmail, otpCode);
        setOtpVerified(true);
      }
      const res = await authService.register({
        email: regEmail,
        password: regPassword,
        firstName: regFirstName,
        lastName: regLastName,
        mobile: regMobile
      });
      const token = res.accessToken || res.token || '';
      localStorage.setItem('sk_token', token);
      onLoginSuccess({
        id: res.userId || res.id || 0,
        email: res.email,
        firstName: res.firstName,
        roles: res.roles
      });
    } catch (err: any) {
      setError(err.response?.data?.message || 'Registration or OTP verification failed');
    } finally {
      setLoading(false);
    }
  };

  const handleForgotPassword = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setSuccessMsg(null);
    setLoading(true);
    try {
      const res = await authService.forgotPassword(recoveryEmail);
      setSuccessMsg(res.message || 'Password recovery initiated.');
      setMode('reset');
    } catch (err: any) {
      setError(err.response?.data?.message || 'User email not found');
    } finally {
      setLoading(false);
    }
  };

  const handleResetPassword = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setSuccessMsg(null);
    if (newPassword.length < 8) {
      setError('New password must be at least 8 characters long');
      return;
    }
    setLoading(true);
    try {
      const res = await authService.resetPassword(recoveryEmail, newPassword);
      setSuccessMsg(res.message || 'Password reset successfully! Please log in.');
      setMode('login');
      setLoginEmail(recoveryEmail);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Password reset failed');
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
    <div style={{ maxWidth: '480px', margin: '40px auto', padding: '32px', backgroundColor: '#ffffff', borderRadius: '12px', boxShadow: '0 4px 20px rgba(0,0,0,0.08)' }}>

      {/* Tabs */}
      <div style={{ display: 'flex', borderBottom: '2px solid #e2e8f0', marginBottom: '24px', flexWrap: 'wrap' }}>
        <button
          onClick={() => { setMode('login'); setError(null); setSuccessMsg(null); }}
          style={{
            flex: 1,
            padding: '10px',
            border: 'none',
            background: 'none',
            fontWeight: 600,
            fontSize: '14px',
            cursor: 'pointer',
            borderBottom: mode === 'login' ? '3px solid #14243C' : 'none',
            color: mode === 'login' ? '#14243C' : '#64748b'
          }}
        >
          Sign In
        </button>
        <button
          onClick={() => { setMode('register'); setError(null); setSuccessMsg(null); }}
          style={{
            flex: 1,
            padding: '10px',
            border: 'none',
            background: 'none',
            fontWeight: 600,
            fontSize: '14px',
            cursor: 'pointer',
            borderBottom: mode === 'register' ? '3px solid #14243C' : 'none',
            color: mode === 'register' ? '#14243C' : '#64748b'
          }}
        >
          Register
        </button>
        <button
          onClick={() => { setMode('forgot'); setError(null); setSuccessMsg(null); }}
          style={{
            flex: 1,
            padding: '10px',
            border: 'none',
            background: 'none',
            fontWeight: 600,
            fontSize: '14px',
            cursor: 'pointer',
            borderBottom: (mode === 'forgot' || mode === 'reset') ? '3px solid #E7B85C' : 'none',
            color: (mode === 'forgot' || mode === 'reset') ? '#14243C' : '#64748b'
          }}
        >
          Forgot Pass
        </button>
        {user && (
          <button
            onClick={() => { setMode('seller'); setError(null); setSuccessMsg(null); }}
            style={{
              flex: 1,
              padding: '10px',
              border: 'none',
              background: 'none',
              fontWeight: 600,
              fontSize: '14px',
              cursor: 'pointer',
              borderBottom: mode === 'seller' ? '3px solid #10b981' : 'none',
              color: mode === 'seller' ? '#10b981' : '#64748b'
            }}
          >
            Seller
          </button>
        )}
      </div>

      {error && (
        <div style={{ backgroundColor: '#fef2f2', color: '#dc2626', padding: '12px', borderRadius: '8px', fontSize: '14px', marginBottom: '16px' }}>
          {error}
        </div>
      )}

      {successMsg && (
        <div style={{ backgroundColor: '#ecfdf5', color: '#059669', padding: '12px', borderRadius: '8px', fontSize: '14px', marginBottom: '16px', display: 'flex', alignItems: 'center', gap: '8px' }}>
          <CheckCircle size={18} />
          {successMsg}
        </div>
      )}

      {mode === 'login' && (
        <form onSubmit={handleLogin} style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
          <div>
            <label style={{ display: 'block', fontSize: '13px', fontWeight: 600, color: '#334155', marginBottom: '6px' }}>Email Address</label>
            <div style={{ position: 'relative' }}>
              <Mail size={18} color="#94a3b8" style={{ position: 'absolute', left: '12px', top: '12px' }} />
              <input
                type="email"
                required
                value={loginEmail}
                onChange={(e) => setLoginEmail(e.target.value)}
                placeholder="customer@skmarkethub.com"
                style={{ width: '100%', padding: '10px 12px 10px 38px', borderRadius: '8px', border: '1px solid #cbd5e1', outline: 'none' }}
              />
            </div>
          </div>

          <div>
            <label style={{ display: 'block', fontSize: '13px', fontWeight: 600, color: '#334155', marginBottom: '6px' }}>Password</label>
            <div style={{ position: 'relative' }}>
              <Lock size={18} color="#94a3b8" style={{ position: 'absolute', left: '12px', top: '12px' }} />
              <input
                type="password"
                required
                value={loginPassword}
                onChange={(e) => setLoginPassword(e.target.value)}
                placeholder="••••••••"
                style={{ width: '100%', padding: '10px 12px 10px 38px', borderRadius: '8px', border: '1px solid #cbd5e1', outline: 'none' }}
              />
            </div>
          </div>

          <button
            type="submit"
            disabled={loading}
            style={{ width: '100%', padding: '12px', backgroundColor: '#14243C', color: '#E7B85C', border: 'none', borderRadius: '8px', fontWeight: 700, fontSize: '15px', cursor: 'pointer', marginTop: '8px' }}
          >
            {loading ? 'Authenticating...' : 'Sign In'}
          </button>
        </form>
      )}

      {mode === 'register' && (
        <form onSubmit={handleRegister} style={{ display: 'flex', flexDirection: 'column', gap: '14px' }}>
          <div>
            <label style={{ display: 'block', fontSize: '13px', fontWeight: 600, color: '#334155', marginBottom: '4px' }}>First Name</label>
            <input
              type="text"
              required
              value={regFirstName}
              onChange={(e) => setRegFirstName(e.target.value)}
              placeholder="John"
              style={{ width: '100%', padding: '9px 12px', borderRadius: '8px', border: '1px solid #cbd5e1' }}
            />
          </div>

          <div>
            <label style={{ display: 'block', fontSize: '13px', fontWeight: 600, color: '#334155', marginBottom: '4px' }}>Last Name</label>
            <input
              type="text"
              value={regLastName}
              onChange={(e) => setRegLastName(e.target.value)}
              placeholder="Doe"
              style={{ width: '100%', padding: '9px 12px', borderRadius: '8px', border: '1px solid #cbd5e1' }}
            />
          </div>

          <div>
            <label style={{ display: 'block', fontSize: '13px', fontWeight: 600, color: '#334155', marginBottom: '4px' }}>Email</label>
            <div style={{ display: 'flex', gap: '8px' }}>
              <input
                type="email"
                required
                value={regEmail}
                onChange={(e) => setRegEmail(e.target.value)}
                placeholder="john@example.com"
                style={{ flex: 1, padding: '9px 12px', borderRadius: '8px', border: '1px solid #cbd5e1' }}
              />
              <button
                type="button"
                onClick={handleSendOtp}
                disabled={loading}
                style={{ backgroundColor: '#14243C', color: '#E7B85C', border: 'none', borderRadius: '8px', padding: '0 12px', fontSize: '12px', fontWeight: 700, cursor: 'pointer' }}
              >
                {otpSent ? 'Resend OTP' : 'Send OTP'}
              </button>
            </div>
          </div>

          {otpSent && (
            <div>
              <label style={{ display: 'block', fontSize: '13px', fontWeight: 600, color: '#334155', marginBottom: '4px' }}>6-Digit Verification OTP</label>
              <input
                type="text"
                maxLength={6}
                value={otpCode}
                onChange={(e) => setOtpCode(e.target.value)}
                placeholder="Enter 6-digit OTP code"
                style={{ width: '100%', padding: '9px 12px', borderRadius: '8px', border: '1px solid #E7B85C', fontWeight: 700, letterSpacing: '2px' }}
              />
            </div>
          )}

          <div>
            <label style={{ display: 'block', fontSize: '13px', fontWeight: 600, color: '#334155', marginBottom: '4px' }}>Mobile Number</label>
            <input
              type="tel"
              value={regMobile}
              onChange={(e) => setRegMobile(e.target.value)}
              placeholder="9876543210"
              style={{ width: '100%', padding: '9px 12px', borderRadius: '8px', border: '1px solid #cbd5e1' }}
            />
          </div>

          <div>
            <label style={{ display: 'block', fontSize: '13px', fontWeight: 600, color: '#334155', marginBottom: '4px' }}>Password (Min 8 Chars)</label>
            <input
              type="password"
              required
              value={regPassword}
              onChange={(e) => setRegPassword(e.target.value)}
              placeholder="At least 8 characters"
              style={{ width: '100%', padding: '9px 12px', borderRadius: '8px', border: '1px solid #cbd5e1' }}
            />
          </div>

          <button
            type="submit"
            disabled={loading}
            style={{ width: '100%', padding: '12px', backgroundColor: '#3b82f6', color: '#ffffff', border: 'none', borderRadius: '8px', fontWeight: 600, fontSize: '15px', cursor: 'pointer', marginTop: '8px' }}
          >
            {loading ? 'Registering...' : 'Create Account'}
          </button>
        </form>
      )}

      {mode === 'forgot' && (
        <form onSubmit={handleForgotPassword} style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
          <div>
            <label style={{ display: 'block', fontSize: '13px', fontWeight: 600, color: '#334155', marginBottom: '6px' }}>Registered Email Address</label>
            <div style={{ position: 'relative' }}>
              <Mail size={18} color="#94a3b8" style={{ position: 'absolute', left: '12px', top: '12px' }} />
              <input
                type="email"
                required
                value={recoveryEmail}
                onChange={(e) => setRecoveryEmail(e.target.value)}
                placeholder="customer@skmarkethub.com"
                style={{ width: '100%', padding: '10px 12px 10px 38px', borderRadius: '8px', border: '1px solid #cbd5e1', outline: 'none' }}
              />
            </div>
          </div>

          <button
            type="submit"
            disabled={loading}
            style={{ width: '100%', padding: '12px', backgroundColor: '#f59e0b', color: '#ffffff', border: 'none', borderRadius: '8px', fontWeight: 600, fontSize: '15px', cursor: 'pointer' }}
          >
            {loading ? 'Requesting...' : 'Request Password Reset'}
          </button>
        </form>
      )}

      {mode === 'reset' && (
        <form onSubmit={handleResetPassword} style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
          <div>
            <label style={{ display: 'block', fontSize: '13px', fontWeight: 600, color: '#334155', marginBottom: '6px' }}>Email</label>
            <input
              type="email"
              required
              readOnly
              value={recoveryEmail}
              style={{ width: '100%', padding: '10px 12px', borderRadius: '8px', border: '1px solid #cbd5e1', backgroundColor: '#f1f5f9' }}
            />
          </div>

          <div>
            <label style={{ display: 'block', fontSize: '13px', fontWeight: 600, color: '#334155', marginBottom: '6px' }}>New Password (Min 8 Chars)</label>
            <div style={{ position: 'relative' }}>
              <KeyRound size={18} color="#94a3b8" style={{ position: 'absolute', left: '12px', top: '12px' }} />
              <input
                type="password"
                required
                value={newPassword}
                onChange={(e) => setNewPassword(e.target.value)}
                placeholder="Enter new 8+ char password"
                style={{ width: '100%', padding: '10px 12px 10px 38px', borderRadius: '8px', border: '1px solid #cbd5e1', outline: 'none' }}
              />
            </div>
          </div>

          <button
            type="submit"
            disabled={loading}
            style={{ width: '100%', padding: '12px', backgroundColor: '#10b981', color: '#ffffff', border: 'none', borderRadius: '8px', fontWeight: 600, fontSize: '15px', cursor: 'pointer' }}
          >
            {loading ? 'Updating Password...' : 'Reset Password'}
          </button>
        </form>
      )}

      {mode === 'seller' && user && (
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
            style={{ width: '100%', padding: '12px', backgroundColor: '#10b981', color: '#ffffff', border: 'none', borderRadius: '8px', fontWeight: 600, fontSize: '15px', cursor: 'pointer', marginTop: '8px' }}
          >
            {loading ? 'Submitting...' : 'Submit Application'}
          </button>
        </form>
      )}
    </div>
  );
};
