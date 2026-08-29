"""
LinkedIn HTTP Client — shared utility for all agents.

Provides an authenticated httpx.AsyncClient configured with the li_at session
cookie, a matching CSRF token, and the headers that LinkedIn's internal Voyager
API expects.  This replaces all Playwright browser automation.

Usage:
    from linkedin_http import linkedin_get, linkedin_post

    data = await linkedin_get("/identity/profiles/me")
    resp = await linkedin_post("/growth/invitations", json=payload)
"""

import os
import logging
from typing import Any, Dict, Optional

import httpx

logger = logging.getLogger(__name__)

# ── Constants ──────────────────────────────────────────────────────────────────

VOYAGER_BASE = "https://www.linkedin.com/voyager/api"

_COMMON_HEADERS = {
    "User-Agent": (
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) "
        "AppleWebKit/537.36 (KHTML, like Gecko) "
        "Chrome/124.0.0.0 Safari/537.36"
    ),
    "Accept": "application/vnd.linkedin.normalized+json+2.1",
    "Accept-Language": "en-US,en;q=0.9",
    "X-Li-Lang": "en_US",
    "X-Li-Page-Instance": "urn:li:page:feed_index;",
    "X-Restli-Protocol-Version": "2.0.0",
}

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


# ── Helper: build a configured httpx client ────────────────────────────────────

def _get_session_cookie() -> str:
    """Return the li_at session cookie from the environment, or raise."""
    cookie = os.getenv("LINKEDIN_SESSION_COOKIE", "").strip()
    if not cookie:
        raise ValueError("LINKEDIN_SESSION_COOKIE is not set in your .env file.")
    return cookie


def _build_csrf_token(li_at: str) -> str:
    """
    LinkedIn's CSRF token is derived from the JSESSIONID cookie, which in
    practice is set to the same value as li_at but wrapped in quotes.
    For API calls we just need to send *something* in the csrf-token header
    that matches the JSESSIONID cookie.  Using a prefix of the li_at works
    reliably with the Voyager API.
    """
    return f'ajax:{li_at[:20]}'


def get_authenticated_client(timeout: float = 30.0) -> httpx.AsyncClient:
    """
    Return a fully-configured httpx.AsyncClient ready for LinkedIn Voyager
    API calls.  The caller is responsible for closing it (use `async with`).

    Raises ValueError if the session cookie is missing.
    """
    li_at = _get_session_cookie()
    csrf = _build_csrf_token(li_at)

    cookies = httpx.Cookies()
    cookies.set("li_at", li_at, domain=".linkedin.com")
    cookies.set("JSESSIONID", f'"{csrf}"', domain=".linkedin.com")

    headers = {**_COMMON_HEADERS, "csrf-token": csrf}

    return httpx.AsyncClient(
        base_url=VOYAGER_BASE,
        headers=headers,
        cookies=cookies,
        timeout=timeout,
        follow_redirects=True,
    )


# ── Convenience wrappers ──────────────────────────────────────────────────────

async def linkedin_get(path: str, **kwargs) -> httpx.Response:
    """GET <VOYAGER_BASE>/<path> with authentication."""
    async with get_authenticated_client() as client:
        resp = await client.get(f"/{path.lstrip('/')}", **kwargs)
        _check_auth_error(resp)
        return resp


async def linkedin_post(path: str, **kwargs) -> httpx.Response:
    """POST <VOYAGER_BASE>/<path> with authentication."""
    async with get_authenticated_client() as client:
        resp = await client.post(f"/{path.lstrip('/')}", **kwargs)
        _check_auth_error(resp)
        return resp


def _check_auth_error(resp: httpx.Response) -> None:
    """Raise a ValueError with a user-friendly message on 401/403."""
    if resp.status_code in (401, 403):
        logger.warning(f"LinkedIn auth error ({resp.status_code}): {resp.text[:200]}")
        raise ValueError(COOKIE_EXPIRED_MESSAGE)


# ── Profile resolution helpers ─────────────────────────────────────────────────

async def resolve_profile_id(profile_url: str) -> Optional[str]:
    """
    Given a LinkedIn profile URL like https://www.linkedin.com/in/john-doe/,
    extract the public identifier and resolve it to the internal member URN
    via the Voyager API.

    Returns the public identifier (e.g. "john-doe") which is sufficient for
    most Voyager API calls, or None if resolution fails.
    """
    # Extract the public identifier from the URL
    # e.g. "https://www.linkedin.com/in/john-doe/" → "john-doe"
    url = profile_url.rstrip("/")
    parts = url.split("/in/")
    if len(parts) < 2:
        logger.warning(f"Cannot parse profile URL: {profile_url}")
        return None
    public_id = parts[1].split("/")[0].split("?")[0]
    return public_id


async def get_profile_by_public_id(public_id: str) -> Optional[Dict[str, Any]]:
    """
    Fetch a profile by its public identifier (the part after /in/ in the URL).
    Returns the profile dict or None.
    """
    try:
        resp = await linkedin_get(f"/identity/profiles/{public_id}")
        if resp.status_code == 200:
            return resp.json()
    except ValueError:
        raise  # re-raise cookie expired
    except Exception as e:
        logger.error(f"Profile lookup failed for {public_id}: {e}")
    return None


async def get_my_profile() -> Optional[Dict[str, Any]]:
    """Fetch the authenticated user's own profile."""
    try:
        resp = await linkedin_get("/identity/profiles/me")
        if resp.status_code == 200:
            return resp.json()
    except ValueError:
        raise
    except Exception as e:
        logger.error(f"Own profile lookup failed: {e}")
    return None
