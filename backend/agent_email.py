"""
Email Agent - Sends emails via SMTP
"""

import os
import smtplib
import logging
from email.mime.text import MIMEText
from email.mime.multipart import MIMEMultipart
from typing import Dict, Any

logger = logging.getLogger(__name__)

async def send_email(
    recipient_email: str,
    subject: str,
    body: str
) -> Dict[str, Any]:
    """Send email via SMTP"""
    
    try:
        smtp_email = os.getenv('SMTP_EMAIL')
        smtp_password = os.getenv('SMTP_PASSWORD')
        
        if not smtp_email or not smtp_password:
            return {
                "status": "error",
                "message": "SMTP credentials not set"
            }
        
        logger.info(f"Sending email to: {recipient_email}")
        
        # Create message
        msg = MIMEMultipart()
        msg['From'] = smtp_email
        msg['To'] = recipient_email
        msg['Subject'] = subject
        
        msg.attach(MIMEText(body, 'plain'))
        
        # Send via Gmail SMTP
        with smtplib.SMTP_SSL('smtp.gmail.com', 465) as server:
            server.login(smtp_email, smtp_password)
            server.send_message(msg)
        
        logger.info(f"Email sent successfully to: {recipient_email}")
        
        return {
            "status": "success",
            "message": "Email sent successfully",
            "recipient": recipient_email,
            "subject": subject
        }
        
    except smtplib.SMTPAuthenticationError:
        logger.error("SMTP authentication failed - check email and app password")
        return {
            "status": "error",
            "message": "SMTP authentication failed. Use Gmail App Password."
        }
    except Exception as e:
        logger.error(f"Email sending error: {e}")
        return {
            "status": "error",
            "message": str(e)
        }

async def run(
    recipient_email: str,
    subject: str,
    body: str
) -> Dict[str, Any]:
    """Run the email agent"""
    return await send_email(recipient_email, subject, body)
