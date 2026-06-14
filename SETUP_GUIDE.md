# 🛡️ Aegis Setup Guide

Welcome to Aegis! This guide will walk you through everything you need to get started with the autonomous LinkedIn AI agent.

## Table of Contents
1. [System Requirements](#system-requirements)
2. [Installation](#installation)
3. [Credential Setup](#credential-setup)
4. [Running Aegis](#running-aegis)
5. [First Steps](#first-steps)
6. [Troubleshooting](#troubleshooting)

---

## System Requirements

Before you start, make sure you have:

- **Python 3.10 or higher** - [Download Python](https://www.python.org/downloads/)
- **Node.js 18 or higher** - [Download Node.js](https://nodejs.org/)
- **Git** (optional but recommended) - [Download Git](https://git-scm.com/)
- **Chrome/Chromium browser** - For LinkedIn automation
- **Active LinkedIn account** - With appropriate permissions
- **Gmail account** - For email features
- **OpenAI API account** - For AI-powered features

### Verify Installation

```bash
# Check Python version
python --version
# Should show 3.10 or higher

# Check Node.js version
node --version
# Should show 18 or higher

# Check npm version
npm --version
# Should show 8 or higher
```

---

## Installation

### Step 1: Prepare the Project

If you don't have the project extracted yet:

```bash
# Extract the aegis-complete.zip file
unzip aegis-complete.zip
cd aegis-complete
```

### Step 2: Install Backend Dependencies

```bash
# Install Python dependencies
pip install -r requirements.txt

# Install Playwright browsers (required for LinkedIn automation)
playwright install chromium

# Verify installation
pip list | grep -E "fastapi|playwright|openai"
```

### Step 3: Install Frontend Dependencies

```bash
# Navigate to frontend directory
cd frontend

# Install Node dependencies
npm install

# Go back to root
cd ..
```

### Step 4: Verify Everything Works

```bash
# Test Python imports
python -c "import fastapi, playwright, openai; print('✅ All Python packages installed')"

# Test Node packages
cd frontend
npm list | head -20
cd ..
```

---

## Credential Setup

### LinkedIn Session Cookie

1. **Open LinkedIn in your browser**
   - Go to [LinkedIn.com](https://linkedin.com) and log in

2. **Open Developer Tools**
   - Press `F12` on Windows/Linux or `Cmd+Option+I` on Mac
   - Click on the "Application" tab

3. **Find the Session Cookie**
   - Look for "Cookies" in the left sidebar
   - Click on `linkedin.com`
   - Find the cookie named `li_at`
   - Double-click the value to select and copy it

4. **Save the Cookie**
   - Copy the entire cookie value (it's long!)
   - You'll need this in the next section

### Gmail App Password

Gmail requires an "App Password" for security reasons:

1. **Go to Google Account Security**
   - Visit [Google Account Security](https://myaccount.google.com/security)
   - Scroll down to "How you sign in to Google"

2. **Enable 2-Step Verification (if not already done)**
   - Click "2-Step Verification"
   - Follow the setup process

3. **Create App Password**
   - Go back to Security settings
   - Click "App passwords"
   - Select "Mail" and "Windows Computer"
   - Google will generate a 16-character password
   - **Copy this password** - you'll need it

### OpenAI API Key

1. **Go to OpenAI Platform**
   - Visit [OpenAI API Keys](https://platform.openai.com/api-keys)
   - Log in with your OpenAI account

2. **Create New Secret Key**
   - Click "Create new secret key"
   - Name it "Aegis" or something recognizable
   - Click "Create secret key"
   - **Copy the key immediately** (you can't see it again!)

3. **Save Your Key**
   - Keep this key safe
   - You'll need it in the next section

### LinkedIn Client ID (Optional but Recommended)

1. **Register LinkedIn App**
   - Go to [LinkedIn Developers](https://www.linkedin.com/developers)
   - Click "Create app"
   - Fill in the required information
   - Accept the terms
   - Click "Create app"

2. **Get Your Client ID**
   - In your app settings, find "Client ID"
   - Copy it

---

## Running Aegis

### Option A: Automatic Startup (Recommended)

#### macOS/Linux:
```bash
# From the aegis-complete directory
./start.sh
```

#### Windows:
```bash
# From the aegis-complete directory
start.bat
```

This will:
- ✅ Install dependencies if needed
- ✅ Start the FastAPI backend on port 8000
- ✅ Start the Next.js frontend on port 3000
- ✅ Open ready for use

### Option B: Manual Startup

**Terminal 1 - Backend:**
```bash
cd aegis-complete
python app.py
```
You should see:
```
INFO:     Uvicorn running on http://0.0.0.0:8000
```

**Terminal 2 - Frontend:**
```bash
cd aegis-complete/frontend
npm run dev
```
You should see:
```
▲ Next.js 14.0.0
- Local: http://localhost:3000
```

### Verify Services Are Running

- **Backend API**: Open [http://localhost:8000](http://localhost:8000)
  - You should see the API documentation
  
- **Frontend UI**: Open [http://localhost:3000](http://localhost:3000)
  - You should see the Aegis login screen

---

## First Steps

### 1. Complete the Setup Wizard

When you open [http://localhost:3000](http://localhost:3000):

1. **LinkedIn Configuration**
   - Paste your LinkedIn session cookie
   - Paste your LinkedIn access token
   - Paste your LinkedIn client ID (optional)

2. **Gmail Configuration**
   - Enter your Gmail address
   - Paste your Gmail app password

3. **OpenAI Configuration**
   - Paste your OpenAI API key

4. **Review**
   - Review all settings
   - Click "Activate Aegis"

### 2. Start Using Aegis

Once setup is complete, you'll see the chat interface:

**Try These Commands:**

```
"Post: Excited to announce I'm now using AI to automate my LinkedIn! 🚀 #AI #Automation"
```

```
"Connect to https://linkedin.com/in/some-person"
```

```
"Check my messages"
```

```
"Optimize my profile"
```

### 3. Watch It Work

When you execute a command:
1. Aegis will open your browser
2. A Chrome window will appear
3. You'll see Aegis interacting with LinkedIn
4. Return to the chat for status updates

---

## Troubleshooting

### Connection Issues

**Problem**: "Cannot connect to backend"
- **Solution**: 
  - Verify backend is running at `http://localhost:8000/health`
  - Check firewall isn't blocking port 8000
  - Make sure `NEXT_PUBLIC_API_URL` in frontend `.env.local` is correct

**Problem**: "CORS error"
- **Solution**:
  - Backend CORS is configured for localhost
  - If using different domains, edit `app.py` CORS settings

### Credential Issues

**Problem**: "LinkedIn session cookie expired"
- **Solution**:
  - Log back into LinkedIn
  - Get a fresh `li_at` cookie
  - Update in Aegis settings or `.env`

**Problem**: "Gmail authentication failed"
- **Solution**:
  - Ensure you're using an App Password, not your regular password
  - Generate a new app password
  - Check 2-Step Verification is enabled

**Problem**: "OpenAI API error"
- **Solution**:
  - Verify your API key at [OpenAI Platform](https://platform.openai.com/account/api-keys)
  - Ensure your account has credits
  - Check for typos in the key

### Browser/Automation Issues

**Problem**: "Playwright browser not found"
- **Solution**:
  ```bash
  playwright install chromium
  ```

**Problem**: "Chrome not launching"
- **Solution**:
  - Install Chromium: `playwright install`
  - Make sure you have ~500MB free disk space
  - Try clearing browser cache

**Problem**: "Locator not found" errors
- **Solution**:
  - LinkedIn UI changes frequently
  - Refresh your browser
  - Try the action again after a few minutes

### Performance Issues

**Problem**: "Actions are slow"
- **Solution**:
  - Close other Chrome windows
  - Ensure good internet connection
  - Check CPU/memory usage
  - Try actions during off-peak hours

**Problem**: "Frontend is slow"
- **Solution**:
  - Check internet speed
  - Clear browser cache
  - Try a different browser
  - Restart both services

### Python Environment Issues

**Problem**: "Module not found" errors
- **Solution**:
  ```bash
  # Reinstall dependencies
  pip install --upgrade -r requirements.txt
  
  # Or use a virtual environment (recommended)
  python -m venv venv
  source venv/bin/activate  # On Windows: venv\Scripts\activate
  pip install -r requirements.txt
  ```

---

## Next Steps

✅ **Setup Complete!**

Now that Aegis is running:

1. **Explore the Chat Interface**
   - Try all the quick action buttons
   - Experiment with different commands
   - See how AI understands your requests

2. **Set Up Automations**
   - Schedule posts (recommended: 2-3 times per week)
   - Set up regular profile optimizations
   - Enable inbox auto-replies

3. **Monitor Results**
   - Check engagement on posts
   - Review connection acceptance rates
   - Track inbox response efficiency

4. **Customize**
   - Edit colors in `frontend/app/styles/globals.css`
   - Modify AI prompts in agent files
   - Add your own agents!

---

## Getting Help

If you encounter issues:

1. **Check the README.md** - Common issues and solutions
2. **Review the console logs** - Both backend and browser console
3. **Check browser DevTools** - Network tab for API errors
4. **Verify credentials** - Double-check all credentials are correct
5. **Try restarting** - Kill both services and restart

---

## Advanced Setup

### Using a Virtual Environment (Recommended)

```bash
# Create virtual environment
python -m venv venv

# Activate it
# On macOS/Linux:
source venv/bin/activate
# On Windows:
venv\Scripts\activate

# Install dependencies
pip install -r requirements.txt
playwright install chromium

# Now run your services
python app.py
```

### Using Docker (Optional)

```bash
# Build the image
docker build -t aegis .

# Run with environment
docker run -p 8000:8000 -e LINKEDIN_SESSION_COOKIE=xxx aegis
```

---

## Updating Aegis

To get the latest updates:

```bash
# Update dependencies
pip install --upgrade -r requirements.txt
cd frontend && npm update && cd ..

# Clear cache
cd frontend && rm -rf .next node_modules && npm install && cd ..
```

---

**🎉 You're all set! Welcome to the future of LinkedIn automation!**

*If you have questions or issues, check the README.md or create an issue in the repository.*

---

**Last Updated:** June 2026
**Aegis Version:** 1.0.0
