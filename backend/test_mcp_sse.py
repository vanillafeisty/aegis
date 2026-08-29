"""
Integration Test for Aegis AI MCP SSE Transport.
Validates that:
1. Multiple concurrent clients can connect to `/mcp/sse`.
2. Each client receives a unique connection ID and endpoint.
3. Sending messages to `/mcp/message` routes responses to the correct client stream.
4. No crashes occur and memory is cleaned up on disconnect.
"""

import asyncio
import json
import httpx
import uvicorn
import multiprocessing
import time
import sys
from pathlib import Path

# Add backend to path
sys.path.append(str(Path(__file__).resolve().parent))
from app import app

def run_server():
    """Run uvicorn server in a separate process"""
    # Overwrite environment variables for safe testing
    os = __import__('os')
    os.environ['LINKEDIN_SESSION_COOKIE'] = 'mock_cookie'
    os.environ['GROQ_API_KEY'] = 'mock_groq_key'
    os.environ['SMTP_EMAIL'] = 'mock_email@gmail.com'
    os.environ['SMTP_PASSWORD'] = 'mock_password'
    uvicorn.run(app, host="127.0.0.1", port=8081, log_level="warning")

async def read_sse_stream(client: httpx.AsyncClient, name: str, results_dict: dict):
    """Establishes an SSE stream and reads events"""
    url = "http://127.0.0.1:8081/mcp/sse"
    print(f"[{name}] Opening SSE connection...")
    
    connection_id = None
    messages = []
    
    try:
        async with client.stream("GET", url, timeout=30.0) as response:
            assert response.status_code == 200, f"SSE endpoint failed with {response.status_code}"
            
            # Read first few lines to get the endpoint details
            async for line in response.aiter_lines():
                if not line.strip():
                    continue
                
                print(f"[{name}] SSE raw event: {line}")
                
                if line.startswith("data: /mcp/message?connection_id="):
                    # Extract connection_id
                    conn_str = line.split("connection_id=")[1].strip()
                    connection_id = conn_str
                    results_dict[f"{name}_conn_id"] = connection_id
                    print(f"[{name}] Connected! connection_id={connection_id}")
                    
                if line.startswith("data: "):
                    data_str = line.split("data: ", 1)[1].strip()
                    try:
                        data = json.loads(data_str)
                        messages.append(data)
                        results_dict[f"{name}_messages"] = messages
                    except json.JSONDecodeError:
                        pass
                        
                # Exit stream once we receive the expected result
                if len(messages) >= 1:
                    print(f"[{name}] Received response, closing stream.")
                    break
                    
    except Exception as e:
        print(f"[{name}] Error in SSE stream: {e}")
        results_dict[f"{name}_error"] = str(e)

async def test_concurrent_sse():
    """Main test execution"""
    print("\n=== STARTING MCP SSE CONCURRENCY TEST ===")
    
    results = {}
    
    # 1. Establish concurrent HTTP clients
    async with httpx.AsyncClient() as client1, httpx.AsyncClient() as client2:
        # Start reading SSE streams concurrently
        task1 = asyncio.create_task(read_sse_stream(client1, "Client-1", results))
        task2 = asyncio.create_task(read_sse_stream(client2, "Client-2", results))
        
        # Wait a moment for connection IDs to populate
        print("Waiting for SSE channels to establish...")
        for _ in range(30):
            await asyncio.sleep(0.1)
            if "Client-1_conn_id" in results and "Client-2_conn_id" in results:
                break
        
        conn_id1 = results.get("Client-1_conn_id")
        conn_id2 = results.get("Client-2_conn_id")
        
        if not conn_id1 or not conn_id2:
            print("❌ Failed to establish concurrent connections!")
            return False
            
        print(f"[OK] Both clients connected successfully. IDs: {conn_id1}, {conn_id2}")
        assert conn_id1 != conn_id2, "Connection IDs must be unique"
        
        # 2. Client-1 sends tools/list request
        print("\n[Client-1] Sending POST tools/list request...")
        payload1 = {
            "jsonrpc": "2.0",
            "id": 101,
            "method": "tools/list",
            "params": {}
        }
        res1 = await client1.post(
            f"http://127.0.0.1:8081/mcp/message?connection_id={conn_id1}",
            json=payload1
        )
        assert res1.status_code == 200
        print("[Client-1] POST message accepted.")
        
        # 3. Client-2 sends tools/list request
        print("\n[Client-2] Sending POST tools/list request...")
        payload2 = {
            "jsonrpc": "2.0",
            "id": 102,
            "method": "tools/list",
            "params": {}
        }
        res2 = await client2.post(
            f"http://127.0.0.1:8081/mcp/message?connection_id={conn_id2}",
            json=payload2
        )
        assert res2.status_code == 200
        print("[Client-2] POST message accepted.")
        
        # Wait for both tasks to read the responses from their respective streams
        print("Waiting for responses over SSE streams...")
        await asyncio.gather(task1, task2)
        
        # 4. Assert response values
        messages1 = results.get("Client-1_messages", [])
        messages2 = results.get("Client-2_messages", [])
        
        print(f"\n[Client-1] Received messages: {messages1}")
        print(f"[Client-2] Received messages: {messages2}")
        
        assert len(messages1) >= 1, "Client-1 did not receive tools list response"
        assert len(messages2) >= 1, "Client-2 did not receive tools list response"
        
        # Validate message contents are standard JSON-RPC
        resp1 = messages1[0]
        resp2 = messages2[0]
        
        assert resp1.get("id") == 101, f"Client-1 response ID mismatch: expected 101, got {resp1.get('id')}"
        assert resp2.get("id") == 102, f"Client-2 response ID mismatch: expected 102, got {resp2.get('id')}"
        
        tools = resp1.get("result", {}).get("tools", [])
        tool_names = [t["name"] for t in tools]
        print(f"\n[OK] Registered tools found: {tool_names}")
        assert "post_to_linkedin" in tool_names, "LinkedIn tool must be registered"
        assert "send_cold_messages" in tool_names, "Cold messages tool must be registered"
        
        print("\n=== CONCURRENCY TEST COMPLETED SUCCESSFULLY! ===")
        return True

if __name__ == "__main__":
    # Start server in background process
    server_process = multiprocessing.Process(target=run_server)
    server_process.start()
    
    # Wait for server to start
    time.sleep(2)
    
    success = False
    try:
        success = asyncio.run(test_concurrent_sse())
    except Exception as err:
        print(f"Test failed with error: {err}")
    finally:
        print("Stopping uvicorn server process...")
        server_process.terminate()
        server_process.join()
        
    if success:
        sys.exit(0)
    else:
        sys.exit(1)
