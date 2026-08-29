"""
LinkedIn Inbox Agent — Monitors and auto-replies to messages.
Uses the LinkedIn Voyager messaging API with the li_at session cookie.
No Playwright or browser automation required.
"""

import os
import logging
from typing import Dict, Any

logger = logging.getLogger(__name__)

try:
    from groq import Groq
    GROQ_AVAILABLE = True
except ImportError:
    GROQ_AVAILABLE = False
    logging.warning("Groq not available")

from linkedin_http import (
    get_authenticated_client,
    COOKIE_EXPIRED_MESSAGE,
)


async def process_inbox() -> Dict[str, Any]:
    """
    Check LinkedIn inbox and process recent messages with AI-generated replies.
    Reads the latest conversations, generates a reply via Groq, and sends it.
    """
    groq_key = os.getenv('GROQ_API_KEY')
    model = os.getenv('GROQ_MODEL', 'llama-3.3-70b-versatile')
    session_cookie = os.getenv('LINKEDIN_SESSION_COOKIE')

    if not session_cookie:
        return {"status": "error", "message": "LinkedIn session cookie not set"}

    if not GROQ_AVAILABLE or not groq_key:
        return {"status": "error", "message": "Groq AI not available for auto-replies"}

    logger.info("Processing LinkedIn inbox via Voyager API")
    messages_processed = 0

    try:
        async with get_authenticated_client() as client:
            # 1. Fetch recent conversations
            conv_resp = await client.get(
                "/messaging/conversations",
                params={
                    "keyVersion": "LEGACY_INBOX",
                    "start": "0",
                    "count": "5",
                },
            )

            if conv_resp.status_code in (401, 403):
                return {"status": "cookie_expired", "message": COOKIE_EXPIRED_MESSAGE}

            if conv_resp.status_code != 200:
                return {"status": "error", "message": f"Failed to fetch conversations ({conv_resp.status_code})"}

            conv_data = conv_resp.json()
            conversations = conv_data.get("elements", [])

            if not conversations:
                return {
                    "status": "success",
                    "message": "No conversations found in inbox",
                    "messages_processed": 0,
                }

            groq_client = Groq(api_key=groq_key)

            # Process first 3 conversations
            for conv in conversations[:3]:
                conv_urn = conv.get("entityUrn", "")
                if not conv_urn:
                    continue

                # Extract the conversation ID
                conv_id = conv_urn.split(":")[-1]

                try:
                    # 2. Fetch messages in this conversation
                    msg_resp = await client.get(
                        f"/messaging/conversations/{conv_id}/events",
                        params={"count": "3"},
                    )

                    if msg_resp.status_code != 200:
                        logger.warning(f"Failed to fetch messages for conversation {conv_id}")
                        continue

                    msg_data = msg_resp.json()
                    events = msg_data.get("elements", [])

                    if not events:
                        continue

                    # Get the latest message text
                    latest = events[0]
                    message_body = ""

                    event_content = latest.get("eventContent", {})
                    if isinstance(event_content, dict):
                        msg_event = event_content.get("com.linkedin.voyager.messaging.event.MessageEvent", {})
                        if msg_event:
                            body = msg_event.get("body", "")
                            if body:
                                message_body = body

                    # Also try a simpler path
                    if not message_body:
                        message_body = latest.get("body", "") or latest.get("subContent", "")

                    if not message_body:
                        continue

                    # 3. Generate a reply with Groq
                    response = groq_client.chat.completions.create(
                        model=model,
                        messages=[{
                            "role": "user",
                            "content": f"""Generate a professional, friendly reply to this LinkedIn message:

"{message_body}"

Keep it under 280 characters. Be helpful and professional."""
                        }],
                        max_tokens=100,
                        temperature=0.7
                    )

                    reply = response.choices[0].message.content.strip()
                    if reply.startswith('"') and reply.endswith('"'):
                        reply = reply[1:-1]

                    # 4. Send the reply
                    reply_payload = {
                        "eventCreate": {
                            "value": {
                                "com.linkedin.voyager.messaging.create.MessageCreate": {
                                    "body": reply,
                                    "attachments": [],
                                    "attributedBody": {
                                        "text": reply,
                                        "attributes": [],
                                    },
                                }
                            }
                        },
                        "dedupeByClientGeneratedToken": False,
                    }

                    send_resp = await client.post(
                        f"/messaging/conversations/{conv_id}/events",
                        json=reply_payload,
                        headers={"Content-Type": "application/json"},
                    )

                    if send_resp.status_code in (200, 201):
                        messages_processed += 1
                        logger.info(f"Replied to conversation {conv_id}")
                    else:
                        logger.warning(f"Failed to send reply to {conv_id} ({send_resp.status_code})")

                except Exception as e:
                    logger.warning(f"Error processing conversation {conv_id}: {e}")
                    continue

        return {
            "status": "success",
            "message": "Inbox processing completed",
            "messages_processed": messages_processed,
            "details": f"Processed and replied to {messages_processed} messages",
        }

    except ValueError as ve:
        return {"status": "cookie_expired", "message": str(ve)}
    except Exception as e:
        logger.error(f"Inbox processing error: {e}")
        return {"status": "error", "message": str(e)}


async def run() -> Dict[str, Any]:
    """Run the inbox agent."""
    return await process_inbox()
