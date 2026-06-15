from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
import os
from dotenv import load_dotenv

load_dotenv()

app = FastAPI(title="Aegis AI Agent API", version="1.0.0")

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# ==================== HEALTH CHECK ====================

@app.get("/")
async def root():
    return {
        "name": "Aegis API",
        "status": "running",
        "message": "Backend is live on Render!"
    }

@app.get("/health")
async def health():
    return {"status": "operational"}

@app.get("/credentials/status")
async def get_credentials_status():
    return {
        "linkedin_connected": bool(os.getenv("LINKEDIN_SESSION_COOKIE")),
        "groq_connected": bool(os.getenv("GROQ_API_KEY")),
        "gmail_connected": bool(os.getenv("SMTP_EMAIL")),
        "all_configured": bool(os.getenv("LINKEDIN_SESSION_COOKIE") and os.getenv("GROQ_API_KEY") and os.getenv("SMTP_EMAIL"))
    }

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)
