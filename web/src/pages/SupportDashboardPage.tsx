import { useEffect, useState } from 'react';
import { supportService } from '../services/api';
import type { Ticket, User } from '../types';
import { LifeBuoy, Send } from 'lucide-react';

interface SupportDashboardPageProps {
  user: User | null;
}

export const SupportDashboardPage: React.FC<SupportDashboardPageProps> = ({ user }) => {
  const [tickets, setTickets] = useState<Ticket[]>([]);
  const [selectedTicket, setSelectedTicket] = useState<Ticket | null>(null);

  // New ticket state
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [subject, setSubject] = useState('');
  const [message, setMessage] = useState('');

  // Reply state
  const [replyText, setReplyText] = useState('');

  const [loading, setLoading] = useState(false);

  useEffect(() => {
    loadTickets();
  }, []);

  const loadTickets = async () => {
    setLoading(true);
    try {
      const data = await supportService.getTickets();
      setTickets(data);
      if (data.length > 0 && !selectedTicket) {
        setSelectedTicket(data[0]);
      }
    } catch (e) {
      console.error('Failed to load support tickets', e);
    } finally {
      setLoading(false);
    }
  };

  const handleCreateTicket = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!subject.trim() || !message.trim()) return;
    try {
      const newTck = await supportService.createTicket(subject, message);
      setTickets([newTck, ...tickets]);
      setSelectedTicket(newTck);
      setShowCreateModal(false);
      setSubject('');
      setMessage('');
      alert('Support Ticket Created!');
    } catch (e) {
      alert('Failed to create ticket');
    }
  };

  const handleReplySubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedTicket || !replyText.trim()) return;
    try {
      const updated = await supportService.replyTicket(selectedTicket.ticketId, replyText);
      setSelectedTicket(updated);
      setTickets(tickets.map((t) => t.ticketId === updated.ticketId ? updated : t));
      setReplyText('');
    } catch (e) {
      alert('Failed to send reply');
    }
  };

  if (loading) return <div style={{ textAlign: 'center', padding: '60px' }}>Loading Support Portal...</div>;

  return (
    <div style={{ maxWidth: '1100px', margin: '24px auto', padding: '0 16px', display: 'flex', gap: '24px', height: 'calc(100vh - 140px)' }}>

      {/* Left List */}
      <div style={{ width: '340px', backgroundColor: '#ffffff', borderRadius: '12px', padding: '16px', boxShadow: '0 2px 8px rgba(0,0,0,0.04)', display: 'flex', flexDirection: 'column' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px', borderBottom: '1px solid #f1f5f9', paddingBottom: '12px' }}>
          <h3 style={{ fontSize: '18px', fontWeight: 700, margin: 0, display: 'flex', alignItems: 'center', gap: '8px' }}>
            <LifeBuoy size={20} color="#8b5cf6" /> Tickets ({tickets.length})
          </h3>
          <button onClick={() => setShowCreateModal(true)} style={{ backgroundColor: '#8b5cf6', color: '#fff', border: 'none', padding: '6px 12px', borderRadius: '6px', cursor: 'pointer', fontSize: '13px', fontWeight: 600 }}>
            New Ticket
          </button>
        </div>

        <div style={{ flex: 1, overflowY: 'auto', display: 'flex', flexDirection: 'column', gap: '8px' }}>
          {tickets.length === 0 ? (
            <div style={{ color: '#94a3b8', fontSize: '13px', textAlign: 'center', padding: '20px' }}>No support tickets created yet.</div>
          ) : (
            tickets.map((t) => (
              <div
                key={t.ticketId}
                onClick={() => setSelectedTicket(t)}
                style={{
                  padding: '12px',
                  borderRadius: '8px',
                  backgroundColor: selectedTicket?.ticketId === t.ticketId ? '#f3e8ff' : '#f8fafc',
                  border: selectedTicket?.ticketId === t.ticketId ? '1px solid #c084fc' : '1px solid #e2e8f0',
                  cursor: 'pointer'
                }}
              >
                <div style={{ fontWeight: 600, fontSize: '14px', color: '#0f172a' }}>{t.subject}</div>
                <div style={{ fontSize: '12px', color: '#64748b' }}>{t.ticketId} | Status: <span style={{ color: '#7c3aed', fontWeight: 600 }}>{t.status}</span></div>
              </div>
            ))
          )}
        </div>
      </div>

      {/* Right Details / Reply Panel */}
      <div style={{ flex: 1, backgroundColor: '#ffffff', borderRadius: '12px', padding: '24px', boxShadow: '0 2px 8px rgba(0,0,0,0.04)', display: 'flex', flexDirection: 'column' }}>
        {selectedTicket ? (
          <>
            <div style={{ borderBottom: '1px solid #f1f5f9', paddingBottom: '16px', marginBottom: '16px' }}>
              <div style={{ fontSize: '12px', color: '#64748b' }}>TICKET ID: {selectedTicket.ticketId}</div>
              <h2 style={{ fontSize: '20px', fontWeight: 700, margin: '4px 0 8px 0', color: '#0f172a' }}>{selectedTicket.subject}</h2>
              <div style={{ fontSize: '13px', color: '#475569', backgroundColor: '#f8fafc', padding: '12px', borderRadius: '8px' }}>
                <strong>{selectedTicket.userEmail}:</strong> {selectedTicket.message}
              </div>
            </div>

            {/* Replies list */}
            <div style={{ flex: 1, overflowY: 'auto', display: 'flex', flexDirection: 'column', gap: '12px', marginBottom: '16px' }}>
              {selectedTicket.replies?.map((r, idx) => (
                <div key={idx} style={{ backgroundColor: r.sender === user?.email ? '#eff6ff' : '#f3e8ff', padding: '10px 14px', borderRadius: '8px', alignSelf: r.sender === user?.email ? 'flex-end' : 'flex-start', maxWidth: '80%' }}>
                  <div style={{ fontSize: '11px', fontWeight: 600, color: '#64748b', marginBottom: '2px' }}>{r.sender}</div>
                  <div style={{ fontSize: '14px', color: '#1e293b' }}>{r.reply}</div>
                </div>
              ))}
            </div>

            {/* Reply Input */}
            <form onSubmit={handleReplySubmit} style={{ display: 'flex', gap: '8px' }}>
              <input
                type="text"
                placeholder="Write your response..."
                value={replyText}
                onChange={(e) => setReplyText(e.target.value)}
                style={{ flex: 1, padding: '10px 14px', borderRadius: '8px', border: '1px solid #cbd5e1', outline: 'none' }}
              />
              <button type="submit" style={{ padding: '10px 18px', backgroundColor: '#8b5cf6', color: '#fff', border: 'none', borderRadius: '8px', cursor: 'pointer', fontWeight: 600, display: 'flex', alignItems: 'center', gap: '6px' }}>
                <Send size={16} /> Send
              </button>
            </form>
          </>
        ) : (
          <div style={{ textAlign: 'center', margin: 'auto', color: '#94a3b8' }}>Select a ticket from the left to view communication history.</div>
        )}
      </div>

      {/* Create Ticket Modal */}
      {showCreateModal && (
        <div style={{ position: 'fixed', top: 0, left: 0, right: 0, bottom: 0, backgroundColor: 'rgba(0,0,0,0.5)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
          <form onSubmit={handleCreateTicket} style={{ backgroundColor: '#fff', padding: '24px', borderRadius: '12px', width: '450px', display: 'flex', flexDirection: 'column', gap: '12px' }}>
            <h3 style={{ margin: 0 }}>Create Customer Support Ticket</h3>
            <input required placeholder="Subject (e.g. Order Delivery Query)" value={subject} onChange={(e) => setSubject(e.target.value)} style={{ padding: '9px', borderRadius: '6px', border: '1px solid #cbd5e1' }} />
            <textarea required rows={4} placeholder="Describe your issue or request..." value={message} onChange={(e) => setMessage(e.target.value)} style={{ padding: '9px', borderRadius: '6px', border: '1px solid #cbd5e1' }} />
            <div style={{ display: 'flex', gap: '10px', justifyContent: 'flex-end', marginTop: '8px' }}>
              <button type="button" onClick={() => setShowCreateModal(false)} style={{ padding: '8px 14px', border: 'none', background: '#e2e8f0', borderRadius: '6px', cursor: 'pointer' }}>Cancel</button>
              <button type="submit" style={{ padding: '8px 14px', border: 'none', background: '#8b5cf6', color: '#fff', borderRadius: '6px', cursor: 'pointer', fontWeight: 600 }}>Create Ticket</button>
            </div>
          </form>
        </div>
      )}

    </div>
  );
};
