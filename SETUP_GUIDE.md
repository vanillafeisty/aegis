# 🛠️ Aegis AI v2.0 - Complete Setup Guide

## System Requirements

### Minimum
- Python 3.12+
- Node.js 20+
- 2GB RAM
- 500MB disk space

### Recommended
- Python 3.12.x (latest)
- Node.js 20.x LTS
- 4GB RAM
- 1GB disk space
- Chrome/Chromium browser

---

## Windows Setup

### Step 1: Install Python

1. Go to https://www.python.org/downloads/
2. Download Python 3.12 (or latest 3.12.x)
3. Run installer
4. ✅ **IMPORTANT**: Check "Add Python to PATH" during installation
5. Click "Install Now"

**Verify installation:**
```bash
python --version
# Should show: Python 3.12.x
```

### Step 2: Install Node.js

1. Go to https://nodejs.org/
2. Download LTS version (20.x)
3. Run installer
4. Click "Next" through all steps
5. Complete installation

**Verify installation:**
```bash
node --version
npm --version
# Should show: v20.x.x and 10.x.x
```

### Step 3: Extract Aegis

1. Right-click `aegis-complete-v2.zip`
2. Select "Extract All..."
3. Choose destination (e.g., `C:\Users\YourName\Desktop\aegis-fixed`)
4. Click "Extract"

### Step 4: Start Backend

**Option A: Using Batch Script (Easiest)**
```
Double-click: aegis-fixed\RUN_BACKEND.bat
```

**Option B: Using Command Prompt**
```bash
cd aegis-fixed\backend
pip install -r requirements.txt
python app.py
```

**Expected output:**
```
🚀 Starting Aegis AI Agent Platform v2.0.0
📡 Groq LLM: llama-3.3-70b-versatile
🔗 Zapier MCP: Enabled with HTTP streaming
📧 SMTP: Gmail configured
🌐 CORS: Enabled for localhost:3000
INFO: Uvicorn running on http://0.0.0.0:8000
```

### Step 5: Start Frontend

**Option A: Using Batch Script (Easiest)**
```
Open new Command Prompt and double-click: aegis-fixed\RUN_FRONTEND.bat
```

**Option B: Using Command Prompt**
```bash
cd aegis-fixed\frontend
npm install
npm run dev
```

**Expected output:**
```
  ▲ Next.js 14.x.x
  - Local:        http://localhost:3000
  - Environments: .env.local
```

### Step 6: Open Dashboard

1. Open web browser (Chrome, Edge, Firefox)
2. Go to: **http://localhost:3000**
3. Dashboard should load with dark theme

---

## macOS Setup

### Step 1: Install Python

**Using Homebrew (Recommended):**
```bash
# Install Homebrew if not already installed
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"

# Install Python
brew install python@3.12

# Verify
python3 --version
```

### Step 2: Install Node.js

**Using Homebrew:**
```bash
brew install node

# Verify
node --version
npm --version
```

### Step 3: Extract Aegis

```bash
# Navigate to where you want to extract
cd ~/Desktop

# Extract zip file
unzip aegis-complete-v2.zip

cd aegis-fixed
```

### Step 4: Start Backend

```bash
# Make script executable (first time only)
chmod +x run_backend.sh

# Run backend
./run_backend.sh
```

### Step 5: Start Frontend

```bash
# In a new terminal
cd ~/Desktop/aegis-fixed

# Make script executable (first time only)
chmod +x run_frontend.sh

# Run frontend
./run_frontend.sh
```

### Step 6: Open Dashboard

Open browser and go to: **http://localhost:3000**

---

## Linux Setup (Ubuntu/Debian)

### Step 1: Install Python

```bash
# Update package manager
sudo apt-get update

# Install Python 3.12
sudo apt-get install python3.12 python3.12-venv python3-pip

# Verify
python3 --version
```

### Step 2: Install Node.js

```bash
# Using NodeSource repository (for latest Node)
curl -fsSL https://deb.nodesource.com/setup_20.x | sudo -E bash -
sudo apt-get install -y nodejs

# Verify
node --version
npm --version
```

### Step 3: Extract Aegis

```bash
# Navigate to desired location
cd ~

# Extract zip
unzip aegis-complete-v2.zip

cd aegis-fixed
```

### Step 4: Start Backend

```bash
# Make script executable
chmod +x run_backend.sh

# Run backend
./run_backend.sh
```

### Step 5: Start Frontend

```bash
# In a new terminal
cd ~/aegis-fixed

chmod +x run_frontend.sh

./run_frontend.sh
```

### Step 6: Open Dashboard

Open browser and go to: **http://localhost:3000**

---

## Environment Configuration

### Edit Backend Credentials

The `.env` file is pre-populated but you can update it:

```bash
# Windows
notepad aegis-fixed\backend\.env

# macOS/Linux
nano aegis-fixed/backend/.env
```

**Required variables:**
```env
GROQ_API_KEY=gsk_...          # Your Groq API key
GROQ_MODEL=llama-3.3-70b-versatile
SMTP_EMAIL=your@gmail.com     # Your Gmail address
SMTP_PASSWORD=app-password    # Gmail App Password
```

**Optional variables:**
```env
LINKEDIN_SESSION_COOKIE=AQED...
LINKEDIN_ACCESS_TOKEN=AQED...
ZAPIER_MCP_URL=https://mcp.zapier.com/api/v1
```

### Edit Frontend Configuration

```bash
# Windows
notepad aegis-fixed\frontend\.env.local

# macOS/Linux
nano aegis-fixed/frontend/.env.local
```

**Configuration:**
```env
NEXT_PUBLIC_API_URL=http://localhost:8000
NEXT_PUBLIC_APP_NAME=Aegis AI
NEXT_PUBLIC_VERSION=2.0.0
```

---

## Getting API Keys

### Groq API Key (FREE)

1. Go to https://console.groq.com/keys
2. Sign up or log in
3. Click "Create New API Key"
4. Copy the key starting with `gsk_`
5. Paste in `.env` as `GROQ_API_KEY=gsk_...`

### Gmail App Password

1. Go to https://myaccount.google.com/apppasswords
2. Ensure 2-Factor Authentication is enabled
3. Select "Mail" and "Windows Computer"
4. Click "Generate"
5. Copy the 16-character password
6. Paste in `.env` as `SMTP_PASSWORD=...`

### LinkedIn Session Cookie

1. Open LinkedIn.com
2. Press F12 (open DevTools)
3. Go to "Application" tab
4. Click "Cookies" → "https://www.linkedin.com"
5. Find cookie named `li_at`
6. Copy the long value
7. Paste in `.env` as `LINKEDIN_SESSION_COOKIE=AQED...`

---

## Port Conflicts

### If ports are already in use:

**Windows:**
```bash
# Find what's using port 8000
netstat -ano | findstr :8000

# Kill the process
taskkill /PID <PID> /F
```

**macOS/Linux:**
```bash
# Find what's using port 8000
lsof -i :8000

# Kill the process
kill -9 <PID>
```

**Or change ports:**

Backend (app.py):
```python
if __name__ == "__main__":
    uvicorn.run(
        app,
        host="0.0.0.0",
        port=8001,  # Change from 8000 to 8001
    )
```

Frontend (run terminal):
```bash
npm run dev -- -p 3001  # Use port 3001 instead of 3000
```

Then update `.env.local`:
```env
NEXT_PUBLIC_API_URL=http://localhost:8001
```

---

## Troubleshooting

### "Python not found"
- Python not installed or not in PATH
- **Solution**: Reinstall Python and check "Add Python to PATH"

### "Node not found"
- Node.js not installed
- **Solution**: Install Node.js from nodejs.org

### "pip install failed"
- Network issue or package not available
- **Solution**: Try `pip install --upgrade pip` first

### "npm install failed"
- Network issue
- **Solution**: 
  ```bash
  npm cache clean --force
  npm install
  ```

### "Backend crashes on startup"
- Missing dependencies
- **Solution**: 
  ```bash
  pip install -r requirements.txt
  ```

### "Frontend won't start"
- Node version too old
- **Solution**: Update Node to 20.x LTS

### "Can't connect to backend"
- Backend not running
- **Solution**: Check terminal where backend runs, should see "Uvicorn running"

### "LinkedIn automation not working"
- Session cookie expired
- **Solution**: Get fresh `li_at` cookie from Chrome DevTools

### "Email sending fails"
- Gmail App Password incorrect
- **Solution**: 
  - Enable 2FA on Gmail
  - Generate new App Password
  - Update `.env` file

---

## Keep Running 24/7

### Windows (Using NSSM)

```bash
# Download NSSM from nssm.cc/download
# Extract and run:

nssm install AegisBackend "C:\Python312\python.exe" "C:\path\to\aegis-fixed\backend\app.py"
nssm start AegisBackend

# View status
nssm status AegisBackend
```

### macOS/Linux (Using Screen)

```bash
# Start backend in background
screen -S aegis-backend -d -m bash -c "cd ~/aegis-fixed/backend && python app.py"

# Check status
screen -ls

# Reconnect
screen -r aegis-backend

# Detach
Ctrl+A then D
```

### macOS (Using LaunchAgent)

Create `~/Library/LaunchAgents/com.aegis.plist`:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
<plist version="1.0">
<dict>
    <key>Label</key>
    <string>com.aegis.backend</string>
    <key>ProgramArguments</key>
    <array>
        <string>/usr/local/bin/python3</string>
        <string>/Users/username/aegis-fixed/backend/app.py</string>
    </array>
    <key>RunAtLoad</key>
    <true/>
    <key>KeepAlive</key>
    <true/>
</dict>
</plist>
```

Then:
```bash
launchctl load ~/Library/LaunchAgents/com.aegis.plist
```

---

## Next Steps

1. ✅ **Setup complete**
2. 📖 Read `README.md` for full documentation
3. 🚀 Read `DEPLOYMENT.md` when ready for production
4. 🎓 Check `ANALYSIS_AND_SETUP.md` for technical details

---

**You're all set! Happy automating! 🎉**
