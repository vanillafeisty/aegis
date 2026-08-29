# 🚀 Aegis AI v2.0 - Quick Start Guide

## ⚡ Get Running in 5 Minutes

### Step 1: Extract Zip File
```bash
unzip aegis-complete-v2.zip
cd aegis-fixed
```

### Step 2: Backend Setup (Terminal 1)
```bash
cd backend

# Install dependencies
pip install -r requirements.txt

# Start backend
python app.py
```

You should see:
```
🚀 Starting Aegis AI Agent Platform v2.0.0
📡 Groq LLM: llama-3.3-70b-versatile
🔗 Zapier MCP: Enabled with HTTP streaming
📧 SMTP: Gmail configured
🌐 CORS: Enabled for localhost:3000
```

### Step 3: Frontend Setup (Terminal 2)
```bash
cd frontend

# Install dependencies (first time only)
npm install

# Start frontend
npm run dev
```

### Step 4: Open Browser
```
http://localhost:3000
```

**✅ Done! Dashboard should load**

---

## 📋 What You Get

### Backend (`backend/`)
- ✅ `app.py` - FastAPI server with MCP streaming
- ✅ `agent_post.py` - LinkedIn posting
- ✅ `agent_connect.py` - Send connections
- ✅ `agent_email.py` - Send emails
- ✅ `agent_profile_tweak.py` - AI profile optimization
- ✅ `ai_agent_inbox.py` - Process messages
- ✅ `requirements.txt` - Python dependencies
- ✅ `.env` - Credentials (already populated with your data)
- ✅ `.env.example` - Template file

### Frontend (`frontend/`)
- ✅ `app/page.tsx` - Main dashboard
- ✅ `app/layout.tsx` - Root layout
- ✅ `app/globals.css` - Styling
- ✅ `package.json` - Node dependencies
- ✅ `next.config.js` - Next.js configuration
- ✅ `.env.local` - Frontend config

### Documentation
- ✅ `README.md` - Complete guide
- ✅ `DEPLOYMENT.md` - Production deployment
- ✅ `ANALYSIS_AND_SETUP.md` - Deep dive explanation

---

## 🔑 Your Credentials Are Ready

Your `.env` file already contains:

```
✅ LINKEDIN_SESSION_COOKIE = your_linkedin_session_cookie
✅ LINKEDIN_CLIENT_ID = your_linkedin_client_id
✅ LINKEDIN_ACCESS_TOKEN = your_linkedin_access_token
✅ GROQ_API_KEY = your_groq_api_key
✅ GROQ_MODEL = llama-3.3-70b-versatile
✅ SMTP_EMAIL = your_email@domain.com
✅ SMTP_PASSWORD = your_smtp_password
✅ ZAPIER_MCP_URL = https://mcp.zapier.com/api/v1/connect
```

---

## 🎯 Next Steps

### 1. Test LinkedIn Posting
- Go to Dashboard → LinkedIn tab
- Write a test post
- Click "🚀 Post"
- Check your LinkedIn feed!

### 2. Test Email
- Go to Dashboard → Email tab
- Enter recipient email
- Add subject and message
- Click "✉️ Send Email"
- Check inbox!

### 3. Test Zapier Integration
- Go to Dashboard → Zapier tab
- Enter workflow ID
- Add JSON data
- Click "🌊 Stream"
- See real-time streaming response!

### 4. Optimize Your Profile
- Go to Dashboard → LinkedIn tab
- Click "✨ Optimize Profile"
- Get AI-generated headline and bio suggestions

### 5. Process Inbox
- Go to Dashboard → LinkedIn tab
- Click "📬 Process Inbox"
- AI will reply to your recent messages

---

## 🔧 Troubleshooting

### "Backend not responding"
```bash
# Check backend is running
# Terminal should show: INFO: Uvicorn running on http://0.0.0.0:8000

# Kill and restart
# Ctrl+C in backend terminal
# Run: python app.py
```

### "LinkedIn session expired"
- Get fresh session cookie from Chrome DevTools
- Update `backend/.env`
- Restart backend

### "Port 8000 already in use"
```bash
# Linux/Mac
lsof -i :8000
kill -9 <PID>

# Windows
netstat -ano | findstr :8000
taskkill /PID <PID> /F
```

### "npm: command not found"
- Install Node.js from nodejs.org
- Verify: `node --version` should show v20+

### "python: command not found"
- Install Python from python.org
- Verify: `python --version` should show 3.12+

---

## 🎓 Learning Path

1. **Start Here**: Open `http://localhost:3000` and test all buttons
2. **Understand Flow**: Read `ANALYSIS_AND_SETUP.md`
3. **Deep Dive**: Read `README.md` for full documentation
4. **Deploy**: Follow `DEPLOYMENT.md` when ready for production

---

## 📡 API Endpoints

All endpoints are available at `http://localhost:8000`

### View API Docs
- Open `http://localhost:8000/` for full API documentation
- Or `http://localhost:8000/docs` for Swagger UI (if enabled)

### Key Endpoints
- `POST /agents/post` - LinkedIn posting
- `POST /agents/connect` - Send connections
- `POST /agents/email` - Send emails
- `POST /agents/profile` - Profile optimization
- `POST /agents/inbox` - Process inbox
- `POST /agents/zapier/stream` - Zapier MCP streaming

---

## 🌟 Advanced Features

### MCP Streaming
For large responses or long-running tasks, use the streaming endpoint:

```bash
curl -X POST http://localhost:8000/agents/zapier/stream \
  -H "Content-Type: application/json" \
  -d '{"workflow_id": "wf_123", "data": {"key": "value"}}'
```

Response comes in chunks:
```json
{"status":"started","workflow_id":"wf_123"}
{"progress":"10%","data":{...}}
{"progress":"50%","data":{...}}
{"status":"completed","workflow_id":"wf_123"}
```

### Async Processing
All endpoints are fully async for non-blocking I/O. Multiple requests run in parallel!

---

## 🔐 Security Notes

⚠️ **Important**: Never commit `.env` file to GitHub

```bash
# Add to .gitignore (already done)
echo ".env" >> .gitignore
```

For production:
- Use environment variables on cloud platform (Render, Heroku, etc.)
- Never expose `.env` file in repositories
- Rotate credentials regularly
- LinkedIn cookies expire every 30-60 days

---

## 📚 Full Documentation

For complete documentation, see:
- `README.md` - Full guide with architecture
- `DEPLOYMENT.md` - Production deployment
- `ANALYSIS_AND_SETUP.md` - Deep technical analysis

---

## 🆘 Still Having Issues?

1. Check that Python 3.12+ is installed: `python --version`
2. Check that Node 20+ is installed: `node --version`
3. Ensure both backends are running in separate terminals
4. Check `.env` file has all credentials
5. Look at terminal output for error messages
6. Try restarting both backend and frontend

---

**Happy automating! 🎉**

**Version**: 2.0.0 | **MCP Streaming**: Enabled | **Status**: Production Ready
