import os
import asyncio
import random
from playwright.async_api import async_playwright
from dotenv import load_dotenv

# Load browser metrics from your .env settings
load_dotenv()
COOKIE_VALUE = os.getenv("LINKEDIN_SESSION_COOKIE")

def run_feed_post_via_browser(post_text_content):
    """Launches a stealth automation container to publish updates directly to your feed."""
    if not COOKIE_VALUE:
        print("❌ Error: LINKEDIN_SESSION_COOKIE is missing in your .env file.")
        return False

    async def execute():
        async with async_playwright() as p:
            # Set headless=False so you can visually watch your agent click and post!
            browser = await p.chromium.launch(headless=False)
            context = await browser.new_context()
            
            # Inject your active session cookie to bypass login screens
            await context.add_cookies([{
                "name": "li_at",
                "value": COOKIE_VALUE,
                "domain": ".www.linkedin.com",
                "path": "/"
            }])
            
            page = await context.new_page()
            print("🤖 Navigating straight to your LinkedIn homepage feed...")
            await page.goto("https://linkedin.com")
            await page.wait_for_timeout(random.randint(4000, 6000))
            
            try:
                # 1. Locate and click the primary 'Start a post' trigger button
                print("📝 Locating share box element...")
                share_trigger = page.locator("button.share-box-feed-entry__trigger").first
                await share_trigger.click()
                await page.wait_for_timeout(random.randint(1500, 2500))
                
                # 2. Locate the active text field container layer and insert copy
                print("✍️ Typing your AI agent update text payload...")
                editor_field = page.locator("div[role='textbox'][contenteditable='true']").first
                await editor_field.click()
                await editor_field.fill(post_text_content)
                await page.wait_for_timeout(random.randint(2000, 3500))
                
                # 3. Locate the primary submission completion element button and send
                print("🚀 Shipping post live...")
                post_button = page.locator("button.share-actions__post-action").first
                await post_button.click()
                await page.wait_for_timeout(4000)
                
                print("✅ Success! Your update has been successfully published to your personal profile feed.")
                return True
                
            except Exception as e:
                print(f"❌ Failed to post content over browser layer: {e}")
                return False
            finally:
                await browser.close()

    # Runs the async task loop context cleanly inside synchronous frameworks
    return asyncio.run(execute())

if __name__ == "__main__":
    test_message = "Hello World! This post was automatically written and published by my custom AI agent workspace environment. 🚀🤖 #AIAgents #Python #Automation"
    run_feed_post_via_browser(test_message)
