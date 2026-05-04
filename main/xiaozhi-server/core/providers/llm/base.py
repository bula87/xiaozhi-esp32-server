from abc import ABC, abstractmethod
from config.logger import setup_logging

TAG = __name__
logger = setup_logging()


class LLMProviderBase(ABC):
    @abstractmethod
    def response(self, session_id, dialogue):
        """LLM response generator"""
        pass

    def response_no_stream(self, system_prompt, user_prompt, **kwargs):
        # Construct dialogue format
        dialogue = [
            {"role": "system", "content": system_prompt},
            {"role": "user", "content": user_prompt},
        ]
        result = ""
        for part in self.response("", dialogue, **kwargs):
            result += part
        return result

    def response_with_functions(self, session_id, dialogue, functions=None):
        """
        Default implementation for function calling (streaming)
        This should be overridden by providers that support function calls

        Returns: generator that yields either text tokens or a special function call token
        """
        # For providers that don't support functions, just return regular response
        for token in self.response(session_id, dialogue):
            yield token, None

    def supports_audio_input(self):
        """Check if this LLM provider supports direct audio input"""
        return False

    def response_with_audio(self, session_id, audio_data, **kwargs):
        """
        Default implementation for LLMs that accept audio input directly
        This should be overridden by providers that support direct audio input
        """
        # For providers that don't support direct audio input, this is a placeholder
        # that will be overridden by specific providers
        raise NotImplementedError("This LLM provider does not support direct audio input")
