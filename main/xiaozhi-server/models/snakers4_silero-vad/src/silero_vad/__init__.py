from importlib.metadata import version

__version__ = version(__name__)

from silero_vad.model import load_silero_vad
from silero_vad.utils_vad import (
    get_speech_timestamps,
    save_audio,
    read_audio,
    VADIterator,
    collect_chunks,
)
