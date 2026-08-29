"""
LinkedIn Post Agent — Posts content to LinkedIn feed.
Uses the LinkedIn REST API (OAuth) or the Voyager API (session cookie).
No Playwright or browser automation required.
"""

import os
import asyncio
import logging
import time
from pathlib import Path
from typing import Dict, Any, Optional

import httpx

logger = logging.getLogger(__name__)

from linkedin_http import (
    get_authenticated_client,
    COOKIE_EXPIRED_MESSAGE,
)


async def download_image(url: str) -> str:
    """Download image to a temporary file and return the path."""
    suffix = Path(url).suffix or '.jpg'
    if suffix not in ['.jpg', '.jpeg', '.png', '.gif', '.webp']:
        suffix = '.jpg'

    temp_dir = Path("temp_media")
    temp_dir.mkdir(exist_ok=True)
    temp_file = temp_dir / f"temp_upload_{int(time.time())}{suffix}"

    async with httpx.AsyncClient() as client:
        response = await client.get(url, follow_redirects=True)
        if response.status_code == 200:
            with open(temp_file, 'wb') as f:
                f.write(response.content)
            return str(temp_file.absolute())
    raise Exception(f"Failed to download image from {url}")


async def _post_via_voyager(content: str) -> Dict[str, Any]:
    """
    Post text content to LinkedIn using the Voyager share API with the
    session cookie.  This is the fallback when OAuth is not available.
    """
    try:
        async with get_authenticated_client() as client:
            # First get our own profile to know our member URN
            profile_resp = await client.get("/identity/profiles/me")
            if profile_resp.status_code in (401, 403):
                return {"status": "cookie_expired", "message": COOKIE_EXPIRED_MESSAGE, "content": content}

            if profile_resp.status_code != 200:
                return {"status": "error", "message": f"Failed to fetch profile ({profile_resp.status_code})", "content": content}

            profile_data = profile_resp.json()
            member_urn = profile_data.get("entityUrn", "")
            if not member_urn:
                # Try with the miniProfile
                member_urn = profile_data.get("miniProfile", {}).get("entityUrn", "")

            # Build the share payload
            payload = {
                "visibilityScope": "PUBLIC",
                "commentary": content,
                "origin": "FEED",
            }

            share_resp = await client.post(
                "/feed/shares",
                json=payload,
                headers={"Content-Type": "application/json"},
            )

            if share_resp.status_code in (200, 201):
                logger.info("Successfully posted to LinkedIn via Voyager API")
                return {
                    "status": "success",
                    "message": "Post published to LinkedIn",
                    "content": content,
                    "platform": "LinkedIn",
                }

            if share_resp.status_code in (401, 403):
                return {"status": "cookie_expired", "message": COOKIE_EXPIRED_MESSAGE, "content": content}

            logger.error(f"Voyager share failed ({share_resp.status_code}): {share_resp.text[:300]}")
            return {"status": "error", "message": f"LinkedIn API error ({share_resp.status_code})", "content": content}

    except ValueError as ve:
        return {"status": "cookie_expired", "message": str(ve), "content": content}
    except Exception as e:
        logger.error(f"Voyager post error: {e}")
        return {"status": "error", "message": str(e), "content": content}


async def post_to_linkedin(content: str, image_path: str = None) -> Dict[str, Any]:
    """
    Post content to LinkedIn feed.

    Args:
        content: The text content to post
        image_path: Optional path or URL of an image (currently only text posts
                    are supported via the cookie fallback; image posts require OAuth)

    Returns:
        Dictionary with post result
    """
    temp_image_path = None
    try:
        session_cookie = os.getenv('LINKEDIN_SESSION_COOKIE')
        if not session_cookie:
            return {
                "status": "error",
                "message": "LinkedIn session cookie not set",
                "content": content,
            }

        # If we have an image URL, download it (might be needed if OAuth path handles it)
        if image_path and image_path.startswith(('http://', 'https://')):
            try:
                logger.info(f"Downloading image from: {image_path}")
                temp_image_path = await download_image(image_path)
                image_path = temp_image_path
            except Exception as e:
                logger.error(f"Failed to download image: {e}")
                return {
                    "status": "error",
                    "message": f"Failed to download image: {str(e)}",
                    "content": content,
                }

        logger.info(f"Posting to LinkedIn: {content[:50]}...")

        # Post via Voyager API using session cookie
        result = await _post_via_voyager(content)
        if image_path and result.get("status") == "success":
            result["message"] += " (image attachment requires OAuth — text-only posted)"

        return result

    except Exception as e:
        logger.error(f"LinkedIn post agent error: {e}")
        return {"status": "error", "message": str(e), "content": content}
    finally:
        if temp_image_path and os.path.exists(temp_image_path):
            try:
                os.remove(temp_image_path)
            except Exception:
                pass


# Async wrapper for integration
async def run(content: str, image_path: str = None) -> Dict[str, Any]:
    """Run the post agent."""
    return await post_to_linkedin(content, image_path)
