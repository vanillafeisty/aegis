"""
LinkedIn Recruiter Cold Outreach Agent
Searches for recruiters based on area and job description,
generates a custom cold message using Groq, and sends a connection request with a note.
Uses the LinkedIn Voyager API with the li_at session cookie.
No Playwright or browser automation required.
"""

import os
import logging
import urllib.parse
from typing import Dict, Any, List

import httpx

logger = logging.getLogger(__name__)

from linkedin_http import (
    get_authenticated_client,
    COOKIE_EXPIRED_MESSAGE,
)

# Groq import with fallback
try:
    from groq import Groq
    GROQ_AVAILABLE = True
except ImportError:
    GROQ_AVAILABLE = False
    logger.warning("Groq not available - mock messages will be used")


async def generate_cold_message(recruiter_name: str, job_description: str, area: str) -> str:
    """Generate a personalized connection note under 300 characters using Groq."""
    if not GROQ_AVAILABLE or not os.getenv('GROQ_API_KEY'):
        # Fallback template
        note = f"Hi {recruiter_name}, saw you recruit for {job_description} roles in {area}. Would love to connect and chat about potential opportunities. Best!"
        return note[:299]

    try:
        api_key = os.getenv('GROQ_API_KEY')
        model = os.getenv('GROQ_MODEL', 'llama-3.3-70b-versatile')
        client = Groq(api_key=api_key)

        prompt = f"""Write a friendly, highly professional LinkedIn connection request note to a recruiter.
Recruiter name: {recruiter_name}
Target role/Job description: {job_description}
Location/Area: {area}

Constraints:
- STRICTLY under 290 characters (including spaces). This is a hard limit.
- Be polite, state your interest in {job_description} roles, and ask to connect.
- Do NOT include placeholders (like [My Name] or [Your Company]). Ends the message with a generic friendly sign-off like 'Best' or 'Regards' without placeholders.
- Output ONLY the final note message, nothing else."""

        response = client.chat.completions.create(
            model=model,
            messages=[{"role": "user", "content": prompt}],
            max_tokens=100,
            temperature=0.7
        )

        note = response.choices[0].message.content.strip()
        # Clean up any quotes
        if note.startswith('"') and note.endswith('"'):
            note = note[1:-1]

        # Safe truncation fallback
        if len(note) >= 300:
            note = note[:295] + "..."

        return note
    except Exception as e:
        logger.error(f"Error generating cold message via Groq: {e}")
        return f"Hi {recruiter_name}, I'm looking for {job_description} roles in {area} and would love to connect. Best!"


async def _search_people(client: httpx.AsyncClient, query: str, limit: int = 10) -> List[Dict[str, Any]]:
    """
    Search for people on LinkedIn using the Voyager search API.
    Returns a list of dicts with name, publicIdentifier, entityUrn.
    """
    try:
        encoded_query = urllib.parse.quote(query)
        resp = await client.get(
            "/search/dash/clusters",
            params={
                "decorationId": "com.linkedin.voyager.dash.deco.search.SearchClusterCollection-185",
                "origin": "GLOBAL_SEARCH_HEADER",
                "q": "all",
                "query": f"(keywords:{encoded_query},resultType:List(PEOPLE))",
                "start": "0",
                "count": str(limit),
            },
        )

        if resp.status_code != 200:
            logger.warning(f"People search failed ({resp.status_code})")
            return []

        data = resp.json()
        profiles = []

        # Extract profiles from the included section
        included = data.get("included", [])
        for item in included:
            item_type = item.get("$type", "")
            if "MiniProfile" in item_type or "Profile" in item_type:
                first_name = item.get("firstName", "")
                last_name = item.get("lastName", "")
                entity_urn = item.get("entityUrn", "")
                public_id = item.get("publicIdentifier", "")

                if entity_urn and first_name:
                    profiles.append({
                        "name": f"{first_name} {last_name}".strip(),
                        "firstName": first_name,
                        "publicIdentifier": public_id,
                        "entityUrn": entity_urn,
                        "profileUrl": f"https://www.linkedin.com/in/{public_id}/" if public_id else "",
                    })

        return profiles[:limit]

    except Exception as e:
        logger.error(f"People search error: {e}")
        return []


async def _send_invitation(client: httpx.AsyncClient, profile_member_id: str, note: str = None) -> Dict[str, Any]:
    """Send a connection invitation to a profile by member ID."""
    payload = {
        "trackingId": os.urandom(8).hex(),
        "inviteeProfileId": profile_member_id,
    }

    if note:
        payload["message"] = note[:300]

    try:
        resp = await client.post(
            "/growth/invitations",
            json=payload,
            headers={"Content-Type": "application/json"},
        )

        if resp.status_code in (200, 201):
            return {"status": "success", "message": "Connection request sent with note"}

        if resp.status_code == 422:
            return {"status": "skipped", "message": "Already connected, invitation pending, or profile not accepting connections"}

        if resp.status_code in (401, 403):
            return {"status": "cookie_expired", "message": COOKIE_EXPIRED_MESSAGE}

        return {"status": "error", "message": f"Invitation failed ({resp.status_code})"}

    except Exception as e:
        return {"status": "error", "message": str(e)}


async def send_cold_messages(job_description: str, area: str, custom_message: str = None, limit: int = 3) -> Dict[str, Any]:
    """Search for recruiters and send personalized connection requests with notes."""

    session_cookie = os.getenv('LINKEDIN_SESSION_COOKIE')
    if not session_cookie:
        return {"status": "error", "message": "LinkedIn session cookie not set"}

    results = []
    query = f"Recruiter {job_description} {area}"
    logger.info(f"Searching for recruiters using query: '{query}'")

    try:
        async with get_authenticated_client() as client:
            # Search for people
            profiles = await _search_people(client, query, limit=limit * 2)

            if not profiles:
                return {
                    "status": "success",
                    "message": "No recruiter profiles found in search results",
                    "results": [],
                }

            targets = profiles[:limit]
            logger.info(f"Found {len(profiles)} profiles. Outreaching to top {len(targets)} targets.")

            for profile in targets:
                recruiter_name = profile.get("firstName", "there")
                member_id = profile["entityUrn"].split(":")[-1]
                profile_url = profile.get("profileUrl", "")

                try:
                    # Generate the personalized note
                    if custom_message:
                        note = custom_message.replace("{name}", recruiter_name).replace("{role}", job_description).replace("{area}", area)
                        if len(note) > 299:
                            note = note[:295] + "..."
                    else:
                        note = await generate_cold_message(recruiter_name, job_description, area)

                    logger.info(f"Generated note for {recruiter_name}: '{note}'")

                    # Send the invitation
                    invite_result = await _send_invitation(client, member_id, note)

                    results.append({
                        "name": recruiter_name,
                        "profile_url": profile_url,
                        "status": invite_result["status"],
                        "message": invite_result["message"],
                    })

                    if invite_result["status"] == "cookie_expired":
                        # Stop the campaign if cookie expired
                        break

                    logger.info(f"Result for {recruiter_name}: {invite_result['status']}")

                except Exception as ex:
                    logger.error(f"Error outreaching to {profile_url}: {ex}")
                    results.append({
                        "name": recruiter_name,
                        "profile_url": profile_url,
                        "status": "error",
                        "message": str(ex),
                    })

        return {
            "status": "success",
            "message": f"Completed outreach campaign for {len(results)} recruiters",
            "results": results,
        }
    except ValueError as ve:
        return {"status": "cookie_expired", "message": str(ve)}
    except Exception as e:
        logger.error(f"Cold message agent exception: {e}")
        return {"status": "error", "message": str(e)}


async def run(job_description: str, area: str, custom_message: str = None, limit: int = 3) -> Dict[str, Any]:
    """Run the cold message recruiter agent."""
    return await send_cold_messages(job_description, area, custom_message, limit)
