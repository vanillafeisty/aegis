'use client';

import { useState, useRef, useEffect } from 'react';
import { agentsAPI } from '../lib/api';
import './ChatInterface.css';

type CredentialStatus = {
  linkedin_connected: boolean;
  openai_connected: boolean;
  gmail_connected: boolean;
  all_configured: boolean;
};

type Message = {
  id: string;
  role: 'user' | 'agent';
  content: string;
  timestamp: Date;
  action?: string;
  status?: 'sending' | 'success' | 'error';
};

interface ChatInterfaceProps {
  credentialStatus: CredentialStatus;
}

export default function ChatInterface({ credentialStatus }: ChatInterfaceProps) {
  const [messages, setMessages] = useState<Message[]>([
    {
      id: '1',
      role: 'agent',
      content: '🛡️ Welcome to Aegis! I\'m your AI-powered LinkedIn automation agent. I can help you:\n\n✓ Post content to your feed\n✓ Send connection requests\n✓ Reply to inbox messages\n✓ Optimize your profile\n✓ Send emails\n\nWhat would you like to do?',
      timestamp: new Date(),
    },
  ]);
  const [input, setInput] = useState('');
  const [loading, setLoading] = useState(false);
  const messagesEndRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  const addMessage = (role: 'user' | 'agent', content: string, action?: string) => {
    const newMessage: Message = {
      id: Date.now().toString(),
      role,
      content,
      timestamp: new Date(),
      action,
    };
    setMessages((prev) => [...prev, newMessage]);
  };

  const handleSend = async () => {
    if (!input.trim() || loading) return;

    addMessage('user', input);
    const userInput = input;
    setInput('');
    setLoading(true);

    try {
      const lowerInput = userInput.toLowerCase();

      if (lowerInput.includes('post') || lowerInput.includes('publish')) {
        await agentsAPI.post(userInput);
        addMessage(
          'agent',
          '📝 Posting to your LinkedIn feed...\n\nYour post is queued and will be published within moments. I\'ll watch the browser and publish when ready! ✨',
          'post'
        );
      } else if (lowerInput.includes('connect') || lowerInput.includes('request')) {
        const urlMatch = userInput.match(/https:\/\/[^\s]+/);
        if (urlMatch) {
          await agentsAPI.connect(urlMatch[0]);
          addMessage(
            'agent',
            `🤝 Connection request queued for:\n${urlMatch[0]}\n\nI\'m sending this now. The request will appear in their inbox shortly! 🚀`,
            'connect'
          );
        } else {
          addMessage(
            'agent',
            '❌ Please provide a LinkedIn profile URL (starting with https://)\n\nExample: "Connect to https://linkedin.com/in/username"'
          );
        }
      } else if (lowerInput.includes('message') || lowerInput.includes('inbox') || lowerInput.includes('reply')) {
        await agentsAPI.inbox();
        addMessage(
          'agent',
          '📬 Starting inbox sweep...\n\nI\'ll scan your unread messages and reply intelligently to the most recent ones. This keeps your conversations flowing naturally! 💬',
          'inbox'
        );
      } else if (lowerInput.includes('profile') || lowerInput.includes('optimize') || lowerInput.includes('headline')) {
        await agentsAPI.profile();
        addMessage(
          'agent',
          '⚙️ Profile optimization started...\n\nI\'m analyzing your current headline and about section, then using AI to craft more compelling versions. Stand by! ✨',
          'profile'
        );
      } else if (lowerInput.includes('email') || lowerInput.includes('send email')) {
        const emailMatch = userInput.match(/[\w\.-]+@[\w\.-]+\.\w+/);
        if (emailMatch) {
          addMessage(
            'agent',
            '📧 To send an email, please provide:\n1. Recipient email\n2. Subject\n3. Message body\n\nExample: "Send email to recipient@example.com, subject: Hello, body: This is my message"'
          );
        } else {
          addMessage(
            'agent',
            '📧 Email sending feature ready!\n\nFormat: "Send email to [recipient@email.com], subject: [Your Subject], body: [Your message]"'
          );
        }
      } else {
        addMessage(
          'agent',
          '🤔 I understood you want to do something, but I need clarification.\n\nTry one of these commands:\n• "Post: [your content]"\n• "Connect to [LinkedIn URL]"\n• "Check messages"\n• "Optimize my profile"\n• "Send email"'
        );
      }
    } catch (error: any) {
      addMessage(
        'agent',
        `❌ Error: ${error.response?.data?.detail || error.message}\n\nMake sure your credentials are configured correctly.`
      );
    } finally {
      setLoading(false);
    }
  };

  const handleQuickAction = (action: string) => {
    setInput(action);
  };

  return (
    <div className="chat-interface">
      <header className="chat-header">
        <div className="header-content">
          <div className="header-logo">🛡️</div>
          <div className="header-info">
            <h1>Aegis AI Agent</h1>
            <p>LinkedIn Automation Powered by AI</p>
          </div>
        </div>
        <div className="header-status">
          <div className="status-indicator linkedin-status">
            {credentialStatus.linkedin_connected ? '✓ LinkedIn' : '○ LinkedIn'}
          </div>
          <div className="status-indicator gmail-status">
            {credentialStatus.gmail_connected ? '✓ Gmail' : '○ Gmail'}
          </div>
          <div className="status-indicator ai-status">
            {credentialStatus.openai_connected ? '✓ AI' : '○ AI'}
          </div>
        </div>
      </header>

      <div className="chat-messages">
        {messages.map((msg) => (
          <div
            key={msg.id}
            className={`message ${msg.role} ${msg.status ? msg.status : ''}`}
          >
            <div className="message-avatar">
              {msg.role === 'user' ? '👤' : '🛡️'}
            </div>
            <div className="message-content">
              <div className="message-text">{msg.content}</div>
              <span className="message-time">
                {msg.timestamp.toLocaleTimeString([], {
                  hour: '2-digit',
                  minute: '2-digit',
                })}
              </span>
            </div>
          </div>
        ))}
        <div ref={messagesEndRef} />
      </div>

      <div className="chat-quick-actions">
        <div className="quick-actions-label">Quick Actions</div>
        <div className="quick-actions-grid">
          <button
            className="quick-action-btn"
            onClick={() => handleQuickAction('Post: Check out my latest project!')}
          >
            📝 Post
          </button>
          <button
            className="quick-action-btn"
            onClick={() => handleQuickAction('Connect to https://linkedin.com/in/')}
          >
            🤝 Connect
          </button>
          <button
            className="quick-action-btn"
            onClick={() => handleQuickAction('Check my messages')}
          >
            💬 Messages
          </button>
          <button
            className="quick-action-btn"
            onClick={() => handleQuickAction('Optimize my profile')}
          >
            ⚙️ Profile
          </button>
        </div>
      </div>

      <div className="chat-input-area">
        <form
          onSubmit={(e) => {
            e.preventDefault();
            handleSend();
          }}
          className="chat-input-form"
        >
          <input
            type="text"
            value={input}
            onChange={(e) => setInput(e.target.value)}
            placeholder="Tell Aegis what you want to do... (e.g., 'Post something' or 'Check messages')"
            disabled={loading}
            className="chat-input"
          />
          <button
            type="submit"
            disabled={loading || !input.trim()}
            className="send-button"
          >
            {loading ? '⏳' : '→'}
          </button>
        </form>
        <div className="input-helper">
          💡 Try: "Post about my latest project" or "Send me a connection request to [URL]"
        </div>
      </div>
    </div>
  );
}
