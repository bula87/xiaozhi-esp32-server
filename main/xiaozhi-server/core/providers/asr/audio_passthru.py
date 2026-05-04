from core.providers.asr.base import ASRProviderBase
from core.providers.asr.dto.dto import InterfaceType
import asyncio
import logging

logger = logging.getLogger(__name__)
TAG = __name__

class ASRProvider(ASRProviderBase):
    def __init__(self, config: dict, delete_audio_file: bool = True):
        self.interface_type = InterfaceType.AUDIO_DIRECT
        self.config = config
        self.delete_audio_file = delete_audio_file
        logger.info("AudioPassthruASRProvider initialized")

    async def recognize(self, audio_file_path: str) -> str:
        """Direct audio passthrough - returns empty string to indicate audio should be passed through"""
        logger.info(f"[{TAG}] Audio passthrough mode - audio will be sent directly to LLM")
        return ""

    def get_interface_type(self) -> InterfaceType:
        """Return the interface type for this ASR provider"""
        return self.interface_type