"""
Aegis — Autonomous LinkedIn Intelligence Agent
FastAPI Application Entry Point
"""
from contextlib import asynccontextmanager

from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from fastapi.middleware.gzip import GZipMiddleware
from prometheus_fastapi_instrumentator import Instrumentator

from app.api.routes import auth, agent, jobs, resume, content, analytics, websocket
from app.core.config import settings
from app.core.logging import setup_logging
from app.db.session import init_db

logger = setup_logging()


@asynccontextmanager
async def lifespan(app: FastAPI):
    """Startup and shutdown lifecycle."""
    logger.info("🚀 Aegis starting up...")
    await init_db()
    logger.info("✅ Database initialized")
    yield
    logger.info("🛑 Aegis shutting down...")


app = FastAPI(
    title="Aegis API",
    description="Autonomous LinkedIn Intelligence Agent — REST API",
    version="1.0.0",
    docs_url="/api/docs",
    redoc_url="/api/redoc",
    openapi_url="/api/openapi.json",
    lifespan=lifespan,
)

# ── Middleware ────────────────────────────────────────────────────────────────
app.add_middleware(
    CORSMiddleware,
    allow_origins=settings.CORS_ORIGINS,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)
app.add_middleware(GZipMiddleware, minimum_size=1000)

# ── Prometheus Metrics ────────────────────────────────────────────────────────
Instrumentator().instrument(app).expose(app, endpoint="/metrics")

# ── Routers ───────────────────────────────────────────────────────────────────
app.include_router(auth.router,      prefix="/api/auth",      tags=["Authentication"])
app.include_router(agent.router,     prefix="/api/agent",     tags=["Agent"])
app.include_router(jobs.router,      prefix="/api/jobs",      tags=["Jobs"])
app.include_router(resume.router,    prefix="/api/resume",    tags=["Resume"])
app.include_router(content.router,   prefix="/api/content",   tags=["Content"])
app.include_router(analytics.router, prefix="/api/analytics", tags=["Analytics"])
app.include_router(websocket.router, prefix="/ws",            tags=["WebSocket"])


@app.get("/health", tags=["Health"])
async def health_check():
    return {"status": "healthy", "service": "aegis-api", "version": "1.0.0"}
