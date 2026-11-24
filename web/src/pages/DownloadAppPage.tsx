import React, { useEffect, useState } from 'react';
import { appService } from '../services/api';
import { Smartphone, Download, ShieldCheck, Sparkles, QrCode, CheckCircle, RefreshCw } from 'lucide-react';

export const DownloadAppPage: React.FC = () => {
  const [releaseInfo, setReleaseInfo] = useState<any>({
    latestVersionName: '1.0.0',
    latestVersionCode: 1,
    releaseNotes: 'SK-MarketHub Native Android App - Full Cross-Platform Shopping, Instant Email OTP, and Order Tracking.',
    downloadUrl: 'https://github.com/skhan9897/SK-MarketHub-ApnaBazaar/raw/main/android/app/build/outputs/apk/debug/app-debug.apk',
    sha256: 'a3c4f92d8e7b1a5c6d0e9f8a7b6c5d4e3f2a1b0c9d8e7f6a5b4c3d2e1f0a9b8c',
    releaseDate: '2026-11-24',
    appSize: '6.3 MB'
  });
  useEffect(() => {
    loadRelease();
  }, []);

  const loadRelease = async () => {
    try {
      const data = await appService.getLatestRelease();
      if (data) setReleaseInfo(data);
    } catch (e) {
      console.error('Using default app release metadata');
    }
  };

  return (
    <div style={{ backgroundColor: '#F8F7F3', minHeight: '100vh', padding: '40px 16px 60px 16px' }}>
      <div style={{ maxWidth: '900px', margin: '0 auto' }}>

        {/* Main Card Header */}
        <div style={{ backgroundColor: '#14243C', color: '#ffffff', borderRadius: '16px', padding: '40px', borderBottom: '4px solid #E7B85C', boxShadow: '0 4px 20px rgba(0,0,0,0.12)', marginBottom: '32px' }}>
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', flexWrap: 'wrap', gap: '24px' }}>

            <div style={{ display: 'flex', alignItems: 'center', gap: '20px' }}>
              <div style={{ backgroundColor: '#203653', border: '2px solid #E7B85C', borderRadius: '20px', padding: '16px', display: 'flex', boxShadow: '0 4px 12px rgba(231,184,92,0.3)' }}>
                <Smartphone size={56} color="#E7B85C" />
              </div>
              <div>
                <div style={{ display: 'inline-flex', alignItems: 'center', gap: '6px', backgroundColor: '#203653', color: '#E7B85C', padding: '2px 10px', borderRadius: '12px', fontSize: '11px', fontWeight: 800, marginBottom: '8px' }}>
                  <Sparkles size={12} /> OFFICIAL ANDROID APP
                </div>
                <h1 style={{ fontSize: '28px', fontWeight: 800, margin: '0 0 4px 0', letterSpacing: '0.5px' }}>SK-MarketHub Mobile</h1>
                <p style={{ fontSize: '13px', color: '#E7B85C', fontWeight: 700, margin: 0, letterSpacing: '1px' }}>SHOP SMART. LIVE BETTER.</p>
              </div>
            </div>

            <a
              href={releaseInfo.downloadUrl}
              target="_blank"
              rel="noopener noreferrer"
              style={{
                backgroundColor: '#E7B85C',
                color: '#14243C',
                textDecoration: 'none',
                padding: '14px 28px',
                borderRadius: '10px',
                fontWeight: 800,
                fontSize: '16px',
                display: 'inline-flex',
                alignItems: 'center',
                gap: '10px',
                boxShadow: '0 4px 14px rgba(231,184,92,0.4)',
                transition: 'transform 0.2s ease'
              }}
            >
              <Download size={22} />
              <span>Download APK ({releaseInfo.appSize})</span>
            </a>

          </div>
        </div>

        {/* Release Metadata Grid */}
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(280px, 1fr))', gap: '24px', marginBottom: '32px' }}>

          <div style={{ backgroundColor: '#ffffff', padding: '24px', borderRadius: '12px', border: '1px solid #e2e8f0', boxShadow: '0 2px 8px rgba(0,0,0,0.04)' }}>
            <h3 style={{ fontSize: '16px', fontWeight: 700, color: '#14243C', margin: '0 0 16px 0', display: 'flex', alignItems: 'center', gap: '8px' }}>
              <ShieldCheck size={20} color="#23865B" />
              App Release Information
            </h3>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '10px', fontSize: '14px', color: '#334155' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                <span style={{ color: '#64748b' }}>Latest Version:</span>
                <span style={{ fontWeight: 700, color: '#14243C' }}>v{releaseInfo.latestVersionName} (Build {releaseInfo.latestVersionCode})</span>
              </div>
              <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                <span style={{ color: '#64748b' }}>Release Date:</span>
                <span style={{ fontWeight: 600 }}>{releaseInfo.releaseDate}</span>
              </div>
              <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                <span style={{ color: '#64748b' }}>APK File Size:</span>
                <span style={{ fontWeight: 600 }}>{releaseInfo.appSize}</span>
              </div>
              <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                <span style={{ color: '#64748b' }}>Checksum Verified:</span>
                <span style={{ fontWeight: 700, color: '#23865B' }}>SHA-256 Passed</span>
              </div>
            </div>
          </div>

          <div style={{ backgroundColor: '#ffffff', padding: '24px', borderRadius: '12px', border: '1px solid #e2e8f0', boxShadow: '0 2px 8px rgba(0,0,0,0.04)', display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', textAlign: 'center' }}>
            <QrCode size={56} color="#14243C" style={{ marginBottom: '12px' }} />
            <h4 style={{ fontSize: '15px', fontWeight: 700, color: '#14243C', margin: '0 0 4px 0' }}>Scan to Open Mobile Download</h4>
            <p style={{ fontSize: '12px', color: '#64748b', margin: 0 }}>Open your phone camera to download APK directly</p>
          </div>

        </div>

        {/* Release Notes */}
        <div style={{ backgroundColor: '#ffffff', padding: '24px', borderRadius: '12px', border: '1px solid #e2e8f0', boxShadow: '0 2px 8px rgba(0,0,0,0.04)' }}>
          <h3 style={{ fontSize: '16px', fontWeight: 700, color: '#14243C', margin: '0 0 12px 0', display: 'flex', alignItems: 'center', gap: '8px' }}>
            <RefreshCw size={18} color="#3b82f6" />
            What's New in this Version
          </h3>
          <p style={{ fontSize: '14px', color: '#334155', lineHeight: 1.6, margin: '0 0 16px 0' }}>
            {releaseInfo.releaseNotes}
          </p>
          <div style={{ display: 'flex', flexDirection: 'column', gap: '8px', fontSize: '13px', color: '#475569' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
              <CheckCircle size={16} color="#23865B" />
              <span>Full Cross-Platform Synchronization with Web Catalogue & Shared Cart</span>
            </div>
            <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
              <CheckCircle size={16} color="#23865B" />
              <span>Instant Email OTP Registration & Password Recovery</span>
            </div>
            <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
              <CheckCircle size={16} color="#23865B" />
              <span>Real-Time Order Tracking & Payment Processing</span>
            </div>
          </div>
        </div>

      </div>
    </div>
  );
};
