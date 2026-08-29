"""
AEGIS AI - Direct Environment Setup Script
This script:
1. Creates .env file with correct format (no line ending issues)
2. Verifies it can be read
3. Tests each credential loads correctly
"""

import os
from pathlib import Path

# Get the script's directory
BACKEND_DIR = Path(__file__).resolve().parent
ENV_FILE = BACKEND_DIR / ".env"

# Template credentials (paste your credentials here or use .env)
ENV_CONTENT = """LINKEDIN_SESSION_COOKIE=your_linkedin_session_cookie_here
LINKEDIN_CLIENT_ID=your_linkedin_client_id_here
GROQ_API_KEY=your_groq_api_key_here
ZAPIER_MCP_URL=https://mcp.zapier.com/api/v1/connect
GROQ_MODEL=llama-3.3-70b-versatile
SMTP_EMAIL=your_email@gmail.com
SMTP_PASSWORD=your_app_password_here
CLIENT_SECRET=your_client_secret_here
REDIRECT_URI=http://localhost:8000/callback
LINKEDIN_ACCESS_TOKEN=your_access_token_here
ENVIRONMENT=development
DEBUG=True
LOG_LEVEL=INFO
"""

def setup_env():
    """Setup and verify .env file"""
    
    print("\n" + "="*70)
    print("AEGIS AI - ENVIRONMENT SETUP")
    print("="*70)
    print(f"Backend directory: {BACKEND_DIR}")
    print(f"Target .env file: {ENV_FILE}")
    print("="*70 + "\n")
    
    # Step 1: Write .env file
    print("📝 Writing .env file...")
    try:
        # Write with Unix line endings (LF, not CRLF)
        with open(ENV_FILE, 'w', newline='\n') as f:
            f.write(ENV_CONTENT.strip())
        print(f"✅ .env file created successfully")
    except Exception as e:
        print(f"❌ Error writing .env: {e}")
        return False
    
    # Step 2: Verify file exists
    print("\n📋 Verifying file exists...")
    if ENV_FILE.exists():
        file_size = ENV_FILE.stat().st_size
        print(f"✅ .env file exists ({file_size} bytes)")
    else:
        print(f"❌ .env file does not exist!")
        return False
    
    # Step 3: Read and validate
    print("\n🔍 Reading and validating .env...")
    try:
        with open(ENV_FILE, 'r') as f:
            content = f.read()
        
        lines = [l for l in content.split('\n') if l.strip() and not l.startswith('#')]
        print(f"✅ Found {len(lines)} environment variables:\n")
        
        for line in lines:
            if '=' in line:
                key, value = line.split('=', 1)
                key = key.strip()
                value = value.strip()
                
                # Show masked value for security
                if len(value) > 20:
                    masked = value[:10] + "..." + value[-5:]
                else:
                    masked = value if len(value) < 10 else value[:10] + "..."
                
                print(f"  ✅ {key:30} = {masked}")
    
    except Exception as e:
        print(f"❌ Error reading .env: {e}")
        return False
    
    # Step 4: Test loading with python-dotenv
    print("\n🧪 Testing with python-dotenv...")
    try:
        from dotenv import load_dotenv
        
        # Clear any existing values
        for key in list(os.environ.keys()):
            if key.startswith(('LINKEDIN', 'GROQ', 'SMTP', 'ZAPIER', 'CLIENT', 'REDIRECT', 'ENVIRONMENT', 'DEBUG', 'LOG')):
                del os.environ[key]
        
        # Load the .env file
        load_dotenv(dotenv_path=ENV_FILE, override=True)
        
        # Verify loaded
        print("\n✅ Environment variables loaded:\n")
        
        expected_keys = [
            'LINKEDIN_SESSION_COOKIE',
            'LINKEDIN_CLIENT_ID',
            'LINKEDIN_ACCESS_TOKEN',
            'GROQ_API_KEY',
            'GROQ_MODEL',
            'SMTP_EMAIL',
            'SMTP_PASSWORD',
            'ZAPIER_MCP_URL'
        ]
        
        all_set = True
        for key in expected_keys:
            value = os.getenv(key)
            if value:
                if len(value) > 20:
                    masked = value[:10] + "..." + value[-5:]
                else:
                    masked = value[:10] + "..."
                print(f"  ✅ {key:30} = {masked}")
            else:
                print(f"  ❌ {key:30} = NOT SET")
                all_set = False
        
        return all_set
    
    except ImportError:
        print("❌ python-dotenv not installed!")
        print("   Run: pip install python-dotenv")
        return False
    except Exception as e:
        print(f"❌ Error loading .env: {e}")
        return False

def create_startup_script():
    """Create a startup script that loads .env before running app"""
    
    startup_script = f"""#!/usr/bin/env python
\"\"\"
Aegis AI Backend Startup Script
This ensures .env is loaded before app starts
\"\"\"

import sys
from pathlib import Path
import os

# Ensure .env is in the right place
backend_dir = Path(__file__).resolve().parent
env_file = backend_dir / ".env"

if not env_file.exists():
    print(f"ERROR: .env file not found at {{env_file}}")
    print("Run: python setup_env.py")
    sys.exit(1)

# Load environment FIRST
from dotenv import load_dotenv
load_dotenv(dotenv_path=str(env_file), override=True)

# Verify credentials loaded
required_keys = ['GROQ_API_KEY', 'SMTP_EMAIL', 'SMTP_PASSWORD']
missing = [k for k in required_keys if not os.getenv(k)]

if missing:
    print(f"ERROR: Missing credentials: {{', '.join(missing)}}")
    sys.exit(1)

# NOW import and run app
from app_final_fixed import app
import uvicorn

if __name__ == "__main__":
    uvicorn.run(
        app,
        host="0.0.0.0",
        port=8000,
        log_level="info"
    )
"""
    
    startup_file = BACKEND_DIR / "run_app.py"
    with open(startup_file, 'w') as f:
        f.write(startup_script)
    
    return startup_file

if __name__ == "__main__":
    success = setup_env()
    
    if success:
        print("\n" + "="*70)
        print("✅ ENVIRONMENT SETUP COMPLETE!")
        print("="*70)
        print("\nNext steps:")
        print("1. Restart your backend:")
        print("   python app_final_fixed.py")
        print("\n2. Check dashboard at:")
        print("   http://localhost:3000")
        print("\n3. All credentials should now show ✅ GREEN")
        print("="*70 + "\n")
    else:
        print("\n" + "="*70)
        print("❌ SETUP FAILED - Check errors above")
        print("="*70 + "\n")
        sys.exit(1)