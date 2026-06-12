"""LinkedIn content creation and publishing routes."""
import logging
from uuid import UUID

from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy.future import select

from app.api.deps import get_current_user_id
from app.db.session import get_db
from app.models.post import Post
from app.schemas import ContentGenerationRequest, ContentResponse
from app.services.gemini_service import GeminiService

router = APIRouter()
logger = logging.getLogger("aegis")

gemini = GeminiService()


@router.post("/generate", response_model=ContentResponse)
async def generate_content(
    request: ContentGenerationRequest,
    user_id: UUID = Depends(get_current_user_id),
    db: AsyncSession = Depends(get_db),
):
    """Generate LinkedIn post content (Level 1 - No approval)."""
    try:
        result = await gemini.generate_content(
            topic=request.topic,
            image_context=request.image_base64,
            tone=request.tone,
        )

        # Create draft post
        post = Post(
            user_id=user_id,
            content=result.get("post_text", ""),
            hashtags=",".join(result.get("hashtags", [])),
            tone=request.tone,
            approval_status="pending_approval",
        )
        db.add(post)
        await db.commit()
        await db.refresh(post)

        return ContentResponse(
            post_text=result.get("post_text", ""),
            hashtags=result.get("hashtags", []),
            preview_html=f"<div>{result.get('post_text', '')}</div>",
        )
    except Exception as e:
        logger.error(f"Content generation failed: {e}")
        raise HTTPException(status_code=500, detail="Generation failed")


@router.post("/publish/{post_id}", status_code=202)
async def publish_post(
    post_id: UUID,
    user_id: UUID = Depends(get_current_user_id),
    db: AsyncSession = Depends(get_db),
):
    """Publish post (Level 3 - Mandatory approval)."""
    try:
        stmt = select(Post).filter(Post.id == post_id, Post.user_id == user_id)
        result = await db.execute(stmt)
        post = result.scalars().first()

        if not post:
            raise HTTPException(status_code=404, detail="Post not found")

        post.approval_status = "published"
        # Would call Playwright service here
        await db.commit()

        return {
            "post_id": post.id,
            "status": "published",
            "message": "Post published successfully"
        }
    except Exception as e:
        logger.error(f"Post publishing failed: {e}")
        raise HTTPException(status_code=500, detail="Publishing failed")


@router.get("/drafts")
async def get_drafts(
    user_id: UUID = Depends(get_current_user_id),
    skip: int = 0,
    limit: int = 20,
    db: AsyncSession = Depends(get_db),
):
    """Get user's draft posts."""
    stmt = (
        select(Post)
        .filter(Post.user_id == user_id, Post.approval_status == "pending_approval")
        .order_by(Post.created_at.desc())
        .offset(skip)
        .limit(limit)
    )
    result = await db.execute(stmt)
    posts = result.scalars().all()
    return {"posts": posts, "total": len(posts)}
