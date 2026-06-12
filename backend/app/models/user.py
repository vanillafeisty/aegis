"""User model."""
import uuid
from datetime import datetime

from sqlalchemy import Boolean, DateTime, String, Text, func
from sqlalchemy.dialects.postgresql import UUID
from sqlalchemy.orm import Mapped, mapped_column, relationship

from app.db.session import Base


class User(Base):
    __tablename__ = "users"

    id: Mapped[uuid.UUID] = mapped_column(UUID(as_uuid=True), primary_key=True, default=uuid.uuid4)
    email: Mapped[str] = mapped_column(String(255), unique=True, index=True, nullable=False)
    hashed_password: Mapped[str] = mapped_column(String(255), nullable=False)
    full_name: Mapped[str] = mapped_column(String(255), nullable=False)
    role: Mapped[str] = mapped_column(String(50), default="user", nullable=False)  # user | admin
    is_active: Mapped[bool] = mapped_column(Boolean, default=False)  # requires email verify
    is_verified: Mapped[bool] = mapped_column(Boolean, default=False)
    autonomy_mode: Mapped[str] = mapped_column(String(20), default="assisted")
    linkedin_email_enc: Mapped[str | None] = mapped_column(Text, nullable=True)
    linkedin_pass_enc: Mapped[str | None] = mapped_column(Text, nullable=True)
    linkedin_profile_url: Mapped[str | None] = mapped_column(String(500), nullable=True)
    onboarding_complete: Mapped[bool] = mapped_column(Boolean, default=False)
    created_at: Mapped[datetime] = mapped_column(DateTime(timezone=True), server_default=func.now())
    updated_at: Mapped[datetime] = mapped_column(DateTime(timezone=True), server_default=func.now(), onupdate=func.now())

    # Relationships
    tasks = relationship("Task", back_populates="user", cascade="all, delete-orphan")
    applications = relationship("Application", back_populates="user", cascade="all, delete-orphan")
    recruiters = relationship("Recruiter", back_populates="user", cascade="all, delete-orphan")
    messages = relationship("Message", back_populates="user", cascade="all, delete-orphan")
    posts = relationship("Post", back_populates="user", cascade="all, delete-orphan")
    notifications = relationship("Notification", back_populates="user", cascade="all, delete-orphan")
    agent_logs = relationship("AgentLog", back_populates="user", cascade="all, delete-orphan")
    memory_entries = relationship("MemoryStore", back_populates="user", cascade="all, delete-orphan")
