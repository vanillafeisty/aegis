"""
LinkedIn OAuth 2.0 Token Manager
Handles the 3-legged OAuth flow: authorization URL, code exchange, token storage/refresh.
"""

import os
import json
import time
import logging
import secrets
from pathlib import Path
from typing import Optional, Dict, Any

import httpx

logger = logging.getLogger(__name__)

# Token file stored next to .env in the backend directory
TOKEN_FILE = Path(__file__).resolve().parent / "linkedin_token.json"

# LinkedIn OAuth 2.0 endpoints
LINKEDIN_AUTH_URL = "https://www.linkedin.com/oauth/v2/authorization"
LINKEDIN_TOKEN_URL = "https://www.linkedin.com/oauth/v2/accessToken"
LINKEDIN_USERINFO_URL = "https://api.linkedin.com/v2/userinfo"

# Scopes: OpenID Connect + posting permission
# w_member_social allows creating posts on behalf of the user
DEFAULT_SCOPES = "openid profile email w_member_social"


def _get_oauth_config() -> Dict[str, str]:
    """Read OAuth credentials from environment."""
    return {
        "client_id": os.getenv("LINKEDIN_CLIENT_ID", ""),
        "client_secret": os.getenv("CLIENT_SECRET", ""),
        "redirect_uri": os.getenv("REDIRECT_URI", "http://localhost:8000/callback"),
    }


def get_auth_url(state: Optional[str] = None) -> str:
    """
    Build the LinkedIn authorization URL that the user should be redirected to.
    Returns the full URL including client_id, redirect_uri, scopes, and state.
    """
    cfg = _get_oauth_config()
    if not cfg["client_id"]:
        raise ValueError("LINKEDIN_CLIENT_ID not set in environment")

    if state is None:
        state = secrets.token_urlsafe(32)

    params = {
        "response_type": "code",
        "client_id": cfg["client_id"],
        "redirect_uri": cfg["redirect_uri"],
        "scope": DEFAULT_SCOPES,
        "state": state,
    }
    query = "&".join(f"{k}={httpx.URL('', params={k: v}).params[k]}" for k, v in params.items())
    # Build manually to avoid double-encoding
    import urllib.parse
    query = urllib.parse.urlencode(params)
    url = f"{LINKEDIN_AUTH_URL}?{query}"
    logger.info(f"Generated LinkedIn auth URL (state={state[:8]}...)")
    return url, state


async def exchange_code_for_token(code: str) -> Dict[str, Any]:
    """
    Exchange the authorization code for an access token.
    Returns the full token response dict including access_token, expires_in, scope.
    """
    cfg = _get_oauth_config()
    if not cfg["client_id"] or not cfg["client_secret"]:
        raise ValueError("LinkedIn OAuth credentials (CLIENT_ID / CLIENT_SECRET) not set")

    payload = {
        "grant_type": "authorization_code",
        "code": code,
        "redirect_uri": cfg["redirect_uri"],
        "client_id": cfg["client_id"],
        "client_secret": cfg["client_secret"],
    }

    async with httpx.AsyncClient() as client:
        response = await client.post(
            LINKEDIN_TOKEN_URL,
            data=payload,
            headers={"Content-Type": "application/x-www-form-urlencoded"},
        )

        if response.status_code != 200:
            error_body = response.text
            logger.error(f"Token exchange failed ({response.status_code}): {error_body}")
            raise ValueError(f"Token exchange failed: {error_body}")

        token_data = response.json()
        logger.info("Successfully exchanged authorization code for access token")
        return token_data


async def fetch_profile(access_token: str) -> Dict[str, Any]:
    """
    Fetch the authenticated user's profile via OpenID Connect userinfo endpoint.
    Returns dict with sub, name, email, picture, etc.
    """
    async with httpx.AsyncClient() as client:
        response = await client.get(
            LINKEDIN_USERINFO_URL,
            headers={"Authorization": f"Bearer {access_token}"},
        )

        if response.status_code == 401:
            raise ValueError("Access token is expired or invalid")
        if response.status_code != 200:
            raise ValueError(f"Profile fetch failed ({response.status_code}): {response.text}")

        return response.json()


def save_token(token_data: Dict[str, Any], profile: Optional[Dict[str, Any]] = None) -> None:
    """
    Persist the token data to a local JSON file.
    Adds a calculated `expires_at` timestamp for easy expiry checking.
    """
    stored = {
        "access_token": token_data["access_token"],
        "expires_in": token_data.get("expires_in", 5184000),  # default 60 days
        "expires_at": int(time.time()) + token_data.get("expires_in", 5184000),
        "scope": token_data.get("scope", ""),
        "token_type": token_data.get("token_type", "Bearer"),
        "saved_at": int(time.time()),
    }

    if profile:
        stored["profile"] = {
            "sub": profile.get("sub", ""),
            "name": profile.get("name", ""),
            "email": profile.get("email", ""),
            "picture": profile.get("picture", ""),
        }

    with open(TOKEN_FILE, "w") as f:
        json.dump(stored, f, indent=2)

    logger.info(f"Token saved to {TOKEN_FILE} (expires in {stored['expires_in']}s)")


def get_stored_token() -> Optional[Dict[str, Any]]:
    """
    Read the stored token from disk.
    Returns None if no token file exists.
    """
    if not TOKEN_FILE.exists():
        return None

    try:
        with open(TOKEN_FILE, "r") as f:
            return json.load(f)
    except (json.JSONDecodeError, IOError) as e:
        logger.warning(f"Failed to read token file: {e}")
        return None


def get_access_token() -> Optional[str]:
    """
    Get the current access token if it exists and is still valid.
    Returns the token string, or None if expired/missing.
    """
    token_data = get_stored_token()
    if not token_data:
        return None

    if is_token_expired(token_data):
        logger.warning("Stored LinkedIn token has expired")
        return None

    return token_data.get("access_token")


def is_token_expired(token_data: Optional[Dict[str, Any]] = None) -> bool:
    """
    Check if the stored token is expired.
    Considers a token expired 5 minutes before actual expiry for safety.
    """
    if token_data is None:
        token_data = get_stored_token()

    if not token_data:
        return True

    expires_at = token_data.get("expires_at", 0)
    # 5 minute safety margin
    return time.time() > (expires_at - 300)


def get_token_status() -> Dict[str, Any]:
    """
    Get a user-friendly status summary of the LinkedIn token.
    Used by /auth/status and /credentials/status endpoints.
    """
    token_data = get_stored_token()

    if not token_data:
        return {
            "connected": False,
            "status": "not_connected",
            "message": "LinkedIn not connected. Click 'Connect LinkedIn' to authenticate.",
        }

    expired = is_token_expired(token_data)
    expires_at = token_data.get("expires_at", 0)
    remaining_seconds = max(0, int(expires_at - time.time()))
    remaining_days = remaining_seconds // 86400

    profile = token_data.get("profile", {})

    if expired:
        return {
            "connected": False,
            "status": "expired",
            "message": "LinkedIn token has expired. Please re-authenticate.",
            "profile_name": profile.get("name", ""),
        }

    return {
        "connected": True,
        "status": "connected",
        "message": f"Connected as {profile.get('name', 'LinkedIn User')}",
        "profile_name": profile.get("name", ""),
        "profile_email": profile.get("email", ""),
        "profile_picture": profile.get("picture", ""),
        "expires_in_days": remaining_days,
        "expires_at": expires_at,
    }


def clear_token() -> None:
    """Delete the stored token file (for logout/disconnect)."""
    if TOKEN_FILE.exists():
        TOKEN_FILE.unlink()
        logger.info("LinkedIn token cleared")
