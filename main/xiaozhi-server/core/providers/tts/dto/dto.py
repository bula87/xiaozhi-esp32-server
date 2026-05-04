from enum import Enum
from typing import Optional


class SentenceType(Enum):
    # Speaking phase
    FIRST = "FIRST"  # First sentence
    MIDDLE = "MIDDLE"  # In progress
    LAST = "LAST"  # Last sentence


class ContentType(Enum):
    # Content type
    TEXT = "TEXT"  # Text content
    FILE = "FILE"  # File content
    ACTION = "ACTION"  # Action content


class InterfaceType(Enum):
    # Interface type
    DUAL_STREAM = "DUAL_STREAM"  # Dual stream
    SINGLE_STREAM = "SINGLE_STREAM"  # Single stream


NON_STREAM = "NON_STREAM"  # Non-stream


class TTSMessageDTO:
    def __init__(
        self,
        sentence_id: str,
        # Speaking phase
        sentence_type: SentenceType,
        # content type
        content_type: ContentType,
        # content detail, generally the text or lyrics of the audio that needs conversion
        content_detail: Optional[str] = None,
        # If the content type is a file, the file path needs to be passed
        content_file: Optional[str] = None,
    ):
        self.sentence_id = sentence_id
        self.sentence_type = sentence_type
        self.content_type = content_type
        self.content_detail = content_detail
        self.content_file = content_file
