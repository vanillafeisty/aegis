"""
LinkedIn Session Manager
Validates the li_at session cookie by making an HTTP request to LinkedIn's
Voyager API.  No browser or Playwright required.
"""

import os
import logging
from typing import Dict, Any

import httpx

logger = logging.getLogger(__name__)

# Human-readable error returned when the cookie is stale
COOKIE_EXPIRED_MESSAGE = (
    "Your LinkedIn session cookie (li_at) has expired or is invalid. "
    "To fix this:\n"
    "  1. Open Chrome / Firefox and log in to linkedin.com.\n"
    "  2. Open DevTools (F12) -> Application -> Cookies -> https://www.linkedin.com.\n"
    "  3. Copy the value of the 'li_at' cookie.\n"
    "  4. Paste it as LINKEDIN_SESSION_COOKIE in your backend/.env file.\n"
    "  5. Restart the backend server."
)


async def validate_session() -> Dict[str, Any]:
    """
    Validate the LinkedIn session cookie by calling the Voyager identity API.

    Returns:
        {"valid": bool, "message": str}
    """
    session_cookie = os.getenv("LINKEDIN_SESSION_COOKIE", "").strip()
    if not session_cookie:
        return {"valid": False, "message": "LINKEDIN_SESSION_COOKIE is not set in your .env file."}

    try:
        from linkedin_http import get_authenticated_client

        async with get_authenticated_client(timeout=15.0) as client:
            resp = await client.get("/identity/profiles/me")

            if resp.status_code == 200:
                data = resp.json()
                name = data.get("firstName", "") + " " + data.get("lastName", "")
                name = name.strip() or data.get("publicIdentifier", "User")
                return {"valid": True, "message": f"LinkedIn session cookie is active. Logged in as {name}."}

            if resp.status_code in (401, 403):
                return {"valid": False, "message": COOKIE_EXPIRED_MESSAGE}

            return {"valid": False, "message": f"Unexpected LinkedIn API response ({resp.status_code})."}

    except ValueError as ve:
        return {"valid": False, "message": str(ve)}
    except Exception as ex:
        logger.error(f"Session validation error: {ex}")
        return {"valid": False, "message": f"Unexpected error during validation: {ex}"}
