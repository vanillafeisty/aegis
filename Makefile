.PHONY: help setup up down logs test clean rebuild

help:
	@echo "Aegis Development Commands"
	@echo "============================"
	@echo ""
	@echo "make setup       - Initial setup (.env + docker build)"
	@echo "make up         - Start all services"
	@echo "make down       - Stop all services"
	@echo "make logs       - View logs (follow mode)"
	@echo "make logs-backend - Backend logs only"
	@echo "make logs-frontend - Frontend logs only"
	@echo "make test       - Run tests"
	@echo "make clean      - Remove containers/volumes"
	@echo "make rebuild    - Rebuild without cache"
	@echo "make shell-backend - SSH into backend container"
	@echo "make shell-postgres - Connect to database"
	@echo ""

setup:
	@echo "Setting up Aegis..."
	@cp .env.example .env
	@echo "✅ Created .env file"
	@echo "⚠️  Edit .env with your credentials"
	@docker-compose build

up:
	@docker-compose up -d
	@echo "✅ Services started"
	@echo "Frontend: http://localhost:3000"
	@echo "API Docs: http://localhost:8000/api/docs"

down:
	@docker-compose down
	@echo "✅ Services stopped"

logs:
	@docker-compose logs -f

logs-backend:
	@docker-compose logs -f backend

logs-frontend:
	@docker-compose logs -f frontend

test:
	@cd backend && pytest tests/ -v --cov=app

test-frontend:
	@cd frontend && npm test

clean:
	@docker-compose down -v
	@echo "✅ Cleaned up"

rebuild:
	@docker-compose build --no-cache
	@docker-compose up -d

shell-backend:
	@docker-compose exec backend bash

shell-postgres:
	@docker-compose exec postgres psql -U aegis -d aegis

db-backup:
	@docker-compose exec postgres pg_dump -U aegis aegis > backup_$$(date +%Y%m%d_%H%M%S).sql
	@echo "✅ Database backed up"

db-reset:
	@docker-compose exec postgres psql -U aegis aegis -c "DROP SCHEMA public CASCADE; CREATE SCHEMA public;"
	@echo "✅ Database reset"

prettier-backend:
	@cd backend && python -m black app/
	@echo "✅ Backend formatted"

prettier-frontend:
	@cd frontend && npm run format
	@echo "✅ Frontend formatted"

lint-backend:
	@cd backend && python -m ruff check app/
	@echo "✅ Backend linted"

lint-frontend:
	@cd frontend && npm run lint
	@echo "✅ Frontend linted"
