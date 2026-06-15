import os
import smtplib
import ssl
from email.mime.text import MIMEText
from email.mime.multipart import MIMEMultipart
from dotenv import load_dotenv

# Load environmental variables
load_dotenv()
SENDER_EMAIL = os.getenv("SMTP_EMAIL")
SENDER_PASSWORD = os.getenv("SMTP_PASSWORD")

def verify_and_send_email(receiver_email, subject, body_content):
    """Establishes a secure connection to Google SMTP servers and transmits an email payload."""
    if not SENDER_EMAIL or not SENDER_PASSWORD:
        print("❌ Configuration Error: SMTP_EMAIL or SMTP_PASSWORD missing inside your .env file.")
        return False

    # 1. Setup secure connection port parameters
    smtp_server = "smtp.gmail.com"
    port = 465 # SSL industrial standard port
    
    # 2. Package the email envelope fields
    message = MIMEMultipart()
    message["From"] = SENDER_EMAIL
    message["To"] = receiver_email
    message["Subject"] = subject
    
    # Attach the body string payload
    message.attach(MIMEText(body_content, "plain"))
    
    # 3. Establish SSL security layer context
    ssl_context = ssl.create_default_context()
    
    print(f"🔒 Initiating secure handshake with {smtp_server}...")
    try:
        # Connect directly to Gmail's server via SSL
        with smtplib.SMTP_SSL(smtp_server, port, context=ssl_context) as server:
            print("🔑 Authenticating your App Password credentials...")
            server.login(SENDER_EMAIL, SENDER_PASSWORD)
            
            print(f"📤 Transmitting secure email packet to {receiver_email}...")
            server.sendmail(SENDER_EMAIL, receiver_email, message.as_string())
            
        print("✅ SMTP Verification Successful! Email sent smoothly.")
        return True
        
    except smtplib.SMTPAuthenticationError:
        print("❌ SMTP Authentication Failed! Your App Password is incorrect or 2-Step Verification is disabled.")
        return False
    except Exception as e:
        print(f"⚠️ SMTP Protocol Error encountered: {e}")
        return False

if __name__ == "__main__":
    # Test your email agent pipeline by sending a verification note straight to yourself
    test_subject = "Aegis AI Agent: SMTP Verification Alert"
    test_body = (
        "Hello!\n\n"
        "This is an automated system confirmation verification. "
        "Your custom AI agent is now successfully linked to your Gmail account and can push live notifications."
    )
    
    # Sends a test email to your own inbox address to confirm it works
    verify_and_send_email(receiver_email=SENDER_EMAIL, subject=test_subject, body_content=test_body)
