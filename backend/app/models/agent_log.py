"""Immutable Agent Audit Log model."""
import uuid
from datetime import datetime

from sqlalchemy import DateTime, ForeignKey, JSON, String, Text, func
from sqlalchemy.dialects.postgresql import UUID
from sqlalchemy.orm import Mapped, mapped_column, relationship

from app.db.session import Base


class AgentLog(Base):
    __tablename__ = "agent_logs"

    id: Mapped[uuid.UUID] = mapped_column(UUID(as_uuid=True), primary_key=True, default=uuid.uuid4)
    user_id: Mapped[uuid.UUID] = mapped_column(UUID(as_uuid=True), ForeignKey("users.id", ondelete="CASCADE"), index=True)
    task_id: Mapped[uuid.UUID | None] = mapped_column(UUID(as_uuid=True), ForeignKey("tasks.id"), nullable=True, index=True)
    action: Mapped[str] = mapped_column(String(200), nullable=False)
    tool_used: Mapped[str | None] = mapped_column(String(100))
    # playwright_action | gemini_generate | db_write | api_call
    ai_decision_summary: Mapped[str | None] = mapped_column(Text)
    execution_result: Mapped[str] = mapped_column(String(50), default="success")
    # success | failure | partial_success
    approval_status: Mapped[str] = mapped_column(String(50), default="auto_approved")
    # auto_approved | user_approved | user_rejected | pending_approval
    error_message: Mapped[str | None] = mapped_column(Text)
    metadata_json: Mapped[dict | None] = mapped_column(JSON)
    timestamp: Mapped[datetime] = mapped_column(DateTime(timezone=True), server_default=func.now(), index=True)

    user = relationship("User", back_populates="agent_logs")
    task = relationship("Task", back_populates="agent_logs")
