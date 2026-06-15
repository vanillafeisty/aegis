from fastapi import FastAPI, HTTPException, BackgroundTasks
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
from typing import Optional
import asyncio
import os
from dotenv import load_dotenv

# ❌ Comment out these (need Playwright/browser):
# import agent_post
# import agent_connect

# ✅ Keep these (no Playwright needed):
import agent_email
import agent_profile_tweak
import ai_agent_inbox

load_dotenv()

app = FastAPI(title="Aegis AI Agent API", version="1.0.0")

# Enable CORS for frontend
app.add_middleware(
    CORSMiddleware,
    allow_origins=["http://localhost:3000", "http://localhost:3001", "*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# ==================== REQUEST MODELS ====================

class PostRequest(BaseModel):
    content: str

class ConnectionRequest(BaseModel):
    profile_url: str

class EmailRequest(BaseModel):
    recipient_email: str
    subject: str
    body: str

class CredentialsRequest(BaseModel):
    linkedin_session_cookie: str
    linkedin_access_token: str
    linkedin_client_id: str
    openai_api_key: str
    smtp_email: str
    smtp_password: str

class HealthResponse(BaseModel):
    status: str
    available_agents: list

# ==================== HEALTH CHECK ====================

@app.get("/health", response_model=HealthResponse)
async def health_check():
    """Check if API is running and list available agents"""
    return {
        "status": "operational",
        "available_agents": [
            "send_email",
            "optimize_profile",
            "reply_to_inbox",
            "get_credentials_status"
        ]
    }

# ==================== CREDENTIALS ====================

@app.post("/credentials/set")
async def set_credentials(request: CredentialsRequest):
    """Update credentials in .env file"""
    try:
        env_content = f"""LINKEDIN_SESSION_COOKIE={request.linkedin_session_cookie}
LINKEDIN_ACCESS_TOKEN={request.linkedin_access_token}
LINKEDIN_CLIENT_ID={request.linkedin_client_id}
OPENAI_API_KEY={request.openai_api_key}
SMTP_EMAIL={request.smtp_email}
SMTP_PASSWORD={request.smtp_password}
"""
        with open(".env", "w") as f:
            f.write(env_content)
        
        # Reload environment variables
        load_dotenv()
        return {"status": "success", "message": "Credentials updated successfully"}
    except Exception as e:
        raise HTTPException(status_code=400, detail=str(e))

@app.get("/credentials/status")
async def get_credentials_status():
    """Check if required credentials are set"""
    linkedin_cookie = os.getenv("LINKEDIN_SESSION_COOKIE")
    openai_key = os.getenv("OPENAI_API_KEY")
    smtp_email = os.getenv("SMTP_EMAIL")
    
    return {
        "linkedin_connected": bool(linkedin_cookie),
        "openai_connected": bool(openai_key),
        "gmail_connected": bool(smtp_email),
        "all_configured": bool(linkedin_cookie and openai_key and smtp_email)
    }

# ==================== LINKEDIN AGENTS (COMMENTED - NEEDS BROWSER) ====================

# ❌ These need Playwright - only run locally!
# @app.post("/agents/post")
# async def post_to_linkedin(request: PostRequest, background_tasks: BackgroundTasks):
#     """Post content to LinkedIn feed"""
#     try:
#         linkedin_cookie = os.getenv("LINKEDIN_SESSION_COOKIE")
#         if not linkedin_cookie:
#             raise HTTPException(status_code=400, detail="LinkedIn credentials not configured")
#         background_tasks.add_task(agent_post.run_feed_post_via_browser, request.content)
#         return {"status": "queued", "message": "Post is being published to your LinkedIn feed", "content": request.content}
#     except Exception as e:
#         raise HTTPException(status_code=400, detail=str(e))

# @app.post("/agents/connect")
# async def send_connection_request(request: ConnectionRequest, background_tasks: BackgroundTasks):
#     """Send a connection request to a LinkedIn profile"""
#     try:
#         linkedin_cookie = os.getenv("LINKEDIN_SESSION_COOKIE")
#         if not linkedin_cookie:
#             raise HTTPException(status_code=400, detail="LinkedIn credentials not configured")
#         if not request.profile_url.startswith("https://"):
#             raise HTTPException(status_code=400, detail="Invalid profile URL")
#         background_tasks.add_task(agent_connect.send_connection_request, request.profile_url)
#         return {"status": "queued", "message": "Connection request is being sent", "profile_url": request.profile_url}
#     except Exception as e:
#         raise HTTPException(status_code=400, detail=str(e))

# ==================== AI AGENTS (NO BROWSER NEEDED) ====================

@app.post("/agents/inbox")
async def reply_to_inbox(background_tasks: BackgroundTasks):
    """Check inbox and auto-reply to messages"""
    try:
        linkedin_cookie = os.getenv("LINKEDIN_SESSION_COOKIE")
        openai_key = os.getenv("OPENAI_API_KEY")
        
        if not linkedin_cookie or not openai_key:
            raise HTTPException(status_code=400, detail="LinkedIn or OpenAI credentials not configured")
        
        background_tasks.add_task(ai_agent_inbox.run_ai_agent_inbox)
        return {"status": "queued", "message": "Inbox processing started - AI will reply to unread messages"}
    except Exception as e:
        raise HTTPException(status_code=400, detail=str(e))

@app.post("/agents/profile")
async def optimize_profile(background_tasks: BackgroundTasks):
    """Optimize LinkedIn profile with AI"""
    try:
        linkedin_cookie = os.getenv("LINKEDIN_SESSION_COOKIE")
        openai_key = os.getenv("OPENAI_API_KEY")
        
        if not linkedin_cookie or not openai_key:
            raise HTTPException(status_code=400, detail="LinkedIn or OpenAI credentials not configured")
        
        background_tasks.add_task(agent_profile_tweak.optimize_my_profile)
        return {"status": "queued", "message": "Profile optimization started - AI will enhance your headline and about section"}
    except Exception as e:
        raise HTTPException(status_code=400, detail=str(e))

# ==================== EMAIL AGENT ====================

@app.post("/agents/email")
async def send_email(request: EmailRequest):
    """Send an email via Gmail"""
    try:
        smtp_email = os.getenv("SMTP_EMAIL")
        smtp_password = os.getenv("SMTP_PASSWORD")
        
        if not smtp_email or not smtp_password:
            raise HTTPException(status_code=400, detail="Gmail credentials not configured")
        
        success = agent_email.verify_and_send_email(
            request.recipient_email,
            request.subject,
            request.body
        )
        
        if success:
            return {"status": "success", "message": "Email sent successfully", "recipient": request.recipient_email}
        else:
            raise HTTPException(status_code=400, detail="Failed to send email")
    except Exception as e:
        raise HTTPException(status_code=400, detail=str(e))

# ==================== ROOT ====================

@app.get("/")
async def root():
    """API documentation"""
    return {
        "name": "Aegis AI Agent Platform API",
        "version": "1.0.0",
        "description": "Autonomous LinkedIn & Gmail automation with AI",
        "endpoints": {
            "health": "/health",
            "credentials": {
                "set": "POST /credentials/set",
                "status": "GET /credentials/status"
            },
            "agents": {
                "inbox": "POST /agents/inbox",
                "profile": "POST /agents/profile",
                "email": "POST /agents/email"
            }
        }
    }

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)
