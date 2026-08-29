# 📦 Aegis AI v2.0 - Project Structure

## File Organization

```
aegis-fixed/
│
├── 📄 README.md                    # Main documentation
├── 📄 QUICK_START.md              # 5-minute quick start guide
├── 📄 SETUP_GUIDE.md              # Complete setup for all OS
├── 📄 DEPLOYMENT.md               # Production deployment guide
├── 📄 ANALYSIS_AND_SETUP.md       # Technical deep dive
├── 📄 .gitignore                  # Git ignore file
│
├── 🪟 RUN_BACKEND.bat             # Windows backend launcher
├── 🪟 RUN_FRONTEND.bat            # Windows frontend launcher
├── 🐧 run_backend.sh              # Linux/Mac backend launcher
├── 🐧 run_frontend.sh             # Linux/Mac frontend launcher
│
├── 📁 backend/
│   ├── 🐍 app.py                  # FastAPI main application (11 endpoints)
│   ├── 🐍 mcp_server.py           # MCP server with HTTP streaming transport
│   ├── 🐍 agent_post.py           # LinkedIn post agent (Playwright)
│   ├── 🐍 agent_connect.py        # Connection request agent (Playwright)
│   ├── 🐍 agent_email.py          # Email sending agent (SMTP/Gmail)
│   ├── 🐍 agent_profile_tweak.py  # Profile optimization agent (Groq LLM)
│   ├── 🐍 ai_agent_inbox.py       # Inbox processing agent (Playwright + Groq)
│   ├── 📋 requirements.txt         # Python dependencies
│   ├── 🔐 .env                    # Environment variables (populated)
│   └── 🔐 .env.example            # Environment template
│
├── 📁 frontend/
│   ├── 📁 app/
│   │   ├── 📄 page.tsx            # Main dashboard component
│   │   ├── 📄 layout.tsx          # Root layout
│   │   └── 📄 globals.css         # Global styles
│   ├── 📋 package.json            # Node.js dependencies
│   ├── ⚙️  next.config.js          # Next.js configuration
│   └── 🔐 .env.local              # Frontend environment config
│
└── 📁 docs/ (optional)
    ├── API_REFERENCE.md           # Complete API documentation
    ├── ARCHITECTURE.md            # System architecture
    └── TROUBLESHOOTING.md         # Troubleshooting guide
```

---

## File Descriptions

### 📋 Documentation Files

#### README.md
- Complete project overview
- Architecture diagram
- All 10+ features explained
- API endpoint reference
- Environment variables guide
- Deployment instructions
- Troubleshooting section

#### QUICK_START.md
- 5-minute setup guide
- Quick test procedures
- Common issues and fixes
- Next steps

#### SETUP_GUIDE.md
- Step-by-step setup for Windows, macOS, Linux
- API key generation instructions
- Port conflict resolution
- Keep-running-24/7 guide
- Comprehensive troubleshooting

#### DEPLOYMENT.md
- Production deployment strategies
- Local + Cloud hybrid setup
- Docker containerization
- Cost analysis
- Scaling guide

#### ANALYSIS_AND_SETUP.md
- Project analysis
- What's working vs what needs fixing
- Flow architecture diagrams
- Credentials setup details
- Step-by-step execution flow

### 🐍 Backend Files

#### app.py (900+ lines)
**Main FastAPI application**

Includes:
- ✅ 11 REST endpoints
- ✅ MCP streaming HTTP transport
- ✅ CORS middleware setup
- ✅ Environment validation
- ✅ Comprehensive error handling
- ✅ Request/response models (Pydantic)
- ✅ Health checks and logging
- ✅ Async/await throughout
- ✅ Authentication endpoints
- ✅ Credential management
- ✅ All agent orchestration

Endpoints:
```
GET    /                     - API documentation
GET    /health              - Health check
GET    /auth/status         - Authentication status
POST   /auth/login          - User login
POST   /auth/send-code      - Send verification code
POST   /auth/verify-code    - Verify code
GET    /credentials/status  - Credentials status
POST   /credentials/set     - Update credentials
POST   /agents/post         - Post to LinkedIn
POST   /agents/connect      - Send connections
POST   /agents/email        - Send emails
POST   /agents/profile      - Optimize profile
POST   /agents/inbox        - Process inbox
POST   /agents/zapier       - Execute Zapier workflow
POST   /agents/zapier/stream - Stream workflow execution
```

#### mcp_server.py (600+ lines)
**MCP Protocol Implementation**

Includes:
- ✅ MCP message protocol types
- ✅ Tool definitions (5 tools)
- ✅ HTTP streaming transport
- ✅ Async/await support
- ✅ Request/response handling
- ✅ Error management
- ✅ Example usage

Tools:
- `create_workflow` - Create Zapier workflow
- `execute_workflow` - Execute existing workflow
- `list_workflows` - List all workflows
- `send_email` - Send via Gmail
- `post_to_slack` - Post to Slack

#### agent_post.py
**LinkedIn Posting Agent**

Features:
- Posts to LinkedIn feed using Playwright
- Handles session cookies
- Full error handling
- Simulated mode for cloud environments
- Type hints and logging

#### agent_connect.py
**Connection Request Agent**

Features:
- Sends connection requests
- Optional personalized message
- Handles LinkedIn UI automation
- Fallback error handling

#### agent_email.py
**Email Sending Agent**

Features:
- SMTP/Gmail integration
- Full email support (To, Subject, Body)
- Error handling for auth failures
- Async support

#### agent_profile_tweak.py
**Profile Optimization Agent**

Features:
- Uses Groq LLM for AI generation
- Generates optimized headline
- Generates optimized bio
- Provides recommendations
- Free API tier compatible

#### ai_agent_inbox.py
**Inbox Processing Agent**

Features:
- Monitors LinkedIn messages
- AI-powered replies using Groq
- Auto-reply functionality
- Async processing

#### requirements.txt
```
fastapi==0.115.0
uvicorn[standard]==0.30.6
python-dotenv==1.0.1
groq==0.11.0
pydantic==2.9.2
httpx==0.28.0
playwright==1.48.0
```

#### .env
**Populated with your credentials:**
- ✅ LINKEDIN_SESSION_COOKIE
- ✅ LINKEDIN_CLIENT_ID
- ✅ LINKEDIN_ACCESS_TOKEN
- ✅ GROQ_API_KEY
- ✅ GROQ_MODEL
- ✅ SMTP_EMAIL
- ✅ SMTP_PASSWORD
- ✅ CLIENT_SECRET
- ✅ ZAPIER_MCP_URL

#### .env.example
**Template for reference**

### ⚛️ Frontend Files

#### page.tsx (1000+ lines)
**Main Dashboard Component**

Features:
- ✅ Modern dark theme UI
- ✅ Cyan/purple gradient design
- ✅ 4 main tabs: Dashboard, LinkedIn, Email, Zapier
- ✅ Real-time API communication
- ✅ Status indicators
- ✅ Form validation
- ✅ Error handling
- ✅ Responsive design

Functionality:
- Dashboard with status overview
- LinkedIn posting form
- Connection request form
- Email composition
- Profile optimization
- Inbox processing
- Zapier workflow execution
- Streaming response handling

#### layout.tsx
- Root layout for Next.js
- HTML metadata
- Font setup

#### globals.css
- Global styles
- Dark theme colors
- Typography
- Responsive utilities

#### package.json
```
react@18.3.1
react-dom@18.3.1
next@14.2.0
axios@1.7.0
tailwindcss@3.4.0
```

#### next.config.js
- Next.js configuration
- API rewrites for local development
- Environment variable setup

#### .env.local
```
NEXT_PUBLIC_API_URL=http://localhost:8000
NEXT_PUBLIC_APP_NAME=Aegis AI
NEXT_PUBLIC_VERSION=2.0.0
```

### 🪟 Launcher Scripts

#### RUN_BACKEND.bat
- Windows batch script
- Checks Python installation
- Installs dependencies
- Starts FastAPI server
- User-friendly error messages

#### RUN_FRONTEND.bat
- Windows batch script
- Checks Node.js installation
- Installs npm packages
- Starts Next.js dev server
- User-friendly error messages

#### run_backend.sh
- Linux/Mac shell script
- Creates Python virtual environment
- Installs dependencies
- Starts FastAPI server

#### run_frontend.sh
- Linux/Mac shell script
- Checks Node.js
- Installs npm packages
- Starts Next.js dev server

---

## Technology Stack

### Backend
- **Framework**: FastAPI 0.115.0
- **Server**: Uvicorn 0.30.6
- **LLM**: Groq API (Free - llama-3.3-70b-versatile)
- **Browser Automation**: Playwright 1.48.0
- **Email**: SMTP + Gmail
- **Async**: Python asyncio
- **Validation**: Pydantic 2.9.2
- **Environment**: python-dotenv 1.0.1
- **HTTP Client**: httpx 0.28.0

### Frontend
- **Framework**: Next.js 14.2.0
- **Library**: React 18.3.1
- **Language**: TypeScript 5.3.0
- **HTTP**: Axios 1.7.0
- **Styling**: Tailwind CSS 3.4.0
- **Package Manager**: npm 10.x

### Deployment
- **Backend**: Render.com (Python 3.12)
- **Frontend**: Vercel (Next.js)
- **Database**: Optional (PostgreSQL on Render)

---

## Key Features by Component

### Backend Features
- ✅ 11 REST endpoints with full documentation
- ✅ MCP server with HTTP streaming transport
- ✅ 5 automation agents
- ✅ LinkedIn automation (Playwright)
- ✅ Email automation (SMTP)
- ✅ AI-powered content generation (Groq)
- ✅ Zapier workflow integration
- ✅ Comprehensive error handling
- ✅ Request validation with Pydantic
- ✅ CORS enabled for development
- ✅ Full async/await support
- ✅ Environment validation at startup

### Frontend Features
- ✅ Modern dark theme dashboard
- ✅ Real-time API communication
- ✅ 4 main tabs with full functionality
- ✅ Form validation and error handling
- ✅ Status indicators
- ✅ Responsive design
- ✅ Type-safe React components
- ✅ Streaming response support
- ✅ Professional UI/UX

---

## Environment Variables

### Required (Backend)
```
GROQ_API_KEY        - Groq LLM API key (free from console.groq.com)
GROQ_MODEL          - Model name (llama-3.3-70b-versatile)
SMTP_EMAIL          - Gmail address for sending emails
SMTP_PASSWORD       - Gmail App Password (16 characters)
```

### Optional (Backend)
```
LINKEDIN_SESSION_COOKIE   - LinkedIn session (expires every 30 days)
LINKEDIN_ACCESS_TOKEN     - LinkedIn OAuth token
LINKEDIN_CLIENT_ID        - LinkedIn app ID
CLIENT_SECRET            - LinkedIn OAuth secret
REDIRECT_URI             - OAuth redirect URL
ZAPIER_MCP_URL          - Zapier MCP endpoint
```

### Frontend
```
NEXT_PUBLIC_API_URL      - Backend URL (http://localhost:8000)
NEXT_PUBLIC_APP_NAME     - App name (Aegis AI)
NEXT_PUBLIC_VERSION      - Version (2.0.0)
```

---

## Data Flow

```
1. User opens http://localhost:3000
2. Frontend loads dashboard
3. Frontend calls GET /auth/status
4. Backend returns credential status
5. User fills form (e.g., LinkedIn post)
6. Frontend sends POST /agents/post
7. Backend calls agent_post.py
8. Playwright opens LinkedIn
9. Posts content to feed
10. Returns success response
11. Frontend updates UI
12. User sees success message
```

---

## Deployment Architecture

### Local Development
```
http://localhost:3000  (Frontend - Next.js)
         ↓↑
http://localhost:8000  (Backend - FastAPI)
         ↓↑
    Local Resources
    - Playwright (LinkedIn)
    - Groq API (LLM)
    - Gmail SMTP (Email)
    - Zapier (Webhooks)
```

### Production (Recommended)
```
https://your-app.vercel.app  (Frontend - Vercel)
         ↓↑
https://your-app.onrender.com (Backend - Render)
         ↓↑
    Cloud Resources
    - Groq API (LLM)
    - Gmail SMTP (Email)
    - Zapier (Webhooks)

Your Local Machine
         ↓↑
LinkedIn Automation via Playwright
(Keep local machine running 24/7)
```

---

## File Sizes

```
Backend:
- app.py              ~25 KB
- mcp_server.py       ~20 KB
- agent_*.py files    ~15 KB each
- requirements.txt    ~0.2 KB
- .env               ~1 KB

Frontend:
- page.tsx           ~35 KB
- package.json       ~1 KB
- Other files        ~5 KB

Documentation:
- README.md          ~30 KB
- DEPLOYMENT.md      ~25 KB
- SETUP_GUIDE.md     ~20 KB
- ANALYSIS_AND_SETUP.md ~15 KB

Total (without node_modules/venv):
≈ 250 KB
```

---

## Version History

### v2.0.0 (Current)
- ✅ Complete rewrite with FastAPI
- ✅ MCP streaming HTTP transport
- ✅ 11 REST endpoints
- ✅ Full TypeScript frontend
- ✅ Production-ready deployment
- ✅ Comprehensive documentation
- ✅ Multi-platform support

---

## Support Files

Each major document is self-contained:
- **README.md** - Best for understanding the project
- **QUICK_START.md** - Best for getting started fast
- **SETUP_GUIDE.md** - Best for detailed setup instructions
- **DEPLOYMENT.md** - Best for production deployment
- **ANALYSIS_AND_SETUP.md** - Best for technical understanding

---

**Everything you need to run Aegis AI v2.0 is included!** 🚀
