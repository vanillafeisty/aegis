"""Pydantic schemas for request/response validation."""
from datetime import datetime
from typing import Optional
from uuid import UUID

from pydantic import BaseModel, EmailStr, Field


# ── Auth Schemas ──────────────────────────────────────────────────────────────
class UserRegister(BaseModel):
    email: EmailStr
    password: str = Field(..., min_length=8, max_length=128)  # ADD max_length
    full_name: str = Field(..., min_length=2, max_length=255)


class UserLogin(BaseModel):
    email: EmailStr
    password: str


class TokenResponse(BaseModel):
    access_token: str
    refresh_token: str
    token_type: str = "bearer"


class RefreshTokenRequest(BaseModel):
    refresh_token: str


class PasswordResetRequest(BaseModel):
    email: EmailStr


class PasswordReset(BaseModel):
    token: str
    new_password: str = Field(..., min_length=8)


class UserResponse(BaseModel):
    id: UUID
    email: str
    full_name: str
    role: str
    is_active: bool
    is_verified: bool
    autonomy_mode: str
    onboarding_complete: bool
    created_at: datetime

    class Config:
        from_attributes = True


# ── Agent Command ─────────────────────────────────────────────────────────────
class AgentCommand(BaseModel):
    command: str = Field(..., min_length=5, max_length=1000)
    mode: str = Field(default="assisted")  # manual | assisted | autonomous


class TaskResponse(BaseModel):
    id: UUID
    user_id: UUID
    intent: str
    status: str
    autonomy_mode: str
    plan_json: Optional[dict] = None
    result_json: Optional[dict] = None
    error_message: Optional[str] = None
    created_at: datetime
    completed_at: Optional[datetime] = None

    class Config:
        from_attributes = True


class TaskApproval(BaseModel):
    approved: bool
    edits: Optional[dict] = None


# ── Job Search ────────────────────────────────────────────────────────────────
class JobSearchRequest(BaseModel):
    query: str
    location: Optional[str] = None
    remote: Optional[str] = None
    experience_level: Optional[str] = None
    company_size: Optional[str] = None
    limit: int = Field(default=20, le=50)


class JobResponse(BaseModel):
    id: str
    title: str
    company: str
    location: str
    url: str
    match_score: float
    jd_summary: Optional[str] = None
    salary_range: Optional[str] = None
    posted_date: Optional[str] = None


# ── Resume ────────────────────────────────────────────────────────────────────
class ResumeAnalysisResponse(BaseModel):
    ats_score: float
    skills: list[str]
    suggestions: dict
    match_score: Optional[float] = None
    gaps: Optional[list[str]] = None


class SkillGapAnalysis(BaseModel):
    missing_skills: list[str]
    target_jd: str


# ── Content ───────────────────────────────────────────────────────────────────
class ContentGenerationRequest(BaseModel):
    topic: Optional[str] = None
    image_base64: Optional[str] = None
    tone: str = Field(default="professional")


class ContentResponse(BaseModel):
    post_text: str
    hashtags: list[str]
    preview_html: str


# ── Recruiter Outreach ────────────────────────────────────────────────────────
class RecruiterOutreachRequest(BaseModel):
    recruiter_id: Optional[UUID] = None
    message_type: str = "connection_request"
    content: Optional[str] = None


# ── Application ───────────────────────────────────────────────────────────────
class ApplicationCreate(BaseModel):
    company: str
    position: str
    job_posting_url: Optional[str] = None


class ApplicationUpdate(BaseModel):
    status: Optional[str] = None
    notes: Optional[str] = None


class ApplicationResponse(BaseModel):
    id: UUID
    company: str
    position: str
    status: str
    match_score: Optional[float]
    applied_at: Optional[datetime]
    created_at: datetime

    class Config:
        from_attributes = True


# ── Analytics ─────────────────────────────────────────────────────────────────
class AnalyticsSummary(BaseModel):
    applications_count: int
    applications_this_week: int
    response_rate: float
    top_skills: list[str]
    connection_requests_sent: int
    connection_acceptance_rate: float
    posts_published: int
    engagement_rate: float
