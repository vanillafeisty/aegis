# Aegis — Setup & Deployment Guide

## ⚠️ CRITICAL: Credential Setup

Before running anything, **NEVER commit credentials to git**:

1. **Rotate your credentials** (you may have exposed them):
   - Gemini API key: Go to Google AI Studio, delete old key, create new one
   - LinkedIn password: Change at linkedin.com/settings
   - AWS credentials: Check console.aws.amazon.com for activity, rotate

2. **Create local `.env` file**:
   ```bash
   cp .env.example .env
   ```

3. **Fill in `.env` with your NEW credentials** (not the ones in .gitignore):
   ```env
   GEMINI_API_KEY=your_new_key_here
   SECRET_KEY=generate_with: python -c "import secrets; print(secrets.token_urlsafe(32))"
   ```

4. **Verify `.env` is in `.gitignore`**:
   ```bash
   grep "^.env$" .gitignore  # Should match
   ```

## 🚀 Local Development (Docker)

### Step 1: Prerequisites
- Install Docker Desktop: https://www.docker.com/products/docker-desktop
- Install Docker Compose: Comes with Docker Desktop

### Step 2: Start Services
```bash
cd /path/to/aegis
docker-compose up -d
```

This starts:
- PostgreSQL on :5432
- Redis on :6379
- ChromaDB on :8001
- FastAPI backend on :8000
- React frontend on :3000
- Celery worker (async tasks)
- Prometheus on :9090
- Grafana on :3001

### Step 3: Verify Services
```bash
# Check running containers
docker-compose ps

# View logs
docker-compose logs -f backend  # FastAPI logs
docker-compose logs -f frontend # React logs
docker-compose logs -f celery_worker

# Health check
curl http://localhost:8000/health
```

### Step 4: Access the App
- **Frontend**: http://localhost:3000
- **API Docs**: http://localhost:8000/api/docs
- **Grafana**: http://localhost:3001 (admin/admin)

### Step 5: Stop Services
```bash
docker-compose down  # Stop and remove containers
docker-compose down -v  # Also remove volumes (data)
```

## 🔧 Local Frontend Development

If you want to run frontend without Docker (hot reload):

```bash
cd frontend
npm install
npm run dev  # Runs on :3000 with hot reload
```

Note: Backend API will still run in Docker.

## 🔧 Local Backend Development

If you want to run backend without Docker:

```bash
cd backend

# Create virtual environment
python -m venv venv
source venv/bin/activate  # On Windows: venv\Scripts\activate

# Install dependencies
pip install -r requirements.txt

# Create .env file with DATABASE_URL=postgresql://... etc
cp ../.env .

# Run with auto-reload
uvicorn app.main:app --reload --host 0.0.0.0 --port 8000
```

## 🐳 Building Docker Images

### Rebuild images after code changes:
```bash
docker-compose build --no-cache
docker-compose up -d
```

### Push to Docker Hub (for deployment):
```bash
docker login
docker tag aegis_backend yourusername/aegis-backend:latest
docker tag aegis_frontend yourusername/aegis-frontend:latest
docker push yourusername/aegis-backend:latest
docker push yourusername/aegis-frontend:latest
```

## ☁️ AWS Deployment

### Option 1: EC2 + Docker Compose

1. **Launch EC2 instance** (Ubuntu 22.04 LTS, t3.medium)

2. **Install Docker on EC2**:
   ```bash
   sudo apt update && sudo apt install -y docker.io docker-compose
   sudo usermod -aG docker $USER
   ```

3. **Clone repo and setup**:
   ```bash
   git clone your-repo aegis
   cd aegis
   cp .env.example .env
   # Edit .env with production values
   ```

4. **Start services**:
   ```bash
   docker-compose -f docker-compose.yml up -d
   ```

5. **Setup domain + SSL** (with Nginx reverse proxy):
   ```bash
   sudo apt install -y nginx certbot python3-certbot-nginx
   # Configure Nginx to proxy to :8000 and :3000
   # Run certbot for SSL
   sudo certbot certonly --nginx -d yourdomain.com
   ```

### Option 2: AWS ECS + RDS + ElastiCache

1. **Create RDS PostgreSQL instance** (managed database)
2. **Create ElastiCache Redis** (managed cache)
3. **Create ECR repository** for Docker images
4. **Create ECS cluster** + task definitions
5. **Update docker-compose.yml** with RDS/ElastiCache endpoints

### Option 3: Lambda (Future)
Serverless option requires refactoring for Lambda compatibility.

## 📋 Initial Setup Checklist

- [ ] Rotated Gemini API key
- [ ] Changed LinkedIn password
- [ ] Created local `.env` file with NEW credentials
- [ ] `.env` is in `.gitignore`
- [ ] Docker Desktop installed
- [ ] `docker-compose up -d` runs successfully
- [ ] http://localhost:3000 is accessible
- [ ] Can register and login

## 🧪 Testing First Run

1. **Register user**: http://localhost:3000/register
   - Email: test@example.com
   - Password: TestPass123
   - Full Name: Test User

2. **Login**: http://localhost:3000/login

3. **View Dashboard**: Should see empty state

4. **Check API**: http://localhost:8000/api/docs
   - Try `/health` endpoint

5. **Check database**: 
   ```bash
   docker-compose exec postgres psql -U aegis -d aegis -c "SELECT * FROM users;"
   ```

## 🐛 Troubleshooting

### Port already in use
```bash
# Find process on port 8000
lsof -i :8000
# Kill it
kill -9 <PID>
```

### Docker containers failing to start
```bash
# Check logs
docker-compose logs backend
docker-compose logs postgres

# Rebuild from scratch
docker-compose down -v
docker-compose build --no-cache
docker-compose up -d
```

### Database connection error
```bash
# Wait a bit and retry (DB takes time to start)
docker-compose restart postgres backend

# Verify connection
docker-compose exec postgres pg_isready -U aegis
```

### Redis connection error
```bash
docker-compose restart redis
docker-compose exec redis redis-cli ping
```

### Node dependencies issue
```bash
cd frontend
npm ci  # Clean install
npm run dev
```

## 🚨 Security Reminders

- **Never commit `.env`** — It's in `.gitignore`
- **Use strong SECRET_KEY** — `python -c "import secrets; print(secrets.token_urlsafe(32))"`
- **Rotate API keys quarterly**
- **Use HTTPS in production** — Get free SSL from Let's Encrypt
- **Enable 2FA on GitHub account**
- **Audit your git history** for any secrets:
  ```bash
  git log --all --source --oneline | grep -i "key\|password\|secret"
  ```

## 📊 Monitoring in Production

- **Prometheus**: Collect metrics at `/metrics`
- **Grafana**: Create dashboards for uptime, latency, errors
- **CloudWatch**: If using AWS, enable logs
- **Alerts**: Set up email alerts for errors/downtime

## 🔄 Database Backups

### Manual backup:
```bash
docker-compose exec postgres pg_dump -U aegis aegis > backup.sql
```

### Restore from backup:
```bash
docker-compose exec postgres psql -U aegis aegis < backup.sql
```

### Automated backups (AWS):
- Enable automatic snapshots on RDS
- Set backup retention to 30 days
- Test restore procedure

## 📈 Scaling

- **Horizontal**: Load balance frontend across multiple servers
- **Vertical**: Increase EC2 instance size for CPU/RAM
- **Database**: RDS read replicas for scaling reads
- **Cache**: ElastiCache cluster mode for scalable Redis
- **Celery**: Add more workers for async task processing

## ✅ Production Checklist

- [ ] HTTPS enabled
- [ ] Strong SECRET_KEY
- [ ] Database backups configured
- [ ] Monitoring/alerting active
- [ ] Rate limiting enabled
- [ ] CORS configured correctly
- [ ] API keys in Secrets Manager
- [ ] Logs centralized (CloudWatch/ELK)
- [ ] Load testing completed
- [ ] Disaster recovery plan

---

**Questions?** Check GitHub Issues or reach out to support.
