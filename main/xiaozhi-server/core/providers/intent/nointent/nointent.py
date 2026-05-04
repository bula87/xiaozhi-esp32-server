from ..base import IntentProviderBase
from typing import List, Dict
from config.logger import setup_logging

TAG = __name__
logger = setup_logging()


class IntentProvider(IntentProviderBase):
    async def detect_intent(self, conn, dialogue_history: List[Dict], text: str) -> str:
        """
        Default intent recognition implementation, always returning continue chat
        Args:
            dialogue_history: dialogue history record list
            text: current dialogue record
        Returns:
            Fixedly returns "continue chat"
        """
        logger.bind(tag=TAG).debug(
            "Using NoIntentProvider, always returning continue chat"
        )
        return '{"function_call": {"name": "continue_chat"}}'
