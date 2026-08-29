@echo off
REM Aegis AI - Windows Startup Script
REM This script starts the backend server on Windows

echo.
echo ========================================
echo   AEGIS AI v2.0 - Backend Startup
echo ========================================
echo.

REM Check if Python is installed
python --version >nul 2>&1
if errorlevel 1 (
    echo ERROR: Python is not installed or not in PATH
    echo Please install Python 3.12+ from python.org
    pause
    exit /b 1
)

echo ✓ Python found
echo.

REM Navigate to backend directory
cd /d "%~dp0\backend"
if errorlevel 1 (
    echo ERROR: Could not navigate to backend directory
    pause
    exit /b 1
)

echo ✓ Backend directory found
echo.

REM Check if requirements are installed
echo Checking dependencies...
pip show fastapi >nul 2>&1
if errorlevel 1 (
    echo Installing dependencies...
    pip install -r requirements.txt
    if errorlevel 1 (
        echo ERROR: Failed to install dependencies
        pause
        exit /b 1
    )
)

echo ✓ Dependencies installed
echo.

REM Start the server
echo ========================================
echo Starting Aegis AI Backend Server...
echo ========================================
echo.
echo Server will run on: http://localhost:8000
echo Dashboard will be at: http://localhost:3000
echo.
echo Press Ctrl+C to stop the server
echo.

python app.py

pause
