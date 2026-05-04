import requests
from config.logger import setup_logging
from plugins_func.register import register_function, ToolType, ActionResponse, Action
from typing import TYPE_CHECKING

if TYPE_CHECKING:
    from core.connection import ConnectionHandler

TAG = __name__
logger = setup_logging()

# Define the base function description template
SEARCH_FROM_RAGFLOW_FUNCTION_DESC = {
    "type": "function",
    "function": {
        "name": "search_from_ragflow",
        "description": "Query information from the knowledge base",
        "parameters": {
            "type": "object",
            "properties": {
                "question": {"type": "string", "description": "Question to query"}
            },
            "required": ["question"],
        },
    },
}


@register_function(
    "search_from_ragflow", SEARCH_FROM_RAGFLOW_FUNCTION_DESC, ToolType.SYSTEM_CTL
)
def search_from_ragflow(conn: "ConnectionHandler", question=None):
    # Ensure string parameter is properly encoded
    if question and isinstance(question, str):
        # Ensure the question parameter is a UTF-8 encoded string
        pass
    else:
        question = str(question) if question is not None else ""

    ragflow_config = conn.config.get("plugins", {}).get("search_from_ragflow", {})
    base_url = ragflow_config.get("base_url", "")
    api_key = ragflow_config.get("api_key", "")
    dataset_ids = ragflow_config.get("dataset_ids", [])

    url = base_url + "/api/v1/retrieval"
    headers = {"Authorization": f"Bearer {api_key}", "Content-Type": "application/json"}

    # Ensure all strings in payload are UTF-8 encoded
    payload = {"question": question, "dataset_ids": dataset_ids}

    try:
        # Use ensure_ascii=False to properly handle non-ASCII characters during JSON serialization
        response = requests.post(
            url,
            json=payload,
            headers=headers,
            timeout=5,
            verify=False,
        )

        # Explicitly set response encoding to utf-8
        response.encoding = "utf-8"

        response.raise_for_status()

        # Get text content first, then manually decode JSON
        response_text = response.text
        import json

        result = json.loads(response_text)

        if result.get("code") != 0:
            error_detail = result.get("error", {}).get("detail", "Unknown error")
            error_message = result.get("error", {}).get("message", "")
            error_code = result.get("code", "")

            # Safely log error information
            logger.bind(tag=TAG).error(
                f"RAGFlow API call failed, response code: {error_code}, error detail: {error_detail}, full response: {result}"
            )

            # Build detailed error response
            error_response = f"RAG API returned exception (error code: {error_code})"

            if error_message:
                error_response += f": {error_message}"
            if error_detail:
                error_response += f"\nDetail: {error_detail}"

            return ActionResponse(Action.RESPONSE, None, error_response)

        chunks = result.get("data", {}).get("chunks", [])
        contents = []
        for chunk in chunks:
            content = chunk.get("content", "")
            if content:
                # Safely handle content string
                if isinstance(content, str):
                    contents.append(content)
                elif isinstance(content, bytes):
                    contents.append(content.decode("utf-8", errors="replace"))
                else:
                    contents.append(str(content))

        if contents:
            # Organize knowledge base content as reference format
            context_text = f"# Knowledge base results for question [{question}]\n"
            context_text += "```\n\n\n".join(contents[:5])
            context_text += "\n```"
        else:
            context_text = f"# Knowledge base results for question [{question}]\nNo relevant information found."

        return ActionResponse(Action.REQLLM, context_text, None)

    except requests.exceptions.RequestException as e:
        # Network request exception
        context_text = "No relevant information found in the knowledge base."
        logger.bind(tag=TAG).error(
            f"RAGflow network request failed, exception type: {type(e).__name__}, detail: {str(e)}"
        )

        # Provide more detailed error information and solutions based on exception type
        if isinstance(e, requests.exceptions.ConnectTimeout):
            error_response = "Possible reason: RAGflow service not started or network connection issue"
            error_response += (
                "\nSolution: Please check RAGflow service status and network connection"
            )

        elif isinstance(e, requests.exceptions.ConnectionError):
            error_response = "Unable to connect to RAG API"
            error_response += "\nPossible reason: RAGflow service address error or service not running"
            error_response += "\nSolution: Please check RAGflow service address configuration and service status"

        elif isinstance(e, requests.exceptions.Timeout):
            error_response = "RAG API request timed out"
            error_response += (
                "\nPossible reason: RAGflow service response is slow or network latency"
            )
            error_response += "\nSolution: Please try again later or check RAGflow service performance"

        elif isinstance(e, requests.exceptions.HTTPError):
            # Handle HTTP error status code
            if hasattr(e.response, "status_code"):
                status_code = e.response.status_code
                error_response = f"RAG API HTTP error (status code: {status_code})"

                # Try to get error message from response content
                try:
                    error_detail = e.response.json().get("error", {}).get("message", "")
                    if error_detail:
                        error_response += f"\nError detail: {error_detail}"
                except json.JSONDecodeError:
                    # Handle cases where response is not valid JSON
                    pass
            else:
                error_response = f"RAG API HTTP exception: {str(e)}"

        else:
            error_response = f"RAG API network exception ({type(e).__name__}): {str(e)}"

        return ActionResponse(Action.RESPONSE, None, error_response)

    except Exception as e:
        # Other exceptions
        error_type = type(e).__name__
        logger.bind(tag=TAG).error(
            f"RAGflow processing exception, exception type: {error_type}, detail: {str(e)}"
        )

        # Provide detailed error information
        error_response = f"RAG API processing exception ({error_type}): {str(e)}"
        return ActionResponse(Action.RESPONSE, None, error_response)
