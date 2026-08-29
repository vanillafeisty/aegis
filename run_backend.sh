#!/bin/bash

# Aegis AI - Backend Startup Script for Linux/Mac

echo ""
echo "========================================"
echo "   AEGIS AI v2.0 - Backend Startup"
echo "========================================"
echo ""

# Check if Python is installed
if ! command -v python3 &> /dev/null; then
    echo "ERROR: Python 3 is not installed"
    echo "Please install Python 3.12+ using:"
    echo "  - macOS: brew install python@3.12"
    echo "  - Ubuntu: sudo apt-get install python3.12"
    exit 1
fi

PYTHON_VERSION=$(python3 --version | awk '{print $2}')
echo "✓ Python $PYTHON_VERSION found"
echo ""

# Navigate to backend directory
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
BACKEND_DIR="$SCRIPT_DIR/backend"

if [ ! -d "$BACKEND_DIR" ]; then
    echo "ERROR: Backend directory not found at $BACKEND_DIR"
    exit 1
fi

cd "$BACKEND_DIR" || exit 1
echo "✓ Backend directory found"
echo ""

# Check if Python virtual environment exists
if [ ! -d "venv" ]; then
    echo "Creating Python virtual environment..."
    python3 -m venv venv
fi

# Activate virtual environment
source venv/bin/activate

# Check and install dependencies
echo "Checking dependencies..."
if ! python3 -m pip show fastapi &> /dev/null; then
    echo "Installing dependencies..."
    pip install -r requirements.txt
    if [ $? -ne 0 ]; then
        echo "ERROR: Failed to install dependencies"
        exit 1
    fi
fi

echo "✓ Dependencies installed"
echo ""

# Start the server
echo "========================================"
echo "Starting Aegis AI Backend Server..."
echo "========================================"
echo ""
echo "Server will run on: http://localhost:8000"
echo "Dashboard will be at: http://localhost:3000"
echo ""
echo "Press Ctrl+C to stop the server"
echo ""

python3 app.py
