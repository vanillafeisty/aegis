import os
import asyncio
from playwright.async_api import async_playwright
from dotenv import load_dotenv

load_dotenv()
COOKIE_VALUE = os.getenv("LINKEDIN_SESSION_COOKIE")

async def send_connection_request(target_profile_url):
    async with async_playwright() as p:
        # Launch browser. Set headless=False to visually see your agent work!
        browser = await p.chromium.launch(headless=False)
        context = await browser.new_context()
        
        # Inject your live session cookie
        await context.add_cookies([{
            "name": "li_at",
            "value": COOKIE_VALUE,
            "domain": ".www.linkedin.com",
            "path": "/"
        }])
        
        page = await context.new_page()
        print(f"Navigating to prospect profile: {target_profile_url}")
        await page.goto(target_profile_url)
        await page.wait_for_timeout(3000) # Give the page time to load fully
        
        try:
            # 1. Look for a visible 'Connect' button on the profile header
            connect_button = page.get_by_role("button", name="Connect", exact=True).first
            
            # 2. If 'Connect' is hidden under the 'More' dropdown menu
            if not await connect_button.is_visible():
                print("'Connect' not instantly visible. Clicking 'More' dropdown...")
                more_button = page.get_by_role("button", name="More actions").first
                await more_button.click()
                await page.wait_for_timeout(1000)
                connect_button = page.get_by_role("button", name="Connect").first
                
            if await connect_button.is_visible():
                await connect_button.click()
                await page.wait_for_timeout(1500)
                
                # 3. LinkedIn will ask if you want to 'Send without a note' or 'Add a note'
                # For an AI Agent, we want to click 'Send without a note' (or add custom AI text)
                send_now_button = page.get_by_role("button", name="Send without a note")
                await send_now_button.click()
                print(f"🚀 Success! Connection request sent to {target_profile_url}")
            else:
                print("❌ Could not find a 'Connect' button. You might already be connected or pending.")
                
        except Exception as e:
            print(f"An error occurred while attempting connection: {e}")
            
        await page.wait_for_timeout(2000)
        await browser.close()

if __name__ == "__main__":
    # Test it on a public profile (replace with any valid profile link to test)
    test_url = "https://linkedin.com" 
    asyncio.run(send_connection_request(test_url))
