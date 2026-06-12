"""WebSocket routes for real-time dashboard updates."""
import json
import logging

from fastapi import APIRouter, WebSocketException, websocket

router = APIRouter()
logger = logging.getLogger("aegis")

# Store active WebSocket connections per user
connections: dict = {}


@router.websocket("/ws/{user_id}")
async def websocket_endpoint(websocket: websocket.WebSocket, user_id: str):
    """WebSocket endpoint for real-time updates."""
    try:
        await websocket.accept()
        logger.info(f"✅ WebSocket connected for user {user_id}")

        connections[user_id] = websocket

        # Keep connection alive
        while True:
            # Receive heartbeat/messages from client
            data = await websocket.receive_text()
            try:
                message = json.loads(data)
                logger.debug(f"Received: {message}")
            except json.JSONDecodeError:
                logger.warning(f"Invalid JSON: {data}")

    except Exception as e:
        logger.error(f"WebSocket error: {e}")
    finally:
        if user_id in connections:
            del connections[user_id]
        logger.info(f"❌ WebSocket disconnected for user {user_id}")


async def broadcast_to_user(user_id: str, message: dict):
    """Send message to user's WebSocket."""
    if user_id in connections:
        try:
            await connections[user_id].send_json(message)
            logger.debug(f"Broadcast to {user_id}: {message['type']}")
        except Exception as e:
            logger.error(f"Broadcast failed: {e}")


async def notify_task_update(user_id: str, task_id: str, status: str):
    """Notify user of task status update."""
    await broadcast_to_user(user_id, {
        "type": "task_update",
        "task_id": task_id,
        "status": status,
        "timestamp": str(__import__('datetime').datetime.utcnow()),
    })


async def notify_approval_required(user_id: str, task_id: str, plan: dict):
    """Notify user that approval is required."""
    await broadcast_to_user(user_id, {
        "type": "approval_required",
        "task_id": task_id,
        "plan": plan,
        "timestamp": str(__import__('datetime').datetime.utcnow()),
    })


async def notify_job_match(user_id: str, job: dict):
    """Notify user of new job match."""
    await broadcast_to_user(user_id, {
        "type": "job_match",
        "job": job,
        "timestamp": str(__import__('datetime').datetime.utcnow()),
    })


async def notify_recruiter_message(user_id: str, message: dict):
    """Notify user of recruiter message."""
    await broadcast_to_user(user_id, {
        "type": "recruiter_message",
        "message": message,
        "timestamp": str(__import__('datetime').datetime.utcnow()),
    })
