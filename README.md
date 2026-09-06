# 🛡️ Aegis AI v2.0 - LinkedIn & Email Automation Agent

**Autonomous AI agent for LinkedIn and email automation with MCP streaming capabilities**

---

## 📋 Table of Contents

1. [Quick Start](#quick-start)
2. [Architecture](#architecture)
3. [Features](#features)
4. [Setup Guide](#setup-guide)
5. [API Endpoints](#api-endpoints)
6. [Environment Variables](#environment-variables)
7. [Deployment](#deployment)
8. [Troubleshooting](#troubleshooting)

---

## 🚀 Quick Start

### Prerequisites
- Python 3.12+
- Node.js 20+
- Chrome/Chromium browser (for Playwright)

### Local Development (5 minutes)

```bash
# 1. Backend Setup
cd backend
python -m pip install -r requirements.txt
python app.py

# 2. Frontend Setup (new terminal)
cd frontend
npm install
npm run dev

# 3. Open Browser
http://localhost:3000
```

**✅ You're ready to automate!**

---

## 🏗️ Architecture

### System Components

```
┌─────────────────────────────────────────────────────────────┐
│                   FRONTEND (Next.js 14)                     │
│              http://localhost:3000                          │
│  • Modern React UI with TypeScript                          │
│  • Real-time API communication                              │
│  • Dark theme with cyan/purple gradient                     │
└─────────────────────────────────────────────────────────────┘
                          ↓ ↑
           ════════════════════════════════
              HTTP REST API + MCP Stream
           ════════════════════════════════
                          ↓ ↑
┌─────────────────────────────────────────────────────────────┐
│                   BACKEND (FastAPI)                         │
│              http://localhost:8000                          │
│  • 11 REST Endpoints                                        │
│  • MCP Streaming HTTP Transport                             │
│  • Async/Await Architecture                                │
│  • Comprehensive Error Handling                             │
│  • Environment Validation                                   │
└─────────────────────────────────────────────────────────────┘
    ↓              ↓               ↓              ↓
┌─────────┐  ┌──────────┐  ┌───────────┐  ┌───────────────┐
│Playwright│  │Groq LLM  │  │SMTP Email │  │Zapier MCP API│
│(Local)   │  │(Free API)│  │(Gmail)    │  │(HTTP Stream) │
└─────────┘  └──────────┘  └───────────┘  └───────────────┘
    ↓
┌─────────────────────┐
│  LinkedIn Platform  │
│  • Posting          │
│  • Connections      │
│  • Messaging        │
│  • Profile Edit     │
└─────────────────────┘
```

### Data Flow

```
1. USER ACTION (Frontend)
   ↓
2. HTTP REQUEST (API Call)
   ↓
3. BACKEND PROCESSING
   ├─ Auth validation
   ├─ Agent orchestration
   └─ Integration execution
   ↓
4. MCP COMMUNICATION (Optional)
   ├─ HTTP Streaming for large responses
   ├─ Chunked transfer encoding
   └─ Zapier workflow execution
   ↓
5. RESPONSE (JSON or Streaming)
   ↓
6. FRONTEND UPDATE (UI)
```

---

## ✨ Features

### LinkedIn Automation
- ✅ **Post to Feed** - Share content with AI-generated captions
- ✅ **Send Connections** - Automate connection requests
- ✅ **Process Inbox** - AI-powered message replies
- ✅ **Profile Optimization** - Groq-generated headlines and bios

### Email Automation
- ✅ **Send Emails** - SMTP integration with Gmail
- ✅ **Template Support** - Customizable email bodies
- ✅ **Rich Text** - HTML email support (add later)

### AI Integration
- ✅ **Groq LLM** - Free API with llama-3.3-70b-versatile
- ✅ **Real-time Generation** - Instant content creation
- ✅ **Intelligent Replies** - Context-aware responses

### Zapier Integration
- ✅ **MCP Streaming** - HTTP chunked transfer for large payloads
- ✅ **Workflow Execution** - Trigger any Zapier automation
- ✅ **Event Streaming** - Real-time webhook integration

### Developer Features
- ✅ **Type Safety** - Full TypeScript support
- ✅ **Async/Await** - Non-blocking operations
- ✅ **Error Handling** - Comprehensive error messages
- ✅ **Logging** - Detailed operation logs
- ✅ **CORS Enabled** - Development-ready

---

## 🛠️ Setup Guide

### 1. Get API Keys

**Groq LLM (Free)**
1. Go to https://console.groq.com/keys
2. Create new API key
3. Copy `GROQ_API_KEY`

**Gmail SMTP**
1. Enable 2-Factor Authentication
2. Go to https://myaccount.google.com/apppasswords
3. Generate App Password
4. Copy `SMTP_PASSWORD`

**LinkedIn Credentials**
1. Open LinkedIn in Chrome
2. Press F12 → Application → Cookies
3. Find `li_at` cookie and copy the value
4. Paste to `LINKEDIN_SESSION_COOKIE`

**Zapier MCP (Optional)**
1. Create Zapier account at zapier.com
2. Get your MCP endpoint
3. Add to `ZAPIER_MCP_URL`

### 2. Configure Backend

```bash
cd backend
cp .env.example .env
```

Edit `.env` with your credentials:

```env
# Required
GROQ_API_KEY=gsk_...
SMTP_EMAIL=your@email.com
SMTP_PASSWORD=your-app-password

# Optional but recommended
LINKEDIN_SESSION_COOKIE=AQED...
LINKEDIN_CLIENT_ID=your-client-id
ZAPIER_MCP_URL=https://mcp.zapier.com/api/v1
```

### 3. Install Dependencies

**Backend:**
```bash
cd backend
pip install -r requirements.txt
```

**Frontend:**
```bash
cd frontend
npm install
```

### 4. Run Application

**Terminal 1 - Backend:**
```bash
cd backend
python app.py
```

**Terminal 2 - Frontend:**
```bash
cd frontend
npm run dev
```

**Terminal 3 - Playwright Setup (First time only):**
```bash
cd backend
playwright install chromium
```

### 5. Access Dashboard

Open browser: **http://localhost:3000**

---

## 🔌 API Endpoints

### Authentication
```
GET    /auth/status         - Check authentication status
POST   /auth/login          - User login
POST   /auth/send-code      - Send verification code
POST   /auth/verify-code    - Verify code
```

### Credentials
```
GET    /credentials/status  - Current credentials status
POST   /credentials/set     - Update credentials
```

### Agents (LinkedIn/Email)
```
POST   /agents/post         - Post to LinkedIn
POST   /agents/connect      - Send connection request
POST   /agents/inbox        - Process messages
POST   /agents/profile      - Optimize profile
POST   /agents/email        - Send email
```

### Zapier (MCP Streaming)
```
POST   /agents/zapier       - Execute workflow (regular)
POST   /agents/zapier/stream - Execute workflow (HTTP streaming)
```

### Utility
```
GET    /                    - API documentation
GET    /health              - Health check
```

---

## 📝 Environment Variables

### Required
```env
GROQ_API_KEY          # Groq LLM API key (free from console.groq.com)
GROQ_MODEL            # Model name (default: llama-3.3-70b-versatile)
SMTP_EMAIL            # Gmail address for sending emails
SMTP_PASSWORD         # Gmail App Password (NOT regular password)
```

### Optional
```env
LINKEDIN_SESSION_COOKIE   # LinkedIn session (expires every 30 days)
LINKEDIN_ACCESS_TOKEN     # LinkedIn OAuth token
LINKEDIN_CLIENT_ID        # LinkedIn app ID
CLIENT_SECRET            # LinkedIn OAuth secret
REDIRECT_URI            # OAuth redirect URL
ZAPIER_MCP_URL         # Zapier MCP endpoint for streaming
```

### Development
```env
ENVIRONMENT            # 'development' or 'production'
DEBUG                  # True/False for debug logging
LOG_LEVEL              # 'INFO', 'DEBUG', 'WARNING', 'ERROR'
```

---

## 🌐 Deployment

### Deploy to Render (Backend)

```bash
# 1. Push to GitHub
git push origin main

# 2. Connect GitHub repo to Render
# https://render.com/

# 3. Create Web Service
# • Runtime: Python 3.12
# • Build: pip install -r requirements.txt
# • Start: python app.py
# • Port: 8000

# 4. Add Environment Variables
# - GROQ_API_KEY=...
# - SMTP_EMAIL=...
# - SMTP_PASSWORD=...
# - ZAPIER_MCP_URL=...
# (Don't add Playwright vars - use local machine instead)
```

### Deploy to Vercel (Frontend)

```bash
# 1. Push to GitHub

# 2. Connect GitHub repo to Vercel
# https://vercel.com/

# 3. Environment Variables
# NEXT_PUBLIC_API_URL=https://aegis-backend.onrender.com

# 4. Deploy!
```

### LinkedIn Automation Locally

Keep browser automation on local machine:

```bash
# Local machine only
python app.py

# Call remote backend for other tasks
# Frontend connects to:
# - Local Playwright (localhost:8000/agents/post)
# - Remote Groq, Email, Zapier (via API)
```

---

## 🐛 Troubleshooting

### "LinkedIn credentials not set"
- LinkedIn session cookies expire every 30-60 days
- Get fresh `li_at` cookie from Chrome DevTools
- Update `.env` file
- Restart backend

### "SMTP authentication failed"
- Ensure you're using Gmail App Password, NOT regular password
- Enable 2-Factor Authentication
- Generate new App Password
- Update `.env` file

### "Playwright timeout on connect"
- LinkedIn UI changes occasionally
- Check if Playwright can open chrome
- Run: `playwright install chromium`
- Session cookie might be expired

### "Groq API quota exceeded"
- Free tier has limits
- Wait 24 hours for quota reset
- Or upgrade to paid Groq plan

### "Port 3000/8000 already in use"
```bash
# Find process using port
lsof -i :8000
lsof -i :3000

# Kill process
kill -9 <PID>

# Or change port
# Backend: python app.py --port 8001
# Frontend: npm run dev -- -p 3001
```

### "CORS error when calling API"
- Ensure backend is running on http://localhost:8000
- Frontend on http://localhost:3000
- CORS is enabled in app.py for both URLs

---

## 📚 Architecture Deep Dive

### MCP Streaming HTTP Transport

Aegis v2.0 implements **HTTP chunked transfer encoding** for streaming responses:

```python
# Non-streaming (Regular)
POST /agents/zapier
Response: { "status": "success", "result": {...} }

# Streaming (Large payloads)
POST /agents/zapier/stream
Response: (HTTP 200)
Line 1: {"status":"started","workflow_id":"wf_123"}
Line 2: {"progress":"10%","data":{...}}
Line 3: {"progress":"50%","data":{...}}
Line 4: {"progress":"100%","data":{...}}
Line 5: {"status":"completed","workflow_id":"wf_123"}
```

### Async/Await Pattern

All endpoints are fully async for non-blocking I/O:

```python
@app.post("/agents/email")
async def send_email(request: EmailRequest):
    # Non-blocking SMTP
    result = await agent_email.send_email(...)
    return result
```

### Error Handling Strategy

```python
try:
    result = await execute_agent(...)
except SpecificError as e:
    logger.error(f"Specific error: {e}")
    raise HTTPException(status_code=400, detail=str(e))
except Exception as e:
    logger.error(f"Unexpected error: {e}")
    raise HTTPException(status_code=500, detail=str(e))
```

---

## 🤝 Contributing

Want to extend Aegis?

1. **Add New Agent**: Create `agent_name.py` with async functions
2. **Add New Endpoint**: Add route in `app.py`
3. **Add Frontend Panel**: Update `page.tsx` with new tab
4. **Test**: Run backend and frontend locally

---

## 📄 License

MIT License - Use freely for personal and commercial projects

---

## 🆘 Support

Having issues? Check:
1. `.env` file has all required variables
2. Backend is running: `http://localhost:8000/health`
3. Frontend can reach backend: Check browser console
4. LinkedIn session cookie is fresh (< 30 days)
5. Groq API key is active

---

**Built with ❤️ for LinkedIn automation enthusiasts**

**v2.0.0** | MCP Streaming | Groq AI | Playwright | Zapier Integration
