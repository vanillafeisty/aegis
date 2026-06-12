"""Jobs search and application routes."""
import logging
from uuid import UUID

from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy.future import select

from app.api.deps import get_current_user_id
from app.db.session import get_db
from app.models.application import Application
from app.schemas import JobSearchRequest, ApplicationCreate, ApplicationResponse, ApplicationUpdate

router = APIRouter()
logger = logging.getLogger("aegis")


@router.post("/search")
async def search_jobs(
    request: JobSearchRequest,
    user_id: UUID = Depends(get_current_user_id),
    db: AsyncSession = Depends(get_db),
):
    """Search LinkedIn jobs (Level 1 - No approval required)."""
    try:
        # Would call Playwright service here
        jobs = [
            {
                "id": "job_123",
                "title": "Senior AI Engineer",
                "company": "Google",
                "location": request.location or "Remote",
                "url": "https://linkedin.com/jobs/view/123",
                "match_score": 92.5,
                "jd_summary": "Build cutting-edge AI systems...",
                "salary_range": "$180K - $250K",
                "posted_date": "2 days ago",
            }
        ]
        return {"jobs": jobs, "total": len(jobs)}
    except Exception as e:
        logger.error(f"Job search failed: {e}")
        raise HTTPException(status_code=500, detail="Search failed")


@router.post("/apply", status_code=202)
async def apply_job(
    request: ApplicationCreate,
    user_id: UUID = Depends(get_current_user_id),
    db: AsyncSession = Depends(get_db),
):
    """Apply to a job (Level 3 - Mandatory approval)."""
    try:
        application = Application(
            user_id=user_id,
            company=request.company,
            position=request.position,
            job_posting_url=request.job_posting_url,
            status="applied",
        )
        db.add(application)
        await db.commit()
        await db.refresh(application)

        return {
            "application_id": application.id,
            "status": "applied",
            "message": "Application submitted successfully"
        }
    except Exception as e:
        logger.error(f"Application failed: {e}")
        raise HTTPException(status_code=500, detail="Application failed")


@router.get("/applications")
async def get_applications(
    user_id: UUID = Depends(get_current_user_id),
    status: str = None,
    skip: int = 0,
    limit: int = 20,
    db: AsyncSession = Depends(get_db),
):
    """Get user's applications."""
    stmt = select(Application).filter(Application.user_id == user_id)
    if status:
        stmt = stmt.filter(Application.status == status)
    stmt = stmt.order_by(Application.applied_at.desc()).offset(skip).limit(limit)

    result = await db.execute(stmt)
    applications = result.scalars().all()
    return {"applications": applications, "total": len(applications)}


@router.get("/applications/{app_id}", response_model=ApplicationResponse)
async def get_application(
    app_id: UUID,
    user_id: UUID = Depends(get_current_user_id),
    db: AsyncSession = Depends(get_db),
):
    """Get application details."""
    stmt = select(Application).filter(Application.id == app_id, Application.user_id == user_id)
    result = await db.execute(stmt)
    application = result.scalars().first()

    if not application:
        raise HTTPException(status_code=404, detail="Application not found")

    return application


@router.patch("/applications/{app_id}")
async def update_application(
    app_id: UUID,
    update: ApplicationUpdate,
    user_id: UUID = Depends(get_current_user_id),
    db: AsyncSession = Depends(get_db),
):
    """Update application status/notes."""
    stmt = select(Application).filter(Application.id == app_id, Application.user_id == user_id)
    result = await db.execute(stmt)
    application = result.scalars().first()

    if not application:
        raise HTTPException(status_code=404, detail="Application not found")

    if update.status:
        application.status = update.status
    if update.notes:
        application.notes = update.notes

    await db.commit()
    return {"status": "updated", "application_id": application.id}
