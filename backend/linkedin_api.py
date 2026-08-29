"""
LinkedIn REST API Client
Provides functions to interact with LinkedIn's official REST API using OAuth 2.0 access tokens.
Replaces Playwright browser automation for supported operations.
"""

import os
import logging
from typing import Dict, Any, Optional

import httpx

logger = logging.getLogger(__name__)

# LinkedIn API base URLs
LINKEDIN_API_BASE = "https://api.linkedin.com"
LINKEDIN_USERINFO = f"{LINKEDIN_API_BASE}/v2/userinfo"
LINKEDIN_POSTS_API = f"{LINKEDIN_API_BASE}/rest/posts"


def _headers(access_token: str, restli: bool = True) -> Dict[str, str]:
    """Build standard LinkedIn API headers."""
    h = {
        "Authorization": f"Bearer {access_token}",
        "Content-Type": "application/json",
    }
    if restli:
        # Required for the v2 REST API (versioned endpoints)
        h["LinkedIn-Version"] = "202401"
        h["X-Restli-Protocol-Version"] = "2.0.0"
    return h


async def get_user_profile(access_token: str) -> Dict[str, Any]:
    """
    Get the authenticated user's profile using OpenID Connect userinfo.
    Returns: { sub, name, email, picture, ... }
    """
    async with httpx.AsyncClient() as client:
        resp = await client.get(
            LINKEDIN_USERINFO,
            headers={"Authorization": f"Bearer {access_token}"},
        )

        if resp.status_code == 401:
            return {"status": "error", "message": "Token expired or invalid. Please re-authenticate."}
        if resp.status_code != 200:
            return {"status": "error", "message": f"Profile fetch failed: {resp.text}"}

        profile = resp.json()
        return {
            "status": "success",
            "sub": profile.get("sub", ""),
            "name": profile.get("name", ""),
            "email": profile.get("email", ""),
            "picture": profile.get("picture", ""),
        }


async def create_text_post(access_token: str, user_sub: str, content: str) -> Dict[str, Any]:
    """
    Create a text-only post on LinkedIn using the Posts API.

    Args:
        access_token: Valid OAuth 2.0 access token with w_member_social scope
        user_sub: The user's LinkedIn sub (person URN identifier from OpenID)
        content: The text content of the post

    Returns:
        Dict with status and details
    """
    # The person URN format for the Posts API
    author_urn = f"urn:li:person:{user_sub}"

    payload = {
        "author": author_urn,
        "commentary": content,
        "visibility": "PUBLIC",
        "distribution": {
            "feedDistribution": "MAIN_FEED",
            "targetEntities": [],
            "thirdPartyDistributionChannels": [],
        },
        "lifecycleState": "PUBLISHED",
        "isReshareDisabledByAuthor": False,
    }

    async with httpx.AsyncClient() as client:
        resp = await client.post(
            LINKEDIN_POSTS_API,
            headers=_headers(access_token),
            json=payload,
        )

        if resp.status_code == 401:
            return {
                "status": "error",
                "message": "LinkedIn token expired. Please re-authenticate via the dashboard.",
                "code": "TOKEN_EXPIRED",
            }

        if resp.status_code == 403:
            return {
                "status": "error",
                "message": "Insufficient permissions. Your LinkedIn app may need the 'Share on LinkedIn' or 'Community Management API' product enabled.",
                "code": "INSUFFICIENT_SCOPE",
            }

        if resp.status_code in (200, 201):
            # LinkedIn returns the post ID in the x-restli-id header
            post_id = resp.headers.get("x-restli-id", "unknown")
            logger.info(f"Successfully posted to LinkedIn (post_id={post_id})")
            return {
                "status": "success",
                "message": "Post published to LinkedIn via API",
                "post_id": post_id,
                "content": content,
                "platform": "LinkedIn",
            }

        # Unexpected error
        logger.error(f"LinkedIn post failed ({resp.status_code}): {resp.text}")
        return {
            "status": "error",
            "message": f"LinkedIn API error ({resp.status_code}): {resp.text}",
            "code": "API_ERROR",
        }


async def create_image_post(
    access_token: str, user_sub: str, content: str, image_url: str
) -> Dict[str, Any]:
    """
    Create a post with an image on LinkedIn.
    For image posts, LinkedIn requires a multi-step upload process:
    1. Initialize the upload
    2. Upload the binary
    3. Create the post referencing the uploaded asset

    Falls back to text-only post if image upload fails.
    """
    author_urn = f"urn:li:person:{user_sub}"

    try:
        # Step 1: Initialize image upload
        init_payload = {
            "initializeUploadRequest": {
                "owner": author_urn,
            }
        }

        async with httpx.AsyncClient(timeout=30.0) as client:
            init_resp = await client.post(
                f"{LINKEDIN_API_BASE}/rest/images?action=initializeUpload",
                headers=_headers(access_token),
                json=init_payload,
            )

            if init_resp.status_code not in (200, 201):
                logger.warning(f"Image upload init failed ({init_resp.status_code}), falling back to text post")
                return await create_text_post(access_token, user_sub, content)

            init_data = init_resp.json()
            upload_url = init_data.get("value", {}).get("uploadUrl", "")
            image_urn = init_data.get("value", {}).get("image", "")

            if not upload_url or not image_urn:
                logger.warning("Missing upload URL or image URN, falling back to text post")
                return await create_text_post(access_token, user_sub, content)

            # Step 2: Download the image and upload to LinkedIn
            img_resp = await client.get(image_url, follow_redirects=True)
            if img_resp.status_code != 200:
                logger.warning(f"Failed to download image from {image_url}, falling back to text post")
                return await create_text_post(access_token, user_sub, content)

            upload_resp = await client.put(
                upload_url,
                content=img_resp.content,
                headers={
                    "Authorization": f"Bearer {access_token}",
                    "Content-Type": "application/octet-stream",
                },
            )

            if upload_resp.status_code not in (200, 201):
                logger.warning(f"Image binary upload failed ({upload_resp.status_code}), falling back to text post")
                return await create_text_post(access_token, user_sub, content)

            # Step 3: Create post with image reference
            post_payload = {
                "author": author_urn,
                "commentary": content,
                "visibility": "PUBLIC",
                "distribution": {
                    "feedDistribution": "MAIN_FEED",
                    "targetEntities": [],
                    "thirdPartyDistributionChannels": [],
                },
                "content": {
                    "media": {
                        "title": "Image",
                        "id": image_urn,
                    }
                },
                "lifecycleState": "PUBLISHED",
                "isReshareDisabledByAuthor": False,
            }

            post_resp = await client.post(
                LINKEDIN_POSTS_API,
                headers=_headers(access_token),
                json=post_payload,
            )

            if post_resp.status_code in (200, 201):
                post_id = post_resp.headers.get("x-restli-id", "unknown")
                logger.info(f"Successfully posted to LinkedIn with image (post_id={post_id})")
                return {
                    "status": "success",
                    "message": "Post published to LinkedIn with image via API",
                    "post_id": post_id,
                    "content": content,
                    "platform": "LinkedIn",
                }

            logger.error(f"Image post creation failed ({post_resp.status_code}): {post_resp.text}")
            # Fall back to text-only
            return await create_text_post(access_token, user_sub, content)

    except Exception as e:
        logger.error(f"Image post error: {e}. Falling back to text-only post.")
        return await create_text_post(access_token, user_sub, content)


async def post_content(access_token: str, content: str, image_url: Optional[str] = None) -> Dict[str, Any]:
    """
    High-level post function. Gets user profile, then creates text or image post.
    This is the main entry point for agents.
    """
    # First get the user's sub (person ID) from the token
    profile = await get_user_profile(access_token)
    if profile.get("status") == "error":
        return profile

    user_sub = profile["sub"]

    if image_url and image_url.startswith(("http://", "https://")):
        return await create_image_post(access_token, user_sub, content, image_url)
    else:
        return await create_text_post(access_token, user_sub, content)
