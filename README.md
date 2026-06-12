# Aegis — Autonomous LinkedIn Intelligence Agent

An AI-powered, goal-driven autonomous career intelligence agent that operates on LinkedIn on behalf of the user.

## 🚀 Quick Start

### Prerequisites
- Docker & Docker Compose
- Node.js 20+ (for local frontend dev)
- Python 3.11+ (for local backend dev)

### Setup

1. **Clone and Configure**
```bash
cp .env.example .env
# Edit .env with your API keys:
# - GEMINI_API_KEY from Google AI Studio
# - SENDGRID_API_KEY (optional)
```

2. **Start Services**
```bash
docker-compose up -d
```

3. **Access the App**
- Frontend: http://localhost:3000
- API Docs: http://localhost:8000/api/docs
- Grafana: http://localhost:3001 (admin/admin)

## 📋 Project Structure

```
aegis/
├── backend/              # FastAPI Python backend
│   ├── app/
│   │   ├── api/         # REST API routes
│   │   ├── models/      # SQLAlchemy ORM models
│   │   ├── schemas/     # Pydantic validation schemas
│   │   ├── services/    # Business logic
│   │   ├── worker/      # Celery async tasks
│   │   ├── core/        # Config, security, logging
│   │   └── db/          # Database session
│   ├── requirements.txt
│   └── Dockerfile
├── frontend/            # React/TypeScript SPA
│   ├── src/
│   │   ├── pages/       # Route pages
│   │   ├── components/  # Reusable components
│   │   ├── store/       # Zustand state
│   │   ├── styles/      # Tailwind CSS
│   │   └── types/       # TypeScript types
│   ├── package.json
│   └── Dockerfile
├── infrastructure/      # Docker, monitoring
│   ├── docker/         # Docker configs
│   └── monitoring/     # Prometheus, Grafana
├── scripts/            # Utility scripts
└── docker-compose.yml
```

## 🔑 Key Features

### 1. **Autonomous Job Discovery**
- Search, filter, and rank job openings
- Resume-to-JD matching with skill gap analysis
- Scheduled background job scans

### 2. **AI Planning Engine**
- Gemini 2.5 Flash for intent classification
- Goal decomposition into task DAGs
- Context-aware decision making via RAG

### 3. **Browser Automation**
- Playwright-based LinkedIn navigation
- Anti-detection measures
- Form filling, data extraction, publishing

### 4. **Multi-Mode Execution**
- **Manual**: Every action requires approval
- **Assisted**: Read-only auto, write actions need approval (default)
- **Autonomous**: Pre-authorized actions execute automatically

### 5. **RAG Knowledge System**
- ChromaDB vector embeddings
- Persistent resume, job, and recruiter memory
- Semantic similarity search

### 6. **Real-time Dashboard**
- WebSocket-based live updates
- Agent status, notifications, analytics
- Application tracker (Kanban-style)

## 🏗️ Architecture

### Backend Stack
- **Framework**: FastAPI (Python 3.11+)
- **Database**: PostgreSQL 15+ with async SQLAlchemy
- **Cache/Queue**: Redis 7+ with Celery
- **AI Layer**: Gemini 2.5 Flash API
- **Vectors**: ChromaDB for RAG
- **Automation**: Playwright headless Chromium
- **Monitoring**: Prometheus + Grafana

### Frontend Stack
- **Framework**: React 18 + TypeScript
- **State**: Zustand
- **Styling**: Tailwind CSS + ShadCN UI
- **Bundler**: Vite
- **HTTP**: Axios

## 📡 API Endpoints

### Authentication
- `POST /api/auth/register` — Register user
- `POST /api/auth/login` — Login with email/password
- `POST /api/auth/refresh` — Refresh access token

### Agent
- `POST /api/agent/command` — Submit command
- `GET /api/agent/tasks` — Get user tasks
- `POST /api/agent/tasks/{id}/approve` — Approve pending task

### Jobs
- `POST /api/jobs/search` — Search jobs
- `POST /api/jobs/apply` — Apply to job
- `GET /api/applications` — Get applications

### Content
- `POST /api/content/generate` — Generate post
- `POST /api/content/publish/{id}` — Publish post

### Analytics
- `GET /api/analytics/summary` — Get summary
- `GET /api/analytics/dashboard` — Dashboard metrics

### WebSocket
- `WS /ws/{user_id}` — Real-time updates

## 🔐 Security

- **Auth**: JWT tokens (15 min access, 7 day refresh)
- **Encryption**: AES-256 for credentials at rest
- **TLS**: Required in production
- **RBAC**: Role-based access control (user/admin)
- **Rate Limiting**: 100 req/min per user
- **Audit Logging**: Immutable agent action logs

## 🚦 Environment Variables

See `.env.example`. Key ones:

```env
GEMINI_API_KEY=your_key_here
DATABASE_URL=postgresql+asyncpg://user:pass@postgres:5432/aegis
REDIS_URL=redis://:pass@redis:6379/0
SECRET_KEY=your_jwt_secret_key
```

## 📊 Monitoring

- **Prometheus**: Metrics at http://localhost:9090
- **Grafana**: Dashboards at http://localhost:3001
- **Metrics**: API latency, agent throughput, error rates

## 🧪 Testing

```bash
# Backend tests
cd backend
pytest tests/ -v --cov=app

# Frontend tests
cd frontend
npm test
```

## 🚢 Deployment

### AWS EC2
```bash
# Build images
docker-compose build

# Push to ECR
aws ecr get-login-password | docker login --username AWS --password-stdin <ECR_URI>
docker tag aegis:latest <ECR_URI>/aegis:latest
docker push <ECR_URI>/aegis:latest

# Deploy via CloudFormation / ECS
```

### Docker Hub
```bash
docker-compose build
docker tag aegis:latest yourusername/aegis:latest
docker push yourusername/aegis:latest
```

## 📝 Development

### Backend Development
```bash
cd backend
python -m venv venv
source venv/bin/activate
pip install -r requirements.txt
uvicorn app.main:app --reload
```

### Frontend Development
```bash
cd frontend
npm install
npm run dev
```

## 🔄 Database Migrations

```bash
# Using Alembic (when ready)
cd backend
alembic revision --autogenerate -m "description"
alembic upgrade head
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/awesome-feature`)
3. Commit changes (`git commit -m 'Add awesome feature'`)
4. Push to branch (`git push origin feature/awesome-feature`)
5. Open a Pull Request

## 📄 License

MIT License — See LICENSE file

## 🆘 Support

- **Docs**: See `/docs` folder
- **Issues**: GitHub Issues
- **Email**: support@aegis.app

---

**Built with ❤️ for autonomous career growth**
