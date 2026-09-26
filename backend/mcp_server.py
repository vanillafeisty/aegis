"""
MCP Server with HTTP Streaming Transport
Zapier Automation Integration
"""

import json
import asyncio
import logging
from typing import Any, AsyncIterator, Optional
from dataclasses import dataclass, asdict
from enum import Enum
import httpx

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

# ==================== MCP Protocol Types ====================

class MessageType(str, Enum):
    """MCP Message Types"""
    JSONRPC = "jsonrpc"
    TOOLS_LIST = "tools/list"
    TOOL_CALL = "tools/call"
    RESOURCE_READ = "resources/read"
    PROMPT_GET = "prompts/get"

@dataclass
class MCPMessage:
    """MCP Protocol Message"""
    jsonrpc: str = "2.0"
    id: Optional[int] = None
    method: str = ""
    params: dict = None
    result: Any = None
    error: Optional[dict] = None

    def to_dict(self) -> dict:
        return {k: v for k, v in asdict(self).items() if v is not None}

    def to_json(self) -> str:
        return json.dumps(self.to_dict()) + "\n"

# ==================== MCP Tools Definition ====================

ZAPIER_TOOLS = [
    {
        "name": "create_workflow",
        "description": "Create a new Zapier workflow for LinkedIn automation",
        "inputSchema": {
            "type": "object",
            "properties": {
                "workflow_name": {
                    "type": "string",
                    "description": "Name of the workflow"
                },
                "trigger": {
                    "type": "string",
                    "enum": ["schedule", "webhook", "email", "form"],
                    "description": "Workflow trigger type"
                },
                "actions": {
                    "type": "array",
                    "items": {"type": "string"},
                    "description": "List of actions to perform"
                }
            },
            "required": ["workflow_name", "trigger", "actions"]
        }
    },
    {
        "name": "execute_workflow",
        "description": "Execute an existing Zapier workflow immediately",
        "inputSchema": {
            "type": "object",
            "properties": {
                "workflow_id": {
                    "type": "string",
                    "description": "Zapier workflow ID"
                },
                "input_data": {
                    "type": "object",
                    "description": "Data to pass to the workflow"
                }
            },
            "required": ["workflow_id"]
        }
    },
    {
        "name": "list_workflows",
        "description": "List all Zapier workflows",
        "inputSchema": {
            "type": "object",
            "properties": {}
        }
    },
    {
        "name": "send_email",
        "description": "Send email through Zapier Gmail integration",
        "inputSchema": {
            "type": "object",
            "properties": {
                "to": {
                    "type": "string",
                    "description": "Recipient email address"
                },
                "subject": {
                    "type": "string",
                    "description": "Email subject"
                },
                "body": {
                    "type": "string",
                    "description": "Email body"
                }
            },
            "required": ["to", "subject", "body"]
        }
    },
    {
        "name": "post_to_slack",
        "description": "Post message to Slack channel",
        "inputSchema": {
            "type": "object",
            "properties": {
                "channel": {
                    "type": "string",
                    "description": "Slack channel name"
                },
                "message": {
                    "type": "string",
                    "description": "Message to post"
                }
            },
            "required": ["channel", "message"]
        }
    },
    {
        "name": "post_to_linkedin",
        "description": "Post content with an optional image path or URL to LinkedIn",
        "inputSchema": {
            "type": "object",
            "properties": {
                "content": {
                    "type": "string",
                    "description": "Text content of the post"
                },
                "image_path": {
                    "type": "string",
                    "description": "Local path or remote URL of image to attach"
                }
            },
            "required": ["content"]
        }
    },
    {
        "name": "send_connection_request",
        "description": "Send connection invitation to a LinkedIn profile URL with an optional note",
        "inputSchema": {
            "type": "object",
            "properties": {
                "profile_url": {
                    "type": "string",
                    "description": "Full LinkedIn profile URL"
                },
                "message": {
                    "type": "string",
                    "description": "Optional connection note (under 300 chars)"
                }
            },
            "required": ["profile_url"]
        }
    },
    {
        "name": "send_cold_messages",
        "description": "Search and send automated connection invites with personalized cold notes to recruiters",
        "inputSchema": {
            "type": "object",
            "properties": {
                "job_description": {
                    "type": "string",
                    "description": "Target job description or roles (e.g. Software Engineer)"
                },
                "area": {
                    "type": "string",
                    "description": "Location or area (e.g. San Francisco)"
                },
                "custom_message": {
                    "type": "string",
                    "description": "Optional note template (supports {name}, {role}, {area})"
                },
                "limit": {
                    "type": "integer",
                    "description": "Number of recruiters to outreach (default: 3)"
                }
            },
            "required": ["job_description", "area"]
        }
    },
    {
        "name": "process_inbox",
        "description": "Read recent LinkedIn messages and auto-reply using Claude AI",
        "inputSchema": {
            "type": "object",
            "properties": {}
        }
    },
    {
        "name": "optimize_profile",
        "description": "Generate AI suggestions to optimize LinkedIn profile headline and about sections",
        "inputSchema": {
            "type": "object",
            "properties": {}
        }
    }
]

# ==================== HTTP Streaming Transport ====================

class StreamingHTTPTransport:
    """HTTP Transport with streaming support for MCP"""
    
    def __init__(self, base_url: str, api_key: Optional[str] = None):
        self.base_url = base_url.rstrip("/")
        self.api_key = api_key
        self.client = httpx.AsyncClient(
            timeout=30.0,
            headers={"Content-Type": "application/json"}
        )
    
    async def send_message(self, message: MCPMessage) -> AsyncIterator[str]:
        """Send MCP message via HTTP with streaming response"""
        headers = {"Content-Type": "application/x-ndjson"}
        if self.api_key:
            headers["Authorization"] = f"Bearer {self.api_key}"
        
        try:
            async with self.client.stream(
                "POST",
                f"{self.base_url}/mcp/message",
                content=message.to_json(),
                headers=headers
            ) as response:
                if response.status_code != 200:
                    raise Exception(f"HTTP {response.status_code}: {response.text}")
                
                async for line in response.aiter_lines():
                    if line.strip():
                        yield line
        except Exception as e:
            logger.error(f"Transport error: {e}")
            raise
    
    async def close(self):
        """Close HTTP client"""
        await self.client.aclose()

# ==================== MCP Server ====================

class ZapierMCPServer:
    """MCP Server for Zapier Automation"""
    
    def __init__(self, api_key: str):
        self.api_key = api_key
        self.workflows = {}
        self.execution_log = []
    
    async def list_tools(self) -> MCPMessage:
        """Return list of available tools"""
        return MCPMessage(
            jsonrpc="2.0",
            result={"tools": ZAPIER_TOOLS}
        )
    
    async def call_tool(self, name: str, arguments: dict) -> MCPMessage:
        """Execute a tool and return result"""
        try:
            if name == "create_workflow":
                result = await self._create_workflow(arguments)
            elif name == "execute_workflow":
                result = await self._execute_workflow(arguments)
            elif name == "list_workflows":
                result = await self._list_workflows()
            elif name == "send_email":
                result = await self._send_email(arguments)
            elif name == "post_to_slack":
                result = await self._post_to_slack(arguments)
            elif name == "post_to_linkedin":
                result = await self._post_to_linkedin(arguments)
            elif name == "send_connection_request":
                result = await self._send_connection_request(arguments)
            elif name == "send_cold_messages":
                result = await self._send_cold_messages(arguments)
            elif name == "process_inbox":
                result = await self._process_inbox(arguments)
            elif name == "optimize_profile":
                result = await self._optimize_profile(arguments)
            else:
                return MCPMessage(
                    jsonrpc="2.0",
                    error={"code": -32601, "message": f"Tool '{name}' not found"}
                )
            
            # Log execution
            self.execution_log.append({
                "tool": name,
                "arguments": arguments,
                "result": result,
                "timestamp": asyncio.get_event_loop().time()
            })
            
            return MCPMessage(jsonrpc="2.0", result=result)
        except Exception as e:
            logger.error(f"Tool execution error: {e}")
            return MCPMessage(
                jsonrpc="2.0",
                error={"code": -32603, "message": str(e)}
            )
    
    async def _create_workflow(self, args: dict) -> dict:
        """Create a Zapier workflow"""
        workflow_id = f"wf_{len(self.workflows) + 1}"
        self.workflows[workflow_id] = {
            "name": args.get("workflow_name"),
            "trigger": args.get("trigger"),
            "actions": args.get("actions", []),
            "created_at": asyncio.get_event_loop().time(),
            "enabled": True
        }
        logger.info(f"Created workflow: {workflow_id}")
        return {"workflow_id": workflow_id, "status": "created"}
    
    async def _execute_workflow(self, args: dict) -> dict:
        """Execute a Zapier workflow"""
        workflow_id = args.get("workflow_id")
        if workflow_id not in self.workflows:
            raise ValueError(f"Workflow {workflow_id} not found")
        
        workflow = self.workflows[workflow_id]
        results = []
        
        for action in workflow.get("actions", []):
            logger.info(f"Executing action: {action}")
            results.append({"action": action, "status": "executed"})
        
        return {
            "workflow_id": workflow_id,
            "status": "executed",
            "actions_executed": len(results),
            "results": results
        }
    
    async def _list_workflows(self) -> dict:
        """List all workflows"""
        return {
            "total": len(self.workflows),
            "workflows": [
                {
                    "id": wf_id,
                    "name": wf.get("name"),
                    "trigger": wf.get("trigger"),
                    "enabled": wf.get("enabled")
                }
                for wf_id, wf in self.workflows.items()
            ]
        }
    
    async def _send_email(self, args: dict) -> dict:
        """Send email via Zapier"""
        logger.info(f"Sending email to {args.get('to')}")
        return {
            "status": "sent",
            "to": args.get("to"),
            "subject": args.get("subject")
        }
    
    async def _post_to_slack(self, args: dict) -> dict:
        """Post to Slack via Zapier"""
        logger.info(f"Posting to Slack channel: {args.get('channel')}")
        return {
            "status": "posted",
            "channel": args.get("channel")
        }

    async def _post_to_linkedin(self, args: dict) -> dict:
        """Post to LinkedIn"""
        import agent_post
        content = args.get("content")
        image_path = args.get("image_path")
        return await agent_post.post_to_linkedin(content, image_path)

    async def _send_connection_request(self, args: dict) -> dict:
        """Send LinkedIn connection request"""
        import agent_connect
        profile_url = args.get("profile_url")
        message = args.get("message")
        return await agent_connect.send_connection(profile_url, message)

    async def _send_cold_messages(self, args: dict) -> dict:
        """Send personalized cold connection requests to recruiters"""
        import agent_cold_message
        job_description = args.get("job_description")
        area = args.get("area")
        custom_message = args.get("custom_message")
        limit = int(args.get("limit", 3))
        return await agent_cold_message.send_cold_messages(job_description, area, custom_message, limit)

    async def _process_inbox(self, args: dict) -> dict:
        """Process LinkedIn inbox messages and auto-reply"""
        import ai_agent_inbox
        return await ai_agent_inbox.process_inbox()

    async def _optimize_profile(self, args: dict) -> dict:
        """Optimize LinkedIn profile details"""
        import agent_profile_tweak
        return await agent_profile_tweak.optimize_profile()

# ==================== MCP Client ====================

class ZapierMCPClient:
    """MCP Client for communicating with Zapier server"""
    
    def __init__(self, transport: StreamingHTTPTransport):
        self.transport = transport
        self.request_id = 0
    
    def _next_id(self) -> int:
        """Get next request ID"""
        self.request_id += 1
        return self.request_id
    
    async def list_tools(self) -> list:
        """Get list of available tools"""
        message = MCPMessage(
            jsonrpc="2.0",
            id=self._next_id(),
            method="tools/list"
        )
        
        async for line in self.transport.send_message(message):
            try:
                data = json.loads(line)
                if "result" in data:
                    return data["result"].get("tools", [])
            except json.JSONDecodeError:
                continue
        return []
    
    async def call_tool(self, name: str, arguments: dict) -> Any:
        """Call a tool on the server"""
        message = MCPMessage(
            jsonrpc="2.0",
            id=self._next_id(),
            method="tools/call",
            params={"name": name, "arguments": arguments}
        )
        
        async for line in self.transport.send_message(message):
            try:
                data = json.loads(line)
                if "result" in data:
                    return data["result"]
                elif "error" in data:
                    raise Exception(data["error"]["message"])
            except json.JSONDecodeError:
                continue
        
        raise Exception("No response from server")

# ==================== Example Usage ====================

async def example_usage():
    """Example of using MCP client/server"""
    # Initialize server
    server = ZapierMCPServer(api_key="test_key")
    
    # Initialize client
    transport = StreamingHTTPTransport("http://localhost:8000")
    client = ZapierMCPClient(transport)
    
    try:
        # List available tools
        tools = await client.list_tools()
        print(f"Available tools: {[t['name'] for t in tools]}")
        
        # Create a workflow
        result = await client.call_tool("create_workflow", {
            "workflow_name": "LinkedIn Auto-Post",
            "trigger": "schedule",
            "actions": ["post_to_linkedin", "send_notification"]
        })
        print(f"Workflow created: {result}")
        
    finally:
        await transport.close()

if __name__ == "__main__":
    asyncio.run(example_usage())
