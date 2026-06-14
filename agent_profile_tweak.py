import os
import asyncio
import random
from bs4 import BeautifulSoup
from playwright.async_api import async_playwright
from openai import OpenAI
from dotenv import load_dotenv

# Initialize hooks
load_dotenv()
COOKIE_VALUE = os.getenv("LINKEDIN_SESSION_COOKIE")
OPENAI_KEY = os.getenv("OPENAI_API_KEY")

ai_client = OpenAI(api_key=OPENAI_KEY)

def generate_profile_tweaks(current_headline, current_about):
    """Passes current profile elements to OpenAI to analyze and generate enhancements."""
    print("🧠 AI is analyzing your current text for optimizations...")
    try:
        system_prompt = (
            "You are a world-class professional brand strategist and copywriter. "
            "Analyze the user's current LinkedIn profile text and optimize it. "
            "Make it highly engaging, impactful, clear, and filled with search keywords.\n\n"
            "CRITICAL: You must respond in a strict format separating your adjustments "
            "using the exact headers: [NEW_HEADLINE] and [NEW_ABOUT]. Do not include any conversational pleasantries."
        )
        
        user_prompt = (
            f"CURRENT HEADLINE:\n{current_headline}\n\n"
            f"CURRENT ABOUT SECTION:\n{current_about}\n\n"
            f"Improve both sections to make them sound exceptional and visually clean."
        )
        
        response = ai_client.chat.completions.create(
            model="gpt-4o-mini",
            messages=[
                {"role": "system", "content": system_prompt},
                {"role": "user", "content": user_prompt}
            ],
            temperature=0.8
        )
        
        output = response.choices[0].message.content.strip()
        
        # Parse the structured response back out cleanly
        new_headline = output.split("[NEW_HEADLINE]")[1].split("[NEW_ABOUT]")[0].strip()
        new_about = output.split("[NEW_ABOUT]")[1].strip()
        
        return new_headline, new_about
    except Exception as e:
        print(f"❌ LLM profile processing engine failed: {e}")
        return None, None

async def optimize_my_profile():
    if not COOKIE_VALUE or not OPENAI_KEY:
        print("❌ Configuration Missing inside your .env file.")
        return

    async with async_playwright() as p:
        browser = await p.chromium.launch(headless=False) # Visually supervise the profile changes!
        context = await browser.new_context()
        
        await context.add_cookies([{
            "name": "li_at",
            "value": COOKIE_VALUE,
            "domain": ".www.linkedin.com",
            "path": "/"
        }])
        
        page = await context.new_page()
        print("🤖 Navigating straight to your profile editing dashboard...")
        await page.goto("https://linkedin.com") # Overrides directly to your personal logged-in profile
        await page.wait_for_timeout(5000)
        
        # --- PHASE 1: READ PROFILE DATA ---
        html_content = await page.content()
        soup = BeautifulSoup(html_content, "html.parser")
        
        # Extract existing Headline
        headline_element = soup.find("div", {"class": "text-body-medium"})
        current_headline = headline_element.get_text().strip() if headline_element else "Not Found"
        
        # Extract existing Summary/About
        about_element = soup.find("div", {"class": "display-flex phasing-modal-trigger-hook"})
        current_about = about_element.get_text().strip() if about_element else "Not Found"
        
        print(f"\n📥 Extracted Current Headline:\n> \"{current_headline}\"")
        
        # --- PHASE 2: GENERATE THE OPTIMIZATIONS ---
        new_headline, new_about = generate_profile_tweaks(current_headline, current_about)
        
        if not new_headline or not new_about:
            print("❌ Stopping optimization run due to an issue generating modifications.")
            await browser.close()
            return
            
        print(f"\n✨ AI Recommended Headline:\n> \"{new_headline}\"")
        
        # --- PHASE 3: EXECUTE THE PROFILE UPDATE ---
        # Direct navigation shortcut straight to your account intro structural editing model popup panel
        print("\n⚙️ Opening intro section editor popup layer...")
        await page.goto("https://linkedin.com/edit/forms/intro/new/")
        await page.wait_for_timeout(3000)
        
        try:
            # Locate the correct Headline textbox element
            headline_input = page.locator("input[id^='single-line-text-form-component-']").first
            if await headline_input.is_visible():
                print("✍️ Overwriting old headline with optimized AI variation text...")
                await headline_input.click()
                # Clear existing text cleanly before filling
                await page.keyboard.press("Control+A")
                await page.keyboard.press("Backspace")
                await headline_input.fill(new_headline)
                await page.wait_for_timeout(2000)
                
                # Scroll down inside popup modal layer and trigger the Save event button
                save_button = page.get_by_role("button", name="Save").first
                await save_button.click()
                print("✅ Headline successfully updated and synced live!")
                await page.wait_for_timeout(4000)
                
        except Exception as edit_error:
            print(f"⚠️ Headline save flow caught an issue: {edit_error}")

        # Direct navigation shortcut to edit the summary/About section text modal panel box
        print("\n⚙️ Navigating to your About section editor panel block...")
        await page.goto("https://linkedin.com/edit/about/")
        await page.wait_for_timeout(3000)
        
        try:
            # Locate the primary text editing box
            about_textarea = page.locator("textarea").first
            if await about_textarea.is_visible():
                print("✍️ Writing the advanced brand messaging description narrative...")
                await about_textarea.click()
                await page.keyboard.press("Control+A")
                await page.keyboard.press("Backspace")
                await about_textarea.fill(new_about)
                await page.wait_for_timeout(2000)
                
                save_about = page.get_by_role("button", name="Save").first
                await save_about.click()
                print("✅ Summary About section text successfully optimized and saved!")
                
        except Exception as about_error:
            print(f"⚠️ About text block field adjustment failed: {about_error}")
            
        print("\n🤖 Profile refinement sweep completed. Closing background session windows.")
        await page.wait_for_timeout(3000)
        await browser.close()

if __name__ == "__main__":
    asyncio.run(optimize_my_profile())
