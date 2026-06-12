"""Analytics and reporting routes."""
import logging
from datetime import datetime, timedelta
from uuid import UUID

from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy.future import select
from sqlalchemy import func

from app.api.deps import get_current_user_id
from app.db.session import get_db
from app.models.application import Application
from app.models.post import Post
from app.schemas import AnalyticsSummary

router = APIRouter()
logger = logging.getLogger("aegis")


@router.get("/summary")
async def get_analytics_summary(
    user_id: UUID = Depends(get_current_user_id),
    period: str = "30d",
    db: AsyncSession = Depends(get_db),
) -> AnalyticsSummary:
    """Get analytics summary for user."""
    try:
        # Parse period
        if period == "7d":
            days = 7
        elif period == "30d":
            days = 30
        else:
            days = 7

        start_date = datetime.utcnow() - timedelta(days=days)

        # Applications count
        stmt = select(func.count(Application.id)).filter(
            Application.user_id == user_id,
        )
        result = await db.execute(stmt)
        total_apps = result.scalar() or 0

        # Applications this period
        stmt = select(func.count(Application.id)).filter(
            Application.user_id == user_id,
            Application.applied_at >= start_date,
        )
        result = await db.execute(stmt)
        period_apps = result.scalar() or 0

        # Posts published
        stmt = select(func.count(Post.id)).filter(
            Post.user_id == user_id,
            Post.approval_status == "published",
        )
        result = await db.execute(stmt)
        posts_published = result.scalar() or 0

        return AnalyticsSummary(
            applications_count=total_apps,
            applications_this_week=period_apps if period == "7d" else 0,
            response_rate=75.5,  # Mock
            top_skills=["Python", "AWS", "Machine Learning", "FastAPI"],
            connection_requests_sent=42,
            connection_acceptance_rate=85.0,
            posts_published=posts_published,
            engagement_rate=12.5,
        )

    except Exception as e:
        logger.error(f"Analytics failed: {e}")
        raise HTTPException(status_code=500, detail="Analytics unavailable")


@router.get("/dashboard")
async def get_dashboard_metrics(
    user_id: UUID = Depends(get_current_user_id),
    db: AsyncSession = Depends(get_db),
):
    """Get real-time dashboard metrics."""
    try:
        # Mock dashboard data
        return {
            "agent_status": "idle",
            "pending_approvals": 0,
            "recent_matches": [],
            "active_applications": 12,
            "connection_requests_pending": 3,
            "new_recruiter_messages": 1,
        }
    except Exception as e:
        logger.error(f"Dashboard metrics failed: {e}")
        raise HTTPException(status_code=500, detail="Metrics unavailable")
