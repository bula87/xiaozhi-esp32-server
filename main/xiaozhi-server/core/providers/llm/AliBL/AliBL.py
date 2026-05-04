from config.logger import setup_logging
from http import HTTPStatus
import dashscope
from dashscope import Application
from core.providers.llm.base import LLMProviderBase
from core.utils.util import check_model_key

TAG = __name__
logger = setup_logging()


class LLMProvider(LLMProviderBase):
    def __init__(self, config):
        self.api_key = config["api_key"]
        self.app_id = config["app_id"]
        self.base_url = config.get("base_url")
        self.is_No_prompt = config.get("is_no_prompt")
        self.memory_id = config.get("ali_memory_id")
        self.streaming_chunk_size = config.get(
            "streaming_chunk_size", 3
        )  # Number of characters returned per stream
        check_model_key("AliBLLM", self.api_key)

    def response(self, session_id, dialogue):
        # Process dialogue
        if self.is_No_prompt:
            dialogue.pop(0)
            logger.bind(tag=TAG).debug(
                f"【AliBailian API Service】processed dialogue: {dialogue}"
            )
        # Construct call parameters
        call_params = {
            "api_key": self.api_key,
            "app_id": self.app_id,
            "session_id": session_id,
            "messages": dialogue,
            # Enable SDK native streaming
            "stream": True,
        }
        if self.memory_id:
            # BaiLian memory needs prompt parameter
            prompt = dialogue[-1].get("content")
            call_params["memory_id"] = self.memory_id
            call_params["prompt"] = prompt
            logger.bind(tag=TAG).debug(
                f"【AliBailian API Service】processed prompt: {prompt}"
            )
        # 可选地设置自定义API基地址（若配置为兼容模式URL则忽略）
        if self.base_url and ("/api/" in self.base_url):
            dashscope.base_http_api_url = self.base_url

        responses = Application.call(**call_params)

        # Streaming processing (SDK returns an iterable object when stream=True; otherwise returns a single response object)
        logger.bind(tag=TAG).debug(
            f"[Aliyun Bailian API Service] construction parameters: {dict(call_params, api_key='***')}"
        )

        last_text = ""
        try:
            for resp in responses:
                if resp.status_code != HTTPStatus.OK:
                    logger.bind(tag=TAG).error(
                        f"code={resp.status_code}, message={resp.message}, Please refer to the documentation:https://help.aliyun.com/zh/model-studio/developer-reference/error-code"
                    )
                    continue
                current_text = getattr(getattr(resp, "output", None), "text", None)
                if current_text is None:
                    continue
                # SDK streaming is incremental overwrite, calculate delta output
                if len(current_text) >= len(last_text):
                    delta = current_text[len(last_text) :]
                else:
                    # Avoid occasional rollback
                    delta = current_text
                if delta:
                    yield delta
                last_text = current_text
        except TypeError:
            # Non-streaming fallback (single return)
            if responses.status_code != HTTPStatus.OK:
                logger.bind(tag=TAG).error(
                    f"code={responses.status_code}, message={responses.message}, Please refer to the documentation:https://help.aliyun.com/zh/model-studio/developer-reference/error-code"
                )
                yield "【AliBaiLian API service response exception】"
            else:
                full_text = getattr(getattr(responses, "output", None), "text", "")
                logger.bind(tag=TAG).info(
                    f"【AliBaiLian API service】full response length: {len(full_text)}"
                )
                for i in range(0, len(full_text), self.streaming_chunk_size):
                    chunk = full_text[i : i + self.streaming_chunk_size]
                    if chunk:
                        yield chunk

    def response_with_functions(self, session_id, dialogue, functions=None):
        # AliBaiLian currently does not support native function calls. To maintain compatibility, we fall back to plain text streaming output here.
        # The upper layer will consume it in the form of (content, tool_calls); here we always return (token, None)
        logger.bind(tag=TAG).warning(
            "AliBaiLian does not implement native function calls; it has fallen back to plain text streaming output"
        )
        for token in self.response(session_id, dialogue):
            yield token, None
