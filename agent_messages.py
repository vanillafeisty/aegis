import os
import asyncio
import random
from playwright.async_api import async_playwright
from dotenv import load_dotenv

# Load credentials from your root .env configuration
load_dotenv()
COOKIE_VALUE = os.getenv("LINKEDIN_SESSION_COOKIE")

async def process_inbox_and_reply():
    if not COOKIE_VALUE:
        print("❌ Error: LINKEDIN_SESSION_COOKIE is missing inside your .env file.")
        return

    async with async_playwright() as p:
        # Launching browser. Set headless=False so you can watch your AI think and type!
        browser = await p.chromium.launch(headless=False)
        context = await browser.new_context()
        
        # Inject cookie token to bypass the login pages entirely
        await context.add_cookies([{
            "name": "li_at",
            "value": COOKIE_VALUE,
            "domain": ".www.linkedin.com",
            "path": "/"
        }])
        
        page = await context.new_page()
        print("🤖 Agent navigating directly to LinkedIn Messaging dashboard...")
        await page.goto("https://linkedin.com")
        await page.wait_for_timeout(random.randint(4000, 6000)) # Natural delay for elements to mount
        
        try:
            # 1. Locate all dynamic message items inside the left sidebar stream cards
            conversation_cards = page.locator("li.msg-conversations-container__convo-item")
            count = await conversation_cards.count()
            print(f"📥 Found {count} recent active conversation streams in your view.")
            
            for i in range(min(count, 5)): # Process the 5 most recent threads to avoid automation penalties
                card = conversation_cards.nth(i)
                
                # Look for the internal visual badge indicating an unread message thread
                unread_badge = card.locator(".msg-conversations-container__unread-count")
                
                if await unread_badge.is_visible():
                    sender_name = await card.locator(".msg-conversations-container__participant-name").inner_text()
                    sender_name = sender_name.strip()
                    print(f"✉️ New unread message found from: {sender_name}!")
                    
                    # Click the conversation card stream container to load chat content on the right
                    await card.click()
                    await page.wait_for_timeout(random.randint(2000, 3500))
                    
                    # 2. Extract the literal incoming text payload string
                    # Grabs the text elements within the loaded message bubble container list
                    msg_bubbles = page.locator(".msg-s-message-list-container .msg-s-event-listitem__body")
                    bubble_count = await msg_bubbles.count()
                    
                    if bubble_count > 0:
                        latest_incoming_text = await msg_bubbles.nth(bubble_count - 1).inner_text()
                        print(f"💬 Last Text Received: \"{latest_incoming_text.strip()}\"")
                        
                        # --- THIS IS WHERE YOUR LLM LOGIC PLUGS IN ---
                        # Pass 'latest_incoming_text' to your OpenAI/LangChain model to generate custom response.
                        # For now, we will drop in a quick contextual placeholder message block string.
                        ai_reply_draft = f"Hi {sender_name.split()[0]}, thanks for reaching out! This is an automated confirmation caught by my personal project assistant agent. Talk soon!"
                        # ---------------------------------------------
                        
                        # 3. Locate the text container, simulate human key presses, and transmit
                        reply_box = page.locator("div[role='textbox'][contenteditable='true']").first
                        if await reply_box.is_visible():
                            print(f"✍️ Drafting response to {sender_name}...")
                            await reply_box.click()
                            await reply_box.fill(ai_reply_draft)
                            await page.wait_for_timeout(random.randint(1500, 3000)) # Emulate reading/typing rhythm
                            
                            # Fire the reply message package by hitting the Send submission trigger element
                            send_button = page.get_by_role("button", name="Send").first
                            await send_button.click()
                            print(f"✅ Reply smoothly sent to {sender_name}.")
                            await page.wait_for_timeout(2000)
                else:
                    pass # Thread has already been read, skipping to check the next one
                    
            print("🤖 Finished checking message queue processing run cycle.")
            
        except Exception as e:
            print(f"⚠️ Pipeline execution warning encountered: {e}")
            
        await page.wait_for_timeout(3000)
        await browser.close()

if __name__ == "__main__":
    asyncio.run(process_inbox_and_reply())
