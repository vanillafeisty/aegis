# 🛡️ AEGIS - QUICK START GUIDE

## 📥 What You Downloaded

You have **aegis-complete.zip** - a fully built LinkedIn automation AI agent with:
- ✅ Fixed Python backend (3 bugs corrected)
- ✅ Beautiful Next.js React frontend
- ✅ FastAPI server with all agents
- ✅ Botanical-themed UI (sage green, Times New Roman)
- ✅ Complete documentation
- ✅ Startup scripts for Windows/Mac/Linux

---

## ⚡ FASTEST START (2 Minutes)

### Step 1: Extract
```bash
unzip aegis-complete.zip
cd aegis-complete
```

### Step 2: Run
**Mac/Linux:**
```bash
./start.sh
```

**Windows:**
```bash
start.bat
```

### Step 3: Open Browser
```
http://localhost:3000
```

### Step 4: Add Credentials
The setup wizard will guide you through:
1. LinkedIn credentials
2. Gmail credentials
3. OpenAI API key
4. Review & activate

---

## 🔑 You'll Need These 6 Credentials

### 1. LinkedIn Session Cookie
- Go to LinkedIn.com → Log in
- Press F12 (DevTools)
- Application → Cookies → search "li_at"
- Copy the value (it's long)

### 2. LinkedIn Access Token
- From LinkedIn API
- Similar to session cookie

### 3. Gmail Email
- Your Gmail address (user@gmail.com)

### 4. Gmail App Password
- Go to myaccount.google.com/apppasswords
- Generate one for Mail
- Copy the 16-character password

### 5. OpenAI API Key
- Go to platform.openai.com/api-keys
- Create new key
- Copy it

### 6. LinkedIn Client ID (Optional)
- From LinkedIn App Registration
- Or skip it

---

## 🎮 Using Aegis

Once setup is complete, you can:

**Post to LinkedIn:**
```
"Post: Check out my new AI project! 🚀"
```

**Send Connection Request:**
```
"Connect to https://linkedin.com/in/john-doe"
```

**Check Messages:**
```
"Check my messages"
```

**Optimize Profile:**
```
"Optimize my profile"
```

---

## 🆘 Something's Wrong?

### Backend won't start
```bash
pip install -r requirements.txt
playwright install chromium 
python app.py
```

### Frontend won't start
```bash
cd frontend
npm install
npm run dev
```

### Credentials not working
- Double-check LinkedIn cookie hasn't expired (get a fresh one)
- Verify Gmail app password is correct
- Check OpenAI API key is correct

### More help?
- Read **SETUP_GUIDE.md** (detailed step-by-step)
- Read **README.md** (project overview)
- Check API docs: http://localhost:8000/docs

---

## 📁 Project Structure (Key Files)

```
aegis-complete/
├── app.py                  ← FastAPI backend
├── frontend/app/page.tsx   ← Main React component
├── start.sh & start.bat    ← Startup scripts
├── README.md               ← Full documentation
├── SETUP_GUIDE.md         ← Detailed setup
└── CHANGES.md             ← What's new & fixed
```

---

## ✨ What's Special

🎨 **Beautiful UI**
- Sage green botanical theme
- Times New Roman bold typography
- Smooth animations
- Mobile responsive

🤖 **Smart Agents**
- Post to LinkedIn
- Send connections
- Auto-reply to messages
- Optimize profile
- Send emails

💻 **Tech Stack**
- FastAPI (backend)
- Next.js 14 (frontend)
- React (UI)
- Playwright (browser automation)
- OpenAI (intelligence)

---

## 🚀 Common Commands

```bash
# Start both services (recommended)
./start.sh              # Mac/Linux
start.bat              # Windows

# Start just backend
python app.py

# Start just frontend
cd frontend && npm run dev

# Build for production
cd frontend && npm run build

# See API documentation
http://localhost:8000/docs
```

---

## ✅ Success = When You See

1. Terminal shows "Uvicorn running on http://0.0.0.0:8000"
2. Terminal shows "▲ Next.js" and "Local: http://localhost:3000"
3. Browser shows Aegis logo and setup wizard
4. You can enter credentials
5. Chat interface appears with welcome message

---

## 🎯 Next Steps

1. ✅ Extract the zip
2. ✅ Run start.sh or start.bat
3. ✅ Gather your 6 credentials
4. ✅ Fill in the setup wizard
5. ✅ Start chatting with Aegis
6. ✅ Automate your LinkedIn!

---

## 💡 Pro Tips

- Keep the browser window open while Aegis is working
- Start with simple commands ("Post: Hello")
- Check API docs for all endpoints
- Keep credentials secure (don't share .env)
- Customize colors in frontend/app/styles/globals.css

---

## 📞 Need More Help?

- **Quick Setup**: SETUP_GUIDE.md (70+ steps)
- **Full Docs**: README.md
- **What Changed**: CHANGES.md
- **API Reference**: http://localhost:8000/docs
- **Troubleshooting**: SETUP_GUIDE.md → Troubleshooting

---

## 🎊 TL;DR

```bash
cd aegis-complete
./start.sh
# → Open http://localhost:3000
# → Add credentials
# → Start automating LinkedIn!
```

---

**Built with ❤️ for autonomous LinkedIn intelligence**

🛡️ **Aegis v1.0** - Your AI LinkedIn Agent

*Everything is ready. Time to automate.* 🚀

---

**Questions?** Check the docs inside aegis-complete/
