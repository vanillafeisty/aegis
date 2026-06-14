#!/bin/bash

# Aegis Startup Script
# This script starts both the FastAPI backend and Next.js frontend

echo "🛡️  Starting Aegis AI Agent Platform..."
echo ""

# Check if Python is installed
if ! command -v python3 &> /dev/null; then
    echo "❌ Python 3 is not installed. Please install Python 3.10 or higher."
    exit 1
fi

# Check if Node.js is installed
if ! command -v node &> /dev/null; then
    echo "❌ Node.js is not installed. Please install Node.js 18 or higher."
    exit 1
fi

# Check if .env exists
if [ ! -f .env ]; then
    echo "⚠️  .env file not found. Creating from .env.example..."
    if [ -f .env.example ]; then
        cp .env.example .env
        echo "📝 Please update .env with your credentials"
    else
        echo "❌ .env.example not found"
        exit 1
    fi
fi

echo "📦 Installing/Updating dependencies..."
pip install -r requirements.txt > /dev/null 2>&1
cd frontend
npm install > /dev/null 2>&1
cd ..

echo ""
echo "🚀 Starting services..."
echo ""
echo "📌 Backend: http://localhost:8000"
echo "📌 Frontend: http://localhost:3000"
echo "📌 API Docs: http://localhost:8000/docs"
echo ""
echo "Press Ctrl+C to stop both services"
echo ""

# Start backend in background
echo "Starting FastAPI backend..."
python app.py &
BACKEND_PID=$!

# Wait for backend to start
sleep 2

# Start frontend
echo "Starting Next.js frontend..."
cd frontend
npm run dev &
FRONTEND_PID=$!
cd ..

# Handle Ctrl+C
trap "echo ''; echo '⏹️  Shutting down services...'; kill $BACKEND_PID $FRONTEND_PID; exit 0" SIGINT

# Wait for both processes
wait $BACKEND_PID $FRONTEND_PID
