# 🚀 Aegis AI v2.0 - Deployment Guide

---

## Local Development to Production

### Step 1: Prepare for Deployment

```bash
# Ensure all dependencies are in requirements.txt
pip freeze > requirements.txt

# Build frontend
cd frontend
npm run build
npm start  # Test build locally

# Backend production setup
cd backend
export NODE_ENV=production
```

### Step 2: Choose Deployment Strategy

#### Option A: Local Machine + Cloud Backend

**Best for**: LinkedIn automation needs (Playwright runs locally)

```
Local Machine (Your Computer)
├─ Python FastAPI Backend (with Playwright)
│  ├─ LinkedIn posting ✅
│  ├─ Connections ✅
│  └─ Inbox processing ✅
└─ Runs 24/7 on Windows/Mac/Linux

Cloud (Render + Vercel)
├─ Frontend (Vercel)
│  └─ React UI
└─ API Layer (Render) - NO Playwright
    ├─ Email (SMTP) ✅
    ├─ Groq AI ✅
    └─ Zapier MCP ✅
```

**Setup:**
```bash
# Local Machine
cd backend
python app.py

# Cloud Frontend
# Deploy to Vercel
# Set NEXT_PUBLIC_API_URL=http://localhost:8000

# This requires port forwarding to expose local backend to web
# NOT RECOMMENDED for production security
```

#### Option B: Fully Cloud (Recommended)

**Best for**: Production + security

```
Render (Backend without Playwright)
├─ Groq AI ✅
├─ Email ✅
└─ Zapier MCP ✅

Vercel (Frontend)
├─ React UI
└─ Connect to Render

Local Machine
└─ Only for testing

LinkedIn Automation
└─ Uses Zapier MCP + webhooks instead of Playwright
```

#### Option C: Docker Containerization

**Best for**: Scalability

```dockerfile
# backend/Dockerfile
FROM python:3.12-slim
WORKDIR /app
COPY requirements.txt .
RUN pip install -r requirements.txt
COPY . .
CMD ["python", "app.py"]

# frontend/Dockerfile
FROM node:20-alpine
WORKDIR /app
COPY package*.json .
RUN npm ci
COPY . .
RUN npm run build
CMD ["npm", "start"]
```

---

## Production Deployment: Render.com

### 1. Prepare Repository

```bash
# Create deployable backend folder
aegis-backend/
├─ app.py
├─ agent_*.py
├─ requirements.txt
├─ .env.example
└─ Procfile

# Procfile content:
# web: python app.py
```

### 2. Render Web Service Setup

**Step 1: Connect GitHub**
- Go to render.com
- Connect your GitHub repository

**Step 2: Create Web Service**
```
Name:               aegis-api
Repository:         your-github-repo
Branch:             main
Runtime:            Python 3.12
Build Command:      pip install -r requirements.txt
Start Command:      python app.py
Plan:               Free or Starter
Port:               8000
```

**Step 3: Environment Variables**
```env
GROQ_API_KEY=gsk_...
GROQ_MODEL=llama-3.3-70b-versatile
SMTP_EMAIL=your@gmail.com
SMTP_PASSWORD=app-password
ZAPIER_MCP_URL=https://mcp.zapier.com/api/v1
ENVIRONMENT=production
DEBUG=False
LOG_LEVEL=INFO
```

**⚠️ Important:**
- DO NOT add `LINKEDIN_SESSION_COOKIE` on Render
- LinkedIn automation stays on local machine
- Use Zapier MCP webhooks as fallback

### 3: Vercel Frontend Deployment

**Step 1: Build Locally**
```bash
cd frontend
npm run build
```

**Step 2: Push to GitHub**
```bash
git add .
git commit -m "Production ready v2.0"
git push origin main
```

**Step 3: Deploy to Vercel**
```
1. Go to vercel.com
2. Click "New Project"
3. Select your GitHub repo
4. Configure:
   - Framework: Next.js
   - Root Directory: frontend
   - Build Command: npm run build
   - Output Directory: .next
5. Environment Variables:
   NEXT_PUBLIC_API_URL=https://aegis-api.onrender.com
6. Deploy!
```

---

## Hybrid Deployment: Local + Cloud

For LinkedIn automation on local machine while API runs on cloud:

### 1. Separate Codebase

```
aegis-cloud/
├─ backend/
│  ├─ app.py (NO Playwright imports)
│  ├─ agent_email.py ✅
│  ├─ agent_profile_tweak.py ✅
│  └─ requirements.txt
└─ frontend/
   └─ all files

aegis-local/
└─ backend/
   ├─ app.py (WITH Playwright)
   └─ agent_post.py ✅
   └─ agent_connect.py ✅
```

### 2. Environment Configuration

**Cloud (.env)**
```env
GROQ_API_KEY=...
SMTP_EMAIL=...
ZAPIER_MCP_URL=...
# NO LinkedIn or Playwright
```

**Local (.env)**
```env
LINKEDIN_SESSION_COOKIE=...
LINKEDIN_CLIENT_ID=...
# Full setup for browser automation
```

### 3. Run Local Backend

```bash
cd aegis-local/backend

# Install with Playwright
pip install -r requirements.txt

# Run
python app.py

# Keep terminal open 24/7 for automation
# Use screen/tmux for persistent sessions
```

### 4. Frontend Points to Cloud

```env
NEXT_PUBLIC_API_URL=https://aegis-api.onrender.com
```

### 5. Local Webhook Handling

For LinkedIn events that need local execution:

```python
# Cloud receives webhook
@app.post("/webhooks/linkedin")
async def linkedin_webhook(data: Dict):
    # Process on cloud if possible
    # OR
    # Send to local machine via ngrok
    requests.post("https://local-tunnel.ngrok.io/process", json=data)
    return {"status": "queued"}
```

**Local ngrok tunnel:**
```bash
ngrok http 8000
# Expose local backend to cloud webhooks
```

---

## Production Checklist

### Before Deploying

- [ ] All `.env` variables set
- [ ] `requirements.txt` updated
- [ ] No hardcoded secrets in code
- [ ] CORS configured correctly
- [ ] Error handling complete
- [ ] Logging enabled
- [ ] `.gitignore` excludes `.env`
- [ ] README updated
- [ ] Tests passing

### After Deploying

- [ ] Test all endpoints: `https://api-url/health`
- [ ] Test auth flow
- [ ] Test email sending
- [ ] Test Zapier integration
- [ ] Monitor logs for errors
- [ ] Set up alerts for failures
- [ ] Test user workflow end-to-end

### Maintenance

```bash
# Monitor
curl https://api-url/health

# Update dependencies
pip install --upgrade -r requirements.txt

# Rotate credentials
# LinkedIn session: Get fresh cookie
# Groq key: Regenerate if needed
# Gmail password: Change if account compromised

# Backup data
# Daily export of credentials
# Weekly database backups
```

---

## Cost Analysis

### Render.com
- **Free tier**: 
  - 750 hours/month
  - Spins down after 15 min inactivity
  - Good for testing
  
- **Starter**: 
  - $7/month
  - 24/7 uptime
  - 0.5GB RAM
  - Recommended for production

### Vercel
- **Free**: Unlimited deployments, fast CDN
- **Pro**: $20/month if you need more

### Groq API
- **Free tier**: 
  - Limits: ~30 requests/minute
  - Good for low-volume automation
  
- **Paid**: 
  - $0.20/million tokens
  - Unlimited requests

### Total Monthly Cost
- **Minimum**: $7 (Render Starter only)
- **Recommended**: $27 (Render + Vercel Pro + Groq)
- **With high-volume Groq**: $50-100

---

## Troubleshooting Production

### "Backend not responding"
```bash
# Check Render logs
# https://dashboard.render.com/

# Restart service
# Settings → Manual Deploy

# Check environment variables
# Are all required vars set?
```

### "LinkedIn automation not working"
- Render doesn't have Playwright
- Keep local machine running with `python app.py`
- Use Zapier webhooks as fallback

### "CORS errors in production"
```python
# Update CORS in app.py
app.add_middleware(
    CORSMiddleware,
    allow_origins=[
        "https://aegis-frontend.vercel.app",  # Your Vercel URL
        "http://localhost:3000"  # Local dev
    ],
)
```

### "502 Bad Gateway"
- Backend crashed
- Check Render logs
- Restart service
- Check environment variables

### "Email sending fails"
- Gmail password might have changed
- 2FA needs to be enabled
- Generate new App Password
- Update Render environment variables

---

## Scaling Aegis

### For 1000+ Automations/Day

1. **Add Task Queue**
   - Use Celery + Redis
   - Queue long-running tasks

2. **Database**
   - Store automation history
   - PostgreSQL on Render

3. **Caching**
   - Redis for session data
   - Reduce API calls

4. **Load Balancing**
   - Multiple Render instances
   - Render automatically handles

---

**Ready to deploy?** 🚀 Good luck!
