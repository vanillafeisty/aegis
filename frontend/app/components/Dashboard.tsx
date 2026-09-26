'use client';

import { useState, useEffect, useRef } from 'react';
import axios from 'axios';
import AegisLogo from './AegisLogo';
import {
  Shield,
  Zap,
  Mail,
  Send,
  Users,
  Terminal,
  Activity,
  CheckCircle2,
  AlertCircle,
  RefreshCw,
  Sparkles,
  ExternalLink,
  Sliders,
  FileText,
  Flame,
  Check,
  Copy,
  Layers,
  ArrowRight,
  TrendingUp,
  Cpu,
  Globe,
  Radio,
  Play,
  HelpCircle,
  MessageSquare,
  UserCheck,
  AlertTriangle,
  Clock,
  ChevronRight,
  KeyRound,
  Image as ImageIcon
} from 'lucide-react';

function LinkedInIcon({ className = "w-4 h-4" }: { className?: string }) {
  return (
    <svg className={className} viewBox="0 0 24 24" fill="currentColor" aria-hidden="true">
      <path d="M19 3a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h14m-.5 15.5v-5.3a3.26 3.26 0 0 0-3.26-3.26c-.85 0-1.84.52-2.28 1.3v-1.11h-2.79v8.37h2.79v-4.93c0-.77.62-1.4 1.39-1.4a1.4 1.4 0 0 1 1.4 1.4v4.93h2.75M6.88 8.56a1.68 1.68 0 0 0 1.68-1.68c0-.93-.75-1.69-1.68-1.69a1.69 1.69 0 0 0-1.69 1.69c0 .93.76 1.68 1.69 1.68m1.39 9.94v-8.37H5.5v8.37h2.77z" />
    </svg>
  );
}

const API_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8000';

interface LogItem {
  id: string;
  time: string;
  type: 'info' | 'success' | 'warning' | 'error';
  message: string;
}

export default function Home() {
  const [activeTab, setActiveTab] = useState<'dashboard' | 'linkedin' | 'outreach' | 'email' | 'zapier' | 'logs'>('dashboard');
  const [healthStatus, setHealthStatus] = useState<any>(null);
  const [credentialStatus, setCredentialStatus] = useState<any>(null);
  const [loading, setLoading] = useState(false);
  const [loadingAction, setLoadingAction] = useState<string | null>(null);
  const [notification, setNotification] = useState<{ type: 'success' | 'error' | 'warning' | 'info'; text: string } | null>(null);

  // Form states
  const [postContent, setPostContent] = useState('');
  const [postImageUrl, setPostImageUrl] = useState('');
  const [trendingTopic, setTrendingTopic] = useState('');
  const [postTone, setPostTone] = useState<'thought_leadership' | 'technical' | 'story' | 'contrarian'>('thought_leadership');
  const [aiGeneratingPost, setAiGeneratingPost] = useState(false);

  const [connectUrl, setConnectUrl] = useState('');
  const [connectNote, setConnectNote] = useState('');

  const [dmName, setDmName] = useState('');
  const [dmMessage, setDmMessage] = useState('');

  const [coldJob, setColdJob] = useState('Senior Full Stack Engineer');
  const [coldArea, setColdArea] = useState('San Francisco, CA');
  const [coldLimit, setColdLimit] = useState(3);
  const [coldCustomMessage, setColdCustomMessage] = useState('');
  const [outreachResults, setOutreachResults] = useState<any[]>([]);

  const [emailTo, setEmailTo] = useState('');
  const [emailSubject, setEmailSubject] = useState('');
  const [emailBody, setEmailBody] = useState('');
  const [emailTemplate, setEmailTemplate] = useState('custom');

  const [workflowId, setWorkflowId] = useState('agent_sync_pipeline');
  const [workflowData, setWorkflowData] = useState('{\n  "lead_source": "linkedin_campaign",\n  "status": "qualified",\n  "enrich": true\n}');
  const [mcpStreamLogs, setMcpStreamLogs] = useState<any[]>([]);
  const [mcpStreamProgress, setMcpStreamProgress] = useState(0);
  const [isStreamingMcp, setIsStreamingMcp] = useState(false);

  // Cookie Validation State
  const [cookieValidation, setCookieValidation] = useState<any>(null);
  const [cookieChecking, setCookieChecking] = useState(false);
  const [showCookieModal, setShowCookieModal] = useState(false);
  const [copiedCookieStep, setCopiedCookieStep] = useState(false);

  // System Logs State
  const [logs, setLogs] = useState<LogItem[]>([]);
  const logsEndRef = useRef<HTMLDivElement>(null);

  const addLog = (type: 'info' | 'success' | 'warning' | 'error', message: string) => {
    const newLog: LogItem = {
      id: Math.random().toString(36).substring(2, 9),
      time: new Date().toLocaleTimeString(),
      type,
      message,
    };
    setLogs((prev) => [...prev.slice(-99), newLog]);
  };

  const showToast = (type: 'success' | 'error' | 'warning' | 'info', text: string) => {
    setNotification({ type, text });
    addLog(type, text);
    setTimeout(() => {
      setNotification((curr) => (curr?.text === text ? null : curr));
    }, 6000);
  };

  useEffect(() => {
    addLog('info', 'Aegis AI v2.0 dashboard initialized');
    checkHealth();
    checkCredentials();
    const interval = setInterval(() => {
      checkHealth(true);
    }, 20000);
    return () => clearInterval(interval);
  }, []);

  const checkHealth = async (silent = false) => {
    try {
      const response = await axios.get(`${API_URL}/health`);
      setHealthStatus(response.data);
      if (!silent) addLog('success', 'Backend health check: Online');
    } catch (error: any) {
      if (!silent) {
        showToast('error', 'Backend server unreachable at ' + API_URL);
      }
      setHealthStatus({ status: 'unreachable', version: '2.0.0' });
    }
  };

  const checkCredentials = async () => {
    try {
      const response = await axios.get(`${API_URL}/auth/status`);
      setCredentialStatus(response.data);
    } catch (error) {
      console.error('Credential check failed');
    }
  };

  const validateLinkedInCookie = async () => {
    setCookieChecking(true);
    setCookieValidation(null);
    addLog('info', 'Testing LinkedIn session cookie validity...');
    try {
      const response = await axios.get(`${API_URL}/auth/validate-cookie`);
      setCookieValidation(response.data);
      if (response.data?.valid) {
        showToast('success', 'LinkedIn session cookie is valid and active!');
      } else {
        showToast('warning', 'LinkedIn cookie expired or invalid. Please refresh li_at.');
      }
    } catch (error: any) {
      setCookieValidation({ valid: false, message: error.message });
      showToast('error', 'Failed to validate cookie: ' + error.message);
    } finally {
      setCookieChecking(false);
    }
  };

  const handlePostToLinkedIn = async () => {
    if (!postContent.trim()) {
      showToast('warning', 'Please enter post content');
      return;
    }
    setLoading(true);
    setLoadingAction('post');
    addLog('info', 'Publishing update to LinkedIn...');
    try {
      const response = await axios.post(`${API_URL}/agents/post`, {
        content: postContent,
        image_url: postImageUrl.trim() || null,
      });
      const result = response.data?.result || response.data;
      if (result?.status === 'cookie_expired') {
        showToast('warning', 'LinkedIn session cookie expired. Check Dashboard to refresh.');
      } else {
        showToast('success', 'Post published successfully to LinkedIn!');
        setPostContent('');
        setPostImageUrl('');
      }
    } catch (error: any) {
      showToast('error', `Failed to post: ${error.response?.data?.detail || error.message}`);
    } finally {
      setLoading(false);
      setLoadingAction(null);
    }
  };

  const handleGenerateAiPost = async () => {
    if (!trendingTopic.trim()) {
      showToast('warning', 'Enter a topic to generate content');
      return;
    }
    setAiGeneratingPost(true);
    addLog('info', `Generating high-engagement post on topic: "${trendingTopic}"`);
    try {
      const response = await axios.post(`${API_URL}/agents/post-trending`, {
        topic: `${trendingTopic} (Tone: ${postTone})`,
        image_url: postImageUrl.trim() || null,
      });
      if (response.data?.generated_content) {
        setPostContent(response.data.generated_content);
        showToast('success', 'AI generated & published post successfully!');
      } else {
        showToast('success', 'AI trending post published successfully!');
      }
    } catch (error: any) {
      showToast('error', `AI Generation Error: ${error.response?.data?.detail || error.message}`);
    } finally {
      setAiGeneratingPost(false);
    }
  };

  const handleSendConnection = async () => {
    if (!connectUrl.trim()) {
      showToast('warning', 'Please enter a LinkedIn profile URL');
      return;
    }
    setLoading(true);
    setLoadingAction('connect');
    addLog('info', `Dispatching connection request to: ${connectUrl}`);
    try {
      await axios.post(`${API_URL}/agents/connect`, {
        profile_url: connectUrl,
        message: connectNote.trim() || null,
      });
      showToast('success', 'Connection invitation sent successfully!');
      setConnectUrl('');
      setConnectNote('');
    } catch (error: any) {
      showToast('error', `Connection failed: ${error.response?.data?.detail || error.message}`);
    } finally {
      setLoading(false);
      setLoadingAction(null);
    }
  };

  const handleSendDm = async () => {
    if (!dmName.trim() || !dmMessage.trim()) {
      showToast('warning', 'Please enter recipient name and message');
      return;
    }
    setLoading(true);
    setLoadingAction('dm');
    addLog('info', `Sending LinkedIn direct message to: ${dmName}`);
    try {
      await axios.post(`${API_URL}/agents/send-dm`, {
        name: dmName,
        message: dmMessage,
      });
      showToast('success', `Direct message sent to ${dmName}!`);
      setDmName('');
      setDmMessage('');
    } catch (error: any) {
      showToast('error', `Failed to send DM: ${error.response?.data?.detail || error.message}`);
    } finally {
      setLoading(false);
      setLoadingAction(null);
    }
  };

  const handleColdOutreach = async () => {
    if (!coldJob.trim() || !coldArea.trim()) {
      showToast('warning', 'Please specify target job role and location');
      return;
    }
    setLoading(true);
    setLoadingAction('outreach');
    addLog('info', `Starting recruiter outreach campaign: "${coldJob}" in "${coldArea}" (Limit: ${coldLimit})`);
    try {
      const response = await axios.post(`${API_URL}/agents/cold-message`, {
        job_description: coldJob,
        area: coldArea,
        custom_message: coldCustomMessage.trim() || null,
        limit: coldLimit,
      });
      const resData = response.data?.result || response.data;
      if (resData?.status === 'cookie_expired') {
        showToast('warning', 'LinkedIn cookie expired. Please validate cookie on Dashboard.');
      } else if (resData?.status === 'success' || resData?.status === 'simulated') {
        const results = resData.results || [];
        setOutreachResults(results);
        const count = results.filter((r: any) => r.status === 'success').length;
        showToast('success', `Campaign complete! Contacted ${count} recruiters successfully.`);
      } else {
        showToast('error', `Campaign error: ${resData?.message || 'Unknown error'}`);
      }
    } catch (error: any) {
      showToast('error', `Outreach failed: ${error.response?.data?.detail || error.message}`);
    } finally {
      setLoading(false);
      setLoadingAction(null);
    }
  };

  const handleSendEmail = async () => {
    if (!emailTo.trim() || !emailSubject.trim() || !emailBody.trim()) {
      showToast('warning', 'Please fill in all email fields');
      return;
    }
    setLoading(true);
    setLoadingAction('email');
    addLog('info', `Sending email to: ${emailTo} via SMTP`);
    try {
      await axios.post(`${API_URL}/agents/email`, {
        recipient_email: emailTo,
        subject: emailSubject,
        body: emailBody,
      });
      showToast('success', 'Email dispatched successfully!');
      setEmailTo('');
      setEmailSubject('');
      setEmailBody('');
    } catch (error: any) {
      showToast('error', `Email failed: ${error.response?.data?.detail || error.message}`);
    } finally {
      setLoading(false);
      setLoadingAction(null);
    }
  };

  const handleOptimizeProfile = async () => {
    setLoading(true);
    setLoadingAction('optimize');
    addLog('info', 'Running Claude AI profile enhancement scan...');
    try {
      const response = await axios.post(`${API_URL}/agents/profile`);
      showToast('success', 'Profile optimization analysis complete! Check logs for details.');
      addLog('success', 'Profile optimization recommendations received');
    } catch (error: any) {
      showToast('error', `Profile optimization failed: ${error.response?.data?.detail || error.message}`);
    } finally {
      setLoading(false);
      setLoadingAction(null);
    }
  };

  const handleProcessInbox = async () => {
    setLoading(true);
    setLoadingAction('inbox');
    addLog('info', 'Scanning LinkedIn inbox messages for automated AI replies...');
    try {
      await axios.post(`${API_URL}/agents/inbox`);
      showToast('success', 'LinkedIn inbox scan completed!');
    } catch (error: any) {
      showToast('error', `Inbox processing error: ${error.response?.data?.detail || error.message}`);
    } finally {
      setLoading(false);
      setLoadingAction(null);
    }
  };

  const handleStreamZapierSse = async () => {
    if (!workflowId.trim()) {
      showToast('warning', 'Please specify a workflow ID');
      return;
    }
    let parsedData = {};
    try {
      parsedData = JSON.parse(workflowData);
    } catch (e) {
      showToast('error', 'Workflow payload must be valid JSON');
      return;
    }

    setIsStreamingMcp(true);
    setMcpStreamProgress(10);
    setMcpStreamLogs([]);
    addLog('info', `Initiating Zapier MCP SSE Stream for workflow [${workflowId}]`);

    try {
      const response = await fetch(`${API_URL}/agents/zapier/stream-sse`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ workflow_id: workflowId, data: parsedData }),
      });

      if (!response.body) throw new Error('ReadableStream not supported by response');

      const reader = response.body.getReader();
      const decoder = new TextDecoder();
      let buffer = '';

      while (true) {
        const { value, done } = await reader.read();
        if (done) break;
        buffer += decoder.decode(value, { stream: true });
        const lines = buffer.split('\n\n');
        buffer = lines.pop() || '';

        for (const line of lines) {
          if (line.includes('data:')) {
            try {
              const dataMatch = line.match(/data:\s*(.*)/);
              if (dataMatch && dataMatch[1]) {
                const parsed = JSON.parse(dataMatch[1]);
                setMcpStreamLogs((prev) => [...prev, parsed]);
                if (parsed.step) {
                  setMcpStreamProgress(parsed.step * 20);
                } else if (parsed.status === 'completed') {
                  setMcpStreamProgress(100);
                }
              }
            } catch (e) {
              setMcpStreamLogs((prev) => [...prev, { raw: line }]);
            }
          }
        }
      }
      showToast('success', 'Zapier MCP workflow stream finished!');
    } catch (error: any) {
      showToast('error', `Stream Error: ${error.message}`);
    } finally {
      setIsStreamingMcp(false);
    }
  };

  const applyEmailTemplate = (templateKey: string) => {
    setEmailTemplate(templateKey);
    if (templateKey === 'recruiter') {
      setEmailSubject('Inquiry regarding Senior Engineering Roles - Aegis Profile');
      setEmailBody(
        `Hi Team,\n\nI hope you are having a productive week.\n\nI came across your talent open roles and wanted to introduce myself. I specialize in scalable backend architectures, AI agent workflows, and distributed systems.\n\nI would love to connect for a quick 10-minute sync to see how my background could add value to your team.\n\nBest regards,\nAegis User`
      );
    } else if (templateKey === 'demo') {
      setEmailSubject('Exclusive Demo: Autonomous AI Lead Agent & Automation');
      setEmailBody(
        `Hello,\n\nI noticed your focus on optimizing outreach and lead acquisition workflows. We've built Aegis AI v2.0 to automate multichannel LinkedIn engagement and MCP stream execution.\n\nWould you be open to a quick 5-minute interactive walkthrough this week?\n\nCheers,\nAutomation Team`
      );
    } else if (templateKey === 'followup') {
      setEmailSubject('Quick follow-up on our previous conversation');
      setEmailBody(
        `Hi,\n\nFollowing up on my previous note regarding the open discussion. Let me know if you have any questions or if there's a better time to reconnect.\n\nThanks!\n`
      );
    }
  };

  return (
    <div className="min-h-screen bg-[#f8fafc] text-slate-900 flex flex-col selection:bg-indigo-100 selection:text-indigo-900">
      {/* Top Banner & Header */}
      <header className="sticky top-0 z-40 border-b border-slate-200 bg-white/90 backdrop-blur-xl">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-18 py-3.5 flex items-center justify-between">
          <div className="flex items-center gap-3.5">
            <AegisLogo size="md" />
            <div>
              <div className="flex items-center gap-2">
                <h1 className="text-xl font-bold tracking-tight bg-gradient-to-r from-slate-900 to-indigo-600 bg-clip-text text-transparent">
                  AEGIS AI
                </h1>
                <span className="px-2 py-0.5 text-[10px] font-mono font-semibold tracking-wide rounded-full bg-indigo-50 text-indigo-600 border border-indigo-200">
                  v2.0 PRO
                </span>
              </div>
              <p className="text-xs text-slate-500 font-medium">Autonomous LinkedIn & Multi-Channel AI Agent</p>
            </div>
          </div>

          {/* Quick System Indicators */}
          <div className="hidden md:flex items-center gap-4">
            <div className="flex items-center gap-2 px-3 py-1.5 rounded-lg bg-slate-50 border border-slate-200 text-xs">
              <span
                className={`w-2 h-2 rounded-full ${
                  healthStatus?.status === 'healthy' ? 'bg-emerald-500 status-dot-active' : 'bg-rose-500'
                }`}
              />
              <span className="text-slate-500 font-medium">Backend:</span>
              <span className={healthStatus?.status === 'healthy' ? 'text-emerald-600 font-semibold' : 'text-rose-600 font-semibold'}>
                {healthStatus?.status === 'healthy' ? 'Online' : 'Offline'}
              </span>
            </div>

            <div className="flex items-center gap-2 px-3 py-1.5 rounded-lg bg-slate-50 border border-slate-200 text-xs">
              <Radio className="w-3.5 h-3.5 text-indigo-500 animate-pulse" />
              <span className="text-slate-500 font-medium">MCP SSE:</span>
              <span className="text-indigo-600 font-semibold">Active</span>
            </div>

            <a
              href={`${API_URL}/docs`}
              target="_blank"
              rel="noreferrer"
              className="flex items-center gap-1.5 px-3 py-1.5 rounded-lg bg-slate-50 hover:bg-slate-100 text-slate-600 hover:text-slate-900 border border-slate-200 text-xs font-medium transition"
            >
              <span>API Docs</span>
              <ExternalLink className="w-3 h-3" />
            </a>

            <button
              onClick={() => {
                checkHealth();
                checkCredentials();
              }}
              title="Refresh status"
              className="p-2 rounded-lg bg-slate-50 hover:bg-slate-100 text-slate-500 hover:text-indigo-600 border border-slate-200 transition"
            >
              <RefreshCw className={`w-3.5 h-3.5 ${loading ? 'animate-spin' : ''}`} />
            </button>
          </div>
        </div>

        {/* Navigation Tabs */}
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <nav className="flex space-x-1.5 overflow-x-auto py-2 scrollbar-none">
            {[
              { id: 'dashboard', label: 'Mission Control', icon: Activity },
              { id: 'linkedin', label: 'LinkedIn Hub', icon: LinkedInIcon },
              { id: 'outreach', label: 'Recruiter Outreach', icon: Users },
              { id: 'email', label: 'Email Automation', icon: Mail },
              { id: 'zapier', label: 'Zapier & MCP Stream', icon: Zap },
              { id: 'logs', label: 'Activity Logs', icon: Terminal },
            ].map((tab) => {
              const Icon = tab.icon;
              const isActive = activeTab === tab.id;
              return (
                <button
                  key={tab.id}
                  onClick={() => setActiveTab(tab.id as any)}
                  className={`flex items-center gap-2 px-3.5 py-2 rounded-lg text-xs sm:text-sm font-semibold transition-all whitespace-nowrap ${
                    isActive
                      ? 'bg-indigo-50 text-indigo-700 border border-indigo-200 shadow-sm'
                      : 'text-slate-500 hover:text-slate-800 hover:bg-slate-50 border border-transparent'
                  }`}
                >
                  <Icon className={`w-4 h-4 ${isActive ? 'text-indigo-600' : 'text-slate-400'}`} />
                  {tab.label}
                </button>
              );
            })}
          </nav>
        </div>
      </header>

      {/* Global Toast Notification */}
      {notification && (
        <div className="fixed bottom-6 right-6 z-50 max-w-md animate-in slide-in-from-bottom-5 fade-in duration-200">
          <div
            className={`flex items-start gap-3 p-4 rounded-xl shadow-xl border backdrop-blur-xl ${
              notification.type === 'success'
                ? 'bg-emerald-50/95 border-emerald-200 text-emerald-800'
                : notification.type === 'warning'
                ? 'bg-amber-50/95 border-amber-200 text-amber-800'
                : notification.type === 'error'
                ? 'bg-rose-50/95 border-rose-200 text-rose-800'
                : 'bg-white/95 border-indigo-200 text-slate-800'
            }`}
          >
            {notification.type === 'success' && <CheckCircle2 className="w-5 h-5 text-emerald-500 shrink-0 mt-0.5" />}
            {notification.type === 'warning' && <AlertTriangle className="w-5 h-5 text-amber-500 shrink-0 mt-0.5" />}
            {notification.type === 'error' && <AlertCircle className="w-5 h-5 text-rose-500 shrink-0 mt-0.5" />}
            {notification.type === 'info' && <Radio className="w-5 h-5 text-indigo-500 shrink-0 mt-0.5" />}
            <div className="text-xs font-medium leading-relaxed">{notification.text}</div>
          </div>
        </div>
      )}

      {/* Main Workspace Body */}
      <main className="flex-1 max-w-7xl w-full mx-auto px-4 sm:px-6 lg:px-8 py-6 space-y-6">
        {/* ===================== TAB 1: MISSION CONTROL ===================== */}
        {activeTab === 'dashboard' && (
          <div className="space-y-6 animate-in fade-in duration-300">
            {/* Aegis Brand Hero Banner */}
            <div className="relative overflow-hidden rounded-3xl p-6 sm:p-7 bg-gradient-to-br from-indigo-50 via-white to-violet-50 border border-indigo-100 shadow-sm">
              {/* Soft Background Blobs */}
              <div className="absolute top-0 right-0 -mt-10 -mr-10 w-80 h-80 rounded-full bg-indigo-200/25 blur-3xl pointer-events-none" />
              <div className="absolute bottom-0 left-1/4 w-72 h-72 rounded-full bg-violet-200/25 blur-3xl pointer-events-none" />

              <div className="relative z-10 flex flex-col md:flex-row items-center justify-between gap-6">
                <div className="flex flex-col sm:flex-row items-center sm:items-start gap-5 text-center sm:text-left">
                  <AegisLogo size="lg" />
                  <div className="space-y-2">
                    <div className="flex flex-wrap items-center justify-center sm:justify-start gap-2">
                      <span className="px-2.5 py-1 rounded-full text-[11px] font-mono font-semibold bg-indigo-50 text-indigo-700 border border-indigo-200 flex items-center gap-1.5">
                        <span className="w-2 h-2 rounded-full bg-indigo-500 status-dot-active" />
                        AGENT ACTIVE
                      </span>
                      <span className="px-2.5 py-1 rounded-full text-[11px] font-mono font-semibold bg-blue-50 text-blue-700 border border-blue-200">
                        CLAUDE OPUS 5
                      </span>
                      <span className="px-2.5 py-1 rounded-full text-[11px] font-mono font-semibold bg-emerald-50 text-emerald-700 border border-emerald-200">
                        RFC 6202 SSE READY
                      </span>
                    </div>
                    <h2 className="text-2xl sm:text-3xl font-extrabold tracking-tight text-slate-900">
                      Next-Gen <span className="bg-gradient-to-r from-indigo-600 to-violet-600 bg-clip-text text-transparent">Autonomous Outreach</span> Engine
                    </h2>
                    <p className="text-xs sm:text-sm text-slate-600 max-w-2xl leading-relaxed">
                      AI-driven candidate scouting, automated multi-channel messaging, and real-time streaming agent workflows configured with intelligent rate limiting.
                    </p>
                  </div>
                </div>

                {/* Quick Action Badges */}
                <div className="flex flex-row md:flex-col gap-2.5 shrink-0 w-full sm:w-auto">
                  <button
                    onClick={() => setActiveTab('outreach')}
                    className="flex-1 sm:flex-initial btn-primary px-4 py-2.5 rounded-xl text-xs font-bold flex items-center justify-center gap-2"
                  >
                    <Users className="w-4 h-4" />
                    <span>Start Recruiter Scouting</span>
                  </button>
                  <button
                    onClick={() => setActiveTab('zapier')}
                    className="flex-1 sm:flex-initial btn-secondary px-4 py-2.5 rounded-xl text-xs font-semibold flex items-center justify-center gap-2"
                  >
                    <Zap className="w-4 h-4 text-indigo-600" />
                    <span>Stream SSE Pipeline</span>
                  </button>
                </div>
              </div>
            </div>

            {/* Top KPI Cards Grid */}
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
              {/* Card 1: AI Engine */}
              <div className="surface-card-interactive rounded-2xl p-4.5 relative overflow-hidden">
                <div className="flex items-center justify-between mb-3">
                  <span className="text-xs font-semibold text-slate-500 uppercase tracking-wider">AI Inference Engine</span>
                  <div className="w-8 h-8 rounded-lg bg-purple-50 border border-purple-200 flex items-center justify-center">
                    <Sparkles className="w-4 h-4 text-purple-600" />
                  </div>
                </div>
                <div className="flex items-baseline gap-2">
                  <span className="text-lg font-bold text-slate-900">Claude Opus 5</span>
                  <span className="text-[10px] px-1.5 py-0.5 rounded bg-emerald-50 text-emerald-700 font-semibold">Active</span>
                </div>
                <p className="text-xs text-slate-500 mt-1">High-speed cold message & post generation</p>
              </div>

              {/* Card 2: LinkedIn Status */}
              <div className="surface-card-interactive rounded-2xl p-4.5 relative overflow-hidden">
                <div className="flex items-center justify-between mb-3">
                  <span className="text-xs font-semibold text-slate-500 uppercase tracking-wider">LinkedIn Bridge</span>
                  <div className="w-8 h-8 rounded-lg bg-sky-50 border border-sky-200 flex items-center justify-center">
                    <LinkedInIcon className="w-4 h-4 text-[#0077b5]" />
                  </div>
                </div>
                <div className="flex items-baseline gap-2">
                  <span className="text-lg font-bold text-slate-900">
                    {credentialStatus?.linkedin?.connected ? 'Connected' : 'Hybrid Auth'}
                  </span>
                  <span
                    className={`text-[10px] px-1.5 py-0.5 rounded font-semibold ${
                      credentialStatus?.linkedin?.connected
                        ? 'bg-emerald-50 text-emerald-700'
                        : 'bg-amber-50 text-amber-700'
                    }`}
                  >
                    {credentialStatus?.linkedin?.connected ? 'OAuth 2.0' : 'Cookie Mode'}
                  </span>
                </div>
                <p className="text-xs text-slate-500 mt-1">
                  {credentialStatus?.linkedin?.profile_name || 'Autonomous dispatch active'}
                </p>
              </div>

              {/* Card 3: Outreach Pipeline */}
              <div className="surface-card-interactive rounded-2xl p-4.5 relative overflow-hidden">
                <div className="flex items-center justify-between mb-3">
                  <span className="text-xs font-semibold text-slate-500 uppercase tracking-wider">Outreach Speed</span>
                  <div className="w-8 h-8 rounded-lg bg-indigo-50 border border-indigo-200 flex items-center justify-center">
                    <TrendingUp className="w-4 h-4 text-indigo-600" />
                  </div>
                </div>
                <div className="flex items-baseline gap-2">
                  <span className="text-lg font-bold text-slate-900">Auto-Personalized</span>
                  <span className="text-[10px] px-1.5 py-0.5 rounded bg-indigo-50 text-indigo-700 font-semibold">100% AI</span>
                </div>
                <p className="text-xs text-slate-500 mt-1">Dynamic recruiter targeting by role & city</p>
              </div>

              {/* Card 4: Protocol Transport */}
              <div className="surface-card-interactive rounded-2xl p-4.5 relative overflow-hidden">
                <div className="flex items-center justify-between mb-3">
                  <span className="text-xs font-semibold text-slate-500 uppercase tracking-wider">MCP Protocol</span>
                  <div className="w-8 h-8 rounded-lg bg-blue-50 border border-blue-200 flex items-center justify-center">
                    <Zap className="w-4 h-4 text-blue-600" />
                  </div>
                </div>
                <div className="flex items-baseline gap-2">
                  <span className="text-lg font-bold text-slate-900">RFC 6202 SSE</span>
                  <span className="text-[10px] px-1.5 py-0.5 rounded bg-emerald-50 text-emerald-700 font-semibold">Ready</span>
                </div>
                <p className="text-xs text-slate-500 mt-1">Real-time event streaming & tools catalog</p>
              </div>
            </div>

            {/* Main Interactive Grid */}
            <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
              {/* Left Column (2 spans): LinkedIn Integration & Cookie Health Manager */}
              <div className="lg:col-span-2 space-y-6">
                {/* LinkedIn Authentication Card */}
                <div className="surface-card rounded-2xl p-6">
                  <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-5 border-b border-slate-100">
                    <div className="flex items-center gap-3">
                      <div className="w-10 h-10 rounded-xl bg-sky-50 border border-sky-200 flex items-center justify-center">
                        <LinkedInIcon className="w-5 h-5 text-[#0077b5]" />
                      </div>
                      <div>
                        <h2 className="text-base font-bold text-slate-900 flex items-center gap-2">
                          LinkedIn Connection Authority
                          {credentialStatus?.linkedin?.connected ? (
                            <span className="text-[11px] px-2 py-0.5 rounded-full bg-emerald-50 text-emerald-700 border border-emerald-200">
                              Connected
                            </span>
                          ) : (
                            <span className="text-[11px] px-2 py-0.5 rounded-full bg-amber-50 text-amber-700 border border-amber-200">
                              Cookie Mode
                            </span>
                          )}
                        </h2>
                        <p className="text-xs text-slate-500">Manage OAuth authentication or fallback session cookie token</p>
                      </div>
                    </div>

                    <div className="flex items-center gap-2">
                      {credentialStatus?.linkedin?.connected ? (
                        <button
                          onClick={async () => {
                            try {
                              await axios.post(`${API_URL}/auth/linkedin/disconnect`);
                              showToast('info', 'LinkedIn OAuth disconnected');
                              checkCredentials();
                            } catch (e: any) {
                              showToast('error', e.message);
                            }
                          }}
                          className="px-3 py-1.5 text-xs font-semibold rounded-lg bg-rose-50 text-rose-600 border border-rose-200 hover:bg-rose-100 transition"
                        >
                          Disconnect
                        </button>
                      ) : null}

                      <button
                        onClick={() => {
                          window.open(`${API_URL}/auth/linkedin`, '_blank', 'width=600,height=700');
                          const poll = setInterval(() => {
                            checkCredentials();
                          }, 3000);
                          setTimeout(() => clearInterval(poll), 90000);
                        }}
                        className="btn-primary px-4 py-2 text-xs rounded-lg flex items-center gap-1.5"
                      >
                        <LinkedInIcon className="w-3.5 h-3.5" />
                        <span>{credentialStatus?.linkedin?.connected ? 'Re-Authenticate' : 'Connect LinkedIn'}</span>
                      </button>
                    </div>
                  </div>

                  {credentialStatus?.linkedin?.connected ? (
                    <div className="pt-4 grid grid-cols-1 sm:grid-cols-3 gap-4 text-xs">
                      <div className="bg-slate-50 p-3 rounded-xl border border-slate-100">
                        <span className="text-slate-500 block mb-1">Active User</span>
                        <span className="font-semibold text-slate-900">{credentialStatus.linkedin.profile_name}</span>
                      </div>
                      <div className="bg-slate-50 p-3 rounded-xl border border-slate-100">
                        <span className="text-slate-500 block mb-1">Email Account</span>
                        <span className="font-semibold text-slate-700">
                          {credentialStatus.linkedin.profile_email || 'Linked to account'}
                        </span>
                      </div>
                      <div className="bg-slate-50 p-3 rounded-xl border border-slate-100">
                        <span className="text-slate-500 block mb-1">Token Lifetime</span>
                        <span className="font-semibold text-indigo-600">
                          {credentialStatus.linkedin.expires_in_days || 60} days remaining
                        </span>
                      </div>
                    </div>
                  ) : (
                    <div className="pt-4 flex items-center justify-between text-xs text-slate-500">
                      <span>Direct OAuth token not linked. Falling back to session cookie mode.</span>
                    </div>
                  )}

                  {/* Cookie Health & Diagnostic Section */}
                  <div className="mt-5 p-4 rounded-xl bg-slate-50 border border-slate-200 flex flex-col sm:flex-row sm:items-center justify-between gap-4">
                    <div className="flex items-start gap-3">
                      <div className="w-8 h-8 rounded-lg bg-indigo-50 flex items-center justify-center shrink-0 mt-0.5">
                        <KeyRound className="w-4 h-4 text-indigo-600" />
                      </div>
                      <div>
                        <h3 className="text-xs font-semibold text-slate-900">Session Cookie (li_at) Health Validator</h3>
                        <p className="text-[11px] text-slate-500">
                          Ensures background recruiter searches and invitations don't get throttled
                        </p>
                      </div>
                    </div>

                    <div className="flex items-center gap-2">
                      <button
                        onClick={validateLinkedInCookie}
                        disabled={cookieChecking}
                        className="px-3.5 py-1.5 text-xs font-semibold rounded-lg bg-indigo-50 text-indigo-700 border border-indigo-200 hover:bg-indigo-100 transition flex items-center gap-1.5"
                      >
                        <RefreshCw className={`w-3.5 h-3.5 ${cookieChecking ? 'animate-spin' : ''}`} />
                        <span>{cookieChecking ? 'Testing...' : 'Test Cookie'}</span>
                      </button>

                      <button
                        onClick={() => setShowCookieModal(!showCookieModal)}
                        className="px-3 py-1.5 text-xs font-medium rounded-lg bg-white text-slate-600 hover:text-slate-900 border border-slate-200 hover:bg-slate-100 transition"
                      >
                        Refresh Guide
                      </button>
                    </div>
                  </div>

                  {/* Cookie Validation Result Banner */}
                  {cookieValidation && (
                    <div
                      className={`mt-3 p-3.5 rounded-xl border text-xs leading-relaxed animate-in fade-in ${
                        cookieValidation.valid
                          ? 'bg-emerald-50 border-emerald-200 text-emerald-800'
                          : 'bg-rose-50 border-rose-200 text-rose-800'
                      }`}
                    >
                      <div className="flex items-center gap-2 font-bold mb-1">
                        {cookieValidation.valid ? <CheckCircle2 className="w-4 h-4 text-emerald-500" /> : <AlertCircle className="w-4 h-4 text-rose-500" />}
                        <span>{cookieValidation.valid ? 'Cookie Valid & Active' : 'Cookie Expired or Missing'}</span>
                      </div>
                      <p className="text-[11px] opacity-90">{cookieValidation.message}</p>
                    </div>
                  )}

                  {/* Step-by-step Cookie Helper Accordion */}
                  {showCookieModal && (
                    <div className="mt-4 p-4 rounded-xl bg-indigo-50/60 border border-indigo-200 text-xs space-y-3 animate-in slide-in-from-top-2">
                      <div className="flex items-center justify-between">
                        <span className="font-bold text-indigo-700 flex items-center gap-1.5">
                          <HelpCircle className="w-4 h-4" /> 5-Step Guide to Refresh `li_at` Session Cookie
                        </span>
                        <button
                          onClick={() => {
                            navigator.clipboard.writeText('LINKEDIN_SESSION_COOKIE=');
                            setCopiedCookieStep(true);
                            setTimeout(() => setCopiedCookieStep(false), 2000);
                          }}
                          className="text-[11px] text-slate-500 hover:text-indigo-600 flex items-center gap-1"
                        >
                          {copiedCookieStep ? <Check className="w-3 h-3 text-emerald-500" /> : <Copy className="w-3 h-3" />}
                          <span>Copy key</span>
                        </button>
                      </div>
                      <ol className="list-decimal list-inside space-y-1.5 text-slate-600 text-[11px] leading-relaxed">
                        <li>
                          Open <span className="text-indigo-700 font-mono">linkedin.com</span> in Chrome and make sure you are logged in.
                        </li>
                        <li>
                          Press <kbd className="px-1.5 py-0.5 rounded bg-white border border-slate-300 text-slate-700 shadow-sm">F12</kbd> (or right click Inspect) and select the <strong className="text-slate-900">Application</strong> tab.
                        </li>
                        <li>
                          In the left sidebar, expand <strong className="text-slate-900">Cookies</strong> &rarr; click <span className="text-indigo-700">https://www.linkedin.com</span>.
                        </li>
                        <li>
                          Find the cookie named <strong className="text-slate-900 font-mono">li_at</strong> and copy its Value.
                        </li>
                        <li>
                          Paste it in <strong className="text-slate-900 font-mono">backend/.env</strong> as <code className="text-indigo-700 bg-indigo-50 px-1 rounded">LINKEDIN_SESSION_COOKIE=...</code> and restart backend.
                        </li>
                      </ol>
                    </div>
                  )}
                </div>

                {/* Quick Action Matrix */}
                <div className="surface-card rounded-2xl p-6">
                  <h3 className="text-sm font-bold text-slate-900 mb-4 flex items-center gap-2">
                    <Sparkles className="w-4 h-4 text-indigo-600" />
                    Autonomous Agent Quick Dispatches
                  </h3>
                  <div className="grid grid-cols-1 sm:grid-cols-3 gap-3">
                    <button
                      onClick={handleOptimizeProfile}
                      disabled={loading}
                      className="p-3.5 rounded-xl bg-white hover:bg-slate-50 border border-slate-200 hover:border-indigo-200 hover:shadow-sm text-left transition group"
                    >
                      <div className="flex items-center justify-between mb-2">
                        <Sparkles className="w-4 h-4 text-purple-600 group-hover:scale-110 transition-transform" />
                        <ChevronRight className="w-3.5 h-3.5 text-slate-400 group-hover:text-indigo-600 transition" />
                      </div>
                      <div className="text-xs font-bold text-slate-900">Optimize Profile</div>
                      <p className="text-[11px] text-slate-500 mt-1">AI suggestions for headline & summary</p>
                    </button>

                    <button
                      onClick={handleProcessInbox}
                      disabled={loading}
                      className="p-3.5 rounded-xl bg-white hover:bg-slate-50 border border-slate-200 hover:border-indigo-200 hover:shadow-sm text-left transition group"
                    >
                      <div className="flex items-center justify-between mb-2">
                        <MessageSquare className="w-4 h-4 text-blue-600 group-hover:scale-110 transition-transform" />
                        <ChevronRight className="w-3.5 h-3.5 text-slate-400 group-hover:text-indigo-600 transition" />
                      </div>
                      <div className="text-xs font-bold text-slate-900">Process Inbox</div>
                      <p className="text-[11px] text-slate-500 mt-1">Scan unread DMs & prepare replies</p>
                    </button>

                    <button
                      onClick={() => setActiveTab('outreach')}
                      className="p-3.5 rounded-xl bg-white hover:bg-slate-50 border border-slate-200 hover:border-indigo-200 hover:shadow-sm text-left transition group"
                    >
                      <div className="flex items-center justify-between mb-2">
                        <Users className="w-4 h-4 text-emerald-600 group-hover:scale-110 transition-transform" />
                        <ChevronRight className="w-3.5 h-3.5 text-slate-400 group-hover:text-indigo-600 transition" />
                      </div>
                      <div className="text-xs font-bold text-slate-900">Recruiter Blast</div>
                      <p className="text-[11px] text-slate-500 mt-1">Launch targeted candidate outreach</p>
                    </button>
                  </div>
                </div>
              </div>

              {/* Right Column (1 span): Credentials Status & Agent Capabilities */}
              <div className="space-y-6">
                {/* Credentials Card */}
                <div className="surface-card rounded-2xl p-6">
                  <h3 className="text-sm font-bold text-slate-900 mb-4 flex items-center gap-2">
                    <KeyRound className="w-4 h-4 text-indigo-600" />
                    Service Credentials
                  </h3>
                  <div className="space-y-3 text-xs">
                    <div className="flex items-center justify-between p-2.5 rounded-lg bg-slate-50 border border-slate-100">
                      <div className="flex items-center gap-2.5">
                        <LinkedInIcon className="w-4 h-4 text-[#0077b5]" />
                        <span className="font-medium text-slate-700">LinkedIn Session</span>
                      </div>
                      <span
                        className={`px-2 py-0.5 rounded text-[10px] font-semibold ${
                          credentialStatus?.linkedin_configured
                            ? 'bg-emerald-50 text-emerald-700'
                            : 'bg-rose-50 text-rose-700'
                        }`}
                      >
                        {credentialStatus?.linkedin_configured ? 'Configured' : 'Missing'}
                      </span>
                    </div>

                    <div className="flex items-center justify-between p-2.5 rounded-lg bg-slate-50 border border-slate-100">
                      <div className="flex items-center gap-2.5">
                        <Cpu className="w-4 h-4 text-purple-600" />
                        <span className="font-medium text-slate-700">Claude AI Opus 5</span>
                      </div>
                      <span
                        className={`px-2 py-0.5 rounded text-[10px] font-semibold ${
                          credentialStatus?.claude_configured
                            ? 'bg-emerald-50 text-emerald-700'
                            : 'bg-rose-50 text-rose-700'
                        }`}
                      >
                        {credentialStatus?.claude_configured ? 'Ready' : 'Missing'}
                      </span>
                    </div>

                    <div className="flex items-center justify-between p-2.5 rounded-lg bg-slate-50 border border-slate-100">
                      <div className="flex items-center gap-2.5">
                        <Mail className="w-4 h-4 text-amber-600" />
                        <span className="font-medium text-slate-700">Email SMTP</span>
                      </div>
                      <span
                        className={`px-2 py-0.5 rounded text-[10px] font-semibold ${
                          credentialStatus?.smtp_configured
                            ? 'bg-emerald-50 text-emerald-700'
                            : 'bg-rose-50 text-rose-700'
                        }`}
                      >
                        {credentialStatus?.smtp_configured ? 'Ready' : 'Missing'}
                      </span>
                    </div>

                    <div className="flex items-center justify-between p-2.5 rounded-lg bg-slate-50 border border-slate-100">
                      <div className="flex items-center gap-2.5">
                        <Zap className="w-4 h-4 text-blue-600" />
                        <span className="font-medium text-slate-700">Zapier MCP Bridge</span>
                      </div>
                      <span
                        className={`px-2 py-0.5 rounded text-[10px] font-semibold ${
                          credentialStatus?.zapier_configured
                            ? 'bg-emerald-50 text-emerald-700'
                            : 'bg-slate-100 text-slate-500'
                        }`}
                      >
                        {credentialStatus?.zapier_configured ? 'Connected' : 'Optional'}
                      </span>
                    </div>
                  </div>
                </div>

                {/* Agent Feature Set Card */}
                <div className="surface-card rounded-2xl p-6">
                  <h3 className="text-sm font-bold text-slate-900 mb-4 flex items-center gap-2">
                    <Layers className="w-4 h-4 text-indigo-600" />
                    Available Agent Tools
                  </h3>
                  <div className="space-y-2 text-xs">
                    {[
                      'LinkedIn Feed Publisher (Text + Media)',
                      'Claude Viral Thought-Leadership Generator',
                      'Recruiter Geo & Title Scanner',
                      'Personalized Invite Note Crafter',
                      'Direct Connection Inviter',
                      'Direct Messenger (DM) Dispatcher',
                      'RFC 6202 SSE Stream Server',
                      'SMTP Cold Outreach Email Engine',
                    ].map((tool, idx) => (
                      <div key={idx} className="flex items-center gap-2 text-slate-600">
                        <CheckCircle2 className="w-3.5 h-3.5 text-indigo-500 shrink-0" />
                        <span className="text-[11px] font-medium">{tool}</span>
                      </div>
                    ))}
                  </div>
                </div>
              </div>
            </div>
          </div>
        )}

        {/* ===================== TAB 2: LINKEDIN HUB ===================== */}
        {activeTab === 'linkedin' && (
          <div className="space-y-6 animate-in fade-in duration-300">
            <div className="grid grid-cols-1 lg:grid-cols-12 gap-6">
              {/* Left Column: Post Studio & Viral Topic Generator */}
              <div className="lg:col-span-7 space-y-6">
                {/* AI Post Studio */}
                <div className="surface-card rounded-2xl p-6">
                  <div className="flex items-center justify-between mb-4">
                    <div className="flex items-center gap-2.5">
                      <div className="w-9 h-9 rounded-xl bg-indigo-50 border border-indigo-200 flex items-center justify-center">
                        <FileText className="w-4 h-4 text-indigo-600" />
                      </div>
                      <div>
                        <h2 className="text-base font-bold text-slate-900">Post Studio</h2>
                        <p className="text-xs text-slate-500">Craft and publish engaging updates directly to LinkedIn</p>
                      </div>
                    </div>

                    <span className="text-xs font-mono text-slate-400">
                      {postContent.length} / 3000 chars
                    </span>
                  </div>

                  <div className="space-y-4">
                    <textarea
                      value={postContent}
                      onChange={(e) => setPostContent(e.target.value)}
                      placeholder="What insights or engineering updates would you like to share today?..."
                      rows={5}
                      className="w-full field-input p-3.5 rounded-xl text-xs sm:text-sm resize-none font-sans leading-relaxed"
                    />

                    {/* Image Attachment URL */}
                    <div>
                      <label className="text-xs font-semibold text-slate-700 flex items-center gap-1.5 mb-1.5">
                        <ImageIcon className="w-3.5 h-3.5 text-slate-400" />
                        Attachment Image URL (Optional)
                      </label>
                      <input
                        type="text"
                        value={postImageUrl}
                        onChange={(e) => setPostImageUrl(e.target.value)}
                        placeholder="https://images.unsplash.com/... or local file path"
                        className="w-full field-input px-3.5 py-2.5 rounded-xl text-xs"
                      />
                    </div>

                    {/* Preview box if image URL is present */}
                    {postImageUrl && (
                      <div className="p-2 rounded-xl bg-slate-50 border border-slate-200 flex items-center gap-3">
                        <img
                          src={postImageUrl}
                          alt="Attachment preview"
                          className="w-12 h-12 object-cover rounded-lg bg-slate-100"
                          onError={(e) => {
                            (e.target as HTMLElement).style.display = 'none';
                          }}
                        />
                        <span className="text-xs text-slate-500 truncate">Image attachment ready</span>
                      </div>
                    )}

                    <div className="flex items-center justify-end gap-3 pt-2">
                      <button
                        onClick={() => {
                          setPostContent('');
                          setPostImageUrl('');
                        }}
                        disabled={loading || (!postContent && !postImageUrl)}
                        className="px-4 py-2 text-xs font-medium rounded-xl text-slate-500 hover:text-slate-800 transition"
                      >
                        Clear
                      </button>

                      <button
                        onClick={handlePostToLinkedIn}
                        disabled={loading || !postContent.trim()}
                        className="btn-primary px-6 py-2.5 rounded-xl text-xs flex items-center gap-2 disabled:opacity-50 disabled:cursor-not-allowed"
                      >
                        <Send className={`w-3.5 h-3.5 ${loadingAction === 'post' ? 'animate-spin' : ''}`} />
                        <span>{loadingAction === 'post' ? 'Publishing...' : 'Publish to Feed'}</span>
                      </button>
                    </div>
                  </div>
                </div>

                {/* Claude AI Trending Topic Generator */}
                <div className="surface-card rounded-2xl p-6">
                  <div className="flex items-center gap-2.5 mb-4">
                    <div className="w-9 h-9 rounded-xl bg-purple-50 border border-purple-200 flex items-center justify-center">
                      <Flame className="w-4 h-4 text-purple-600" />
                    </div>
                    <div>
                      <h2 className="text-base font-bold text-slate-900">AI Viral Topic Generator</h2>
                      <p className="text-xs text-slate-500">
                        Let Claude compose a high-engagement thought leadership post
                      </p>
                    </div>
                  </div>

                  <div className="space-y-4">
                    <div>
                      <label className="text-xs font-semibold text-slate-700 block mb-1.5">Topic or Tech Angle</label>
                      <input
                        type="text"
                        value={trendingTopic}
                        onChange={(e) => setTrendingTopic(e.target.value)}
                        placeholder="e.g. Next.js 15 Server Actions vs REST, LLM Agents in production"
                        className="w-full field-input px-3.5 py-2.5 rounded-xl text-xs sm:text-sm"
                      />
                    </div>

                    {/* Quick Topic Chips */}
                    <div className="flex flex-wrap gap-1.5">
                      {[
                        'AI Agents in Production',
                        'Serverless vs Containers 2026',
                        'Clean Architecture & Domain Driven Design',
                        'Next.js 15 Fullstack Strategies',
                      ].map((chip) => (
                        <button
                          key={chip}
                          type="button"
                          onClick={() => setTrendingTopic(chip)}
                          className="px-2.5 py-1 rounded-lg bg-slate-50 hover:bg-slate-100 border border-slate-200 text-[11px] text-slate-600 hover:text-indigo-600 transition"
                        >
                          + {chip}
                        </button>
                      ))}
                    </div>

                    <div>
                      <label className="text-xs font-semibold text-slate-700 block mb-1.5">Writing Tone</label>
                      <div className="grid grid-cols-2 sm:grid-cols-4 gap-2">
                        {[
                          { key: 'thought_leadership', label: 'Thought Leader' },
                          { key: 'technical', label: 'Tech Deep-Dive' },
                          { key: 'story', label: 'Builder Story' },
                          { key: 'contrarian', label: 'Hot Take' },
                        ].map((item) => (
                          <button
                            key={item.key}
                            type="button"
                            onClick={() => setPostTone(item.key as any)}
                            className={`px-3 py-2 rounded-xl text-xs font-medium transition ${
                              postTone === item.key
                                ? 'bg-indigo-50 text-indigo-700 border border-indigo-300'
                                : 'bg-slate-50 text-slate-500 hover:text-slate-800 border border-slate-200'
                            }`}
                          >
                            {item.label}
                          </button>
                        ))}
                      </div>
                    </div>

                    <button
                      onClick={handleGenerateAiPost}
                      disabled={aiGeneratingPost || !trendingTopic.trim()}
                      className="w-full py-2.5 rounded-xl bg-gradient-to-r from-indigo-600 via-violet-600 to-indigo-500 hover:opacity-95 text-white font-bold text-xs flex items-center justify-center gap-2 shadow-sm shadow-indigo-200 transition disabled:opacity-50"
                    >
                      <Sparkles className={`w-4 h-4 ${aiGeneratingPost ? 'animate-spin' : ''}`} />
                      <span>{aiGeneratingPost ? 'Synthesizing with Claude...' : 'Generate & Post with AI'}</span>
                    </button>
                  </div>
                </div>
              </div>

              {/* Right Column: Connection Sender & Direct Messaging */}
              <div className="lg:col-span-5 space-y-6">
                {/* Direct Connection Inviter */}
                <div className="surface-card rounded-2xl p-6">
                  <div className="flex items-center gap-2.5 mb-4">
                    <div className="w-9 h-9 rounded-xl bg-emerald-50 border border-emerald-200 flex items-center justify-center">
                      <UserCheck className="w-4 h-4 text-emerald-600" />
                    </div>
                    <div>
                      <h2 className="text-base font-bold text-slate-900">Send Connection</h2>
                      <p className="text-xs text-slate-500">Target a specific profile with a personalized invite</p>
                    </div>
                  </div>

                  <div className="space-y-3.5">
                    <div>
                      <label className="text-xs font-semibold text-slate-700 block mb-1.5">LinkedIn Profile URL</label>
                      <input
                        type="text"
                        value={connectUrl}
                        onChange={(e) => setConnectUrl(e.target.value)}
                        placeholder="https://www.linkedin.com/in/username"
                        className="w-full field-input px-3.5 py-2.5 rounded-xl text-xs"
                      />
                    </div>

                    <div>
                      <label className="text-xs font-semibold text-slate-700 block mb-1.5">
                        Personalized Note (Optional)
                      </label>
                      <textarea
                        value={connectNote}
                        onChange={(e) => setConnectNote(e.target.value)}
                        placeholder="Hi [Name], would love to connect and follow your engineering work!..."
                        rows={3}
                        className="w-full field-input p-3 rounded-xl text-xs resize-none"
                      />
                    </div>

                    <button
                      onClick={handleSendConnection}
                      disabled={loading || !connectUrl.trim()}
                      className="w-full btn-primary py-2.5 rounded-xl text-xs flex items-center justify-center gap-2 disabled:opacity-50"
                    >
                      <Send className={`w-3.5 h-3.5 ${loadingAction === 'connect' ? 'animate-spin' : ''}`} />
                      <span>{loadingAction === 'connect' ? 'Dispatching...' : 'Send Connection Request'}</span>
                    </button>
                  </div>
                </div>

                {/* Direct Messaging (DM) */}
                <div className="surface-card rounded-2xl p-6">
                  <div className="flex items-center gap-2.5 mb-4">
                    <div className="w-9 h-9 rounded-xl bg-blue-50 border border-blue-200 flex items-center justify-center">
                      <MessageSquare className="w-4 h-4 text-blue-600" />
                    </div>
                    <div>
                      <h2 className="text-base font-bold text-slate-900">Direct Message (DM)</h2>
                      <p className="text-xs text-slate-500">Send direct notes to existing 1st degree connections</p>
                    </div>
                  </div>

                  <div className="space-y-3.5">
                    <div>
                      <label className="text-xs font-semibold text-slate-700 block mb-1.5">Connection Name</label>
                      <input
                        type="text"
                        value={dmName}
                        onChange={(e) => setDmName(e.target.value)}
                        placeholder="e.g. John Doe"
                        className="w-full field-input px-3.5 py-2.5 rounded-xl text-xs"
                      />
                    </div>

                    <div>
                      <label className="text-xs font-semibold text-slate-700 block mb-1.5">Message</label>
                      <textarea
                        value={dmMessage}
                        onChange={(e) => setDmMessage(e.target.value)}
                        placeholder="Hey John, let's catch up regarding the new project!..."
                        rows={3}
                        className="w-full field-input p-3 rounded-xl text-xs resize-none"
                      />
                    </div>

                    <button
                      onClick={handleSendDm}
                      disabled={loading || !dmName.trim() || !dmMessage.trim()}
                      className="w-full btn-primary py-2.5 rounded-xl text-xs flex items-center justify-center gap-2 disabled:opacity-50"
                    >
                      <Send className={`w-3.5 h-3.5 ${loadingAction === 'dm' ? 'animate-spin' : ''}`} />
                      <span>{loadingAction === 'dm' ? 'Sending Message...' : 'Send Direct Message'}</span>
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        )}

        {/* ===================== TAB 3: RECRUITER COLD OUTREACH ===================== */}
        {activeTab === 'outreach' && (
          <div className="space-y-6 animate-in fade-in duration-300">
            {/* Header Banner */}
            <div className="surface-card rounded-2xl p-6 relative overflow-hidden">
              <div className="max-w-3xl">
                <div className="inline-flex items-center gap-2 px-2.5 py-1 rounded-full bg-indigo-50 text-indigo-600 border border-indigo-200 text-xs font-semibold mb-3">
                  <Sparkles className="w-3.5 h-3.5" />
                  AI Candidate-Recruiter Pipeline Engine
                </div>
                <h2 className="text-xl font-extrabold text-slate-900 tracking-tight sm:text-2xl">
                  Automated Recruiter Cold Outreach Campaigns
                </h2>
                <p className="text-xs sm:text-sm text-slate-500 mt-2 leading-relaxed">
                  Aegis AI searches local recruiters and tech talent leads matching your target role and geography,
                  then crafts hyper-personalized notes using Claude and dispatches connection invites autonomously.
                </p>
              </div>
            </div>

            <div className="grid grid-cols-1 lg:grid-cols-12 gap-6">
              {/* Campaign Setup Form */}
              <div className="lg:col-span-5 space-y-6">
                <div className="surface-card rounded-2xl p-6">
                  <h3 className="text-sm font-bold text-slate-900 mb-4 flex items-center gap-2">
                    <Sliders className="w-4 h-4 text-indigo-600" />
                    Campaign Parameters
                  </h3>

                  <div className="space-y-4">
                    <div>
                      <label className="text-xs font-semibold text-slate-700 block mb-1.5">Target Job Title / Role</label>
                      <input
                        type="text"
                        value={coldJob}
                        onChange={(e) => setColdJob(e.target.value)}
                        placeholder="e.g. Senior Full Stack Engineer"
                        className="w-full field-input px-3.5 py-2.5 rounded-xl text-xs sm:text-sm"
                      />
                    </div>

                    {/* Quick Role Selection */}
                    <div className="flex flex-wrap gap-1.5">
                      {[
                        'Staff AI Engineer',
                        'Senior Frontend Engineer',
                        'Tech Lead',
                        'Engineering Manager',
                      ].map((role) => (
                        <button
                          key={role}
                          type="button"
                          onClick={() => setColdJob(role)}
                          className="px-2 py-0.5 rounded-md bg-slate-50 hover:bg-slate-100 border border-slate-200 text-[10px] text-slate-600 hover:text-indigo-600 transition"
                        >
                          {role}
                        </button>
                      ))}
                    </div>

                    <div>
                      <label className="text-xs font-semibold text-slate-700 block mb-1.5">Location / Target Market</label>
                      <input
                        type="text"
                        value={coldArea}
                        onChange={(e) => setColdArea(e.target.value)}
                        placeholder="e.g. San Francisco, CA or Remote"
                        className="w-full field-input px-3.5 py-2.5 rounded-xl text-xs sm:text-sm"
                      />
                    </div>

                    {/* Quick Location Selection */}
                    <div className="flex flex-wrap gap-1.5">
                      {['San Francisco, CA', 'New York, NY', 'Seattle, WA', 'Bengaluru, IN', 'London, UK'].map((loc) => (
                        <button
                          key={loc}
                          type="button"
                          onClick={() => setColdArea(loc)}
                          className="px-2 py-0.5 rounded-md bg-slate-50 hover:bg-slate-100 border border-slate-200 text-[10px] text-slate-600 hover:text-indigo-600 transition"
                        >
                          {loc}
                        </button>
                      ))}
                    </div>

                    <div>
                      <div className="flex items-center justify-between mb-1.5">
                        <label className="text-xs font-semibold text-slate-700">Recruiter Batch Limit</label>
                        <span className="text-xs font-mono font-bold text-indigo-600">{coldLimit} recruiters</span>
                      </div>
                      <input
                        type="range"
                        min="1"
                        max="10"
                        value={coldLimit}
                        onChange={(e) => setColdLimit(parseInt(e.target.value) || 3)}
                        className="w-full accent-indigo-600 bg-slate-200 h-2 rounded-lg cursor-pointer"
                      />
                    </div>

                    <div>
                      <label className="text-xs font-semibold text-slate-700 block mb-1.5">
                        Custom Note Template (Optional)
                      </label>
                      <textarea
                        value={coldCustomMessage}
                        onChange={(e) => setColdCustomMessage(e.target.value)}
                        placeholder="Hi {name}, noticed you recruit for {role} roles in {area}. I'd love to connect! (Leave blank for Claude auto-generation)"
                        rows={3}
                        className="w-full field-input p-3 rounded-xl text-xs resize-none font-mono"
                      />
                    </div>

                    <button
                      onClick={handleColdOutreach}
                      disabled={loading || !coldJob.trim() || !coldArea.trim()}
                      className="w-full py-3 rounded-xl btn-primary text-xs font-bold flex items-center justify-center gap-2 transition disabled:opacity-50"
                    >
                      <Play className={`w-4 h-4 ${loadingAction === 'outreach' ? 'animate-spin' : ''}`} />
                      <span>{loadingAction === 'outreach' ? 'Executing Campaign...' : 'Launch Outreach Campaign'}</span>
                    </button>
                  </div>
                </div>
              </div>

              {/* Campaign Live Stream & Results */}
              <div className="lg:col-span-7 space-y-6">
                <div className="surface-card rounded-2xl p-6 min-h-[400px] flex flex-col">
                  <div className="flex items-center justify-between pb-4 border-b border-slate-100 mb-4">
                    <h3 className="text-sm font-bold text-slate-900 flex items-center gap-2">
                      <Users className="w-4 h-4 text-indigo-600" />
                      Candidate Pipeline Dispatch Feed
                    </h3>
                    <span className="text-xs text-slate-500 font-mono">
                      {outreachResults.length} leads in session
                    </span>
                  </div>

                  {outreachResults.length === 0 ? (
                    <div className="flex-1 flex flex-col items-center justify-center p-8 text-center text-slate-400">
                      <Users className="w-12 h-12 stroke-[1.2] text-slate-300 mb-3" />
                      <p className="text-xs font-semibold text-slate-500">No active campaign results yet</p>
                      <p className="text-[11px] text-slate-400 max-w-sm mt-1">
                        Configure your target role and region on the left and click "Launch Outreach Campaign" to watch Aegis AI discover recruiters and dispatch invites.
                      </p>
                    </div>
                  ) : (
                    <div className="space-y-3 overflow-y-auto max-h-[500px] pr-1">
                      {outreachResults.map((lead: any, idx: number) => (
                        <div
                          key={idx}
                          className="p-4 rounded-xl bg-slate-50 border border-slate-200 hover:border-indigo-200 transition space-y-2"
                        >
                          <div className="flex items-start justify-between">
                            <div>
                              <span className="text-xs font-bold text-slate-900 flex items-center gap-2">
                                👤 {lead.recruiter_name || lead.name || 'Recruiter Lead'}
                                <span className="text-[10px] px-1.5 py-0.5 rounded bg-emerald-50 text-emerald-700 font-semibold">
                                  {lead.status || 'Success'}
                                </span>
                              </span>
                              <span className="text-[11px] text-slate-500">{lead.profile_url || lead.url || 'LinkedIn Profile'}</span>
                            </div>
                            <span className="text-[10px] text-slate-400 font-mono">{new Date().toLocaleTimeString()}</span>
                          </div>

                          {lead.note && (
                            <div className="p-2.5 rounded-lg bg-white border border-slate-200 text-[11px] text-slate-600 font-sans italic">
                              "{lead.note}"
                            </div>
                          )}
                        </div>
                      ))}
                    </div>
                  )}
                </div>
              </div>
            </div>
          </div>
        )}

        {/* ===================== TAB 4: EMAIL AUTOMATION ===================== */}
        {activeTab === 'email' && (
          <div className="space-y-6 animate-in fade-in duration-300 max-w-4xl mx-auto">
            <div className="surface-card rounded-2xl p-6 sm:p-8">
              <div className="flex items-center justify-between mb-6 pb-4 border-b border-slate-100">
                <div className="flex items-center gap-3">
                  <div className="w-10 h-10 rounded-xl bg-amber-50 border border-amber-200 flex items-center justify-center">
                    <Mail className="w-5 h-5 text-amber-600" />
                  </div>
                  <div>
                    <h2 className="text-lg font-bold text-slate-900">SMTP Email Dispatcher</h2>
                    <p className="text-xs text-slate-500">Send high-converting cold pitches, demos, and follow-ups</p>
                  </div>
                </div>

                <div className="flex items-center gap-2 text-xs">
                  <span className="text-slate-500">SMTP:</span>
                  <span
                    className={`px-2 py-0.5 rounded font-semibold text-[10px] ${
                      credentialStatus?.smtp_configured
                        ? 'bg-emerald-50 text-emerald-700'
                        : 'bg-rose-50 text-rose-700'
                    }`}
                  >
                    {credentialStatus?.smtp_configured ? 'Ready' : 'Not configured'}
                  </span>
                </div>
              </div>

              {/* Template Switcher */}
              <div className="mb-5">
                <label className="text-xs font-semibold text-slate-700 block mb-2">Preset Email Templates</label>
                <div className="grid grid-cols-2 sm:grid-cols-4 gap-2">
                  {[
                    { key: 'recruiter', label: 'Recruiter Inquiry' },
                    { key: 'demo', label: 'Product Demo Pitch' },
                    { key: 'followup', label: 'Follow-up Note' },
                    { key: 'custom', label: 'Blank / Custom' },
                  ].map((tpl) => (
                    <button
                      key={tpl.key}
                      type="button"
                      onClick={() => applyEmailTemplate(tpl.key)}
                      className={`px-3 py-2 rounded-xl text-xs font-medium transition ${
                        emailTemplate === tpl.key
                          ? 'bg-indigo-50 text-indigo-700 border border-indigo-300'
                          : 'bg-slate-50 text-slate-500 hover:text-slate-800 border border-slate-200'
                      }`}
                    >
                      {tpl.label}
                    </button>
                  ))}
                </div>
              </div>

              <div className="space-y-4">
                <div>
                  <label className="text-xs font-semibold text-slate-700 block mb-1.5">Recipient Email Address</label>
                  <input
                    type="email"
                    value={emailTo}
                    onChange={(e) => setEmailTo(e.target.value)}
                    placeholder="talent@company.com"
                    className="w-full field-input px-3.5 py-2.5 rounded-xl text-xs sm:text-sm"
                  />
                </div>

                <div>
                  <label className="text-xs font-semibold text-slate-700 block mb-1.5">Subject Line</label>
                  <input
                    type="text"
                    value={emailSubject}
                    onChange={(e) => setEmailSubject(e.target.value)}
                    placeholder="Inquiry regarding Engineering Role..."
                    className="w-full field-input px-3.5 py-2.5 rounded-xl text-xs sm:text-sm"
                  />
                </div>

                <div>
                  <label className="text-xs font-semibold text-slate-700 block mb-1.5">Email Body</label>
                  <textarea
                    value={emailBody}
                    onChange={(e) => setEmailBody(e.target.value)}
                    placeholder="Compose email message here..."
                    rows={8}
                    className="w-full field-input p-3.5 rounded-xl text-xs sm:text-sm resize-none font-sans leading-relaxed"
                  />
                </div>

                <div className="flex items-center justify-end gap-3 pt-2">
                  <button
                    onClick={() => {
                      setEmailTo('');
                      setEmailSubject('');
                      setEmailBody('');
                    }}
                    className="px-4 py-2 text-xs font-medium rounded-xl text-slate-500 hover:text-slate-800 transition"
                  >
                    Clear
                  </button>

                  <button
                    onClick={handleSendEmail}
                    disabled={loading || !emailTo.trim() || !emailSubject.trim() || !emailBody.trim()}
                    className="btn-primary px-6 py-2.5 rounded-xl text-xs flex items-center gap-2 disabled:opacity-50 disabled:cursor-not-allowed"
                  >
                    <Send className={`w-3.5 h-3.5 ${loadingAction === 'email' ? 'animate-spin' : ''}`} />
                    <span>{loadingAction === 'email' ? 'Dispatching...' : 'Send Email via SMTP'}</span>
                  </button>
                </div>
              </div>
            </div>
          </div>
        )}

        {/* ===================== TAB 5: ZAPIER & MCP STREAMING ===================== */}
        {activeTab === 'zapier' && (
          <div className="space-y-6 animate-in fade-in duration-300">
            <div className="grid grid-cols-1 lg:grid-cols-12 gap-6">
              {/* Left Column: Workflow Configuration */}
              <div className="lg:col-span-5 space-y-6">
                <div className="surface-card rounded-2xl p-6">
                  <div className="flex items-center gap-2.5 mb-4">
                    <div className="w-9 h-9 rounded-xl bg-blue-50 border border-blue-200 flex items-center justify-center">
                      <Zap className="w-4 h-4 text-blue-600" />
                    </div>
                    <div>
                      <h2 className="text-base font-bold text-slate-900">Zapier & MCP Workflow Dispatcher</h2>
                      <p className="text-xs text-slate-500">Stream RFC 6202 events with real-time progress</p>
                    </div>
                  </div>

                  <div className="space-y-4">
                    <div>
                      <label className="text-xs font-semibold text-slate-700 block mb-1.5">Workflow ID</label>
                      <input
                        type="text"
                        value={workflowId}
                        onChange={(e) => setWorkflowId(e.target.value)}
                        placeholder="e.g. agent_sync_pipeline"
                        className="w-full field-input px-3.5 py-2.5 rounded-xl text-xs sm:text-sm"
                      />
                    </div>

                    <div>
                      <label className="text-xs font-semibold text-slate-700 block mb-1.5">JSON Payload</label>
                      <textarea
                        value={workflowData}
                        onChange={(e) => setWorkflowData(e.target.value)}
                        rows={8}
                        className="w-full field-input p-3 rounded-xl text-xs font-mono text-indigo-700 bg-slate-50 resize-none leading-relaxed"
                      />
                    </div>

                    <div className="flex items-center gap-3">
                      <button
                        onClick={handleStreamZapierSse}
                        disabled={isStreamingMcp || !workflowId.trim()}
                        className="flex-1 py-2.5 rounded-xl btn-primary text-xs font-bold flex items-center justify-center gap-2 disabled:opacity-50"
                      >
                        <Zap className={`w-3.5 h-3.5 ${isStreamingMcp ? 'animate-spin' : ''}`} />
                        <span>{isStreamingMcp ? 'Streaming SSE...' : 'Launch MCP SSE Stream'}</span>
                      </button>
                    </div>
                  </div>
                </div>
              </div>

              {/* Right Column: Live Event Stream Terminal */}
              <div className="lg:col-span-7 space-y-6">
                <div className="surface-card rounded-2xl p-6 min-h-[420px] flex flex-col">
                  <div className="flex items-center justify-between pb-4 border-b border-slate-100 mb-4">
                    <div className="flex items-center gap-2">
                      <Radio className="w-4 h-4 text-indigo-600 animate-pulse" />
                      <h3 className="text-sm font-bold text-slate-900">Live MCP Event Stream Terminal</h3>
                    </div>
                    <span className="text-xs font-mono text-indigo-600">{mcpStreamProgress}%</span>
                  </div>

                  {/* Progress Bar */}
                  <div className="w-full bg-slate-100 h-2 rounded-full overflow-hidden mb-4 border border-slate-200">
                    <div
                      className="h-full bg-gradient-to-r from-indigo-500 via-blue-500 to-violet-500 transition-all duration-300"
                      style={{ width: `${mcpStreamProgress}%` }}
                    />
                  </div>

                  {/* Stream logs */}
                  <div className="flex-1 bg-slate-900 rounded-xl p-4 border border-slate-800 font-mono text-xs overflow-y-auto max-h-[360px] space-y-2">
                    {mcpStreamLogs.length === 0 ? (
                      <div className="h-full flex items-center justify-center text-slate-500 text-xs">
                        Awaiting MCP SSE Stream trigger...
                      </div>
                    ) : (
                      mcpStreamLogs.map((item, idx) => (
                        <div key={idx} className="text-slate-300 leading-relaxed">
                          <span className="text-indigo-400 font-semibold">[{new Date().toLocaleTimeString()}]</span>{' '}
                          <span className="text-amber-300 font-semibold">{item.status || item.event || 'EVENT'}:</span>{' '}
                          <span className="text-slate-200">{JSON.stringify(item)}</span>
                        </div>
                      ))
                    )}
                  </div>
                </div>
              </div>
            </div>
          </div>
        )}

        {/* ===================== TAB 6: ACTIVITY LOGS & CONSOLE ===================== */}
        {activeTab === 'logs' && (
          <div className="space-y-6 animate-in fade-in duration-300">
            <div className="surface-card rounded-2xl p-6">
              <div className="flex items-center justify-between pb-4 border-b border-slate-100 mb-4">
                <div className="flex items-center gap-2.5">
                  <div className="w-9 h-9 rounded-xl bg-slate-100 border border-slate-200 flex items-center justify-center">
                    <Terminal className="w-4 h-4 text-indigo-600" />
                  </div>
                  <div>
                    <h2 className="text-base font-bold text-slate-900">System Runtime Terminal</h2>
                    <p className="text-xs text-slate-500">Live feed of all agent executions, API handshakes, and tasks</p>
                  </div>
                </div>

                <button
                  onClick={() => setLogs([])}
                  className="btn-secondary px-3 py-1.5 rounded-lg text-xs font-medium transition"
                >
                  Clear Console
                </button>
              </div>

              <div className="bg-slate-900 rounded-xl p-4 border border-slate-800 font-mono text-xs overflow-y-auto max-h-[500px] space-y-2">
                {logs.length === 0 ? (
                  <div className="py-8 text-center text-slate-500">No logs captured in current session.</div>
                ) : (
                  logs.map((log) => (
                    <div key={log.id} className="flex items-start gap-2.5 leading-relaxed">
                      <span className="text-slate-500 select-none">[{log.time}]</span>
                      <span
                        className={`font-semibold uppercase text-[10px] px-1 rounded ${
                          log.type === 'success'
                            ? 'bg-emerald-500/20 text-emerald-300'
                            : log.type === 'error'
                            ? 'bg-rose-500/20 text-rose-300'
                            : log.type === 'warning'
                            ? 'bg-amber-500/20 text-amber-300'
                            : 'bg-indigo-500/20 text-indigo-300'
                        }`}
                      >
                        {log.type}
                      </span>
                      <span className="text-slate-200">{log.message}</span>
                    </div>
                  ))
                )}
                <div ref={logsEndRef} />
              </div>
            </div>
          </div>
        )}
      </main>

      {/* Footer */}
      <footer className="border-t border-slate-200 bg-white py-5 mt-auto">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 flex flex-col sm:flex-row items-center justify-between gap-3 text-xs text-slate-500">
          <div className="flex items-center gap-2.5">
            <AegisLogo size="xs" />
            <span className="font-bold tracking-wider uppercase text-slate-700">AEGIS AI v2.0</span>
            <span className="text-slate-300">•</span>
            <span className="text-slate-500">Autonomous Outreach Platform</span>
          </div>
          <div className="flex items-center gap-4">
            <span className="text-slate-500">
              Repository:{' '}
              <a
                href="https://github.com/vanillafeisty/aegis"
                target="_blank"
                rel="noreferrer"
                className="text-indigo-600 hover:text-indigo-700 hover:underline font-mono"
              >
                vanillafeisty/aegis
              </a>
            </span>
          </div>
        </div>
      </footer>
    </div>
  );
}
