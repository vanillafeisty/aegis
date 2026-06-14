'use client';

import { useState, useEffect } from 'react';
import { credentialsAPI } from './lib/api';
import ConnectionSetup from './components/ConnectionSetup';
import ChatInterface from './components/ChatInterface';
import './styles/page.css';

type CredentialStatus = {
  linkedin_connected: boolean;
  openai_connected: boolean;
  gmail_connected: boolean;
  all_configured: boolean;
};

export default function Home() {
  const [credentialStatus, setCredentialStatus] = useState<CredentialStatus | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    checkCredentials();
  }, []);

  const checkCredentials = async () => {
    try {
      const response = await credentialsAPI.getStatus();
      setCredentialStatus(response.data);
    } catch (error) {
      console.error('Failed to check credentials:', error);
      setCredentialStatus({
        linkedin_connected: false,
        openai_connected: false,
        gmail_connected: false,
        all_configured: false,
      });
    } finally {
      setLoading(false);
    }
  };

  const handleCredentialsUpdated = () => {
    checkCredentials();
  };

  if (loading) {
    return (
      <div className="loading-screen">
        <div className="loading-content">
          <div className="logo-placeholder">🛡️</div>
          <h1>Aegis</h1>
          <p>Initializing AI Agent Platform...</p>
          <div className="spinner"></div>
        </div>
      </div>
    );
  }

  return (
    <main className="main-container">
      {!credentialStatus?.all_configured ? (
        <ConnectionSetup onCredentialsUpdated={handleCredentialsUpdated} />
      ) : (
        <ChatInterface credentialStatus={credentialStatus} />
      )}
    </main>
  );
}
