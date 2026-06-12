#!/bin/bash
set -e

echo "🚀 Aegis Setup Script"
echo "===================="
echo ""

# Check Docker
if ! command -v docker &> /dev/null; then
    echo "❌ Docker not found. Install from https://docker.com"
    exit 1
fi

echo "✅ Docker found"

# Check Docker Compose
if ! command -v docker-compose &> /dev/null; then
    echo "⚠️  docker-compose command not found (may be 'docker compose')"
fi

echo "✅ Docker Compose ready"
echo ""

# Setup .env
if [ ! -f .env ]; then
    echo "📝 Creating .env file from template..."
    cp .env.example .env
    echo "⚠️  IMPORTANT: Edit .env with your credentials:"
    echo "   - GEMINI_API_KEY from Google AI Studio"
    echo "   - SECRET_KEY (run: python -c \"import secrets; print(secrets.token_urlsafe(32))\")"
    echo ""
    echo "   File: .env"
    exit 1
fi

echo "✅ .env file found"
echo ""

# Start services
echo "🐳 Starting Docker services..."
docker-compose down 2>/dev/null || true
docker-compose up -d --build

echo ""
echo "⏳ Waiting for services to be ready (30 seconds)..."
sleep 30

echo ""
echo "✅ All services started!"
echo ""
echo "📍 Access points:"
echo "   Frontend:     http://localhost:3000"
echo "   API Docs:     http://localhost:8000/api/docs"
echo "   Prometheus:   http://localhost:9090"
echo "   Grafana:      http://localhost:3001 (admin/admin)"
echo ""
echo "🔗 Next steps:"
echo "   1. Open http://localhost:3000 in your browser"
echo "   2. Register a new account"
echo "   3. Explore the dashboard"
echo ""
echo "📖 Documentation:"
echo "   README:       ./README.md"
echo "   Deployment:   ./DEPLOYMENT.md"
echo ""
echo "🛑 To stop services:"
echo "   docker-compose down"
echo ""
