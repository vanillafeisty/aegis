"""Application configuration — loaded from environment variables."""
from functools import lru_cache
from typing import List

from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    model_config = SettingsConfigDict(env_file=".env", extra="ignore")

    # ── App ───────────────────────────────────────────────────────────────────
    APP_NAME: str = "Aegis"
    ENVIRONMENT: str = "development"
    LOG_LEVEL: str = "INFO"
    API_V1_PREFIX: str = "/api"

    # ── Database ──────────────────────────────────────────────────────────────
    DATABASE_URL: str = "postgresql+asyncpg://aegis:aegispass@localhost:5432/aegis"
    DATABASE_URL_SYNC: str = "postgresql://aegis:aegispass@localhost:5432/aegis"

    # ── Redis ─────────────────────────────────────────────────────────────────
    REDIS_URL: str = "redis://:redispass@localhost:6379/0"

    # ── Security ──────────────────────────────────────────────────────────────
    SECRET_KEY: str = "change_me_in_production"
    ALGORITHM: str = "HS256"
    ACCESS_TOKEN_EXPIRE_MINUTES: int = 15
    REFRESH_TOKEN_EXPIRE_DAYS: int = 7

    # ── CORS ──────────────────────────────────────────────────────────────────
    CORS_ORIGINS: List[str] = [
        "http://localhost:3000",
        "http://localhost:5173",
        "https://aegis.yourdomain.com",
    ]

    # ── AI / Gemini ───────────────────────────────────────────────────────────
    GEMINI_API_KEY: str = ""
    GEMINI_MODEL: str = "gemini-2.5-flash"
    GEMINI_PRO_MODEL: str = "gemini-2.5-pro"
    GEMINI_EMBEDDING_MODEL: str = "models/text-embedding-004"

    # ── ChromaDB ──────────────────────────────────────────────────────────────
    CHROMA_HOST: str = "localhost"
    CHROMA_PORT: int = 8000

    # ── Email ─────────────────────────────────────────────────────────────────
    SENDGRID_API_KEY: str = ""
    FROM_EMAIL: str = "noreply@aegis.app"

    # ── Rate Limiting ─────────────────────────────────────────────────────────
    API_RATE_LIMIT_PER_MINUTE: int = 100

    # ── Autonomy ──────────────────────────────────────────────────────────────
    DEFAULT_AUTONOMY_MODE: str = "assisted"  # manual | assisted | autonomous
    MAX_DAILY_CONNECTIONS: int = 20
    MAX_DAILY_MESSAGES: int = 15
    MAX_DAILY_APPLICATIONS: int = 10
    ACTION_DELAY_MIN_SEC: float = 2.0
    ACTION_DELAY_MAX_SEC: float = 5.0


@lru_cache()
def get_settings() -> Settings:
    return Settings()


settings = get_settings()
