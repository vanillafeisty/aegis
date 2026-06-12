"""Authentication service."""
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy.future import select

from app.core.security import hash_password, verify_password, create_access_token, create_refresh_token, decode_token
from app.models.user import User
from app.schemas import UserRegister, UserLogin


class AuthService:
    @staticmethod
    async def register_user(db: AsyncSession, user_data: UserRegister) -> User:
        """Register a new user."""
        # Check if email exists
        stmt = select(User).filter(User.email == user_data.email)
        result = await db.execute(stmt)
        if result.scalars().first():
            raise ValueError("Email already registered")

        user = User(
            email=user_data.email,
            hashed_password=hash_password(user_data.password),
            full_name=user_data.full_name,
        )
        db.add(user)
        await db.commit()
        await db.refresh(user)
        return user

    @staticmethod
    async def authenticate_user(db: AsyncSession, email: str, password: str) -> User | None:
        """Authenticate user by email and password."""
        stmt = select(User).filter(User.email == email)
        result = await db.execute(stmt)
        user = result.scalars().first()

        if not user:
            return None
        if not verify_password(password, user.hashed_password):
            return None
        return user

    @staticmethod
    def create_tokens(user_id: str) -> dict:
        """Create access and refresh tokens."""
        access_token = create_access_token({"sub": str(user_id)})
        refresh_token = create_refresh_token({"sub": str(user_id)})
        return {"access_token": access_token, "refresh_token": refresh_token}

    @staticmethod
    def verify_token(token: str) -> dict | None:
        """Verify and decode token."""
        try:
            payload = decode_token(token)
            return payload
        except Exception:
            return None
