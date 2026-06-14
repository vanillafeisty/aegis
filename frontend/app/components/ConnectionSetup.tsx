'use client';

import { useState } from 'react';
import { credentialsAPI } from '../lib/api';
import './ConnectionSetup.css';

interface ConnectionSetupProps {
  onCredentialsUpdated: () => void;
}

export default function ConnectionSetup({ onCredentialsUpdated }: ConnectionSetupProps) {
  const [step, setStep] = useState<'linkedin' | 'gmail' | 'openai' | 'review'>('linkedin');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState(false);

  const [credentials, setCredentials] = useState({
    linkedin_session_cookie: '',
    linkedin_access_token: '',
    linkedin_client_id: '',
    openai_api_key: '',
    smtp_email: '',
    smtp_password: '',
  });

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setCredentials((prev) => ({
      ...prev,
      [name]: value,
    }));
    setError(null);
  };

  const validateStep = (): boolean => {
    if (step === 'linkedin') {
      if (!credentials.linkedin_session_cookie || !credentials.linkedin_access_token) {
        setError('LinkedIn Session Cookie and Access Token are required');
        return false;
      }
    } else if (step === 'gmail') {
      if (!credentials.smtp_email || !credentials.smtp_password) {
        setError('Gmail Email and App Password are required');
        return false;
      }
    } else if (step === 'openai') {
      if (!credentials.openai_api_key) {
        setError('OpenAI API Key is required');
        return false;
      }
    }
    return true;
  };

  const handleNext = () => {
    if (!validateStep()) return;

    if (step === 'linkedin') {
      setStep('gmail');
    } else if (step === 'gmail') {
      setStep('openai');
    } else if (step === 'openai') {
      setStep('review');
    }
  };

  const handlePrevious = () => {
    if (step === 'gmail') {
      setStep('linkedin');
    } else if (step === 'openai') {
      setStep('gmail');
    } else if (step === 'review') {
      setStep('openai');
    }
  };

  const handleSubmit = async () => {
    setLoading(true);
    setError(null);

    try {
      await credentialsAPI.set(credentials);
      setSuccess(true);
      setTimeout(() => {
        onCredentialsUpdated();
      }, 1500);
    } catch (err: any) {
      setError(err.response?.data?.detail || 'Failed to save credentials');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="connection-setup">
      <div className="setup-container">
        <div className="setup-header">
          <div className="logo">🛡️</div>
          <h1>Aegis Setup</h1>
          <p>Connect your LinkedIn and Gmail accounts to enable AI automation</p>
        </div>

        <div className="setup-progress">
          <div className={`progress-step ${step === 'linkedin' ? 'active' : step !== 'gmail' && step !== 'openai' && step !== 'review' ? '' : 'completed'}`}>
            <div className="step-number">1</div>
            <div className="step-label">LinkedIn</div>
          </div>
          <div className="progress-line"></div>
          <div className={`progress-step ${step === 'gmail' ? 'active' : step === 'openai' || step === 'review' ? 'completed' : ''}`}>
            <div className="step-number">2</div>
            <div className="step-label">Gmail</div>
          </div>
          <div className="progress-line"></div>
          <div className={`progress-step ${step === 'openai' ? 'active' : step === 'review' ? 'completed' : ''}`}>
            <div className="step-number">3</div>
            <div className="step-label">OpenAI</div>
          </div>
          <div className="progress-line"></div>
          <div className={`progress-step ${step === 'review' ? 'active' : ''}`}>
            <div className="step-number">4</div>
            <div className="step-label">Review</div>
          </div>
        </div>

        {success ? (
          <div className="success-message">
            <div className="success-icon">✓</div>
            <h2>All Set!</h2>
            <p>Your credentials have been saved. Launching Aegis...</p>
          </div>
        ) : (
          <>
            {step === 'linkedin' && (
              <div className="setup-step fade-in">
                <h2>LinkedIn Authentication</h2>
                <p className="step-description">
                  To automate LinkedIn actions, we need your session credentials. These are kept secure and only used locally.
                </p>

                <div className="form-group">
                  <label>LinkedIn Session Cookie (li_at)</label>
                  <textarea
                    name="linkedin_session_cookie"
                    value={credentials.linkedin_session_cookie}
                    onChange={handleInputChange}
                    placeholder="Your LinkedIn session cookie..."
                    rows={3}
                  />
                  <small>Find this in: DevTools → Application → Cookies → linkedin.com</small>
                </div>

                <div className="form-group">
                  <label>LinkedIn Access Token</label>
                  <textarea
                    name="linkedin_access_token"
                    value={credentials.linkedin_access_token}
                    onChange={handleInputChange}
                    placeholder="Your LinkedIn access token..."
                    rows={2}
                  />
                </div>

                <div className="form-group">
                  <label>LinkedIn Client ID</label>
                  <input
                    type="text"
                    name="linkedin_client_id"
                    value={credentials.linkedin_client_id}
                    onChange={handleInputChange}
                    placeholder="Your LinkedIn client ID..."
                  />
                </div>
              </div>
            )}

            {step === 'gmail' && (
              <div className="setup-step fade-in">
                <h2>Gmail Configuration</h2>
                <p className="step-description">
                  Enable Gmail access for sending emails and notifications.
                </p>

                <div className="form-group">
                  <label>Gmail Email Address</label>
                  <input
                    type="email"
                    name="smtp_email"
                    value={credentials.smtp_email}
                    onChange={handleInputChange}
                    placeholder="your.email@gmail.com"
                  />
                </div>

                <div className="form-group">
                  <label>Gmail App Password</label>
                  <input
                    type="password"
                    name="smtp_password"
                    value={credentials.smtp_password}
                    onChange={handleInputChange}
                    placeholder="Your Gmail App Password"
                  />
                  <small>
                    Generate one at: <a href="https://myaccount.google.com/apppasswords" target="_blank">Google App Passwords</a>
                  </small>
                </div>
              </div>
            )}

            {step === 'openai' && (
              <div className="setup-step fade-in">
                <h2>OpenAI Integration</h2>
                <p className="step-description">
                  OpenAI powers the intelligent replies and profile optimization features.
                </p>

                <div className="form-group">
                  <label>OpenAI API Key</label>
                  <textarea
                    name="openai_api_key"
                    value={credentials.openai_api_key}
                    onChange={handleInputChange}
                    placeholder="sk-..."
                    rows={2}
                  />
                  <small>
                    Get your key from: <a href="https://platform.openai.com/api-keys" target="_blank">OpenAI API Keys</a>
                  </small>
                </div>
              </div>
            )}

            {step === 'review' && (
              <div className="setup-step fade-in">
                <h2>Review Your Setup</h2>
                <p className="step-description">
                  Review your configuration before activating Aegis.
                </p>

                <div className="review-list">
                  <div className="review-item">
                    <div className="review-icon">✓</div>
                    <div>
                      <strong>LinkedIn Connected</strong>
                      <p>Session cookie and access token configured</p>
                    </div>
                  </div>
                  <div className="review-item">
                    <div className="review-icon">✓</div>
                    <div>
                      <strong>Gmail Connected</strong>
                      <p>{credentials.smtp_email}</p>
                    </div>
                  </div>
                  <div className="review-item">
                    <div className="review-icon">✓</div>
                    <div>
                      <strong>OpenAI Integration Ready</strong>
                      <p>AI features enabled</p>
                    </div>
                  </div>
                </div>
              </div>
            )}

            {error && <div className="error-message">{error}</div>}

            <div className="setup-actions">
              {step !== 'linkedin' && (
                <button
                  className="btn btn-secondary"
                  onClick={handlePrevious}
                  disabled={loading}
                >
                  Back
                </button>
              )}

              {step !== 'review' ? (
                <button
                  className="btn btn-primary"
                  onClick={handleNext}
                  disabled={loading}
                >
                  Next
                </button>
              ) : (
                <button
                  className="btn btn-primary"
                  onClick={handleSubmit}
                  disabled={loading}
                >
                  {loading ? 'Setting up...' : 'Activate Aegis'}
                </button>
              )}
            </div>
          </>
        )}
      </div>
    </div>
  );
}
