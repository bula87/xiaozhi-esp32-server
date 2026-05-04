"""Tool system type definition"""

from enum import Enum

from dataclasses import dataclass
from typing import Any, Dict, Optional


class ToolType(Enum):
    """Tool type enumeration"""

    SERVER_PLUGIN = "server_plugin"  # server side plugin
    SERVER_MCP = "server_mcp"  # server mcp
    DEVICE_IOT = "device_iot"  # device iot
    DEVICE_MCP = "device_mcp"  # device mcp
    MCP_ENDPOINT = "mcp_endpoint"  # mcp endpoint


@dataclass
class ToolDefinition:
    """Tool definition"""

    name: str  # Tool name
    description: Dict[str, Any]  # Tool description (OpenAI function call format)
    tool_type: ToolType  # Tool type
    parameters: Optional[Dict[str, Any]] = None  # Extra parameters
