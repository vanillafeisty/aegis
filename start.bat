@echo off
REM Aegis Startup Script for Windows
REM This script starts both the FastAPI backend and Next.js frontend

echo.
echo 🛡️  Starting Aegis AI Agent Platform...
echo.

REM Check if .env exists
if not exist .env (
    echo ⚠️  .env file not found. Creating from template...
    if exist .env.example (
        copy .env.example .env
        echo 📝 Please update .env with your credentials
    ) else (
        echo ❌ .env.example not found
        exit /b 1
    )
)

echo 📦 Installing/Updating dependencies...
pip install -r requirements.txt > nul 2>&1
cd frontend
call npm install > nul 2>&1
cd ..

echo.
echo 🚀 Starting services...
echo.
echo 📌 Backend: http://localhost:8000
echo 📌 Frontend: http://localhost:3000
echo 📌 API Docs: http://localhost:8000/docs
echo.
echo Press Ctrl+C to stop both services
echo.

REM Start backend in a new window
echo Starting FastAPI backend...
start "Aegis Backend" cmd /k "python app.py"

REM Wait for backend to start
timeout /t 3 /nobreak

REM Start frontend in a new window
echo Starting Next.js frontend...
start "Aegis Frontend" cmd /k "cd frontend && npm run dev"

echo.
echo ✅ Services started in separate windows
echo.
