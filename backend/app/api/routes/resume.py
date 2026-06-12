"""Resume analysis and management routes."""
import logging
from uuid import UUID

from fastapi import APIRouter, Depends, File, HTTPException, UploadFile
from sqlalchemy.ext.asyncio import AsyncSession

from app.services.gemini_service import GeminiService

router = APIRouter()
logger = logging.getLogger("aegis")

gemini = GeminiService()


@router.post("/upload")
async def upload_resume(
    file: UploadFile = File(...),
    user_id: UUID = None,
    db: AsyncSession = Depends(None),
):
    """Upload and parse resume (Level 1 - No approval)."""
    try:
        # Read file
        contents = await file.read()

        # Mock parsing (would use PyPDF2/python-docx)
        resume_text = f"Parsed resume from {file.filename}"

        # Analyze with Gemini
        analysis = await gemini.analyze_resume(resume_text)

        return {
            "resume_id": f"res_{user_id}",
            "filename": file.filename,
            "ats_score": analysis.get("ats_score", 0),
            "skills": analysis.get("skills", []),
            "parse_summary": f"Extracted {len(analysis.get('skills', []))} skills",
        }
    except Exception as e:
        logger.error(f"Resume upload failed: {e}")
        raise HTTPException(status_code=500, detail="Upload failed")


@router.post("/analysis")
async def analyze_resume(
    resume_id: str,
    jd_text: str = None,
    user_id: UUID = None,
):
    """Analyze resume and generate insights (Level 1 - No approval)."""
    try:
        # Mock resume retrieval
        resume_text = "Resume content placeholder"

        # Analyze
        analysis = await gemini.analyze_resume(resume_text, jd_text)

        return {
            "match_score": analysis.get("match_score"),
            "ats_score": analysis.get("ats_score"),
            "skills": analysis.get("matched_skills", []),
            "gaps": analysis.get("missing_skills", []),
            "suggestions": analysis.get("suggestions", []),
        }
    except Exception as e:
        logger.error(f"Resume analysis failed: {e}")
        raise HTTPException(status_code=500, detail="Analysis failed")


@router.post("/skill-gap")
async def skill_gap_analysis(
    resume_id: str,
    jd_text: str,
    user_id: UUID = None,
):
    """Analyze skill gaps between resume and job (Level 1)."""
    try:
        resume_text = "Resume placeholder"
        analysis = await gemini.analyze_resume(resume_text, jd_text)

        return {
            "missing_skills": analysis.get("missing_skills", []),
            "in_demand": True,
            "improvement_suggestions": analysis.get("suggestions", []),
        }
    except Exception as e:
        logger.error(f"Skill gap analysis failed: {e}")
        raise HTTPException(status_code=500, detail="Analysis failed")
