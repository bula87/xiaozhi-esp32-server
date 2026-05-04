from abc import ABC, abstractmethod


class VADProviderBase(ABC):
    @abstractmethod
    def is_vad(self, conn, data) -> bool:
        """Detect speech activity in audio data"""
        pass
