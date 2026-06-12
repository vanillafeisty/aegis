"""Authentication routes."""
import logging

from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.ext.asyncio import AsyncSession

from app.db.session import get_db
from app.schemas import UserRegister, UserLogin, TokenResponse, UserResponse
from app.services.auth_service import AuthService

router = APIRouter()
logger = logging.getLogger("aegis")


@router.post("/register", response_model=TokenResponse, status_code=201)
async def register(user_data: UserRegister, db: AsyncSession = Depends(get_db)):
    """Register a new user."""
    try:
        user = await AuthService.register_user(db, user_data)
        tokens = AuthService.create_tokens(user.id)
        return TokenResponse(
            access_token=tokens["access_token"],
            refresh_token=tokens["refresh_token"],
        )
    except ValueError as e:
        raise HTTPException(status_code=400, detail=str(e))
    except Exception as e:
        logger.error(f"Registration error: {e}")
        raise HTTPException(status_code=500, detail="Registration failed")


@router.post("/login", response_model=TokenResponse)
async def login(credentials: UserLogin, db: AsyncSession = Depends(get_db)):
    """Login user."""
    user = await AuthService.authenticate_user(db, credentials.email, credentials.password)
    if not user:
        raise HTTPException(status_code=401, detail="Invalid credentials")

    tokens = AuthService.create_tokens(user.id)
    return TokenResponse(
        access_token=tokens["access_token"],
        refresh_token=tokens["refresh_token"],
    )


@router.post("/refresh", response_model=TokenResponse)
async def refresh_token(refresh_token: str):
    """Refresh access token."""
    payload = AuthService.verify_token(refresh_token)
    if not payload or payload.get("type") != "refresh":
        raise HTTPException(status_code=401, detail="Invalid refresh token")

    user_id = payload.get("sub")
    tokens = AuthService.create_tokens(user_id)
    return TokenResponse(
        access_token=tokens["access_token"],
        refresh_token=tokens["refresh_token"],
    )


@router.get("/me", response_model=UserResponse)
async def get_current_user(
    token: str = None, db: AsyncSession = Depends(get_db)
):
    """Get current user info."""
    # Token extraction from header would be done by middleware in real app
    if not token:
        raise HTTPException(status_code=401, detail="Not authenticated")

    payload = AuthService.verify_token(token)
    if not payload:
        raise HTTPException(status_code=401, detail="Invalid token")

    # Fetch user from DB
    from sqlalchemy.future import select
    from app.models.user import User

    user_id = payload.get("sub")
    stmt = select(User).filter(User.id == user_id)
    result = await db.execute(stmt)
    user = result.scalars().first()

    if not user:
        raise HTTPException(status_code=404, detail="User not found")

    return user
