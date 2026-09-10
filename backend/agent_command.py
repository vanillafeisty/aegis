"""
Natural-Language Command Agent — lets the user type a plain-English instruction
("post about trending AI tools", "connect with recruiters hiring for backend
roles in Seattle", "email jane@acme.com about the opening") and have it
classified into one of Aegis's existing actions with the parameters extracted.

This agent never invents contact details. If a command requires information
the user didn't supply (a recipient email, a profile URL, a target role/area),
it reports that back as `missing` fields + a clarifying question instead of
guessing — Aegis has no way to discover a stranger's personal email address,
and guessing profile URLs would send connection requests to the wrong people.
"""

import os
import json
import logging
from typing import Any, Dict, List, Optional

logger = logging.getLogger(__name__)

try:
    from groq import Groq
    GROQ_AVAILABLE = True
except ImportError:
    GROQ_AVAILABLE = False
    logger.warning("Groq not available")

# Intents Aegis knows how to execute, and the parameters each one needs.
INTENTS: Dict[str, List[str]] = {
    "post": ["content"],
    "post_trending": ["topic"],
    "connect": ["profile_url"],
    "cold_outreach": ["job_description", "area"],
    "dm": ["name", "message"],
    "email": ["recipient_email", "subject", "body"],
    "profile_optimize": [],
    "inbox": [],
}

SYSTEM_PROMPT = """You are the command router for Aegis, a LinkedIn & email automation agent.
Read the user's free-text instruction and classify it into exactly one of these intents:

- post: publish a specific, already-written piece of text to LinkedIn.
- post_trending: write and publish a LinkedIn post about a topic/trend the user named (they did not give exact text).
- connect: send a LinkedIn connection request to ONE specific person, when the user gave a linkedin.com/in/... profile URL.
- cold_outreach: search LinkedIn for people matching a role/title and location (e.g. recruiters, engineers) and send each a personalized connection request. Use this whenever the user wants to reach multiple people by role/industry/location rather than one named URL.
- dm: send a direct message to an EXISTING LinkedIn connection, identified by name.
- email: send an email via SMTP to a specific, already-known email address.
- profile_optimize: generate AI suggestions to improve the user's own LinkedIn headline/about section.
- inbox: scan the user's LinkedIn inbox and auto-reply to recent messages.
- unknown: the instruction doesn't match any of the above, or is unrelated small talk.

Required parameters per intent:
- post: content (the exact text to publish)
- post_trending: topic (string describing what to write about), image_url (optional, only if the user gave an actual image URL)
- connect: profile_url (a linkedin.com/in/... URL), message (optional personal note)
- cold_outreach: job_description (role/title/industry to search for), area (city/region/"remote"), limit (optional integer, default 3), custom_message (optional)
- dm: name (the connection's name), message (the exact text to send)
- email: recipient_email, subject, body — if the user only stated intent/context but not literal subject/body text, leave subject and body as empty strings so Aegis can draft them, but NEVER invent recipient_email
- profile_optimize: none
- inbox: none

CRITICAL RULES:
1. Never invent an email address, a LinkedIn profile URL, or a person's exact name. If the command doesn't contain one but the intent needs it, put that field's name in "missing" and leave it out of "params".
2. If the user asks to email "recruiters" or "hiring managers" in general (no address given), classify as intent "email" with recipient_email missing, UNLESS they describe wanting to reach people by role/location on LinkedIn itself (no email involved) — then classify as cold_outreach instead.
3. "missing" must list ONLY required fields (marked required above) that you could not fill from the command. image_url is always optional — never put it in "missing" even if the user mentioned wanting a picture; just omit it from params and proceed without it.
4. If "missing" is non-empty, also write a short, specific "clarifying_question" asking the user for exactly those fields. If "missing" is empty but you're proceeding without an optional field the user mentioned (like an image), say so briefly in "clarifying_question" anyway so they know — this does not block execution.
5. Respond with ONLY a JSON object, no markdown, no commentary, in this exact shape:
{"intent": "<one of the intents above>", "params": {...}, "missing": [...], "clarifying_question": "<string, empty if nothing to say>"}

Examples:
User: "post something about the trendy AI tools happening in the market right now, with a picture"
{"intent": "post_trending", "params": {"topic": "trendy AI tools and what's actively happening in AI right now"}, "missing": [], "clarifying_question": "Posting text-only since no image URL was given — mention one next time if you want an image attached."}

User: "send some connection requests to my people around"
{"intent": "cold_outreach", "params": {}, "missing": ["job_description", "area"], "clarifying_question": "Who should I connect with — what role/industry, and in which city or region?"}

User: "send email to hiring recruiters who are actively hiring"
{"intent": "email", "params": {}, "missing": ["recipient_email"], "clarifying_question": "I can't look up a recruiter's personal email address — LinkedIn doesn't expose it. Give me the email address to send to (or ask me to send a LinkedIn connection request/cold outreach to recruiters instead), and I'll draft and send it."}

User: "connect with https://www.linkedin.com/in/janedoe/"
{"intent": "connect", "params": {"profile_url": "https://www.linkedin.com/in/janedoe/"}, "missing": [], "clarifying_question": ""}

User: "email jane@acme.com about the backend engineer opening, ask for a referral"
{"intent": "email", "params": {"recipient_email": "jane@acme.com", "subject": "", "body": ""}, "missing": [], "clarifying_question": ""}
"""


def _rule_based_fallback(command: str) -> Dict[str, Any]:
    """Very small keyword fallback if Groq is unavailable — better than nothing."""
    lower = command.lower()
    if "profile" in lower and ("optim" in lower or "improve" in lower or "headline" in lower):
        return {"intent": "profile_optimize", "params": {}, "missing": [], "clarifying_question": ""}
    if "inbox" in lower or "reply" in lower and "message" in lower:
        return {"intent": "inbox", "params": {}, "missing": [], "clarifying_question": ""}
    return {
        "intent": "unknown",
        "params": {},
        "missing": [],
        "clarifying_question": "I couldn't understand that command (AI classification is unavailable right now — check GROQ_API_KEY).",
    }


async def interpret_command(command: str) -> Dict[str, Any]:
    """
    Classify a free-text command into an Aegis intent + parameters.

    Returns a dict: {"intent", "params", "missing", "clarifying_question"}.
    Never raises for a bad/ambiguous command — returns intent "unknown" instead.
    """
    command = (command or "").strip()
    if not command:
        return {"intent": "unknown", "params": {}, "missing": [], "clarifying_question": "Say what you'd like Aegis to do."}

    if not GROQ_AVAILABLE or not os.getenv("GROQ_API_KEY"):
        return _rule_based_fallback(command)

    try:
        client = Groq(api_key=os.getenv("GROQ_API_KEY"))
        model = os.getenv("GROQ_MODEL", "openai/gpt-oss-120b")

        response = client.chat.completions.create(
            model=model,
            messages=[
                {"role": "system", "content": SYSTEM_PROMPT},
                {"role": "user", "content": command},
            ],
            max_tokens=600,
            temperature=0.2,
            reasoning_effort="low",
            response_format={"type": "json_object"},
        )

        raw = response.choices[0].message.content.strip()
        parsed = json.loads(raw)

        intent = parsed.get("intent", "unknown")
        if intent not in INTENTS:
            intent = "unknown"

        params = parsed.get("params") or {}
        missing = parsed.get("missing") or []
        clarifying_question = parsed.get("clarifying_question", "") or ""

        # Defensive re-check: make sure every required field is actually present
        # and non-empty, regardless of what the model claimed.
        if intent in INTENTS:
            required = INTENTS[intent]
            recomputed_missing = [
                field for field in required
                if not str(params.get(field, "")).strip() and field not in ("subject", "body")
            ]
            # subject/body for email are allowed to be blank (Aegis drafts them)
            missing = list(dict.fromkeys(missing + recomputed_missing))

        if missing and not clarifying_question:
            clarifying_question = f"I need a bit more info: {', '.join(missing)}."

        return {
            "intent": intent,
            "params": params,
            "missing": missing,
            "clarifying_question": clarifying_question,
        }

    except Exception as e:
        logger.error(f"Command interpretation error: {e}")
        return {
            "intent": "unknown",
            "params": {},
            "missing": [],
            "clarifying_question": f"Sorry, I couldn't process that command ({str(e)}).",
        }


async def draft_email(context: str) -> Dict[str, str]:
    """Draft a subject + body for an email when the user described intent but not literal text."""
    if not GROQ_AVAILABLE or not os.getenv("GROQ_API_KEY"):
        return {"subject": "Quick note", "body": context}

    try:
        client = Groq(api_key=os.getenv("GROQ_API_KEY"))
        model = os.getenv("GROQ_MODEL", "openai/gpt-oss-120b")

        response = client.chat.completions.create(
            model=model,
            messages=[{
                "role": "user",
                "content": f"""Write a concise, professional email based on this instruction:
"{context}"

Respond with ONLY a JSON object: {{"subject": "...", "body": "..."}}
The body should be plain text (no markdown), signed off generically without inventing a name/company.""",
            }],
            max_tokens=600,
            temperature=0.6,
            reasoning_effort="low",
            response_format={"type": "json_object"},
        )
        parsed = json.loads(response.choices[0].message.content.strip())
        return {
            "subject": parsed.get("subject", "Quick note"),
            "body": parsed.get("body", context),
        }
    except Exception as e:
        logger.error(f"Email drafting error: {e}")
        return {"subject": "Quick note", "body": context}
