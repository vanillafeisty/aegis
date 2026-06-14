import axios from 'axios';

const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8000';

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Credentials API
export const credentialsAPI = {
  set: (credentials: any) => apiClient.post('/credentials/set', credentials),
  getStatus: () => apiClient.get('/credentials/status'),
};

// Agents API
export const agentsAPI = {
  post: (content: string) => apiClient.post('/agents/post', { content }),
  connect: (profileUrl: string) => apiClient.post('/agents/connect', { profile_url: profileUrl }),
  inbox: () => apiClient.post('/agents/inbox'),
  profile: () => apiClient.post('/agents/profile'),
  email: (recipientEmail: string, subject: string, body: string) =>
    apiClient.post('/agents/email', {
      recipient_email: recipientEmail,
      subject,
      body,
    }),
};

// Health check
export const healthAPI = {
  check: () => apiClient.get('/health'),
};

export default apiClient;
