"""LinkedIn Post model."""
import uuid
from datetime import datetime

from sqlalchemy import ARRAY, DateTime, ForeignKey, String, Text, func
from sqlalchemy.dialects.postgresql import UUID
from sqlalchemy.orm import Mapped, mapped_column, relationship

from app.db.session import Base


class Post(Base):
    __tablename__ = "posts"

    id: Mapped[uuid.UUID] = mapped_column(UUID(as_uuid=True), primary_key=True, default=uuid.uuid4)
    user_id: Mapped[uuid.UUID] = mapped_column(UUID(as_uuid=True), ForeignKey("users.id", ondelete="CASCADE"), index=True)
    content: Mapped[str] = mapped_column(Text, nullable=False)
    hashtags: Mapped[str | None] = mapped_column(Text)  # JSON string of list
    image_url: Mapped[str | None] = mapped_column(String(500))
    tone: Mapped[str] = mapped_column(String(50), default="professional")
    approval_status: Mapped[str] = mapped_column(String(50), default="pending_approval")
    # pending_approval | approved | rejected | published | failed
    published_at: Mapped[datetime | None] = mapped_column(DateTime(timezone=True))
    created_at: Mapped[datetime] = mapped_column(DateTime(timezone=True), server_default=func.now())

    user = relationship("User", back_populates="posts")
