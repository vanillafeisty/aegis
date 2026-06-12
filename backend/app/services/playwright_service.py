"""Playwright browser automation engine."""
import asyncio
import logging
import random
from typing import Optional

from playwright.async_api import async_playwright, Browser, Page

from app.core.security import decrypt_credential

logger = logging.getLogger("aegis")


class PlaywrightService:
    def __init__(self):
        self.browser: Optional[Browser] = None
        self.pages: dict = {}  # user_id -> Page
        self.playwright = None

    async def initialize(self):
        """Initialize Playwright browser."""
        self.playwright = await async_playwright().start()
        self.browser = await self.playwright.chromium.launch(
            headless=True,
            args=["--disable-blink-features=AutomationControlled"],
        )
        logger.info("✅ Playwright browser initialized")

    async def close(self):
        """Close Playwright browser."""
        if self.browser:
            await self.browser.close()
        if self.playwright:
            await self.playwright.stop()

    async def create_session(self, user_id: str, linkedin_email: str, linkedin_pass_enc: str) -> bool:
        """Create isolated browser session for user."""
        try:
            page = await self.browser.new_page()

            # Set anti-detection headers
            await page.set_extra_http_headers({
                "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36"
            })

            # Navigate to LinkedIn login
            await page.goto("https://www.linkedin.com/login", wait_until="networkidle")

            # Decrypt credentials
            linkedin_pass = decrypt_credential(linkedin_pass_enc)

            # Fill login form
            await page.fill("input[name='session_key']", linkedin_email)
            await page.fill("input[name='session_password']", linkedin_pass)
            await page.click("button[type='submit']")

            # Wait for redirect to feed
            await page.wait_for_url("**/feed/**", timeout=30000)
            logger.info(f"✅ LinkedIn login successful for user {user_id}")

            self.pages[user_id] = page
            return True

        except Exception as e:
            logger.error(f"❌ LinkedIn login failed: {e}")
            return False

    async def search_jobs(self, user_id: str, query: str, location: str = "") -> list:
        """Search LinkedIn jobs."""
        if user_id not in self.pages:
            return []

        page = self.pages[user_id]
        try:
            # Navigate to jobs page
            search_url = f"https://www.linkedin.com/jobs/search/?keywords={query}"
            if location:
                search_url += f"&location={location}"

            await page.goto(search_url, wait_until="networkidle")
            await asyncio.sleep(random.uniform(2, 4))

            # Extract job cards
            jobs = []
            job_cards = await page.query_selector_all(".base-card")

            for card in job_cards[:20]:  # Limit to 20 results
                try:
                    job_id = await card.get_attribute("data-job-id")
                    title_el = await card.query_selector(".base-search-card__title")
                    company_el = await card.query_selector(".base-search-card__subtitle")
                    location_el = await card.query_selector(".job-search-card__location")

                    title = await title_el.text_content() if title_el else "N/A"
                    company = await company_el.text_content() if company_el else "N/A"
                    location = await location_el.text_content() if location_el else "N/A"

                    jobs.append({
                        "job_id": job_id,
                        "title": title.strip() if title else "",
                        "company": company.strip() if company else "",
                        "location": location.strip() if location else "",
                    })
                except Exception as e:
                    logger.warning(f"Failed to parse job card: {e}")

            return jobs

        except Exception as e:
            logger.error(f"❌ Job search failed: {e}")
            return []

    async def send_connection_request(self, user_id: str, profile_url: str, message: str = "") -> bool:
        """Send connection request to a LinkedIn profile."""
        if user_id not in self.pages:
            return False

        page = self.pages[user_id]
        try:
            await page.goto(profile_url, wait_until="networkidle")
            await asyncio.sleep(random.uniform(2, 3))

            # Find and click connect button
            connect_btn = await page.query_selector("[aria-label='Connect']")
            if not connect_btn:
                connect_btn = await page.query_selector("button:has-text('Connect')")

            if connect_btn:
                await connect_btn.click()
                await asyncio.sleep(1)

                # Fill optional message
                if message:
                    msg_input = await page.query_selector("textarea")
                    if msg_input:
                        await msg_input.fill(message)

                # Send request
                send_btn = await page.query_selector("button:has-text('Send')")
                if send_btn:
                    await send_btn.click()
                    await asyncio.sleep(1)

                logger.info(f"✅ Connection request sent for user {user_id}")
                return True

        except Exception as e:
            logger.error(f"❌ Connection request failed: {e}")

        return False

    async def publish_post(self, user_id: str, post_text: str, image_path: Optional[str] = None) -> bool:
        """Publish a LinkedIn post."""
        if user_id not in self.pages:
            return False

        page = self.pages[user_id]
        try:
            # Navigate to feed
            await page.goto("https://www.linkedin.com/feed/", wait_until="networkidle")
            await asyncio.sleep(random.uniform(2, 3))

            # Click post editor
            post_editor = await page.query_selector(".share-box-feed-entry__trigger")
            if post_editor:
                await post_editor.click()
                await asyncio.sleep(1)

            # Fill post text
            text_area = await page.query_selector("textarea[aria-label*='Write']")
            if text_area:
                await text_area.fill(post_text)
                await asyncio.sleep(1)

            # Upload image if provided
            if image_path:
                file_input = await page.query_selector("input[type='file']")
                if file_input:
                    await file_input.set_input_files(image_path)
                    await asyncio.sleep(2)

            # Click publish
            publish_btn = await page.query_selector("button:has-text('Post')")
            if publish_btn:
                await publish_btn.click()
                await asyncio.sleep(2)

            logger.info(f"✅ Post published for user {user_id}")
            return True

        except Exception as e:
            logger.error(f"❌ Post publishing failed: {e}")

        return False

    async def close_session(self, user_id: str):
        """Close user session."""
        if user_id in self.pages:
            try:
                await self.pages[user_id].close()
                del self.pages[user_id]
                logger.info(f"✅ Session closed for user {user_id}")
            except Exception as e:
                logger.error(f"Failed to close session: {e}")


# Global Playwright instance
pw_service = PlaywrightService()
