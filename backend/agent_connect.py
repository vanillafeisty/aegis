"""
LinkedIn Connect Agent — Sends connection requests via the Voyager API.
No Playwright or browser automation required.
"""

import os
import logging
from typing import Dict, Any, Optional

import httpx

logger = logging.getLogger(__name__)

from linkedin_http import (
    get_authenticated_client,
    resolve_profile_id,
    get_profile_by_public_id,
    COOKIE_EXPIRED_MESSAGE,
)


async def send_connection(profile_url: str, message: str = None) -> Dict[str, Any]:
    """
    Send a connection request to a LinkedIn profile.

    Args:
        profile_url: Full LinkedIn profile URL (e.g. https://www.linkedin.com/in/john-doe/)
        message: Optional connection note (under 300 chars)

    Returns:
        Dictionary with connection result
    """
    try:
        session_cookie = os.getenv('LINKEDIN_SESSION_COOKIE')
        if not session_cookie:
            return {"status": "error", "message": "LinkedIn session cookie not set"}

        logger.info(f"Sending connection to: {profile_url}")

        # Resolve the public identifier from the URL
        public_id = await resolve_profile_id(profile_url)
        if not public_id:
            return {"status": "error", "message": f"Could not parse profile URL: {profile_url}"}

        # Look up the profile to get the member URN
        profile = await get_profile_by_public_id(public_id)
        if not profile:
            return {"status": "error", "message": f"Could not find LinkedIn profile for: {public_id}"}

        # Extract the entity URN (e.g. "urn:li:fsd_profile:ACoAA...")
        entity_urn = profile.get("entityUrn", "")
        if not entity_urn:
            return {"status": "error", "message": "Could not determine profile URN"}

        # The profile ID is the part after the last colon
        profile_member_id = entity_urn.split(":")[-1]

        # Build the invitation payload
        payload = {
            "trackingId": os.urandom(8).hex(),
            "inviterProfileId": None,  # will be set by LinkedIn
            "inviteeProfileId": profile_member_id,
        }

        # Add a message/note if provided
        if message:
            # LinkedIn limits connection notes to 300 characters
            truncated = message[:300]
            payload["message"] = truncated

        async with get_authenticated_client() as client:
            resp = await client.post(
                "/growth/invitations",
                json=payload,
                headers={"Content-Type": "application/json"},
            )

            if resp.status_code in (200, 201):
                logger.info(f"Connection request sent to {public_id}")
                return {
                    "status": "success",
                    "message": "Connection request sent",
                    "profile_url": profile_url,
                }

            if resp.status_code in (401, 403):
                return {"status": "cookie_expired", "message": COOKIE_EXPIRED_MESSAGE}

            if resp.status_code == 422:
                return {
                    "status": "error",
                    "message": "Already connected, invitation pending, or profile not accepting connections",
                    "profile_url": profile_url,
                }

            logger.error(f"Connection request failed ({resp.status_code}): {resp.text[:300]}")
            return {
                "status": "error",
                "message": f"LinkedIn API error ({resp.status_code})",
                "profile_url": profile_url,
            }

    except ValueError as ve:
        return {"status": "cookie_expired", "message": str(ve)}
    except Exception as e:
        logger.error(f"Connect agent error: {e}")
        return {"status": "error", "message": str(e)}


async def run(profile_url: str, message: str = None) -> Dict[str, Any]:
    """Run the connection agent."""
    return await send_connection(profile_url, message)
