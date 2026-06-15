import os
import sys
import asyncio
from dotenv import load_dotenv

# Import only your exact sidebar workspace modules
try:
    import agent_post
    import agent_connect
    import agent_email
    import agent_profile_tweak
    import ai_agent_inbox
except ImportError as e:
    print(f"\n⚠️ Directory Sync Issue: {e}")

load_dotenv()

def print_banner():
    os.system('cls' if os.name == 'nt' else 'clear')
    print("=" * 60)
    print("      🛡️  AEGIS AI AGENT PLATFORM CENTRAL DASHBOARD  🛡️")
    print("=" * 60)
    print(" 1. [LINKEDIN] Publish an Automated Post to Feed")
    print(" 2. [LINKEDIN] Check Inbox DMs & Auto-Reply with OpenAI")
    print(" 3. [LINKEDIN] Send a Dynamic Outbound Connection Request")
    print(" 4. [LINKEDIN] Audit and Enhance Profile Summary Text")
    print(" 5. [GMAIL]    Send a Verification Test Email via SMTP")
    print(" 0. Exit Agent Operations Workspace")
    print("=" * 60)

async def main_loop():
    while True:
        print_banner()
        choice = input("👉 Enter an operation number to execute: ").strip()
        
        if choice == "1":
            print("\n🚀 Operation Activated: Publishing content to feed...")
            custom_update = (
                "Deploying custom autonomous system frameworks! "
                "This update was constructed and processed directly via my central Playwright core model setup. 🚀🤖 #AIAgents"
            )
            # Execute your clean browser poster mechanism cleanly
            agent_post.run_feed_post_via_browser(custom_update)
            input("\nPress Enter to return to menu...")
            
        elif choice == "2":
            print("\n📥 Operation Activated: Running inbox processing sweep...")
            await ai_agent_inbox.run_ai_agent_inbox()
            input("\nPress Enter to return to menu...")
            
        elif choice == "3":
            print("\n✉️  Operation Activated: Connection Engine Booting...")
            target_url = input("Paste the target profile URL link to invite: ").strip()
            if target_url.startswith("https://"):
                await agent_connect.send_connection_request(target_url)
            else:
                print("❌ Invalid URL string structure provided.")
            input("\nPress Enter to return to menu...")
            
        elif choice == "4":
            print("\n⚙️  Operation Activated: Opening profile copy layout tuning forms...")
            await agent_profile_tweak.optimize_my_profile()
            input("\nPress Enter to return to menu...")
            
        elif choice == "5":
            print("\n🔒 Operation Activated: Executing SMTP mail verification check...")
            os.system("python agent_email.py")
            input("\nPress Enter to return to menu...")
            
        elif choice == "0":
            print("\n👋 Deactivating workspace environment logs. Goodbye!")
            sys.exit(0)
            
        else:
            print("\n❌ Command selection out of scope. Try again.")
            await asyncio.sleep(1.5)

if __name__ == "__main__":
    try:
        asyncio.run(main_loop())
    except KeyboardInterrupt:
        print("\n👋 Forcefully closing agent panels.")
