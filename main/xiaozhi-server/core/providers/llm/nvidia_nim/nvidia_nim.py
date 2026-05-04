import base64
import httpx
import json
from core.providers.llm.base import LLMProviderBase
from config.logger import setup_logging

logger = setup_logging()
TAG = __name__

class LLMProvider(LLMProviderBase):
    def __init__(self, config):
        self.model_name = config.get("model_name")
        self.api_key = config.get("api_key")
        self.base_url = config.get("base_url", "http://192.168.1.100:8000/v1/audio/transcriptions")
        self.timeout = config.get("timeout", 30)
        
        # Set up the HTTP client
        self.client = httpx.Client(timeout=self.timeout)
        
        logger.bind(tag=TAG).info(f"NVIDIA NIM provider initialized with model: {self.model_name}")

    def response(self, session_id, dialogue, **kwargs):
        """Handle text-based responses for the NVIDIA NIM endpoint"""
        # For text-based responses, we'll use the standard chat completions endpoint
        # This is a placeholder implementation - would need to be extended based on actual NVIDIA NIM API
        raise NotImplementedError("NVIDIA NIM provider does not support text-based responses")

    def supports_audio_input(self):
        """Check if this LLM provider supports direct audio input"""
        return True

    def response_with_audio(self, session_id, audio_file_path, **kwargs):
        """
        Implementation for LLMs that accept audio input directly
        This uses the NVIDIA NIM endpoint with audio support
        """
        try:
            # Read the audio file
            with open(audio_file_path, "rb") as audio_file:
                audio_data = audio_file.read()
            
            # Encode the audio data in base64 and remove newlines to prevent JSON errors
            audio_base64 = base64.b64encode(audio_data).decode('utf-8').replace('\n', '')
            
            # Build the JSON payload with the base64-encoded audio data
            payload = {
                "model": self.model_name,
                "input": {
                    "type": "audio_url",
                    "audio_url": {
                        "url": f"data:audio/wav;base64,{audio_base64}"
                    }
                }
            }
            
            # Send the request to the NVIDIA NIM endpoint
            headers = {
                "Authorization": f"Bearer {self.api_key}",
                "Content-Type": "application/json"
            }
            
            response = self.client.post(
                self.base_url,
                headers=headers,
                json=payload
            )
            
            # Parse the response
            if response.status_code == 200:
                result = response.json()
                # Return the transcribed text
                return result.get("text", "")
            else:
                raise Exception(f"Error from NVIDIA NIM endpoint: {response.status_code} - {response.text}")
                
        except Exception as e:
            logger.bind(tag=TAG).error(f"Error processing audio with NVIDIA NIM: {e}")
            raise NotImplementedError(f"This LLM provider does not support direct audio input: {str(e)}")
