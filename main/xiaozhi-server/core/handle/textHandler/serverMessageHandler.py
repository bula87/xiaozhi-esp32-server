import json
from typing import Dict, Any

from core.handle.textMessageHandler import TextMessageHandler
from core.handle.textMessageType import TextMessageType

TAG = __name__


class ServerTextMessageHandler(TextMessageHandler):
    """MCP message handler"""

    @property
    def message_type(self) -> TextMessageType:
        return TextMessageType.SERVER

    async def handle(self, conn, msg_json: Dict[str, Any]) -> None:
        # If configuration is read from API, then secret needs to be verified
        if not conn.read_config_from_api:
            return
        # Get the secret of the POST request
        post_secret = msg_json.get("content", {}).get("secret", "")
        secret = conn.config["manager-api"].get("secret", "")
        # If secret does not match, return
        if post_secret != secret:
            await conn.websocket.send(
                json.dumps(
                    {
                        "type": "server",
                        "status": "error",
                        "message": "Server key verification failed",
                    }
                )
            )
            return

        # Dynamically update configuration
        if msg_json["action"] == "update_config":
            try:
                # Update the configuration of the WebSocketServer
                if not conn.server:
                    await conn.websocket.send(
                        json.dumps(
                            {
                                "type": "server",
                                "status": "error",
                                "message": "cannot get server instance",
                                "content": {"action": "update_config"},
                            }
                        )
                    )
                    return

                if not await conn.server.update_config():
                    await conn.websocket.send(
                        json.dumps(
                            {
                                "type": "server",
                                "status": "error",
                                "message": "Update server configuration failed",
                                "content": {"action": "update_config"},
                            }
                        )
                    )
                    return

                # Send success response
                await conn.websocket.send(
                    json.dumps(
                        {
                            "type": "server",
                            "status": "success",
                            "message": "Configuration update successful",
                            "content": {"action": "update_config"},
                        }
                    )
                )
            except Exception as e:
                conn.logger.bind(tag=TAG).error(
                    f"Update configuration failed: {str(e)}"
                )
                await conn.websocket.send(
                    json.dumps(
                        {
                            "type": "server",
                            "status": "error",
                            "message": f"Update configuration failed: {str(e)}",
                            "content": {"action": "update_config"},
                        }
                    )
                )
        # Restart server
        elif msg_json["action"] == "restart":
            await conn.handle_restart(msg_json)
