# 🛡️ Aegis - Autonomous LinkedIn Intelligence Agent

Aegis is a sophisticated AI-powered automation platform that enables you to manage your LinkedIn and Gmail presence autonomously. With natural language commands, Aegis can post updates, send connection requests, reply to messages, optimize your profile, and manage emails—all powered by AI.

## 🌿 Design Theme

Aegis features a beautiful **botanical-inspired UI** with:
- **Sage green & dark green** color palette
- **Times New Roman bold** typography
- White backgrounds with subtle gradients
- Smooth animations and intuitive interactions

## ✨ Features

### LinkedIn Automation
- **📝 Intelligent Posting** - Publish content to your LinkedIn feed
- **🤝 Connection Requests** - Send personalized connection invitations
- **💬 Smart Inbox Replies** - AI-powered responses to messages
- **⚙️ Profile Optimization** - AI-enhanced headlines and about sections

### Email Management
- **📧 SMTP Email Sending** - Send emails via Gmail
- **🔒 Secure Authentication** - Safe credential handling

### AI-Powered Intelligence
- **🧠 Natural Language Understanding** - Chat naturally with Aegis
- **✨ Context-Aware Actions** - Smart automation based on your requests

## 🚀 Quick Start

### Prerequisites
- Python 3.10+
- Node.js 18+
- Git

### Backend Setup

1. **Navigate to backend directory:**
   ```bash
   cd aegis-complete
   ```

2. **Install Python dependencies:**
   ```bash
   pip install -r requirements.txt
   ```

3. **Install Playwright browsers:**
   ```bash
   playwright install chromium
   ```

4. **Configure environment variables:**
   ```bash
   cp .env.example .env  # Create from your existing .env
   ```

5. **Add your credentials to `.env`:**
   ```
   LINKEDIN_SESSION_COOKIE=your_cookie_here
   LINKEDIN_ACCESS_TOKEN=your_token_here
   LINKEDIN_CLIENT_ID=your_client_id
   OPENAI_API_KEY=your_openai_key
   SMTP_EMAIL=your_email@gmail.com
   SMTP_PASSWORD=your_app_password
   ```

6. **Start the FastAPI server:**
   ```bash
   python app.py
   ```
   The API will run at `http://localhost:8000`

### Frontend Setup

1. **Navigate to frontend directory:**
   ```bash
   cd frontend
   ```

2. **Install dependencies:**
   ```bash
   npm install
   ```

3. **Create `.env.local`:**
   ```bash
   echo "NEXT_PUBLIC_API_URL=http://localhost:8000" > .env.local
   ```

4. **Start development server:**
   ```bash
   npm run dev
   ```
   The frontend will run at `http://localhost:3000`

### Access Aegis

Open `http://localhost:3000` in your browser and follow the setup wizard to connect your credentials.

## 🔐 Getting Your Credentials

### LinkedIn Session Cookie & Access Token
1. Go to LinkedIn and log in
2. Open DevTools (F12)
3. Go to **Application → Cookies → linkedin.com**
4. Copy the value of `li_at` cookie (Session Cookie)

### Gmail App Password
1. Go to [Google Account Security](https://myaccount.google.com/security)
2. Enable 2-Step Verification if not already done
3. Generate an App Password for "Mail"
4. Use this 16-character password

### OpenAI API Key
1. Go to [OpenAI Platform](https://platform.openai.com/api-keys)
2. Create a new API key
3. Copy it to your `.env` file

## 📝 Using Aegis

Once setup is complete, interact with Aegis naturally:

### Post to LinkedIn
```
"Post: Just launched my new AI project! Check it out 🚀 #AI"
```

### Send Connection Request
```
"Connect to https://linkedin.com/in/john-doe"
```

### Check & Reply to Messages
```
"Check my messages"
```

### Optimize Profile
```
"Optimize my profile"
```

## 🏗️ Project Structure

```
aegis-complete/
├── app.py                     # FastAPI server (NEW!)
├── agent_post.py              # LinkedIn posting
├── agent_connect.py           # Connection requests
├── agent_email.py             # Email sending
├── agent_profile_tweak.py     # Profile optimization (FIXED!)
├── ai_agent_inbox.py          # Inbox replies
├── requirements.txt           # Python dependencies (NEW!)
├── .env                       # Your credentials
├── README.md                  # This file
│
└── frontend/                  # React/Next.js UI (NEW!)
    ├── app/
    │   ├── components/
    │   │   ├── ConnectionSetup.tsx
    │   │   ├── ChatInterface.tsx
    │   │   └── *.css
    │   ├── lib/
    │   │   └── api.ts
    │   ├── styles/
    │   │   └── globals.css
    │   ├── layout.tsx
    │   └── page.tsx
    ├── package.json
    ├── tsconfig.json
    ├── next.config.js
    └── .env.local
```

## 🔧 API Endpoints

### Health Check
```
GET /health
```

### Credentials Management
```
POST /credentials/set       # Set credentials
GET /credentials/status     # Check configuration
```

### LinkedIn Agents
```
POST /agents/post           # Post to feed
POST /agents/connect        # Send connection request
POST /agents/inbox          # Auto-reply to messages
POST /agents/profile        # Optimize profile
```

### Email
```
POST /agents/email          # Send email
```

## ✅ What's Fixed

1. **agent_profile_tweak.py line 43:** Fixed `response.choices.message.content` → `response.choices[0].message.content`
2. **agent_profile_tweak.py line 102:** Fixed URL `linkedin.comedit` → `linkedin.com/edit`
3. **agent_profile_tweak.py line 128:** Fixed URL `linkedin.comedit` → `linkedin.com/edit`
4. **Added FastAPI backend** with proper API endpoints
5. **Created beautiful Next.js frontend** with botanical theme
6. **Integrated all agents** into API endpoints

## 🚀 Run Both Services

**Terminal 1 (Backend):**
```bash
cd aegis-complete
python app.py
```

**Terminal 2 (Frontend):**
```bash
cd aegis-complete/frontend
npm run dev
```

Then open `http://localhost:3000` 🎉

## 🎨 UI Features

- ✨ Beautiful botanical green theme
- 🔐 Secure credential setup wizard
- 💬 Natural language chat interface
- 🎯 Quick action buttons
- 📊 Real-time status indicators
- 🌙 Smooth animations and transitions
- 📱 Fully responsive mobile design

## ⚠️ Security Notes

- Never commit `.env` to git
- Keep API keys secure
- Respect LinkedIn and Gmail rate limits
- Review actions before execution
- Use responsibly within platform ToS

## 📦 Production Deployment

### Backend (Gunicorn)
```bash
pip install gunicorn
gunicorn app:app --workers 4 --worker-class uvicorn.workers.UvicornWorker --bind 0.0.0.0:8000
```

### Frontend (Next.js)
```bash
npm run build
npm start
```

## 🐛 Troubleshooting

| Issue | Solution |
|-------|----------|
| CORS error | Check `NEXT_PUBLIC_API_URL` in frontend `.env.local` |
| LinkedIn cookie expired | Log in again and get fresh cookie |
| Playwright not found | Run `playwright install chromium` |
| API not responding | Ensure backend is running on port 8000 |
| OpenAI error | Verify API key and check OpenAI account |

## 🎯 Next Steps

1. Set up your credentials in the Aegis UI
2. Try the quick action buttons
3. Chat naturally with Aegis
4. Automate your LinkedIn presence!

---

**Built with ❤️ for autonomous LinkedIn intelligence**

🛡️ Aegis - Your AI LinkedIn Agent
