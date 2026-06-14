import os
import asyncio
import random
from playwright.async_api import async_playwright
from openai import OpenAI
from dotenv import load_dotenv

# Initialize the environments and API hooks
load_dotenv()
COOKIE_VALUE = os.getenv("LINKEDIN_SESSION_COOKIE")
OPENAI_KEY = os.getenv("OPENAI_API_KEY")

# Create the OpenAI client pipeline
ai_client = OpenAI(api_key=OPENAI_KEY)

def generate_ai_reply(sender_name, inbound_message):
    """Passes the incoming message string to OpenAI to construct a professional reply."""
    try:
        system_prompt = (
            "You are a professional, helpful personal AI assistant agent managing my LinkedIn account. "
            "Draft a concise, warm, natural response (under 3 sentences) to incoming direct messages. "
            "Be authentic and polite. Do not use corporate jargon or sound robotic. "
            "Sign off with 'Best, [My Name Assistant]' or keep it open-ended to keep the conversation moving naturally."
        )
        
        user_prompt = f"Inbound message from {sender_name}: '{inbound_message}'\n\nGenerate the reply:"
        
        # Calling the lightweight, fast, and cost-effective gpt-4o-mini engine model
        response = ai_client.chat.completions.create(
            model="gpt-4o-mini",
            messages=[
                {"role": "system", "content": system_prompt},
                {"role": "user", "content": user_prompt}
            ],
            max_tokens=150,
            temperature=0.7
        )
        return response.choices[0].message.content.strip()
    except Exception as e:
        print(f"❌ LLM completion engine failed: {e}")
        return f"Hi {sender_name}, thanks for reaching out! I've received your note and will review it shortly."

async def run_ai_agent_inbox():
    if not COOKIE_VALUE or not OPENAI_KEY:
        print("❌ Configuration Missing: Check that both LINKEDIN_SESSION_COOKIE and OPENAI_API_KEY are set inside your .env file.")
        return

    async with async_playwright() as p:
        browser = await p.chromium.launch(headless=False) # Keep False to supervise what your AI types live!
        context = await browser.new_context()
        
        await context.add_cookies([{
            "name": "li_at",
            "value": COOKIE_VALUE,
            "domain": ".www.linkedin.com",
            "path": "/"
        }])
        
        page = await context.new_page()
        print("🤖 AI Agent searching LinkedIn Inbox for unread messages...")
        await page.goto("https://linkedin.com")
        await page.wait_for_timeout(random.randint(4000, 6000))
        
        try:
            conversation_cards = page.locator("li.msg-conversations-container__convo-item")
            count = await conversation_cards.count()
            
            for i in range(min(count, 3)): # Limit to top 3 conversations per sweep loop run cycle to remain completely safe
                card = conversation_cards.nth(i)
                unread_badge = card.locator(".msg-conversations-container__unread-count")
                
                if await unread_badge.is_visible():
                    sender_raw = await card.locator(".msg-conversations-container__participant-name").inner_text()
                    sender_name = sender_raw.strip()
                    print(f"\n📬 Processing unread thread from: {sender_name}")
                    
                    # Open the chat context panel window
                    await card.click()
                    await page.wait_for_timeout(random.randint(2000, 3000))
                    
                    # Isolate chat thread history content strings
                    msg_bubbles = page.locator(".msg-s-message-list-container .msg-s-event-listitem__body")
                    bubble_count = await msg_bubbles.count()
                    
                    if bubble_count > 0:
                        latest_incoming_text = await msg_bubbles.nth(bubble_count - 1).inner_text()
                        incoming_cleaned = latest_incoming_text.strip()
                        print(f"📥 Received Text: \"{incoming_cleaned}\"")
                        
                        # Trigger the live LLM completion engine thread call 
                        print("🧠 Thinking... Querying OpenAI completion framework for draft...")
                        ai_response = generate_ai_reply(sender_name, incoming_cleaned)
                        print(f"🤖 Generated Response: \"{ai_response}\"")
                        
                        # Locate input target field element, simulate typing rhythm, and ship the data payload
                        reply_box = page.locator("div[role='textbox'][contenteditable='true']").first
                        if await reply_box.is_visible():
                            await reply_box.click()
                            await reply_box.fill(ai_response)
                            await page.wait_for_timeout(random.randint(2000, 4000)) # Mimic human review spacing
                            
                            send_button = page.get_by_role("button", name="Send").first
                            await send_button.click()
                            print(f"🚀 Reply sent cleanly to {sender_name}!")
                            await page.wait_for_timeout(2000)
                else:
                    print(f"⏭️ Thread {i+1} already marked read. Skipping...")
                    
            print("\n🤖 Run cycle processing run complete. Agent context closing successfully.")
            
        except Exception as e:
            print(f"⚠️ Operation workflow exception context caught: {e}")
            
        await page.wait_for_timeout(3000)
        await browser.close()

if __name__ == "__main__":
    asyncio.run(run_ai_agent_inbox())
