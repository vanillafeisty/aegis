"""Gemini AI integration service."""
import json
import logging
from typing import Optional

import google.generativeai as genai

from app.core.config import settings

logger = logging.getLogger("aegis")


class GeminiService:
    def __init__(self):
        genai.configure(api_key=settings.GEMINI_API_KEY)
        self.flash_model = settings.GEMINI_MODEL
        self.pro_model = settings.GEMINI_PRO_MODEL

    async def classify_intent(self, command: str) -> dict:
        """Classify user command into an intent category."""
        intent_categories = [
            "job_search",
            "send_connection",
            "create_post",
            "apply_job",
            "analyze_profile",
            "analyze_resume",
            "schedule_followup",
            "generate_content",
            "track_applications",
            "get_analytics",
            "modify_profile",
            "search_recruiters",
        ]

        prompt = f"""Classify this user command into ONE of these intent categories:
{', '.join(intent_categories)}

Command: "{command}"

Respond with ONLY valid JSON (no markdown):
{{
  "intent": "intent_name",
  "confidence": 0.95,
  "extracted_params": {{
    "key": "value"
  }}
}}"""

        model = genai.GenerativeModel(self.flash_model)
        response = model.generate_content(prompt)
        
        try:
            result = json.loads(response.text)
            return result
        except json.JSONDecodeError:
            logger.error(f"Failed to parse intent response: {response.text}")
            return {"intent": "unknown", "confidence": 0.0, "extracted_params": {}}

    async def decompose_goal(self, command: str, intent: str, user_context: dict) -> dict:
        """Decompose high-level goal into atomic task DAG."""
        prompt = f"""You are an AI agent planner. Decompose this user goal into a task graph.

User Command: "{command}"
Intent: {intent}
User Context: {json.dumps(user_context, indent=2)}

Return valid JSON (no markdown) with this structure:
{{
  "tasks": [
    {{
      "id": "task_1",
      "name": "Search LinkedIn for jobs",
      "dependencies": [],
      "tool": "playwright_search",
      "params": {{"query": "AI Engineer", "location": "Bangalore"}},
      "estimated_duration_sec": 15
    }}
  ],
  "estimated_total_duration_sec": 60,
  "approval_required": true,
  "approval_level": 2
}}"""

        model = genai.GenerativeModel(self.flash_model)
        response = model.generate_content(prompt)

        try:
            result = json.loads(response.text)
            return result
        except json.JSONDecodeError:
            logger.error(f"Failed to parse decomposition: {response.text}")
            return {"tasks": [], "approval_required": True}

    async def generate_content(
        self, topic: Optional[str] = None, image_context: Optional[str] = None, tone: str = "professional"
    ) -> dict:
        """Generate LinkedIn post content."""
        if image_context:
            prompt = f"""Generate a professional LinkedIn post based on this image context:
{image_context}

Tone: {tone}

Max 1300 characters. Return valid JSON:
{{
  "post_text": "...",
  "hashtags": ["#hashtag1", "#hashtag2"],
  "call_to_action": "Optional CTA"
}}"""
        else:
            prompt = f"""Generate a LinkedIn post about: {topic}

Tone: {tone}
Max 1300 characters.

Return valid JSON:
{{
  "post_text": "...",
  "hashtags": ["#hashtag1", "#hashtag2"],
  "call_to_action": "Optional CTA"
}}"""

        model = genai.GenerativeModel(self.flash_model)
        response = model.generate_content(prompt)

        try:
            return json.loads(response.text)
        except json.JSONDecodeError:
            return {"post_text": response.text, "hashtags": []}

    async def generate_outreach_message(self, recruiter_name: str, company: str, message_type: str) -> str:
        """Generate personalized outreach message."""
        prompt = f"""Write a professional LinkedIn {message_type} message to {recruiter_name} at {company}.
Keep it under 300 characters. Personalize based on recruiter role.

Return ONLY the message text, no JSON."""

        model = genai.GenerativeModel(self.flash_model)
        response = model.generate_content(prompt)
        return response.text.strip()

    async def analyze_resume(self, resume_text: str, jd_text: Optional[str] = None) -> dict:
        """Analyze resume and return structured insights."""
        if jd_text:
            prompt = f"""Analyze this resume against the job description.

RESUME:
{resume_text}

JOB DESCRIPTION:
{jd_text}

Return valid JSON:
{{
  "match_score": 85,
  "matched_skills": ["Skill1", "Skill2"],
  "missing_skills": ["Skill3"],
  "suggestions": ["Add X", "Improve Y"],
  "ats_score": 78
}}"""
        else:
            prompt = f"""Analyze this resume for ATS optimization.

RESUME:
{resume_text}

Return valid JSON:
{{
  "ats_score": 75,
  "skills": ["Python", "AWS"],
  "formatting_issues": [],
  "suggestions": ["Add metrics", "Use action verbs"]
}}"""

        model = genai.GenerativeModel(self.flash_model)
        response = model.generate_content(prompt)

        try:
            return json.loads(response.text)
        except json.JSONDecodeError:
            logger.error(f"Failed to parse resume analysis: {response.text}")
            return {"ats_score": 0, "skills": [], "suggestions": []}

    async def optimize_headline(self, current_headline: str, target_role: str, keywords: list) -> list:
        """Generate alternative profile headline variants."""
        keywords_str = ", ".join(keywords)
        prompt = f"""Generate 3 alternative LinkedIn headlines.

Current: "{current_headline}"
Target Role: {target_role}
Keywords: {keywords_str}

Return valid JSON (no markdown):
{{
  "headlines": [
    "Option 1",
    "Option 2",
    "Option 3"
  ]
}}"""

        model = genai.GenerativeModel(self.flash_model)
        response = model.generate_content(prompt)

        try:
            result = json.loads(response.text)
            return result.get("headlines", [current_headline])
        except json.JSONDecodeError:
            return [current_headline]
