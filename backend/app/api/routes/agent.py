"""Agent command and task routes."""
import logging
from uuid import UUID

from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy.future import select

from app.api.deps import get_current_user_id
from app.db.session import get_db
from app.models.task import Task
from app.schemas import AgentCommand, TaskResponse, TaskApproval
from app.services.gemini_service import GeminiService

router = APIRouter()
logger = logging.getLogger("aegis")

gemini = GeminiService()


@router.post("/command", status_code=202)
async def send_command(
    command: AgentCommand,
    user_id: UUID = Depends(get_current_user_id),
    db: AsyncSession = Depends(get_db),
):
    """Submit a high-level command to the agent."""
    try:
        # Step 1: Classify intent
        intent_result = await gemini.classify_intent(command.command)
        intent = intent_result.get("intent", "unknown")

        # Step 2: Create task record
        task = Task(
            user_id=user_id,
            intent=intent,
            command=command.command,
            autonomy_mode=command.mode,
            status="planning",
        )
        db.add(task)
        await db.commit()
        await db.refresh(task)

        # Step 3: Start planning (would normally queue to Celery)
        user_context = {}  # Would retrieve from RAG
        plan = await gemini.decompose_goal(command.command, intent, user_context)
        task.plan_json = plan
        task.status = "awaiting_approval" if plan.get("approval_required") else "executing"
        await db.commit()

        return {
            "task_id": task.id,
            "status": task.status,
            "intent": intent,
            "plan_summary": plan.get("tasks", [])[:3],  # First 3 tasks
        }

    except Exception as e:
        logger.error(f"Command execution failed: {e}")
        raise HTTPException(status_code=500, detail="Command processing failed")


@router.get("/tasks", response_model=list[TaskResponse])
async def get_tasks(
    user_id: UUID = Depends(get_current_user_id),
    status: str = None,
    skip: int = 0,
    limit: int = 20,
    db: AsyncSession = Depends(get_db),
):
    """Get user's agent tasks."""
    stmt = select(Task).filter(Task.user_id == user_id)
    if status:
        stmt = stmt.filter(Task.status == status)
    stmt = stmt.order_by(Task.created_at.desc()).offset(skip).limit(limit)

    result = await db.execute(stmt)
    tasks = result.scalars().all()
    return tasks


@router.get("/tasks/{task_id}", response_model=TaskResponse)
async def get_task(
    task_id: UUID,
    user_id: UUID = Depends(get_current_user_id),
    db: AsyncSession = Depends(get_db),
):
    """Get task details."""
    stmt = select(Task).filter(Task.id == task_id, Task.user_id == user_id)
    result = await db.execute(stmt)
    task = result.scalars().first()

    if not task:
        raise HTTPException(status_code=404, detail="Task not found")

    return task


@router.post("/tasks/{task_id}/approve")
async def approve_task(
    task_id: UUID,
    user_id: UUID = Depends(get_current_user_id),
    approval: TaskApproval = None,
    db: AsyncSession = Depends(get_db),
):
    """Approve or reject a pending task."""
    stmt = select(Task).filter(Task.id == task_id, Task.user_id == user_id)
    result = await db.execute(stmt)
    task = result.scalars().first()

    if not task:
        raise HTTPException(status_code=404, detail="Task not found")

    if approval.approved:
        task.status = "executing"
        # Would queue to Celery here
    else:
        task.status = "rejected"

    await db.commit()
    return {"status": task.status, "task_id": task.id}


@router.delete("/tasks/{task_id}")
async def cancel_task(
    task_id: UUID,
    user_id: UUID = Depends(get_current_user_id),
    db: AsyncSession = Depends(get_db),
):
    """Cancel a pending task."""
    stmt = select(Task).filter(Task.id == task_id, Task.user_id == user_id)
    result = await db.execute(stmt)
    task = result.scalars().first()

    if not task:
        raise HTTPException(status_code=404, detail="Task not found")

    task.status = "cancelled"
    await db.commit()
    return {"status": "cancelled", "task_id": task.id}
