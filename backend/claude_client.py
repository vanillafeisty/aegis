"""
Shared Claude API client for all Aegis agents.
Reads ANTHROPIC_API_KEY (and optional CLAUDE_MODEL) from the environment.
"""

import os
import logging

logger = logging.getLogger(__name__)

try:
    import anthropic
    CLAUDE_AVAILABLE = True
except ImportError:
    CLAUDE_AVAILABLE = False
    logger.warning("anthropic package not available")

DEFAULT_MODEL = "claude-opus-5"

_client = None


def is_configured() -> bool:
    return CLAUDE_AVAILABLE and bool(os.getenv('ANTHROPIC_API_KEY'))


def _get_client():
    global _client
    if _client is None:
        _client = anthropic.AsyncAnthropic()
    return _client


async def generate_text(prompt: str, max_tokens: int = 4000) -> str:
    """Send a single-turn prompt to Claude and return the text reply."""
    if not is_configured():
        raise RuntimeError("Claude AI not configured - set ANTHROPIC_API_KEY")

    response = await _get_client().beta.messages.create(
        model=os.getenv('CLAUDE_MODEL', DEFAULT_MODEL),
        max_tokens=max_tokens,
        # Short copywriting tasks don't need deep reasoning
        output_config={"effort": "low"},
        # On a safety refusal, the API retries on a fallback model automatically
        betas=["server-side-fallback-2026-07-01"],
        fallbacks="default",
        messages=[{"role": "user", "content": prompt}],
    )

    if response.stop_reason == "refusal":
        raise RuntimeError("Claude declined to generate this content")

    text = "".join(block.text for block in response.content if block.type == "text").strip()
    if text.startswith('"') and text.endswith('"'):
        text = text[1:-1]
    return text
