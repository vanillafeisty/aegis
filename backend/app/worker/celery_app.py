"""Celery worker configuration and tasks."""
from celery import Celery
from celery.schedules import crontab
import logging

from app.core.config import settings

logger = logging.getLogger("aegis.celery")

celery_app = Celery(
    "aegis",
    broker=settings.REDIS_URL,
    backend=settings.REDIS_URL,
)

celery_app.conf.update(
    task_serializer="json",
    accept_content=["json"],
    result_serializer="json",
    timezone="UTC",
    enable_utc=True,
    task_track_started=True,
    task_time_limit=30 * 60,  # 30 min hard limit
    task_soft_time_limit=25 * 60,  # 25 min soft limit
    worker_prefetch_multiplier=1,
    worker_max_tasks_per_child=1000,
)

# ── Scheduled Tasks ──────────────────────────────────────────────────────────
celery_app.conf.beat_schedule = {
    "scan-new-jobs-every-6h": {
        "task": "app.worker.tasks.scan_new_jobs",
        "schedule": crontab(minute=0, hour="*/6"),  # Every 6 hours
    },
    "send-pending-followups": {
        "task": "app.worker.tasks.send_followup_messages",
        "schedule": crontab(hour=9, minute=0, day_of_week="MON"),  # Monday 9 AM
    },
    "generate-weekly-analytics": {
        "task": "app.worker.tasks.generate_analytics",
        "schedule": crontab(hour=18, minute=0, day_of_week="FRI"),  # Friday 6 PM
    },
}


# ── Task Imports ─────────────────────────────────────────────────────────────
from app.worker.tasks import (  # noqa: E402, F401
    execute_agent_task,
    scan_new_jobs,
    send_followup_messages,
    generate_analytics,
)
