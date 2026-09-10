"""
LinkedIn Profile Optimization Agent - Uses Groq LLM to generate better profile content
"""

import os
import logging
from typing import Dict, Any

try:
    from groq import Groq
    GROQ_AVAILABLE = True
except ImportError:
    GROQ_AVAILABLE = False
    logging.warning("Groq not available")

logger = logging.getLogger(__name__)

async def optimize_profile() -> Dict[str, Any]:
    """Optimize LinkedIn profile using AI"""
    
    if not GROQ_AVAILABLE:
        logger.warning("Groq not available, returning mock optimization")
        return {
            "status": "error",
            "message": "Groq LLM not available",
            "suggestion": "Install groq package"
        }
    
    try:
        api_key = os.getenv('GROQ_API_KEY')
        model = os.getenv('GROQ_MODEL', 'openai/gpt-oss-120b')
        
        if not api_key:
            return {"status": "error", "message": "Groq API key not set"}
        
        logger.info("Optimizing LinkedIn profile with Groq LLM")
        
        client = Groq(api_key=api_key)
        
        # Generate optimized headline
        response_headline = client.chat.completions.create(
            model=model,
            messages=[{
                "role": "user",
                "content": """Generate a professional LinkedIn headline for an AI/ML engineer focused on:
                - Full-stack development
                - LinkedIn automation
                - Cloud deployment
                
                Keep it under 220 characters. Make it catchy and SEO-friendly."""
            }],
            max_tokens=250,
            temperature=0.7,
            reasoning_effort="low"
        )

        headline = response_headline.choices[0].message.content
        
        # Generate optimized about section
        response_about = client.chat.completions.create(
            model=model,
            messages=[{
                "role": "user",
                "content": """Write a compelling LinkedIn about section (200-300 chars) for someone who:
                - Builds AI agents for automation
                - Works with LinkedIn, email, and cloud APIs
                - Focuses on practical implementations
                
                Be personal, highlight achievements, and include a call-to-action."""
            }],
            max_tokens=450,
            temperature=0.7,
            reasoning_effort="low"
        )

        about = response_about.choices[0].message.content
        
        logger.info("Profile optimization completed")
        
        return {
            "status": "success",
            "message": "Profile optimization suggestions generated",
            "suggestions": {
                "headline": headline,
                "about": about,
                "recommendations": [
                    "Add relevant skills: Python, FastAPI, Groq, Playwright",
                    "Include recent projects in experience section",
                    "Add recommendations from colleagues",
                    "Enable profile visibility to recruiter searches"
                ]
            }
        }
        
    except Exception as e:
        logger.error(f"Profile optimization error: {e}")
        return {
            "status": "error",
            "message": str(e)
        }

async def run() -> Dict[str, Any]:
    """Run the profile optimization agent"""
    return await optimize_profile()
