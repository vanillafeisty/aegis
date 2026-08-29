@echo off
REM Aegis AI - Frontend Startup Script
REM This script starts the frontend on Windows

echo.
echo ========================================
echo   AEGIS AI v2.0 - Frontend Startup
echo ========================================
echo.

REM Check if Node is installed
node --version >nul 2>&1
if errorlevel 1 (
    echo ERROR: Node.js is not installed or not in PATH
    echo Please install Node.js v20+ from nodejs.org
    pause
    exit /b 1
)

echo ✓ Node.js found
node --version

echo.

REM Navigate to frontend directory
cd /d "%~dp0\frontend"
if errorlevel 1 (
    echo ERROR: Could not navigate to frontend directory
    pause
    exit /b 1
)

echo ✓ Frontend directory found
echo.

REM Check if node_modules exists
if not exist "node_modules" (
    echo Installing dependencies...
    call npm install
    if errorlevel 1 (
        echo ERROR: Failed to install dependencies
        pause
        exit /b 1
    )
)

echo ✓ Dependencies installed
echo.

REM Start the dev server
echo ========================================
echo Starting Aegis AI Frontend...
echo ========================================
echo.
echo Frontend will run on: http://localhost:3000
echo Backend should be running on: http://localhost:8000
echo.
echo Press Ctrl+C to stop the server
echo.

call npm run dev

pause
