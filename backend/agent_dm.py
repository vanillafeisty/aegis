"""
LinkedIn Direct Message Agent
Finds a connection by name and sends them a direct message.
Uses the LinkedIn Voyager messaging API with the li_at session cookie.
No Playwright or browser automation required.
"""

import os
import logging
from typing import Dict, Any

import httpx

logger = logging.getLogger(__name__)

from linkedin_http import (
    get_authenticated_client,
    COOKIE_EXPIRED_MESSAGE,
)


async def _search_connection(client: httpx.AsyncClient, name: str) -> Dict[str, Any] | None:
    """
    Search for a connection by name using LinkedIn's typeahead API.
    Returns the first matching result with entityUrn and name, or None.
    """
    try:
        resp = await client.get(
            "/search/dash/typeahead",
            params={
                "q": "blended",
                "query": name,
                "type": "CONNECTIONS",
            },
        )

        if resp.status_code != 200:
            logger.warning(f"Typeahead search failed ({resp.status_code})")
            return None

        data = resp.json()

        # Navigate the response to find results
        elements = data.get("elements", [])
        if not elements:
            # Try alternate response structure
            included = data.get("included", [])
            for item in included:
                if item.get("$type", "").endswith("MiniProfile"):
                    first_name = item.get("firstName", "")
                    last_name = item.get("lastName", "")
                    entity_urn = item.get("entityUrn", "")
                    if entity_urn:
                        return {
                            "name": f"{first_name} {last_name}".strip(),
                            "entityUrn": entity_urn,
                            "publicIdentifier": item.get("publicIdentifier", ""),
                        }
            return None

        # Use the first element
        first = elements[0]
        entity_urn = first.get("entityUrn", "") or first.get("targetUrn", "")
        display_name = first.get("title", {}).get("text", name)

        if entity_urn:
            return {"name": display_name, "entityUrn": entity_urn}

        return None

    except Exception as e:
        logger.error(f"Connection search error: {e}")
        return None


async def _find_or_create_conversation(client: httpx.AsyncClient, member_urn: str) -> str | None:
    """
    Look up an existing messaging conversation with the given member,
    or return the member URN which can be used to create a new message thread.
    """
    try:
        # Try to find existing conversations
        resp = await client.get(
            "/messaging/conversations",
            params={"keyVersion": "LEGACY_INBOX"},
        )

        if resp.status_code == 200:
            data = resp.json()
            elements = data.get("elements", [])
            for conv in elements:
                participants = conv.get("participants", [])
                for p in participants:
                    profile_urn = p.get("com.linkedin.voyager.messaging.MessagingMember", {}).get("miniProfile", {}).get("entityUrn", "")
                    if member_urn in profile_urn or profile_urn in member_urn:
                        return conv.get("entityUrn", "")

        # No existing conversation found — return None, we'll create a new one
        return None

    except Exception as e:
        logger.error(f"Conversation lookup error: {e}")
        return None


async def send_dm(name: str, message: str) -> Dict[str, Any]:
    """
    Search LinkedIn messaging for a connection by name and send a message.

    Args:
        name:    Display name (or partial name) of the connection, e.g. "G Sanjib".
        message: The text to send.

    Returns:
        A dict with keys: status, message, detail
    """
    try:
        async with get_authenticated_client() as client:
            # 1. Search for the connection
            contact = await _search_connection(client, name)
            if not contact:
                return {
                    "status": "not_found",
                    "message": f"Could not find a connection matching '{name}' in LinkedIn.",
                    "detail": "Ensure the person is in your connections and the name matches.",
                }

            contact_name = contact["name"]
            contact_urn = contact["entityUrn"]

            # Extract the member ID from the URN
            # e.g. "urn:li:fsd_profile:ACoAA..." → "ACoAA..."
            member_id = contact_urn.split(":")[-1]
            member_urn = f"urn:li:fsd_profile:{member_id}"

            # 2. Send the message
            # LinkedIn's messaging API for sending a new message
            payload = {
                "message": {
                    "body": message,
                },
                "mailboxUrn": "urn:li:fsd_profile:me",
                "trackingId": os.urandom(8).hex(),
                "dedupeByClientGeneratedToken": False,
                "hostRecipientUrns": [member_urn],
            }

            resp = await client.post(
                "/messaging/conversations",
                json=payload,
                headers={"Content-Type": "application/json"},
            )

            if resp.status_code in (200, 201):
                logger.info(f"Message sent to {contact_name}")
                return {
                    "status": "success",
                    "message": f"Message sent to {contact_name}",
                    "detail": message,
                }

            if resp.status_code in (401, 403):
                return {"status": "cookie_expired", "message": COOKIE_EXPIRED_MESSAGE}

            logger.error(f"Send message failed ({resp.status_code}): {resp.text[:300]}")
            return {
                "status": "error",
                "message": f"Failed to send message ({resp.status_code})",
                "detail": resp.text[:200],
            }

    except ValueError as ve:
        return {"status": "error", "message": str(ve)}
    except Exception as ex:
        logger.error(f"DM agent error: {ex}")
        return {"status": "error", "message": str(ex)}


async def run(name: str, message: str) -> Dict[str, Any]:
    """Entry-point used by the MCP server and app routes."""
    return await send_dm(name, message)
